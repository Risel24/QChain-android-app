package io.rsl.pragma.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

import butterknife.ButterKnife;
import io.rsl.pragma.R;
import io.rsl.pragma.db.dbmodels.DBContract;
import io.rsl.pragma.screens.poll.PollAdminActivity;
import io.rsl.pragma.screens.poll.PollCreateFragment;
import io.rsl.pragma.screens.poll.models.core.CorePollModel;
import io.rsl.pragma.screens.process.ProcessingFragment;
import io.rsl.pragma.screens.txdispatch.DispatchFragment;
import io.rsl.pragma.utils.ContractConstant;
import io.rsl.pragma.utils.web3.ParcelableRawTransaction;

public class DispatchActivity extends AppCompatActivity implements
        PollCreateFragment.OnCreationFinishListener,
        DispatchFragment.OnTransactionHashReceivedListener,
        ProcessingFragment.OnTransactionConfirmListener {

    public static final String CONTRACT_TO_OPEN = "CONTRACT TO OPEN";
    public static final String FRAGMENT_NAME_KEY = "FRAGMENT NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);
        ButterKnife.bind(this);

        if(getIntent().hasExtra(FRAGMENT_NAME_KEY)) {
            String fragmentClassName = getIntent().getStringExtra(FRAGMENT_NAME_KEY);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, Fragment.instantiate(this, fragmentClassName)).commit();
        } else if (getIntent().hasExtra(CONTRACT_TO_OPEN)) {
            DBContract dbContract = getIntent().getParcelableExtra(CONTRACT_TO_OPEN);

            switch (dbContract.getStatus()) {
                case ContractConstant.DEPLOYED:
                    //to admin panel !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    break;
                case ContractConstant.PENDING:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, ProcessingFragment.newInstance(dbContract.getRemoteId(), dbContract.getInitHash())).commit();
                    break;
                case ContractConstant.SAVED:

                    switch (dbContract.getType()) {
                        case ContractConstant.POLL_TYPE:
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, PollCreateFragment.newInstance(dbContract.getData())).commit();
                            break;
                        default:
                            break;
                    }

                default:
                    break;

            }

        }
    }

    private void startAdminActivity(CorePollModel poll, boolean deployImmediately) {
        Intent intent = new Intent(DispatchActivity.this, PollAdminActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        intent.putExtra("Core Poll Model", poll);
        intent.putExtra("Deploy Immediately", deployImmediately);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onCreationFinish(String remoteID, String title, ParcelableRawTransaction rawTransaction, boolean deployImmediately) {
        if(deployImmediately) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, DispatchFragment.newInstance(remoteID, "Deploy vote contract: " + title, rawTransaction)).commit();
        } else finish();
    }

    @Override
    public void onTransactionHashReceived(String remoteID, String hash) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, ProcessingFragment.newInstance(remoteID, hash)).commit();
    }

    @Override
    public void onTransactonConfirm(TransactionReceipt transactionReceipt) {
        finish();
    }
}