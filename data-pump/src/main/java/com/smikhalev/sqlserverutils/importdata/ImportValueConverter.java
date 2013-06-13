package com.smikhalev.sqlserverutils.importdata;

import com.smikhalev.sqlserverutils.schema.dbobjects.DbType;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImportValueConverter {

    private List<DbType> typesNeedQuotes = Arrays.asList(DbType.NVARCHAR, DbType.VARCHAR, DbType.DATE,
                                            DbType.TIME, DbType.DATETIME);

    public List<String> convert(Table table, List<String> values) {
        for (int i = 0; i < table.getColumns().size(); i++) {
            DbType dbType = table.getColumns().get(i).getType();
            if (typesNeedQuotes.contains(dbType)) {
                values.set(i, "'" + values.get(i) + "'");
            }
        }
        return values;
    }


}
