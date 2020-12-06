package com.example.CarDj.view_player;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.CarDj.R;
import com.example.CarDj.models.audio_view_models.AppState;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

public class PlayerFragment extends Fragment {
    YouTubePlayerView youTubePlayerView;
    OnSongEndListener endSongListener;
    AppState appState;
    private static final String CHOICE = "";


    public static PlayerFragment newInstance(String song){
        PlayerFragment player = new PlayerFragment();
        Bundle songId = new Bundle();
        songId.putString(CHOICE, song);
        player.setArguments(songId);
        return player;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_player, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appState = new ViewModelProvider(requireActivity()).get(AppState.class);
        youTubePlayerView = view.findViewById(R.id.youtube_player_view);
        youTubePlayerView.setEnableAutomaticInitialization(false);
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                youTubePlayer.loadVideo(getArguments().getString(CHOICE), 0);
                appState.setAppState(AppState.GREETING);
                System.out.println("Playing");
            }

            @Override
            public void onStateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlayerState state) {
                super.onStateChange(youTubePlayer, state);
                if(state == PlayerConstants.PlayerState.ENDED){
                    endSongListener.onSongEnd(true);
                }
                if(state == PlayerConstants.PlayerState.PLAYING){
                    appState.getCurrentState().postValue(AppState.GREETING);
                }
            }

            @Override
            public void onVideoLoadedFraction(@NotNull YouTubePlayer youTubePlayer, float loadedFraction) {
                super.onVideoLoadedFraction(youTubePlayer, loadedFraction);

            }

            @Override
            public void onError(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerError error) {
                super.onError(youTubePlayer, error);
                youTubePlayer.loadVideo(getArguments().getString(CHOICE), 0);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnSongEndListener){
            endSongListener = (OnSongEndListener)context;
        }else {
            throw new ClassCastException(context.toString());
        }
    }
    public interface OnSongEndListener{
        void onSongEnd(Boolean songEnded);
    }
}

