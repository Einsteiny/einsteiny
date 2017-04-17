package com.einsteiny.einsteiny.network;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.einsteiny.einsteiny.R;
import com.einsteiny.einsteiny.activities.PlayYoutubeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class EinsteinyBroadcastReceiver extends BroadcastReceiver {

    public static final String intentAction = "com.parse.push.intent.RECEIVE";

    private String TAG = "EinsteinyBroadcastRec";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.d(TAG, "got action " + action);
        if (action.equals(intentAction)) {
            try {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

                // Iterate the parse keys if needed
                Iterator<String> itr = json.keys();
                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    String value = json.getString(key);
                    Log.d(TAG, "..." + key + " => " + value);
                    // Extract custom push data
                    if (key.equals("course")) {
                        String videoUrl = value;

//                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(
//                                R.drawable.ic_classes).setContentTitle("Notification: " + courseId).setContentText("Pushed!");
//                        NotificationManager mNotificationManager = (NotificationManager) context
//                                .getSystemService(Context.NOTIFICATION_SERVICE);
//                        mNotificationManager.notify(45, mBuilder.build());

                        Intent intentActivity = new Intent(context, PlayYoutubeActivity.class);
                        intentActivity.putExtra(PlayYoutubeActivity.EXTRA_LESSON, videoUrl);
                        intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);


                        int requestID = (int) System.currentTimeMillis(); //unique requestID to differentiate between various notification with same NotifId
                        int flags = PendingIntent.FLAG_UPDATE_CURRENT; // cancel old intent and create new one
                        PendingIntent pIntent = PendingIntent.getActivity(context.getApplicationContext(), requestID, intentActivity, flags);
// Now we can attach the pendingIntent to a new notification using setContentIntent
                        Notification noti = new NotificationCompat.Builder(context.getApplicationContext())
                                .setSmallIcon(R.drawable.ic_explore)
                                .setContentTitle("My notification")
                                .setContentText("Hello World!")
                                .setContentIntent(pIntent)
                                .setAutoCancel(true) // Hides the notification after its been selected
                                .build();
// Get the notification manager system service
                        NotificationManager mNotificationManager =
                                (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
                        mNotificationManager.notify(0, noti);
                    }
                }
            } catch (JSONException ex) {
                Log.d(TAG, "JSON failed!");
            }
        }
    }
}
