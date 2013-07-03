package com.smikhalev.sqlserverutils.importdata.importer;

import com.smikhalev.sqlserverutils.RestorableAction;
import com.smikhalev.sqlserverutils.importdata.*;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

public class ParallelImporter extends BaseImporter {

    private final int threadCount;
    private ThreadPoolExecutor threadPool;
    private final AtomicLong overallImportedCount = new AtomicLong(0);

    public ParallelImporter(PacketImporter packetImporter, ImportStrategySelector selector, List<RestorableAction> restorableActions, CsvLineParser csvLineParser, int threadCount) {
        super(packetImporter, selector, restorableActions, csvLineParser);
        this.threadCount = threadCount;
    }

    @Override
    protected void startImport() {
        threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
    }

    @Override
    protected void importPacket(Packet packet, ImportStrategy importStrategy) {
        ImportWorker worker = new ImportWorker(getPacketImporter(), packet, importStrategy, overallImportedCount);
        threadPool.execute(worker);

        waitForContinueImportProcess();
    }

    private void waitForContinueImportProcess() {
        while(threadPool.getQueue().size() > threadCount) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new ImportException(e.getMessage(), e.getCause());
            }
        }
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
