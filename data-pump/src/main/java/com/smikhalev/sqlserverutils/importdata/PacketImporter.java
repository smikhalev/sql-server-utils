package com.smikhalev.sqlserverutils.importdata;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;

public class PacketImporter {

    private TableValueConstructor constructor;
    private StatementExecutor executor;

    public PacketImporter (TableValueConstructor constructor, StatementExecutor executor) {
        this.constructor = constructor;
        this.executor = executor;
    }

    public void importPacket(Packet packet) {
        String select = constructor.construct(packet.getTable(), packet.getDataTable());

        String insert = String.format("insert into %s (%s) %s",
                packet.getTable().getFullName(), packet.getTable().getColumns().generateFields(), select);

        executor.executeScript(insert);
    }
}
