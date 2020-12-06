package com.example.CarDj.models.audio_view_models;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.CarDj.models.SongData;
import com.example.CarDj.models.SongsDataResults;
import com.example.CarDj.models.play_lists.SongsDataSource;

import java.util.ArrayList;

public class AppState extends ViewModel {
    public static final int
            GREETING = 18,                ASKING_FOR_SEARCH = 1,             SEARCH = 2,
            SHOW_RESULTS = 3,            DEVICE_SAY_RESULT = 4,   USER_ALREADY_SAID_RESULT = 5,
            USER_SAY_RESULT_NUMBER = 6,  USER_SAID_YES = 7,       USER_SAID_NO = 8,
            USER_DIDNT_CHOSE = 9,        ADDTO_PLAYLIST = 10,     DISPLAY_PLAYLIST = 11,
            DISPLAYING_SONG = 12,        REJECT = 13,             REPEAT_RESULTS = 14,
            DELETE_ALL = 15,             SONG_ENDED = 16,         DEVICE_ALREADY_SAID_RESULT = 17,
            OPENING = 19,                USER_SPEAK = 20,          USER_FINISH_SPEAK = 21;
    int countSayResults;
    private DeviceSayFragment userUi;
    private final MutableLiveData<Integer> currentState = new MutableLiveData<>();
    private final MutableLiveData<String> searchWords = new MutableLiveData<>();
    private final MutableLiveData<String> userSpokenText = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<SongData>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<StringBuilder> deviceTextToSay = new MutableLiveData<>();
    private final MutableLiveData<String> songId = new MutableLiveData<>();
    private String wordsText;
    public int chosenResult;
    private SongsDataSource dataSource;
    Context context;

    public MutableLiveData<String> getSongId(){ return songId;}

    public String getWordsText() {
        return wordsText;
    }
    public MutableLiveData<String> getSearchWords(){ return searchWords;}
    public int getChosenResult() {
        return chosenResult;
    }

    public SongsDataSource getDataSource() {
        return dataSource;
    }
    public MutableLiveData<StringBuilder>getDeviceTextToSay() {
        return deviceTextToSay;
    }

    public MutableLiveData<String> getUserSpokenText() {
        return userSpokenText;
    }

    public MutableLiveData<ArrayList<SongData>> getSearchResults(){ return searchResults;}


    public void setAppState(int state) {
        this.currentState.postValue(state);;
    }


    public MutableLiveData<Integer> getCurrentState(){
        return currentState;
    }
    public void getWordsText(MutableLiveData<String> callback){
        callback.postValue(wordsText);
    }

    public void tellOptions(ArrayList<SongData> songs){
        int delay = countSayResults == 0 ? 5000 : 10;
        Handler h = new Handler();
        Runnable delayBeforeDeviceSay = new Runnable() {
            @Override
            public void run() {
                String words = songs.get(countSayResults).getTitle();
                String wordsToSpeak = words.length() >= 30 ? words.substring(0, 30) : words;
                System.out.println("listen");
                currentState.postValue(DEVICE_SAY_RESULT);
            }
        };
        h.postDelayed(delayBeforeDeviceSay, delay);
    }

}
