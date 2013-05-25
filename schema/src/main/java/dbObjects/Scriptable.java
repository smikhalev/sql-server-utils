package dbObjects;

public interface Scriptable {
    public String generateCreateScript();
    public String generateDropScript();
}
