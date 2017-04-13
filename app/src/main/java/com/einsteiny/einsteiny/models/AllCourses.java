package com.einsteiny.einsteiny.models;

import java.io.Serializable;

/**
 * Created by tonya on 4/13/17.
 */

public class AllCourses implements Serializable {

    public CourseCategory artCourses;
    public CourseCategory economicsCourses;

    public CourseCategory computingCourses;
    public CourseCategory scienceCourses;

    public AllCourses(CourseCategory artCourses, CourseCategory computingCourses, CourseCategory economicsCourses, CourseCategory scienceCourses) {
        this.artCourses = artCourses;
        this.computingCourses = computingCourses;
        this.economicsCourses = economicsCourses;
        this.scienceCourses = scienceCourses;
    }
}
