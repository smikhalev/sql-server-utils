package com.smikhalev.sqlserverutils.export.strategy;

import com.google.common.base.Joiner;
import com.smikhalev.sqlserverutils.export.IndexSizeProvider;
import com.smikhalev.sqlserverutils.export.TableSizeProvider;
import com.smikhalev.sqlserverutils.schema.dbobjects.*;

import java.util.ArrayList;
import java.util.List;

/*
 *   select *
 *   from
 *   (
 *       select id
 *       from test_order_by_clause
 *       order by id offset 1000 rows fetch next 20 rows only
 *   ) d
 *   inner join test_order_by_clause m
 *       on m.id = d.id
 *       UniqueNonClusteredIndexChunkStrategy
 */
public abstract class IndexChunkSubQueryStrategy extends IndexChunkStrategy {

    public IndexChunkSubQueryStrategy(TableSizeProvider tableSizeProvider, IndexSizeProvider indexSizeProvider, int pageSize) {
        super(tableSizeProvider, indexSizeProvider, pageSize);
    }

    protected String generateExportSelect(Table table, long offset) {
        Index index = getIndex(table);
        String innerSelect = generateInnerSelect(table, offset, index);
        String mainSelect = generateMainSelect(table, index, innerSelect);
        return mainSelect;
    }

    protected String generateMainSelect(Table table, Index index, String innerSelect) {
        return generateSelectClause(table) +
        generateMainFromClause(table, index, innerSelect);
    }

    protected String generateInnerSelect(Table table, long offset, Index index) {
        return generateInnerSelectFields(index) +
            generateFromClause(table) +
            generateOrderByClause(index, offset);
    }

    protected String generateSelectClause(Table table) {
        List<String> columnNames = new ArrayList<>();

        for(Column column : table.getColumns().values()) {
            columnNames.add(String.format("m.[%s]", column.getName()));
        }

        String fields = Joiner.on(", ").join(columnNames);
        String select = String.format("select %s ", fields);
        return select;
    }

    protected String generateMainFromClause(Table table, Index index, String innerSelect) {
        List<String> joinConditions = new ArrayList<>();

        for (IndexColumn column : index.getKeyColumns()) {
            String condition = String.format("m.[%s] = d.[%s]", column.getName(), column.getName());
            joinConditions.add(condition);
        }

        String joinConditionClause = Joiner.on(" and ").join(joinConditions);
        return String.format(" from (%s) as d inner join %s as m on %s ",
                innerSelect, table.getFullName(), joinConditionClause);
    }

    protected String generateInnerSelectFields(Index index) {
        List<String> fields = new ArrayList<>();

        for(IndexColumn column : index.getKeyColumns()) {
            fields.add(String.format("[%s]", column.getName()));
        }

        return String.format("select %s ", Joiner.on(", ").join(fields));
    }
}
