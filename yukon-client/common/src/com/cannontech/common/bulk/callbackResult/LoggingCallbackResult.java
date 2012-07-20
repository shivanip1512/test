package com.cannontech.common.bulk.callbackResult;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Combined callback and results object containing methods for populating a UI log with information
 * from the background process.
 */
public abstract class LoggingCallbackResult<A, B> extends CollectingBulkProcessorCallback<A, B> implements BackgroundProcessResultHolder {
    protected List<String> log = Lists.newArrayList();
    protected int logIndex = 0;
    
    /**
     * Resets the log index to zero, so getNextLogLine() and
     * getNewLogLines() will return values starting at the beginning
     * of the list next time they are called.
     */
    public boolean resetLog() {
        logIndex = 0;
        return true;
    }
    
    /**
     * Returns a single log line per call, remembering the current 
     * position in the log for subsequent calls. Call resetLog() to reset
     * the index and begin returning lines from the beginning of the log
     * again. 
     */
    public String getNextLogLine() {
        String logLine = null;
        try {
            logLine = log.get(logIndex);
            logIndex++;
        } catch(IndexOutOfBoundsException e) {
            //index is past the last line. return null
        }
        
        return logLine;
    }
    
    /**
     * Returns all unread log lines in a List, based on the position
     * of the log index. Call resetLog() to reset the index and begin
     * returning lines from the beginning of the log again.
     */
    public List<String> getNewLogLines() {
        List<String> logLines = Lists.newArrayList();
        String logLine = getNextLogLine();
        while(logLine != null) {
            logLines.add(logLine);
            logLine = getNextLogLine();
        }
        return logLines;
    }
    
    public int getLogIndex() {
        return logIndex;
    }
    
    public List<String> getLog() {
        return log;
    }
}
