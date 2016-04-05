package com.hartleylab.app.timesheet.Model;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EmpIdModel extends BaseModel {

    @SerializedName("data")
    private List<EmpID> empIDList = new ArrayList<>();

    public List<EmpID> getEmpIDList() {
        return empIDList;
    }
}
