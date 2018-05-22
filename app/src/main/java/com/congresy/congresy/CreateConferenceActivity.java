package com.congresy.congresy;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

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
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.congresy.congresy.remote.ApiUtils.useSession;

public class CreateConferenceActivity extends AppCompatActivity {

    UserService userService;

    public static String userAccount;
    public static String userAccountId;

    EditText edtName;
    EditText edtTheme;
    EditText edtPrice;
    EditText edtStart;
    EditText edtEnd;
    EditText edtSpeakers;
    EditText edtDescription;

    Button btnCreateConference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_conference);

        btnCreateConference = findViewById(R.id.btnCreate);

        edtName = findViewById(R.id.edtName);
        edtTheme = findViewById(R.id.edtTheme);
        edtPrice = findViewById(R.id.edtPrice);
        edtStart = findViewById(R.id.edtStart);
        edtEnd = findViewById(R.id.edtEnd);
        edtSpeakers = findViewById(R.id.edtSpeakers);
        edtDescription = findViewById(R.id.edtDescription);

        userService = ApiUtils.getUserService();
        new LoadUserAccount().execute();

        btnCreateConference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String theme = edtTheme.getText().toString();
                String price = edtPrice.getText().toString();
                String start = edtStart.getText().toString();
                String end = edtEnd.getText().toString();
                String speakers = edtSpeakers.getText().toString();
                String description = edtDescription.getText().toString();

                // adding properties to json for POST
                JSONObject json = new JSONObject();
                List<String> organizators = new ArrayList<>();
                organizators.add(userAccountId);

                try {

                    json.put("name", name);
                    json.put("theme", theme);
                    json.put("price", Double.valueOf(price));
                    json.put("start", start);
                    json.put("end", end);
                    json.put("speakersNames", speakers);
                    json.put("description", description);
                    json.put("organizators", new JSONArray(organizators));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //validate form
                if(validateRegister(name, theme, price, start, end, speakers, description)){
                    doConference(json);
                }
            }
        });
    }

    private boolean validateRegister(String name, String theme, String price, String start, String end, String speakers, String descripton){ //TODO
        if(name == null || name.trim().length() == 0 || theme == null || theme.trim().length() == 0 ||
                price == null || price.trim().length() == 0 || end == null || end .trim().length() == 0 ||
                start == null || start.trim().length() == 0 ||
                speakers == null || speakers.trim().length() == 0 || descripton == null || descripton.trim().length() == 0){
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doConference(final JSONObject json){
        Call call1 = userService.login(LoginActivity.username, LoginActivity.password);
        call1.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){

                    call = userService.createConference(json);
                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            if(response.isSuccessful()){

                                Intent intent = new Intent(CreateConferenceActivity.this, HomeActivity.class);
                                startActivity(intent);

                            } else {
                                Toast.makeText(CreateConferenceActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {
                            Toast.makeText(CreateConferenceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(CreateConferenceActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(CreateConferenceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private class LoadUserAccount extends AsyncTask<Void, Void, String> {
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
        protected String doInBackground(Void... params) {
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet("https://congresy.herokuapp.com/actors/userAccount/" + LoginActivity.username);
            String text;
            try {

                useSession();

                HttpResponse response = LoginActivity.httpClient.execute(httpGet, localContext);
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);
                userAccount = text;

                JSONObject jsonUserAccount = new JSONObject(userAccount);
                userAccountId = jsonUserAccount.getString("id");


            } catch (Exception e) {
                return e.getLocalizedMessage();

            }

            return text;

        }
    }
}
