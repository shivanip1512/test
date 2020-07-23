package com.eaton.pages.support;

import com.eaton.elements.Section;
import com.eaton.elements.SimpleList;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class SiteMapPage extends PageBase {

        //Public
    public static final String DEFAULT_URL = Urls.SITE_MAP;
    
    //Private
    private Section amiSection;
    private SimpleList amiSectionList;
    private Section drSection;
    private SimpleList drSectionList;
    private Section ccSection;
    private SimpleList ccSectionList;
    private Section assetsSection;
    private SimpleList assetsSectionList;
    private Section toolsSection;
    private SimpleList toolsSectionList;
    private Section adminSection;
    private SimpleList adminSectionList;
    private Section supportSection;
    private SimpleList supportSectionList;
    
    //================================================================================
    // Constructors Section
    //================================================================================
    

    public SiteMapPage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = DEFAULT_URL;
        
    }
        
    //================================================================================
    // Getters/Setters Section
    //================================================================================
    
    public Section getAMISection() {
        if(amiSection == null) {
                amiSection = new Section(driverExt, "AMI");
        }
        return amiSection;
    }
    
    public SimpleList getAMISectionSimpleList() {
        if(amiSectionList == null) {
                amiSectionList = new SimpleList(driverExt, "simple-list", getAMISection().getSection());
        }
        return amiSectionList;
    }
    
    public Section getDRSection() {
        if(drSection == null) {
                drSection = new Section(driverExt, "Demand Response");
        }
        return drSection;
    }
    
    public SimpleList getDRSectionSimpleList() {
        if(drSectionList == null) {
                drSectionList = new SimpleList(driverExt, "simple-list", getDRSection().getSection());
        }
        return drSectionList;
    }
    
    public Section getCCSection() {
        if(ccSection == null) {
                ccSection = new Section(driverExt, "Volt/Var");
        }
        return ccSection;
    }
    
    public SimpleList getCCSectionSimpleList() {
        if(ccSectionList == null) {
                ccSectionList = new SimpleList(driverExt, "simple-list", getCCSection().getSection());
        }
        return ccSectionList; 
    }
    
    public Section getAssetsSection() {
        if(assetsSection == null) {
                assetsSection = new Section(driverExt, "Assets");
        }
        return assetsSection;
    }
    
    public SimpleList getAssetsSectionSimpleList() {
        if(assetsSectionList == null) {
                assetsSectionList = new SimpleList(driverExt, "simple-list", getAssetsSection().getSection());
        }
        return assetsSectionList; 
    }
    
    public Section getToolsSection() {
        if(toolsSection == null) {
                toolsSection = new Section(driverExt, "Tools");
        }
        return toolsSection;
    }
    
    public SimpleList getToolsSectionSimpleList() {
        if(toolsSectionList == null) {
                toolsSectionList = new SimpleList(driverExt, "simple-list", getToolsSection().getSection());
        }
        return toolsSectionList;
    }
    
    public Section getAdminSection() {
        if(adminSection == null) {
                adminSection = new Section(driverExt, "Admin");
        }
        return adminSection;
    }
    
    public SimpleList getAdminSectionSimpleList() {
        if(adminSectionList == null) {
                adminSectionList = new SimpleList(driverExt, "simple-list", getAdminSection().getSection());
        }
        return adminSectionList;
    }
    
    public Section getSupportSection() {
        if(supportSection == null) {
                supportSection = new Section(driverExt, "Support");
        }
        return supportSection;
    }
    
    public SimpleList getSupportSectionSimpleList() {
        if(supportSectionList == null) {
                supportSectionList = new SimpleList(driverExt, "simple-list", getSupportSection().getSection());
        }
        return supportSectionList;
    }
}
