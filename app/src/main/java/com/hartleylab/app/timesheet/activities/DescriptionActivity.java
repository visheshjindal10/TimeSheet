package com.hartleylab.app.timesheet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.hartleylab.app.timesheet.Model.HistoryDescription;
import com.hartleylab.app.timesheet.R;
import com.hartleylab.app.timesheet.utilities.DateUtil;

public class DescriptionActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        setUpToolbar();

        Intent i = getIntent();
        HistoryDescription descriptionModel = i.getParcelableExtra(getString(R.string.key_project_description));

        AppCompatTextView tvProjectName = (AppCompatTextView) findViewById(R.id.tvProjectName);
        AppCompatTextView tvDescription = (AppCompatTextView) findViewById(R.id.tvDescription);
        AppCompatTextView tvDate = (AppCompatTextView) findViewById(R.id.tvDate);
        AppCompatTextView tvLoggedHours = (AppCompatTextView) findViewById(R.id.tvLoggedHours);
        AppCompatTextView tvTicketNumber = (AppCompatTextView) findViewById(R.id.tvTicket);
        AppCompatTextView tvTimeStamp = (AppCompatTextView) findViewById(R.id.tvTimeStamp);

        
        if (!TextUtils.isEmpty(descriptionModel.getProjectName()))
            tvProjectName.setText(descriptionModel.getProjectName());

        if (!TextUtils.isEmpty(descriptionModel.getLoggedDate())){
            tvDate.setText(DateUtil.getFormattedTime(descriptionModel.getLoggedDate(),DateUtil
                    .LOGGED_DATE_FROM_API, DateUtil.LOGGED_DATE_FOR_LIST));
        }

        if (!TextUtils.isEmpty(descriptionModel.getDescription()))
            tvDescription.setText(descriptionModel.getDescription());
        if (!TextUtils.isEmpty(descriptionModel.getLoggedHours()))
            tvLoggedHours.setText(descriptionModel.getLoggedHours());
        if (!TextUtils.isEmpty(descriptionModel.getTicketNo()))
            tvTicketNumber.setText(descriptionModel.getTicketNo());
        if (!TextUtils.isEmpty(descriptionModel.getTimeStamp())){
            tvTimeStamp.setText(DateUtil.getFormattedDateFromTimeStamp(descriptionModel
                    .getTimeStamp(),DateUtil.DATE_FORMAT_DD_MMM_YYYY_HH_MM));
        }
    }

    /**
     * Function to setup toolbar
     */
    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.tv_description);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


}
