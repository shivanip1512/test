package com.eaton.pages.demandresponse;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupEditPage;

public class LoadGroupEmetconEditPage extends LoadGroupEditPage{
	private TextEditElement goldAddress;
	private TextEditElement silverAddress;
	private RadioButtonElement addressUsage;
	private RadioButtonElement relayUsage;
	
	public LoadGroupEmetconEditPage(DriverExtensions driverExt, int id) {
		  super(driverExt, id);

	        requiresLogin = true;
	        pageUrl = Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT;

	        goldAddress = new TextEditElement(this.driverExt, "goldAddress");
	        silverAddress= new TextEditElement(this.driverExt, "silverAddress");
	        addressUsage= new RadioButtonElement(this.driverExt, "addressUsage", getPageSection("Addressing").getSection());
	        relayUsage= new RadioButtonElement(this.driverExt, "relayUsage", getPageSection("Addressing").getSection());
	        
	}
	
	public TextEditElement getGoldAddress() {
		return goldAddress;
	}
	
	public TextEditElement getSilverAddress() {
		return silverAddress;
	}
	
	public RadioButtonElement getaddressUsage() {
		return addressUsage;
	}
	
	public RadioButtonElement getaddressrelayUsage() {
		return relayUsage;
	}


}