package io.rsl.pragma.screens.poll.models;

import io.rsl.pragma.utils.BaseModel;

public class TitleModel implements BaseModel {

    private String title = "";

    public TitleModel(){ }

    public TitleModel(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int getViewType() {
        return Constants.ViewType.TITLE;
    }
}
