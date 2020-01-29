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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StatAdapter extends ArrayAdapter<FragmentStatViewPager.PlayerStats> {
    private Map<Integer, Integer> textList = new HashMap<Integer, Integer>();

    public StatAdapter(Context context, ArrayList<FragmentStatViewPager.PlayerStats> stats){
        super(context, 0, stats);
        textList.put(0, R.id.stat_text_1);
        textList.put(1, R.id.stat_text_2);
        textList.put(2, R.id.stat_text_3);
        textList.put(3, R.id.stat_text_4);
        textList.put(4, R.id.stat_text_5);
        textList.put(5, R.id.stat_text_6);
        textList.put(6, R.id.stat_text_7);
        textList.put(7, R.id.stat_text_8);
        textList.put(8, R.id.stat_text_9);
        textList.put(9, R.id.stat_text_10);
        textList.put(10, R.id.stat_text_11);
        textList.put(11, R.id.stat_text_12);
        textList.put(12, R.id.stat_text_13);
        textList.put(13, R.id.stat_text_14);
        textList.put(14, R.id.stat_text_15);
        textList.put(15, R.id.stat_text_16);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position

        FragmentStatViewPager.PlayerStats stat = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.stat_listview, parent, false);

        }
        TextView playerName = (TextView) convertView.findViewById(R.id.stat_name);
        playerName.setText(stat.getPlayerName());
        playerName.setTextSize(32);
        TextView fieldName;
        TextView fieldValue;
        Context context = getContext();
        for(int i = 0; i < 8; i++) {
            fieldName = (TextView) convertView.findViewById(textList.get(i*2));
            fieldValue = (TextView) convertView.findViewById(textList.get(i*2+1));
            switch (i) {

                case 0:
                    fieldName.setText(context.getResources().getString(R.string.gamesPlayed));
                    fieldValue.setText(context.getResources().getString(R.string.gamesPlayedValue, stat.getGamesPlayed()));
                    break;
                case 1:
                    fieldName.setText(context.getResources().getString(R.string.gamesWon));
                    fieldValue.setText(context.getResources().getString(R.string.gamesWonValue, stat.getGamesWon(), (float) stat.getGamesWon() / stat.getGamesPlayed() * 100));
                    break;
                case 2:
                    fieldName.setText(context.getResources().getString(R.string.roundsPlayed));
                    fieldValue.setText(context.getResources().getString(R.string.roundsPlayedValue, stat.getRoundsPlayed()));
                    break;
                case 3:
                    fieldName.setText(context.getResources().getString(R.string.roundsWon));
                    fieldValue.setText(context.getResources().getString(R.string.roundsWonValue, stat.getRoundsWon(), (float) stat.getRoundsWon() / stat.getRoundsPlayed() * 100));
                    break;
                case 4:
                    fieldName.setText(context.getResources().getString(R.string.gameSecond));
                    fieldValue.setText(context.getResources().getString(R.string.gameSecondValue, stat.getGamesSecond()));
                    break;
                case 5:
                    fieldName.setText(context.getResources().getString(R.string.gameSecondWon));
                    fieldValue.setText(context.getResources().getString(R.string.gameSecondWonValue, stat.getGamesSecondWon(), (float) stat.getGamesSecondWon() / stat.getGamesWon() * 100));
                    break;
                case 6:
                    fieldName.setText(context.getResources().getString(R.string.gameThird));
                    fieldValue.setText(context.getResources().getString(R.string.gameThirdValue, stat.getGamesPlayed() - stat.getGamesSecond()));
                    break;
                case 7:
                    fieldName.setText(context.getResources().getString(R.string.gameThirdWon));
                    fieldValue.setText(context.getResources().getString(R.string.gameThirdWonValue, stat.getGamesThirdWon(), (float) stat.getGamesThirdWon() / stat.getGamesWon() * 100));
                    break;
                default:
                    fieldName.setText("null");
                    fieldValue.setText("null");
            }
            fieldName.setTextSize(18);
            fieldValue.setTextSize(18);
        }

        return convertView;

    }

}
