package FtcLoggerTest.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import org.littletonrobotics.junction.AutoLog;

import java.io.IOException;
import java.util.function.DoubleSupplier;

@AutoLog
public class MainActivity extends AppCompatActivity {
    boolean isLogging = false;

    // Handler on main thread for scheduling
    final Handler handler = new Handler(Looper.getMainLooper());
    final Handler handlerfunctions = new Handler(Looper.getMainLooper());
    Runnable logRunnable;
    Runnable logRunnableFunction;

    double dontLogTest = 4;
    double rotation = 0;
    double x = 0;
    double y = 0;
    double[] pose = new double[3];

    public static double yststic = 0;

    DoubleSupplier xsupplier = () -> x;


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
                    ((MainActivityAutoLogged) MainActivity.this).toLog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isLogging) {
                    handler.postDelayed(this, 10);
                }

            }
        };

        logRunnableFunction = new Runnable() {
            @Override
            public void run() {
                try {
                    test();
                    ((MainActivityAutoLogged) MainActivity.this).toLog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isLogging) {
                    handlerfunctions.postDelayed(this, 1000);
                }

            }
        };

        WpiLog.getInstance().setup(this.getApplicationContext());

//        TelemetryManager.getInstance().register(this);
//        TelemetryManager.getInstance().start();
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

    int xsdv = 0;

    public double test() {
        xsdv++;
        double testValue = 1 + 3 + xsdv;
        return testValue + 3;
    }

}
