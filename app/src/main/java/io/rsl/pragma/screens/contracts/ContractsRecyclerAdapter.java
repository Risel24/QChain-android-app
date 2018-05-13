package io.rsl.pragma.screens.contracts;

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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.rsl.pragma.R;
import io.rsl.pragma.db.dbmodels.DBContract;
import io.rsl.pragma.utils.ContractConstant;

public class ContractsRecyclerAdapter extends RecyclerView.Adapter<ContractsRecyclerAdapter.ContractViewHolder> {

    private List<DBContract> contracts;
    private OnContractClickListener listener;

    public void setContractsList(final List<DBContract> newContracts) {
        Collections.sort(newContracts, (o1, o2) -> o2.getStatus() - o1.getStatus());
        if (contracts == null) {
            contracts = newContracts;
            notifyItemRangeInserted(0, newContracts.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return contracts.size();
                }

                @Override
                public int getNewListSize() {
                    return newContracts.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return contracts.get(oldItemPosition).getId() == newContracts.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    DBContract newContract = newContracts.get(newItemPosition);
                    DBContract oldContract = contracts.get(oldItemPosition);
                    return newContract.getId() == oldContract.getId()
                            && Objects.equals(newContract.getInitHash(), oldContract.getInitHash())
                            && Objects.equals(newContract.getAddress(), oldContract.getAddress())
                            && Objects.equals(newContract.getOwnerAddress(), oldContract.getOwnerAddress())
                            && Objects.equals(newContract.getData(), oldContract.getData())
                            && newContract.getStatus() == oldContract.getStatus()
                            && newContract.getType() == oldContract.getType();
                }
            });
            contracts = new ArrayList<>(newContracts);
            result.dispatchUpdatesTo(this);
        }
    }

    public void setOnContractClickListener(OnContractClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContractsRecyclerAdapter.ContractViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contract, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContractViewHolder holder, int position) {
        final DBContract contract = contracts.get(position);

        StringBuilder stringBuilder = new StringBuilder();
        switch (contract.getType()) {
            case ContractConstant.POLL_TYPE:
                stringBuilder.append("Poll contract (");
                break;
            default:
                break;
        }
        stringBuilder.append(contract.getTitle()).append(" )");

        holder.titleText.setText(stringBuilder);

        if(contract.getAddress() != null) holder.addressText.setText(contract.getAddress());
        if(contract.getInitHash() != null) holder.initHashText.setText(contract.getInitHash());

        int color;
        switch (contract.getStatus()) {
            case ContractConstant.SAVED:
                color = Color.RED;
                break;
            case ContractConstant.PENDING:
                color = Color.YELLOW;
                break;
            case ContractConstant.DEPLOYED:
                color = Color.GREEN;
                break;
            default:
                color = Color.WHITE;
                break;
        }

        GradientDrawable shapeDrawable = (GradientDrawable) holder.dot;
        shapeDrawable.setColor(color);
        holder.statusImage.setImageDrawable(shapeDrawable);

        if(listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onContractClick(contract));
            holder.itemView.setOnLongClickListener(v -> {
                listener.onContractLongClick(contract);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return contracts == null ? 0 : contracts.size();
    }

    public class ContractViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.titleText)       TextView  titleText;
        @BindView(R.id.addressText)     TextView  addressText;
        @BindView(R.id.initHashText)    TextView  initHashText;
        @BindView(R.id.statusImage)     ImageView statusImage;

        @BindDrawable(R.drawable.dot)   Drawable  dot;

        ContractViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnContractClickListener {
        void onContractClick(DBContract contract);
        void onContractLongClick(DBContract contract);
    }
}
