package io.rsl.pragma.screens.wallets;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.rsl.pragma.db.dao.CredentialsDao;
import io.rsl.pragma.db.dbmodels.DBCredential;

public class CredentialsSelectorDialog {

    private OnCredentialSelectListener listener;
    private Context context;

    private CredentialsDao credentialsDao;

    public CredentialsSelectorDialog(Context context, CredentialsDao credentialsDao) {
        this.context = context;
        this.credentialsDao = credentialsDao;
    }

    public void setOnCredentialsSelectorListener(OnCredentialSelectListener listener) {
        this.listener = listener;
    }

    public void show() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        RecyclerView recyclerView = new RecyclerView(context);
        dialogBuilder.setView(recyclerView);
        dialogBuilder.setMessage("Credentials:");
        AlertDialog dialog = dialogBuilder.create();

        List<DBCredential> dbCredentials = credentialsDao.getAllCredentials();
        List<String> addresses = new ArrayList<>();
        for(DBCredential c: dbCredentials) {
            addresses.add(c.getAddress());
        }
        dbCredentials.clear();

        if(addresses.size() == 0) {
            //TODO
        } else {
            CredentialsRecyclerAdapter credentialsRecyclerAdapter = new CredentialsRecyclerAdapter(/*addresses, credentialsDao*/);
            credentialsRecyclerAdapter.setOnItemClickListener(listener);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(credentialsRecyclerAdapter);

            dialog.show();
        }
    }

    public interface OnCredentialSelectListener {
        void onSelect(DBCredential dbCredential);
        void onLongClick(DBCredential dbCredential);
    }
}
