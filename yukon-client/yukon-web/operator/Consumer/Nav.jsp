<%
	// Map of page name / link text
	String linkMap[][] = {{"Update.jsp", "General"},
						  {"Contacts.jsp", "Contacts"},
						  {"Residence.jsp", "Residence"},
						  {"Calls.jsp", "Call Tracking"},
						  {"Metering.jsp", "Interval Data"},
						  {"Usage.jsp", "Usage"},
						  {"ProgramHist.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_CONTROL_HISTORY, "Control History")},
						  {"Programs.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_ENROLLMENT, "Enrollment")},
						  {"OptOut.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_OPT_OUT, "Opt Out")},
						  {"CreateCalls.jsp", "Create Call"},
						  {"Service.jsp", "Service Request"},
						  {"ServiceSummary.jsp", "Service History"},
						  {"Password.jsp", "Change Login"},
						  {"Privileges.jsp", "Privileges"},
						  {"CreateWizard.jsp", "Create"},
						  {"PrintExport.jsp", "Print/Export"},
						  {"FAQ.jsp", "FAQ"},
						  {"CreateAppliances.jsp", "New"},
						  {"CreateHardware.jsp", "New"},
						  {"ThermSchedule.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_THERM_SCHED, "Schedule")},
						  {"Thermostat.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_THERM_MANUAL, "Manual")}
						 };
						   
	String bulletImg = ServletUtils.getECProperty( ecWebSettings.getURL(), ServletUtils.WEB_NAV_BULLET );
	if (bulletImg == null) bulletImg = "Bullet.gif";
	String bulletImg2 = ServletUtils.getECProperty( ecWebSettings.getURL(), ServletUtils.WEB_NAV_BULLET2 );
	if (bulletImg2 == null) bulletImg2 = "Bullet2.gif";
	
	Hashtable links = new Hashtable();
	for (int i = 0; i < linkMap.length; i++) {
		if (linkMap[i][0].equalsIgnoreCase(pageName))
			links.put(linkMap[i][0], "<img src=\"../" + bulletImg + "\" width=\"12\" height=\"12\"><span class=\"Nav\">" + linkMap[i][1] + "</span>");
		else
			links.put(linkMap[i][0], "<img src=\"../" + bulletImg2 + "\" width=\"12\" height=\"12\"><a href=\"" + linkMap[i][0] + "\" class=\"Link2\"><span class=\"NavText\">" + linkMap[i][1] + "</span></a>");
	}
		
	String[] appLinks = new String[ appliances.getStarsApplianceCount() ];
	int lastItemType = 0;
	int itemNo = 1;
	
    for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
        StarsAppliance app = appliances.getStarsAppliance(i);
		String linkText = app.getDescription();
		
		if (app.getApplianceCategoryID() != lastItemType) {
			lastItemType = app.getApplianceCategoryID();
			itemNo = 1;
		}
		else {
			itemNo++;
			linkText = linkText + " (" + String.valueOf(itemNo) + ")";
		}
			
		if (pageName.equalsIgnoreCase("Appliance.jsp?AppNo=" + i))
			appLinks[i] = "<img src=\"../" + bulletImg + "\" width=\"12\" height=\"12\"><span class=\"Nav\">" + linkText + "</span>";
		else
			appLinks[i] = "<img src=\"../" + bulletImg2 + "\" width=\"12\" height=\"12\"><a href=\"Appliance.jsp?AppNo=" + i + "\" class=\"Link2\"><span class=\"NavText\">" + linkText + "</span></a>";
    }
	
	String[] invLinks = new String[ inventories.getStarsLMHardwareCount() ];
	lastItemType = 0;
	itemNo = 1;
	
	for (int i = 0; i < inventories.getStarsLMHardwareCount(); i++) {
		StarsLMHardware hw = inventories.getStarsLMHardware(i);
		String linkText = hw.getLMDeviceType().getContent();
		
		if (hw.getLMDeviceType().getEntryID() != lastItemType) {
			lastItemType = hw.getLMDeviceType().getEntryID();
			itemNo = 1;
		}
		else {
			itemNo++;
			linkText = linkText + " (" + String.valueOf(itemNo) + ")";
		}
		
		if (pageName.equalsIgnoreCase("Inventory.jsp?InvNo=" + i))
			invLinks[i] = "<img src=\"../" + bulletImg + "\" width=\"12\" height=\"12\"><span class=\"Nav\">" + linkText + "</span>";
		else
			invLinks[i] = "<img src=\"../" + bulletImg2 + "\" width=\"12\" height=\"12\"><a href=\"Inventory.jsp?InvNo=" + i + "\" class=\"Link2\"><span class=\"NavText\">" + linkText + "</span></a>";
	}
%>

<table width="101" border="0" cellspacing="0" cellpadding="5">
<cti:checkMultiProperty propertyid="<%= String.valueOf(ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_GENERAL) + "," + String.valueOf(ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_RESIDENCE) + "," + String.valueOf(ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_CALL_TRACKING) %>">
  <tr> 
    <td> 
      <div align="left"><span class="PageHeader">Account</span><br>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_GENERAL %>">
        <%= links.get("Update.jsp") %><br>
        <%= links.get("Contacts.jsp") %><br>
</cti:checkProperty>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_RESIDENCE %>">
		<%= links.get("Residence.jsp") %><br>
</cti:checkProperty>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_CALL_TRACKING %>">
		<%= links.get("Calls.jsp") %><br>
		<%= links.get("CreateCalls.jsp") %><br>
</cti:checkProperty>
      </div>
    </td>
  </tr>
</cti:checkMultiProperty>
<cti:checkMultiProperty propertyid="<%= String.valueOf(ConsumerInfoRole.CONSUMER_INFO_METERING_INTERVAL_DATA) + "," + String.valueOf(ConsumerInfoRole.CONSUMER_INFO_METERING_USAGE) %>">
  <tr> 
    <td> 
      <div align="left"><span class="PageHeader">Metering</span><br>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_METERING_INTERVAL_DATA %>">
        <%= links.get("Metering.jsp") %><br>
</cti:checkProperty>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_METERING_USAGE %>">
        <%= links.get("Usage.jsp") %><br>
</cti:checkProperty>
      </div>
    </td>
  </tr>
</cti:checkMultiProperty>
<cti:checkMultiProperty propertyid="<%= String.valueOf(ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY) + "," + String.valueOf(ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_ENROLLMENT) + "," + String.valueOf(ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPTOUT) %>">
  <tr> 
    <td> 
      <div align="left"><span class="PageHeader">Programs</span><br>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY %>">
        <%= links.get("ProgramHist.jsp") %><br>
</cti:checkProperty>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_ENROLLMENT %>">
		<%= links.get("Programs.jsp") %><br>
</cti:checkProperty>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPTOUT %>">
        <%= links.get("OptOut.jsp") %><br>
</cti:checkProperty>
      </div>
    </td>
  </tr>
</cti:checkMultiProperty>
<cti:checkMultiProperty propertyid="<%= String.valueOf(ConsumerInfoRole.CONSUMER_INFO_APPLIANCES) + "," + String.valueOf(ConsumerInfoRole.CONSUMER_INFO_APPLIANCES_CREATE) %>">
  <tr> 
    <td> 
      <div align="left"><span class="PageHeader">Appliances</span><br>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_APPLIANCES %>">
<%
		for (int i = 0; i < appLinks.length; i++) {
%>
        <%= appLinks[i] %><br>
<%
		}
%>
</cti:checkProperty>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_APPLIANCES_CREATE %>">
        <%= links.get("CreateAppliances.jsp") %><br>
</cti:checkProperty>
	  </div>
    </td>
  </tr>
</cti:checkMultiProperty>
<cti:checkMultiProperty propertyid="<%= String.valueOf(ConsumerInfoRole.CONSUMER_INFO_HARDWARES) + "," + String.valueOf(ConsumerInfoRole.CONSUMER_INFO_HARDWARES_CREATE) %>">
  <tr> 
    <td> 
      <div align="left"><span class="PageHeader">Hardware</span><br>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_HARDWARES %>">
<%
		for (int i = 0; i < invLinks.length; i++) {
%>
        <%= invLinks[i] %><br>
<%
			if (thermoSettings != null
				&& inventories.getStarsLMHardware(i).getInventoryID() == thermoSettings.getInventoryID()
				&& AuthFuncs.checkRoleProperty(lYukonUser, ConsumerInfoRole.CONSUMER_INFO_HARDWARES_THERMOSTAT)) {
%>
	    <table width="90" border="0" cellspacing="0" cellpadding="0">
          <tr> 
            <td width="12">&nbsp;</td>
            <td width="78">
			  <%= links.get("ThermSchedule.jsp") %><br>
              <%= links.get("Thermostat.jsp") %><br>
			</td>
          </tr>
        </table>
<%
			}
		}
%>
</cti:checkProperty>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_HARDWARES_CREATE %>">
		<%= links.get("CreateHardware.jsp") %><br>
</cti:checkProperty>
      </div>
    </td>
  </tr>
</cti:checkMultiProperty>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_WORK_ORDERS %>">
  <tr> 
    <td> 
      <div align="left"><span class="PageHeader">Work Orders</span><br>
        <%= links.get("Service.jsp") %><br>
        <%= links.get("ServiceSummary.jsp") %><br>
	  </div>
    </td>
  </tr>
</cti:checkProperty>
<cti:checkMultiProperty propertyid="<%= String.valueOf(ConsumerInfoRole.CONSUMER_INFO_ADMIN_CHANGE_LOGIN) + "," + String.valueOf(ConsumerInfoRole.CONSUMER_INFO_ADMIN_FAQ) %>">
  <tr> 
    <td> 
      <div align="left"><span class="PageHeader">Administration</span><br>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_ADMIN_CHANGE_LOGIN %>">
        <%= links.get("Password.jsp") %><br>
</cti:checkProperty>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_ADMIN_FAQ %>">
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CUSTOMIZED_FAQ_LINK %>">
		<img src="../<%= bulletImg2 %>" width="12" height="12"><a href="<cti:getProperty propertyid="<%= ConsumerInfoRole.WEB_LINK_FAQ %>"/>" class="Link2" target="new"><span class="NavText">FAQ</span></a><br>
</cti:checkProperty>
<cti:checkNoProperty propertyid="<%= ConsumerInfoRole.CUSTOMIZED_FAQ_LINK %>">
        <%= links.get("FAQ.jsp") %><br>
</cti:checkNoProperty>
</cti:checkProperty>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_NOT_IMPLEMENTED %>">
        <%= links.get("Privileges.jsp")%><br>
		<%= links.get("PrintExport.jsp")%><br>
</cti:checkProperty>
	  </div>
    </td>
  </tr>
</cti:checkMultiProperty>
</table>
