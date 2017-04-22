package com.einsteiny.einsteiny.models;

import com.einsteiny.einsteiny.db.CourseDatabase;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

@Table(database = CourseDatabase.class)
@Parcel(analyze = {Lesson.class})
public class Lesson extends BaseModel {

    @Column
    @PrimaryKey
    @SerializedName("id")
    String id;

    @Column
    @SerializedName("courseId")
    String courseId;

    @Column
    @SerializedName("description")
    String description;

    @Column
    @SerializedName("title")
    String title;

    @Column
    @SerializedName("image_url")
    String imageUrl;

    @Column
    @SerializedName("video_url")
    String videoUrl;


    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        String[] parts = videoUrl.split("/");
        String video = parts[parts.length - 1];
        String videoId = video.substring(0, video.indexOf('.'));
        return videoId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

}
