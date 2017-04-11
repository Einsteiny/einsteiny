package com.einsteiny.einsteiny.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

@ParseClassName("Topic")
public class Topic extends ParseObject implements Serializable {
    //Todo: make this look like Course
    String description;
    String title;
    String slug;


    public String getDescription() {
        return description;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public Topic() {
        super();
        //Needed for Parse
    }

    public Topic(JSONObject jsonObject) throws JSONException {
        super();
        description = jsonObject.getString("description");
        title = jsonObject.getString("title");
        slug = jsonObject.getString("node_slug");
    }

    public static ArrayList<Topic> fromJsonArray(JSONArray array) {
        ArrayList<Topic> topics = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                topics.add(new Topic(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return topics;
    }
}
