package com.smikhalev.sqlserverutils.test;

import com.smikhalev.sqlserverutils.core.executor.DataTable;
import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

public class TableComparator {

    private StatementExecutor executor;

    private String compareQueryTemplate =
            ";with source " +
            "as " +
            "( " +
            "    select %s " +
            "    from %s " +
            "), " +
            "target " +
            "as " +
            "( " +
            "    select %s " +
            "    from %s " +
            ") " +
            "select * " +
            "from " +
            "( " +
            "    select * from target except select * from source " +
            "    union all " +
            "    select * from source except select * from target " +
            ") d";

    public TableComparator(StatementExecutor executor) {
        this.executor = executor;
    }

    public DataTable compare(Table source, Table target) {
        String compareQuery = String.format(compareQueryTemplate,
                source.getColumns().generateFields(), source.getFullName(),
                source.getColumns().generateFields(), target.getFullName());

        return executor.executeAsDataTable(compareQuery);
    }
}
