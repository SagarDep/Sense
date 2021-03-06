package in.sensemusic.sense;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;

public class PlayerFragment extends Fragment {

    MediaPlayer mediaPlayer = new MediaPlayer();
    long currentSongID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle SongInfo = getArguments();

        TextView NowPlayingTrack,Album,Artist,Duration;
        ImageView AlbumArt;

        NowPlayingTrack = getActivity().findViewById(R.id.song);
        Album = getActivity().findViewById(R.id.album);
        Artist = getActivity().findViewById(R.id.artist);
        Duration = getActivity().findViewById(R.id.time_duration);
        AlbumArt = getActivity().findViewById(R.id.album_art);


        if (SongInfo != null) {
            currentSongID = SongInfo.getLong(MediaStore.Audio.Media._ID);
            NowPlayingTrack.setText(SongInfo.getString(MediaStore.Audio.Media.TITLE));
            Artist.setText(SongInfo.getString(MediaStore.Audio.Media.ARTIST));
            Album.setText(SongInfo.getString(MediaStore.Audio.Media.ALBUM));
            Duration.setText(SongInfo.getString(MediaStore.Audio.Media.DURATION));

            Glide
                    .with(getContext())
                    .load(SongInfo.getString(MediaStore.Audio.Albums.ALBUM_ART))
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.album_art)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                    )
                    .transition(new DrawableTransitionOptions()
                            .crossFade()
                    )
                    .into(AlbumArt);

            //play song
            playSong(currentSongID);
        }
        else {
            NowPlayingTrack.setText("Now Playing");
            Artist.setText("Artist");
            Album.setText("Album");
            Duration.setText("00:00");

            Glide
                    .with(getContext()).load("")
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.album_art)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                    )
                    .transition(new DrawableTransitionOptions()
                            .crossFade()
                    )
                    .into(AlbumArt);

        }
       // Log.e("Sense", currentSongID+" currentSongID");
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set Action Bar title
        ((MainActivity) getActivity()).setActionBarTitle("Player");
        // ((MainActivity) getActivity()).getSupportActionBar().setTitle("Player");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Stop Play when paused
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void playSong(long currSong){

        // prepare uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);

        //set player properties
        mediaPlayer.setWakeMode(getActivity().getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //play a song from start
        mediaPlayer.reset();

        //set source
        try{
            mediaPlayer.setDataSource(getActivity().getApplicationContext(), trackUri);
        } catch(Exception e){
            Log.e("sense", "Error setting data source", e);
        }

        //prepare
        try{
            mediaPlayer.prepare();
        }catch(IOException e){
            Log.e("sense", "Error at mediaplayer prepare", e);}

        //start play
        mediaPlayer.start();
    }

}
