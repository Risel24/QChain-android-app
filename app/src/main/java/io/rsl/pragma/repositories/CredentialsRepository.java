package io.rsl.pragma.repositories;

import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import java.util.List;
import java.util.Objects;

import io.rsl.pragma.db.dao.CredentialsDao;
import io.rsl.pragma.db.dbmodels.DBCredential;

public class CredentialsRepository {

    private CredentialsDao credentialsDao;
    private final MutableLiveData<List<DBCredential>> data = new MutableLiveData<>();

    private boolean loaded = false;

    public CredentialsRepository(CredentialsDao credentialsDaoe) {
        this.credentialsDao = credentialsDao;
    }

    public MutableLiveData<List<DBCredential>> getCredentials() {
        if(!loaded) {
            AsyncTask.execute(() -> {
                data.postValue(credentialsDao.getAllCredentials());
                loaded = true;
            });
        }
        return data;
    }

    public DBCredential getCredential(String address) {
        List<DBCredential> credentials = data.getValue();
        if(loaded && credentials != null) {
            for(DBCredential cred : credentials) {
                if(Objects.equals(cred.getAddress(), address)) return cred;
            }
        }
        return credentialsDao.getCredentials(address);
    }

    public void delete(DBCredential dbCredential) {
        AsyncTask.execute(() -> {
            credentialsDao.delete(dbCredential);
            data.postValue(credentialsDao.getAllCredentials());
        });
    }

    public void addCredential(DBCredential credential) {
        AsyncTask.execute(() -> {
            credentialsDao.insert(credential);
            data.postValue(credentialsDao.getAllCredentials());
        });
        /*List<DBCredential> newList = data.getValue();
        if (newList != null) {
            newList.add(credential);
            data.setValue(newList);
        }*/
    }

    /*public void showCredentialsSelectorDialog(CredentialsSelectorDialog.OnCredentialSelectListener listener) {
        CredentialsSelectorDialog selectorDialog = new CredentialsSelectorDialog(context, credentialsDao);
        selectorDialog.setOnCredentialsSelectorListener(listener);
        selectorDialog.show();
    }*/

}
