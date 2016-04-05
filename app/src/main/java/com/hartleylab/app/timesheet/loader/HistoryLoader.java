package com.hartleylab.app.timesheet.loader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.hartleylab.app.timesheet.AppController;
import com.hartleylab.app.timesheet.Constants;
import com.hartleylab.app.timesheet.Model.HistoryDescription;
import com.hartleylab.app.timesheet.Model.HistoryDescriptionModel;
import com.hartleylab.app.timesheet.R;
import com.hartleylab.app.timesheet.utilities.CommonUtils;
import com.hartleylab.app.timesheet.utilities.NetworkUtil;
import com.hartleylab.app.timesheet.utilities.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryLoader extends Loader<List<HistoryDescription>> {

    private List<HistoryDescription> historyDescriptionList;
    public static final String ACTION = "com.loader.FORCE";
    public static final String TAG = "historyLoader";
    private RequestQueue queue;
    private SharedPreferenceManager sharedPreferenceManager;

    public HistoryLoader(Context context) {
        super(context);
        AppController appController = AppController.getInstance();
        sharedPreferenceManager = new SharedPreferenceManager(getContext());
        queue = appController.getRequestQueue();
    }

    @Override
    protected void onStartLoading() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter filter = new IntentFilter(ACTION);
        manager.registerReceiver(broadcastReceiver, filter);
        if (historyDescriptionList == null) {
            forceLoad();
        } else {
            super.deliverResult(historyDescriptionList);
        }
    }

    @Override
    protected void onForceLoad() {
        queue.cancelAll(TAG);
        fetchDescription();
    }

    /**
     * Function call to request for the list of details
     */
    private void fetchDescription() {
        final String empID = sharedPreferenceManager.getEmployeeID();
        if (NetworkUtil.checkNetwork(getContext(), true)) {
            Log.d(TAG, "Loading new data");
            if (TextUtils.isEmpty(empID)) {
                Toast.makeText(getContext(), R.string.err_auth_na_empId, Toast.LENGTH_SHORT).show();
            } else {
                String url = Constants.BASE_URL + Constants.GET_HISTORY + Constants.URL_BACKSLASH + empID;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Gson gson = new Gson();
                                HistoryDescriptionModel historyDescriptionModel =
                                        gson.fromJson(response, HistoryDescriptionModel.class);
                                HistoryLoader.this.onResponse(historyDescriptionModel);
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (historyDescriptionList != null){
                            deliverResult(historyDescriptionList);
                        }else {
                            deliverResult(new ArrayList<HistoryDescription>());
                            Toast.makeText(getContext(), getContext().getString(R.string
                                    .err_server_not_reachable), Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put(Constants.HEADER_CONTENT_TYPE,Constants.APPLICATION_JSON);
                        map.put(Constants.API_KEY_NAME,
                                CommonUtils.getKey(sharedPreferenceManager.getEmployeeID()));
                        return map;
                    }
                };
                stringRequest.setTag(TAG);
                queue.add(stringRequest);
            }
        }else {
            deliverResult(historyDescriptionList);
        }
    }

    /**
     * Function call on Response Received
     *
     * @param historyDescriptionModel HistoryDescriptionModel
     */
    private void onResponse(HistoryDescriptionModel historyDescriptionModel) {
        if (historyDescriptionModel != null) {

            if (historyDescriptionModel.getStatus() == Constants.USER_NOT_FOUND) {
                deliverResult(new ArrayList<HistoryDescription>());
                Toast.makeText(getContext(), historyDescriptionModel.getMessage(), Toast.LENGTH_LONG).show();
            } else if (historyDescriptionModel.getStatus() == Constants.SUCCESS) {

                List<HistoryDescription> tempList = historyDescriptionModel
                        .getHistoryDescriptionList();
                if (tempList.isEmpty()) {
                    if (historyDescriptionList !=null){
                        deliverResult(historyDescriptionList);
                    }else {
                        deliverResult(new ArrayList<HistoryDescription>());
                    }
                } else {
                    deliverResult(tempList);
                }
            } else {
                deliverResult(new ArrayList<HistoryDescription>());
                Toast.makeText(getContext(), historyDescriptionModel.getMessage(), Toast.LENGTH_LONG).show();
            }

        } else {
            if (historyDescriptionList !=null){
                deliverResult(historyDescriptionList);
            }else {
                deliverResult(new ArrayList<HistoryDescription>());
            }
            Toast.makeText(getContext(), getContext().getString(R.string
                    .err_server), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void deliverResult(List<HistoryDescription> data) {
        historyDescriptionList = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onReset() {
        //Cancel Request
        queue.cancelAll(TAG);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            forceLoad();
        }
    };
}
