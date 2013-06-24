package com.smikhalev.sqlserverutils.importdata.importer;

import com.smikhalev.sqlserverutils.importdata.Packet;
import com.smikhalev.sqlserverutils.importdata.PacketImporter;
import com.smikhalev.sqlserverutils.importdata.RestorableAction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class ParallelImporter extends BaseImporter {

    private AtomicLong overallImportedCount = new AtomicLong(0);
    private ExecutorService threadPool;

    public ParallelImporter(PacketImporter packetImporter, Iterable<RestorableAction> restorableActions, int chunkSize, int threadCount) {
        super(packetImporter, restorableActions, chunkSize);
        threadPool = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    protected void importPacket(Packet packet) {
        ImportWorker worker = new ImportWorker(getPacketImporter(), packet, overallImportedCount);
        threadPool.execute(worker);
    }

    @Override
    protected void finishImport() {
        threadPool.shutdown();

        while (!threadPool.isTerminated()) {
        }
    }

    @Override
    public long getResult() {
        return overallImportedCount.longValue();
    }
}
