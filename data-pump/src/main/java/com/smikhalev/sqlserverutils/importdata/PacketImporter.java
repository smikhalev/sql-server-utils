package com.smikhalev.sqlserverutils.importdata;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;

import java.util.List;

public class PacketImporter {
    private StatementExecutor executor;

    public PacketImporter(StatementExecutor executor){
        this.executor = executor;
    }

    public void importPacket(Packet packet, ImportStrategy importStrategy) {
        String insert = importStrategy.generateImportInsert(packet);
        executor.executeScript(insert);
    }
}
