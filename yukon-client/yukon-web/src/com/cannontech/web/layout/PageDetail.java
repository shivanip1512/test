package com.cannontech.web.layout;

public class PageDetail {
    private String pageTitle;
    private String pageHeading;
    private String sideHeading;
    private String breadCrumbText;
    private String renderContextualNavigation;
    private String detailInfoIncludePath;
    
    public String getPageTitle() {
        return pageTitle;
    }
    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }
    public String getSideHeading() {
        return sideHeading;
    }
    public void setSideHeading(String sideHeading) {
        this.sideHeading = sideHeading;
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
    public void setContextualNavigationText(String renderContextualNavigation) {
        this.renderContextualNavigation = renderContextualNavigation;
    }
    public String getRenderContextualNavigation() {
        return renderContextualNavigation;
    }
    public void setDetailInfoIncludePath(String detailInfoIncludePath) {
        this.detailInfoIncludePath = detailInfoIncludePath;
    }
    public String getDetailInfoIncludePath() {
        return detailInfoIncludePath;
    }
}
