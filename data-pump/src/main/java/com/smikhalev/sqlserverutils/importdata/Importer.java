package com.smikhalev.sqlserverutils.importdata;

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

public class Importer {

    private PacketImporter packetImporter;
    private ImportValueConverter valueConverter;
    private int chunkSize;

    public Importer(PacketImporter packetImporter, ImportValueConverter valueConverter, int chunkSize) {
        this.packetImporter = packetImporter;
        this.valueConverter = valueConverter;
        this.chunkSize = chunkSize;
    }

    public void importData(Database database, Reader reader) {
        try (ICsvListReader listReader = new CsvListReader(reader, CsvPreference.STANDARD_PREFERENCE)) {
            List<String> lineValues;

            Map<String, Packet> packets = new HashMap<>();

            while((lineValues = listReader.read()) != null ) {
                loadPackets(database, packets, lineValues);
                importFullPackets(packets);
            }

            importPackets(packets);
        }
        catch (IOException e) {
            throw new ImportException(e.getMessage(), e);
        }
    }

    private void loadPackets(Database database, Map<String, Packet> packets, List<String> lineValues) {
        String tableFullName = DbObject.buildFullName(lineValues.get(0), lineValues.get(1));
        Table table = database.getTables().get(tableFullName);

        if (!packets.containsKey(tableFullName))
        {
            packets.put(tableFullName, new Packet(table));
        }

        Packet packet = packets.get(tableFullName);

        List<String> values = valueConverter.convert(table, lineValues.subList(2, lineValues.size()));
        packet.add(values);
    }

    private void importFullPackets(Map<String, Packet> packets) {
        for(Packet packet : packets.values()) {
            if (isPacketFull(packet)) {
                packetImporter.importPacket(packet);
                packet.clear();
            }
        }
    }

    private void importPackets(Map<String, Packet> packets) {
        for(Packet packet : packets.values()) {
            packetImporter.importPacket(packet);
            packet.clear();
        }
    }

    private boolean isPacketFull(Packet packet) {
        return packet.size() == chunkSize;
    }
}
