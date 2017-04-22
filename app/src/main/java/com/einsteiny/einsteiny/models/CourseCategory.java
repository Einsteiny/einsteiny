package com.einsteiny.einsteiny.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class CourseCategory {

    @SerializedName("title")
    String title;

    @SerializedName("courses")
    List<Course> courses;

    public List<Course> getCourses() {
        return courses;
    }

    public String getTitle() {
        return title;
    }
}
