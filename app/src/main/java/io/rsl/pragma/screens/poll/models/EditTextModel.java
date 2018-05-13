package io.rsl.pragma.screens.poll.models;

import io.rsl.pragma.utils.BaseModel;

public class EditTextModel implements BaseModel {

    private String voteOption = "";

    public EditTextModel(){}

    public EditTextModel(String voteOption) {
        this.voteOption = voteOption;
    }

    public String getVoteOption() {
        return voteOption;
    }

    public void setVoteOption(String voteOption) {
        this.voteOption = voteOption;
    }

    @Override
    public int getViewType() {
        return Constants.ViewType.VOTE_EDITTEXT;
    }
}
