package com.congresy.congresy.remote;

import android.os.AsyncTask;

import com.congresy.congresy.LoginActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;

public class ApiUtils extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... voids) {
        return null;
    }

    public static final String BASE_URL = "https://congresy.herokuapp.com/";

    public static UserService getUserService(){
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

    public static void useSession(){
        try {
            HttpPost httppost = new HttpPost(ApiUtils.BASE_URL + "login?username=" + LoginActivity.username + "&password=" + LoginActivity.password);
            LoginActivity.httpClient.execute(httppost);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
