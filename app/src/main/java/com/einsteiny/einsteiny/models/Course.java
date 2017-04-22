package com.einsteiny.einsteiny.models;

import com.einsteiny.einsteiny.db.CourseDatabase;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.List;

@Table(database = CourseDatabase.class)
@Parcel(analyze = {Course.class})
public class Course extends BaseModel {

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
    @SerializedName("category")
    String category;

    @Column
    @SerializedName("photo_url")
    String photoUrl;


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

    public float getComplexity() {
        return complexity;
    }

    public String getCategory() {
        return category;
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
