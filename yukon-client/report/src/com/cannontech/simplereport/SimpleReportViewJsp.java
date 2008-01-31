package com.cannontech.simplereport;

public enum SimpleReportViewJsp {

    BODY("simple/htmlBodyView.jsp"),
    MENU("simple/htmlMenuView.jsp");
    
    
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
