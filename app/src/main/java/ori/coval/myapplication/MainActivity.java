package ori.coval.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.function.DoubleSupplier;

public class MainActivity extends AppCompatActivity {
    @NoLog
    private static final String LOG_FILENAME = "test.wpilog";
    private boolean isLogging = false;

    // Handler on main thread for scheduling
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable logRunnable;
    TelemetryManager tm;

    private double rotation = 0;
    private double x = 0;
    private double y = 0;
    private double[] pose = new double[3];

    public static double yststic = 0;

    DoubleSupplier xsupplier = SupplierLog.wrap("xSupplier",()->x);
    

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

        // Optional: share button to pull the file
        Button shareBtn = new Button(this);
        shareBtn.setText("Share Log File");
        shareBtn.setOnClickListener(v -> shareLogFile());
        layout.addView(shareBtn);

        setContentView(layout);

        // Initialize the logger singleton
        try {
            File outFile = new File(
                    getExternalFilesDir(null),
                    LOG_FILENAME
            );
            WpiLog.getInstance().setup(outFile, "Android Continuous Test");
            Toast.makeText(this,
                    "Logger initialized at:\n" + outFile.getAbsolutePath(),
                    Toast.LENGTH_LONG
            ).show();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to init logger", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        // Define the periodic logging task (every 1s)
        logRunnable = new Runnable() {
            @Override
            public void run() {
                try {
//                    double rnd = Math.random() * 100;
//                    WpiLog.getInstance().log("randomDouble", rnd);
                    rotation+=Math.PI/1000;
                    x += Math.random()/8.5;
                    y += Math.random()/8.5;
                    yststic = y;
                    pose = new double[]{x,y,rotation};
                    xsupplier.getAsDouble();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isLogging) {
                    handler.postDelayed(this, 1);
                }
            }
        };


        tm = new TelemetryManager(100); // log every 100ms
        tm.register(this);
        tm.start();
    }

    /** Kick off the repeating log task */
    private void startLogging() {
        handler.post(logRunnable);
    }

    /** Stop the repeating log task */
    private void stopLogging() {
        handler.removeCallbacks(logRunnable);
        tm.stop();
        try {
            WpiLog.getInstance().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Fire a share Intent for the .wpilog file */
    private void shareLogFile() {
        File file = new File(getExternalFilesDir(null), LOG_FILENAME);
        Uri uri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".fileprovider",
                file
        );
        Intent share = new Intent(Intent.ACTION_SEND)
                .setType("*/*")
                .putExtra(Intent.EXTRA_STREAM, uri)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(share, "Share WPILOG"));
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
