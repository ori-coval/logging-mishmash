package ori.coval.myapplication;

import android.os.Environment;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataLogManager {
    private static final String LOG_DIR = Environment.getExternalStorageDirectory() + "/Android/data/ori.coval.myapplication/files/FIRST/logs/";
    private static final int MAX_LOG_FILES = 10;

    private static FileOutputStream logStream;
    private static final Map<String, Integer> entryIds = new HashMap<>();
    private static int nextId = 1;

    public static void start() {
        try {
            File dir = new File(LOG_DIR);
            if (!dir.exists()) dir.mkdirs();
            cleanupOldLogs(dir);

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            File logFile = new File(dir, "FTCDataLog_" + timestamp + ".wpilog");
            logStream = new FileOutputStream(logFile);

            // Write WPILOG header
            logStream.write("WPILOG".getBytes());
            logStream.write(new byte[]{0x00, 0x01}); // version 1
            logStream.write(new byte[]{0x00, 0x00, 0x00, 0x00}); // zero extra header size
            logStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void cleanupOldLogs(File dir) {
        File[] files = dir.listFiles((d, name) -> name.endsWith(".wpilog"));
        if (files == null || files.length < MAX_LOG_FILES) return;
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));
        for (int i = 0; i <= files.length - MAX_LOG_FILES; i++) {
            files[i].delete();
        }
    }

    private static void defineEntry(String name, String type) {
        try {
            if (entryIds.containsKey(name)) return;
            int id = nextId++;
            entryIds.put(name, id);

            ByteArrayOutputStream payload = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(payload);
            dos.writeByte(0); // control record type
            dos.writeInt(id);
            byte[] nameBytes = name.getBytes("UTF-8");
            byte[] typeBytes = type.getBytes("UTF-8");
            dos.writeInt(nameBytes.length);
            dos.write(nameBytes);
            dos.writeInt(typeBytes.length);
            dos.write(typeBytes);
            dos.flush();

            byte[] ctrlPayload = payload.toByteArray();
            writeRecord(0, ctrlPayload, System.nanoTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeRecord(int entryId, byte[] payload, long timestamp) throws IOException {
        if (logStream == null) return;
        int sizeLen = 1, idLen = 1, tsLen = 6;
        byte header = (byte) (((tsLen - 1) << 4) | ((sizeLen - 1) << 2) | (idLen - 1));
        logStream.write(header);
        logStream.write(entryId);
        logStream.write(payload.length);
        logStream.write(longToBytes(timestamp, tsLen));
        logStream.write(payload);
        logStream.flush();
    }

    private static byte[] longToBytes(long value, int length) {
        byte[] b = new byte[length];
        for (int i = 0; i < length; i++) {
            b[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return b;
    }

    public static void logDouble(String name, double value) {
        defineEntry(name, "double");
        int id = entryIds.get(name);
        byte[] payload = new byte[8];
        long bits = Double.doubleToLongBits(value);
        for (int i = 7; i >= 0; i--) {
            payload[7 - i] = (byte) ((bits >> (i * 8)) & 0xFF);
        }
        try {
            writeRecord(id, payload, System.nanoTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logInt(String name, long value) {
        defineEntry(name, "int64");
        int id = entryIds.get(name);
        byte[] payload = longToBytes(value, 8);
        try {
            writeRecord(id, payload, System.nanoTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logBoolean(String name, boolean value) {
        defineEntry(name, "boolean");
        int id = entryIds.get(name);
        byte[] payload = new byte[]{(byte) (value ? 1 : 0)};
        try {
            writeRecord(id, payload, System.nanoTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logString(String name, String value) {
        defineEntry(name, "string");
        int id = entryIds.get(name);
        try {
            byte[] payload = value.getBytes("UTF-8");
            writeRecord(id, payload, System.nanoTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        try {
            if (logStream != null) logStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
