package ori.coval.myapplication;

import java.io.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * WpiLog: write WPILOG-format files for Advantage Scope.
 * Supports scalar and array data types.
 */
public class WpiLog implements Closeable {
    private FileOutputStream fos;
    private final HashMap<String, Integer> recordIDs;
    private int largestId = 0;
    private static WpiLog instance = null;

    public static synchronized WpiLog getInstance() {
        if (instance == null)
            instance = new WpiLog();

        return instance;
    }

    private WpiLog() {
        recordIDs = new HashMap<>();
    }

    public void setup(File file, String extraHeader) throws IOException {
        fos = new FileOutputStream(file);
        writeHeader(extraHeader != null ? extraHeader : "");
    }

    private void writeHeader(String extra) throws IOException {
        fos.write("WPILOG".getBytes(StandardCharsets.US_ASCII));
        fos.write(le16((short) 0x0100));               // version 1.0
        byte[] eb = extra.getBytes(StandardCharsets.UTF_8);
        fos.write(le32(eb.length));                    // extra-header length
        fos.write(eb);                                  // extra-header data
    }

    @Override
    public void close() throws IOException {
        fos.close();
    }

    // ─── Control records ─────────────────────────────────────────────────────
    private void startEntry(int entryId, String name, String type, long ts) throws IOException {
        ByteArrayOutputStream bb = new ByteArrayOutputStream();
        bb.write(0);
        bb.write(le32(entryId));
        bb.write(le32(name.length()));
        bb.write(name.getBytes(StandardCharsets.UTF_8));
        bb.write(le32(type.length()));
        bb.write(type.getBytes(StandardCharsets.UTF_8));
        bb.write(le32("".length()));
        bb.write("".getBytes(StandardCharsets.UTF_8));
        writeRecord(0, bb.toByteArray(), ts);
    }

    private void finishEntry(int entryId, long ts) throws IOException {
        ByteArrayOutputStream bb = new ByteArrayOutputStream();
        bb.write(1);
        bb.write(le32(entryId));
        writeRecord(0, bb.toByteArray(), ts);
    }

    private void setMetadata(int entryId, String metadata, long ts) throws IOException {
        ByteArrayOutputStream bb = new ByteArrayOutputStream();
        bb.write(2);
        bb.write(le32(entryId));
        bb.write(le32(metadata.length()));
        bb.write(metadata.getBytes(StandardCharsets.UTF_8));
        writeRecord(0, bb.toByteArray(), ts);
    }

    // ─── Low-level record writer ─────────────────────────────────────────────
    private void writeRecord(int entryId, byte[] payload, long ts) throws IOException {
        fos.write(0x7F);               // header: 4B id,4B size,8B timestamp
        fos.write(le32(entryId));
        fos.write(le32(payload.length));
        fos.write(le64(ts));
        fos.write(payload);
    }

    // ─── public logging ──────────────────────────────────────────────────────

    public void log(String name, boolean value){
        if (!recordIDs.containsKey(name)) {
            try {
                startEntry(getID(name), name, "boolean", nowMicros());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            logBoolean(getID(name), value, nowMicros());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String name, long value){
        if (!recordIDs.containsKey(name)) {
            try {
                startEntry(getID(name), name, "int64", nowMicros());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            logInt64(getID(name), value, nowMicros());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String name, float value){
        if (!recordIDs.containsKey(name)) {
            try {
                startEntry(getID(name), name, "float", nowMicros());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            logFloat(getID(name), value, nowMicros());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String name, double value){
        if (!recordIDs.containsKey(name)) {
            try {
                startEntry(getID(name), name, "double", nowMicros());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            logDouble(getID(name), value, nowMicros());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String name, String value){
        if (!recordIDs.containsKey(name)) {
            try {
                startEntry(getID(name), name, "string", nowMicros());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            logString(getID(name), value, nowMicros());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String name, boolean[] value){
        if (!recordIDs.containsKey(name)) {
            try {
                startEntry(getID(name), name, "boolean[]", nowMicros());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            logBooleanArray(getID(name), value, nowMicros());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String name, long[] value){
        if (!recordIDs.containsKey(name)) {
            try {
                startEntry(getID(name), name, "int64[]", nowMicros());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            logInt64Array(getID(name), value, nowMicros());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String name, float[] value){
        if (!recordIDs.containsKey(name)) {
            try {
                startEntry(getID(name), name, "float[]", nowMicros());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            logFloatArray(getID(name), value, nowMicros());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String name, double[] value){
        if (!recordIDs.containsKey(name)) {
            try {
                startEntry(getID(name), name, "double[]", nowMicros());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            logDoubleArray(getID(name), value, nowMicros());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String name, String[] value){
        if (!recordIDs.containsKey(name)) {
            try {
                startEntry(getID(name), name, "String[]", nowMicros());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            logStringArray(getID(name), value, nowMicros());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ─── INTERNAL LOGGING ────────────────────────────────────────────────────
    // ─── Scalar logging ──────────────────────────────────────────────────────
    private void logBoolean(int id, boolean v, long ts) throws IOException {
        writeRecord(id, new byte[]{(byte) (v ? 1 : 0)}, ts);
    }

    private void logInt64(int id, long v, long ts) throws IOException {
        ByteBuffer b = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(v);
        writeRecord(id, b.array(), ts);
    }

    private void logFloat(int id, float v, long ts) throws IOException {
        ByteBuffer b = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(v);
        writeRecord(id, b.array(), ts);
    }

    private void logDouble(int id, double v, long ts) throws IOException {
        ByteBuffer b = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(v);
        writeRecord(id, b.array(), ts);
    }

    private void logString(int id, String s, long ts) throws IOException {
        byte[] data = s.getBytes(StandardCharsets.UTF_8);
        writeRecord(id, data, ts);
    }

    // ─── Array logging ───────────────────────────────────────────────────────
    private void logBooleanArray(int id, boolean[] arr, long ts) throws IOException {
        byte[] data = new byte[arr.length];
        for (int i = 0; i < arr.length; i++) data[i] = (byte) (arr[i] ? 1 : 0);
        writeRecord(id, data, ts);
    }

    private void logInt64Array(int id, long[] arr, long ts) throws IOException {
        ByteBuffer b = ByteBuffer.allocate(arr.length * 8).order(ByteOrder.LITTLE_ENDIAN);
        for (long v : arr) b.putLong(v);
        writeRecord(id, b.array(), ts);
    }

    private void logFloatArray(int id, float[] arr, long ts) throws IOException {
        ByteBuffer b = ByteBuffer.allocate(arr.length * 4).order(ByteOrder.LITTLE_ENDIAN);
        for (float v : arr) b.putFloat(v);
        writeRecord(id, b.array(), ts);
    }

    private void logDoubleArray(int id, double[] arr, long ts) throws IOException {
        ByteBuffer b = ByteBuffer.allocate(arr.length * 8).order(ByteOrder.LITTLE_ENDIAN);
        for (double v : arr) b.putDouble(v);
        writeRecord(id, b.array(), ts);
    }

    private void logStringArray(int id, String[] arr, long ts) throws IOException {
        ByteArrayOutputStream bb = new ByteArrayOutputStream();
        bb.write(le32(arr.length));
        for (String s : arr) {
            byte[] sb = s.getBytes(StandardCharsets.UTF_8);
            bb.write(le32(sb.length));
            bb.write(sb);
        }
        writeRecord(id, bb.toByteArray(), ts);
    }

    // ─── Utils ───────────────────────────────────────────────────────────────
    private int getID(String logName) {
        if (recordIDs.containsKey(logName)) {
            return recordIDs.get(logName);
        }

        largestId++;
        recordIDs.put(logName, largestId);
        return largestId;
    }

    private long nowMicros() {
        return System.nanoTime() / 1000;
    }

    private byte[] le16(short v) {
        return new byte[]{(byte) v, (byte) (v >> 8)};
    }

    private byte[] le32(int v) {
        return new byte[]{
                (byte) v,
                (byte) (v >> 8),
                (byte) (v >> 16),
                (byte) (v >> 24)
        };
    }

    private byte[] le64(long v) {
        return new byte[]{
                (byte) v,
                (byte) (v >> 8),
                (byte) (v >> 16),
                (byte) (v >> 24),
                (byte) (v >> 32),
                (byte) (v >> 40),
                (byte) (v >> 48),
                (byte) (v >> 56)
        };
    }
}