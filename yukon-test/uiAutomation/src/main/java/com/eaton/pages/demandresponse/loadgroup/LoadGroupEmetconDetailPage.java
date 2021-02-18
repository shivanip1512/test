package com.eaton.pages.demandresponse.loadgroup;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;

public class LoadGroupEmetconDetailPage extends LoadGroupDetailPage {

    private Section addressingSection;
    
    public LoadGroupEmetconDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt, id);
        
        addressingSection = new Section(this.driverExt, "Addressing");
    }
    
    public Section getAddressingSection() {
        return addressingSection;
    }
}