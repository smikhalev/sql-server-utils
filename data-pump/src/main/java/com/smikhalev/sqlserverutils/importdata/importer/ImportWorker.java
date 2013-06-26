package com.smikhalev.sqlserverutils.importdata.importer;

import com.smikhalev.sqlserverutils.importdata.ImportStrategy;
import com.smikhalev.sqlserverutils.importdata.Packet;
import com.smikhalev.sqlserverutils.importdata.PacketImporter;

import java.util.concurrent.atomic.AtomicLong;

public class ImportWorker implements Runnable {

    private PacketImporter packetImporter;
    private Packet packet;
    private ImportStrategy importStrategy;
    private AtomicLong overallImportedCount;

    public ImportWorker(PacketImporter packetImporter, Packet packet, ImportStrategy importStrategy, AtomicLong overallImportedCount) {
        this.packetImporter = packetImporter;
        this.packet = packet;
        this.overallImportedCount = overallImportedCount;
        this.importStrategy = importStrategy;
    }

    @Override
    public void run() {
        packetImporter.importPacket(packet, importStrategy);
        overallImportedCount.addAndGet(packet.size());
    }
}