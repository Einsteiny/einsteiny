package com.einsteiny.einsteiny.network;

import com.einsteiny.einsteiny.models.CourseCategory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by tonya on 4/13/17.
 */

public class EinsteinyServerClient {

    private final static String url = "https://einsteiny.herokuapp.com/";

    private final static EinsteinyCloudService service;

    private static EinsteinyServerClient instance;

    //Initialize okhttp client, retrofit and nytimes service
    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
//        builder.addInterceptor(chain -> {
//            Request original = chain.request();
//            HttpUrl originalHttpUrl = original.url();
//
////            HttpUrl url1 = originalHttpUrl.newBuilder()
////                    .addQueryParameter("api-key", API_KEY)
////                    .build();
//
//            // Request customization: add request headers
////            Request.Builder requestBuilder = original.newBuilder()
////                    .url(url1);
//
//            Request request = requestBuilder.build();
//            return chain.proceed(request);
//        });

        OkHttpClient client = builder.build();

        Gson gson = new GsonBuilder()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        service = retrofit.create(EinsteinyCloudService.class);
    }

    private EinsteinyServerClient() {

    }

    public static EinsteinyServerClient getInstance() {
        if (instance == null) {
            instance = new EinsteinyServerClient();
        }
        return instance;
    }


    public Observable<CourseCategory> getArtsCourses() {
        return service.getArtsCourses();
    }

    public Observable<CourseCategory> getEconomicsCourses() {
        return service.getEconomicsCourses();
    }

    public Observable<CourseCategory> getComputingCourses() {
        return service.getComputingCourses();
    }

    public Observable<CourseCategory> getScienceCourses() {
        return service.getScienceCourses();
    }


}
