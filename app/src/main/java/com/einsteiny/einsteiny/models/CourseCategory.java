package com.einsteiny.einsteiny.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by tonya on 4/13/17.
 */

public class CourseCategory implements Serializable {

    @SerializedName("title")
    String title;

    @SerializedName("courses")
    ArrayList<Course> courses;

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public String getTitle() {
        return title;
    }
}
