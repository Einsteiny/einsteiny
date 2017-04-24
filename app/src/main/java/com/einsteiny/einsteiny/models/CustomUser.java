package com.einsteiny.einsteiny.models;

import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lukas on 4/9/17.
 */

public class CustomUser implements Serializable {

    public static final String SUBSCRIBED_COURSES_KEY = "subscribed_courses";
    public static final String COMPLETED_COURSES_KEY = "completed_courses";
    public static final String PROGRESS_FOR_COURSE = "progress_for_course";
    public static final String SUBSCRIBED_COURSES_DATES_KEY = "subscribed_courses_dates";
    public static final String LIKED_COURSES_KEY = "liked_courses";

    private static Course newlyFinishedCourse = null;

    public static Course getNewlyFinishedCourse() {
        return newlyFinishedCourse;
    }

    public static void setNewlyFinishedCourse(Course newlyFinishedCourse) {
        CustomUser.newlyFinishedCourse = newlyFinishedCourse;
    }


    public static void addSubscribedCourse(Course course) {
        ParseUser user = ParseUser.getCurrentUser();
        List<String> courses = (List<String>) user.get(SUBSCRIBED_COURSES_KEY);
        if (courses == null) {
            courses = new ArrayList<>();
        }
        if (!courses.contains(course.getId())) {
            courses.add(course.getId());
        }

        user.put(SUBSCRIBED_COURSES_KEY, courses);
        user.saveInBackground();
    }

    public static void unsubscribeCourse(Course course) {
        ParseUser user = ParseUser.getCurrentUser();
        List<String> courses = (List<String>) user.get(SUBSCRIBED_COURSES_KEY);
        if (courses == null) {
            courses = new ArrayList<>();
        } else {
            courses.remove(course.getId());
        }


        resetProgressForCourse(course.getId());

        user.put(SUBSCRIBED_COURSES_KEY, courses);
        user.saveInBackground();
    }

    public static void likeCourse(Course course) {
        ParseUser user = ParseUser.getCurrentUser();
        List<String> courses = (List<String>) user.get(LIKED_COURSES_KEY);
        if (courses == null) {
            courses = new ArrayList<>();
        }
        if (!courses.contains(course.getId())) {
            courses.add(course.getId());
        }

        user.put(LIKED_COURSES_KEY, courses);
        user.saveInBackground();
    }

    public static void unlikeCourse(Course course) {
        ParseUser user = ParseUser.getCurrentUser();
        List<String> courses = (List<String>) user.get(LIKED_COURSES_KEY);
        if (courses == null) {
            courses = new ArrayList<>();
        } else {
            courses.remove(course.getId());
        }

        user.put(LIKED_COURSES_KEY, courses);
        user.saveInBackground();
    }

    public static boolean isLikedCourse(Course course) {
        ParseUser user = ParseUser.getCurrentUser();
        List<String> courses = (List<String>) user.get(LIKED_COURSES_KEY);
        if (courses == null || courses.isEmpty()) {
            return false;
        }
        return courses.contains(course.getId());

    }

    public static void addProgressForCourse(String courseId) {
        ParseUser user = ParseUser.getCurrentUser();
        HashMap<String, Integer> progress = (HashMap<String, Integer>) user.get(PROGRESS_FOR_COURSE);
        if (progress == null) {
            progress = new HashMap<>();
        }

        Integer progressIndex = progress.get(courseId);
        if (progressIndex == null) {
            progress.put(courseId, 0);
        } else {
            progress.put(courseId, ++progressIndex);
        }

        user.put(PROGRESS_FOR_COURSE, progress);
        user.saveInBackground();
    }

    public static void addDatesForCourse(String courseId, long date) {
        ParseUser user = ParseUser.getCurrentUser();
        HashMap<String, Long> dates = (HashMap<String, Long>) user.get(SUBSCRIBED_COURSES_DATES_KEY);
        if (dates == null) {
            dates = new HashMap<>();
        }

        dates.put(courseId, date);
        user.put(SUBSCRIBED_COURSES_DATES_KEY, dates);
        user.saveInBackground();
    }

    public static long getDateForCourse(String courseId) {
        ParseUser user = ParseUser.getCurrentUser();
        HashMap<String, Long> dates = (HashMap<String, Long>) user.get(SUBSCRIBED_COURSES_DATES_KEY);
        if (dates == null || dates.get(courseId) == null) {
            return 0;
        }
        return dates.get(courseId);
    }

    public static void resetProgressForCourse(String courseId) {
        ParseUser user = ParseUser.getCurrentUser();

        HashMap<String, Integer> progress = (HashMap<String, Integer>) user.get(PROGRESS_FOR_COURSE);
        if (progress == null) {
            progress = new HashMap<>();
        }

        progress.remove(courseId);
        progress.put(courseId, 0);

        user.put(PROGRESS_FOR_COURSE, progress);
        user.saveInBackground();

    }

    public static int getProgressForCourse(String courseId) {
        ParseUser user = ParseUser.getCurrentUser();
        HashMap<String, Integer> progress = (HashMap<String, Integer>) user.get(PROGRESS_FOR_COURSE);
        if (progress == null) {
            return 0;
        }

        Integer progressIndex = progress.get(courseId);
        if (progressIndex == null) {
            return 0;
        }
        return progressIndex;
    }

    public static List<String> getSubscribedCourses() {
        ParseUser user = ParseUser.getCurrentUser();
        return (List<String>) user.get(SUBSCRIBED_COURSES_KEY);
    }

    public static void addCompletedCourse(Course course) {
        ParseUser user = ParseUser.getCurrentUser();
        List<String> courses = (List<String>) user.get(COMPLETED_COURSES_KEY);
        if (courses == null) {
            courses = new ArrayList<>();
        }
        if (!courses.contains(course.getId())) {
            courses.add(course.getId());
            newlyFinishedCourse = course;
        }

        user.put(COMPLETED_COURSES_KEY, courses);
        user.saveInBackground();
    }

    public static boolean isCompletedCourse(String courseId) {
        List<String> completedCourses = getCompletedCourses();
        return (completedCourses != null && completedCourses.contains(courseId));
    }

    public static boolean isSubscribedCourse(String courseId) {
        List<String> subscribedCourses = getSubscribedCourses();
        return (subscribedCourses != null && subscribedCourses.contains(courseId));
    }

    public static List<String> getCompletedCourses() {
        ParseUser user = ParseUser.getCurrentUser();
        return (List<String>) user.get(COMPLETED_COURSES_KEY);
    }


    public static ParseUser getUser() {
        return ParseUser.getCurrentUser();
    }


}

