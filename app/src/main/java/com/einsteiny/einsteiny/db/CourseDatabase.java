package com.einsteiny.einsteiny.db;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = CourseDatabase.NAME, version = CourseDatabase.VERSION)
public class CourseDatabase {

    public static final String NAME = "EinsteinyCoursesDB";

    public static final int VERSION = 2;
}
