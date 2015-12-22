package com.cannontech.web.updater;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

@JsonIgnoreProperties(ignoreUnknown=true)
public final class UpdateRequest {
    private final List<String> requestTokens;
    private final long fromDate;

    /**
     * @return an immutable List
     */
    public List<String> getRequestTokens() {
        return requestTokens;
    }

    public long getFromDate() {
        return fromDate;
    }

    /**
     * fromDate must not be null. requestTokens must not be null. requestTokens can be empty
     * 
     * Throws IllegalArgumentException if fromDate or requestTokens is null;
     */
    @JsonCreator
    public UpdateRequest(@JsonProperty(value="requestTokens") List<String> requestTokens,
                         @JsonProperty(value="fromDate") Long fromDate) {
        // throws IllegalArgumentException instead of NPE
        checkArgument(fromDate != null);
        checkArgument(requestTokens != null);

        this.requestTokens = ImmutableList.copyOf(requestTokens);
        this.fromDate = fromDate.longValue();
    }
}
