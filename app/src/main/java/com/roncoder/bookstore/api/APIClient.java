package com.roncoder.bookstore.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

//    public static final String BASE_URL = "http://bookstore.com";
    public static final String BASE_URL = "http://frnlzcr.cluster023.hosting.ovh.net";
    private static Retrofit retrofit = null;

    public static Retrofit getInstance () {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

}
