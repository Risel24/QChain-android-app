package io.rsl.pragma.screens.history;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
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
import io.rsl.pragma.activities.TransactionActivity;
import io.rsl.pragma.db.dbmodels.DBTransaction;

public class HistoryFragment extends Fragment {

    @BindView(R.id.transactionsRecycler)    RecyclerView recyclerView;
    @BindView(R.id.layoutNoItem)            View         noItemView;
    @BindView(R.id.noItemTitle)             TextView     noItemText;

    private HistoryRecyclerAdapter recyclerAdapter;

    private     FragmentActivity activityContext;
    private     HistoryViewModel viewModel;
    private     Unbinder         unbinder;

    public HistoryFragment() {}

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_history, container, false);
        activityContext.setTitle("Ваши кошельки");
        unbinder = ButterKnife.bind(this, fragmentView);

        noItemText.setText("Здесь появяться ваши транзакции");

        recyclerAdapter = new HistoryRecyclerAdapter();

        recyclerAdapter.setOnTransactionClickListener(transaction -> {
            Intent intent = new Intent(activityContext, TransactionActivity.class);
            intent.putExtra(TransactionActivity.TRANSACTION_KEY, transaction);
            startActivity(intent);
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(activityContext);
        DividerItemDecoration decoration = new DividerItemDecoration(activityContext, layoutManager.getOrientation());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(recyclerAdapter);

        return fragmentView;
    }

    private void subscribeUI() {
        viewModel.getTransactions().observe(this, transactions -> {
            noItemView.setVisibility(transactions == null || transactions.size() == 0 ? View.VISIBLE : View.INVISIBLE);
            if(transactions != null) {
                recyclerAdapter.setTransactionsList(transactions);
                viewModel.checkTransactions();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unbinder.unbind();
    }
}
