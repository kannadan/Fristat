package com.fristats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class Games extends AppCompatActivity implements FragmentGameListPager.handleApiCalls{

    private String address, addressP;

    public static final String TAG = "requestTag";
    public static final String TAG2 = "requestPTag";
    RequestQueue mRequestQueue;
    ArrayList<game> gamesAll = new ArrayList<>();
    ArrayList<Player> playersAll = new ArrayList<>();
    private MyGameModel model;
    ViewPager pager;
    Api myApiHandler;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(1);
        model = ViewModelProviders.of(this).get(MyGameModel.class);
        mRequestQueue = Volley.newRequestQueue(this);
        //getPlayers();
        //getGames();
        myApiHandler = new Api(model, this);
        myApiHandler.getPlayers(getApplicationContext());
        myApiHandler.getGames(getApplicationContext());
        myApiHandler.getLocations(getApplicationContext());
        //myApiHandler.createGame(getApplicationContext(), "hello world from android",
          //      "2019-08-07T17:00:00.000000Z", 1);
        myApiHandler.deleteGame(getApplicationContext(), 38);
    }


    @Override
    protected void onStop () {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

    @Override
    public void refresh() {
        myApiHandler.getGames(getApplicationContext());
        myApiHandler.getPlayers(getApplicationContext());
        myApiHandler.getLocations(getApplicationContext());
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                    return FragmentNewGamePager.newInstance();
                case 1:
                    return FragmentGameListPager.newInstance();
                case 2:
                    return FragmentStatViewPager.newInstance();
                default:
                    return FragmentGameListPager.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }


    public class game implements Cloneable{
        int _state, _team1, _team2, _id;

        public int get_id() {
            return _id;
        }

        public void set_id(int _id) {
            this._id = _id;
        }

        String _place, _date, _name;
        List<String> _players1, _players2;

        game(int state, int team1, int team2, String place, String date, String name,
             ArrayList<String> players1, ArrayList<String> players2, int id){
            _state = state;
            _team1 = team1;
            _team2 = team2;
            _place = place;
            _date = date;
            _name = name;
            _players1 = players1;
            _players2 = players2;
            _id = id;
        }

        @Override
        public Object clone() {
            game clone = null;
            try
            {
                clone = (game) super.clone();

            }
            catch (CloneNotSupportedException e)
            {
                throw new RuntimeException(e);
            }
            return clone;
        }



        public int get_state() {
            return _state;
        }

        public int get_team1() {
            return _team1;
        }

        public int get_team2() {
            return _team2;
        }

        public String get_place() {
            return _place;
        }

        public String get_date() {
            return _date;
        }

        public String get_name() {
            return _name;
        }

        public void set_name(String _name) {
            this._name = _name;
        }

        public List<String> get_players1() {
            return _players1;
        }

        public void set_players1(List<String> _players1) {
            this._players1 = _players1;
        }

        public List<String> get_players2() {
            return _players2;
        }
    }
}
