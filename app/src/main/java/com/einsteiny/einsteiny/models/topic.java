package com.einsteiny.einsteiny.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lsyang on 4/8/17.
 */

public class Topic implements Serializable {



    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    String description;
    String title;
    String slug;

    public Topic(JSONObject jsonObject) throws JSONException{
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
