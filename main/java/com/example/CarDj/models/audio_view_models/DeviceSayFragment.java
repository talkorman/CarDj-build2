package com.example.CarDj.models.audio_view_models;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.CarDj.R;
import com.example.CarDj.models.SongData;

import java.util.ArrayList;
import java.util.Locale;

public class DeviceSayFragment extends Fragment {
    private EditText etText;
    private ImageButton btnUserSay;
    private boolean greet = false;
    private TextToSpeech textToSpeech;
    private int resultNumber;
    private ArrayList<SongData> results;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       final View root =  inflater.inflate(R.layout.fragment_device_audio, container, false);
        etText = root.findViewById(R.id.etText);

    return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            AppState appState = new ViewModelProvider(requireActivity()).get(AppState.class);
        textToSpeech = new TextToSpeech(getContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status == TextToSpeech.SUCCESS){
                            int lang = textToSpeech.setLanguage(Locale.US);
                            if(lang==TextToSpeech.LANG_MISSING_DATA||lang==TextToSpeech.LANG_NOT_SUPPORTED){
                                Toast.makeText(getContext(), "Language not supported", Toast.LENGTH_LONG).show();
                            }
                            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                                @Override
                                public void onStart(String utteranceId) {
                                    System.out.println("device speaking");
                                }

                                @Override
                                public void onDone(String utteranceId) {
                                    System.out.println("device complete speak");
                                    int state = appState.getCurrentState().getValue();
                                    System.out.println("state seen from device after device speak:" + state);
                                    if(state == AppState.ASKING_FOR_SEARCH){
                                        appState.getCurrentState().postValue(AppState.SEARCH);
                                        return;
                                    }
                                    if(state == AppState.SHOW_RESULTS || state == AppState.USER_SAID_NO){
                                        resultNumber++;
                                        appState.getCurrentState().postValue(AppState.DEVICE_ALREADY_SAID_RESULT);
                                        System.out.println("device said results in device");
                                        return;
                                    }
                                    if(state == AppState.REPEAT_RESULTS){
                                        appState.getCurrentState().postValue(AppState.SHOW_RESULTS);
                                        appState.getSearchResults().postValue(results);
                                    }
                                    if(resultNumber >= results.size()){
                                        appState.getCurrentState().postValue(AppState.OPENING);
                                    }
                                }

                                @Override
                                public void onError(String utteranceId) {

                                }
                            });
                        }
                    }
                });
        appState.getDeviceTextToSay().observe(getViewLifecycleOwner(), (text)->{
            StringBuilder fullText = new StringBuilder();
            if(appState.getCurrentState().getValue() == AppState.ASKING_FOR_SEARCH){
                fullText.append("I'm going to search for ").append(text);
            }
            speak(fullText.toString());
            etText.setText(text.toString());
        });
        appState.getCurrentState().observe(getViewLifecycleOwner(), (state)->{
            if(state == AppState.GREETING)greeting();
            else if(state == AppState.USER_SPEAK)textToSpeech.stop();
            else if(state == AppState.USER_SAID_NO)sayResults();
            else if(state == AppState.USER_SAID_YES)appState.getSongId().postValue(results.get(resultNumber).getVideoId());
            else if(state == AppState.USER_DIDNT_CHOSE){
                System.out.println("Device know that user didn't chose");
                speak("I repeat");
                appState.getCurrentState().postValue(AppState.REPEAT_RESULTS);
                resultNumber = 0;
            }
        });
        appState.getSearchResults().observe(getViewLifecycleOwner(), (results)->{
            System.out.println("got search result in device");
            this.results = results;
            resultNumber = 0;
            sayResults();
        });

    }
    public void sayResults(){
        System.out.println("device say a result number " + resultNumber);
        if(resultNumber < results.size()){
            String words = results.get(resultNumber).getTitle().substring(0, 30);
            speak(words);
        }else{
            System.out.println("result eccsess the limit");
            speak("please chose again");
        }
    }
    public void speak(String s){
        textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null ,TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
    }
    public void greeting(){
       speak("Hello");
//        speak("Hello, My name is Miri. I will search for you and play your favorate songs. " +
//                "Please just tell me which song do you like and please " +
//                "concentrate on the road. have a safty drive and a pleasent jurney!");
    }
}