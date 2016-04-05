package com.hartleylab.app.timesheet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.hartleylab.app.timesheet.AppController;
import com.hartleylab.app.timesheet.Constants;
import com.hartleylab.app.timesheet.Model.BaseModel;
import com.hartleylab.app.timesheet.Model.EmpID;
import com.hartleylab.app.timesheet.Model.EmpIdModel;
import com.hartleylab.app.timesheet.Model.Employee;
import com.hartleylab.app.timesheet.R;
import com.hartleylab.app.timesheet.utilities.CommonUtils;
import com.hartleylab.app.timesheet.utilities.NetworkUtil;
import com.hartleylab.app.timesheet.utilities.SharedPreferenceManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SignUpFormActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatEditText etFullName, etEmailID, etPhoneNumber, etPassword, etConfirmPassword;
    private TextInputLayout tvFullName, tvEmailId, tvPhoneNumber, tvEmpId,
            tvPassword, tvConfirmPassword;
    private AppCompatAutoCompleteTextView actcEmployeeID;
    private ArrayList<String> empIDList = new ArrayList<>();
    private AppController appController;
    private List<TextInputLayout> fields = new ArrayList<>();
    private CoordinatorLayout clContainer;
    private SharedPreferenceManager sharedPreferenceManager;
    private ProgressBar progressBar;
    private LinearLayout llSignUpContainer;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_form);
        appController = AppController.getInstance();
        sharedPreferenceManager = new SharedPreferenceManager(SignUpFormActivity.this);
        setUpToolbar();
        initTextInputLayouts();
        initViews();
        fetchEmpIDs();
        fillArray();
    }

    /**
     * Function to create array of Text input layout fields
     */
    private void fillArray() {
        fields.add(tvFullName);
        fields.add(tvEmailId);
        fields.add(tvEmpId);
        fields.add(tvPhoneNumber);
        fields.add(tvPassword);
        fields.add(tvConfirmPassword);
    }

    /**
     * Function to setup Toolbar
     */
    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.tv_signup);
        setSupportActionBar(toolbar);
    }


    /**
     * Function on Submit button Click
     */
    private void onSubmitClick() {

        String fullName = etFullName.getText().toString();
        String email = etEmailID.getText().toString();
        String employeeID = actcEmployeeID.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        boolean isEmpty = false;
        for (int i = 0; i < fields.size(); i++) {
            if (TextUtils.isEmpty(fields.get(i).getEditText().getText())) {
                fields.get(i).setError(fields.get(i).getHint()
                        + " " + getString(R.string.err_field_empty));
                isEmpty = true;
            }
        }

        if (isEmpty) {
            return;
        }  else if (!CommonUtils.isValidEmail(email)) {
            tvEmailId.setError(getString(R.string.err_email));
        } else if (phoneNumber.length() < 10) {
            tvPhoneNumber.setError(getString(R.string.err_phone_not_10_digits));
        } else if (!confirmPassword.equals(password)) {
            tvConfirmPassword.setError(getString(R.string.err_password));
        } else {
            Employee employee = createEmployeeObject(fullName, email, employeeID, phoneNumber, password);
            saveOnServer(employee);
        }
    }

    @NonNull
    private Employee createEmployeeObject(String fullName, String email, String employeeID, String phoneNumber, String password) {
        Employee employee = new Employee();
        employee.setFullName(fullName);
        employee.setEmail(email);
        employee.setEmpId(employeeID);
        employee.setPhoneNumber(phoneNumber);
        employee.setPassword(password);
        return employee;
    }

    /**
     * Function to initiate Views
     */
    private void initViews() {

        etFullName = (AppCompatEditText) findViewById(R.id.etFullName);
        etFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvFullName.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etEmailID = (AppCompatEditText) findViewById(R.id.etEmailID);
        etEmailID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvEmailId.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPhoneNumber = (AppCompatEditText) findViewById(R.id.etPhoneNumber);
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvPhoneNumber.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPassword = (AppCompatEditText) findViewById(R.id.etPassword);
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvPassword.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etConfirmPassword = (AppCompatEditText) findViewById(R.id.etConfirmPassword);
        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvConfirmPassword.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        actcEmployeeID = (AppCompatAutoCompleteTextView) findViewById(R.id.etEmpId);
        actcEmployeeID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvEmpId.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ArrayAdapter<String> adpt = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, empIDList);
        actcEmployeeID.setAdapter(adpt);

        AppCompatButton btnSubmitDetail = (AppCompatButton) findViewById(R.id.btnSubmitDetails);
        btnSubmitDetail.setOnClickListener(this);

        etConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    CommonUtils.hideKeyboard(SignUpFormActivity.this, etConfirmPassword);
                    onSubmitClick();
                }
                return false;
            }
        });

    }

    /**
     * Function to initialize Text Input layouts
     */
    private void initTextInputLayouts() {
        progressBar = (ProgressBar) findViewById(R.id.pb);
        clContainer = (CoordinatorLayout) findViewById(R.id.clContainer);
        llSignUpContainer = (LinearLayout) findViewById(R.id.llSignUpContainer);
        tvFullName = (TextInputLayout) findViewById(R.id.tvFullName);
        tvEmailId = (TextInputLayout) findViewById(R.id.tvEmailId);
        tvEmpId = (TextInputLayout) findViewById(R.id.tilEmpId);
        tvPhoneNumber = (TextInputLayout) findViewById(R.id.tvPhoneNumber);
        tvPassword = (TextInputLayout) findViewById(R.id.tvPassword);
        tvConfirmPassword = (TextInputLayout) findViewById(R.id.tvConfirmPassword);
    }

    /**
     * Function to store data in Shared preference
     *@param employee Employee
     */
    private void saveItSharedPreference(Employee employee) {
        sharedPreferenceManager.putObject(Constants.KEY_EMPLOYEE, employee);
        goToDashboard();
    }

    private void goToDashboard() {
        Intent i = new Intent(getApplicationContext(), DashBoardActivity.class);
        startActivity(i);
    }

    /**
     * Function for api call call for registration
     *
     * @param employee Employee
     */
    private void saveOnServer(final Employee employee) {

        if (NetworkUtil.checkNetwork(SignUpFormActivity.this, true)) {
            llSignUpContainer.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            HashMap<String, String> params = new HashMap<>();
            params.put(getString(R.string.tv_key_fullName), employee.getFullName());
            params.put(getString(R.string.tv_key_email), employee.getEmail());
            params.put(getString(R.string.tv_key_empID), employee.getEmpId());
            params.put(getString(R.string.tv_key_phoneNumber), employee.getPhoneNumber());
            params.put(getString(R.string.tv_key_password), employee.getPassword());

            JSONObject jsonObject = new JSONObject(params);

            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST,
                    Constants.BASE_URL + Constants.POST_ADD_EMPLOYEE,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    llSignUpContainer.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Gson gson = new Gson();
                    BaseModel registrationResponse =
                            gson.fromJson(response.toString(), BaseModel.class);
                    if (registrationResponse != null){
                        onRegistrationResponseReceived(registrationResponse, employee);
                    }else {
                        Snackbar.make(clContainer, R.string.err_server_not_reachable, Snackbar
                                .LENGTH_LONG).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    llSignUpContainer.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(clContainer, R.string.err_server, Snackbar
                            .LENGTH_LONG).show();
                }
            });
            appController.addToRequestQueue(stringRequest);
        }else {
            llSignUpContainer.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            Snackbar.make(clContainer, R.string.err_network_availablity, Snackbar
                    .LENGTH_LONG).show();
        }
    }


    /**
     * Function call on registration api response
     * @param registrationResponse Response
     * @param employee Employee
     */
    private void onRegistrationResponseReceived(BaseModel registrationResponse, Employee employee) {
        if (registrationResponse.isSuccess()){
            sharedPreferenceManager.setBooleanValue(getString(R.string
                    .key_isLogin),registrationResponse.isSuccess());
            saveItSharedPreference(employee);
        }else if (registrationResponse.getStatus() == Constants.USER_NOT_FOUND){
            Snackbar.make(clContainer, R.string.err_user_already_exist, Snackbar
                    .LENGTH_LONG).setAction(R.string.btn_login, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SignUpFormActivity.this,
                            SplashScreenActivity.class));
                }
            }).show();
        }else {
            Snackbar.make(clContainer, R.string.err_server_bad_request, Snackbar
                    .LENGTH_LONG).show();
        }
    }

    /**
     * Function to Fetch Employee IDs
     */
    private void fetchEmpIDs() {
        if (NetworkUtil.checkNetwork(SignUpFormActivity.this, true)) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.BASE_URL +
                    Constants.GET_EMP_ID,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            llSignUpContainer.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            Gson gson = new Gson();
                            onEmpListResponseReceived(response, gson);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    llSignUpContainer.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(clContainer, R.string.err_server, Snackbar
                            .LENGTH_LONG).show();
                }
            });
            appController.addToRequestQueue(stringRequest);
        } else {
            llSignUpContainer.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            Snackbar.make(clContainer, R.string.err_network_availablity, Snackbar
                    .LENGTH_LONG).show();
        }
    }

    /**
     * Function to handle response
     *
     * @param response String
     * @param gson     Gson
     */
    private void onEmpListResponseReceived(String response, Gson gson) {
        EmpIdModel empIdModel = gson.fromJson(response, EmpIdModel.class);
        if (empIdModel != null) {
            if (empIdModel.isSuccess()) {
                List<EmpID> empIDs = empIdModel.getEmpIDList();
                if (!empIDs.isEmpty()) {
                    for (int i = 0; i < empIDs.size(); i++) {
                        empIDList.add(empIDs.get(i).getEmpID());
                    }
                } else {
                    Snackbar.make(clContainer, R.string.err_server,
                            Snackbar.LENGTH_LONG).show();
                }
            } else if (empIdModel.getStatus() == Constants.USER_NOT_FOUND) {
                Snackbar.make(clContainer, empIdModel.getMessage(),
                        Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(clContainer, R.string.err_server_not_responding, Snackbar
                        .LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(clContainer, R.string.err_server_not_reachable, Snackbar
                    .LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSubmitDetails) {
            onSubmitClick();
        }
    }

}

