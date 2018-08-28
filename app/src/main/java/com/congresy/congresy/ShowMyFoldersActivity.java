package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.congresy.congresy.domain.Folder;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowMyFoldersActivity extends BaseActivity {

    UserService userService;

    ListView list;

    Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Folders");
        loadDrawer(R.layout.activity_show_my_folders);

        userService = ApiUtils.getUserService();

        create = findViewById(R.id.create);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowMyFoldersActivity.this, CreateMessageActivity.class);
                intent.putExtra("comeFrom", "create");
                intent.putExtra("received", "0");
                startActivity(intent);
            }
        });

        loadMyFolders();
    }

    private void loadMyFolders(){
        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
        String id = sp.getString("Id", "not found");

        Call<List<Folder>> call = userService.getFoldersOfActor(id);
        call.enqueue(new Callback<List<Folder>>() {
            @Override
            public void onResponse(Call<List<Folder>> call, Response<List<Folder>> response) {

                final List<Folder> folders = response.body();

                ListView lv = findViewById(R.id.listView);

                ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1, folders);

                lv.setAdapter(adapter);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("folderName", folders.get(position).getName());
                        editor.apply();


                        Intent intent = new Intent(ShowMyFoldersActivity.this, ShowMessagesOfFolderActivity.class);
                        intent.putExtra("idFolder", folders.get(position).getId());

                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Folder>> call, Throwable t) {
                Toast.makeText(ShowMyFoldersActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
