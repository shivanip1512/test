package com.cannontech.web.updater;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public final class UpdateResponse {
    private final Map<String, String> data;
    private final long toDate;

    /**
     * @return an immutable Map containing <Requested Token, Value>
     */
    public Map<String, String> getData() {
        return data;
    }

    public long getToDate() {
        return toDate;
    }

    public UpdateResponse(Map<String, String> data, long toDate) {
        this.data = ImmutableMap.copyOf(data);
        this.toDate = toDate;
    }
}
