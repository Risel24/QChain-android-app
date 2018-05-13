package io.rsl.pragma.screens.wallets;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rsl.pragma.R;
import io.rsl.pragma.db.dbmodels.DBCredential;
import io.rsl.pragma.utils.Blockies;
import timber.log.Timber;

public class CredentialsRecyclerAdapter extends RecyclerView.Adapter<CredentialsRecyclerAdapter.CredentialsViewHolder> {

    private OnCredentialsSelectListener listener;

    private List<DBCredential> credentials = new ArrayList<>();

    @NonNull
    @Override
    public CredentialsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CredentialsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_credentials, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CredentialsViewHolder holder, int position) {
        final DBCredential credential = credentials.get(position);
        holder.addressText.setText(credential.getAddress());
        if(credential.isPrivateNotImported()) {
            holder.importStatusText.setText("Private key is NOT imported!");
            holder.importStatusText.setTextColor(Color.RED);
        } else {
            holder.importStatusText.setText("Private key is imported!");
            holder.importStatusText.setTextColor(Color.GREEN);
        }
        AsyncTask.execute(() -> {
            Bitmap bitmap = Blockies.createIcon(credential.getAddress());
            new Handler(Looper.getMainLooper()).post(() -> holder.avatar.setImageBitmap(bitmap));
        });

        if(listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onCredentialsSelect(credential));
            holder.itemView.setOnLongClickListener(v -> {
                listener.onCredentialsLongClick(credential);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return credentials.size();
    }

    public void setOnCredentialsSelectListener(OnCredentialsSelectListener listener) {
        this.listener = listener;
    }

    public void setCredentialList(List<DBCredential> newCredentials) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return credentials.size();
            }

            @Override
            public int getNewListSize() {
                return newCredentials.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return Objects.equals(credentials.get(oldItemPosition).getAddress(), newCredentials.get(newItemPosition).getAddress());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                DBCredential newCredential = newCredentials.get(newItemPosition);
                DBCredential oldCredential = credentials.get(oldItemPosition);
                return Objects.equals(newCredential.getAddress(), oldCredential.getAddress())
                        && Objects.equals(newCredential.getPublicKey(), oldCredential.getPublicKey())
                        && Objects.equals(newCredential.getPrivateKey(), oldCredential.getPrivateKey());
            }
        });
        credentials = new ArrayList<>(newCredentials); //TODO think about it!
        result.dispatchUpdatesTo(this);
    }

    public interface OnCredentialsSelectListener {
        void onCredentialsSelect(DBCredential dbCredential);
        void onCredentialsLongClick(DBCredential dbCredential);
    }

    static class CredentialsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.addressText) TextView addressText;
        @BindView(R.id.importStatusText) TextView importStatusText;
        @BindView(R.id.avatar) ImageView avatar;

        CredentialsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
