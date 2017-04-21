package com.einsteiny.einsteiny.models;

import com.einsteiny.einsteiny.db.CourseDatabase;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.util.List;

@Table(database = CourseDatabase.class)
public class Course extends BaseModel implements Serializable {

    @Column
    @PrimaryKey
    @SerializedName("id")
    String id;

    @Column
    @SerializedName("title")
    String title;

    @Column
    @SerializedName("complexity")
    float complexity;

    @Column
    @SerializedName("description")
    String description;

    @Column
    @SerializedName("photo_url")
    String photoUrl;

    @Column
    long startTime;

    @SerializedName("lessons")
    List<Lesson> lessons;


    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "lessons")
    public List<Lesson> getMyLessons() {
        if (lessons == null || lessons.isEmpty()) {
            lessons = SQLite.select()
                    .from(Lesson.class)
                    .where(Lesson_Table.courseId.eq(id))
                    .queryList();
        }
        return lessons;
    }

    public String getPhotoUrl() {
        if (photoUrl != null && !photoUrl.isEmpty())
            return photoUrl;

        return lessons.get(0).getImageUrl();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public float getComplexity() {
        return complexity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;

        return id != null ? id.equals(course.id) : course.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }


}
