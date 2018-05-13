package io.rsl.pragma.screens.contracts.create;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rsl.pragma.R;
import io.rsl.pragma.screens.poll.PollCreateFragment;

public class ContractSelectRecyclerAdapter extends RecyclerView.Adapter<ContractSelectRecyclerAdapter.ViewHolder> {

    private List<ContractDescription> contractDescriptions = new ArrayList<>();
    private OnContractSelectListener listener;

    public ContractSelectRecyclerAdapter() {
        contractDescriptions.add(new ContractDescription(PollCreateFragment.class.getName(), "Vote", "The problem with the UserRepository implementation above is that after fetching the data, it does not keep it anywhere. If the user leaves the UserProfileFragment and comes back to it, the app re-fetches the data. This is bad for two reasons: it wastes valuable network bandwidth and forces the user to wait for the new query to complete. To address this, we will add a new data source to our UserRepository which will cache the User objects in memory."));
        contractDescriptions.add(new ContractDescription(PollCreateFragment.class.getName(), "Vote 2 with same activity", "The problem with the UserRepository implementation above is that after fetching the data, it does not keep it anywhere. If the user leaves the UserProfileFragment and comes back to it, the app re-fetches the data. This is bad for two reasons: it wastes valuable network bandwidth and forces the user to wait for the new query to complete. To address this, we will add a new data source to our UserRepository which will cache the User objects in memory."));
        contractDescriptions.add(new ContractDescription(PollCreateFragment.class.getName(), "Vote 3 text for text and text again", "The problem with the UserRepository implementation above is that after fetching the data, it does not keep it anywhere. If the user leaves the UserProfileFragment and comes back to it, the app re-fetches the data. This is bad for two reasons: it wastes valuable network bandwidth and forces the user to wait for the new query to complete. To address this, we will add a new data source to our UserRepository which will cache the User objects in memory."));
    }

    public void setOnContractSelectListener(OnContractSelectListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_description, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ContractDescription contractDescription = contractDescriptions.get(position);
        holder.titleText.setText(contractDescription.getTitle());
        holder.bodyText.setText(contractDescription.getDescription());
        holder.itemView.setOnClickListener(v -> listener.onContractSelect(contractDescription.getFragmentName()));
    }

    @Override
    public int getItemCount() {
        return contractDescriptions.size();
    }

    public interface OnContractSelectListener {
        void onContractSelect(String fragmentName);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.titleText) TextView titleText;
        @BindView(R.id.bodyText) TextView bodyText;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
