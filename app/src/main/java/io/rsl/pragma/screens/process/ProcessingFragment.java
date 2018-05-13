package io.rsl.pragma.screens.process;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.rsl.pragma.R;

public class ProcessingFragment extends Fragment {

    private static final String TX_HASH = "TXHASH";
    //private static final String GAS_PRICE = "GASPRICE";
    private static final String REMOTE_ID = "REMOTE ID";

    @BindView(R.id.hash)        TextView    hashText;
    @BindView(R.id.gasPrice)    TextView    gasPriceText;
    @BindView(R.id.from)        TextView    fromText;
    @BindView(R.id.to)          TextView    toText;
    @BindView(R.id.blockNum)    TextView    blockNumText;
    @BindView(R.id.progress)    ProgressBar progress;

    @BindView(R.id.scrollView)  ScrollView  scrollView;

    private ProcessingViewModel             viewModel;

    private OnTransactionConfirmListener    listener;
    private Unbinder                        unbinder;

    public ProcessingFragment() {}

    public static ProcessingFragment newInstance(String remoteID, String transactionHash) {
        ProcessingFragment fragment = new ProcessingFragment();
        Bundle args = new Bundle();
        args.putString(REMOTE_ID, remoteID);
        args.putString(TX_HASH, transactionHash);
        //args.putSerializable(GAS_PRICE, gasPrice);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ProcessingViewModel.class);
        assert getArguments() != null;
        viewModel.init(getArguments().getString(REMOTE_ID), getArguments().getString(TX_HASH));

        subscribeUI();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_processing, container, false);
        unbinder = ButterKnife.bind(this, fragmentView);

        hashText.setText(getArguments().getString(TX_HASH));

        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTransactionConfirmListener)
            listener = (OnTransactionConfirmListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
    }

    private void subscribeUI() {
        viewModel.getTransactionReceipt().observe(this, this::updateUI);
    }

    private void updateUI(TransactionReceipt receipt) {
        if(receipt != null) {
            gasPriceText.setText(viewModel.getGasPrice().toString());
            fromText.setText(receipt.getFrom());
            toText.setText(Objects.equals(receipt.getTo(), "") ? "Not specified during contract deploy" : receipt.getTo());
            blockNumText.setText(receipt.getBlockNumber().toString());
            progress.setVisibility(View.INVISIBLE);
        } else {
            progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        unbinder.unbind();
    }

    @OnClick(R.id.next)
    void next(View view) {
        if(viewModel.isConfirmed()) {
            listener.onTransactonConfirm(viewModel.saveAndReceipt());
        }
    }

    public interface OnTransactionConfirmListener {
        void onTransactonConfirm(TransactionReceipt transactionReceipt);
    }
}
