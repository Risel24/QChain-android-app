package io.rsl.pragma.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class BaseViewHolder<T extends BaseModel> extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(T object, int position);
}