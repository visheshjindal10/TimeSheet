package com.hartleylab.app.timesheet.Model;


import com.google.gson.annotations.SerializedName;

public class BaseModel {

    private int status;
    @SerializedName("success")
    private boolean isSuccess;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
