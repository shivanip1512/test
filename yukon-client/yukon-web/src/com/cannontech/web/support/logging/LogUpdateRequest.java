package com.cannontech.web.support.logging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class LogUpdateRequest {

    private final long fileLength;
    private final int numLines;
    private final String file;

    @JsonCreator
    public LogUpdateRequest(@JsonProperty(value="fileLength") long fileLength,
                            @JsonProperty(value="numLines") int numLines,
                            @JsonProperty(value="file") String file) {
        this.fileLength = fileLength;
        this.numLines = numLines;
        this.file = file;
    }

    public long getFileLength() {
        return fileLength;
    }

    public int getNumLines() {
        return numLines;
    }

    public String getFile() {
        return file;
    }
}
