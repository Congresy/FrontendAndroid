package com.congresy.congresy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.congresy.congresy.adapters.EventListJoinProcessAdapter;
import com.congresy.congresy.domain.Conference;
import com.congresy.congresy.domain.Event;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import org.joda.time.Interval;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoiningConferenceActivity extends BaseActivity {

    static boolean auxOK = false;

    UserService userService;
    private static List<Event> eventsList;

    private String username;
    
    final int REQUEST_CODE = 1;
    final String get_token = "https://congresy.herokuapp.com/payments/checkouts";
    final String send_payment_details = "https://congresy.herokuapp.com/payments/checkouts";
    String token, amount;
    HashMap<String, String> paramHash;

    Button btnPay;
    LinearLayout llHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Choosing events");

        loadDrawer(R.layout.activity_joining_conference);

        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        username = sp.getString("Username", "not found");

        userService = ApiUtils.getUserService();

        Intent intent = getIntent();

        amount = intent.getExtras().get("price").toString();

        loadProcess();

        llHolder = findViewById(R.id.llHolder);
        btnPay = findViewById(R.id.btnPay);

        btnPay.setText("Pay and Join - " + amount + " euros");

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEvents();
            }
        });

        new HttpRequest().execute();

    }

    public void showAlertDialogButtonClicked1() {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention!");
        builder.setMessage("You have to select one or more events before paying.");

        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showAlertDialogButtonClicked2() {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention!");
        builder.setMessage("Make sure to dismiss all the events before going back");

        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkProcess(){
        Call<Conference> call = userService.getConference(getIntent().getStringExtra("idConference"));
        call.enqueue(new Callback<Conference>() {
            @Override
            public void onResponse(Call<Conference> call, Response<Conference> response) {

                checkEventsBack(response.body().getEvents());

            }

            @Override
            public void onFailure(Call<Conference> call, Throwable t) {
                Toast.makeText(JoiningConferenceActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkEventsBack(final List<String> events){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String idActor = sp.getString("Id", "not found");

        Call<List<Event>> call = userService.getOwnEvents(idActor);
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {

                List<Event> events_ = response.body();

                int aux = 0;

                if (events_.isEmpty()){
                    onBraintreeSubmit();
                } else {
                    for (Event e : events_){
                        if (events.contains(e.getId())){
                            aux++;
                        }
                    }
                }

                if (aux > 0) {
                    showAlertDialogButtonClicked2();
                } else {
                    finish();
                }

            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(JoiningConferenceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkEvents(){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String idActor = sp.getString("Id", "not found");

        Call<List<Event>> call = userService.getOwnEvents(idActor);
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {

                if (response.body().isEmpty()){
                    showAlertDialogButtonClicked1();
                } else {
                    onBraintreeSubmit();
                }

            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(JoiningConferenceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        checkProcess();
    }

    public static boolean getStatus(Context context, List<Event> events){
        boolean status = false;
        SharedPreferences sp = context.getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);

        for (Event e : events){
            if (sp.getBoolean("Join " + e.getId() + ", " + sp.getString("Id", "not found"), false)){
                status = true;
                break;
            }
        }

        return status;
    }

    public void showAlertDialogButtonClicked() {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention!");
        builder.setMessage("You have choosed events that overlaps between them, check again and if all is ok go on and click finish");

        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                auxOK = true;
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean checkDates(List<Event> eventsList){
        boolean res = false;

        DateTimeFormatter f = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");

        int i = 0;

        if (eventsList.size() > 1) {
            while(i < eventsList.size()){
                LocalDateTime startTime1 = f.parseLocalDateTime(eventsList.get(i).getStart());
                LocalDateTime endTime1 = f.parseLocalDateTime(eventsList.get(i).getEnd());
                LocalDateTime startTime2 = f.parseLocalDateTime(eventsList.get(i+1).getStart());
                LocalDateTime endTime2 = f.parseLocalDateTime(eventsList.get(i+1).getEnd());

                Interval interval1 = new Interval(startTime1.toDateTime(), endTime1.toDateTime());
                Interval interval2 = new Interval(startTime2.toDateTime(), endTime2.toDateTime());

                if(interval1.overlaps(interval2)){
                    res = true;
                    break;
                }
                i++;
            }

        }

        return res;
    }


    private void loadEvents(){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);

        Intent myIntent = getIntent();
        final String idConference = myIntent.getExtras().get("idConference").toString();

        Call<List<Event>> call = userService.getConferenceEventsUser(idConference, sp.getString("Id", "not found"));
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if(response.isSuccessful()){

                    eventsList = response.body();

                    if(checkDates(eventsList) && !auxOK){
                        showAlertDialogButtonClicked();
                        btnPay.setText("Continue");
                        btnPay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                join();
                            }
                        });
                    } else if (!checkDates(eventsList) && !auxOK){
                        join();
                    } else if (auxOK){
                        join();
                    }

                } else {
                    Toast.makeText(JoiningConferenceActivity.this, "This conference have no events!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(JoiningConferenceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProcess(){
        Intent myIntent = getIntent();
        String idConference = myIntent.getExtras().get("idConference").toString();

        Call<List<Event>> call = userService.getConferenceEventsAllAndOrganizator(idConference);
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if(response.isSuccessful()){

                    eventsList = response.body();

                    if (getStatus(getApplicationContext(), eventsList)){

                    }

                    EventListJoinProcessAdapter adapter = new EventListJoinProcessAdapter(getApplicationContext(), eventsList);
                    final ListView lv = findViewById(R.id.listView);

                    lv.setAdapter(adapter);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(JoiningConferenceActivity.this, ShowEventActivity.class);
                            intent.putExtra("idEvent", eventsList.get(position).getId());
                            startActivity(intent);
                        }
                    });

                } else {
                    Toast.makeText(JoiningConferenceActivity.this, "This conference have no events!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(JoiningConferenceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void join(){

        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);

        Intent myIntent = getIntent();
        final String idConference = myIntent.getExtras().get("idConference").toString();
        
        Call call = userService.addParticipant(idConference, sp.getString("Id", "not found"));
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){

                    Intent intent = new Intent(JoiningConferenceActivity.this, ShowMyConferencesActivity.class);
                    intent.putExtra("idConference", idConference);
                    startActivity(intent);

                } else {
                    Toast.makeText(JoiningConferenceActivity.this.getApplicationContext(), "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(JoiningConferenceActivity.this.getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                String stringNonce = nonce.getNonce();
                Log.d("mylog", "Result: " + stringNonce);
                // Send payment price with the nonce
                // use the result to update your UI and send the payment method nonce to your server
                paramHash = new HashMap<>();
                paramHash.put("amount", amount); //TODO check
                paramHash.put("payment_method_nonce", stringNonce);
                sendPaymentDetails();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
                Log.d("mylog", "user canceled");
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("mylog", "Error : " + error.toString());
            }
        }
    }

    public void onBraintreeSubmit() {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(token);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }

    private void sendPaymentDetails() {
        RequestQueue queue = Volley.newRequestQueue(JoiningConferenceActivity.this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, send_payment_details,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(JoiningConferenceActivity.this, "Transaction successful", Toast.LENGTH_LONG).show();
                        loadEvents();
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("mylog", "Volley error : " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                if (paramHash == null)
                    return null;
                Map<String, String> params = new HashMap<>();
                for (String key : paramHash.keySet()) {
                    params.put(key, paramHash.get(key));
                    Log.d("mylog", "Key : " + key + " Value : " + paramHash.get(key));
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private class HttpRequest extends AsyncTask {
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(JoiningConferenceActivity.this, android.R.style.Theme_DeviceDefault_Dialog);
            progress.setCancelable(false);
            progress.setMessage("We are contacting our servers for token, Please wait");
            progress.setTitle("Getting token");
            progress.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            HttpClient client = new HttpClient();
            client.get(get_token, new HttpResponseCallback() {
                @Override
                public void success(final String responseBody) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(JoiningConferenceActivity.this, "Successfully got token", Toast.LENGTH_SHORT).show();
                            llHolder.setVisibility(View.VISIBLE);
                        }
                    });
                    token = responseBody;
                }

                @Override
                public void failure(Exception exception) {
                    final Exception ex = exception;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(JoiningConferenceActivity.this, "Failed to get token: " + ex.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progress.dismiss();
        }
    }
}
