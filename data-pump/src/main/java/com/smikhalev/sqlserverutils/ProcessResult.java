package com.smikhalev.sqlserverutils;

import java.util.Date;

public class ProcessResult {
    private final long allRows;
    private final long processedRows;
    private final long startTime;
    private final boolean isFinished;
    private final boolean isFailed;

    public ProcessResult(long allRows, long processedRows, long startTime, boolean isFinished, boolean isFailed) {
        this.allRows = allRows;
        this.processedRows = processedRows;
        this.startTime = startTime;
        this.isFinished = isFinished;
        this.isFailed = isFailed;
    }

    public long getAllRows() {
        return allRows;
    }

    public boolean isStarted() {
        return allRows >= processedRows;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean isFailed() {
        return isFailed;
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
