package com.fristats;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PlayerAdapter extends ArrayAdapter<Player> {
    private Map<String, Integer> rankList = new HashMap<String, Integer>();


    public PlayerAdapter(Context context, ArrayList<Player> users) {
        super(context, 0, users);
        rankList.put("Klipsu I", R.drawable.klipsu1);
        rankList.put("Klipsu II", R.drawable.klipsu2);
        rankList.put("Klipsu III", R.drawable.klipsu3);
        rankList.put("Klipsu IV", R.drawable.klipsu4);
        rankList.put("Klipsu Mestari", R.drawable.klipsu5);
        rankList.put("Klipsu Eliitti Mestari", R.drawable.klipsu6);
        rankList.put("Kultapossu I", R.drawable.gold1);
        rankList.put("Kultapossu II", R.drawable.gold2);
        rankList.put("Kultapossu III", R.drawable.gold3);
        rankList.put("Kultapossu Mestari", R.drawable.gold4);
        rankList.put("Mestari Heittäjä I", R.drawable.mg1);
        rankList.put("Mestari Heittäjä II", R.drawable.mg2);
        rankList.put("Mestari Heittäjä Eliitti", R.drawable.mge);
        rankList.put("Arvostettu Jallu Mestari", R.drawable.jallu);
        rankList.put("Legendaarinen Nalle", R.drawable.nalle);
        rankList.put("Legendaarinen Nalle Mestari", R.drawable.nallemestari);
        rankList.put("Korkein Ykkösluokan Mestari", R.drawable.supreme);
        rankList.put("Urheileva Alkoholisti", R.drawable.global);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position

        Player user = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.player_listview, parent, false);

        }

        // Lookup view for data population

        TextView playerName = (TextView) convertView.findViewById(R.id.listview_player_name);

        TextView playerScore = (TextView) convertView.findViewById(R.id.listview_player_score);

        ImageView rankImage = convertView.findViewById(R.id.playerList_rank);

        // Populate the data into the template view using the data object

        playerName.setText(user.get_name());

        playerScore.setText(String.valueOf(user.get_score()));

        final String tempRank = user.get_rank();
        if(tempRank != "null"){
            if (rankList.containsKey(tempRank)){
                rankImage.setImageResource(rankList.get(tempRank));
                rankImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), tempRank, Toast.LENGTH_SHORT).show();
                    }
                });
                rankImage.setVisibility(View.VISIBLE);
            }
            else{
                rankImage.setVisibility(View.GONE);
            }
            Log.d("RankFinder1", tempRank);
        }
        else{
            Log.d("RankFinder2", tempRank);
            rankImage.setVisibility(View.GONE);
        }



        // Return the completed view to render on screen

        return convertView;

    }

}
