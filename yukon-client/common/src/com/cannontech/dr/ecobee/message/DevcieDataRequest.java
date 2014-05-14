package com.cannontech.dr.ecobee.message;

import com.cannontech.dr.ecobee.message.partial.Selection.SelectionType;

public class DevcieDataRequest {
    private final String startDate;
    private final String startInterval;
    private final String endDate;
    private final String endInterval;
    private final String selectionType;
    private final String selectionMatch;
    private final String columns;

    public DevcieDataRequest(String startDate, int startInterval, String endDate, int endInterval,
                             SelectionType selectionType, String selectionMatch, String columns) {
        this.startDate = startDate;
        this.startInterval = Integer.toString(startInterval);
        this.endDate = endDate;
        this.endInterval = Integer.toString(endInterval);
        this.selectionType = selectionType.getEcobeeString();
        this.selectionMatch = selectionMatch;
        this.columns = columns;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getStartInterval() {
        return startInterval;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getEndInterval() {
        return endInterval;
    }

    public String getSelectionType() {
        return selectionType;
    }

    public String getSelectionMatch() {
        return selectionMatch;
    }

    public String getColumns() {
        return columns;
    }
}
