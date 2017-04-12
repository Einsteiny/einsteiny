package com.einsteiny.einsteiny.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

@ParseClassName("Topic")
public class Topic extends ParseObject implements Serializable {

    public Topic() {
        super();
        //Needed for Parse
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public void setSlug(String slug) {
        put("slug", slug);
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public String getDescription() {
        try {
            return fetchIfNeeded().getString("description");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getSlug() {
        try {
            return fetchIfNeeded().getString("slug");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getTitle() {
        try {
            return fetchIfNeeded().getString("title");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    public Topic(JSONObject jsonObject) throws JSONException {
        super();
        setDescription(jsonObject.getString("description"));
        setTitle(jsonObject.getString("title"));
        setSlug(jsonObject.getString("node_slug"));
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
