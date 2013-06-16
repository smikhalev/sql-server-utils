package com.smikhalev.sqlserverutils.importdata.importer;

import com.smikhalev.sqlserverutils.importdata.Packet;
import com.smikhalev.sqlserverutils.importdata.PacketImporter;

public class SequentialImporter extends BaseImporter {

    public SequentialImporter(PacketImporter packetImporter, int chunkSize) {
        super(packetImporter, chunkSize);
    }

    @Override
    protected void importPacket(Packet packet) {
        getPacketImporter().importPacket(packet);
    }
}
