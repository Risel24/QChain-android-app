package io.rsl.pragma.screens.txdispatch;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.rsl.pragma.R;
import io.rsl.pragma.db.dbmodels.DBCredential;
import io.rsl.pragma.screens.wallets.CredentialsViewModel;
import io.rsl.pragma.utils.web3.ParcelableRawTransaction;
import io.rsl.pragma.utils.web3.QFastRawTransactionManager;

public class DispatchFragment extends Fragment {

    private static final String DB_ID = "DB ID";
    private static final String TX_INFO = "TX INFO";
    private static final String RAW_TX = "RAW TX";

    @BindView(R.id.spinner)             Spinner     credentialsSpinner;
    @BindView(R.id.details)             TextView    detailsText;
    @BindView(R.id.recipient)           TextView    recipientText;
    @BindView(R.id.gasAmount)           TextView    gasAmountText;
    @BindView(R.id.gasPrice)            TextView    gasPriceText;
    @BindView(R.id.includedData)        TextView    includedDataText;
    @BindView(R.id.includedDataTitle)   TextView    includedDataTitleText;
    @BindView(R.id.advancedButton)      Button      advancedButton;
    @BindView(R.id.showAllData)         Button      showAllButton;
    @BindView(R.id.approve)             Button      approve;
    @BindView(R.id.reject)              Button      reject;
    @BindView(R.id.constraint)   ConstraintLayout   layout;

    private EditText                            advancedGasPrice;
    private EditText                            advancedGasLimit;
    private EditText                            advancedNonce;
    private Button                              advancedHide;

    private String                              txInfo;

    private Unbinder                            unbinder;
    private OnTransactionHashReceivedListener   listener;
    private CredentialsArrayAdapter             credentialsArrayAdapter;
    private TextWatcher                         advancedPriceWatcher;
    private View                                backup;
    private FragmentActivity                    activityContext;
    private DispatchViewModel                   viewModel;

    public DispatchFragment() { }

    public static DispatchFragment newInstance(String remoteID, String txInfo, ParcelableRawTransaction rawTransaction) {
        DispatchFragment fragment = new DispatchFragment();
        Bundle args = new Bundle();
        args.putString(DB_ID, remoteID);
        args.putString(TX_INFO, txInfo);
        args.putParcelable(RAW_TX, rawTransaction);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(DispatchViewModel.class);
        assert getArguments() != null;
        viewModel.init(getArguments().getString(DB_ID), getArguments().getParcelable(RAW_TX));

        subscribeUI();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            txInfo = getArguments().getString(TX_INFO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_dispatch, container, false);
        unbinder = ButterKnife.bind(this, fragmentView);

        detailsText.setText(txInfo);

        credentialsArrayAdapter = new CredentialsArrayAdapter(activityContext, R.layout.item_credentials, new ArrayList<>());
        credentialsSpinner.setAdapter(credentialsArrayAdapter);

        credentialsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setLastAddress(credentialsArrayAdapter.getAddress(position));
                gasAmountText.setText("Loading...");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        advancedPriceWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().matches("\\d+"))
                    viewModel.updateGasPrice(new BigInteger(s.toString()));
                else viewModel.reloadGasPrice();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        return fragmentView;
    }

    private void subscribeUI() {
        viewModel.getGasAmount().observe(this, this::updateGasAmount);
        viewModel.getRawTranscation().observe(this, this::updateUI);
        viewModel.getCredentials().observe(this, credentialsArrayAdapter::setCredentials);

        viewModel.getHash().observe(this, this::subscribeDispatch);
    }

    private void updateUI(ParcelableRawTransaction rawTransaction) {
        if(rawTransaction.getGasPrice() != null) gasPriceText.setText(String.format("%s Gwei", viewModel.getGweiPrice(rawTransaction.getGasPrice()).stripTrailingZeros().toPlainString()));
        recipientText.setText(rawTransaction.getTo() == null ? "Not required in contract deployment" : rawTransaction.getTo());
        setIncludedDataTitle(rawTransaction);
        setIncludedData(rawTransaction);

        if(advancedGasLimit != null) advancedGasLimit.setText(rawTransaction.getGasLimit().toString());
        if(advancedNonce != null) advancedNonce.setText(rawTransaction.getNonce() == null ? "auto" : rawTransaction.getNonce().toString());
        if(advancedGasPrice != null) {
            int selectorPos = advancedGasPrice.getSelectionEnd();
            advancedGasPrice.removeTextChangedListener(advancedPriceWatcher);
            advancedGasPrice.setText(rawTransaction.getGasPrice().toString());
            advancedGasPrice.addTextChangedListener(advancedPriceWatcher);
            advancedGasPrice.setSelection(selectorPos);
        }
    }

    private void updateGasAmount(GasAmount gasAmountData) {
        if(viewModel.isSenderSelected())
            gasAmountText.setText(gasAmountData == null ? "Error occurred" : gasAmountData.toString());
        else gasAmountText.setText("Sender address is not set");
    }

    @OnClick(R.id.advancedButton)
    public void advanced(View view) {
        backup = view;
        layout.removeView(view);
        View advancedView = getLayoutInflater().inflate(R.layout.advanced_dispatch_layout, layout, false);
        bindAdvanced(advancedView);
        layout.addView(advancedView);

        ConstraintSet constraintSet = new ConstraintSet();

        constraintSet.clone(layout);

        constraintSet.connect(advancedView.getId(), ConstraintSet.TOP, R.id.includedData, ConstraintSet.BOTTOM, 10);
        constraintSet.connect(advancedView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        constraintSet.connect(advancedView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);

        constraintSet.applyTo(layout);
    }

    private void bindAdvanced(View view) {
        advancedGasLimit = view.findViewById(R.id.gasLimitInput);
        advancedGasPrice = view.findViewById(R.id.gasPriceInput);
        advancedNonce = view.findViewById(R.id.nonceInput);
        advancedHide = view.findViewById(R.id.advancedHideButton);

        viewModel.force();

        advancedHide.setOnClickListener(v -> {
            layout.removeView(view);
            layout.addView(backup);
        });
    }

    private void setIncludedDataTitle(ParcelableRawTransaction rawTransaction) {
        int bytes = rawTransaction.getData().getBytes().length;
        StringBuilder bytesStr = new StringBuilder("Included data");
        bytesStr.append(" (").append(bytes).append(" byte");
        if(bytes > 1) bytesStr.append("s");
        bytesStr.append(")");
        includedDataTitleText.setText(bytesStr);
    }

    private void setIncludedData(ParcelableRawTransaction rawTransaction) {
        if(rawTransaction.getData().length() > 100) {
            includedDataText.setText(String.format("%s...", rawTransaction.getData().substring(0, 100)));
            showAllButton.setEnabled(true);
            showAllButton.setOnClickListener(v -> {
                if(includedDataText.getText().length() > 103) {
                    ((Button) v).setText("Show all");
                    includedDataText.setText(String.format("%s...", rawTransaction.getData().substring(0, 100)));
                } else {
                    ((Button) v).setText("Hide");
                    includedDataText.setText(rawTransaction.getData());
                }
            });
        } else {
            includedDataText.setText(rawTransaction.getData());
            showAllButton.setEnabled(false);
        }
    }

    private void subscribeDispatch(EthSendTransaction ethSendTransaction) {
        toggleButtons(true);
        if(ethSendTransaction != null) {
            if(!ethSendTransaction.hasError()) {
                listener.onTransactionHashReceived(viewModel.getRemoteID(), ethSendTransaction.getTransactionHash());
            } else Snackbar.make(layout, "Error sending transaction:\n" + ethSendTransaction.getError().getMessage(), Snackbar.LENGTH_LONG).show();
        } else Snackbar.make(layout, "Error sending transaction. Check your connection!", Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.approve)
    public void approve(View view) {
        if(viewModel.isSenderSelected()) {
            toggleButtons(false);
            viewModel.dispatch();
        } else Snackbar.make(layout, "Sender address is not selected", Snackbar.LENGTH_LONG).show();
    }

    private void toggleButtons(boolean enabled) {
        approve.setEnabled(enabled);
        reject.setEnabled(enabled);
        credentialsSpinner.setEnabled(enabled);
        advancedButton.setEnabled(enabled);
        if(advancedGasPrice != null) advancedGasPrice.setFocusable(enabled);
        if(advancedGasLimit != null) advancedGasLimit.setFocusable(enabled);
        if(advancedNonce != null) advancedNonce.setFocusable(enabled);
    }

    @OnClick(R.id.reject)
    public void reject(View view) {
        activityContext.finish();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnTransactionHashReceivedListener && context instanceof FragmentActivity) {
            activityContext = (FragmentActivity) context;
            listener = (OnTransactionHashReceivedListener) context;
        } else throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener and be an Activity");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        unbinder.unbind();
    }

    public interface OnTransactionHashReceivedListener {
        void onTransactionHashReceived(String remoteID, String hash);
    }
}
