package io.rsl.pragma.screens.contracts;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.JsonRpc2_0Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.response.NoOpProcessor;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import io.rsl.pragma.App;
import io.rsl.pragma.api.QService;
import io.rsl.pragma.api.models.ContractsListResponse;
import io.rsl.pragma.db.dbmodels.DBContract;
import io.rsl.pragma.repositories.ContractsRepository;
import io.rsl.pragma.utils.ContractConstant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ContractsViewModel extends AndroidViewModel {

    private LiveData<List<DBContract>> contracts;

    @Inject
    ContractsRepository contractsRepository;

    @Inject
    Web3j web3j;

    @Inject
    QService qService;

    public ContractsViewModel(@NonNull Application application) {
        super(application);
        ((App) application).getComponent().inject(this);
    }


    public LiveData<List<DBContract>> getContracts() {
        if(contracts == null) {
            contracts = contractsRepository.getContracts();
        }
        return contracts;
    }

    public void update() {
        contractsRepository.force();
    }

    public void delete(DBContract cont) {
        contractsRepository.delete(cont);
    }

    public void checkContracts() {
        AsyncTask.execute(() -> {
            for(DBContract contract : contracts.getValue()) {
                if(contract.getStatus() == ContractConstant.PENDING) {
                    try {
                        TransactionReceipt receipt = web3j.ethGetTransactionReceipt(contract.getInitHash()).send().getTransactionReceipt();
                        if(receipt != null) contractsRepository.setReceipt(contract.getRemoteId(), receipt);
                    } catch (IOException e) {
                        Timber.e(e);
                    }
                }
            }
        });
    }

}