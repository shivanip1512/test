package com.eaton.pages.support;

import com.eaton.elements.Section;
import com.eaton.elements.SimpleList;
import com.eaton.elements.Refresh;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class SiteMapPage extends PageBase {

	//Public
    public static final String DEFAULT_URL = Urls.SITE_MAP;
    
    //Private
    private Refresh<Section> amiSection = new Refresh<Section>();
    private Refresh<SimpleList> amiSimpleList = new Refresh<SimpleList>();
    private Refresh<Section> drSection = new Refresh<Section>();
    private Refresh<SimpleList> drSimpleList = new Refresh<SimpleList>();
    private Refresh<Section> ccSection = new Refresh<Section>();
    private Refresh<SimpleList> ccSimpleList = new Refresh<SimpleList>();
    private Refresh<Section> assetsSection = new Refresh<Section>();
    private Refresh<SimpleList> assetsSimpleList = new Refresh<SimpleList>();
    private Refresh<Section> toolsSection = new Refresh<Section>();
    private Refresh<SimpleList> toolsSimpleList = new Refresh<SimpleList>();
    private Refresh<Section> adminSection = new Refresh<Section>();
    private Refresh<SimpleList> adminSimpleList = new Refresh<SimpleList>();
    private Refresh<Section> supportSection = new Refresh<Section>();
    private Refresh<SimpleList> supportSimpleList = new Refresh<SimpleList>();

    
	//================================================================================
    // Constructors Section
    //================================================================================
    
    public SiteMapPage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = DEFAULT_URL;
        
    }
    
	//================================================================================
    // Public Functions Section
    //================================================================================
    
    public void Refresh()
    {
    	amiSection.setIsDirty(true);
    	amiSimpleList.setIsDirty(true);
    	drSection.setIsDirty(true);
    	drSimpleList.setIsDirty(true);
    	ccSection.setIsDirty(true);
    	ccSimpleList.setIsDirty(true);
    	assetsSection.setIsDirty(true);
    	assetsSimpleList.setIsDirty(true);
    	toolsSection.setIsDirty(true);
    	toolsSimpleList.setIsDirty(true);
    	adminSection.setIsDirty(true);
    	adminSimpleList.setIsDirty(true);
    	supportSection.setIsDirty(true);
    	supportSimpleList.setIsDirty(true);
    }
    
    //================================================================================
    // Getters/Setters Section
    //================================================================================
    
    public Section getAMISection() {
    	if(amiSection.getIsDirty())	{
    		amiSection.setObject(new Section(driverExt, "AMI"));
    	}
    	return amiSection.getObject();
    }
    
    public SimpleList getAMISectionSimpleList() {
    	if(amiSimpleList.getIsDirty())	{
    		amiSimpleList.setObject(new SimpleList(driverExt, "simple-list", getAMISection().getSection()));
    	}
    	return amiSimpleList.getObject();
    }
    
    public Section getDRSection() {
    	if(drSection.getIsDirty())	{
    		drSection.setObject(new Section(driverExt, "Demand Response"));
    	}
    	return drSection.getObject();
    }
    
    public SimpleList getDRSectionSimpleList() {
    	if(drSimpleList.getIsDirty())	{
    		drSimpleList.setObject(new SimpleList(driverExt, "simple-list", getDRSection().getSection()));
    	}
    	return drSimpleList.getObject();
    }
    
    public Section getCCSection() {
    	if(ccSection.getIsDirty())	{
    		ccSection.setObject(new Section(driverExt, "Volt/Var"));
    	}
    	return ccSection.getObject();
    }
    
    public SimpleList getCCSectionSimpleList() {
    	if(ccSimpleList.getIsDirty()) {
    		ccSimpleList.setObject(new SimpleList(driverExt, "simple-list", getCCSection().getSection()));
    	}
    	return ccSimpleList.getObject();
    }
    
    public Section getAssetsSection() {
    	if(assetsSection.getIsDirty()) {
    		assetsSection.setObject(new Section(driverExt, "Assets"));
    	}
    	return assetsSection.getObject();
    }
    
    public SimpleList getAssetsSectionSimpleList() {
    	if(assetsSimpleList.getIsDirty()) {
    		assetsSimpleList.setObject(new SimpleList(driverExt, "simple-list", getAssetsSection().getSection()));
    	}
    	return assetsSimpleList.getObject();
    }
    
    public Section getToolsSection() {
    	if(toolsSection.getIsDirty()) {
    		toolsSection.setObject(new Section(driverExt, "Tools"));
    	}
    	return toolsSection.getObject();
    }
    
    public SimpleList getToolsSectionSimpleList() {
    	if(toolsSimpleList.getIsDirty()) {
    		toolsSimpleList.setObject(new SimpleList(driverExt, "simple-list", getToolsSection().getSection()));
    	}
    	return toolsSimpleList.getObject();
    }
    
    public Section getAdminSection() {
    	if(adminSection.getIsDirty()) {
    		adminSection.setObject(new Section(driverExt, "Admin"));
    	}
    	return adminSection.getObject();
    }
    
    public SimpleList getAdminSectionSimpleList() {
    	if(adminSimpleList.getIsDirty()) {
    		adminSimpleList.setObject(new SimpleList(driverExt, "simple-list", getAdminSection().getSection()));
    	}
    	return adminSimpleList.getObject();
    }
    
    public Section getSupportSection() {
    	if(supportSection.getIsDirty()) {
    		supportSection.setObject(new Section(driverExt, "Support"));
    	}
    	return supportSection.getObject();
    }
    
    public SimpleList getSupportSectionSimpleList() {
    	if(supportSimpleList.getIsDirty()) {
    		supportSimpleList.setObject(new SimpleList(driverExt, "simple-list", getSupportSection().getSection()));
    	}
    	return supportSimpleList.getObject();
    }
}
