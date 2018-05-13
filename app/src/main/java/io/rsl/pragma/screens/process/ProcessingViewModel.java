package io.rsl.pragma.screens.process;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.response.Callback;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.QueuingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;
import org.web3j.utils.Async;

import java.io.IOException;
import java.math.BigInteger;

import javax.inject.Inject;

import io.rsl.pragma.App;
import io.rsl.pragma.repositories.ContractsRepository;
import io.rsl.pragma.repositories.TransactionsRepository;
import io.rsl.pragma.screens.poll.eth.Ballot;
import io.rsl.pragma.screens.txdispatch.GasAmount;
import io.rsl.pragma.utils.web3.QFastRawTransactionManager;
import timber.log.Timber;

public class ProcessingViewModel extends AndroidViewModel {

    private MutableLiveData<TransactionReceipt> data;

    PollingTransactionReceiptProcessor processor;

    private String      hash;
    private BigInteger  gasPrice;
    private String      remoteID;

    private boolean confirmed = false;

    @Inject
    Web3j web3j;

    @Inject
    ContractsRepository contractsRepository;

    @Inject
    TransactionsRepository transactionsRepository;

    public ProcessingViewModel(@NonNull Application application) {
        super(application);
        ((App) application).getComponent().inject(this);
    }

    public void init(String remoteID, String hash) {
        this.remoteID = remoteID;
        this.hash = hash;

        processor = new PollingTransactionReceiptProcessor(web3j, 50, 5000);
    }

    public LiveData<TransactionReceipt> getTransactionReceipt() {
        if(data == null) {
            data = new MutableLiveData<>();
            startPolling();
        }
        return data;
    }

    public void startPolling() {
        AsyncTask.execute(() -> {
            try {
                TransactionReceipt transactionReceipt = processor.waitForTransactionReceipt(hash);
                gasPrice = web3j.ethGetTransactionByHash("").send().getTransaction().getGasPrice();
                data.postValue(transactionReceipt);
                confirmed = true;
            } catch (IOException | TransactionException e) {
                Timber.e(e);
                data.postValue(null);
                confirmed = false;
            }
        });
    }

    public GasAmount getGasPrice() {
        TransactionReceipt receipt = data.getValue();
        if(receipt != null) {
            return new GasAmount(receipt.getGasUsed(), gasPrice);
        } else return null;
    }

    public TransactionReceipt saveAndReceipt() {
        TransactionReceipt receipt = data.getValue();
        if(receipt != null) {
            contractsRepository.setReceipt(remoteID, receipt);
            transactionsRepository.setReceipt(receipt);
            return data.getValue();
        } else return null;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
