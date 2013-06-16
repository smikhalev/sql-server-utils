package com.smikhalev.sqlserverutils.importdata.importer;

import com.smikhalev.sqlserverutils.importdata.*;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.dbobjects.DbObject;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseImporter implements Importer {
    private PacketImporter packetImporter;
    private int chunkSize;

    public BaseImporter(PacketImporter packetImporter, int chunkSize) {
        this.packetImporter = packetImporter;
        this.chunkSize = chunkSize;
    }

    public void importData(Database database, Reader reader) {
        try (ICsvListReader listReader = new CsvListReader(reader, CsvPreference.STANDARD_PREFERENCE)) {
            List<String> lineValues;

            Map<String, Packet> packets = new HashMap<>();

            while((lineValues = listReader.read()) != null ) {
                Packet packet = loadPackets(database, packets, lineValues);

                if (isPacketFull(packet)) {
                    importPacket(packet);
                    packets.put(packet.getTable().getFullName(), new Packet(packet.getTable()));
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

    protected abstract void importPacket(Packet packet);

    protected PacketImporter getPacketImporter() {
        return packetImporter;
    }

    protected boolean isPacketFull(Packet packet) {
        return packet.size() == chunkSize;
    }

    private Packet loadPackets(Database database, Map<String, Packet> packets, List<String> lineValues) {
        String tableFullName = DbObject.buildFullName(lineValues.get(0), lineValues.get(1));
        Table table = database.getTables().get(tableFullName);

        if (!packets.containsKey(tableFullName))
        {
            packets.put(tableFullName, new Packet(table));
        }

        Packet packet = packets.get(tableFullName);
        packet.add(lineValues.subList(2, lineValues.size()));

        return packet;
    }
}
