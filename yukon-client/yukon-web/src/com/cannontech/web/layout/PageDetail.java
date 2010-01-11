package com.cannontech.web.layout;

public class PageDetail {
    private String pageTitle;
    private String pageHeading;
    private String breadCrumbText;
    
    public String getPageTitle() {
        return pageTitle;
    }
    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }
    public String getBreadCrumbText() {
        return breadCrumbText;
    }
    public void setBreadCrumbText(String breadCrumbText) {
        this.breadCrumbText = breadCrumbText;
    }
    public String getPageHeading() {
        return pageHeading;
    }
    public void setPageHeading(String pageHeading) {
        this.pageHeading = pageHeading;
    }
}
