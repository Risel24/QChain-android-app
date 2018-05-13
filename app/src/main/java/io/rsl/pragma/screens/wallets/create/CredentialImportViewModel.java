package io.rsl.pragma.screens.wallets.create;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.web3j.crypto.Credentials;

import javax.inject.Inject;

import io.rsl.pragma.App;
import io.rsl.pragma.db.dbmodels.DBCredential;
import io.rsl.pragma.repositories.CredentialsRepository;
import io.rsl.pragma.utils.Blockies;

public class CredentialImportViewModel extends AndroidViewModel {

    @Inject
    CredentialsRepository credentialsRepository;

    private MutableLiveData<DBCredential> credentialLiveData;
    private MutableLiveData<Bitmap>       bitmapLiveData;
    private String privateBack;
    private boolean hidden = true;
    private boolean nullCred;

    private boolean correctKey = true;

    public CredentialImportViewModel(@NonNull Application application) {
        super(application);
        ((App) application).getComponent().inject(this);
    }

    public void init(DBCredential credential) {
        credentialLiveData = new MutableLiveData<>();
        bitmapLiveData = new MutableLiveData<>();

        credentialLiveData.setValue(credential);
        nullCred = credential == null;
        if(!nullCred) generateBitmap(credential.getAddress());
    }

    private void generateBitmap(String address) {
        AsyncTask.execute(() -> bitmapLiveData.postValue(Blockies.createIcon(address)));
    }

    public void updateCredential(Credentials c) {
        correctKey = true;
        nullCred = false;
        DBCredential dbCredential = credentialLiveData.getValue();
        if (dbCredential == null) {
            privateBack = c.getEcKeyPair().getPrivateKey().toString(16);
            credentialLiveData.postValue(new DBCredential(c.getAddress(),
                    c.getEcKeyPair().getPublicKey().toString(16),
                    "Private key is imported and hidden for security reason!"));
            generateBitmap(c.getAddress());
        } else {
            if (dbCredential.getAddress().equals(c.getAddress())) {
                correctKey = false;
            } else {
                dbCredential.setPublicKey(c.getEcKeyPair().getPublicKey().toString(16));
                privateBack = c.getEcKeyPair().getPrivateKey().toString(16);
                dbCredential.setPrivateKey("Private key is imported and hidden for security reason!");
            }
            credentialLiveData.postValue(dbCredential);
        }
    }

    public boolean isCorrectKey() {
        return correctKey;
    }

    public boolean isNullCred() {
        return nullCred;
    }

    public void showHide() {
        DBCredential dbCredential = credentialLiveData.getValue();
        if(dbCredential != null && dbCredential.getPrivateKey() != null) {
            if(!hidden) {
                privateBack = dbCredential.getPrivateKey();
                dbCredential.setPrivateKey("Private key is imported and hidden for security reason!");
            } else {
                dbCredential.setPrivateKey(privateBack);
            }
            hidden = !hidden;
            credentialLiveData.setValue(dbCredential);
        }
    }

    public void save() {
        DBCredential dbCredential = credentialLiveData.getValue();
        if(dbCredential != null) {
            if(hidden) dbCredential.setPrivateKey(privateBack);
            credentialsRepository.addCredential(dbCredential);
        }
    }

    public LiveData<DBCredential> getCredentials() {
        return credentialLiveData;
    }

    public LiveData<Bitmap> getBlockies() {
        return bitmapLiveData;
    }

}
