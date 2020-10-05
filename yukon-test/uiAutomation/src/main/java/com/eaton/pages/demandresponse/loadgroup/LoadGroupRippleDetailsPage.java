package com.eaton.pages.demandresponse.loadgroup;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;

public class LoadGroupRippleDetailsPage extends LoadGroupDetailPage {
	private Section addressingSection;
	private Section doubleOrdersSection;

   public LoadGroupRippleDetailsPage(DriverExtensions driverExt, int id) {
       super(driverExt, id);
       
       addressingSection = new Section(this.driverExt, "Addressing");
       doubleOrdersSection = new Section(this.driverExt, "Double Orders");
    }
 
   public Section getAddressingSection() {
       return addressingSection;
   }
   
   public Section getDoubleOrdersSection() {
       return doubleOrdersSection;
   }   
}