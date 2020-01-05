package com.example.gestor_tab.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.gestor_tab.R;

import java.util.ArrayList;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        ArrayList<String> enc = intent.getStringArrayListExtra("Encomendas");

        issueNotification(context, enc.get(0));
    }

    void issueNotification(Context context, String s)
    {


        Intent intent = new Intent(context, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // make the channel. The method has been discussed before.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel("CHANNEL_1", "Example channel", NotificationManager.IMPORTANCE_DEFAULT, context);
        }
        // the check ensures that the channel will only be made
        // if the device is running Android 8+

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(context, "CHANNEL_1");
        // the second parameter is the channel id.
        // it should be the same as passed to the makeNotificationChannel() method
        int color = 0xff123456;

        NotificationCompat.Builder builder = notification
                .setSmallIcon(R.mipmap.ic_launcher) // can use any other icon
                .setContentTitle("ENCOMENDA!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Está na hora de enviar a encomenda:" + s))
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setNumber(3);// this shows a number in the notification dots

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

        // it is better to not use 0 as notification id, so used 1.
        assert notificationManager != null;
        notificationManager.notify(1, notification.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void makeNotificationChannel(String id, String name, int importance, Context context)
    {
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setShowBadge(true); // set false to disable badges, Oreo exclusive

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }
}
