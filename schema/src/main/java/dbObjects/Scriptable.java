package dbObjects;

public interface Scriptable {

    public static final String NEW_LINE = System.getProperty("line.separator");

    public String generateCreateScript();
    public String generateDropScript();
}
