package com.example.b5_th5;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView timeTextView;
    private Button startButton, stopButton;
    private Handler handler;
    private int elapsedTime = 0;
    private boolean isRunning = false;
    private Thread timerThread;

    // Runnable để cập nhật UI
    private final Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            timeTextView.setText("Time: " + elapsedTime + "s");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeTextView = findViewById(R.id.timeTextView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        // Khởi tạo Handler gắn với UI thread
        handler = new Handler(Looper.getMainLooper());

        startButton.setOnClickListener(v -> startTimer());
        stopButton.setOnClickListener(v -> stopTimer());
    }

    private void startTimer() {
        if (!isRunning) {
            isRunning = true;
            startButton.setEnabled(false);
            stopButton.setEnabled(true);

            // Tạo thread nền để đếm thời gian
            timerThread = new Thread(() -> {
                while (isRunning) {
                    try {
                        Thread.sleep(1000); // Ngủ 1 giây
                        elapsedTime++;
                        // Gửi Runnable đến UI thread để cập nhật TextView
                        handler.post(updateTimeRunnable);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            });
            timerThread.start();
        }
    }

    private void stopTimer() {
        if (isRunning) {
            isRunning = false;
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            // Không cần dừng thread thủ công vì vòng lặp sẽ tự thoát khi isRunning = false
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đảm bảo dừng thread khi activity bị hủy
        isRunning = false;
        if (timerThread != null && timerThread.isAlive()) {
            timerThread.interrupt();
        }
        // Xóa các Runnable đang chờ trong Handler
        handler.removeCallbacks(updateTimeRunnable);
    }
}