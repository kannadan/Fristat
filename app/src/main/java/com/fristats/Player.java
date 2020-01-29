package com.fristats;

public class Player implements Comparable<Player>, Cloneable{
    private int _id, _score;
    private String _name, _rank;

    @Override
    public int compareTo(Player comparePlayers) {
        int compareScore = comparePlayers.get_score();
        /* For Ascending order*/
        //return this._score - compareScore;

        /* For Descending order do like this */
        return compareScore - this._score;
    }

    @Override
    public Object clone() {
        Player clone = null;
        try
        {
            clone = (Player) super.clone();

        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
        return clone;
    }


    Player(int id, int score, String name, String rank){
        _id = id;
        _score = score;
        _name = name;
        _rank = rank;
    }



    public int get_id() {
        return _id;
    }

    public String get_name() {
        return _name;
    }

    public String get_rank() {
        return _rank;
    }

    public int get_score() { return _score; }
}
