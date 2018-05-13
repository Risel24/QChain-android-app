package io.rsl.pragma.screens.history;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import io.rsl.pragma.App;
import io.rsl.pragma.db.dbmodels.DBTransaction;
import io.rsl.pragma.repositories.TransactionsRepository;
import timber.log.Timber;

public class HistoryViewModel extends AndroidViewModel {

    private LiveData<List<DBTransaction>> transactions;

    @Inject
    TransactionsRepository transactionsRepository;

    @Inject
    Web3j web3j;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        ((App) application).getComponent().inject(this);
    }

    public LiveData<List<DBTransaction>> getTransactions() {
        if(transactions == null) {
            transactions = transactionsRepository.getTransactions();
        }
        return transactions;
    }

    public void checkTransactions() {
        List<DBTransaction> transactionList = transactions.getValue();
        if(transactionList != null) {
            for (DBTransaction transaction : transactionList) {
                if (transaction.isPending()) {
                    try {
                        TransactionReceipt receipt = web3j.ethGetTransactionReceipt(transaction.getTxHash()).send().getTransactionReceipt();
                        if (receipt != null) transactionsRepository.setReceipt(receipt);
                    } catch (IOException e) {
                        Timber.e(e);
                    }
                }
            }
        }
    }

}
