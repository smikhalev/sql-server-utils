package com.smikhalev.sqlserverutils.importdata.importer;

import com.smikhalev.sqlserverutils.importdata.Packet;
import com.smikhalev.sqlserverutils.importdata.PacketImporter;
import com.smikhalev.sqlserverutils.importdata.RestorableAction;

public class SequentialImporter extends BaseImporter {

    public SequentialImporter(PacketImporter packetImporter, Iterable<RestorableAction> restorableActions, int chunkSize) {
        super(packetImporter, restorableActions, chunkSize);
    }

    @Override
    protected void importPacket(Packet packet) {
        getPacketImporter().importPacket(packet);
    }
}
