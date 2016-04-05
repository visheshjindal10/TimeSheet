package com.hartleylab.app.timesheet.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.hartleylab.app.timesheet.AppController;
import com.hartleylab.app.timesheet.Constants;
import com.hartleylab.app.timesheet.Model.BaseModel;
import com.hartleylab.app.timesheet.Model.Employee;
import com.hartleylab.app.timesheet.R;
import com.hartleylab.app.timesheet.utilities.CommonUtils;
import com.hartleylab.app.timesheet.utilities.NetworkUtil;
import com.hartleylab.app.timesheet.utilities.SharedPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout llContainer,llLoginContainer;
    private ProgressBar progressBar;
    public static final int TIME_OUT = 1000;
    private TextInputLayout tilEmplID, tilPassword;
    private View loginView;
    private EditText etPasswordLogin, etEmpId;
    private AppController appController;
    private SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fullScreen();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        appController = AppController.getInstance();
        initViews();
        inflateLoginView();
        checkIfLoggedIn();
    }

    /**
     * Function to check if user is logged in or not
     */
    private void checkIfLoggedIn() {
        sharedPreferenceManager = new SharedPreferenceManager(this);
        boolean isLogin = sharedPreferenceManager.getBooleanValue(getString(R.string.key_isLogin));

        if (isLogin) {
            onAlreadyLoggedIn();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    addViewToContainer();
                }
            }, TIME_OUT);
        }
    }

    /**
     * Function if already logged in
     */
    private void onAlreadyLoggedIn() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, DashBoardActivity.class));
                finish();
            }
        }, TIME_OUT);
    }


    /**
     * Function to inflate login view
     */
    private void inflateLoginView() {
        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        loginView = inflater.inflate(R.layout.view_login_form, null);
    }

    /**
     * Function to Initialize views
     */
    private void initViews() {
        llContainer = (LinearLayout) findViewById(R.id.llContainer);

    }

    /**
     * Function to make activity full screen
     */
    private void fullScreen() {
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnLogin) {
            validateAndLogin();
        } else if (v.getId() == R.id.tvSignUpText) {
            onSignUpClick();
        }

    }

    /**
     * Function on Click of SignUp Button
     */
    private void onSignUpClick() {
        llContainer.removeView(loginView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, SignUpFormActivity.class));
                finish();
            }
        }, TIME_OUT);
    }

    /**
     * Function to add view and initialize login form
     */
    private void addViewToContainer() {
        llContainer.addView(loginView);
        AppCompatButton tvSignUpText = (AppCompatButton) findViewById(R.id.tvSignUpText);
        tvSignUpText.setOnClickListener(this);
        AppCompatButton btnLogin = (AppCompatButton) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        tilEmplID = (TextInputLayout) findViewById(R.id.tilEmpID);
        passwordFieldController();
        empIdController();
    }

    /**
     * Function to initialize and remove error from Employee Id field
     */
    private void empIdController() {
        etEmpId = (EditText) findViewById(R.id.etEmpID);
        etEmpId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Function to initialize and remove error from Password field
     */
    private void passwordFieldController() {
        llLoginContainer = (LinearLayout) findViewById(R.id.llLoginContainer);
        progressBar = (ProgressBar) findViewById(R.id.pb);
        tilPassword = (TextInputLayout) findViewById(R.id.tilPassword);
        etPasswordLogin = (EditText) findViewById(R.id.etPasswordLogin);
        etPasswordLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        goLoginClick();
    }

    /**
     * Function to handle on keyboard go click
     */
    private void goLoginClick() {
        etPasswordLogin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    CommonUtils.hideKeyboard(SplashScreenActivity.this, etPasswordLogin);
                    validateAndLogin();
                }
                return false;
            }
        });
    }

    /**
     * Function to validate and login
     */
    private void validateAndLogin() {
        String employeeId = etEmpId.getText().toString();
        String password = etPasswordLogin.getText().toString();

        if (TextUtils.isEmpty(employeeId)) {
            tilEmplID.setError(getString(R.string.err_empId_empty));
        } else if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.err_password_empty));
        } else {
            tryLogin(employeeId, password);
        }
    }

    /**
     * Function to Try Login from API
     *
     * @param employeeId String
     * @param password   String
     */
    private void tryLogin(final String employeeId, final String password) {

        if (NetworkUtil.checkNetwork(SplashScreenActivity.this, false)){
            llLoginContainer.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(getString(R.string.key_empId), employeeId);
                jsonObject.put(getString(R.string.key_password), password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = Constants.BASE_URL + Constants.POST_LOGIN;
            JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressBar.setVisibility(View.GONE);
                            llLoginContainer.setVisibility(View.VISIBLE);
                            Gson gson = new Gson();
                            BaseModel loginResponse = gson.fromJson(response.toString(), BaseModel.class);
                            if (loginResponse != null){
                                onResponseRecived(loginResponse, employeeId, password);
                            }else {
                                Snackbar.make(llContainer, R.string.err_server ,Snackbar
                                        .LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    llLoginContainer.setVisibility(View.VISIBLE);
                    Snackbar.make(llContainer, R.string.err_server_not_reachable ,Snackbar.LENGTH_SHORT).show();
                }
            });

            appController.addToRequestQueue(loginRequest);
        }else {
            progressBar.setVisibility(View.GONE);
            llLoginContainer.setVisibility(View.VISIBLE);
            Snackbar.make(llContainer, R.string.err_network_availablity,Snackbar
                    .LENGTH_LONG).show();
        }
    }

    /**
     * Function on successfully response received
     * @param loginResponse BaseModel
     * @param empID  String
     * @param password  String
     */
    private void onResponseRecived(BaseModel loginResponse, String empID, String password) {
        if (loginResponse.isSuccess()) {
            sharedPreferenceManager.setBooleanValue(getString(R.string.key_isLogin),
                    loginResponse.isSuccess());
            Employee employee = new Employee();
            employee.setEmpId(empID);
            employee.setPassword(password);
            sharedPreferenceManager.putObject(Constants.KEY_EMPLOYEE,employee);
            llContainer.removeView(loginView);
            onAlreadyLoggedIn();

        } else if (Constants.USER_NOT_FOUND == loginResponse.getStatus()) {
            onUserNotFoundError(loginResponse);
        } else if (Constants.INVALID_USER == loginResponse.getStatus()) {
            tilEmplID.setError(loginResponse.getMessage());
            tilPassword.setError(loginResponse.getMessage());
        }else {
            Snackbar.make(llContainer, R.string.err_server_not_reachable,Snackbar
                    .LENGTH_LONG).show();
        }
    }

    /**
     * Function on user not found
     *
     * @param loginResponse Base Model
     */
    private void onUserNotFoundError(BaseModel loginResponse) {
        Snackbar.make(llContainer, loginResponse.getMessage(), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.sign_up), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSignUpClick();
                    }
                }).show();
    }

}
