package io.rsl.pragma.screens.poll.models.core;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public class CorePollModel implements Serializable{

    private String title;
    private List<String> voteOptions;
    private boolean majority;
    private int voteCount;
    private Calendar startDate;
    private Calendar endDate;

    public CorePollModel(String title, List<String> voteOptions, boolean majority, int voteCount, Calendar startDate, Calendar endDate) {
        this.title = title;
        this.voteOptions = voteOptions;
        this.majority = majority;
        this.voteCount = voteCount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getVoteOptions() {
        return voteOptions;
    }

    public boolean isMajority() {
        return majority;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public String serializeToJson(Gson gson) {
        return gson.toJson(this, CorePollModel.class);
    }
}
