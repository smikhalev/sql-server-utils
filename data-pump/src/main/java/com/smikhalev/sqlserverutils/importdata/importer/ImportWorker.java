package com.smikhalev.sqlserverutils.importdata.importer;

import com.smikhalev.sqlserverutils.importdata.Packet;
import com.smikhalev.sqlserverutils.importdata.PacketImporter;

import java.util.concurrent.atomic.AtomicLong;

public class ImportWorker implements Runnable {

    private PacketImporter packetImporter;
    private Packet packet;
    private AtomicLong overallImportedCount;

    public ImportWorker(PacketImporter packetImporter, Packet packet, AtomicLong overallImportedCount) {
        this.packetImporter = packetImporter;
        this.packet = packet;
        this.overallImportedCount = overallImportedCount;
    }

    @Override
    public void run() {
        packetImporter.importPacket(packet);
        overallImportedCount.addAndGet(packet.size());
    }
}