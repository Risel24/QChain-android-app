package io.rsl.pragma.screens.poll;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import io.rsl.pragma.R;
import io.rsl.pragma.screens.poll.eth.EthPollManager;
import io.rsl.pragma.screens.poll.models.core.CorePollModel;

public class PollAdminActivity extends AppCompatActivity {

    private EthPollManager ethPollManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_admin);

        //ethPollManager = new EthPollManager((CorePollModel) getIntent().getSerializableExtra("Core Poll Model"));

        boolean deployImmediately = getIntent().getBooleanExtra("Deploy Immediately", false);

        if(deployImmediately) {
            //ethPollManager.deploy(transactionReceipt -> {

            //});
        }

    }
}
