import dbObjects.Table;
import org.testng.annotations.Test;

public class SchemaLoaderTest {

    private final String connectionString = "jdbc:jtds:sqlserver://mikhalevpc/mini-pump;user=sa;password=sa2013;";

    @Test
    public void simpleSchemaLoad() {
        SchemaLoader schemaLoader = new SchemaLoader(connectionString);

        Schema schema = schemaLoader.load();

        for (Table table : schema.getTables())
        {
            String createScript = table.generateCreateScript();
            System.out.println(createScript);
        }


    }
}
