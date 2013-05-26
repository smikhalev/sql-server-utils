import dbObjects.Table;

import java.util.ArrayList;
import java.util.List;

public class Schema {
    private List<Table> tables = new ArrayList<>();

    public List<Table> getTables() {
        return tables;
    }
}
