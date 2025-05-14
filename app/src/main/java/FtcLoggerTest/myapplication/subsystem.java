package FtcLoggerTest.myapplication;


import java.util.function.DoubleSupplier;

import FtcLoggerTest.myapplication.Logging.AutoLog;


@AutoLog
public class subsystem {

    public subsystem(){}

    double dontLogTest = 4;
    double rotation = 0;
    double x = 0;
    double y = 0;
    double[] pose = new double[3];

    public static double yststic = 0;

    DoubleSupplier xsupplier = () -> x;

}
