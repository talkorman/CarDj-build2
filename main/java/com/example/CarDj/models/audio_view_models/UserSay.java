package com.example.CarDj.models.audio_view_models;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class UserSay extends Fragment {
    private Intent intent;
    public SpeechRecognizer recognizer;
    private Context context;
    private MutableLiveData<String> userSpokenText;
    private StringBuilder voice;
    private StringBuilder searchingString;
    private ImageButton btnUserSay;
    private AppState appState;


    public UserSay(){
        userSpokenText = new MutableLiveData<>();
        voice = new StringBuilder();
        searchingString = new StringBuilder();
        appState = new ViewModelProvider(this).get(AppState.class);
        /*btnUserSay = ((Activity)context).findViewById(R.id.btnUserSay);
        btnUserSay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognizer.startListening(intent);
            }
        });
        setUpSpeachToText();
        */

    }


    public void userSay(){
        recognizer.startListening(intent);
    }

    public void setUpSpeachToText(){
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizer = SpeechRecognizer.createSpeechRecognizer(context);
        recognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(matches != null) {
                    System.out.println("got user speach result");
                    String string = matches.get(0);
                    recognizer.stopListening();
                    //AppState.getInstance().userSpokenText.postValue(string);
                }
            }
            @Override
            public void onReadyForSpeech(Bundle params) {
                System.out.println("User Ready for speach");
            }
            @Override
            public void onBeginningOfSpeech() {
                Log.v("UserSay", "BeginDpeach");;
            }
            @Override
            public void onRmsChanged(float rmsdB) { }
            @Override
            public void onBufferReceived(byte[] buffer) { }
            @Override
            public void onEndOfSpeech() {
                System.out.println("end user speach");
            }
            @Override
            public void onError(int error) {
                System.out.println("user speach error " + error);
                if(error == 1){
                  //  translateSpokenText("");
                }
            }
            @Override
            public void onPartialResults(Bundle partialResults) { }
            @Override
            public void onEvent(int eventType, Bundle params) { }
        });
    }
    public void activateUserSay(boolean b){
        if(b){
            recognizer.startListening(intent);
            return;
        }
        recognizer.stopListening();
    }


}
