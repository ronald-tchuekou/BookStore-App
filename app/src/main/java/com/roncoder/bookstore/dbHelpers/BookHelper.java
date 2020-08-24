package com.roncoder.bookstore.dbHelpers;

import com.roncoder.bookstore.api.APIClient;
import com.roncoder.bookstore.api.APIRequest;
import com.roncoder.bookstore.api.Result;

import retrofit2.Call;

public class BookHelper {

    public static APIRequest getService () { return APIClient.getInstance().create(APIRequest.class); }

    public static Call<Result> getBooksByCycle(String cycle) {
        return getService().getAllCycleBook(cycle);
    }

    public static Call<Result> getClassBooks(String _class) {
        return getService().getAllClassBook(_class);
    }

    public static Call<Result> getQueryBooks(String query) {
        return getService().getAllClassBook(query);
    }
}
