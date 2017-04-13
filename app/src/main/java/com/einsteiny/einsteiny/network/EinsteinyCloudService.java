package com.einsteiny.einsteiny.network;

import com.einsteiny.einsteiny.models.Course;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by tonya on 4/13/17.
 */

public interface EinsteinyCloudService {

    @GET("humanities")
    Observable<Course> getArtsCourses();

    @GET("economics-finance-domain")
    Observable<Course> getEconomicsCourses();

    @GET("computing")
    Observable<Course> getComputingCourses();

    @GET("science")
    Observable<Course> getScienceCourses();


}
