<%
	// Map of page name / link text
	String linkPairs[][] = {{"Update.jsp", "General"},
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
						 };

	ArrayList linkList = new ArrayList();
	for (int i = 0; i < linkPairs.length; i++)
		linkList.add( linkPairs[i] );
		
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
			linkText = linkText + " (" + Integer.toString(itemNo) + ")";
		}
		
		linkList.add( new String[] {"Appliance.jsp?AppNo=" + i, linkText} );
	}
	
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
			linkText = linkText + " (" + Integer.toString(itemNo) + ")";
		}
		
		linkList.add( new String[] {"Inventory.jsp?InvNo=" + i, linkText} );
		
		if (hw.getStarsThermostatSettings() != null) {
			if (hw.getStarsThermostatSettings().getStarsThermostatDynamicData() == null) {
				linkList.add( new String[] {"ThermSchedule.jsp?InvNo=" + i, AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_THERM_SCHED, "Schedule")} );
				linkList.add( new String[] {"Thermostat.jsp?InvNo=" + i, AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_THERM_MANUAL, "Manual")} );
			}
			else {
				linkList.add( new String[] {"ThermSchedule2.jsp?InvNo=" + i, AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_THERM_SCHED, "Schedule")} );
				linkList.add( new String[] {"Thermostat2.jsp?InvNo=" + i, AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_THERM_MANUAL, "Manual")} );
			}
		}
	}

	String bulletImg = "../../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED);
	if (bulletImg == null) bulletImg = "../../WebConfig/Bullet.gif";
	String bulletImg2 = "../../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET);
	if (bulletImg2 == null) bulletImg2 = "../../WebConfig/Bullet2.gif";
	
	Hashtable links = new Hashtable();
	for (int i = 0; i < linkList.size(); i++) {
		String[] linkPair = (String[]) linkList.get(i);
		if (linkPair[0].equalsIgnoreCase(pageName))
			links.put(linkPair[0], "<img src='" + bulletImg + "' width='12' height='12'><span class='Nav'>" + linkPair[1] + "</span>");
		else
			links.put(linkPair[0], "<img src='" + bulletImg2 + "' width='12' height='12'><a href='" + linkPair[0] + "' class='Link2'><span class='NavText'>" + linkPair[1] + "</span></a>");
	}
%>

<table width="101" border="0" cellspacing="0" cellpadding="5">
<cti:checkMultiProperty propertyid="<%= Integer.toString(ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_GENERAL) + ',' + Integer.toString(ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_RESIDENCE) + ',' + Integer.toString(ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_CALL_TRACKING) %>">
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
<cti:checkMultiProperty propertyid="<%= Integer.toString(ConsumerInfoRole.CONSUMER_INFO_METERING_INTERVAL_DATA) + ',' + Integer.toString(ConsumerInfoRole.CONSUMER_INFO_METERING_USAGE) %>">
  <tr> 
    <td> 
      <div align="left"><span class="PageHeader">Metering</span><br>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_METERING_INTERVAL_DATA %>">
        <%= links.get("Metering.jsp") %><br>
       <table>
          <tr>
            <td>
<%   /* Retrieve all the predefined graphs for this user*/                       
	if( gData != null )
	{
		for( int i = 0; i < gData.length; i++ )                                                          
		{
			if( Integer.parseInt(gData[i][0].toString()) == graphBean.getGdefid())
			{%>
				<img src="<%=bulletImg%>" width="12" height="12"><span class="Nav"><%=gData[i][1] %></span><br>
			<%}
			else 
			{%>
				<img src="<%=bulletImg2%>" width="12" height="12"><a href="<%=request.getContextPath()%>/operator/Consumer/Metering.jsp?<%= "gdefid=" + gData[i][0]%>" class="Link2"><span class="NavText"><%=gData[i][1] %></span></a><br>
			<%}
		}
	}%>
	</td></tr></table>        
</cti:checkProperty>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_METERING_USAGE %>">
        <%= links.get("Usage.jsp") %><br>
</cti:checkProperty>
      </div>
    </td>
  </tr>
</cti:checkMultiProperty>
<cti:checkMultiProperty propertyid="<%= Integer.toString(ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY) + ',' + Integer.toString(ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_ENROLLMENT) + ',' + Integer.toString(ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPT_OUT) %>">
  <tr> 
    <td> 
      <div align="left"><span class="PageHeader">Programs</span><br>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY %>">
        <%= links.get("ProgramHist.jsp") %><br>
</cti:checkProperty>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_ENROLLMENT %>">
		<%= links.get("Programs.jsp") %><br>
</cti:checkProperty>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPT_OUT %>">
        <%= links.get("OptOut.jsp") %><br>
</cti:checkProperty>
      </div>
    </td>
  </tr>
</cti:checkMultiProperty>
<cti:checkMultiProperty propertyid="<%= Integer.toString(ConsumerInfoRole.CONSUMER_INFO_APPLIANCES) + ',' + Integer.toString(ConsumerInfoRole.CONSUMER_INFO_APPLIANCES_CREATE) %>">
  <tr> 
    <td> 
      <div align="left"><span class="PageHeader">Appliances</span><br>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_APPLIANCES %>">
<%
	for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
%>
        <%= links.get("Appliance.jsp?AppNo=" + i) %><br>
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
<cti:checkMultiProperty propertyid="<%= Integer.toString(ConsumerInfoRole.CONSUMER_INFO_HARDWARES) + ',' + Integer.toString(ConsumerInfoRole.CONSUMER_INFO_HARDWARES_CREATE) %>">
  <tr> 
    <td> 
      <div align="left"><span class="PageHeader">Hardware</span><br>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_HARDWARES %>">
<%
	boolean showThermostat = AuthFuncs.checkRoleProperty(lYukonUser, ConsumerInfoRole.CONSUMER_INFO_HARDWARES_THERMOSTAT);
	for (int i = 0; i < inventories.getStarsLMHardwareCount(); i++) {
%>
        <%= links.get("Inventory.jsp?InvNo=" + i) %><br>
<%
		StarsThermostatSettings settings = inventories.getStarsLMHardware(i).getStarsThermostatSettings();
		if (settings != null && showThermostat) {
%>
	    <table width="90" border="0" cellspacing="0" cellpadding="0">
          <tr> 
            <td width="12">&nbsp;</td>
            <td width="78">
<%
			if (settings.getStarsThermostatDynamicData() == null) {
%>
			  <%= links.get("ThermSchedule.jsp?InvNo=" + i) %><br>
              <%= links.get("Thermostat.jsp?InvNo=" + i) %><br>
<%
			}
			else {
%>
			  <%= links.get("ThermSchedule2.jsp?InvNo=" + i) %><br>
              <%= links.get("Thermostat2.jsp?InvNo=" + i) %><br>
<%
			}
%>
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
<cti:checkMultiProperty propertyid="<%= Integer.toString(ConsumerInfoRole.CONSUMER_INFO_ADMIN_CHANGE_LOGIN) + ',' + Integer.toString(ConsumerInfoRole.CONSUMER_INFO_ADMIN_FAQ) %>">
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
