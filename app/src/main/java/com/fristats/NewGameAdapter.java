package com.fristats;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewGameAdapter extends ArrayAdapter<Games.game> {

    private final String TAG = "GameAdapter";

    public NewGameAdapter(Context context, ArrayList<Games.game> users) {
        super(context, 0, users);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Games.game game = getItem(position);
        //Log.d(TAG, game.get_name());

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.newgame_listview, parent, false);

        }

        TextView gameName =  convertView.findViewById(R.id.listviewN_gameName);
        TextView team1 =  convertView.findViewById(R.id.listviewN_team1);
        TextView team2 =  convertView.findViewById(R.id.listviewN_team2);
        TextView info =  convertView.findViewById(R.id.listviewN_game_info);
        TextView unfinished =  convertView.findViewById(R.id.listviewN_noTeams);

        gameName.setText(game.get_name());
        List<String> players1 = game.get_players1();
        List<String> players2 = game.get_players2();
        if(players1.size() == 3 && players2.size() == 3 && game.get_state() > 0) {
            String team = players1.get(0) + "\n" +
                    players1.get(1) + "\n" +
                    players1.get(2);
            team1.setText(team);
            team = players2.get(0) + "\n" +
                    players2.get(1) + "\n" +
                    players2.get(2);
            team2.setText(team);
            unfinished.setText("");
            unfinished.setVisibility(View.GONE);
        }else if(players1.size() != 0){
            Log.d(TAG, "gameState0");
            String team = "";
            for(int i = 0; i< players1.size(); i++){
                Log.d(TAG, "added player");
                if(i == players1.size()-1){
                    team = team + players1.get(i);
                } else {
                    team = team + players1.get(i) + " - ";
                }
            }
            Log.d(TAG, team);
            team1.setText("");
            team2.setText("");
            unfinished.setVisibility(View.VISIBLE);
            unfinished.setText(team);
        } else{
            team1.setText("");
            team2.setText("");
            unfinished.setVisibility(View.GONE);
        }
        if(game.get_place().equals("null")){
            info.setText(game.get_team1() + " - " + game.get_team2() + "\n" + game.get_date() + "\n" + "No location data");
        } else {
            info.setText(game.get_team1() + " - " + game.get_team2() + "\n" + game.get_date() + "\n" + game.get_place());
        }



        return convertView;
    }
}
