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
        videoSeekBar = (SeekBar) findViewById(R.id.videoSeekBar);
        videoSeekBar.setMax(10); // 시크바 최대값 설정
        videoSeekBar.setProgress(3); // 초기 시크바 값 설정


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
        setVideo(); // 이전 비디오 설정
        videoView.start(); // 자동 재생
        playPauseButton.setText(getString(R.string.pause_text));
        isPlaying = true;
    }

    private void setVideo() {
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResources[currentVideoIndex]);
        videoView.setVideoURI(videoUri);

        // 재생 바 길이 설정 (비디오 준비 완료 시)
        videoView.setOnPreparedListener(mp -> {
            videoSeekBar.setMax(videoView.getDuration());
            adjustVideoAspectRatio(mp.getVideoWidth(), mp.getVideoHeight());
        });
    }

    private void adjustVideoAspectRatio(int videoWidth, int videoHeight) {
        float videoProportion = (float) videoWidth / (float) videoHeight;

        int screenWidth = videoFrame.getWidth();
        int screenHeight = videoFrame.getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) videoView.getLayoutParams();
        if (videoProportion > screenProportion)
        {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        }
        else
        {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        videoView.setLayoutParams(lp);
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
        if (isFullScreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoView.getLayoutParams();
            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
            params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            videoView.setLayoutParams(params);

            fullScreenButton.setImageResource(R.drawable.baseline_fullscreen_24);
        } else {
            // full
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoView.getLayoutParams();
            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
            params.height = FrameLayout.LayoutParams.MATCH_PARENT;
            videoView.setLayoutParams(params);

            fullScreenButton.setImageResource(R.drawable.baseline_fullscreen_24);
        }
        isFullScreen = !isFullScreen;   // 전체 화면 상태 토글
    }
}