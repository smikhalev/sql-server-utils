package com.smikhalev.sqlserverutils.importdata.importer;

import com.smikhalev.sqlserverutils.RestorableAction;
import com.smikhalev.sqlserverutils.RestorableContext;
import com.smikhalev.sqlserverutils.importdata.*;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbObject;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public abstract class BaseImporter implements Importer {
    private final PacketImporter packetImporter;
    private final List<RestorableAction> restorableActions;
    private final ImportStrategySelector strategySelector;
    private final CsvLineParser csvLineParser;
    private boolean isFinished = false;

    public BaseImporter(PacketImporter packetImporter, ImportStrategySelector strategySelector, List<RestorableAction> restorableAction, CsvLineParser csvLineParser) {
        this.packetImporter = packetImporter;
        this.strategySelector = strategySelector;
        this.restorableActions = restorableAction;
        this.csvLineParser = csvLineParser;
    }

    public void importData(Database database, Reader reader) {
        try (RestorableContext context = new RestorableContext(restorableActions)) {
            context.prepare(database);
            importDataBody(database, context, reader);
        }
    }

    private void importDataBody(Database database, RestorableContext context, Reader reader) {
        startImport();

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            Map<String, Packet> packets = new HashMap<>();
            Set<String> processingTables = new TreeSet<>();
            long uniquePacketId = 0;
            while ((line = bufferedReader.readLine()) != null) {
                List<String> lineValues = csvLineParser.parse(line);
                Packet packet = loadPackets(database, packets, lineValues, uniquePacketId, context, processingTables);

                if (packet.getUniqueId() > uniquePacketId) {
                    uniquePacketId = packet.getUniqueId();
                }

                if (packet.isFull()) {
                    importPacket(packet);
                    packets.remove(packet.getTable().getFullName());
                }
            }

            importLastPackets(packets);
        } catch (IOException e) {
            throw new ImportException(e.getMessage(), e);
        }

        finishImport();
        isFinished = true;
    }

    protected void startImport() {
    }

    protected void finishImport() {
    }

    protected void importLastPackets(Map<String, Packet> packets) {
        for (Packet packet : packets.values()) {
            if (packet.size() != 0)
                importPacket(packet);
        }
    }

    private void importPacket(Packet packet) {
        ImportStrategy strategy = strategySelector.select(packet.getTable());
        importPacket(packet, strategy);
    }

    protected abstract void importPacket(Packet packet, ImportStrategy importStrategy);

    protected PacketImporter getPacketImporter() {
        return packetImporter;
    }

    private Packet loadPackets(Database database, Map<String, Packet> packets, List<String> lineValues, long uniquePacketId, RestorableContext context, Set<String> processingTables) {
        String tableFullName = DbObject.buildFullName(lineValues.get(0), lineValues.get(1));

        if (!packets.containsKey(tableFullName)) {
            Table table = database.getTableByFullName(tableFullName);
            uniquePacketId++;
            ImportStrategy strategy = strategySelector.select(table);

            if (!processingTables.contains(tableFullName)){
                context.prepare(strategy.getRestorableAction(table), database);
                processingTables.add(tableFullName);
            }

            Packet packet = new Packet(table, uniquePacketId, strategy.getSize());
            packets.put(tableFullName, packet);
        }

        Packet packet = packets.get(tableFullName);
        packet.add(lineValues);
        return packet;
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }
}
