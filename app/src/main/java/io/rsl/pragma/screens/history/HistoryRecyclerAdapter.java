package io.rsl.pragma.screens.history;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.rsl.pragma.R;
import io.rsl.pragma.db.dbmodels.DBTransaction;

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.ViewHolder> {

    private List<DBTransaction> transactions;
    private OnTransactionClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final DBTransaction transaction = transactions.get(position);

        holder.hashText.setText(transaction.getTxHash());

        GradientDrawable shapeDrawable = (GradientDrawable) holder.dot;
        shapeDrawable.setColor(transaction.hasError() ? Color.RED : Color.GREEN);
        holder.statusImage.setImageDrawable(shapeDrawable);

        if(listener != null) listener.onTransactionSelect(transaction);
    }

    @Override
    public int getItemCount() {
        return transactions == null ? 0 : transactions.size();
    }

    public void setOnTransactionClickListener(OnTransactionClickListener listener) {
        this.listener = listener;
    }

    public void setTransactionsList(final List<DBTransaction> newTransactions) {
        if (transactions == null) {
            transactions = newTransactions;
            notifyItemRangeInserted(0, newTransactions.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return transactions.size();
                }

                @Override
                public int getNewListSize() {
                    return newTransactions.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return Objects.equals(transactions.get(oldItemPosition).getTxHash(), newTransactions.get(newItemPosition).getTxHash());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    DBTransaction newTransaction = newTransactions.get(newItemPosition);
                    DBTransaction oldTransaction = transactions.get(oldItemPosition);
                    return Objects.equals(newTransaction.getTxHash(), oldTransaction.getTxHash())
                            && Objects.equals(newTransaction.getFrom(), oldTransaction.getFrom())
                            && Objects.equals(newTransaction.getTo(), oldTransaction.getTo())
                            && Objects.equals(newTransaction.getGasUsed(), oldTransaction.getGasUsed())
                            && Objects.equals(newTransaction.getGasUsed(), oldTransaction.getGasUsed())
                            && Objects.equals(newTransaction.getBlockNum(), oldTransaction.getBlockNum())
                            && Objects.equals(newTransaction.getError(), oldTransaction.getError());
                }
            });
            transactions = new ArrayList<>(newTransactions);
            result.dispatchUpdatesTo(this);
        }
    }

    public interface OnTransactionClickListener {
        void onTransactionSelect(DBTransaction transaction);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.statusImage) ImageView statusImage;
        @BindView(R.id.hashText) TextView hashText;

        @BindDrawable(R.drawable.dot) Drawable dot;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
