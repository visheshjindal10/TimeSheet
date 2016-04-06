package com.hartleylab.app.timesheet.localNotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat.Builder;

import com.hartleylab.app.timesheet.R;
import com.hartleylab.app.timesheet.activities.SplashScreenActivity;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager mManager = (NotificationManager) context.getApplicationContext()
                .getSystemService(context.getApplicationContext().NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(context.getApplicationContext(), SplashScreenActivity.class);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Builder mBuilder = new Builder(context).setSmallIcon(R.drawable.ic_notificationation)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setContentTitle(context.getString(R.string.notification_title))
                .setSound(alarmSound)
                .setLights(Color.parseColor("#2587c7"), 500, 500)
                .setAutoCancel(true).setContentText(context.getString(R.string.notification_msg));

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context
                .getApplicationContext(), 0
                , intent1, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pendingNotificationIntent);
        mManager.notify(0, mBuilder.build());

    }
}
