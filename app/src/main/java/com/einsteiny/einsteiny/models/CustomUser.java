package com.einsteiny.einsteiny.models;

import com.parse.ParseUser;

import java.io.Serializable;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 4/9/17.
 */

public class CustomUser implements Serializable {
    private static ParseUser user;
    public static final String COURSES_KEY = "subscribed_courses";
    public static final String LESSONS_KEY = "completed_lessons";


    public CustomUser() {
        user = ParseUser.getCurrentUser();
    }


    public void addSubscribedCourse(Course course) {
        List<String> courses = (ArrayList<String>) user.get(COURSES_KEY);
        if (courses == null) {
            courses = new ArrayList<>();
        }
        if (!courses.contains(course.getId())) {
            courses.add(course.getId());
        }

        user.put(COURSES_KEY, courses);
        user.saveInBackground();
    }

    public void addCompletedTopics(Lesson topic) {
        List<Lesson> topics = (ArrayList<Lesson>) user.get(LESSONS_KEY);
        if (topics == null) {
            topics = new ArrayList<>();
        }
        topics.add(topic);
        user.put(LESSONS_KEY, topics);
        user.saveInBackground();
    }

    // todo - getter and setter for topics list
    // todo - getter and setter for progress

    public void setDefaults(Boolean hasDefaults) {
        user.put("has_defaults", hasDefaults);
        user.saveInBackground();
    }

    public Boolean getDefaults() {
        return user.getBoolean("has_defaults");
    }

    public void setProfilePicUrl(URL url) {
        user.put("image_url", url);
        user.saveInBackground();
    }

    public void setDownloadWifi(Boolean wifi) {
        user.put("wifi_downloads", wifi);
        user.saveInBackground();
    }

    public void setPushNotification(Boolean pushNotification) {
        user.put("push_notification", pushNotification);
        user.saveInBackground();
    }

    public void setReminderDays(ArrayList<String> arrayList) {
        user.put("reminder_days", arrayList);
        user.saveInBackground();
    }

    public void setReminderTime(Time reminderTime) {
        user.put("reminder_time", reminderTime);
        user.saveInBackground();
    }

    public Time getReminderTime() {
        return (Time) user.get("reminder_time");
    }

    public ArrayList<String> getReminderDays() {
        return (ArrayList<String>) user.get("reminder_days");
    }

    public Boolean getPushNotification() {
        return user.getBoolean("push_notification");
    }

    public Boolean getDownloadWifi() {
        return user.getBoolean("wifi_downloads");
    }

    public ArrayList<Course> getSubscribedCourses() {
        return (ArrayList<Course>) user.get(COURSES_KEY);
    }

    public ArrayList<Lesson> getCompletedTopics() {
        return (ArrayList<Lesson>) user.get(LESSONS_KEY);
    }


}

