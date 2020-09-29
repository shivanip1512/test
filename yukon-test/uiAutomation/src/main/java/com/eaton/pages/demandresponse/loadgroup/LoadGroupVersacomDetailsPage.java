package com.eaton.pages.demandresponse.loadgroup;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;

public class LoadGroupVersacomDetailsPage extends LoadGroupDetailPage {
	 private Section addressUsageSection;
	 private Section addressingSection;
	 private Section relayUsageSection;

   public LoadGroupVersacomDetailsPage(DriverExtensions driverExt, int id) {
       super(driverExt, id);
       
       addressUsageSection = new Section(this.driverExt, "Address Usage");
       addressingSection = new Section(this.driverExt, "Addressing");
       relayUsageSection = new Section(this.driverExt, "Relay Usage");
   }
 
   public Section getAddressUsageSection() {
       return addressUsageSection;
   }
   
   public Section getAddressingSection() {
       return addressingSection;
   }
   
   public Section getRelayUsageSection() {
       return relayUsageSection;
   }
}