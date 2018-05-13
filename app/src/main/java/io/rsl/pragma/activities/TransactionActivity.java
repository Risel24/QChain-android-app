package io.rsl.pragma.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rsl.pragma.R;
import io.rsl.pragma.db.dbmodels.DBTransaction;

public class TransactionActivity extends AppCompatActivity {

    public static final String TRANSACTION_KEY = "TRANSACTION KEY";

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.hashText) TextView hashText;
    @BindView(R.id.fromText) TextView fromText;
    @BindView(R.id.toText) TextView toText;
    @BindView(R.id.gasUsedText) TextView gasUsedText;
    @BindView(R.id.gasPriceText) TextView gasPriceText;
    @BindView(R.id.blockNumText) TextView blockNumText;
    @BindView(R.id.errorText) TextView errorText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        DBTransaction transaction = getIntent().getParcelableExtra(TRANSACTION_KEY);

        hashText.setText(transaction.getTxHash());
        fromText.setText(transaction.getFrom());
        toText.setText(transaction.getTo());
        gasUsedText.setText(transaction.getGasUsed());
        gasPriceText.setText(transaction.getGasPrice());
        blockNumText.setText(transaction.getBlockNum());
        if(transaction.hasError()) {
            errorText.setVisibility(View.VISIBLE);
            errorText.setText(transaction.getError());
        } else {
            errorText.setVisibility(View.GONE);
        }
    }

}
