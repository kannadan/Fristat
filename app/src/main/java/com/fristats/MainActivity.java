package com.fristats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    Dialog myDialog;
    private MyGameModel model;
    Api myApiHandler;
    ArrayList<Player> playersAll = new ArrayList<>();
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "UserPrefs";
    public static final String user = "UserName";
    public static final String userId = "UserId";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDialog = new Dialog(this);
        model = ViewModelProviders.of(this).get(MyGameModel.class);
        myApiHandler = new Api(model, this);
        myApiHandler.getPlayers(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Button sets = findViewById(R.id.settings);
        sets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.setContentView(R.layout.settings_pop);
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //Log.d("mainModel", model.getPlayers().getValue().toString());
                String savedName = sharedpreferences.getString(user, "");

                Button setUser = myDialog.findViewById(R.id.setUser);
                final EditText et = myDialog.findViewById(R.id.userNameSet);
                et.setText(savedName);
                setUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<Player> players = model.getPlayers().getValue();
                        Iterator<Player> iterator = players.iterator();
                        String pickedName = et.getText().toString();
                        Boolean found = false;
                        while(iterator.hasNext())
                        {
                            //Add the object clones
                            Player tempplayer = (Player) iterator.next().clone();
                            if (tempplayer.get_name().equals(pickedName)){
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(user, pickedName);
                                editor.putInt(userId, tempplayer.get_id());
                                editor.apply();
                                found = true;
                                break;
                            } else if (pickedName.equals("")) {
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(user, pickedName);
                                editor.putInt(userId, 0);
                                editor.apply();
                                found = true;
                                break;
                            }
                        }
                        if(found)
                        {
                            myDialog.dismiss();
                        } else{
                            et.setText("");
                            Toast toast = Toast.makeText(MainActivity.this, "No such username",
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }


                    }
                });



                myDialog.show();
            }
        });
    }

    public void openPlayers(View v){
        startActivity(new Intent(MainActivity.this, players.class));
    }

    public void openGames(View v){
        startActivity(new Intent(MainActivity.this, Games.class));
    }


}
