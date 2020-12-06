package com.example.CarDj;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.CarDj.models.*;
import com.example.CarDj.models.audio_view_models.AppState;
import com.example.CarDj.models.audio_view_models.DeviceSayFragment;
import com.example.CarDj.models.audio_view_models.UserSay;
import com.example.CarDj.view_play_list.*;
import com.example.CarDj.view_player.*;
import com.example.CarDj.view_search_results.*;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PlayerFragment.OnSongEndListener {
    //properties declarations
    AppState appState;
    int state;
    boolean userDidChoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        //properties setup
        appState = new ViewModelProvider(this).get(AppState.class);
        appState.setAppState(AppState.OPENING);
        appState.getCurrentState().observe(this, (state)->{
            System.out.println("state status: " + state);
            if(state == AppState.SEARCH)showSearchResults();
            if(state == AppState.USER_SAID_NO || state == AppState.USER_SAID_YES)userDidChoose = true;
            if(state == AppState.DEVICE_ALREADY_SAID_RESULT){
                userDidChoose = false;
                int delay = 5000;
                Handler h = new Handler();
                Runnable resultTimeWindow = new Runnable() {
                    @Override
                    public void run() {
                        if(state == AppState.DEVICE_ALREADY_SAID_RESULT && !userDidChoose) {
                            System.out.println("main knows that user didn't chose");
                            appState.getCurrentState().postValue(AppState.USER_DIDNT_CHOSE);
                        }
                    }
                };
                h.postDelayed(resultTimeWindow, delay);
            }
        });
        appState.getSongId().observe(this, id->displaySong(id));



        //userSay = UserSay.getInstance();
        //deviceSay = DeviceSay.getInstance();
        /*
       appState.getAppState().observe(this, (state)->{
            if(state == AppState.DISPLAYING_SONG){
                String song = appState.resultsObjects.get(appState.chosenResult).getVideoId();
                displaySong(song);
            }
            if(state == AppState.DISPLAY_PLAYLIST){
                showPlayList();
            }
            if(state == AppState.SHOW_RESULTS){
                showSearchResults(appState.resultsObjects);
            }
        });
*/
        showPlayList();
       // displaySong("tH2rgPqi8Ag");
    }



    public void displaySong(String song){
        closePlayer();
        PlayerFragment player = PlayerFragment.newInstance(song);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.youtubeplayerfragment, player).commit();
    }


    @Override
    public void onSongEnd(Boolean songEnded) {
        //appState.;
        closePlayer();
    }

    public void closePlayer(){
        FragmentManager fm = getSupportFragmentManager();
        PlayerFragment player = (PlayerFragment)fm.findFragmentById(R.id.youtubeplayerfragment);
        if(player != null){
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(player).commit();
        }
    }
    public void closeSearchResults(){
        FragmentManager fm = getSupportFragmentManager();
        ResultsFragment resultsFragment = (ResultsFragment) fm.findFragmentById(R.id.results_view);
        if(resultsFragment != null){
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(resultsFragment).commit();
        }
    }
    public void showSearchResults(){
        System.out.println("show results");
        closeSearchResults();
        ResultsFragment resultsFragment = ResultsFragment.newInstance();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.results_view, resultsFragment).commit();
    }

    public void showPlayList(){
        PlayListFragment playListFragment = PlayListFragment.newInstance();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.play_list_view, playListFragment).commit();
    }



}