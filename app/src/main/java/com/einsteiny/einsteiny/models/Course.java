package com.einsteiny.einsteiny.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Course implements Serializable {

    String title;
    String description;
    //    String photoUrl;
    List<Lesson> lessons;

//    public String getPhotoUrl() {
//        return photoUrl;
//    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public Course(JSONObject jsonObject) throws JSONException {
        title = jsonObject.getString("standalone_title");
        description = jsonObject.getString("description");
//        photoUrl = jsonObject.optString("photo_url");
        lessons = Lesson.fromJsonArray(jsonObject.getJSONArray("lessons"));
    }

    public static ArrayList<Course> fromJSONArray(JSONArray array) {
        ArrayList<Course> courses = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                courses.add(new Course(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return courses;
    }
}
