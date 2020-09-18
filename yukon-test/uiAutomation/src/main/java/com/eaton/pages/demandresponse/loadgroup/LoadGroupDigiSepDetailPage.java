package com.eaton.pages.demandresponse.loadgroup;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;

public class LoadGroupDigiSepDetailPage extends LoadGroupDetailPage {
	
	 public LoadGroupDigiSepDetailPage(DriverExtensions driverExt, int id) {
		 super(driverExt, id);
	 }
	 
	 public Section getPageSection(String sectionName) {
	        return new Section(this.driverExt, sectionName);
	    }    
	 
}