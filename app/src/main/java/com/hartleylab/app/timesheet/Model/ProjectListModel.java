package com.hartleylab.app.timesheet.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class ProjectListModel extends BaseModel {

    @SerializedName("data")
    private ArrayList<Project> projectList = new ArrayList<>();

    public ArrayList<Project> getProjectList() {
        return projectList;
    }
}
