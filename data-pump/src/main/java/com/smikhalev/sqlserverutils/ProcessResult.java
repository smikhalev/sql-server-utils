package com.smikhalev.sqlserverutils;

public class ProcessResult {
    private long allRows;
    private long processedRows;

    public ProcessResult(long allRows, long processedRows) {
        this.allRows = allRows;
        this.processedRows = processedRows;
    }

    public long getAllRows() {
        return allRows;
    }

    public boolean isStarted() {
        return allRows >= processedRows;
    }

    public boolean isFinished() {
        return allRows == processedRows;
    }

    public long getProcessedRows() {
        return processedRows;
    }

    public double getPercent() {
        return getProcessedRows() * 100.0 / getAllRows();
    }

    public String getMessage() {
        return String.format("%s\\%s - %.2f", getProcessedRows(), getAllRows(), getPercent());
    }

}
