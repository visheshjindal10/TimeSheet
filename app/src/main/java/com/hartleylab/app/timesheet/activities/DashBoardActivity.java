package com.hartleylab.app.timesheet.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.hartleylab.app.timesheet.Model.HistoryDescription;
import com.hartleylab.app.timesheet.R;
import com.hartleylab.app.timesheet.loader.HistoryLoader;
import com.hartleylab.app.timesheet.localNotification.MyReceiver;
import com.hartleylab.app.timesheet.utilities.DividerItemDecoration;
import com.hartleylab.app.timesheet.utilities.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;
import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

public class DashBoardActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rv;
    private List<HistoryDescription> historyDescriptionList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private SharedPreferenceManager sharedPreferenceManager;
    private PendingIntent pendingIntent;
    private AlarmManager alarmMgr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        sharedPreferenceManager = new SharedPreferenceManager(this);
        alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (!sharedPreferenceManager.getBooleanValue(getString(R.string.sp_key_isAlramRegistered))) {
            sharedPreferenceManager.setBooleanValue(getString(R.string.sp_key_isAlramRegistered), true);
            setUpAlarmForNotification();
        }
        setupToolbar();
        initiViews();
        getSupportLoaderManager().initLoader(R.string.id_history_loader, null, loaderCallbacks);
    }

    /**
     * Function to set Alarm for notification
     */
    private void setUpAlarmForNotification() {
        // Set the alarm to start at approximately 6:45 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 45);

        Intent myIntent = new Intent(DashBoardActivity.this, MyReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(DashBoardActivity.this, 1234,
                myIntent, 0);

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    /**
     * Function to initialize views
     */
    private void initiViews() {
        progressBar = (ProgressBar) findViewById(R.id.pb);
        rv = (RecyclerView) findViewById(R.id.recyclerview);
        initRecyclerView(historyDescriptionList);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton2);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), UpdateActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * Function to setup toolbar
     */
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setLogo(R.drawable.ic_logo);
        }
    }

    /**
     * Function to initiate Recycler View
     *
     * @param list Array List
     */
    private void initRecyclerView(List<HistoryDescription> list) {
        rv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        rv.setLayoutManager(linearLayoutManager);
        Adapter adapter = new Adapter(list,DashBoardActivity.this);
        rv.setAdapter(adapter);
        rv.addItemDecoration(new DividerItemDecoration(DashBoardActivity.this, VERTICAL));
        rv.setItemAnimator(new DefaultItemAnimator());
        adapter.setmOnItemClickListener(new Adapter.OnItemClickListener() {

            @Override
            public void onClick(View v, HistoryDescription historyDescription) {
                Intent i = new Intent(DashBoardActivity.this, DescriptionActivity.class);
                i.putExtra(getString(R.string.key_project_description), historyDescription);
                startActivity(i);
            }
        });
    }


    @Override
    public void onRefresh() {
        refershItemList();
    }

    /**
     * Function to refreshList
     */
    private void refershItemList() {
        Intent intent = new Intent(HistoryLoader.ACTION);
        LocalBroadcastManager.getInstance(DashBoardActivity.this).sendBroadcast(intent);
    }

    private LoaderCallbacks<List<HistoryDescription>> loaderCallbacks =
            new LoaderCallbacks<List<HistoryDescription>>() {
                @Override
                public Loader<List<HistoryDescription>> onCreateLoader(int id, Bundle args) {
                    return new HistoryLoader(getApplicationContext());
                }

                @Override
                public void onLoadFinished(Loader<List<HistoryDescription>> loader, List<HistoryDescription> data) {
                    swipeRefreshLayout.setRefreshing(false);
                    if (progressBar.getVisibility() == View.VISIBLE) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        if (!data.isEmpty()) {
                            historyDescriptionList.clear();
                            historyDescriptionList.addAll(data);
                            initRecyclerView(historyDescriptionList);
                        }
                    } else if (!data.isEmpty()) {
                        historyDescriptionList.clear();
                        historyDescriptionList.addAll(data);
                        initRecyclerView(historyDescriptionList);
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<HistoryDescription>> loader) {

                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            onLogout();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Function to logout of the application
     */
    private void onLogout() {
        sharedPreferenceManager.setBooleanValue(getString(R.string.key_isLogin), false);
        if (alarmMgr != null){
            alarmMgr.cancel(pendingIntent);
        }
        sharedPreferenceManager.setBooleanValue(getString(R.string.sp_key_isAlramRegistered),false);
        startActivity(new Intent(DashBoardActivity.this, SplashScreenActivity.class));
        finish();
    }

}

