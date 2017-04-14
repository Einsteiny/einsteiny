package com.einsteiny.einsteiny.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public List<Course> getAllCourses() {
        List<Course> all = new ArrayList<>();
        all.addAll(artCourses.getCourses());
        all.addAll(economicsCourses.getCourses());
        all.addAll(computingCourses.getCourses());
        all.addAll(scienceCourses.getCourses());

        return all;

    }
}
