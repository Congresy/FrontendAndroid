package com.congresy.congresy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IndexActivity extends AppCompatActivity {

    public static DefaultHttpClient httpClient;
    public static String username;
    public static String password;

    public static String text;
    public static String text2;
    public static int conferencesSize;
    public static int usersSize;
    public static int activeConferences;

    EditText edtUsername;
    EditText edtPassword;

    TextView data;

    Button btnLogin;
    Button btnRegister;

    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        data = findViewById(R.id.data);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndexActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndexActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        new IndexActivity.LoadData().execute();

    }


    private class LoadData extends AsyncTask<Void, Void, String> {
        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {

            InputStream in = entity.getContent();

            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n>0) {
                byte[] b = new byte[4096];

                n =  in.read(b);

                if (n>0) out.append(new String(b, 0, n));

            }

            return out.toString();

        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet("https://congresy.herokuapp.com/conferences/detailed?order=date");
            HttpGet httpGet2 = new HttpGet("https://congresy.herokuapp.com/actors/role/User");

            try {
                DefaultHttpClient def1 = new DefaultHttpClient();
                DefaultHttpClient def2 = new DefaultHttpClient();

                HttpResponse response = def1.execute(httpGet, localContext);
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);

                HttpResponse response2 = def2.execute(httpGet2, localContext);
                HttpEntity entity2 = response2.getEntity();
                text2 = getASCIIContentFromEntity(entity2);

                JSONArray array1 = new JSONArray(text);
                JSONArray array2 = new JSONArray(text2);

                setActiveConferences(array1);

                conferencesSize = array1.length();
                usersSize = array2.length();

            } catch (Exception e) {
                return e.getLocalizedMessage();

            }

            return text;
        }

        @SuppressLint("SetTextI18n")
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            // dismiss the dialog after getting all products
            try
            {
                data.setText(conferencesSize + " conferences registed in our database\n" + activeConferences + " active conferences\n" + usersSize + " users using our app");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void setActiveConferences (JSONArray jsonArray) throws JSONException, ParseException {
        for(int index = 0; index < jsonArray.length(); index++) {
            JSONObject jsonObject = jsonArray.getJSONObject(index);
            if(checkEndDate(jsonObject.getString("end"))) {
                activeConferences++;
            }
        }
    }

    private boolean checkEndDate(String date) throws ParseException {
        boolean res = true;

        Date today = new Date();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
        Date dateFormated = format.parse(date);

        if (dateFormated.before(today)){
            res = false;
        }

        return res;
    }
}
