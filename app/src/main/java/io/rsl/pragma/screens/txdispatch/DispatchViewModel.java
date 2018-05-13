package io.rsl.pragma.screens.txdispatch;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.JsonRpc2_0Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.RawTransactionManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import javax.inject.Inject;

import io.rsl.pragma.App;
import io.rsl.pragma.db.dao.ContractsDao;
import io.rsl.pragma.db.dao.CredentialsDao;
import io.rsl.pragma.db.dbmodels.DBCredential;
import io.rsl.pragma.db.dbmodels.DBTransaction;
import io.rsl.pragma.repositories.ContractsRepository;
import io.rsl.pragma.repositories.CredentialsRepository;
import io.rsl.pragma.repositories.TransactionsRepository;
import io.rsl.pragma.screens.poll.eth.Ballot;
import io.rsl.pragma.utils.web3.ParcelableRawTransaction;
import io.rsl.pragma.utils.web3.QFastRawTransactionManager;
import timber.log.Timber;

public class DispatchViewModel extends AndroidViewModel {

    @Inject
    ContractsRepository contractsRepository;

    @Inject
    CredentialsRepository credentialsRepository;

    @Inject
    Web3j web3j;

    @Inject
    TransactionsRepository transactionsRepository;

    private MutableLiveData<EthSendTransaction>         hashLiveData;
    private MutableLiveData<GasAmount>                  gasAmountLiveData;
    private MutableLiveData<ParcelableRawTransaction>   rawTransactionLiveData;
    private MutableLiveData<List<DBCredential>>         credentialsLiveData;

    private String lastAddress;

    private String remoteID;

    public DispatchViewModel(@NonNull Application application) {
        super(application);
        ((App) application).getComponent().inject(this);
    }

    public void init(String remoteID, @NonNull ParcelableRawTransaction rawTransaction) {
        this.remoteID = remoteID;
        rawTransactionLiveData = new MutableLiveData<>();
        rawTransactionLiveData.setValue(rawTransaction);
    }

    public LiveData<List<DBCredential>> getCredentials() {
        if(credentialsLiveData == null)
            credentialsLiveData = credentialsRepository.getCredentials();
        return credentialsLiveData;
    }

    public LiveData<ParcelableRawTransaction> getRawTranscation() {
        if(rawTransactionLiveData.getValue().getGasPrice() == null) reloadGasPrice();
        return rawTransactionLiveData;
    }

    public LiveData<GasAmount> getGasAmount() {
        if(gasAmountLiveData == null) {
            gasAmountLiveData = new MutableLiveData<>();
            reloadGasAmount();
        }
        return gasAmountLiveData;
    }

    public LiveData<EthSendTransaction> getHash() {
        if(hashLiveData == null) {
            hashLiveData = new MutableLiveData<>();
        }
        return hashLiveData;
    }

    public void force() {
        rawTransactionLiveData.setValue(rawTransactionLiveData.getValue());
    }

    public void reloadGasPrice() {
        AsyncTask.execute(() -> {
            BigInteger gasPrice;
            try {
                gasPrice = web3j.ethGasPrice().send().getGasPrice();
            } catch (IOException e) {
                gasPrice = Contract.GAS_PRICE;
                e.printStackTrace();
            }
            ParcelableRawTransaction rawTransaction = rawTransactionLiveData.getValue();
            rawTransaction.setGasPrice(gasPrice);
            rawTransactionLiveData.postValue(rawTransaction);

            reloadGasAmount();
        });
    }

    public void updateGasPrice(BigInteger newGasPrice) {
        ParcelableRawTransaction rawTransaction = rawTransactionLiveData.getValue();
        rawTransaction.setGasPrice(newGasPrice);
        rawTransactionLiveData.setValue(rawTransaction);

        reloadGasAmount();
    }

    private void reloadGasAmount() {
        if(lastAddress != null) {
            AsyncTask.execute(() -> {
                GasAmount gasAmount = gasAmountLiveData.getValue();
                if (gasAmount == null) {
                    try {
                        BigInteger estGas = web3j.ethEstimateGas(rawTransactionLiveData.getValue().getTransaction(lastAddress)).send().getAmountUsed();
                        gasAmount = new GasAmount(estGas, rawTransactionLiveData.getValue().getGasPrice());
                    } catch(IOException e) {
                        gasAmount = null;
                        e.printStackTrace();
                    }
                } else {
                    gasAmount.recalcEthPrice(rawTransactionLiveData.getValue().getGasPrice());
                }
                gasAmountLiveData.postValue(gasAmount);
            });
        }
    }

    public void setLastAddress(String lastAddress) {
        this.lastAddress = lastAddress;
        reloadGasPrice();
    }

    public boolean isSenderSelected() {
        return lastAddress != null;
    }

    public BigDecimal getGweiPrice(BigInteger price) {
        return new BigDecimal(price).setScale(9, RoundingMode.UNNECESSARY).divide(new BigDecimal("1000000000"), BigDecimal.ROUND_UNNECESSARY);
    }

    public BigInteger getGasPrice() {
        if(rawTransactionLiveData.getValue() != null) {
            return rawTransactionLiveData.getValue().getGasPrice();
        } else return null;
    }

    public String getRemoteID() {
        return remoteID;
    }

    public void dispatch() {
        AsyncTask.execute(() -> {

            EthSendTransaction ethSendTransaction = null;
            try {
                DBCredential credential = credentialsRepository.getCredential(lastAddress);

                QFastRawTransactionManager transactionManager = new QFastRawTransactionManager(
                        web3j, Credentials.create(new ECKeyPair(new BigInteger(credential.getPrivateKey(), 16), new BigInteger(credential.getPublicKey(), 16))));
                ethSendTransaction = transactionManager.signAndSend(rawTransactionLiveData.getValue());
            } catch (IOException e) {
                Timber.e(e);
            }
            if(ethSendTransaction != null && ethSendTransaction.getTransactionHash() != null) {
                contractsRepository.setHash(remoteID, ethSendTransaction.getTransactionHash(), lastAddress);
                transactionsRepository.addTransaction(new DBTransaction(ethSendTransaction, getGasPrice()));
            }

            hashLiveData.postValue(ethSendTransaction);
        });
    }
}
