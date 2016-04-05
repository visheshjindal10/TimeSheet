package com.hartleylab.app.timesheet.interfaces;

import com.android.volley.VolleyError;

public interface ApiResponseInterface {

    void onResponse(String response);

    void onError(VolleyError error);
}
