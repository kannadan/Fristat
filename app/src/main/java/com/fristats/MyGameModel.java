package com.fristats;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class MyGameModel extends ViewModel {

    private MutableLiveData<ArrayList<Games.game>> gamesPlayed = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Player>> players = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Location>> locations = new MutableLiveData<>();

    public MutableLiveData<ArrayList<Games.game>> getGamesPlayed(){
        if(gamesPlayed.getValue() == null){
            gamesPlayed.setValue(new ArrayList<Games.game>());
        }
        return gamesPlayed;
    }
    public MutableLiveData<ArrayList<Player>> getPlayers(){
        if(players.getValue() == null){
            players.setValue(new ArrayList<Player>());
        }
        return players;
    }

    public MutableLiveData<ArrayList<Location>> getLocations(){
        if(locations.getValue() == null){
            locations.setValue(new ArrayList<Location>());
        }
        return locations;
    }

    public void setGamesPlayed(ArrayList<Games.game> games){
        gamesPlayed.setValue(games);
    }

    public void setPlayers(ArrayList<Player> users){
        players.setValue(users);
    }

    public void setLocations(ArrayList<Location> locs){
        locations.setValue(locs);
    }
}
