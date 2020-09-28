package com.eaton.pages.demandresponse.loadgroup;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;

public class LoadGroupPointDetailsPage extends LoadGroupDetailPage {

    private Section pointGroupSection;
    
    public LoadGroupPointDetailsPage(DriverExtensions driverExt, int id) {
        super(driverExt, id);
        
        pointGroupSection = new Section(this.driverExt, "Point Group");
    }
    
    public Section getPointGroupSection() {
        return pointGroupSection;
    }
}