package com.cannontech.simplereport;

public enum SimpleReportViewJsp {

    BODY("simple/htmlBodyView.jsp"),
    MENU("simple/htmlMenuView.jsp"),
    ARCHIVED_DATA_REPORT("reports/htmlArchivedDataReportView.jsp"),
    BULK_IMPORT_RESULTS("reports/htmlBulkImportResultsView.jsp"),
    GROUP_DEVICES("reports/htmlGroupDevicesReportView.jsp");
    
    
    private String jspPath;
    
    private SimpleReportViewJsp(String jspPath) {
        this.jspPath = jspPath;
    }

    public String getJspPath() {
        return jspPath;
    }

    public void setJspPath(String jspPath) {
        this.jspPath = jspPath;
    }
    
}
