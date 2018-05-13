package io.rsl.pragma.screens.wallets;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import io.rsl.pragma.App;
import io.rsl.pragma.db.dbmodels.DBCredential;
import io.rsl.pragma.repositories.CredentialsRepository;

public class CredentialsViewModel extends AndroidViewModel {

    private LiveData<List<DBCredential>> credentials;

    @Inject
    CredentialsRepository credentialsRepository;

    public CredentialsViewModel(@NonNull Application application) {
        super(application);
        ((App) application).getComponent().inject(this);
    }

    public LiveData<List<DBCredential>> getCredentials() {
        if(credentials == null) {
            credentials = credentialsRepository.getCredentials();
        }
        return credentials;
    }

    public void delete(DBCredential cred) {
        credentialsRepository.delete(cred);
    }
}
