package com.smikhalev.sqlserverutils.importdata.importer;

import com.smikhalev.sqlserverutils.RestorableContext;
import com.smikhalev.sqlserverutils.importdata.*;
import com.smikhalev.sqlserverutils.importdata.strategy.BulkInsertImportStrategy;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbObject;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseImporter implements Importer {
    private PacketImporter packetImporter;
    private Iterable<RestorableAction> restorableActions;
    private ImportStrategySelector strategySelector;
    private CsvLineParser csvLineParser;
    private int chunkSize;

    public BaseImporter(PacketImporter packetImporter, ImportStrategySelector strategySelector, Iterable<RestorableAction> restorableAction, CsvLineParser csvLineParser, int chunkSize) {
        this.packetImporter = packetImporter;
        this.strategySelector = strategySelector;
        this.restorableActions = restorableAction;
        this.chunkSize = chunkSize;
        this.csvLineParser = csvLineParser;
    }

    public void importData(Database database, Reader reader) {
        try(RestorableContext context = new RestorableContext(restorableActions)) {
            context.prepare(database);
            importDataBody(database, reader);
        }
    }

    private void importDataBody(Database database, Reader reader) {

        try(BufferedReader bufferedReader = new BufferedReader(reader)){
            String line;
            Map<String, Packet> packets = new HashMap<>();
            long uniquePacketId = 0;
            while ((line = bufferedReader.readLine()) != null)
            {
                List<String> lineValues = csvLineParser.parse(line);
                Packet packet = loadPackets(database, packets, lineValues, uniquePacketId);

                if(packet.getUniqueId() > uniquePacketId) {
                    uniquePacketId = packet.getUniqueId();
                }

                if (isPacketFull(packet)) {
                    importPacket(packet);
                    packets.remove(packet.getTable().getFullName());
                }
            }

            importLastPackets(packets);
            finishImport();
        }
        catch (IOException e) {
            throw new ImportException(e.getMessage(), e);
        }
    }

    protected void finishImport() {
    }

    protected void importLastPackets(Map<String, Packet> packets) {
        for(Packet packet : packets.values()) {
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

    protected boolean isPacketFull(Packet packet) {
        return packet.size() == chunkSize;
    }

    private Packet loadPackets(Database database, Map<String, Packet> packets, List<String> lineValues, long uniquePacketId) {
        String tableFullName = DbObject.buildFullName(lineValues.get(0), lineValues.get(1));

        if(!packets.containsKey(tableFullName)){
            Table table = database.getTables().get(tableFullName);
            uniquePacketId++;
            packets.put(tableFullName, new Packet(table, uniquePacketId));
        }

        Packet packet = packets.get(tableFullName);
        packet.add(lineValues.subList(2, lineValues.size()));
        return packet;
    }
}
