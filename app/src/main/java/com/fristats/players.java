package com.fristats;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class players extends AppCompatActivity {


    public static final String TAG = "requestTag";
    RequestQueue mRequestQueue;
    ArrayList<Player> playersAll = new ArrayList<>();
    PlayerAdapter myAdapter;
    Api myApiHandler;
    MyGameModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        myAdapter = new PlayerAdapter(this, playersAll);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(myAdapter);
        final EditText nameField = findViewById(R.id.nameField);

        model = ViewModelProviders.of(this).get(MyGameModel.class);

        model.getPlayers().observe(this, new Observer<ArrayList<Player>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Player> users) {
                playersAll.clear();
                Iterator<Player> iterator = users.iterator();
                while(iterator.hasNext())
                {
                    //Add the object clones
                    Player tempPlayer = (Player) iterator.next().clone();
                    playersAll.add(tempPlayer);
                }

                Log.d(TAG, "playerListReceived " + String.valueOf(playersAll.size()));
                if(playersAll.size() != 0) {
                    myAdapter.notifyDataSetChanged();
                }
            }
        });

        myApiHandler = new Api(model, getApplicationContext());
        myApiHandler.getPlayers(getApplicationContext());
        //getPlayers();
        Button button = findViewById(R.id.findPlayerBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                findUser(nameField.getText().toString());
                nameField.setText("");
            }
        });
        Button button2 = findViewById(R.id.refreshBtn);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myApiHandler.getPlayers(getApplicationContext());
            }
        });
    }

    public void findUser(String userName){
        ListView listView = findViewById(R.id.list_view);
        userName = userName.toLowerCase();
        if(userName.equals("")){
            return;
        }
        for(int i = 0; i < playersAll.size(); i++){
            if (playersAll.get(i).get_name().toLowerCase().equals(userName)){
                listView.smoothScrollToPositionFromTop(i, 0);
                return;
            }
        }
        Toast.makeText(this, R.string.noName, Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onStop () {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

}
