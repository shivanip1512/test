<%
	// Table of (link, page name) pairs
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
						  {"PrintExport.jsp", "Print/Export"},
						  {"FAQ.jsp", "FAQ"},
						  {"CreateAppliances.jsp", "New"},
						  {"SerialNumber.jsp", "New"},
						 };

	String bulletImg = "<img src='../../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED, "Bullet.gif") + "' width='9' height='9'>";
	String bulletImgExp = "<img src='../../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_EXPAND, "BulletExpand.gif") + "' width='9' height='9'>";
	
	// List of String[] (link image, link html)
	Hashtable links = new Hashtable();
	for (int i = 0; i < linkPairs.length; i++) {
		String linkImg = null;
		String linkHtml = null;
		if (linkPairs[i][0].equalsIgnoreCase(pageName)) {
			linkImg = bulletImg;
			linkHtml = "<span class='Nav'>" + linkPairs[i][1] + "</span>";
		}
		else {
			linkImg = "";
			linkHtml = "<a href='" + linkPairs[i][0] + "' class='Link2'><span class='NavText'>" + linkPairs[i][1] + "</span></a>";
		}
		links.put(linkPairs[i][0], new String[] {linkImg, linkHtml});
	}
%>

<table width="101" border="0" cellspacing="0" cellpadding="5">
<cti:checkMultiProperty propertyid="<%= Integer.toString(ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_GENERAL) + ',' + Integer.toString(ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_RESIDENCE) + ',' + Integer.toString(ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_CALL_TRACKING) %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Account</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_GENERAL %>"> 
          <tr> 
            <td width="10"><%= ((String[]) links.get("Update.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("Update.jsp"))[1] %></td>
          </tr>
          <tr> 
            <td width="10"><%= ((String[]) links.get("Contacts.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("Contacts.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty>
		  <cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_RESIDENCE %>">
          <tr> 
            <td width="10"><%= ((String[]) links.get("Residence.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("Residence.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty>
		  <cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_ACCOUNT_CALL_TRACKING %>"> 
          <tr> 
            <td width="10"><%= ((String[]) links.get("Calls.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("Calls.jsp"))[1] %></td>
          </tr>
          <tr> 
            <td width="10"><%= ((String[]) links.get("CreateCalls.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("CreateCalls.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty>
        </table>
      </div>
    </td>
  </tr>
</cti:checkMultiProperty>
<cti:checkMultiProperty propertyid="<%= Integer.toString(ConsumerInfoRole.CONSUMER_INFO_METERING_INTERVAL_DATA) + ',' + Integer.toString(ConsumerInfoRole.CONSUMER_INFO_METERING_USAGE) %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Metering</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_METERING_INTERVAL_DATA %>"> 
          <tr>
            <td width="10"><%= ((String[]) links.get("Metering.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("Metering.jsp"))[1] %></td>
          </tr>
          <%   /* Retrieve all the predefined graphs for this user*/                       
	if( gData != null )
	{
		for( int i = 0; i < gData.length; i++ )                                                          
		{
			String linkImg = null;
			String linkHtml = null;
			if( Integer.parseInt(gData[i][0].toString()) == graphBean.getGdefid())
			{
				linkImg = bulletImg;
				linkHtml = "<span class='Nav'>" + gData[i][1] + "</span>";
			}
			else
			{
				linkImg = "";
				linkHtml = "<a href='" + request.getContextPath() + "/operator/Consumer/Metering.jsp?gdefid=" + gData[i][0] + "' class='Link2'><span class='NavText'>" + gData[i][1] + "</span></a>";
			}%>
          <tr>
            <td width="10"><%= linkImg %></td>
            <td style="padding:1"><%= linkHtml %></td>
          </tr>
          <%
		}
	}%>
          </cti:checkProperty>
		  <cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_METERING_USAGE %>"> 
          <tr>
            <td width="10"><%= ((String[]) links.get("Usage.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("Usage.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty>
        </table>
      </div>
    </td>
  </tr>
</cti:checkMultiProperty>
<cti:checkMultiProperty propertyid="<%= Integer.toString(ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY) + ',' + Integer.toString(ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_ENROLLMENT) + ',' + Integer.toString(ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPT_OUT) %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Programs</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY %>"> 
          <tr>
            <td width="10"><%= ((String[]) links.get("ProgramHist.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("ProgramHist.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty>
		  <cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_ENROLLMENT %>"> 
          <tr>
            <td width="10"><%= ((String[]) links.get("Programs.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("Programs.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty>
		  <cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPT_OUT %>">
          <tr>
            <td width="10"><%= ((String[]) links.get("OptOut.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("OptOut.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty>
        </table>
      </div>
    </td>
  </tr>
</cti:checkMultiProperty>
<cti:checkMultiProperty propertyid="<%= Integer.toString(ConsumerInfoRole.CONSUMER_INFO_APPLIANCES) + ',' + Integer.toString(ConsumerInfoRole.CONSUMER_INFO_APPLIANCES_CREATE) %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Appliances</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_APPLIANCES %>"> 
          <%
	int lastItemType = 0;
	int itemNo = 1;
	for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
        StarsAppliance app = appliances.getStarsAppliance(i);
		
		String linkUrl = "Appliance.jsp?AppNo=" + i;
		String linkLabel = app.getDescription();
		if (app.getApplianceCategoryID() != lastItemType) {
			lastItemType = app.getApplianceCategoryID();
			itemNo = 1;
		}
		else {
			itemNo++;
			linkLabel = linkLabel + " (" + Integer.toString(itemNo) + ")";
		}
		
		String linkImg = null;
		String linkHtml = null;
		if (linkUrl.equalsIgnoreCase(pageName)) {
			linkImg = bulletImg;
			linkHtml = "<span class='Nav'>" + linkLabel + "</span>";
		}
		else {
			linkImg = "";
			linkHtml = "<a href='" + linkUrl + "' class='Link2'><span class='NavText'>" + linkLabel + "</span></a>";
		}
%>
          <tr>
            <td width="10" valign="top"><%= linkImg %></td>
            <td style="padding:1"><%= linkHtml %></td>
          </tr>
<%
	}
%>
		  </cti:checkProperty>
		  <cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_APPLIANCES_CREATE %>"> 
          <tr>
            <td width="10" valign="top"><%= ((String[]) links.get("CreateAppliances.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("CreateAppliances.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty>
        </table>
	  </div>
    </td>
  </tr>
</cti:checkMultiProperty>
<cti:checkMultiProperty propertyid="<%= Integer.toString(ConsumerInfoRole.CONSUMER_INFO_HARDWARES) + ',' + Integer.toString(ConsumerInfoRole.CONSUMER_INFO_HARDWARES_CREATE) %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Hardware</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_HARDWARES %>"> 
          <%
	// List of String[] (inventory no., bullet image, link html, expand bullet image)
	ArrayList switches = new ArrayList();
	ArrayList thermostats = new ArrayList();
	ArrayList meters = new ArrayList();
	
	for (int i = 0; i < inventories.getStarsLMHardwareCount(); i++) {
		StarsLMHardware hw = inventories.getStarsLMHardware(i);
		String linkLabel = hw.getDeviceLabel();
		
		String linkImg = null;
		String linkHtml = null;
		String linkImgExp = null;
		int pos = pageName.lastIndexOf("InvNo=");
		if (pos >= 0 && pageName.substring(pos).equals("InvNo=" + i)) {
			linkImg = bulletImg;
			linkHtml = "<span class='Nav' style='cursor:default'>" + linkLabel + "</span>";
			linkImgExp = bulletImg;
		}
		else {
			linkImg = "";
			linkHtml = "<span class='NavText' style='cursor:default; color:#FFFFFF'>" + linkLabel + "</span>";
			linkImgExp = bulletImgExp;
		}
		
		String[] linkFields = new String[] {String.valueOf(i), linkImg, linkHtml, linkImgExp};
		if (hw.getStarsThermostatSettings() != null)
			thermostats.add( linkFields );
		else
			switches.add( linkFields );
	}
%>
<script language="JavaScript" src="../../JavaScript/hardware_menu.js"></script>
<script language="JavaScript">
// Initialize variables defined in hardware_menu.js
pageName = "<%= pageName %>";
pageLinks = new Array(<%= inventories.getStarsLMHardwareCount() %>);
<%
	for (int i = 0; i < switches.size(); i++) {
		int num = Integer.parseInt( ((String[]) switches.get(i))[0] );
%>
	pageLinks[<%= num %>] = new Array(2);
	pageLinks[<%= num %>][0] = "Inventory.jsp?InvNo=" + <%= num %>;
	pageLinks[<%= num %>][1] = "ConfigHardware.jsp?InvNo=" + <%= num %>;
<%
	}
	for (int i = 0; i < thermostats.size(); i++) {
		int num = Integer.parseInt( ((String[]) thermostats.get(i))[0] );
%>
	pageLinks[<%= num %>] = new Array(4);
	pageLinks[<%= num %>][0] = "Inventory.jsp?InvNo=" + <%= num %>;
	pageLinks[<%= num %>][1] = "ConfigHardware.jsp?InvNo=" + <%= num %>;
<%
		StarsThermostatSettings settings = inventories.getStarsLMHardware(num).getStarsThermostatSettings();
		if (settings.getStarsThermostatDynamicData() == null) {
%>
	pageLinks[<%= num %>][2] = "ThermSchedule.jsp?InvNo=" + <%= num %>;
	pageLinks[<%= num %>][3] = "Thermostat.jsp?InvNo=" + <%= num %>;
<%
		}
		else {
%>
	pageLinks[<%= num %>][2] = "ThermSchedule2.jsp?InvNo=" + <%= num %>;
	pageLinks[<%= num %>][3] = "Thermostat2.jsp?InvNo=" + <%= num %>;
<%
		}
	}
	for (int i = 0; i < meters.size(); i++) {
		int num = Integer.parseInt( ((String[]) meters.get(i))[0] );
%>
	pageLinks[<%= num %>] = new Array(1);
	pageLinks[<%= num %>][0] = "Inventory.jsp?InvNo=" + <%= num %>;
<%
	}
%>
</script>
          <tr>
            <td width="10">&nbsp;</td>
            <td>
<%
	if (switches.size() > 0) {
%>
			  <span class="NavGroup">Switches</span><br>
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <%
		for (int i = 0; i < switches.size(); i++) {
			String[] linkFields = (String[]) switches.get(i);
%>
                <tr onmouseover="menuAppear(event, this, 'switchMenu', <%= linkFields[0] %>)"> 
                  <td width="10" valign="top" style="padding-top:1"><%= linkFields[1] %></td>
                  <td><%= linkFields[2] %></td>
                  <td width="10" valign="bottom" style="padding-bottom:1"><%= linkFields[3] %></td>
                </tr>
<%
		}
%>
              </table>
<%
	}
	if (thermostats.size() > 0) {
%>
              <span class="NavGroup">Thermostats</span><br>
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <%
		for (int i = 0; i < thermostats.size(); i++) {
			String[] linkFields = (String[]) thermostats.get(i);
%>
                <tr onmouseover="menuAppear(event, this, 'thermostatMenu', <%= linkFields[0] %>)"> 
                  <td width="10" valign="top" style="padding-top:1"><%= linkFields[1] %></td>
                  <td><%= linkFields[2] %></td>
                  <td width="10" valign="bottom" style="padding-bottom:1"><%= linkFields[3] %></td>
                </tr>
<%
		}
%>
              </table>
<%
	}
	if (meters.size() > 0) {
%>
              <span class="NavGroup">Meters</span><br>
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <%
		for (int i = 0; i < meters.size(); i++) {
			String[] linkFields = (String[]) meters.get(i);
%>
                <tr onmouseover="menuAppear(event, this, 'meterMenu', <%= linkFields[0] %>)"> 
                  <td width="10" valign="top" style="padding-top:1"><%= linkFields[1] %></td>
                  <td><%= linkFields[2] %></td>
                  <td width="10" valign="bottom" style="padding-bottom:1"><%= linkFields[3] %></td>
                </tr>
<%
		}
%>
              </table>
<%
	}
%>
            </td>
          </tr>
		  </cti:checkProperty>
		  <cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_HARDWARES_CREATE %>"> 
          <tr>
            <td width="10"><%= ((String[]) links.get("SerialNumber.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("SerialNumber.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty>
        </table>
      </div>
    </td>
  </tr>
</cti:checkMultiProperty>
<cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_WORK_ORDERS %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Work Orders</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td width="10"><%= ((String[]) links.get("Service.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("Service.jsp"))[1] %></td>
          </tr>
          <tr>
            <td width="10"><%= ((String[]) links.get("ServiceSummary.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("ServiceSummary.jsp"))[1] %></td>
          </tr>
        </table>
	  </div>
    </td>
  </tr>
</cti:checkProperty>
<cti:checkMultiProperty propertyid="<%= Integer.toString(ConsumerInfoRole.CONSUMER_INFO_ADMIN_CHANGE_LOGIN) + ',' + Integer.toString(ConsumerInfoRole.CONSUMER_INFO_ADMIN_FAQ) %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Administration</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_ADMIN_CHANGE_LOGIN %>"> 
          <tr>
            <td width="10"><%= ((String[]) links.get("Password.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("Password.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty>
<%
	String faqLink = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LINK_FAQ);
	if (ServerUtils.forceNotNone(faqLink).length() > 0) {
%>
          <tr>
            <td width="10">&nbsp;</td>
            <td style="padding:1"><a href='<cti:getProperty propertyid="<%= ConsumerInfoRole.WEB_LINK_FAQ %>"/>' class="Link2" target="new"><span class="NavText">FAQ</span></a></td>
          </tr>
<%	} else { %>
          <tr>
            <td width="10"><%= ((String[]) links.get("FAQ.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("FAQ.jsp"))[1] %></td>
          </tr>
<%	} %>
		  <cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_NOT_IMPLEMENTED %>">
          <tr>
            <td width="10"><%= ((String[]) links.get("Privileges.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("Privileges.jsp"))[1] %></td>
          </tr>
          <tr>
            <td width="10"><%= ((String[]) links.get("PrintExport.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("PrintExport.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty>
        </table>
	  </div>
    </td>
  </tr>
</cti:checkMultiProperty>
</table>

<div id="switchMenu" class="bgMenu" style="width:75px" align="left">
  <div id="MenuItem" style="width:75px" onmouseover="changeOptionStyle(this)" class = "navmenu1" onclick = "showPage(0)">
  &nbsp;&nbsp;&nbsp;Hardware Info
  </div>
  <div id="MenuItemSelected" style="width:75px; display:none" onmouseover="changeOptionStyle(this)" class = "navmenu2" onclick = "showPage(0)">
  &nbsp;&#149;&nbsp;Hardware Info
  </div>
  <div id="MenuItem" style="width:75px" onmouseover="changeOptionStyle(this)" class = "navmenu1" onclick = "showPage(1)">
  &nbsp;&nbsp;&nbsp;Configuration
  </div>
  <div id="MenuItemSelected" style="width:75px; display:none" onmouseover="changeOptionStyle(this)" class = "navmenu2" onclick = "showPage(1)">
  &nbsp;&#149;&nbsp;Configuration
  </div>
</div>

<div id="thermostatMenu" class="bgMenu" style="width:75px" align="left">
  <div id="MenuItem" style="width:75px" onmouseover="changeOptionStyle(this)" class = "navmenu1" onclick = "showPage(0)">
  &nbsp;&nbsp;&nbsp;Hardware Info
  </div>
  <div id="MenuItemSelected" style="width:75px; display:none" onmouseover="changeOptionStyle(this)" class = "navmenu2" onclick = "showPage(0)">
  &nbsp;&#149;&nbsp;Hardware Info
  </div>
  <div id="MenuItem" style="width:75px" onmouseover="changeOptionStyle(this)" class = "navmenu1" onclick = "showPage(1)">
  &nbsp;&nbsp;&nbsp;Configuration
  </div>
  <div id="MenuItemSelected" style="width:75px; display:none" onmouseover="changeOptionStyle(this)" class = "navmenu2" onclick = "showPage(1)">
  &nbsp;&#149;&nbsp;Configuration
  </div>
  <div id="MenuItem" style="width:75px" onmouseover="changeOptionStyle(this)" class = "navmenu1" onclick = "showPage(2)">
  &nbsp;&nbsp;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_THERM_SCHED, "Schedule") %>
  </div>
  <div id="MenuItemSelected" style="width:75px; display:none" onmouseover="changeOptionStyle(this)" class = "navmenu2" onclick = "showPage(2)">
  &nbsp;&#149;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_THERM_SCHED, "Schedule") %>
  </div>
  <div id="MenuItem" style="width:75px" onmouseover="changeOptionStyle(this)" class = "navmenu1" onclick = "showPage(3)">
  &nbsp;&nbsp;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_THERM_MANUAL, "Manual") %>
  </div>
  <div id="MenuItemSelected" style="width:75px; display:none" onmouseover="changeOptionStyle(this)" class = "navmenu2" onclick = "showPage(3)">
  &nbsp;&#149;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_THERM_MANUAL, "Manual") %>
  </div>
</div>

<div id="meterMenu" class="bgMenu" style="width:75px" align="left">
  <div id="MenuItem" style="width:75px" onmouseover="changeOptionStyle(this)" class = "navmenu1" onclick = "showPage(0)">
  &nbsp;&nbsp;&nbsp;Hardware Info
  </div>
  <div id="MenuItemSelected" style="width:75px; display:none" onmouseover="changeOptionStyle(this)" class = "navmenu2" onclick = "showPage(0)">
  &nbsp;&#149;&nbsp;Hardware Info
  </div>
</div>
