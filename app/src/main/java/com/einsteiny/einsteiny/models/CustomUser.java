package com.einsteiny.einsteiny.models;

import com.parse.ParseUser;

import java.io.Serializable;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lukas on 4/9/17.
 */

public class CustomUser implements Serializable {

    public static final String SUBSCRIBED_COURSES_KEY = "subscribed_courses";
    public static final String COMPLETED_COURSES_KEY = "completed_courses";
    public static final String PROGRESS_FOR_COURSE = "progress_for_course";


    public static void addSubscribedCourse(Course course) {
        ParseUser user = ParseUser.getCurrentUser();
        List<String> courses = (List<String>) user.get(SUBSCRIBED_COURSES_KEY);
        if (courses == null) {
            courses = new ArrayList<>();
        }
        if (!courses.contains(course.getId())) {
            courses.add(course.getId());
        }

        user.put(SUBSCRIBED_COURSES_KEY, courses);
        user.saveInBackground();
    }

    public static void unsubscribeCourse(Course course) {
        ParseUser user = ParseUser.getCurrentUser();
        List<String> courses = (List<String>) user.get(SUBSCRIBED_COURSES_KEY);
        if (courses == null) {
            courses = new ArrayList<>();
        } else {
            courses.remove(course.getId());
        }

        resetProgressForCourse(course.getId());

        user.put(SUBSCRIBED_COURSES_KEY, courses);
        user.saveInBackground();
    }

    public static void addProgressForCourse(String courseId) {
        ParseUser user = ParseUser.getCurrentUser();
        HashMap<String, Integer> progress = (HashMap<String, Integer>) user.get(PROGRESS_FOR_COURSE);
        if (progress == null) {
            progress = new HashMap<>();
        }

        Integer progressIndex = progress.get(courseId);
        if (progressIndex == null) {
            progress.put(courseId, 0);
        } else {
            progress.put(courseId, ++progressIndex);
        }

        user.put(PROGRESS_FOR_COURSE, progress);
        user.saveInBackground();
    }

    public static void resetProgressForCourse(String courseId) {
        ParseUser user = ParseUser.getCurrentUser();

        HashMap<String, Integer> progress = (HashMap<String, Integer>) user.get(PROGRESS_FOR_COURSE);
        if (progress == null) {
            progress = new HashMap<>();
        }

        progress.remove(courseId);
        progress.put(courseId, 0);

        user.put(PROGRESS_FOR_COURSE, progress);
        user.saveInBackground();

    }

    public static int getProgressForCourse(String courseId) {
        ParseUser user = ParseUser.getCurrentUser();
        HashMap<String, Integer> progress = (HashMap<String, Integer>) user.get(PROGRESS_FOR_COURSE);
        if (progress == null) {
            return 0;
        }

        Integer progressIndex = progress.get(courseId);
        if (progressIndex == null) {
            return 0;
        }
        return progressIndex;
    }

    public static List<String> getSubscribedCourses() {
        ParseUser user = ParseUser.getCurrentUser();
        return (List<String>) user.get(SUBSCRIBED_COURSES_KEY);
    }

    public static void addCompletedCourse(Course course) {
        ParseUser user = ParseUser.getCurrentUser();
        List<String> courses = (List<String>) user.get(COMPLETED_COURSES_KEY);
        if (courses == null) {
            courses = new ArrayList<>();
        }
        if (!courses.contains(course.getId())) {
            courses.add(course.getId());
        }

        user.put(COMPLETED_COURSES_KEY, courses);
        user.saveInBackground();
    }

    public static List<String> getCompletedCourses() {
        ParseUser user = ParseUser.getCurrentUser();
        return (List<String>) user.get(COMPLETED_COURSES_KEY);
    }


    public static ParseUser getUser() {
        return ParseUser.getCurrentUser();
    }

    public static void setDefaults(Boolean hasDefaults) {
        ParseUser user = ParseUser.getCurrentUser();
        user.put("has_defaults", hasDefaults);
        user.saveInBackground();
    }

    public static Boolean getDefaults() {
        ParseUser user = ParseUser.getCurrentUser();
        return user.getBoolean("has_defaults");
    }

    public static void setProfilePicUrl(URL url) {
        ParseUser user = ParseUser.getCurrentUser();
        user.put("image_url", url);
        user.saveInBackground();
    }

    public static void setDownloadWifi(Boolean wifi) {
        ParseUser user = ParseUser.getCurrentUser();
        user.put("wifi_downloads", wifi);
        user.saveInBackground();
    }

    public void setPushNotification(Boolean pushNotification) {
        ParseUser user = ParseUser.getCurrentUser();
        user.put("push_notification", pushNotification);
        user.saveInBackground();
    }

    public void setReminderDays(ArrayList<String> arrayList) {
        ParseUser user = ParseUser.getCurrentUser();
        user.put("reminder_days", arrayList);
        user.saveInBackground();
    }

    public void setReminderTime(Time reminderTime) {
        ParseUser user = ParseUser.getCurrentUser();
        user.put("reminder_time", reminderTime);
        user.saveInBackground();
    }

    public static Time getReminderTime() {
        ParseUser user = ParseUser.getCurrentUser();
        return (Time) user.get("reminder_time");
    }

    public static ArrayList<String> getReminderDays() {
        ParseUser user = ParseUser.getCurrentUser();
        return (ArrayList<String>) user.get("reminder_days");
    }

    public static Boolean getPushNotification() {
        ParseUser user = ParseUser.getCurrentUser();
        return user.getBoolean("push_notification");
    }

    public static Boolean getDownloadWifi() {
        ParseUser user = ParseUser.getCurrentUser();
        return user.getBoolean("wifi_downloads");
    }


}

