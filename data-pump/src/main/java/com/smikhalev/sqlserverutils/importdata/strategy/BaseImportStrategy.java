package com.smikhalev.sqlserverutils.importdata.strategy;

import com.smikhalev.sqlserverutils.importdata.ImportStrategy;
import com.smikhalev.sqlserverutils.importdata.Packet;
import com.smikhalev.sqlserverutils.importdata.TableValueConstructor;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

public abstract class BaseImportStrategy implements ImportStrategy {

    private TableValueConstructor constructor;
    private int size;

    public BaseImportStrategy(TableValueConstructor constructor, int size) {
        this.constructor = constructor;
        this.size = size;
    }

    protected String generateSelect(Packet packet) {
        return constructor.construct(packet.getTable(), packet.getDataTable());
    }

    @Override
    public int getSize() {
        return size;
    }
}
