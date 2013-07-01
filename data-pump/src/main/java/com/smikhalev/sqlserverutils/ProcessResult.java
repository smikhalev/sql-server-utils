package com.smikhalev.sqlserverutils;

import java.util.Date;

public class ProcessResult {
    private long allRows;
    private long processedRows;
    private long startTime;

    public ProcessResult(long allRows, long processedRows, long startTime) {
        this.allRows = allRows;
        this.processedRows = processedRows;
        this.startTime = startTime;
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

    public Date getElapsedTime() {
        return new Date(System.currentTimeMillis() - startTime);
    }

    public double getAverageSpeedRowsInMs(){
        return processedRows * 1.0 / getElapsedTime().getTime();
    }

    public String getMessage() {
        return String.format("%s sec - %s/%s - %.2f%% - %.2f rows/sec",
                getElapsedTime().getTime() / 1000,
                getProcessedRows(), getAllRows(), getPercent(),
                getAverageSpeedRowsInMs() * 1000);
    }
}
