package com.fristats;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameAdapter extends ArrayAdapter<Games.game> {

    private final String TAG = "GameAdapter";

    public GameAdapter(Context context, ArrayList<Games.game> users) {
        super(context, 0, users);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Games.game game = getItem(position);
        //Log.d(TAG, game.get_name());

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.game_listview, parent, false);

        }

        TextView gameName =  convertView.findViewById(R.id.listview_gameName);
        TextView team1 =  convertView.findViewById(R.id.listview_team1);
        TextView team2 =  convertView.findViewById(R.id.listview_team2);
        TextView info =  convertView.findViewById(R.id.listview_game_info);

        gameName.setText(game.get_name());
        List<String> players1 = game.get_players1();
        List<String> players2 = game.get_players2();
        if(players1.size() == 3 && players2.size() == 3) {
            String team = players1.get(0) + "\n" +
                            players1.get(1) + "\n" +
                            players1.get(2);
            team1.setText(team);
            team = players2.get(0) + "\n" +
                    players2.get(1) + "\n" +
                    players2.get(2);
            team2.setText(team);
        }else{
            Log.d(TAG, "gameState0");
            String team = "";
            for(int i = 0; i< players1.size(); i++){
                team = team + players1.get(i) + "\n";
            }
            team1.setText(team);
            team2.setText("");
        }
        info.setText(game.get_team1() + " - " + game.get_team2() + "\n" + game.get_date() + "\n" + game.get_place());


        return convertView;
    }
}
