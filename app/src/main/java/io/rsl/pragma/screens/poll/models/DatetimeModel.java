package io.rsl.pragma.screens.poll.models;

import java.util.Calendar;

import io.rsl.pragma.utils.BaseModel;

public class DatetimeModel implements BaseModel {

    private Calendar startDate;
    private Calendar endDate;

    public DatetimeModel(){}

    public DatetimeModel(Calendar startDate, Calendar endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    @Override
    public int getViewType() {
        return Constants.ViewType.DATETIME;
    }
}
