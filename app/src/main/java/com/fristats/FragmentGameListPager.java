package com.fristats;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class FragmentGameListPager extends Fragment {
    final String TAG = "FragmentLogs";
    private MyGameModel model;
    ArrayList<Games.game> allGames = new ArrayList<>();
    ArrayList<Games.game> showGames = new ArrayList<>();
    GameAdapter myAdapter;
    final int startYear = 2017;
    TextView gameCounter;

    public interface handleApiCalls {
        void refresh();
    }

    handleApiCalls callback = null;

    @Override
    public void onAttach(Context context) {
        try {
            this.callback = (handleApiCalls) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnFragmentInteractionListener");
        }
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gamelist_fragment, container, false);
        final Activity activity = getActivity();
        myAdapter = new GameAdapter(v.getContext(), showGames);
        ListView listView = v.findViewById(R.id.list_view_games);
        listView.setAdapter(myAdapter);
        gameCounter = v.findViewById(R.id.gamesPlayed);
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
                    if(tempGame.get_state() != 3){
                        continue;
                    }
                    allGames.add(tempGame);
                }
                iterator = allGames.iterator();
                showGames.clear();
                while(iterator.hasNext())
                {
                    //Add the object clones
                    showGames.add((Games.game) iterator.next().clone());
                }
                Log.d(TAG, "received games " + String.valueOf(allGames.size()));
                myAdapter.notifyDataSetChanged();
                gameCounter.setText(getString(R.string.gameCounter, allGames.size()));
            }
        });


        Button button = v.findViewById(R.id.searchGame);
        Button button2 = v.findViewById(R.id.refreshBtnGame);
        gameCounter = v.findViewById(R.id.gamesPlayed);
        final EditText nameText = v.findViewById(R.id.editTextGame);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        ArrayList<String> items = new ArrayList<>();
        items.add(getString(R.string.all));
        for(int i = currentYear; i >= startYear; i--){
            items.add(String.valueOf(i));
        }

        final Spinner dropdown = v.findViewById(R.id.spinner1);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, items);
        dropdown.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

                String text = nameText.getText().toString();
                //nameText.setText("");
                if(allGames.size() != 0) {
                    Log.d("gamesPlayed", text);
                    myAdapter.notifyDataSetChanged();
                    String selectedYear = dropdown.getSelectedItem().toString();
                    int year;
                    if (selectedYear == getString(R.string.all)) {
                        year = 0;
                    } else {
                        year = Integer.valueOf(selectedYear);
                    }
                    showUsersGames(text, year);
                } else{
                    Toast.makeText(activity.getApplicationContext(), R.string.noGame, Toast.LENGTH_SHORT).show();
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allGames.clear();
                showGames.clear();
                myAdapter.notifyDataSetChanged();
                //getGames();
                callback.refresh();
                Log.d(TAG, "ReFresh");
            }
        });


        return v;
    }

    public static Fragment newInstance() {
        FragmentGameListPager f = new FragmentGameListPager();
        return f;
    }
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    public void showUsersGames(String userName, int year){

        if(userName.equals("") && year == 0){
            showGames.clear();
            Iterator<Games.game> iterator = allGames.iterator();

            while(iterator.hasNext())
            {
                //Add the object clones
                Games.game tempGame = (Games.game) iterator.next().clone();
                if(tempGame.get_state() != 3){
                    continue;
                }
                showGames.add(tempGame);
            }
        } else{

            showGames.clear();
            Iterator<Games.game> iterator = allGames.iterator();
            Games.game currentGame;
            List<String> players1, players2;
            Boolean approve;
            String date;
            while(iterator.hasNext())
            {
                currentGame = (Games.game) iterator.next().clone();
                if(currentGame.get_state() != 3){
                    continue;
                }
                Log.d("SearchingGames", String.valueOf(year) + " " + userName);
                approve = false;
                date = currentGame.get_date();
                String[] dateParts = date.split(" ");
                dateParts = dateParts[0].split("\\.");
                int gameYear = Integer.valueOf(dateParts[2]);
                if(currentGame.get_state() != 0) {
                    if (year == 0 || year == gameYear) {
                        if (!userName.equals("")) {
                            players1 = currentGame.get_players1();
                            players2 = currentGame.get_players2();
                            for (int i = 0; i < 3; i++) {
                                if (players1.get(i).toString().toLowerCase().equals(userName.toLowerCase()) || players2.get(i).toString().toLowerCase().equals(userName.toLowerCase())) {
                                    approve = true;
                                    break;
                                }
                            }
                        } else {
                            approve = true;
                        }
                    }
                } else{
                    Log.d(TAG, currentGame.get_players1().toString());
                }

                if(approve){showGames.add(currentGame);}
            }

        }
        gameCounter.setText(getString(R.string.gameCounter, showGames.size()));
        myAdapter.notifyDataSetChanged();

    }
}
