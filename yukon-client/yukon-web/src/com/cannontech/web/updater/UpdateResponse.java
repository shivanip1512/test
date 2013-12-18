package com.cannontech.web.updater;

import java.util.Map;

public final class UpdateResponse {

    private final Map<String, String> data;
    private final long toDate;

    public Map<String, String> getData() {
        return data;
    }

    public long getToDate() {
        return toDate;
    }

    public UpdateResponse(Map<String, String> data, long toDate) {
        this.data = data;
        this.toDate = toDate;
    }
}
