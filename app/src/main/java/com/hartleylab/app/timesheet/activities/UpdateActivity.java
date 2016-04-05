package com.hartleylab.app.timesheet.activities;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.hartleylab.app.timesheet.AppController;
import com.hartleylab.app.timesheet.Constants;
import com.hartleylab.app.timesheet.Model.BaseModel;
import com.hartleylab.app.timesheet.Model.Employee;
import com.hartleylab.app.timesheet.Model.HistoryDescription;
import com.hartleylab.app.timesheet.Model.Project;
import com.hartleylab.app.timesheet.Model.ProjectListModel;
import com.hartleylab.app.timesheet.R;
import com.hartleylab.app.timesheet.utilities.DateUtil;
import com.hartleylab.app.timesheet.utilities.NetworkUtil;
import com.hartleylab.app.timesheet.utilities.SharedPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private AppController appController;
    private AppCompatSpinner ProjectName;
    private AppCompatSpinner spDate;
    private AppCompatEditText etLoggedHours, etTicketNo, etDescription;

    private Employee employee;
    private CoordinatorLayout clContainer;
    private ProgressBar progressBar;
    private NestedScrollView llWorkContainer;

    private TextInputLayout tvWorkedHours, tvTicketNumber, tvDescription;

    private List<Map<String, String>> dates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_hours);
        setUpToolbar();
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(this);
        employee = sharedPreferenceManager.getObject(Constants.KEY_EMPLOYEE, Employee.class);
        initViews();
        getProjectsName();
        initiDateSpinner();

        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> data = new HashMap<String, String>(2);
                data = (Map<String, String>) ProjectName.getSelectedItem();

                Map<String, String> selectedDate = new HashMap<String, String>(2);
                selectedDate = (Map<String, String>) spDate.getSelectedItem();

                String loggedHours = etLoggedHours.getText().toString();
                String ticketNo = etTicketNo.getText().toString();
                String description = etDescription.getText().toString();

                if (TextUtils.isEmpty(loggedHours)) {
                    tvWorkedHours.setError("Enter Hours First!!");
                    return;
                }
                if (TextUtils.isEmpty(ticketNo)) {
                    tvTicketNumber.setError("Enter TicketNo!!");
                    return;
                }
                if (TextUtils.isEmpty(description)) {
                    tvDescription.setError("Enter Description!!");
                    return;
                }

                HistoryDescription logDescription = new HistoryDescription();
                logDescription.setDescription(description);
                logDescription.setTicketNo(ticketNo);
                logDescription.setLoggedHours(loggedHours);
                logDescription.setDate(DateUtil.getFormattedTime(selectedDate.get(getString(R
                                .string.key_date)), DateUtil.LOGGED_DATE_FOR_LIST,
                        DateUtil.FORMAT_DATE_FOR_API));
                logDescription.setProjectName(data.get(getString(R.string.key_project_name)));
                postDescription(logDescription);
            }
        });
    }

    /**
     * Function to setup toolbar
     */
    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_log_work);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Function to initialize Date Spinner
     */
    private void initiDateSpinner() {
        spDate = (AppCompatSpinner) findViewById(R.id.spDate);
        spDate.setOnItemSelectedListener(this);
        Calendar calendar = Calendar.getInstance();
        Map<String, String> today = new HashMap<>(2);
        today.put(getString(R.string.key_day), getString(R.string.tv_today));
        today.put(getString(R.string.key_date),
                DateUtil.getFormattedDateFromTimeStamp(calendar.getTime(), DateUtil.LOGGED_DATE_FOR_LIST));
        dates.add(today);

        Map<String, String> yesterday = new HashMap<>(2);
        calendar.add(Calendar.DATE, -1);
        yesterday.put(getString(R.string.key_day), getString(R.string.tv_yesterday));
        yesterday.put(getString(R.string.key_date),
                DateUtil.getFormattedDateFromTimeStamp(calendar.getTime(), DateUtil.LOGGED_DATE_FOR_LIST));
        dates.add(yesterday);


        SimpleAdapter adapter = new SimpleAdapter(UpdateActivity.this, dates, android.R.layout
                .two_line_list_item,
                new String[]{getString(R.string.key_day), getString(R.string.key_date)},
                new int[]{android.R.id.text1,
                        android.R.id.text2});
        adapter.setDropDownViewResource(android.R.layout.two_line_list_item);

        spDate.setAdapter(adapter);
    }

    /**
     * Function to initiate views
     */
    private void initViews() {
        appController = AppController.getInstance();
        etLoggedHours = (AppCompatEditText) findViewById(R.id.etLoggedHours);
        etTicketNo = (AppCompatEditText) findViewById(R.id.etTicketNo);
        etDescription = (AppCompatEditText) findViewById(R.id.etDescription);
        ProjectName = (AppCompatSpinner) findViewById(R.id.spProjectName);
        clContainer = (CoordinatorLayout) findViewById(R.id.clContainer);
        progressBar = (ProgressBar) findViewById(R.id.pb);
        tvDescription = (TextInputLayout) findViewById(R.id.tvDescription);
        tvTicketNumber = (TextInputLayout) findViewById(R.id.tvTicketNumber);
        tvWorkedHours = (TextInputLayout) findViewById(R.id.tvLoggedHours);
        llWorkContainer = (NestedScrollView) findViewById(R.id.llWorkContainer);

        etLoggedHours.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvWorkedHours.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        etTicketNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvTicketNumber.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvDescription.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Function call to fetch Project Names
     */
    private void getProjectsName() {
        if (NetworkUtil.checkNetwork(UpdateActivity.this, false)) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.BASE_URL +
                    Constants.GET_PROJECT_LIST, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    progressBar.setVisibility(View.GONE);
                    llWorkContainer.setVisibility(View.VISIBLE);
                    Gson gson = new Gson();
                    ProjectListModel projectListModel = gson.fromJson(response,
                            ProjectListModel.class);
                    if (projectListModel == null) {
                        Snackbar.make(clContainer, R.string.err_server_not_responding,
                                Snackbar.LENGTH_LONG).show();
                    } else {
                        if (projectListModel.getStatus() == Constants.USER_NOT_FOUND) {
                            Snackbar.make(clContainer, projectListModel.getMessage(),
                                    Snackbar.LENGTH_LONG).show();
                        } else if (projectListModel.getStatus() == Constants.SUCCESS &&
                                !projectListModel.getProjectList().isEmpty()) {
                            initProjectSpinner(projectListModel.getProjectList());
                        } else {
                            Snackbar.make(clContainer, R.string.err_server_bad_request,
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    llWorkContainer.setVisibility(View.VISIBLE);
                    Snackbar.make(clContainer, R.string.err_server_bad_request, Snackbar.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put(Constants.API_KEY_NAME, Constants.API_KEY);
                    return map;
                }
            };
            appController.addToRequestQueue(stringRequest);
        } else {
            progressBar.setVisibility(View.GONE);
            llWorkContainer.setVisibility(View.VISIBLE);
            Snackbar.make(clContainer, R.string.err_network_availablity, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Function to initiate Project Spinner
     *
     * @param projectList List
     */
    private void initProjectSpinner(ArrayList<Project> projectList) {

        List<Map<String, String>> projects = new ArrayList<>();

        for (int i = 0; i < projectList.size(); i++) {

            Map<String, String> data = new HashMap<>(2);
            data.put(getString(R.string.key_project_name), projectList.get(i).getProjectName());
            data.put(getString(R.string.key_project_type), projectList.get(i).getProjectType());
            projects.add(data);
        }
        SimpleAdapter adapter = new SimpleAdapter(UpdateActivity.this, projects, android.R.layout.two_line_list_item,
                new String[]{getString(R.string.key_project_name), getString(R.string.key_project_type)},
                new int[]{android.R.id.text1,
                        android.R.id.text2});
        adapter.setDropDownViewResource(android.R.layout.two_line_list_item);

        ProjectName.setAdapter(adapter);

    }


    private void goToDashboard() {
        finish();
    }


    /**
     * Function to make API call for Update
     *
     * @param historyDescription Description
     */
    private void postDescription(HistoryDescription historyDescription) {
        if (NetworkUtil.checkNetwork(UpdateActivity.this, true)) {
            llWorkContainer.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(getString(R.string.sp_key_projectName), historyDescription.getProjectName());
                jsonObject.put(getString(R.string.et_key_loggedHours), historyDescription.getLoggedHours());
                jsonObject.put(getString(R.string.et_key_ticketNo), historyDescription.getTicketNo());
                jsonObject.put(getString(R.string.et_key_description), historyDescription.getDescription());
                jsonObject.put(getString(R.string.key_emp_id), employee.getEmpId());
                jsonObject.put(getString(R.string.sp_key_date1), historyDescription.getDate());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST,
                    Constants.BASE_URL + Constants.POST_UPDATES, jsonObject, new Response
                    .Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    BaseModel responseModel = gson.fromJson(response.toString(), BaseModel.class);
                    llWorkContainer.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    if (responseModel == null) {
                        Snackbar.make(clContainer, R.string.err_server_not_responding,
                                Snackbar.LENGTH_LONG).show();

                    } else {
                        if (responseModel.getStatus() == Constants.SUCCESS) {
                            goToDashboard();
                        } else if (responseModel.getStatus() == Constants.USER_NOT_FOUND) {
                            Snackbar.make(clContainer, responseModel.getMessage(),
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(clContainer, R.string.err_server,
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    llWorkContainer.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(clContainer, R.string.err_server_bad_request, Snackbar.LENGTH_LONG).show();
                }
            });
            appController.addToRequestQueue(stringRequest);
        } else {
            Snackbar.make(clContainer, R.string.err_network_availablity, Snackbar.LENGTH_LONG).show();
        }
    }
}