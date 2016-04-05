package com.hartleylab.app.timesheet;

/**
 * Created by hartleylab on 3/3/16.
 */
public class Constants {

    public static final String KEY_EMPLOYEE = "employee";

    //Base URL for Local
    public static final String BASE_URL = "http://10.0.3.179/api/";

    //Login API
    public static final String POST_LOGIN = "login";
    //Employee ID list API
    public static final String GET_EMP_ID = "get-emp-id";
    //Register Employee API
    public static final String POST_ADD_EMPLOYEE = "add-employee";
    //Get History of Log hours
    public static final String GET_HISTORY = "get-updates";
    //    Get Project List
    public static final String GET_PROJECT_LIST = "get-project-list";
    //    Post Updates
    public static final String POST_UPDATES = "add-updates";


    // Network Constants
//    public static final String BASE_URL = "https://timesheet-161e.restdb.io/rest/";
    public static final String EMPLOYEE_ID_URL = "empid";
    public static final String API_KEY = "hartler";
    public static final String API_KEY_NAME = "authToken";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String EMPLOYEE_PROFILE_URL = "employee";
    public static final String PROJECTNAME_URL = "projectsname";
    public static final String LOGGEDHOURS_URL = "logsheet";
    public static final String DESCRIPTION_URL = "{\"empID\":";
    public static final String URL_BACKSLASH = "/";

    //Status Codes
    public static final int SUCCESS = 200;
    public static final int USER_NOT_FOUND = 401;
    public static final int INVALID_USER = 201;

}
