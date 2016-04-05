package com.hartleylab.app.timesheet.Model;


import android.os.Parcel;
import android.os.Parcelable;

public class HistoryDescription implements Parcelable {


    String id,projectName,loggedDate,loggedHours,description,ticketNo,timeStamp;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDate() {
        return loggedDate;
    }

    public void setDate(String date) {
        this.loggedDate = date;
    }

    public String getLoggedHours() {
        return loggedHours;
    }

    public void setLoggedHours(String loggedHours) {
        this.loggedHours = loggedHours;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getLoggedDate() {
        return loggedDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.projectName);
        dest.writeString(this.loggedDate);
        dest.writeString(this.loggedHours);
        dest.writeString(this.description);
        dest.writeString(this.ticketNo);
        dest.writeString(this.timeStamp);
    }

    public HistoryDescription() {
    }

    protected HistoryDescription(Parcel in) {
        this.id = in.readString();
        this.projectName = in.readString();
        this.loggedDate = in.readString();
        this.loggedHours = in.readString();
        this.description = in.readString();
        this.ticketNo = in.readString();
        this.timeStamp = in.readString();
    }

    public static final Parcelable.Creator<HistoryDescription> CREATOR = new Parcelable.Creator<HistoryDescription>() {
        @Override
        public HistoryDescription createFromParcel(Parcel source) {
            return new HistoryDescription(source);
        }

        @Override
        public HistoryDescription[] newArray(int size) {
            return new HistoryDescription[size];
        }
    };
}
