package com.smikhalev.sqlserverutils.importdata.importer;

import com.smikhalev.sqlserverutils.RestorableAction;
import com.smikhalev.sqlserverutils.RestorableContext;
import com.smikhalev.sqlserverutils.TableReaderProvider;
import com.smikhalev.sqlserverutils.importdata.*;
import com.smikhalev.sqlserverutils.schema.Database;
import com.smikhalev.sqlserverutils.schema.dbobjects.Table;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public abstract class BaseImporter implements Importer {
    private final PacketImporter packetImporter;
    private final List<RestorableAction> restorableActions;
    private final ImportStrategySelector strategySelector;
    private final CsvLineParser csvLineParser;
    private boolean isFinished = false;

    public BaseImporter(PacketImporter packetImporter, ImportStrategySelector strategySelector, List<RestorableAction> restorableAction, CsvLineParser csvLineParser) {
        this.packetImporter = packetImporter;
        this.strategySelector = strategySelector;
        this.restorableActions = restorableAction;
        this.csvLineParser = csvLineParser;
    }

    @Override
    public void importData(Database database, TableReaderProvider readerProvider) {
        try (RestorableContext context = new RestorableContext(restorableActions)) {
            context.prepare(database);
            importDataBody(database, context, readerProvider);
        }

        isFinished = true;
    }

    private void importDataBody(Database database, RestorableContext context, TableReaderProvider readerProvider) {
        startImport();

        Map<Table, BufferedReader> tableReaders = new HashMap<>();
        try {
            for (Table table : database.getTables()) {
                Reader reader = readerProvider.provide(table);
                if (reader != null) {
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    ImportStrategy strategy = strategySelector.select(table);
                    context.prepare(strategy.getRestorableAction(table), database);
                    tableReaders.put(table, bufferedReader);
                }
            }

            while(!tableReaders.isEmpty()){
                List<Table> importedTables = new ArrayList<>();

                performImportCycle(tableReaders, importedTables);

                for(Table table : importedTables) {
                    tableReaders.remove(table);
                }
            }
        }
        finally {
            for(Reader currentReader : tableReaders.values()) {
                try {
                    currentReader.close();
                }
                catch (IOException e) {
                    throw new ImportException(e.getMessage(), e);
                }
            }
        }

        finishImport();
    }

    private void performImportCycle(Map<Table, BufferedReader> tableReaders, List<Table> importedTables) {
        for(Map.Entry<Table, BufferedReader> entry : tableReaders.entrySet()) {
            Table table = entry.getKey();
            BufferedReader reader = entry.getValue();
            try {
                boolean isReaderEnd = importPacket(table, reader);

                if (isReaderEnd) {
                    reader.close();
                    importedTables.add(table);
                }
            }
            catch (IOException e) {
                throw new ImportException(e.getMessage(), e);
            }
        }
    }

    private boolean importPacket(Table table, BufferedReader reader) throws IOException {
        String line;
        ImportStrategy strategy = strategySelector.select(table);
        Packet packet = new Packet(table, strategy.getSize());
        while ((line = reader.readLine()) != null) {
            List<String> lineValues = csvLineParser.parse(line);
            packet.add(lineValues);

            if (packet.isFull()) {
                importPacket(packet);
                return false;
            }
        }

        if (!packet.isEmpty()) {
            importPacket(packet);
        }

        return true;
    }

    protected void startImport() {
    }

    protected void finishImport() {
    }

    private void importPacket(Packet packet) {
        ImportStrategy strategy = strategySelector.select(packet.getTable());
        importPacket(packet, strategy);
    }

    protected abstract void importPacket(Packet packet, ImportStrategy importStrategy);

    protected PacketImporter getPacketImporter() {
        return packetImporter;
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }
}
