package io.rsl.pragma.screens.wallets.create;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rsl.pragma.R;
import io.rsl.pragma.db.dbmodels.DBCredential;
import timber.log.Timber;

public class CredentialsImportActivity extends AppCompatActivity {

    public static final String CREDENTIALS_KEY = "CREDENTIALS";
    private static final int FILE_REQUEST_CODE = 12523;

    @BindView(R.id.toolbar)   Toolbar   toolbar;
    @BindView(R.id.avatar)    ImageView avatar;
    @BindView(R.id.address)   TextView  addressText;
    @BindView(R.id.publicKey) TextView  publicKeyText;
    @BindView(R.id.privateKey)TextView  privateKeyText;
    @BindView(R.id.show)      ImageView show;

    private CredentialImportViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials_import);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        viewModel = ViewModelProviders.of(this).get(CredentialImportViewModel.class);
        viewModel.init(getIntent().getParcelableExtra(CREDENTIALS_KEY));

        subscribeUI();
    }

    private void subscribeUI() {
        viewModel.getCredentials().observe(this, this::updateUI);
        viewModel.getBlockies().observe(this, avatar::setImageBitmap);
    }

    private void updateUI(DBCredential credential) {
        if(credential != null) {
            if(viewModel.isCorrectKey()) {
                addressText.setText(credential.getAddress());
                publicKeyText.setText(credential.getPublicKey() == null ? "Private key is not imported!" : credential.getPublicKey());
                privateKeyText.setText(credential.getPrivateKey() == null ? "Private key is not imported!" : credential.getPrivateKey());
            } else showErrorSnackbar();
        }
    }

    private void showErrorSnackbar() {
        Snackbar.make(findViewById(android.R.id.content), "Incorrect private key!", Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.fromPrivateKey)
    void importPrivateKey(View view) {
        EditText editText = new EditText(this);
        editText.setHint("Private key");
        editText.setHintTextColor(Color.parseColor("#ff616161"));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Enter hexadecimal private key")
                .setView(editText)
                .setPositiveButton("Set", (d, which) -> {
                    d.dismiss();
                    AsyncTask.execute(() -> {
                        try {
                            viewModel.updateCredential(Credentials.create(editText.getText().toString()));
                        } catch (NumberFormatException e) {
                            runOnUiThread(this::showErrorSnackbar);
                        }
                    });
                })
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .create();

        dialog.show();
    }

    @OnClick(R.id.fromMnemonic)
    void importMnemonic(View view) {
        View alertView = LayoutInflater.from(this).inflate(R.layout.layout_two_edittext, null);

        EditText mnemonic = alertView.findViewById(R.id.mnemonic);
        EditText pass = alertView.findViewById(R.id.pass);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Enter password for selected file")
                .setView(alertView)
                .setPositiveButton("Set", (d, which) -> {
                    d.dismiss();
                    AsyncTask.execute(() -> {
                        try {
                            viewModel.updateCredential(WalletUtils.loadBip39Credentials(pass.getText().toString(), mnemonic.getText().toString()));
                        } catch (Exception e) {
                            runOnUiThread(this::showErrorSnackbar);
                        }
                    });
                })
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .create();

        dialog.show();
    }

    @OnClick(R.id.show)
    void show(View view) {
        viewModel.showHide();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.import_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_and_exit:
                if(viewModel.isNullCred()) {
                    showNullWarn();
                } else {
                    viewModel.save();
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showNullWarn() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Credentials is not set")
                .setPositiveButton("Close anyway", (d, which) -> {
                    viewModel.save();
                    finish();
                })
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .create();

        dialog.show();
    }
}
