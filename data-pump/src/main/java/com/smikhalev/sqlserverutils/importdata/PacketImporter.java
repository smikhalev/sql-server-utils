package com.smikhalev.sqlserverutils.importdata;

import com.smikhalev.sqlserverutils.core.executor.StatementExecutor;

import java.util.List;

public class PacketImporter {
    private StatementExecutor executor;
    private PacketInsertGenerator insertGenerator;
    private ImportValueConverter converter;

    public PacketImporter(StatementExecutor executor, PacketInsertGenerator insertGenerator, ImportValueConverter converter){
        this.executor = executor;
        this.insertGenerator = insertGenerator;
        this.converter = converter;
    }

    public void importPacket(Packet packet) {
        for (List<String> values : packet.getDataTable()) {
            converter.convert(packet.getTable(), values);
        }

        String insert = insertGenerator.generateInsert(packet);
        executor.executeScript(insert);
    }
}
