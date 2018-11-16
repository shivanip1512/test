package com.cannontech.dr.nest.model.v3;

public class RetrieveCustomers {
    private int pageSize;
    private String pageToken;
    private EnrollmentState enrollmentStateFilter;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getPageToken() {
        return pageToken;
    }

    public void setPageToken(String pageToken) {
        this.pageToken = pageToken;
    }

    public EnrollmentState getEnrollmentStateFilter() {
        return enrollmentStateFilter;
    }

    public void setEnrollmentStateFilter(EnrollmentState enrollmentStateFilter) {
        this.enrollmentStateFilter = enrollmentStateFilter;
    }
}
