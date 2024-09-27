package com.example.videoproject;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    private Button playPauseButton;
    private ImageButton fullScreenButton;
    private SeekBar videoSeekBar;
    private boolean isPlaying = false;
    private int currentVideoIndex = 0;
    private boolean isFullScreen = false;
    private FrameLayout videoFrame;
    private final Handler handler = new Handler();

    private final int[] videoResources = {
            R.raw.video1,
            R.raw.video2,
            R.raw.video3,
            R.raw.video4,
            R.raw.video5,
            R.raw.video6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);
        Button beforeButton = findViewById(R.id.beforeButton);
        Button nextButton = findViewById(R.id.nextButton);
        fullScreenButton = findViewById(R.id.fullScreenButton);
        playPauseButton = findViewById(R.id.playPauseButton);
        videoSeekBar = findViewById(R.id.videoSeekBar);
        videoFrame = findViewById(R.id.videoFrame);

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

        beforeButton.setOnClickListener(view -> playPreviousVideo());
        nextButton.setOnClickListener(view -> playNextVideo());
        fullScreenButton.setOnClickListener(view -> toggleFullScreen());

        videoView.setOnCompletionListener(mp -> playNextVideo());

        // SeekBar 위치 = 동영상 재생 위치
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

    private void playPreviousVideo() {
        currentVideoIndex--;
        if (currentVideoIndex < 0) {
            currentVideoIndex = videoResources.length - 1; // 마지막 비디오로 이동
        }

        videoSeekBar.setProgress(0); // SeekBar 초기화
        setVideo(); // 이전 비디오 설정
        videoView.start(); // 자동 재생
        playPauseButton.setText(getString(R.string.pause_text));
        isPlaying = true;
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
        isPlaying = true;
    }

    private void updateSeekBar() {
        if (isPlaying) {
            videoSeekBar.setProgress(videoView.getCurrentPosition());
            handler.postDelayed(this::updateSeekBar, 1000); // 1초마다 업데이트
        }
    }

    private void toggleFullScreen() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoView.getLayoutParams();
        if (isFullScreen) {
            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
            params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            fullScreenButton.setImageResource(R.drawable.fullscreen);
        } else {
            // full
            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
            params.height = FrameLayout.LayoutParams.MATCH_PARENT;
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            fullScreenButton.setImageResource(R.drawable.fullscreen);
        }
        videoView.setLayoutParams(params);
        isFullScreen = !isFullScreen;   // 전체 화면 상태 토글
    }
}