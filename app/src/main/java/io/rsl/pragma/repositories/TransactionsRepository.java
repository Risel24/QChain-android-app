package io.rsl.pragma.repositories;

import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

import io.rsl.pragma.db.dao.TransactionsDao;
import io.rsl.pragma.db.dbmodels.DBTransaction;

public class TransactionsRepository {

    private TransactionsDao transactionsDao;

    private final MutableLiveData<List<DBTransaction>> data = new MutableLiveData<>();

    private boolean loaded = false;

    public TransactionsRepository(TransactionsDao transactionsDao) {
        this.transactionsDao = transactionsDao;
    }

    public MutableLiveData<List<DBTransaction>> getTransactions() {
        if(!loaded) {
            AsyncTask.execute(() -> {
                data.postValue(transactionsDao.getAllTransactions());
                loaded = true;
            });
        }
        return data;
    }

    public DBTransaction getCredential(String hash) {
        List<DBTransaction> transactions = data.getValue();
        if(loaded && transactions != null) {
            for(DBTransaction transaction : transactions) {
                if(Objects.equals(transaction.getTxHash(), hash)) return transaction;
            }
        }
        return transactionsDao.getTransaction(hash);
    }

    public void addTransaction(DBTransaction transaction) {
        AsyncTask.execute(() -> {
            transactionsDao.insert(transaction);
            data.postValue(transactionsDao.getAllTransactions());
        });
    }

    public void setReceipt(TransactionReceipt transactionReceipt) {
        AsyncTask.execute(() -> {
            transactionsDao.setReceipt(transactionReceipt.getTransactionHash(), transactionReceipt.getFrom(),
                    transactionReceipt.getTo(), transactionReceipt.getGasUsed().toString(), transactionReceipt.getBlockNumber().toString());
            data.postValue(transactionsDao.getAllTransactions());
        });
    }

}
