package com.shiv.mediaplayer;

import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaPlayer media;
    private SeekBar mSeekBar;
    private ImageView artistImage;
    private TextView leftTime;
    private TextView rightTime;
    private Button prevButton;
    private Button playButton;
    private Button nextButton;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpUI();
        mSeekBar.setMax(media.getDuration());

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    media.seekTo(progress);
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                int currentPos = media.getCurrentPosition();
                int duration = media.getDuration();


                leftTime.setText(dateFormat.format(new Date(currentPos)));
                rightTime.setText(dateFormat.format(new Date(duration - currentPos)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setUpUI() {

        media = new MediaPlayer();
        media = MediaPlayer.create(getApplicationContext(), R.raw.a);

        artistImage = (ImageView) findViewById(R.id.imageView);
        leftTime = (TextView) findViewById(R.id.leftTime);
        rightTime = (TextView) findViewById(R.id.rightTime);
        mSeekBar = (SeekBar) findViewById(R.id.mSeekBar);
        prevButton = (Button) findViewById(R.id.prevButton);
        playButton = (Button) findViewById(R.id.playButton);
        nextButton = (Button) findViewById(R.id.nextButton);

        /** setting context **/
        prevButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.prevButton:
                backMusic();
                break;

            case R.id.playButton:
                if(media.isPlaying()){
                    pauseMusic();
                } else {
                    startMusic();
                }
                break;

            case R.id.nextButton:
                nextMusic();
                break;
        }
    }

    public void pauseMusic() {
        if(media != null) {
            media.pause();
            playButton.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }

    public void startMusic() {
        if(media != null){
            media.start();
            updateThread();
            playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
        }
    }

    public void backMusic() {
        if(media != null) {
            media.seekTo(0);
        }
    }

    public void nextMusic() {
        if(media != null) {
            media.seekTo(media.getDuration() - 1000);
            playButton.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }

    /** updating thread for the seekBar **/
    public void updateThread() {

        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(media != null && media.isPlaying()) {
                        Thread.sleep(50);
                        /** Updating the UI **/
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int newPosition = media.getCurrentPosition();
                                int newMax = media.getDuration();

                                mSeekBar.setMax(newMax);
                                mSeekBar.setProgress(newPosition);

                                /** update the time dynamically **/
                                leftTime.setText(String.valueOf (
                                        new java.text.SimpleDateFormat("mm:ss")
                                                .format(new Date(media.getCurrentPosition()))));

                                rightTime.setText(String.valueOf (
                                        new java.text.SimpleDateFormat("mm:ss")
                                                .format(new Date(media.getDuration() - media.getCurrentPosition()))));
                            }
                        });
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        /** Start the thread **/
        thread.start();
    }

    @Override
    protected void onDestroy() {
        if(media != null && media.isPlaying()){
            media.stop();
            media.release();
            media = null;
        }
        super.onDestroy();
    }
}
