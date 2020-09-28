package com.eaton.pages.demandresponse.loadgroup;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;

public class LoadGroupExpresscomDetailsPage extends LoadGroupDetailPage {
	 private Section geographicalAddressSection;
	 private Section geographicalAddressingSection;
	 private Section loadAddressSection;
	 private Section loadAddressingSection;

    public LoadGroupExpresscomDetailsPage(DriverExtensions driverExt, int id) {
        super(driverExt, id);
        
        geographicalAddressSection = new Section(this.driverExt, "Geographical Address");
        geographicalAddressingSection = new Section(this.driverExt, "Geographical Addressing");
        loadAddressSection = new Section(this.driverExt, "Load Address");
        loadAddressingSection = new Section(this.driverExt, "Load Addressing");
    }
  
    public Section getGeographicalAddressSection() {
        return geographicalAddressSection;
    }
    
    public Section getGeographicalAddressingSection() {
        return geographicalAddressingSection;
    }
    
    public Section getLoadAddressSection() {
        return loadAddressSection;
    }
    
    public Section getLoadAddressingSection() {
        return loadAddressingSection;
    }
}