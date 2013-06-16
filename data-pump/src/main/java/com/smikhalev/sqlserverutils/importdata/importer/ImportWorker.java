package com.smikhalev.sqlserverutils.importdata.importer;

import com.smikhalev.sqlserverutils.importdata.Packet;
import com.smikhalev.sqlserverutils.importdata.PacketImporter;

public class ImportWorker implements Runnable {

    private PacketImporter packetImporter;
    private Packet packet;

    public ImportWorker(PacketImporter packetImporter, Packet packet) {
        this.packetImporter = packetImporter;
        this.packet = packet;
    }

    @Override
    public void run() {
        packetImporter.importPacket(packet);
    }
}
