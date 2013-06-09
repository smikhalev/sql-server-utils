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
 */
public class UniqueNonClusteredIndexChunkStrategy extends IndexChunkStrategy {

    private IndexSizeProvider indexSizeProvider;

    public UniqueNonClusteredIndexChunkStrategy(TableSizeProvider tableSizeProvider, IndexSizeProvider indexSizeProvider, int pageSize) {
        super(tableSizeProvider, pageSize);
        this.indexSizeProvider = indexSizeProvider;
    }

    protected String generateExportSelect(Table table, long offset) {
        Index index = findMinimumUniqueNonClusteredIndex(table);
        String innerSelect =
            generateInnerSelectFields(index) +
            super.generateFromClause(table) +
            super.generateOrderByClause(index, offset);

        String mainSelect =
            generateSelectClause(table) +
            generateFromClause(table, index, innerSelect);

        return mainSelect;
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

    protected String generateFromClause(Table table, Index index, String innerSelect) {
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

    @Override
    public boolean isApplicable(Table table) {
        return findMinimumUniqueNonClusteredIndex(table) != null;
    }

    private NonClusteredIndex findMinimumUniqueNonClusteredIndex(Table table) {
        NonClusteredIndex resultIndex = null;

        for (NonClusteredIndex index : table.getNonClusteredIndexes()) {
            if (index.isUnique()) {
                if (resultIndex == null) {
                    resultIndex = index;
                } else {
                    long resultIndexSize = indexSizeProvider.getSize(resultIndex);
                    long pretenderIndexSize = indexSizeProvider.getSize(index);

                    if (pretenderIndexSize < resultIndexSize) {
                        resultIndex = index;
                    }
                }
            }
        }

        return resultIndex;
    }
}
