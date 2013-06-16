package com.smikhalev.sqlserverutils.importdata.importer;

import com.smikhalev.sqlserverutils.importdata.Packet;
import com.smikhalev.sqlserverutils.importdata.PacketImporter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelImporter extends BaseImporter {

    private ExecutorService threadPool;

    public ParallelImporter(PacketImporter packetImporter, int chunkSize, int threadCount) {
        super(packetImporter, chunkSize);
        threadPool = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    protected void importPacket(Packet packet) {
        ImportWorker worker = new ImportWorker(getPacketImporter(), packet);
        threadPool.execute(worker);
    }

    @Override
    protected void finishImport() {
        threadPool.shutdown();

        while (!threadPool.isTerminated()) {
        }
    }
}
