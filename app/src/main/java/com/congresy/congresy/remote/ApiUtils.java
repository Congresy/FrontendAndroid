package com.congresy.congresy.remote;

public class ApiUtils {

    public static final String BASE_URL = "https://congresy.herokuapp.com/";

    public static UserService getUserService(){
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }
}
