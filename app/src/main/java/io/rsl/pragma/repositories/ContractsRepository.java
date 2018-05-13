package io.rsl.pragma.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.rsl.pragma.api.QService;
import io.rsl.pragma.api.models.ContractInit;
import io.rsl.pragma.api.models.ContractsListResponse;
import io.rsl.pragma.db.dao.ContractsDao;
import io.rsl.pragma.db.dbmodels.DBContract;
import io.rsl.pragma.db.dbmodels.DBCredential;
import io.rsl.pragma.utils.ContractConstant;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ContractsRepository {

    private ContractsDao contractsDao;
    private QService qService;

    private CredentialsRepository credentialsRepository;

    private final MutableLiveData<List<DBContract>> data = new MutableLiveData<>();

    private boolean loaded = false;

    public ContractsRepository(ContractsDao contractsDao, QService qService, CredentialsRepository credentialsRepository) {
        this.contractsDao = contractsDao;
        this.qService = qService;
        this.credentialsRepository = credentialsRepository;
    }

    public void force() {
        qService.getContracts().enqueue(new Callback<ContractsListResponse>() {
            @Override
            public void onResponse(@NonNull Call<ContractsListResponse> call, @NonNull Response<ContractsListResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<DBContract> contractList = response.body().getContracts();
                    for(DBContract dbContract : contractList) {
                        if (dbContract.getOwnerAddress() != null)
                            credentialsRepository.addCredential(new DBCredential(dbContract.getOwnerAddress()));
                    }
                    data.postValue(contractList);
                    loaded = true;
                } else {
                    data.postValue(null);
                    loaded = false;
                }
            }

            @Override
            public void onFailure(@NonNull Call<ContractsListResponse> call, @NonNull Throwable t) {
                data.postValue(null);
                loaded = false;
            }
        });
    }

    public void delete(DBContract dbContract) {
        List<DBContract> contracts = data.getValue();
        for(int i = 0; i < contracts.size(); i++) {
            if(Objects.equals(contracts.get(i).getRemoteId(), dbContract.getRemoteId())) {
                contracts.remove(i);
                break;
            }
        }

        qService.deleteContract(dbContract.getRemoteId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if(response.isSuccessful() && response.body() != null) {
                    data.postValue(contracts);
                } else force();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                force();
            }
        });
    }

    public LiveData<List<DBContract>> getContracts() {
        if(!loaded) {

            force();

            /*AsyncTask.execute(() -> {
                data.postValue(contractsDao.getAllContracts());
                loaded = true;
            });*/
        }
        return data;
    }

    public void setHash(String remoteID, String hash, String ownerAddress) {
        /*AsyncTask.execute(() -> {
            contractsDao.setHash(dbID, ContractConstant.PENDING,  hash, ownerAddress);
            data.postValue(contractsDao.getAllContracts());
        });*/
        List<DBContract> contracts = data.getValue();
        for(int i = 0; i < contracts.size(); i++) {
            if(Objects.equals(contracts.get(i).getRemoteId(), remoteID)) {
                DBContract c = contracts.get(i);
                c.setInitHash(hash);
                c.setOwnerAddress(ownerAddress);
                c.setStatus(ContractConstant.PENDING);
                contracts.set(i, c);
                break;
            }
        }

        HashMap<String, Object> newValues = new HashMap<>();
        newValues.put("hash", hash);
        newValues.put("ownerAddress", ownerAddress);
        newValues.put("status", ContractConstant.PENDING);
        qService.patchContract(remoteID, newValues).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if(response.isSuccessful() && response.body() != null) {
                    data.postValue(contracts);
                } else force();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                force();
            }
        });
    }

    public void setReceipt(String remoteID, TransactionReceipt transactionReceipt) {
        /*AsyncTask.execute(() -> {
            contractsDao.setReceipt(transactionReceipt.getTransactionHash(), ContractConstant.DEPLOYED, transactionReceipt.getContractAddress());
            data.postValue(contractsDao.getAllContracts());
        });*/
        List<DBContract> contracts = data.getValue();
        for(int i = 0; i < contracts.size(); i++) {
            if(Objects.equals(contracts.get(i).getInitHash(), transactionReceipt.getTransactionHash())) {
                DBContract c = contracts.get(i);
                c.setAddress(transactionReceipt.getContractAddress());
                c.setStatus(ContractConstant.DEPLOYED);
                contracts.set(i, c);
                break;
            }
        }

        HashMap<String, Object> newValues = new HashMap<>();
        newValues.put("address", transactionReceipt.getContractAddress());
        newValues.put("status", ContractConstant.DEPLOYED);
        qService.patchContract(remoteID, newValues).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if(response.isSuccessful() && response.body() != null) {
                    data.postValue(contracts);
                } else force();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                force();
            }
        });
    }

    public String addContract(DBContract contract) {
        /*long id = contractsDao.insert(contract);
        data.postValue(contractsDao.getAllContracts());
        return id;*/

        try {
            Response<ResponseBody> response = qService.postContract(new ContractInit(contract)).execute();
            if(response.isSuccessful() && response.body() != null) {
                JsonElement jelement = new JsonParser().parse(response.body().string());
                JsonObject jobject = jelement.getAsJsonObject();
                jobject = jobject.getAsJsonObject("createdContract");
                return jobject.get("_id").getAsString();
            } else {
                return null;
            }
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }

    }
}
