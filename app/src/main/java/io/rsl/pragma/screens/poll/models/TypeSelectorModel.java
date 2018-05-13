package io.rsl.pragma.screens.poll.models;

import io.rsl.pragma.utils.BaseModel;

public class TypeSelectorModel implements BaseModel {

    private boolean isMajority = true;
    private int     voteCount = 1;

    public TypeSelectorModel(){}

    public TypeSelectorModel(boolean isMajority, int voteCount) {
        this.isMajority = isMajority;
        this.voteCount = voteCount;
    }

    public boolean isMajority() {
        return isMajority;
    }

    public void setMajority(boolean majority) {
        isMajority = majority;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    @Override
    public int getViewType() {
        return Constants.ViewType.TYPE_SELECTOR;
    }
}
