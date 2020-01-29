package com.fristats;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FragmentStatViewPager extends Fragment {
    private MyGameModel model;
    private ArrayList<Games.game> allGames = new ArrayList<>();
    private ArrayList<PlayerStats> statsList = new ArrayList<>();
    private ArrayList<Player> players = new ArrayList<>();
    private String TAG = "StatView";
    StatAdapter myAdapter;
    final int startYear = 2017;

    public static Fragment newInstance() {
        FragmentStatViewPager f = new FragmentStatViewPager();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.MyThemeStat);
        inflater = inflater.cloneInContext(contextThemeWrapper);
        View v = inflater.inflate(R.layout.game_stat_fragment, container, false);
        //myStats = new PlayerStats("kannadan");

        //spinner setup
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        ArrayList<String> items = new ArrayList<>();
        for(int i = currentYear; i >= startYear; i--){
            items.add(String.valueOf(i));
        }

        items.add(getString(R.string.all));
        final Spinner dropdown = v.findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, items);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(allGames.size() != 0 && players.size() != 0) {
                    String res = parent.getItemAtPosition(position).toString();
                    int year = 0;
                    if(res == getString(R.string.all)){
                        year = 0;
                    }
                    else{
                        year = Integer.valueOf(res);
                    }
                    statsList.clear();
                    updateStats(year);
                    myAdapter.notifyDataSetChanged();
                }else{
                    Log.d(TAG, "skippign");
                    parent.setSelection(0);
                    Toast.makeText(getContext(), getString(R.string.noRes), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });


        model = ViewModelProviders.of(getActivity()).get(MyGameModel.class);
        model.getGamesPlayed().observe(this, new Observer<ArrayList<Games.game>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Games.game> gameList) {
                allGames.clear();
                allGames = gameList;
                Log.d(TAG, "gameListReceived " + String.valueOf(allGames.size()));
                if(allGames.size() != 0 && players.size() != 0) {
                    updateStats(2019);
                    myAdapter.notifyDataSetChanged();
                    Log.d(TAG, String.valueOf(statsList.size()));
                }
            }
        });
        model.getPlayers().observe(this, new Observer<ArrayList<Player>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Player> users) {
                players.clear();
                players = users;
                Log.d(TAG, "playerListReceived " + String.valueOf(players.size()));
                if(allGames.size() != 0 && players.size() != 0) {
                    updateStats(2019);
                    myAdapter.notifyDataSetChanged();
                    Log.d(TAG, String.valueOf(statsList.size()));
                }
            }
        });
        myAdapter = new StatAdapter(getContext(), statsList);
        ListView listView = v.findViewById(R.id.list_view_stats);
        listView.setAdapter(myAdapter);
        return v;
    }

    public void updateStats(int year){
        Iterator<Player> iteratorP = players.iterator();
        //allGames.clear();
        List<String> player1, player2;
        PlayerStats myStats;
        //Log.d(TAG, "playerSize in update " + players.size());
        //Log.d(TAG, "gameSize in update " + allGames.size());
        //Log.d(TAG, "statsSize in update " + statsList.size());
        while(iteratorP.hasNext()) {
            Player player = iteratorP.next();
            //Log.d(TAG, player.get_name());
            myStats = new PlayerStats(player.get_name());
            Iterator<Games.game> iterator = allGames.iterator();
            //allGames.clear();
            while (iterator.hasNext()) {
                //Add the object clones
                Games.game iterGame = iterator.next();
                if(iterGame.get_state() != 3){
                    continue;
                }
                String date = iterGame.get_date();
                String[] dateParts = date.split(" ");
                dateParts = dateParts[0].split("\\.");
                int gameYear = Integer.valueOf(dateParts[2]);
                if (gameYear == year || year == 0) {
                    String userName = myStats.getPlayerName();
                    player1 = iterGame.get_players1();
                    if (player1.contains(userName)) {
                        myStats.reportGame(iterGame.get_team1(), iterGame.get_team2());
                    } else {
                        player2 = iterGame.get_players2();
                        if (player2.contains(userName)) {
                            myStats.reportGame(iterGame.get_team2(), iterGame.get_team1());
                        }
                    }
                }else{
                    //Log.d(TAG, String.valueOf(gameYear));
                    continue;
                }


            }
            statsList.add(myStats);
        }
        Collections.sort(statsList);
    }

    public class PlayerStats implements Comparable<PlayerStats>{
        private String playerName;
        private int gamesPlayed = 0;
        private int gamesWon = 0;
        private int roundsPlayed = 0;
        private int roundsWon = 0;
        private int gamesSecond = 0;
        private int gamesSecondWon = 0;
        private int gamesThirdWon = 0;

        PlayerStats(String name){playerName = name;}

        public void reportGame(int myScore, int oponentScore){
            gamesPlayed++;
            switch (myScore){
                case 0:
                    roundsPlayed = roundsPlayed + 2;
                    gamesSecond++;
                    break;
                case 1:
                    roundsPlayed = roundsPlayed +3;
                    roundsWon++;
                    break;
                case 2:
                    if(oponentScore == 0){
                        roundsPlayed = roundsPlayed + 2;
                        roundsWon = roundsWon + 2;
                        gamesWon++;
                        gamesSecond++;
                        gamesSecondWon++;
                    } else if(oponentScore == 1) {
                        roundsPlayed = roundsPlayed + 3;
                        roundsWon = roundsWon + 2;
                        gamesWon++;
                        gamesThirdWon++;
                    }
                    break;
                default:
                    break;
            }
        }

        public String getPlayerName() {
            return playerName;
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        public int getGamesPlayed() {
            return gamesPlayed;
        }

        public void setGamesPlayed(int gamesPlayed) {
            this.gamesPlayed = gamesPlayed;
        }

        public int getGamesWon() {
            return gamesWon;
        }

        public void setGamesWon(int gamesWon) {
            this.gamesWon = gamesWon;
        }

        public int getRoundsPlayed() {
            return roundsPlayed;
        }

        public void setRoundsPlayed(int roundsPlayed) {
            this.roundsPlayed = roundsPlayed;
        }

        public int getRoundsWon() {
            return roundsWon;
        }

        public void setRoundsWon(int roundsWon) {
            this.roundsWon = roundsWon;
        }

        public int getGamesSecond() {
            return gamesSecond;
        }

        public void setGamesSecond(int gamesSecond) {
            this.gamesSecond = gamesSecond;
        }

        public int getGamesSecondWon() {
            return gamesSecondWon;
        }

        public void setGamesSecondWon(int gamesSecondWon) {
            this.gamesSecondWon = gamesSecondWon;
        }

        public int getGamesThirdWon() {
            return gamesThirdWon;
        }

        public void setGamesThirdWon(int gamesThirdWon) {
            this.gamesThirdWon = gamesThirdWon;
        }

        @Override
        public int compareTo(PlayerStats playerStats) {
            int compareScore = playerStats.getGamesPlayed();
            /* For Ascending order*/
            //return this._score - compareScore;

            /* For Descending order do like this */
            return compareScore - this.gamesPlayed;
        }
    }
}
