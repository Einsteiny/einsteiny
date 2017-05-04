package com.einsteiny.einsteiny.utils;

import com.einsteiny.einsteiny.models.Course;
import com.einsteiny.einsteiny.models.CustomUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        List<Course> sortCourses = new ArrayList<>(allCourses);

        Collections.sort(sortCourses, new Comparator<Course>() {
            public int compare(Course o1, Course o2) {
                return Float.compare(o2.getComplexity(), o1.getComplexity());
            }
        });


        for (int i = 0; i < POPULARITY_THRESHOLD; i++) {
            if (sortCourses.size() >= i) {
                courses.add(sortCourses.get(i));
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
