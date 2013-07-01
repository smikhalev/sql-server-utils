package com.smikhalev.sqlserverutils.importdata.importer;

import com.smikhalev.sqlserverutils.importdata.*;

public class SequentialImporter extends BaseImporter {

    private long overallImportedCount = 0;

    public SequentialImporter(PacketImporter packetImporter, ImportStrategySelector selector, Iterable<RestorableAction> restorableActions, CsvLineParser csvLineParser) {
        super(packetImporter, selector, restorableActions, csvLineParser);
    }

    @Override
    protected void importPacket(Packet packet, ImportStrategy importStrategy) {
        getPacketImporter().importPacket(packet, importStrategy);
        overallImportedCount += packet.size();
    }

    @Override
    public long getResult() {
        return overallImportedCount;
    }
}
