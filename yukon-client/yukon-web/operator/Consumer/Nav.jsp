<%
	// Map of page name / link text
	String linkMap[][] = {{"Update.jsp", "General"},
						  {"Contacts.jsp", "Contacts"},
						  {"Calls.jsp", "Call Tracking"},
						  {"MeterData.jsp", "Interval Data"},
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
						  {"CreateHardware.jsp", "New"}
						 };
						   
	Hashtable links = new Hashtable();
	for (int i = 0; i < linkMap.length; i++) {
		if (linkMap[i][0].equalsIgnoreCase(pageName))
			links.put(linkMap[i][0], "<img src=\"Bullet.gif\" width=\"12\" height=\"12\"><span class=\"Nav\">" + linkMap[i][1] + "</span>");
		else
			links.put(linkMap[i][0], "<img src=\"Bullet2.gif\" width=\"12\" height=\"12\"><a href=\"" + linkMap[i][0] + "\" class=\"Link2\"><span class=\"NavText\">" + linkMap[i][1] + "</span></a>");
	}
		
	String[] appLinks = new String[ appliances.getStarsApplianceCount() ];
	int lastItemType = 0;
	int itemNo = 1;
	
    for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
        StarsAppliance app = appliances.getStarsAppliance(i);
		String linkText = app.getCategoryName();
		
		if (app.getApplianceCategoryID() != lastItemType) {
			lastItemType = app.getApplianceCategoryID();
			itemNo = 1;
		}
		else {
			itemNo++;
			linkText = linkText + " (" + String.valueOf(itemNo) + ")";
		}
			
		if (pageName.equalsIgnoreCase("Appliance.jsp?AppNo=" + i))
			appLinks[i] = "<img src=\"Bullet.gif\" width=\"12\" height=\"12\"><span class=\"Nav\">" + linkText + "</span>";
		else
			appLinks[i] = "<img src=\"Bullet2.gif\" width=\"12\" height=\"12\"><a href=\"Appliance.jsp?AppNo=" + i + "\" class=\"Link2\"><span class=\"NavText\">" + linkText + "</span></a>";
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
			invLinks[i] = "<img src=\"Bullet.gif\" width=\"12\" height=\"12\"><span class=\"Nav\">" + linkText + "</span>";
		else
			invLinks[i] = "<img src=\"Bullet2.gif\" width=\"12\" height=\"12\"><a href=\"Inventory.jsp?InvNo=" + i + "\" class=\"Link2\"><span class=\"NavText\">" + linkText + "</span></a>";
	}
%>

<table width="101" border="0" cellspacing="0" cellpadding="5">
  <tr> 
    <td> 
      <div align="left"><span class="Header2">Account</span><br>
        <%= links.get("Update.jsp") %><br>
        <%= links.get("Contacts.jsp") %><br>
		<%= links.get("Calls.jsp") %><br>
		<%= links.get("CreateCalls.jsp") %></div>
    </td>
  </tr>
  <tr> 
    <td> 
      <div align="left"><span class="Header2">Metering</span><br>
        <%= links.get("MeterData.jsp") %><br>
        <%= links.get("Usage.jsp") %></div>
    </td>
  </tr>
  <tr> 
    <td> 
      <div align="left"><span class="Header2">Programs</span><br>
        <%= links.get("ProgramHist.jsp") %><br>
		<%= links.get("Programs.jsp") %><br>
        <%= links.get("OptOut.jsp") %></div>
    </td>
  </tr>
  <tr> 
    <td> 
      <div align="left"><span class="Header2">Appliances</span><br>
        <!--
	  <%= links.get("Air.jsp") %><br>
	  <%= links.get("WH.jsp") %></div>
-->
        <%
		for (int i = 0; i < appLinks.length; i++) {
%>
        <%= appLinks[i] %><br>
        <%
		}
%><%= links.get("CreateAppliances.jsp") %></div>
    </td>
  </tr>
  <tr> 
    <td> 
      <div align="left"><span class="Header2">Hardware</span><br>
        <!--
        <%= links.get("LCR5000.jsp") %><br>
        <%= links.get("Thermostat.jsp") %><br>
        <%= links.get("Meter.jsp") %></div>
-->
        <%
		for (int i = 0; i < invLinks.length; i++) {
%>
        <%= invLinks[i] %><br>
        <%
		}
%><%= links.get("CreateHardware.jsp") %></div>
    </td>
  </tr>
  <tr> 
    <td> 
      <div align="left"><span class="Header2">Work Orders</span><br>
        <%= links.get("Service.jsp") %><br>
        <%= links.get("ServiceSummary.jsp") %></div>
    </td>
  </tr>
  <tr> 
    <td> 
      <div align="left"><span class="Header2">Administration</span><br>
        <%= links.get("Password.jsp") %><br>
        <%= links.get("Privileges.jsp")%><br>
		<%= links.get("PrintExport.jsp")%></div>
    </td>
  </tr>
</table>
