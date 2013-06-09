package com.smikhalev.sqlserverutils.generator;

import com.google.common.base.Joiner;
import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.dbobjects.Column;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.util.ArrayList;
import java.util.List;

/*
 * This class is based on fake query from sys.all_columns.
 * That really means that it can't generate and insert more rows than in sys.all_columns.
 * It is not good but faster then recursive query, cross joining and better than generating a huge
 * insert into values script. In current implementation I use chunks to support inserting
 * a huge amount of data.
 */
public class SimpleDataGenerator implements DataGenerator {

    private ColumnGeneratorFactory columnGeneratorFactory;
    private StatementExecutor executor;
    private int chunkSize;

    public SimpleDataGenerator(ColumnGeneratorFactory columnGeneratorFactory, StatementExecutor executor, int chunkSize) {
        this.columnGeneratorFactory = columnGeneratorFactory;
        this.executor = executor;
        this.chunkSize = chunkSize;
    }

    public void generateData(Database database, int count) {
        for(int i = 0; i < count; i = i + chunkSize)
        {
            int dataSize = i + chunkSize > count
                    ? count - i
                    : chunkSize;
            generateChunkData(database, dataSize);
        }
    }

    private void generateChunkData(Database database, int count) {
        for(Table table : database.getTables()) {
            executor.executeScript(generateDataScript(table, count));
        }
    }

    public String generateDataScript(Table table, int count) {
        String insertHeader = generateHeader(table);
        String select = generateSelectScript(table, count);
        return insertHeader + select;
    }

    private String generateHeader(Table table) {
        List<String> columnNames = new ArrayList<>();

        for(Column column : table.getColumns().values()) {
            columnNames.add(column.getName());
        }

        String columns = Joiner.on(",").join(columnNames);
        return String.format("insert into %s (%s) ", table.getFullName(), columns);
    }

    private String generateSelectScript(Table table, int count) {
        List<String> valueScripts = new ArrayList<>();

        for(Column column : table.getColumns().values()) {
            ColumnGenerator generator = columnGeneratorFactory.create(column);
            String valueScript = String.format("%s as [%s]",
                                    generator.generateValueScript(), column.getName());
            valueScripts.add(valueScript);
        }

        String selectScript = String.format("select top %s %s from sys.all_columns as s1, sys.all_columns as s2",
                count, Joiner.on(",").join(valueScripts));

        return selectScript;
    }
}
