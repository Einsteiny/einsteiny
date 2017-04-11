package com.einsteiny.einsteiny.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ParseClassName("Course")
public class Course extends ParseObject implements Serializable {

    public Course() {
        super();
        //Needed for Parse
    }

    public Course(JSONObject jsonObject) throws JSONException {
        super();
        setTitle(jsonObject.getString("standalone_title"));
        setDescription(jsonObject.getString("description"));
        setPhotoUrl(jsonObject.optString("photo_url"));
        setTopics(Topic.fromJsonArray(jsonObject.getJSONArray("children")));
        saveInBackground();
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public void setPhotoUrl(String photoUrl) {
        put("photoUrl", photoUrl);
    }

    public void setTopics(List<Topic> topics) {
        put("topics", topics);
    }

    public String getPhotoUrl() {
        return getString("photoUrl");
    }

    public String getTitle() {
        return getString("title");
    }

    public String getDescription() {
        return getString("description");
    }

    public List<Topic> getTopics() {
        return (List<Topic>) getParseObject("topics");
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
