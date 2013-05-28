public class SchemaSynchronizer implements AutoCloseable {
    //TODO; Extract into DI
    private ScriptExecutor executor;
    private Schema schema;

    public SchemaSynchronizer(String connectionString, Schema schema) {
        this.executor = new ScriptExecutor(connectionString);
        this.schema = schema;
    }

    @Override
    public void close() throws Exception {
        drop();
    }

    public void create() {
        String createScript = schema.generateCreateScript();
        executor.execute(createScript);
    }

    public void drop() {
        String dropScript = schema.generateDropScript();
        executor.execute(dropScript);
    }
}