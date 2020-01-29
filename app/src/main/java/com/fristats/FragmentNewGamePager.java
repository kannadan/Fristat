package com.fristats;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static com.fristats.MainActivity.MyPREFERENCES;
import static com.fristats.MainActivity.user;
import static com.fristats.MainActivity.userId;

public class FragmentNewGamePager extends Fragment {

    NewGameAdapter myAdapter;
    ArrayList<Games.game> allGames = new ArrayList<>();
    private String TAG = "newGameTag";
    private MyGameModel model;
    Dialog myDialog;
    Api myApiHandler;
    private int mYear, mMonth, mDay, mHour, mMinute;
    SharedPreferences sharedpreferences;

    public static Fragment newInstance() {
        FragmentNewGamePager f = new FragmentNewGamePager();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_game_fragment, container, false);

        myAdapter = new NewGameAdapter(v.getContext(), allGames);
        ListView listView = v.findViewById(R.id.list_view_newgames);
        listView.setAdapter(myAdapter);
        myDialog = new Dialog(getContext());
        myApiHandler = new Api(model, getContext());
        sharedpreferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        model = ViewModelProviders.of(getActivity()).get(MyGameModel.class);
        model.getGamesPlayed().observe(this, new Observer<ArrayList<Games.game>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Games.game> gameList) {
                Iterator<Games.game> iterator = gameList.iterator();
                allGames.clear();
                while(iterator.hasNext())
                {
                    //Add the object clones
                    Games.game tempGame = (Games.game) iterator.next().clone();
                    if(tempGame.get_state() < 3){
                        allGames.add(tempGame);
                    }

                }
                Log.d(TAG, "received games " + String.valueOf(allGames.size()));
                myAdapter.notifyDataSetChanged();
            }
        });

        Button btn = v.findViewById(R.id.newGameBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.setContentView(R.layout.newgame_pop);
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                ArrayList<String> items = new ArrayList<>();
                ArrayList<Location> locs = model.getLocations().getValue();

                Iterator<Location> iterator = locs.iterator();
                while(iterator.hasNext())
                {
                    items.add(iterator.next().getName());
                }


                final Spinner dropdown = myDialog.findViewById(R.id.spinnerLocations);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, items);
                dropdown.setAdapter(adapter);

                Button dateBtn = myDialog.findViewById(R.id.dateBtn);
                Button timeBtn = myDialog.findViewById(R.id.timeBtn);
                dateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Get Current Date
                        if(mYear == 0) {
                            final Calendar c = Calendar.getInstance();
                            mYear = c.get(Calendar.YEAR);
                            mMonth = c.get(Calendar.MONTH);
                            mDay = c.get(Calendar.DAY_OF_MONTH);
                        }

                        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        mYear = year;
                                        mMonth = monthOfYear;
                                        mDay = dayOfMonth;
                                        Log.d("datePicker", String.valueOf(dayOfMonth));
                                       // txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                    }
                                }, mYear, mMonth, mDay);

                        datePickerDialog.show();
                        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).
                                setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).
                                setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                });

                timeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Get Current Time

                        Log.d("calendar", String.valueOf(mHour));
                        if(mHour == 0){
                            final Calendar c = Calendar.getInstance();
                            mHour = c.get(Calendar.HOUR_OF_DAY);
                            mMinute = c.get(Calendar.MINUTE);
                        }


                        // Launch Time Picker Dialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {
                                        mMinute = minute;
                                        mHour = hourOfDay;
                                        Log.d("timePicker", String.valueOf(hourOfDay));
                                        //txtTime.setText(hourOfDay + ":" + minute);
                                    }
                                }, mHour, mMinute, true);
                        timePickerDialog.show();

                        timePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).
                                setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).
                                setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                    });

                Button submit = myDialog.findViewById(R.id.submitGameBtn);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("submitGame", String.valueOf(mMinute));
                        EditText text = myDialog.findViewById(R.id.gameName);
                        String name = text.getText().toString();
                        int location = dropdown.getSelectedItemPosition() + 1;
                        Log.d("postRequest", name);
                        Log.d("postRequest", String.valueOf(location));

                        final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";

                        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
                        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Calendar today = Calendar.getInstance();
                        today.set(Calendar.YEAR, mYear);
                        today.set(Calendar.MONTH, mMonth);
                        today.set(Calendar.DAY_OF_MONTH, mDay);
                        today.set(Calendar.HOUR_OF_DAY, mHour);
                        today.set(Calendar.MINUTE, mMinute);
                        String timeF = dateFormat.format(today.getTime());

                        Log.d("postRequest", timeF);

                        myApiHandler.createGame(getContext(), name, timeF, location, myAdapter, allGames);

                        //myApiHandler.getGames(getContext());
                        myDialog.dismiss();
                    }
                });

                myDialog.show();

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int position, long l) {
                final Games.game tempGame = (Games.game)adapterView.getAdapter().getItem(position);

                final String nameP;
                final int idP;
                nameP = sharedpreferences.getString(user, null);
                idP = sharedpreferences.getInt(userId, 0);
                Log.d("sharedName", "shared name is: " + nameP);
                Log.d("sharedName", String.valueOf(idP));


                myDialog.setContentView(R.layout.gameinfo_pop);
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView gameName = myDialog.findViewById(R.id.gameNameI);
                TextView gameInfo = myDialog.findViewById(R.id.gameInfo);
                TextView gamePlayers = myDialog.findViewById(R.id.playersINfo);
                Button joinBtn = myDialog.findViewById(R.id.joinBtn);
                Button deleteBtn = myDialog.findViewById(R.id.deleteBtn);
                LinearLayout layout = myDialog.findViewById(R.id.resultsLayout);

                if((tempGame.get_state() != 0 || tempGame.get_players1().size() == 6) &&
                        !tempGame.get_players1().contains(nameP) &&
                        !tempGame.get_players2().contains(nameP)){
                    joinBtn.setVisibility(View.GONE);
                    TextView temp = myDialog.findViewById(R.id.noRoomNotice);
                    temp.setVisibility(View.VISIBLE);
                } else if ((tempGame.get_players1().contains(nameP) || tempGame.get_players2().contains(nameP)
                            ) && tempGame.get_state() == 0){
                    joinBtn.setVisibility(View.VISIBLE);
                    joinBtn.setText(getContext().getResources().getString(R.string.leave));
                } else if(tempGame.get_state() > 1){
                    joinBtn.setVisibility(View.GONE);
                    layout.setVisibility(View.GONE);
                    TextView temp = myDialog.findViewById(R.id.noRoomNotice);
                    temp.setText(getContext().getResources().getString(R.string.approve));
                    temp.setVisibility(View.VISIBLE);
                } else if (tempGame.get_state() == 0 &&
                        !tempGame.get_players1().contains(nameP) &&
                        !tempGame.get_players2().contains(nameP) && tempGame.get_players1().size() != 6) {

                            joinBtn.setVisibility(View.VISIBLE);
                            joinBtn.setText(getContext().getResources().getString(R.string.join));
                            layout.setVisibility(View.GONE);
                } else {
                    joinBtn.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                }

                if(tempGame.get_state() != 1 && (tempGame.get_players1().contains(nameP) ||
                        tempGame.get_players2().contains(nameP)) && tempGame.get_players1().size() == 6){
                    Log.d("deleateVisible", "is");
                    deleteBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setText(getContext().getResources().getString(R.string.teams));
                } else if(tempGame.get_players1().size() < 6 && tempGame.get_players1().size() != 0){
                    deleteBtn.setVisibility(View.GONE);

                } else if(tempGame.get_players1().size() == 6 && !tempGame.get_players1().contains(nameP)){
                    deleteBtn.setVisibility(View.GONE);
                } else {
                    deleteBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setText(getContext().getResources().getString(R.string.delete));
                }

                if(nameP == null){
                    joinBtn.setVisibility(View.GONE);
                    deleteBtn.setVisibility(View.GONE);
                    Toast toast = Toast.makeText(getContext(), "You have no username",
                            Toast.LENGTH_SHORT);
                    toast.show();

                }

                gameName.setText(tempGame.get_name());

                if(tempGame.get_place().equals("null")){
                    gameInfo.setText(tempGame.get_team1() + " - " + tempGame.get_team2() + "\n" + tempGame.get_date() + "\n" + "No location data");
                } else {
                    gameInfo.setText(tempGame.get_team1() + " - " + tempGame.get_team2() + "\n" + tempGame.get_date() + "\n" + tempGame.get_place());
                }
                String players = "";
                int i;
                if(tempGame.get_players2().size() != 0){
                    for(i = 0; i < tempGame.get_players1().size(); i++){
                        players = players + tempGame.get_players1().get(i) + " ";
                    }
                    players = players + "VS ";
                    for(i = 0; i < tempGame.get_players2().size(); i++){
                        players = players + tempGame.get_players2().get(i) + " ";
                    }
                } else {
                    for(i = 0; i < tempGame.get_players1().size(); i++){
                        players = players + tempGame.get_players1().get(i) + " ";
                    }
                }
                players = players.trim();
                gamePlayers.setText(players);
                if(tempGame.get_players1().size() != 0){
                    //deleteBtn.setVisibility(View.GONE);
                    gamePlayers.setVisibility(View.VISIBLE);
                    Log.d("listviewClick", "invisible");
                } else {
                    //deleteBtn.setVisibility(View.VISIBLE);
                    gamePlayers.setVisibility(View.GONE);
                }
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(tempGame.get_state() == 0 && tempGame.get_players1().size() == 0) {
                            myApiHandler.deleteGame(getContext(), tempGame.get_id());

                            allGames.remove(tempGame);
                            myAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("deleteButton", "creating teams");
                            myApiHandler.createTeams(getContext(), tempGame.get_id());
                        }
                        myDialog.dismiss();
                    }
                });
                joinBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        List<String> players1 = tempGame.get_players1();
                        List<String> players2 = tempGame.get_players2();
                        int state = tempGame.get_state();

                        if(state == 0){
                            if(!players1.contains(nameP) && !players2.contains(nameP)){
                                myApiHandler.addPlayer(getContext(), idP, tempGame.get_id());
                                players1.add(nameP);
                                allGames.remove(tempGame);
                                tempGame.set_players1(players1);
                                allGames.add(position, tempGame);
                                myAdapter.notifyDataSetChanged();
                            } else{
                                myApiHandler.removePlayer(getContext(), idP, tempGame.get_id());
                                players1.remove(nameP);
                                allGames.remove(tempGame);
                                tempGame.set_players1(players1);
                                allGames.add(position, tempGame);
                                myAdapter.notifyDataSetChanged();
                            }
                        }
                        myDialog.dismiss();
                    }
                });

                Button res1 = myDialog.findViewById(R.id.result1);
                Button res2 = myDialog.findViewById(R.id.result2);
                Button res3 = myDialog.findViewById(R.id.result3);
                Button res4 = myDialog.findViewById(R.id.result4);

                res1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myApiHandler.reportScore(getContext(), tempGame.get_id(), 2, 0);
                        myDialog.dismiss();
                    }
                });

                res2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myApiHandler.reportScore(getContext(), tempGame.get_id(), 2, 1);
                        myDialog.dismiss();
                    }
                });

                res3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myApiHandler.reportScore(getContext(), tempGame.get_id(), 0, 2);
                        myDialog.dismiss();
                    }
                });

                res4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myApiHandler.reportScore(getContext(), tempGame.get_id(), 1, 2);
                        myDialog.dismiss();
                    }
                });

                myDialog.show();
            }
        });
        return v;

    }
}

