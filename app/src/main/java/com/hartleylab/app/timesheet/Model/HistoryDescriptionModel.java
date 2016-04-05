package com.hartleylab.app.timesheet.Model;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class HistoryDescriptionModel extends  BaseModel {

    @SerializedName("data")
    private List<HistoryDescription> historyDescriptionList = new ArrayList<>();

    public List<HistoryDescription> getHistoryDescriptionList() {
        return historyDescriptionList;
    }
}
