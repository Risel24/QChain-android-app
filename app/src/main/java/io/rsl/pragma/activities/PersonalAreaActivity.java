package io.rsl.pragma.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rsl.pragma.R;
import io.rsl.pragma.screens.contracts.ContractsFragment;
import io.rsl.pragma.screens.history.HistoryFragment;
import io.rsl.pragma.screens.wallets.CredentialsFragment;

public class PersonalAreaActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.bottom_navigation) BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_area);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        bottomNav.setOnNavigationItemReselectedListener(item -> {});
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_contracts:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, ContractsFragment.newInstance()).commit();
                    return true;
                case R.id.action_wallets:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, CredentialsFragment.newInstance()).commit();
                    return true;
                case R.id.action_history:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, HistoryFragment.newInstance()).commit();
                    return true;
                default:
                    return false;
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.container, ContractsFragment.newInstance()).commit();
    }

}
