package ori.coval.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    private WpiLog logger;
    private boolean isRunning = false;
    private Button button;
    private Handler handler = new Handler();
    Runnable runnableTask;
    double rotation = 0;
    double startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.btnToggle);

        startTime = System.currentTimeMillis()/1000.0;

        runnableTask = new Runnable() {
            @Override
            public void run() {

                WpiLog.getInstance().log("time", System.currentTimeMillis()/1000);
                rotation += startTime - System.currentTimeMillis()/1000.0;
                WpiLog.getInstance().log("pose", new double[]{2,2,rotation});

                if(isRunning){
                    handler.postDelayed(this,500);
                }
            }
        };

        button.setOnClickListener(v -> {
            Toast.makeText(this, "Logged current time", Toast.LENGTH_SHORT).show();

            if(isRunning){
                isRunning = false;
                button.setText("start logging");
            }
            else {
                isRunning = true;
                button.setText("stop Logging");
                handler.post(runnableTask);
            }
        });


        try {
            File file = new File(getExternalFilesDir(null), "test.wpilog");
            WpiLog.getInstance().setup(file, "Android WpiLog Test");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Logger init failed", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}