package ori.coval.myapplication;

import android.os.Handler;
import android.os.Looper;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.DoubleSupplier;

/**
 * TelemetryManager: automatically logs all fields on registered
 * objects and any registered suppliers at a fixed interval (ms)
 * via WpiLog.getInstance().log(...).
 * Classes or fields annotated @NoLog will be excluded from field-logging.
 */

//TODO: make telemetry manager singleton
public class TelemetryManager {
    private final List<Object> targets = new ArrayList<>();
    private final List<BooleanSupplierEntry> boolSuppliers = new ArrayList<>();
    private final List<IntSupplierEntry> intSuppliers = new ArrayList<>();
    private final List<LongSupplierEntry> longSuppliers = new ArrayList<>();
    private final List<DoubleSupplierEntry> doubleSuppliers = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private long periodMs;

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            logAllFields();
            logAllSuppliers();
            handler.postDelayed(this, periodMs);
        }
    };

    private static TelemetryManager instance;

    public static synchronized TelemetryManager getInstance(){
        if(instance == null){
            instance = new TelemetryManager(10);
        }
        return instance;
    }

    /**
     * @param periodMs Interval between logs in milliseconds.
     */
    private TelemetryManager(long periodMs) {
        this.periodMs = periodMs;
    }

    /**
     * sets the amount of time in ms between each log
     * @param periodMs how many milliseconds between  each log
     */
    private void setLoggingSpeed(long periodMs){
        this.periodMs = periodMs;
    }

    /**
     * Register an object whose fields will be logged, unless excluded via @NoLog.
     */
    public void register(Object obj) {
        targets.add(obj);
    }

    /**
     * Register a BooleanSupplier to be logged under the given name.
     */
    public void registerBooleanSupplier(String name, BooleanSupplier supplier) {
        boolSuppliers.add(new BooleanSupplierEntry(name, supplier));
    }

    /** Register an IntSupplier to be logged. */
    public void registerIntSupplier(String name, IntSupplier supplier) {
        intSuppliers.add(new IntSupplierEntry(name, supplier));
    }

    /** Register a LongSupplier to be logged. */
    public void registerLongSupplier(String name, LongSupplier supplier) {
        longSuppliers.add(new LongSupplierEntry(name, supplier));
    }

    /** Register a DoubleSupplier to be logged. */
    public void registerDoubleSupplier(String name, DoubleSupplier supplier) {
        doubleSuppliers.add(new DoubleSupplierEntry(name, supplier));
    }

    /** Start automatic logging. */
    public void start() {
        handler.post(task);
    }

    /** Stop automatic logging. */
    public void stop() {
        handler.removeCallbacks(task);
    }

    /** Logs every declared field of each registered object except those excluded. */
    private void logAllFields() {
        for (Object obj : targets) {
            Class<?> cls = obj.getClass();
            if (cls.isAnnotationPresent(NoLog.class)) continue;
            String clsName = cls.getSimpleName();
            for (Field f : cls.getDeclaredFields()) {
                if (f.isAnnotationPresent(NoLog.class)) continue;
                f.setAccessible(true);
                String logName = clsName + "." + f.getName();
                try {
                    logField(obj, f, logName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** Logs all registered suppliers. */
    private void logAllSuppliers() {
        for (BooleanSupplierEntry e : boolSuppliers) {
            WpiLog.getInstance().log(e.name, e.supplier.getAsBoolean());
        }
        for (IntSupplierEntry e : intSuppliers) {
            WpiLog.getInstance().log(e.name, (long) e.supplier.getAsInt());
        }
        for (LongSupplierEntry e : longSuppliers) {
            WpiLog.getInstance().log(e.name, e.supplier.getAsLong());
        }
        for (DoubleSupplierEntry e : doubleSuppliers) {
            WpiLog.getInstance().log(e.name, e.supplier.getAsDouble());
        }
    }

    /** Dispatch based on field type to WpiLog. */
    private void logField(Object obj, Field f, String name) throws IllegalAccessException, IOException {
        Class<?> t = f.getType();
        if (t == boolean.class) {
            WpiLog.getInstance().log(name, f.getBoolean(obj));
        } else if (t == byte.class) {
            WpiLog.getInstance().log(name, (long) f.getByte(obj));
        } else if (t == char.class) {
            WpiLog.getInstance().log(name, (long) f.getChar(obj));
        } else if (t == short.class) {
            WpiLog.getInstance().log(name, (long) f.getShort(obj));
        } else if (t == int.class) {
            WpiLog.getInstance().log(name, (long) f.getInt(obj));
        } else if (t == long.class) {
            WpiLog.getInstance().log(name, f.getLong(obj));
        } else if (t == float.class) {
            WpiLog.getInstance().log(name, f.getFloat(obj));
        } else if (t == double.class) {
            WpiLog.getInstance().log(name, f.getDouble(obj));
        } else if (t == String.class) {
            Object val = f.get(obj);
            WpiLog.getInstance().log(name, val != null ? val.toString() : "");
        } else if (t.isArray()) {
            Class<?> ct = t.getComponentType();
            if (ct == boolean.class) {
                WpiLog.getInstance().log(name, (boolean[]) f.get(obj));
            } else if (ct == byte.class) {
                byte[] ba = (byte[]) f.get(obj);
                long[] la = new long[ba.length]; for (int i = 0; i < ba.length; i++) la[i] = ba[i];
                WpiLog.getInstance().log(name, la);
            } else if (ct == char.class) {
                char[] ca = (char[]) f.get(obj);
                long[] la = new long[ca.length]; for (int i = 0; i < ca.length; i++) la[i] = ca[i];
                WpiLog.getInstance().log(name, la);
            } else if (ct == short.class) {
                short[] sa = (short[]) f.get(obj);
                long[] la = new long[sa.length]; for (int i = 0; i < sa.length; i++) la[i] = sa[i];
                WpiLog.getInstance().log(name, la);
            } else if (ct == int.class) {
                int[] ia = (int[]) f.get(obj);
                long[] la = new long[ia.length]; for (int i = 0; i < ia.length; i++) la[i] = ia[i];
                WpiLog.getInstance().log(name, la);
            } else if (ct == long.class) {
                WpiLog.getInstance().log(name, (long[]) f.get(obj));
            } else if (ct == float.class) {
                WpiLog.getInstance().log(name, (float[]) f.get(obj));
            } else if (ct == double.class) {
                WpiLog.getInstance().log(name, (double[]) f.get(obj));
            } else if (ct == String.class) {
                WpiLog.getInstance().log(name, (String[]) f.get(obj));
            }
        }
    }

    // Supplier entry helper classes
    private static class BooleanSupplierEntry {
        final String name;
        final BooleanSupplier supplier;
        BooleanSupplierEntry(String name, BooleanSupplier s) { this.name = name; this.supplier = s;}    }
    private static class IntSupplierEntry {
        final String name;
        final IntSupplier supplier;
        IntSupplierEntry(String name, IntSupplier s) { this.name = name; this.supplier = s;}    }
    private static class LongSupplierEntry {
        final String name;
        final LongSupplier supplier;
        LongSupplierEntry(String name, LongSupplier s) { this.name = name; this.supplier = s;}    }
    private static class DoubleSupplierEntry {
        final String name;
        final DoubleSupplier supplier;
        DoubleSupplierEntry(String name, DoubleSupplier s) { this.name = name; this.supplier = s;}    }
}
