package io.rsl.pragma.screens.poll;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.rsl.pragma.App;
import io.rsl.pragma.R;
import io.rsl.pragma.db.dao.ContractsDao;
import io.rsl.pragma.db.dbmodels.DBContract;
import io.rsl.pragma.repositories.ContractsRepository;
import io.rsl.pragma.screens.poll.eth.Ballot;
import io.rsl.pragma.screens.poll.models.DatetimeModel;
import io.rsl.pragma.screens.poll.models.EditTextModel;
import io.rsl.pragma.screens.poll.models.TitleModel;
import io.rsl.pragma.screens.poll.models.TypeSelectorModel;
import io.rsl.pragma.screens.poll.models.core.CorePollModel;
import io.rsl.pragma.screens.txdispatch.DispatchFragment;
import io.rsl.pragma.screens.txdispatch.RawContract;
import io.rsl.pragma.utils.BaseModel;
import io.rsl.pragma.utils.ContractConstant;
import io.rsl.pragma.utils.web3.ParcelableRawTransaction;

public class PollCreateFragment extends Fragment {

    private static final String DATA = "DATA";

    @BindView(R.id.pollCreationRecycler)    RecyclerView    recyclerView;
    @BindView(R.id.save)                    View            saveView;
    @BindView(R.id.saveDeploy)              View            saveDeployView;


    private PollCreationRecyclerAdapter recyclerAdapter;
    private Unbinder                    unbinder;
    private OnCreationFinishListener    listener;
    private FragmentActivity            activityContext;

    private String data;

    @Inject
    Gson gson;

    @Inject
    ContractsRepository contractsRepository;

    public PollCreateFragment() {}

    public static PollCreateFragment newInstance(String data) {
        PollCreateFragment pollCreateFragment = new PollCreateFragment();
        Bundle args = new Bundle();
        args.putString(DATA, data);
        pollCreateFragment.setArguments(args);
        return pollCreateFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            data = getArguments().getString(DATA, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_poll_create, container, false);
        unbinder = ButterKnife.bind(this, fragmentView);

        List<BaseModel> baseModels = new ArrayList<>();

        if(data != null) {
            CorePollModel corePollModel = gson.fromJson(data, CorePollModel.class);
            baseModels.add(new TitleModel(corePollModel.getTitle()));
            for(String option : corePollModel.getVoteOptions()) baseModels.add(new EditTextModel(option));
            baseModels.add(new EditTextModel());
            baseModels.add(new TypeSelectorModel(corePollModel.isMajority(), corePollModel.getVoteCount()));
            baseModels.add(new DatetimeModel(corePollModel.getStartDate(), corePollModel.getEndDate()));
        } else {
            baseModels.add(new TitleModel());
            baseModels.add(new EditTextModel());
            baseModels.add(new EditTextModel());
            baseModels.add(new TypeSelectorModel());
            baseModels.add(new DatetimeModel());
        }

        recyclerAdapter = new PollCreationRecyclerAdapter(getActivity(), baseModels);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter);

        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCreationFinishListener) {
            listener = (OnCreationFinishListener) context;
            activityContext = (FragmentActivity) context;
            App.get(activityContext).getComponent().inject(this);
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener and be an Activity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        unbinder.unbind();
    }

    @OnClick(R.id.save)
    public void save(View view) {
        complete(recyclerAdapter.getPoll(), false);
    }

    @OnClick(R.id.saveDeploy)
    public void saveDeploy(View view) {
        CorePollModel poll = recyclerAdapter.getPoll();

        if(!checkPoll(poll)) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle("Contract deploy")
                    .setMessage("Check data one more time, because you can no longer edit it after deploy!")
                    .setPositiveButton("Deploy", (dialog, which) -> complete(poll, true))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create();

            alertDialog.show();
        }
    }

    private void complete(CorePollModel poll, boolean deployImmediately) {

        saveView.setClickable(false);
        saveDeployView.setClickable(false);

        AsyncTask.execute(() -> {
            List<byte[]> prop = new ArrayList<>();
            prop.add("qwertyuiopasdfghjklzxcvbnm123456".getBytes());
            prop.add("qwertyuiopasdfghjklzxcvbnm123456".getBytes());
            prop.add("qwertyuiopasdfghjklzxcvbnm123456".getBytes());
            prop.add("qwertyuiopasdfghjklzxcvbnm123456".getBytes());
            prop.add("qwertyuiopasdfghjklzxcvbnm123456".getBytes());

            ParcelableRawTransaction rawTransaction = Ballot.getRawDeploy(prop);
            DBContract dbContract = new DBContract(ContractConstant.POLL_TYPE, ContractConstant.SAVED, poll.getTitle(), gson.toJson(poll, CorePollModel.class));

            String id = contractsRepository.addContract(dbContract);
            if(id != null) {
                activityContext.runOnUiThread(() -> listener.onCreationFinish(id, poll.getTitle(), rawTransaction, deployImmediately));
            } else {
                saveView.setClickable(false);
                saveDeployView.setClickable(false);
                Snackbar.make(Objects.requireNonNull(getView()), "Ощибка соединения", Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private boolean checkPoll(CorePollModel poll) {
        boolean error = false;
        StringBuilder stringBuilder = new StringBuilder("You can not deploy this contract:\n\n");
        if(poll.getTitle().length() == 0) {
            error = true;
            stringBuilder.append("> Title can not be empty!\n\n");
        }
        if(poll.getVoteOptions().size() < 2) {
            error = true;
            stringBuilder.append("> Poll should contains at least 2 not empty vote options!\n");
        }
        if(error) {
            AlertDialog alertDialog = new AlertDialog.Builder(activityContext)
                    .setTitle("Contract deploy")
                    .setMessage(stringBuilder.toString())
                    .setNegativeButton("Back", (dialog, which) -> dialog.dismiss())
                    .create();

            alertDialog.show();
        }
        return error;
    }

    public interface OnCreationFinishListener {
        void onCreationFinish(String remoteID, String title, ParcelableRawTransaction rawTransaction, boolean deployImmediately);
    }
}
