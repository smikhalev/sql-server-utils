package com.smikhalev.sqlserverutils.importdata.strategy;

import com.smikhalev.sqlserverutils.core.Constants;
import com.smikhalev.sqlserverutils.importdata.Packet;
import com.smikhalev.sqlserverutils.importdata.TableValueConstructor;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

public class BulkInsertImportStrategy extends BaseImportStrategy {
    public BulkInsertImportStrategy(TableValueConstructor constructor) {
        super(constructor);
    }

    @Override
    public String generateImportInsert(Packet packet) {
        String fields = packet.getTable().getColumns().generateFields();

        return String.format("insert into %s with(tablockx) (%s) %s ",
                packet.getTable().getFullName(), fields, generateSelect(packet));
    }

    @Override
    public boolean isApplicable(Table table) {
        return true;
    }
}
