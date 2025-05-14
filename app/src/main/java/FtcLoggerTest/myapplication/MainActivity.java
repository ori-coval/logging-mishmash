package FtcLoggerTest.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.function.DoubleSupplier;

import FtcLoggerTest.myapplication.Logging.AutoLog;
import FtcLoggerTest.myapplication.Logging.AutoLogAndPostToFtcDashboard;
import FtcLoggerTest.myapplication.Logging.AutoLogManager;
import FtcLoggerTest.myapplication.Logging.WpiLog;


@AutoLogAndPostToFtcDashboard
public class MainActivity extends AppCompatActivity {
    boolean isLogging = false;
    // Handler on main thread for scheduling
    final Handler handler = new Handler(Looper.getMainLooper());
    final Handler handlerfunctions = new Handler(Looper.getMainLooper());
    Runnable logRunnable;
    Runnable logRunnableFunction;

    subsystem subsystem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subsystem = new subsystem();

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

        // Define the periodic logging task (every 0.01s)
        logRunnable = new Runnable() {
            @Override
            public void run() {
                subsystem.rotation += Math.PI / 500;
                subsystem.x += Math.random() / 85;
                subsystem.y += Math.random() / 85;
                subsystem.yststic = subsystem.y;
                subsystem.pose = new double[]{subsystem.x, subsystem.y, subsystem.rotation};
                AutoLogManager.periodic();
                if (isLogging) {
                    handler.postDelayed(this, 10);
                }

            }
        };

        // Define the periodic logging task (every 1s)
        logRunnableFunction = new Runnable() {
            @Override
            public void run() {
                test(1, 4);
                subsystem.xsupplier.getAsDouble();

                if (isLogging) {
                    handlerfunctions.postDelayed(this, 1000);
                }

            }
        };
    }

    /**
     * Kick off the repeating log task
     */
    void startLogging() {
        handler.post(logRunnable);
        handlerfunctions.post(logRunnableFunction);
    }

    /**
     * Stop the repeating log task
     */
    void stopLogging() {
        handler.removeCallbacks(logRunnable);
        handlerfunctions.removeCallbacks(logRunnableFunction);
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

    int xsdv = 0;

    public double test(double test, double test2) {
        xsdv++;
        double testValue = 1 + 3 + xsdv + test - test2;
        return testValue + 3;
    }

}
