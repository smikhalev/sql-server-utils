package com.smikhalev.sqlserverutils.importdata;

public class PacketInsertGenerator {

    private TableValueConstructor constructor;

    public PacketInsertGenerator(TableValueConstructor constructor) {
        this.constructor = constructor;
    }

    public String generateInsert(Packet packet) {
        String select = constructor.construct(packet.getTable(), packet.getDataTable());

        String insert = String.format("insert into %s (%s) %s",
                packet.getTable().getFullName(), packet.getTable().getColumns().generateFields(), select);

        return insert;
    }
}
