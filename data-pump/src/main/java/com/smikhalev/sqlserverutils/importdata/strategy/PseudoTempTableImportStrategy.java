package com.smikhalev.sqlserverutils.importdata.strategy;

import com.smikhalev.sqlserverutils.importdata.Packet;
import com.smikhalev.sqlserverutils.importdata.TableValueConstructor;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

public class PseudoTempTableImportStrategy extends BaseImportStrategy {
    public PseudoTempTableImportStrategy(TableValueConstructor constructor, int size) {
        super(constructor, size);
    }

    @Override
    public String generateImportInsert(Packet packet) {
        return String.format("select * into __cloned_%s_%s from (%s) d",
                packet.getTable().getName(), packet.getUniqueId(), generateSelect(packet));
    }

    @Override
    public boolean isApplicable(Table table) {
        return true;
    }
}
