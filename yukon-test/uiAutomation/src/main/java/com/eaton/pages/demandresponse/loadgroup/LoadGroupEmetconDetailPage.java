package com.eaton.pages.demandresponse.loadgroup;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;

public class LoadGroupEmetconDetailPage extends LoadGroupDetailPage {
	
	 public LoadGroupEmetconDetailPage(DriverExtensions driverExt, int id) {
		 super(driverExt, id);
	 }
	 
	 public Section getPageSection(String sectionName) {
	        return new Section(this.driverExt, sectionName);
	    }    
	 
}