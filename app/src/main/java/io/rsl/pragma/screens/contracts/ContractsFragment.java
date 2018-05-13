package io.rsl.pragma.screens.contracts;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.rsl.pragma.R;
import io.rsl.pragma.activities.DispatchActivity;
import io.rsl.pragma.db.dbmodels.DBContract;
import io.rsl.pragma.screens.contracts.create.ContractSelectActivity;
import timber.log.Timber;

public class ContractsFragment extends Fragment {

    @BindView(R.id.contractsRecycler)   RecyclerView            contractsRecycler;
    @BindView(R.id.fab)                 FloatingActionButton    fab;
    @BindView(R.id.layoutNoItem)        View                    noItemView;
    @BindView(R.id.noItemTitle)         TextView                noItemText;
    @BindView(R.id.swipe_container)     SwipeRefreshLayout      swipeRefreshLayout;

    private Unbinder                    unbinder;
    private ContractsRecyclerAdapter    contractsRecyclerAdapter;

    private ContractsViewModel          viewModel;
    private FragmentActivity            activityContext;

    public ContractsFragment() {
    }

    public static ContractsFragment newInstance() {
        return new ContractsFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ContractsViewModel.class);

        subscribeUI();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity) {
            activityContext = (FragmentActivity) context;
        } else throw new RuntimeException("Activity context is required!");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_contracts_list, container, false);
        activityContext.setTitle("Ваши контракты");
        unbinder = ButterKnife.bind(this, fragmentView);

        noItemText.setText("Вы ещё не создали ни одного контракта\nНажмите кнопку внизу для создания");

        contractsRecyclerAdapter = new ContractsRecyclerAdapter();

        contractsRecyclerAdapter.setOnContractClickListener(new ContractsRecyclerAdapter.OnContractClickListener() {
            @Override
            public void onContractClick(DBContract contract) {
                Intent intent = new Intent(activityContext, DispatchActivity.class);
                intent.putExtra(DispatchActivity.CONTRACT_TO_OPEN, contract);
                startActivity(intent);
            }

            @Override
            public void onContractLongClick(DBContract contract) {
                showDeleteDialog(contract);
            }
        });

        contractsRecycler.setLayoutManager(new LinearLayoutManager(activityContext));
        contractsRecycler.setAdapter(contractsRecyclerAdapter);

        fab.setOnClickListener(v -> startActivity(new Intent(activityContext, ContractSelectActivity.class)));

        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            viewModel.update();
        });

        return fragmentView;
    }

    private void showDeleteDialog(DBContract cont) {
        AlertDialog alertDialog = new AlertDialog.Builder(activityContext)
                .setTitle("Удаление контракта")
                .setMessage("Вы действительно хотите удалить контракт:\n" + cont.getTitle())
                .setPositiveButton("Да", (dialog, which) -> viewModel.delete(cont))
                .setNegativeButton("Нет", (dialog, which) -> dialog.dismiss())
                .create();

        alertDialog.show();
    }

    private void subscribeUI() {
        viewModel.getContracts().observe(this, contracts -> {
            noItemView.setVisibility(contracts == null || contracts.size() == 0 ? View.VISIBLE : View.GONE);

            if(contracts != null) {
                swipeRefreshLayout.setRefreshing(false);
                contractsRecyclerAdapter.setContractsList(contracts);
                viewModel.checkContracts();
            } else {
                Snackbar.make(Objects.requireNonNull(getView()), "Ошибка соединения", Snackbar.LENGTH_LONG)
                        .setAction("Обновить", v -> viewModel.update())
                        .show();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unbinder.unbind();
    }
}