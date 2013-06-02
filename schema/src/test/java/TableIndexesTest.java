import com.google.common.collect.Lists;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.DatabaseBuilder;
import com.smikhalev.sqlserverutils.schema.TableBuilder;
import com.smikhalev.sqlserverutils.schema.dbobjects.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

public class TableIndexesTest extends BaseDatabaseContextTest {

    @Test
    public void testTableWithClusteredIndex() throws Exception {
        DatabaseBuilder builder = new DatabaseBuilder();
        builder.addTable(
                new TableBuilder("simple_table")
                        .addNullColumn("bit_column", DbType.BIT)
                        .setClusteredIndex("cls", "bit_column")
                        .build()
        );
        Database expectedDatabase = builder.build();

        Database actualDatabase = loadDatabase(expectedDatabase);
        Assert.assertEquals(actualDatabase, expectedDatabase, "Databases are not the same.");
    }

    @Test
    public void testTableWithNonClusteredIndex() throws Exception {
        DatabaseBuilder builder = new DatabaseBuilder();
        builder.addTable(
                new TableBuilder("simple_table")
                        .addNullColumn("bit_column", DbType.BIT)
                        .addNonClusteredIndex("non", "bit_column")
                        .build()
        );
        Database expectedDatabase = builder.build();

        Database actualDatabase = loadDatabase(expectedDatabase);
        Assert.assertEquals(actualDatabase, expectedDatabase, "Databases are not the same.");
    }

    @Test
    public void testTableWithClusteredAndNonClusteredIndex() throws Exception {
        DatabaseBuilder builder = new DatabaseBuilder();
        builder.addTable(
                new TableBuilder("simple_table")
                        .addNullColumn("bit_column", DbType.BIT)
                        .setClusteredIndex("cls", "bit_column")
                        .addNonClusteredIndex("non", "bit_column")
                        .build()
        );
        Database expectedDatabase = builder.build();

        Database actualDatabase = loadDatabase(expectedDatabase);
        Assert.assertEquals(actualDatabase, expectedDatabase, "Databases are not the same.");
    }

    @Test
    public void testTableWithClusteredAndNonClusteredWithSeveralFieldsIndex() throws Exception {
        Database expectedDatabase = buildTypicalDatabase();

        Database actualDatabase = loadDatabase(expectedDatabase);
        Assert.assertEquals(actualDatabase, expectedDatabase, "Databases are not the same.");
    }

    @Test
    public void testNegativeIndexName() throws Exception {
        Database sourceDatabase = buildTypicalDatabase();
        Database expectedDatabase = buildTypicalDatabase();

        Index expectedIndex = expectedDatabase.getTables().get(0).getClusteredIndex();
        expectedIndex.setName("wrong name");

        Database actualDatabase = loadDatabase(sourceDatabase);

        Index actualIndex = actualDatabase.getTables().get(0).getClusteredIndex();
        Assert.assertNotEquals(actualIndex, expectedIndex, "This index should not be the same");
    }

    @Test
    public void testNegativeClusteredIndexExists() throws Exception {
        Database sourceDatabase = buildTypicalDatabase();
        Database expectedDatabase = buildTypicalDatabase();

        Table expectedTable =  expectedDatabase.getTables().get(0);
        expectedTable.setClusteredIndex(null);

        Database actualDatabase = loadDatabase(sourceDatabase);

        Index actualTable = actualDatabase.getTables().get(0).getClusteredIndex();
        Assert.assertNotEquals(actualTable, expectedTable, "This table should not be the same");
    }

    @Test
    public void testNegativeNonClusteredIndexExists() throws Exception {
        Database sourceDatabase = buildTypicalDatabase();
        Database expectedDatabase = buildTypicalDatabase();

        Table expectedTable =  expectedDatabase.getTables().get(0);
        expectedTable.getNonClusteredIndexes().remove(0);

        Database actualDatabase = loadDatabase(sourceDatabase);

        Index actualTable = actualDatabase.getTables().get(0).getClusteredIndex();
        Assert.assertNotEquals(actualTable, expectedTable, "This table should not be the same");
    }

    @Test
    public void testNegativeIndexColumnName() throws Exception {
        Database sourceDatabase = buildTypicalDatabase();
        Database expectedDatabase = buildTypicalDatabase();

        IndexColumn expectedIndexColumn = expectedDatabase.getTables().get(0).getClusteredIndex().getKeyColumns().get(0);
        expectedIndexColumn.setName("wrong name");

        Database actualDatabase = loadDatabase(sourceDatabase);

        IndexColumn actualIndexColumn = actualDatabase.getTables().get(0).getClusteredIndex().getKeyColumns().get(0);
        Assert.assertNotEquals(actualIndexColumn, expectedIndexColumn, "This index column should not be the same");
    }

    @Test
    public void testNegativeIndexColumnSorting() throws Exception {
        Database sourceDatabase = buildTypicalDatabase();
        Database expectedDatabase = buildTypicalDatabase();

        IndexColumn expectedIndexColumn = expectedDatabase.getTables().get(0).getClusteredIndex().getKeyColumns().get(0);
        expectedIndexColumn.setSort(SortType.DESC);

        Database actualDatabase = loadDatabase(sourceDatabase);

        IndexColumn actualIndexColumn = actualDatabase.getTables().get(0).getClusteredIndex().getKeyColumns().get(0);
        Assert.assertNotEquals(actualIndexColumn, expectedIndexColumn, "This index column should not be the same");
    }

    @Test
    public void testNegativeIndexDifferentColumn() throws Exception {
        Database sourceDatabase = buildTypicalDatabase();
        Database expectedDatabase = buildTypicalDatabase();

        Index expectedIndex = expectedDatabase.getTables().get(0).getClusteredIndex();
        expectedIndex.getKeyColumns().remove(0);

        Database actualDatabase = loadDatabase(sourceDatabase);

        Index actualIndex = actualDatabase.getTables().get(0).getClusteredIndex();
        Assert.assertNotEquals(actualIndex, expectedIndex, "This index should not be the same");
    }

    @Test
    public void testNegativeIndexOrderingColumnName() throws Exception {
        Database sourceDatabase = buildTypicalDatabase();
        Database expectedDatabase = buildTypicalDatabase();

        Index expectedIndex = expectedDatabase.getTables().get(0).getClusteredIndex();
        Collections.reverse(expectedIndex.getKeyColumns());

        Database actualDatabase = loadDatabase(sourceDatabase);

        Index actualIndex = actualDatabase.getTables().get(0).getClusteredIndex();
        Assert.assertNotEquals(actualIndex, expectedIndex, "This index should not be the same");
    }

    @Test
    public void testNegativeIncludedColumnsExists() throws Exception {
        Database sourceDatabase = buildTypicalDatabase();
        Database expectedDatabase = buildTypicalDatabase();

        NonClusteredIndex expectedIndex = expectedDatabase.getTables().get(0).getNonClusteredIndexes().get(0);
        expectedIndex.getIncludedColumns().clear();

        Database actualDatabase = loadDatabase(sourceDatabase);

        Index actualIndex = actualDatabase.getTables().get(0).getClusteredIndex();
        Assert.assertNotEquals(actualIndex, expectedIndex, "This index should not be the same");
    }

    @Test
    public void testPositiveIncludedColumnsOrder() throws Exception {
        Database sourceDatabase = buildTypicalDatabase();
        Database expectedDatabase = buildTypicalDatabase();

        NonClusteredIndex expectedIndex = expectedDatabase.getTables().get(0).getNonClusteredIndexes().get(0);
        Collections.reverse(expectedIndex.getIncludedColumns());

        Database actualDatabase = loadDatabase(sourceDatabase);

        // The order in included columns doesn't matter that is why this modification
        // should not change anything
        Index actualIndex = actualDatabase.getTables().get(0).getNonClusteredIndexes().get(0);
        Assert.assertEquals(actualIndex, expectedIndex, "This index should be the same");
    }


    private Database buildTypicalDatabase() {
        DatabaseBuilder builder = new DatabaseBuilder();
        builder.addTable(
                new TableBuilder("simple_table")
                        .addNullColumn("bit_column", DbType.BIT)
                        .addNotNullColumn("nvarchar_column", DbType.NVARCHAR)
                        .addNotNullColumn("included_column1", DbType.REAL)
                        .addNotNullColumn("included_column2", DbType.REAL)
                        .setClusteredIndex("cls", "bit_column", "nvarchar_column")
                        .addNonClusteredIndexWithIncludeColumns("non",
                                new String[]{"nvarchar_column", "bit_column"},
                                new String[]{"included_column1", "included_column2"})
                        .build()
        );
        return builder.build();
    }
}

