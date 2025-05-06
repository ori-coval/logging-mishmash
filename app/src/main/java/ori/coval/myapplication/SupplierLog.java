package ori.coval.myapplication;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

public class SupplierLog {
    public static BooleanSupplier wrap(String name, BooleanSupplier s) {
        return () -> {
            boolean v = s.getAsBoolean();
            WpiLog.getInstance().log(name, v);
            return v;
        };
    }
    public static IntSupplier wrap(String name, IntSupplier s) {
        return () -> {
            int v = s.getAsInt();
            WpiLog.getInstance().log(name, (long)v);
            return v;
        };
    }
    public static LongSupplier wrap(String name, LongSupplier s) {
        return () -> {
            long v = s.getAsLong();
            WpiLog.getInstance().log(name, v);
            return v;
        };
    }
    public static DoubleSupplier wrap(String name, DoubleSupplier s) {
        return () -> {
            double v = s.getAsDouble();
            WpiLog.getInstance().log(name, v);
            return v;
        };
    }
}
