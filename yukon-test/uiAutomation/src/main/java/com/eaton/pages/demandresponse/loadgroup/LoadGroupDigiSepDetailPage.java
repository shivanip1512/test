package com.eaton.pages.demandresponse.loadgroup;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;

public class LoadGroupDigiSepDetailPage extends LoadGroupDetailPage {

    private Section deviceClassSection;
    private Section enrollmentSection;
    private Section timingSection;
    
    public LoadGroupDigiSepDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt, id);
        
        deviceClassSection = new Section(this.driverExt, "Device Class");
        enrollmentSection = new Section(this.driverExt, "Enrollment");
        timingSection = new Section(this.driverExt, "Timing");
    }
    
    public Section getDeviceClassSection() {
        return deviceClassSection;
    }
    
    public Section getEnrollmentSection() {
        return enrollmentSection;
    }
    
    public Section getTimingSection() {
        return timingSection;
    }
}
