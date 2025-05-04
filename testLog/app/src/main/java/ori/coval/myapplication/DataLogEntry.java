package ori.coval.myapplication;

/**
 * Represents a logging entry with an ID, a name, and a WPILog type string.
 */
public class DataLogEntry {
    public final int id;
    public final String name;
    public final String type;
    public DataLogEntry(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
}
