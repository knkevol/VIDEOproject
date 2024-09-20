package com.example.videoproject;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    private Button playPauseButton;
    private SeekBar videoSeekBar;
    private boolean isPlaying = false;
    private int currentVideoIndex = 0;
    private final Handler handler = new Handler();

    // raw 리소스에 있는 비디오 파일 ID
    private final int[] videoResources = {
            R.raw.video1,
            R.raw.video2,
            R.raw.video3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);
        playPauseButton = findViewById(R.id.playPauseButton);
        videoSeekBar = findViewById(R.id.videoSeekBar);

        // Play/Pause 버튼 동작 설정
        playPauseButton.setOnClickListener(view -> {
            if (isPlaying) {
                videoView.pause();
                playPauseButton.setText(getString(R.string.play_text));
            } else {
                videoView.start();
                playPauseButton.setText(getString(R.string.pause_text));
                updateSeekBar();
            }
            isPlaying = !isPlaying;
        });

        videoView.setOnCompletionListener(mp -> playNextVideo());

        // SeekBar 변경 시 동영상 재생 위치 변경
        videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // 동영상은 자동으로 시작되지 않고 버튼을 눌러야 재생됨
        setVideo();
    }

    private void setVideo() {
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResources[currentVideoIndex]);
        videoView.setVideoURI(videoUri);

        // 재생 바 길이 설정 (비디오 준비 완료 시)
        videoView.setOnPreparedListener(mp -> videoSeekBar.setMax(videoView.getDuration()));
    }

    private void playNextVideo() {
        currentVideoIndex++;
        if (currentVideoIndex >= videoResources.length) {
            currentVideoIndex = 0;
        }

        videoSeekBar.setProgress(0);

        setVideo();
        videoView.start();
 //       playPauseButton.setText(getString(R.string.play_text));
        isPlaying = true;
    }

    // SeekBar를 1초마다 업데이트
    private void updateSeekBar() {
        if (isPlaying) {
            videoSeekBar.setProgress(videoView.getCurrentPosition());
            handler.postDelayed(this::updateSeekBar, 1000); // 1초마다 업데이트
        }
    }
}