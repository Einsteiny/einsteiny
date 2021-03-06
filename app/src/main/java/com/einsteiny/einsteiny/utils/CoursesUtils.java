package com.einsteiny.einsteiny.utils;

import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.CustomUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonya on 4/21/17.
 */

public class CoursesUtils {

    public static final int POPULARITY_THRESHOLD = 4;

    public static List<Course> getCoursesForCategory(List<Course> allCourses, String category) {
        List<Course> courses = new ArrayList();

        for (Course course : allCourses) {
            if (course.getCategory().equals(category)) {
                courses.add(course);
            }
        }

        return courses;

    }

    public static List<Course> getCoursesForIds(List<Course> allCourses, List<String> courseIds) {
        List<Course> courses = new ArrayList<>();

        if (courseIds != null) {
            for (Course course : allCourses) {
                if (courseIds.contains(course.getId())) {
                    courses.add(course);
                }
            }

        }

        return courses;
    }

    public static List<Course> getPopularCourses(List<Course> allCourses) {
        List<Course> courses = new ArrayList<>();
        for (Course course : allCourses) {
            if (course.isPopular()) {
                courses.add(course);
            }
        }

        return courses;
    }

    public static List<Course> getFavouritesCourses(List<Course> allCourses) {
        List<Course> courses = new ArrayList<>();

        for (Course course : allCourses) {
            if (CustomUser.isLikedCourse(course)) {
                courses.add(course);
            }
        }

        return courses;
    }

}
