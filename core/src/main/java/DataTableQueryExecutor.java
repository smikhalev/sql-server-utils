import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DataTableQueryExecutor extends QueryExecutor<DataTable> {
    public DataTableQueryExecutor(String connectionString) {
        super(connectionString);
    }

    @Override
    protected DataTable processResult(ResultSet results) throws SQLException {
        DataTable dataTable = new DataTable();

        ResultSetMetaData metaData = results.getMetaData();

        while (results.next()) {
            DataRow row = createRow(results, metaData);
            dataTable.getRows().add(row);
        }

        return dataTable;
    }

    private DataRow createRow(ResultSet results, ResultSetMetaData metaData) throws SQLException {
        DataRow row = new DataRow();

        for (int columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++) {
            String columnName = metaData.getColumnName(columnIndex);
            Object columnValue = results.getObject(columnIndex);
            row.put(columnName, columnValue);
        }

        return row;
    }
}
