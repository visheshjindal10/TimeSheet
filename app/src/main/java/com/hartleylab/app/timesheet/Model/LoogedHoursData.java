package com.hartleylab.app.timesheet.Model;


public class LoogedHoursData {
    private String ProjectName;
    private String Description;
    private String Date;
    private String LoggedHours;


    /*public LoogedHoursData() {
    }*/

    public LoogedHoursData(String projectName, String description, String Date, String LoggedHours) {
        this.ProjectName = projectName;
        this.Description=description;
        this.Date=Date;
        this.LoggedHours=LoggedHours;
    }



    public String getProjectName() {
        return ProjectName;
    }

    public void setProjectName(String projectName) {
        ProjectName = projectName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDate() { return Date; }

    public String getLoggedHours() {  return LoggedHours; }

    public void setDate(String Date) { Date=  Date; }

    public void setLoggedHours(String loggedHours) { LoggedHours = loggedHours; }
}

