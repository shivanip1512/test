<%
	// Map of page name / link text
	String linkMap[][] = {{"Update.jsp", "General"},
						  {"Contacts.jsp", "Contacts"},
						  {"Residence.jsp", "Residence"},
						  {"Calls.jsp", "Call Tracking"},
						  {"Metering.jsp", "Interval Data"},
						  {"Usage.jsp", "Usage"},
						  {"ProgramHist.jsp", "Control History"},
						  {"Programs.jsp", "Enrollment"},
						  {"OptOut.jsp", "Opt Out"},
						  {"CreateCalls.jsp", "Create Call"},
						  {"Service.jsp", "Service Request"},
						  {"ServiceSummary.jsp", "Service History"},
						  {"Password.jsp", "Change Password"},
						  {"Privileges.jsp", "Privileges"},
						  {"CreateWizard.jsp", "Create"},
						  {"PrintExport.jsp", "Print/Export"},
						  {"CreateAppliances.jsp", "New"},
						  {"CreateHardware.jsp", "New"},
						  {"ThermSchedule.jsp", "Schedule"},
						  {"Thermostat.jsp", "Manual"}
						 };
						   
	String bulletImg = ServletUtils.getECProperty( ecWebSettings.getURL(), ServletUtils.WEB_NAV_BULLET );
	if (bulletImg == null) bulletImg = "Bullet.gif";
	String bulletImg2 = ServletUtils.getECProperty( ecWebSettings.getURL(), ServletUtils.WEB_NAV_BULLET2 );
	if (bulletImg2 == null) bulletImg2 = "Bullet2.gif";
	
	Hashtable links = new Hashtable();
	for (int i = 0; i < linkMap.length; i++) {
		if (linkMap[i][0].equalsIgnoreCase(pageName))
			links.put(linkMap[i][0], "<img src=\"" + bulletImg + "\" width=\"12\" height=\"12\"><span class=\"Nav\">" + linkMap[i][1] + "</span>");
		else
			links.put(linkMap[i][0], "<img src=\"" + bulletImg2 + "\" width=\"12\" height=\"12\"><a href=\"" + linkMap[i][0] + "\" class=\"Link2\"><span class=\"NavText\">" + linkMap[i][1] + "</span></a>");
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
			appLinks[i] = "<img src=\"" + bulletImg + "\" width=\"12\" height=\"12\"><span class=\"Nav\">" + linkText + "</span>";
		else
			appLinks[i] = "<img src=\"" + bulletImg2 + "\" width=\"12\" height=\"12\"><a href=\"Appliance.jsp?AppNo=" + i + "\" class=\"Link2\"><span class=\"NavText\">" + linkText + "</span></a>";
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
			invLinks[i] = "<img src=\"" + bulletImg + "\" width=\"12\" height=\"12\"><span class=\"Nav\">" + linkText + "</span>";
		else
			invLinks[i] = "<img src=\"" + bulletImg2 + "\" width=\"12\" height=\"12\"><a href=\"Inventory.jsp?InvNo=" + i + "\" class=\"Link2\"><span class=\"NavText\">" + linkText + "</span></a>";
	}
%>

<table width="101" border="0" cellspacing="0" cellpadding="5">
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_ACCOUNT %>">
  <tr> 
    <td> 
      <div align="left"><span class="Header2">Account</span><br>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_ACCOUNT_GENERAL %>">
        <%= links.get("Update.jsp") %><br>
        <%= links.get("Contacts.jsp") %><br>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_ACCOUNT_RESIDENCE %>">
		<%= links.get("Residence.jsp") %><br>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_ACCOUNT_CALL_TRACKING %>">
		<%= links.get("Calls.jsp") %><br>
		<%= links.get("CreateCalls.jsp") %><br>
</cti:checkRole>
      </div>
    </td>
  </tr>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_METERING %>">
  <tr> 
    <td> 
      <div align="left"><span class="Header2">Metering</span><br>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_METERING_INTERVAL_DATA %>">
        <%= links.get("Metering.jsp") %><br>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_METERING_USAGE %>">
        <%= links.get("Usage.jsp") %><br>
</cti:checkRole>
      </div>
    </td>
  </tr>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_PROGRAMS %>">
  <tr> 
    <td> 
      <div align="left"><span class="Header2">Programs</span><br>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_PROGRAMS_CONTROL_HISTORY %>">
        <%= links.get("ProgramHist.jsp") %><br>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_PROGRAMS_ENROLLMENT %>">
		<%= links.get("Programs.jsp") %><br>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_PROGRAMS_OPTOUT %>">
        <%= links.get("OptOut.jsp") %><br>
</cti:checkRole>
      </div>
    </td>
  </tr>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_APPLIANCES %>">
  <tr> 
    <td> 
      <div align="left"><span class="Header2">Appliances</span><br>
<%
		for (int i = 0; i < appLinks.length; i++) {
%>
        <%= appLinks[i] %><br>
<%
		}
%>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_APPLIANCES_CREATE %>">
        <%= links.get("CreateAppliances.jsp") %><br>
</cti:checkRole>
	  </div>
    </td>
  </tr>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_HARDWARES %>">
  <tr> 
    <td> 
      <div align="left"><span class="Header2">Hardware</span><br>
        <%
		for (int i = 0; i < invLinks.length; i++) {
%>
        <%= invLinks[i] %><br>
<%
			if (thermoSettings != null &&
				inventories.getStarsLMHardware(i).getInventoryID() == thermoSettings.getInventoryID()) {
%>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_THERMOSTAT %>">
	    <table width="90" border="0" cellspacing="0" cellpadding="0">
          <tr> 
            <td width="12">&nbsp;</td>
            <td width="78">
			  <%= links.get("ThermSchedule.jsp") %><br>
              <%= links.get("Thermostat.jsp") %><br>
			</td>
          </tr>
        </table>
</cti:checkRole>
<%
			}
		}
%>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_HARDWARES_CREATE %>">
		<%= links.get("CreateHardware.jsp") %><br>
</cti:checkRole>
      </div>
    </td>
  </tr>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_WORKORDERS %>">
  <tr> 
    <td> 
      <div align="left"><span class="Header2">Work Orders</span><br>
        <%= links.get("Service.jsp") %><br>
        <%= links.get("ServiceSummary.jsp") %><br>
	  </div>
    </td>
  </tr>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_ADMIN %>">
  <tr> 
    <td> 
      <div align="left"><span class="Header2">Administration</span><br>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_ADMIN_CHANGE_PASSWORD %>">
        <%= links.get("Password.jsp") %><br>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_NOT_IMPLEMENTED %>">
        <%= links.get("Privileges.jsp")%><br>
		<%= links.get("PrintExport.jsp")%><br>
</cti:checkRole>
	  </div>
    </td>
  </tr>
</cti:checkRole>
</table>
