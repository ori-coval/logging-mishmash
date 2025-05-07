package FtcLoggerTest.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.function.DoubleSupplier;

public class MainActivity extends AppCompatActivity {
    private boolean isLogging = false;

    // Handler on main thread for scheduling
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable logRunnable;

    @NoLog
    private double dontLogTest = 4;
    private double rotation = 0;
    private double x = 0;
    private double y = 0;
    private double[] pose = new double[3];

    public static double yststic = 0;

    DoubleSupplier xsupplier = SupplierLog.wrap("xSupplier", () -> x);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vertical layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Toggle button for start/stop logging
        Button toggleBtn = new Button(this);
        toggleBtn.setText("Start Logging");
        toggleBtn.setOnClickListener(v -> {
            if (!isLogging) {
                isLogging = true;
                toggleBtn.setText("Stop Logging");
                startLogging();
            } else {
                isLogging = false;
                toggleBtn.setText("Start Logging");
                stopLogging();
            }
        });
        layout.addView(toggleBtn);

        setContentView(layout);

        // Define the periodic logging task (every 1s)
        logRunnable = new Runnable() {
            @Override
            public void run() {
                try {
//                    double rnd = Math.random() * 100;
//                    WpiLog.getInstance().log("randomDouble", rnd);
                    rotation += Math.PI / 500;
                    x += Math.random() / 85;
                    y += Math.random() / 85;
                    yststic = y;
                    pose = new double[]{x, y, rotation};
                    xsupplier.getAsDouble();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isLogging) {
                    handler.postDelayed(this, 10);
                }
            }
        };

        WpiLog.getInstance().setup(this.getApplicationContext());

        TelemetryManager.getInstance().register(this);
        TelemetryManager.getInstance().start();
    }

    /**
     * Kick off the repeating log task
     */
    private void startLogging() {
        handler.post(logRunnable);
    }

    /**
     * Stop the repeating log task
     */
    private void stopLogging() {
        handler.removeCallbacks(logRunnable);
        TelemetryManager.getInstance().stop();
        try {
            WpiLog.getInstance().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLogging();
        try {
            WpiLog.getInstance().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
