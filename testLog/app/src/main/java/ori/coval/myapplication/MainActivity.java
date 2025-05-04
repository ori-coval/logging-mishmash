package ori.coval.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.Random;

import android.os.Handler;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple app that periodically logs primitives into .wpilog.
 */
public class MainActivity extends AppCompatActivity {
    private static final Logger log = LoggerFactory.getLogger(MainActivity.class);
    private boolean logging = false;
    private Handler handler = new Handler();
    private Runnable tick;
    private Random rng = new Random();
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // request storage permission if needed
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btnToggle);

        btn.setOnClickListener(v -> {
            if (!logging) startLogging();
            else stopLogging();
        });

        // prepare a tick that logs some random values each 200ms
        tick = new Runnable() {
            @Override
            public void run() {
                // log primitives
                DataLogManager.logDouble("RndDouble", rng.nextDouble());
                DataLogManager.logInt("RndInt", rng.nextInt(100));
                DataLogManager.logBoolean("RndBool", rng.nextBoolean());
                DataLogManager.logString("Status", logging ? "ON" : "OFF");
                // schedule next
                if (logging) handler.postDelayed(this, 200);
            }
        };
    }

    private void startLogging() {
        // ensure log folder exists
        File f = new File(Environment.getExternalStorageDirectory(), "FIRST/logs");
        if (!f.exists()) f.mkdirs();
        DataLogManager.start();

        logging = true;
        btn.setText("Stop Logging");
        handler.post(tick);
    }

    private void stopLogging() {
        logging = false;
        btn.setText("Start Logging");
        // logs flushed on each write, no further action needed
    }
}
