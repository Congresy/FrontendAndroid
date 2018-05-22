package com.congresy.congresy.remote;

import android.os.AsyncTask;

public class ApiUtils extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... voids) {
        return null;
    }

    private static final String BASE_URL = "https://congresy.herokuapp.com/";

    public static UserService getUserService(){
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

    public static UserService getUserServiceNoSession(){
        return RetrofitClient.getClientNoSession(BASE_URL).create(UserService.class);
    }


}
