package io.rsl.pragma.screens.wallets;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.rsl.pragma.R;
import io.rsl.pragma.db.dbmodels.DBCredential;
import io.rsl.pragma.screens.wallets.create.CredentialImportViewModel;
import io.rsl.pragma.screens.wallets.create.CredentialsImportActivity;
import timber.log.Timber;

public class CredentialsFragment extends Fragment {

    @BindView(R.id.credentialsRecycler) RecyclerView         credentialsRecycler;
    @BindView(R.id.fab)                 FloatingActionButton fab;
    @BindView(R.id.layoutNoItem)        View                 noItemView;
    @BindView(R.id.noItemTitle)         TextView             noItemText;

    private     Unbinder                   unbinder;
    private     CredentialsRecyclerAdapter credentialsRecyclerAdapter;
    private     CredentialsViewModel       viewModel;
    private     FragmentActivity           activityContext;

    public CredentialsFragment() {}


    public static CredentialsFragment newInstance() {
        return new CredentialsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(CredentialsViewModel.class);
        subscribeUi();
    }

    private void subscribeUi() {
        viewModel.getCredentials().observe(this, credentials -> {
            noItemView.setVisibility(credentials == null || credentials.size() == 0 ? View.VISIBLE : View.INVISIBLE);
            if(credentials != null) credentialsRecyclerAdapter.setCredentialList(credentials);
        });
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_credentials_list, container, false);
        activityContext.setTitle("Ваши кошельки");
        unbinder = ButterKnife.bind(this, fragmentView);

        noItemText.setText("Вы ещё не добавили ни одного кошелька\nНажмите кнопку внизу для добавления");

        credentialsRecyclerAdapter = new CredentialsRecyclerAdapter();

        credentialsRecycler.setLayoutManager(new LinearLayoutManager(activityContext));
        credentialsRecycler.setAdapter(credentialsRecyclerAdapter);
        credentialsRecyclerAdapter.setOnCredentialsSelectListener(new CredentialsRecyclerAdapter.OnCredentialsSelectListener() {
            @Override
            public void onCredentialsSelect(DBCredential dbCredential) {
                startImportActivity(dbCredential);
            }

            @Override
            public void onCredentialsLongClick(DBCredential dbCredential) {
                showDeleteDialog(dbCredential);
            }
        });

        fab.setOnClickListener(v -> startImportActivity(null));

        return fragmentView;
    }

    private void showDeleteDialog(DBCredential cred) {
        AlertDialog alertDialog = new AlertDialog.Builder(activityContext)
                .setTitle("Удаление контракта")
                .setMessage("Вы действительно хотите удалить кошелёк:\n" + cred.getAddress())
                .setPositiveButton("Да", (dialog, which) -> viewModel.delete(cred))
                .setNegativeButton("Нет", (dialog, which) -> dialog.dismiss())
                .create();

        alertDialog.show();
    }

    private void startImportActivity(DBCredential dbCredential) {
        Intent intent = new Intent(activityContext, CredentialsImportActivity.class);
        if(dbCredential != null) intent.putExtra(CredentialsImportActivity.CREDENTIALS_KEY, dbCredential);
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity) {
            activityContext = (FragmentActivity) context;
        } else throw new RuntimeException("Activity context is required!");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unbinder.unbind();
    }
}
