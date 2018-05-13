package io.rsl.pragma.screens.contracts.create;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rsl.pragma.R;
import io.rsl.pragma.activities.DispatchActivity;

public class ContractSelectActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)             Toolbar         toolbar;
    @BindView(R.id.descriptionRecycler) RecyclerView    descriptionRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_select);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ContractSelectRecyclerAdapter adapter = new ContractSelectRecyclerAdapter();
        adapter.setOnContractSelectListener(fragmentName -> {
            Intent intent = new Intent(ContractSelectActivity.this, DispatchActivity.class);
            intent.putExtra(DispatchActivity.FRAGMENT_NAME_KEY, fragmentName);
            startActivity(intent);
        });

        descriptionRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        descriptionRecycler.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        descriptionRecycler.addItemDecoration(dividerItemDecoration);
        descriptionRecycler.setAdapter(adapter);
    }
}
