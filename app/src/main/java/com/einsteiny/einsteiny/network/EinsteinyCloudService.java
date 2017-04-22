package com.einsteiny.einsteiny.network;

import com.einsteiny.einsteiny.models.CourseCategory;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by tonya on 4/13/17.
 */

public interface EinsteinyCloudService {

    @GET("humanities")
    Observable<CourseCategory> getArtsCourses();

    @GET("economics-finance-domain")
    Observable<CourseCategory> getEconomicsCourses();

    @GET("computing")
    Observable<CourseCategory> getComputingCourses();

    @GET("science")
    Observable<CourseCategory> getScienceCourses();

    @GET("all-courses")
    Observable<CourseCategory> getAllCourses();


}
