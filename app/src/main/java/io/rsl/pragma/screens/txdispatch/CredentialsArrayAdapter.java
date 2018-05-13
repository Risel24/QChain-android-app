package io.rsl.pragma.screens.txdispatch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.rsl.pragma.R;
import io.rsl.pragma.db.dbmodels.DBCredential;
import io.rsl.pragma.utils.Blockies;
import timber.log.Timber;

public class CredentialsArrayAdapter extends ArrayAdapter<DBCredential> {

    private List<DBCredential> credentials;
    private LayoutInflater layoutInflater;
    private int layoutRes;

    public CredentialsArrayAdapter(@NonNull Context context, int resource, List<DBCredential> credentials) {
        super(context, resource, credentials);
        this.credentials = credentials;
        this.layoutRes = resource;
        layoutInflater = LayoutInflater.from(context);
    }

    public String getAddress(int position) {
        return credentials.get(position).getAddress();
    }

    public void setCredentials(List<DBCredential> newCredentials) {
        credentials.clear();
        credentials.addAll(newCredentials); // TODO Think about it
        notifyDataSetChanged();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, parent);
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, parent);
    }

    private View createItemView(int position, ViewGroup parent) {
        View view = layoutInflater.inflate(layoutRes, parent, false);

        TextView addressText = view.findViewById(R.id.addressText);
        TextView importStatusText = view.findViewById(R.id.importStatusText);
        ImageView avatar = view.findViewById(R.id.avatar);

        final String address = credentials.get(position).getAddress();
        addressText.setText(address);
        if(credentials.get(position).isPrivateNotImported()) {
            importStatusText.setText("Private key is NOT imported!");
            importStatusText.setTextColor(Color.RED);
        } else {
            importStatusText.setText("Private key is imported!");
            importStatusText.setTextColor(Color.GREEN);
        }
        AsyncTask.execute(() -> {
            Bitmap blockies = Blockies.createIcon(address);
            new Handler(getContext().getMainLooper()).post(() -> avatar.setImageBitmap(blockies));
        });

        return view;
    }
}