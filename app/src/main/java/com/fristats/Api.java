package com.fristats;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fristats.Games.game;

public class Api {

    private String address;
    private String addressP;
    private String addressL;
    private String TAGGAMES = "getGames";
    private String TAGPLAYERS = "getPlayers";
    private String TAGLOCATION = "getLocations";
    private String TAGCreateGame = "postGame";
    private String TAGDELETE = "deleteGame";
    private String TAGADDPLAYER = "addPlayer";
    private String TAGREMOVEPLAYER = "removePlayer";
    private String TAGCREATETEAMS = "createTeams";
    private String TAG = "apiClass";
    private String apiToken;
    RequestQueue mRequestQueue;
    ArrayList<Player> playersAll = new ArrayList<>();
    ArrayList<Games.game> gamesAll = new ArrayList<>();
    ArrayList<Location> locations = new ArrayList<>();
    private MyGameModel model;
    FragmentGameListPager.handleApiCalls callBack = null;



    static {
        System.loadLibrary("native-lib");
    }

    Api(MyGameModel mod, Context context){
        //this.myAdapter = adapt;
        mRequestQueue = Volley.newRequestQueue(context);
        if(BuildConfig.DEBUG){
            this.address = "http://130.231.6.57:8000/API/games/";
            this.addressP = "http://130.231.6.57:8000/API/players/";
            this.addressL = "http://130.231.6.57:8000/API/locations/";
            apiToken = new String(Base64.decode(apiTokenDebug(), Base64.DEFAULT));
            Log.d(TAG, "debug on");
        } else {
            this.address = "https://api.frisbeer.win/API/games/";
            this.addressP = "https://api.frisbeer.win/API/players/";
            this.addressL = "https://api.frisbeer.win/API/locations/";
            //apiToken = new String(Base64.decode(apiTokenRelease(), Base64.DEFAULT));
        }


        try {
            this.callBack = (FragmentGameListPager.handleApiCalls) context;
        } catch (ClassCastException e) {
            Log.d("Error", " must implement OnFragmentInteractionListener");
        }

        this.model = mod;



    }

    public void setCallBack(Context context){
        this.callBack = (FragmentGameListPager.handleApiCalls) context;
    }

    public void getPlayers(final Context context){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, addressP, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("jsonResult", "Got one");
                        playersAll.clear();
                        int id, score;
                        String name, rank;
                        try {
                            for(int i = 0; i < response.length(); i++) {
                                JSONObject playersMain = response.getJSONObject(i);
                                //JSONArray weatherDesc = response.getJSONArray("weather");
                                Log.d("frisbeer players", playersMain.toString());
                                score = playersMain.getInt("score");
                                if(score != 0 || address.contains("8000")) {

                                    id = playersMain.getInt("id");

                                    name = playersMain.getString("name");
                                    if(playersMain.isNull("rank")){
                                        rank = "null";
                                    }else{
                                        JSONObject rankData = playersMain.getJSONObject("rank");
                                        rank = rankData.getString("name");
                                    }

                                    playersAll.add(new Player(id, score, name, rank));
                                }
                            }
                            Log.d("jsonRequst", "Size " + playersAll.size());
                        } catch (JSONException e) {
                            Log.d("jsonResult", "jsonError");
                            Log.d("jsonResult", e.toString());
                        }

                        Log.d("players", "got all players " + playersAll.size());
                        Collections.sort(playersAll);
                        //myAdapter.notifyDataSetChanged();
                        Log.d("players", "sorted all players " + playersAll.size());
                        model.setPlayers(playersAll);

                        for(Player str: playersAll){
                            Log.d("player", str.get_name() + " " + str.get_id());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(context, "Error getting players",
                                Toast.LENGTH_SHORT);
                        toast.show();
                        Log.d("requestResult", error.toString());
                    }
                });
        jsonArrayRequest.setTag(TAGPLAYERS);
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        mRequestQueue.cancelAll(TAGPLAYERS);
        mRequestQueue.add(jsonArrayRequest);

    }


    public void getGames(final Context context){
        gamesAll.clear();


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, address, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("jsonResult", "Got one");
                        //playersAll.clear();
                        int state, team1, team2;
                        String place, date, name;
                        ArrayList<String> players1, players2;
                        try {
                            for(int i = 0; i < response.length(); i++) {
                                players1 = new ArrayList<String>();
                                players2 = new ArrayList<String>();
                                JSONObject gamesMain = response.getJSONObject(i);
                                //JSONArray weatherDesc = response.getJSONArray("weather");
                                Log.d("frisbeerGames", gamesMain.toString());
                                state = gamesMain.getInt("state");
                                int id = gamesMain.getInt("id");
                                date = gamesMain.getString("date");
                                String[] dateParts = date.split("T");
                                String[] timeParts = dateParts[1].split(":");
                                dateParts = dateParts[0].split("-");
                                date = context.getString(R.string.timeFormat, dateParts[2], dateParts[1], dateParts[0], timeParts[0], timeParts[1]);

                                if(true) {
                                    team1 = gamesMain.getInt("team1_score");
                                    team2 = gamesMain.getInt("team2_score");
                                    name = gamesMain.getString("name");
                                    if(!gamesMain.isNull("location_repr")){
                                        place = gamesMain.getJSONObject("location_repr").getString("name");
                                    } else{
                                        place = "null";
                                    }
                                    JSONArray players = gamesMain.getJSONArray("players");
                                    if(state != 0){

                                        JSONObject player;
                                        for(int b = 0; b < players.length(); b++){
                                            player = players.getJSONObject(b);
                                            if(player.getInt("team") == 1){
                                                players1.add(player.getString("name"));
                                            } else{
                                                players2.add(player.getString("name"));
                                            }
                                        }
                                    }else{
                                        JSONObject player;
                                        Log.d("stateZero", String.valueOf(players.length()));
                                        for(int b = 0; b < players.length(); b++){
                                            player = players.getJSONObject(b);
                                            players1.add(player.getString("name"));
                                        }
                                    }
                                    gamesAll.add(new Games().new game(state, team1, team2, place,
                                            date, name, players1, players2, id));

                                    //playersAll.add(new players.player(id, score, name, rank));
                                }
                            }
                            Log.d("jsonRequst", "Size " + String.valueOf(model == null));
                            model.setGamesPlayed(gamesAll);

                        } catch (JSONException e) {
                            Log.d("jsonResult", "jsonError");
                            Log.d("jsonResult", e.toString());
                        }

                        Log.d("gamesPlayed", String.valueOf(gamesAll.size()));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(context, "Error getting games",
                                Toast.LENGTH_SHORT);
                        toast.show();
                        Log.d("requestResult", "no message");
                        Log.d("requestResult", error.toString());
                    }
                });
        jsonArrayRequest.setTag(TAGGAMES);
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        mRequestQueue.cancelAll(TAGGAMES);
        mRequestQueue.add(jsonArrayRequest);

    }

    public void getLocations(final Context context){
        locations.clear();


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, addressL, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("jsonResult", "Got one");
                        //playersAll.clear();
                        try {
                            for(int i = 0; i < response.length(); i++) {
                                JSONObject locationMain = response.getJSONObject(i);
                                //JSONArray weatherDesc = response.getJSONArray("weather");
                                Log.d("frisbeerLocations", locationMain.toString());

                                locations.add(new Location(locationMain.getInt("id"),
                                        locationMain.getString("name")));

                            }
                            model.setLocations(locations);
                            Log.d("jsonRequst", "Size Locations " + locations.size());
                        } catch (JSONException e) {
                            Log.d("jsonResult", "jsonError");
                            Log.d("jsonResult", e.toString());
                        }

                        Log.d("gamesPlayed", String.valueOf(gamesAll.size()));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(context, "Error getting locations",
                                Toast.LENGTH_SHORT);
                        toast.show();
                        Log.d("requestResult", "no message");
                        Log.d("requestResult", error.toString());
                    }
                });
        jsonArrayRequest.setTag(TAGLOCATION);
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        mRequestQueue.cancelAll(TAGLOCATION);
        mRequestQueue.add(jsonArrayRequest);
    }

    public void createGame(final Context context, final String name, final String time, final int location, final NewGameAdapter adap, final ArrayList<Games.game> shortLits){

        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("name", name);
            jsonBody.put("date", time);
            jsonBody.put("location", location);

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, address, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d("postResponse", response.toString());
                        int id = response.getInt("id");
                        String name = response.getString("name");
                        int state = response.getInt("state");
                        int team1 = response.getInt("team1_score");
                        int team2 = response.getInt("team2_score");
                        String date = response.getString("date");
                        String[] dateParts = date.split("T");
                        String[] timeParts = dateParts[1].split(":");
                        dateParts = dateParts[0].split("-");
                        date = context.getString(R.string.timeFormat, dateParts[2], dateParts[1], dateParts[0], timeParts[0], timeParts[1]);
                        String place;
                        if(!response.isNull("location_repr")){
                            place = response.getJSONObject("location_repr").getString("name");
                        } else{
                            place = "null";
                        }
                        ArrayList<String> players1 = new ArrayList<String>();
                        ArrayList<String> players2 = new ArrayList<String>();

                        shortLits.add(0, new Games().new game(state, team1, team2, place,
                                date, name, players1, players2, id));

                        adap.notifyDataSetChanged();
                        callBack.refresh();

                    } catch (JSONException error){
                        Log.d("postResponse", error.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("postResponse", error.toString());
                    callBack.refresh();
                    //onBackPressed();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Token " + apiToken);//put your token here
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            postRequest.setTag(TAGCreateGame);
            postRequest.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            mRequestQueue.cancelAll(TAGCreateGame);
            mRequestQueue.add(postRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_LONG).show();


    }

    public void deleteGame(final Context context, final int gameId){
        //JSONObject jsonBody = new JSONObject();
        String url = address + String.valueOf(gameId) + "/";
        StringRequest dr = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("deleteResponse", response);
                        callBack.refresh();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.
                        Log.d("deleteResponse", error.toString());
                        callBack.refresh();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Token " + apiToken);//put your token here
                headers.put("Content-Type", "application/json");
                return headers;
            }
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                Log.d("deleteResponseS", String.valueOf(mStatusCode));
                callBack.refresh();
                return super.parseNetworkResponse(response);
            }
        };

        dr.setTag(TAGDELETE);
        dr.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        mRequestQueue.cancelAll(TAGDELETE);
        mRequestQueue.add(dr);
        //this.callBack.refresh();

        // Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_LONG).show();
    }

    public void addPlayer(Context context, int playerId, int gameId){
        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("id", playerId);
            String url = address +  String.valueOf(gameId) + "/add_player/";

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("postResponse", response.toString());
                    callBack.refresh();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("postResponse", error.toString());
                    callBack.refresh();
                    //onBackPressed();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Token " + apiToken);//put your token here
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            postRequest.setTag(TAGADDPLAYER);
            postRequest.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            mRequestQueue.cancelAll(TAGADDPLAYER);
            mRequestQueue.add(postRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void removePlayer(Context context, int playerId, int gameId){
        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("id", playerId);
            String url = address +  String.valueOf(gameId) + "/remove_player/";

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("postResponse", response.toString());
                    callBack.refresh();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("postResponse", error.toString());
                    callBack.refresh();
                    //onBackPressed();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Token " + apiToken);//put your token here
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            postRequest.setTag(TAGREMOVEPLAYER);
            postRequest.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            mRequestQueue.cancelAll(TAGREMOVEPLAYER);
            mRequestQueue.add(postRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createTeams(Context context, int gameId){

        JSONObject jsonBody = new JSONObject();

        String url = address +  String.valueOf(gameId) + "/create_teams/";

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("postResponse", response.toString());
                callBack.refresh();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("postResponse", error.toString());
                callBack.refresh();
                //onBackPressed();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Token " + apiToken);//put your token here
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        postRequest.setTag(TAGCREATETEAMS);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        mRequestQueue.cancelAll(TAGCREATETEAMS);
        mRequestQueue.add(postRequest);


    }

    public void reportScore(Context context, int gameId, int team1, int team2){

        try {
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("team1_score", team1);
            jsonBody.put("team2_score", team2);
            jsonBody.put("state", 2);

            String url = address +  String.valueOf(gameId) + "/";

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.PATCH, url, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("postResponse", response.toString());
                    callBack.refresh();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("postResponse", error.toString());

                    //onBackPressed();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Token " + apiToken);//put your token here
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            postRequest.setTag(TAGCREATETEAMS);
            postRequest.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            mRequestQueue.cancelAll(TAGCREATETEAMS);
            mRequestQueue.add(postRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public native String apiTokenDebug();
    public native String apiTokenRelease();
}
