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
import com.einsteiny.einsteiny.course.CourseActivity;
import com.einsteiny.einsteiny.activities.PlayYoutubeActivity;
import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.Course_Table;
import com.einsteiny.einsteiny.models.CustomUser;
import com.einsteiny.einsteiny.models.Lesson;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.Iterator;
import java.util.List;

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
                        String courseId = value;

                        List<String> subscribedCourses = CustomUser.getSubscribedCourses();
                        //This course is not in the list of subscribed courses
                        if (subscribedCourses == null || !subscribedCourses.contains(courseId))
                            return;

                        int progress = CustomUser.getProgressForCourse(courseId);
                        Course course = SQLite.select().
                                from(Course.class).where(Course_Table.id.is(courseId)).querySingle();
                        List<Lesson> lessons = course.getLessons();

                        if (progress < lessons.size()) {
                            //Course is not completed yet
                            Lesson lesson = course.getMyLessons().get(progress);
                            CustomUser.addProgressForCourse(courseId);

                            Intent intentActivity = new Intent(context, PlayYoutubeActivity.class);
                            intentActivity.putExtra(PlayYoutubeActivity.EXTRA_LESSON, lesson.getVideoUrl());

                            Intent intentCourseActivity = new Intent(context, CourseActivity.class);
                            intentCourseActivity.putExtra(CourseActivity.EXTRA_COURSE, Parcels.wrap(course));

                            //unique requestID to differentiate between various notification with same NotifId
                            int requestID = (int) System.currentTimeMillis();
                            PendingIntent pIntent = PendingIntent.getActivity(context.getApplicationContext(), requestID, intentActivity, 0);

                            //unique requestID to differentiate between various notification with same NotifId
                            int requestCourseID = (int) System.currentTimeMillis();
                            PendingIntent pCourseIntent = PendingIntent.getActivity(context.getApplicationContext(), requestCourseID, intentCourseActivity, 0);

                            Notification noti = new NotificationCompat.Builder(context.getApplicationContext())
                                    .setSmallIcon(R.drawable.ic_explore)
                                    .setContentTitle("Hello clever!")
                                    .setContentText(String.format("Time to watch Lesson %s of course %s", progress + 1, course.getTitle()))
                                    .setContentIntent(pIntent)
                                    .addAction(R.drawable.ic_timer, "PLAY VIDEO", pIntent)
                                    .addAction(R.drawable.ic_timer, "GO TO COURSE", pCourseIntent)
                                    .setAutoCancel(true) // Hides the notification after its been selected
                                    .build();

                            NotificationManager mNotificationManager =
                                    (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                            mNotificationManager.notify(requestID, noti);

                        }

                        if (CustomUser.getProgressForCourse(courseId) >= lessons.size()) {
                            CustomUser.addCompletedCourse(course);
                            CustomUser.unsubscribeCourse(course);
                        }

                    }
                }
            } catch (JSONException ex) {
                Log.d(TAG, "JSON failed!");
            }
        }
    }
}
