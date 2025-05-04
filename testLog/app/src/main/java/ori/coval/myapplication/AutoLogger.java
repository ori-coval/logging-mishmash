// AutoLogger.java
package ori.coval.myapplication;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

/**
 * Scans registered objects and logs their fields and methods each cycle.
 */
public class AutoLogger {
    private static final Map<Object, List<Accessor>> registry = new IdentityHashMap<>();

    /** Register an object for auto-logging. */
    public static void register(Object o) {
        Class<?> cls = o.getClass();
        String prefix = "";
        List<Accessor> list = new ArrayList<>();
        for (Field f : cls.getDeclaredFields()) {
            f.setAccessible(true);
            if (isLoggableType(f.getType())) {
                list.add(new FieldAccessor(o, f, prefix + f.getName()));
            }
        }
        for (Method m : cls.getDeclaredMethods()) {
            if (m.getParameterCount() == 0 && isLoggableType(m.getReturnType())) {
                m.setAccessible(true);
                list.add(new MethodAccessor(o, m, prefix + m.getName()));
            }
        }
        registry.put(o, list);
    }

    /** Log all registered objects once. */
    public static void update() {
        registry.values().forEach(list -> list.forEach(Accessor::log));
    }

    private static boolean isLoggableType(Class<?> t) {
        return t == double.class || t == int.class || t == boolean.class || t == String.class
                || t.isArray();
    }

    private interface Accessor { void log(); }

    private static class FieldAccessor implements Accessor {
        final Object obj;
        final Field f;
        final String name;
        FieldAccessor(Object o, Field f, String name) { this.obj = o; this.f = f; this.name = name; }
        public void log() {
            Object v;
            try {
                v = f.get(obj);
            } catch (IllegalAccessException e) {
                return;
            }
            logValue(v);
        }
        private void logValue(Object v) {
            if (v instanceof Double) DataLogManager.logDouble(name, (Double)v);
            else if (v instanceof Integer) DataLogManager.logInt(name, (Integer)v);
            else if (v instanceof Boolean) DataLogManager.logBoolean(name, (Boolean)v);
            else if (v instanceof String) DataLogManager.logString(name, (String)v);
//            else if (v instanceof double[]) DataLogManager.logDoubleArray(name, (double[])v);
//            else if (v instanceof int[]) DataLogManager.logIntArray(name, (int[])v);
//            else if (v instanceof boolean[]) DataLogManager.logBooleanArray(name, (boolean[])v);
        }
    }

    private static class MethodAccessor implements Accessor {
        final Object obj;
        final Method m;
        final String name;
        MethodAccessor(Object o, Method m, String name) { this.obj = o; this.m = m; this.name = name; }
        public void log() {
            Object v;
            try {
                v = m.invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                return;
            }
            new FieldAccessor(obj, null, name).logValue(v);
        }
    }
}
