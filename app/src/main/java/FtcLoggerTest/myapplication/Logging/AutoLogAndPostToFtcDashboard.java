package FtcLoggerTest.myapplication.Logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class for which an AutoLogged subclass will be generated
 * and the logged data will be posted to the FtcDashboard.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface  AutoLogAndPostToFtcDashboard {}
