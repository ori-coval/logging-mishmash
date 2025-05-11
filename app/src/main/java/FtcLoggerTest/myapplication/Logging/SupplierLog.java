package FtcLoggerTest.myapplication.Logging;

import com.acmerobotics.dashboard.FtcDashboard;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

public class SupplierLog {
    public static BooleanSupplier wrap(String name, BooleanSupplier s, boolean postToFtcDashboard) {
        return () -> {
            boolean v = s.getAsBoolean();
            if(postToFtcDashboard){
                FtcDashboard.getInstance().getTelemetry().addData(name,v);
            }
            WpiLog.getInstance().log(name, v);
            return v;
        };
    }
    public static IntSupplier wrap(String name, IntSupplier s, boolean postToFtcDashboard) {
        return () -> {
            int v = s.getAsInt();
            if(postToFtcDashboard){
                FtcDashboard.getInstance().getTelemetry().addData(name,v);
            }
            WpiLog.getInstance().log(name, (long)v);
            return v;
        };
    }
    public static LongSupplier wrap(String name, LongSupplier s, boolean postToFtcDashboard) {
        return () -> {
            long v = s.getAsLong();
            if(postToFtcDashboard){
                FtcDashboard.getInstance().getTelemetry().addData(name,v);
            }
            WpiLog.getInstance().log(name, v);
            return v;
        };
    }
    public static DoubleSupplier wrap(String name, DoubleSupplier s, boolean postToFtcDashboard) {
        return () -> {
            double v = s.getAsDouble();
            if(postToFtcDashboard){
                FtcDashboard.getInstance().getTelemetry().addData(name,v);
            }
            WpiLog.getInstance().log(name, v);
            return v;
        };
    }
}
