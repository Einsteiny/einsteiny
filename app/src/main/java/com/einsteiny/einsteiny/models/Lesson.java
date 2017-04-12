package com.einsteiny.einsteiny.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


public class Lesson implements Serializable {
    String description;
    String title;
    String imageUrl;
    String videoUrl;


    public String getDescription() {
        return description;
    }

//    public String getSlug() {
//        return slug;
//    }

    public String getTitle() {
        return title;
    }

    public Lesson(JSONObject jsonObject) throws JSONException {
        description = jsonObject.getString("description");
        title = jsonObject.getString("title");
        imageUrl = jsonObject.getString("image_url");
        videoUrl = jsonObject.getString("video_url");
    }

    public static ArrayList<Lesson> fromJsonArray(JSONArray array) {
        ArrayList<Lesson> lessons = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                lessons.add(new Lesson(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return lessons;
    }
}
