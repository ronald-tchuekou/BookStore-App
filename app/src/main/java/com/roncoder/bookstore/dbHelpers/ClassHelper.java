package com.roncoder.bookstore.dbHelpers;

import com.roncoder.bookstore.api.APIClient;
import com.roncoder.bookstore.api.APIRequest;
import com.roncoder.bookstore.api.Result;

import retrofit2.Call;

public class ClassHelper {

    public static APIRequest getService () {
        return APIClient.getInstance().create(APIRequest.class);
    }
    public static Call<Result> getClassesByCycle(String cycle) {
        return getService().getCycleClasses(cycle);
    }
}
