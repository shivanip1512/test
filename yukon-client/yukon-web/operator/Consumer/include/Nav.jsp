<%
	// Table of [link, label, page name(optional)]
	String linkTable[][] = {{"Update.jsp", "General"},
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
						  {(AuthFuncs.checkRoleProperty(lYukonUser, ConsumerInfoRole.INVENTORY_CHECKING)?
						  	"SerialNumber.jsp?action=New" : "CreateHardware.jsp"), "New", "CreateHardware.jsp"},
						 };

	String bulletImg = "<img src='../../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED, "Bullet.gif") + "' width='9' height='9'>";
	String bulletImgExp = "<img src='../../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_EXPAND, "BulletExpand.gif") + "' width='9' height='9'>";
	String connImgMid = "<img src='../../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_CONNECTOR_MIDDLE, "MidConnector.gif") + "' width='10' height='12'>";
	String connImgBtm = "<img src='../../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_CONNECTOR_BOTTOM, "BottomConnector.gif") + "' width='10' height='12'>";
	
	// List of String[] (link image, link html)
	Hashtable links = new Hashtable();
	for (int i = 0; i < linkTable.length; i++) {
		String linkImg = null;
		String linkHtml = null;
		String linkPageName = (linkTable[i].length == 3)? linkTable[i][2] : linkTable[i][0];
		if (linkPageName.equalsIgnoreCase(pageName)) {
			linkImg = bulletImg;
			linkHtml = "<span class='Nav'>" + linkTable[i][1] + "</span>";
		}
		else {
			linkImg = "";
			linkHtml = "<a href='" + linkTable[i][0] + "' class='Link2'><span class='NavText'>" + linkTable[i][1] + "</span></a>";
		}
		links.put(linkPageName, new String[] {linkImg, linkHtml});
	}
	
	// List of String[] (inventory no., link html, expand bullet image)
	ArrayList switches = new ArrayList();
	ArrayList thermostats = new ArrayList();
	ArrayList meters = new ArrayList();
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
            <td width="10">&nbsp;</td>
            <td>
<%
	if( gData != null )
	{%>
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
	<%
		String linkURL = "Metering.jsp";
			String linkHtml = null;
			String linkImgExp = null;
			
			if(linkURL.equalsIgnoreCase(pageName))
			{
				linkHtml = "<span class='Nav' style='cursor:default'>Interval Data</span>";
				linkImgExp = bulletImg;
			}
			else
			{
				linkHtml = "<span class='NavTextNoLink' style='cursor:default'>Interval Data</span>";
				linkImgExp = bulletImgExp;
			}
			%>
                <tr onmouseover="trendMenuAppear(event, this, 'intervalDataMenu')"> 
                  <td><%=linkHtml%></td>
                  <td width="10" valign="bottom" style="padding-bottom:1"><%=linkImgExp%></td>
                </tr>
                
              </table>
<%	}%>

            </td>
          </tr>
	
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
	int selectedInvNo = -1;	// selected inventory no.
	
	if (pageName.indexOf("InvNo=") >= 0) {
		StringTokenizer st = new StringTokenizer(pageName, "?&");
		while (st.hasMoreTokens()) {
			String param = st.nextToken();
			if (param.startsWith("InvNo=")) {
				selectedInvNo = Integer.parseInt(param.substring(6));
				break;
			}
		}
	}
	
	for (int i = 0; i < inventories.getStarsInventoryCount(); i++) {
		StarsInventory inv = inventories.getStarsInventory(i);
		
		String linkLabel = inv.getDeviceLabel();
		if (linkLabel.equals("")) {
			if (inv.getLMHardware() != null)
				linkLabel= inv.getLMHardware().getManufacturerSerialNumber();
			else if (inv.getMCT() != null)
				linkLabel = inv.getMCT().getDeviceName();
		}
		
		String linkHtml = null;
		String linkImgExp = null;
		
		if (i == selectedInvNo) {
			linkHtml = "<span class='Nav' style='cursor:default'>" + linkLabel + "</span>";
			linkImgExp = bulletImg;
		}
		else {
			linkHtml = "<span class='NavTextNoLink' style='cursor:default'>" + linkLabel + "</span>";
			linkImgExp = bulletImgExp;
		}
		
		String[] linkFields = new String[] {String.valueOf(i), linkHtml, linkImgExp};
		if (inv.getLMHardware() != null) {
			if (inv.getLMHardware().getStarsThermostatSettings() != null)
				thermostats.add( linkFields );
			else
				switches.add( linkFields );
		}
		else
			meters.add( linkFields );
	}
	
	if (inventories.getStarsInventoryCount() > 0) {
%>
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
				String connImg = (i == switches.size() - 1)? connImgBtm : connImgMid;
%>
                <tr onmouseover="hardwareMenuAppear(event, this, 'switchMenu', <%= linkFields[0] %>)"> 
                  <td width="12"><%= connImg %></td>
                  <td><%= linkFields[1] %></td>
                  <td width="10" valign="bottom" style="padding-bottom:1"><%= linkFields[2] %></td>
                </tr>
<%
			}
%>
              </table>
<%
		}	// switches
		if (thermostats.size() > 0) {
%>
              <span class="NavGroup">Thermostats</span><br>
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <%
			for (int i = 0; i < thermostats.size(); i++) {
				String[] linkFields = (String[]) thermostats.get(i);
				String connImg = (i == thermostats.size() - 1)? connImgBtm : connImgMid;
%>
                <tr onmouseover="hardwareMenuAppear(event, this, 'thermostatMenu', <%= linkFields[0] %>)"> 
                  <td width="12"><%= connImg %></td>
                  <td><%= linkFields[1] %></td>
                  <td width="10" valign="bottom" style="padding-bottom:1"><%= linkFields[2] %></td>
                </tr>
<%
			}
%>
              </table>
<%
		}	// thermostats
		if (meters.size() > 0) {
%>
              <span class="NavGroup">Meters</span><br>
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <%
			for (int i = 0; i < meters.size(); i++) {
				String[] linkFields = (String[]) meters.get(i);
				String connImg = (i == meters.size() - 1)? connImgBtm : connImgMid;
%>
                <tr onmouseover="hardwareMenuAppear(event, this, 'meterMenu', <%= linkFields[0] %>)"> 
                  <td width="12"><%= connImg %></td>
                  <td><%= linkFields[1] %></td>
                  <td width="10" valign="bottom" style="padding-bottom:1"><%= linkFields[2] %></td>
                </tr>
<%
			}
%>
              </table>
<%
		}	// meters
%>
            </td>
          </tr>
<%
	}
%>
		  </cti:checkProperty>
		  <cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_HARDWARES_CREATE %>"> 
          <tr>
            <td width="10"><%= ((String[]) links.get("CreateHardware.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("CreateHardware.jsp"))[1] %></td>
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
          <cti:checkProperty propertyid="<%= ConsumerInfoRole.CONSUMER_INFO_ADMIN_FAQ %>"> 
<%
	String faqLink = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LINK_FAQ);
	if (ServerUtils.forceNotNone(faqLink).length() > 0) {
%>
          <tr>
            <td width="10">&nbsp;</td>
            <td style="padding:1"><a href='<cti:getProperty propertyid="<%= ConsumerInfoRole.WEB_LINK_FAQ %>"/>' class="Link2" target="faqs"><span class="NavText">FAQ</span></a></td>
          </tr>
<%	} else { %>
          <tr>
            <td width="10"><%= ((String[]) links.get("FAQ.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("FAQ.jsp"))[1] %></td>
          </tr>
<%	} %>
		  </cti:checkProperty>
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

<script language="JavaScript" src="../../JavaScript/nav_menu.js"></script>
<script language="JavaScript">
// Initialize variables defined in nav_menu.js
pageName = "<%= pageName %>";
pageLinks = new Array(<%= inventories.getStarsInventoryCount() %>);
<%
	for (int i = 0; i < switches.size(); i++) {
		int num = Integer.parseInt( ((String[]) switches.get(i))[0] );
%>
	pageLinks[<%= num %>] = new Array(2);
	pageLinks[<%= num %>][0] = "Inventory.jsp?InvNo=<%= num %>";
	pageLinks[<%= num %>][1] = "ConfigHardware.jsp?InvNo=<%= num %>";
<%
	}
	for (int i = 0; i < thermostats.size(); i++) {
		int num = Integer.parseInt( ((String[]) thermostats.get(i))[0] );
%>
	pageLinks[<%= num %>] = new Array(4);
	pageLinks[<%= num %>][0] = "Inventory.jsp?InvNo=<%= num %>";
	pageLinks[<%= num %>][1] = "ConfigHardware.jsp?InvNo=<%= num %>";
<%
		StarsThermostatTypes type = inventories.getStarsInventory(num).getLMHardware().getStarsThermostatSettings().getThermostatType();
		if (type.getType() == StarsThermostatTypes.ENERGYPRO_TYPE) {
%>
	pageLinks[<%= num %>][2] = "ThermSchedule2.jsp?InvNo=<%= num %>";
	pageLinks[<%= num %>][3] = "Thermostat2.jsp?InvNo=<%= num %>";
<%
		}
		else if (type.getType() == StarsThermostatTypes.COMMERCIAL_TYPE) {
%>
	pageLinks[<%= num %>][2] = "ThermSchedule1.jsp?InvNo=<%= num %>";
	pageLinks[<%= num %>][3] = "Thermostat.jsp?InvNo=<%= num %>";
<%
		}
		else {
%>
	pageLinks[<%= num %>][2] = "ThermSchedule.jsp?InvNo=<%= num %>";
	pageLinks[<%= num %>][3] = "Thermostat.jsp?InvNo=<%= num %>";
<%
		}
	}
	for (int i = 0; i < meters.size(); i++) {
		int num = Integer.parseInt( ((String[]) meters.get(i))[0] );
%>
	pageLinks[<%= num %>] = new Array(1);
	pageLinks[<%= num %>][0] = "Inventory.jsp?InvNo=<%= num %>";
	pageLinks[<%= num %>][1] = "ConfigMeter.jsp?InvNo=<%= num %>";
	pageLinks[<%= num %>][2] = "CommandMeter.jsp?InvNo=<%= num %>";
<%
	}
%>
</script>

<div id="switchMenu" class="bgMenu" style="width:75px" align="left">
  <div id="switchMenuItem" name="switchMenuItem" style="width:75px" onmouseover="changeNavStyle(this)" class = "navmenu1" onclick = "showPage(0)">
  &nbsp;&nbsp;&nbsp;Hardware Info
  </div>
  <div id="switchMenuItemSelected" name="switchMenuItemSelected" style="width:75px; display:none" onmouseover="changeNavStyle(this)" class = "navmenu2" onclick = "showPage(0)">
  &nbsp;&#149;&nbsp;Hardware Info
  </div>
  <div id="switchMenuItem" name="switchMenuItem" style="width:75px" onmouseover="changeNavStyle(this)" class = "navmenu1" onclick = "showPage(1)">
  &nbsp;&nbsp;&nbsp;Configuration
  </div>
  <div id="switchMenuItemSelected" name="switchMenuItemSelected" style="width:75px; display:none" onmouseover="changeNavStyle(this)" class = "navmenu2" onclick = "showPage(1)">
  &nbsp;&#149;&nbsp;Configuration
  </div>
</div>

<div id="thermostatMenu" class="bgMenu" style="width:75px" align="left">
  <div id="thermostatMenuItem" name="thermostatMenuItem" style="width:75px" onmouseover="changeNavStyle(this)" class = "navmenu1" onclick = "showPage(0)">
  &nbsp;&nbsp;&nbsp;Hardware Info
  </div>
  <div id="thermostatMenuItemSelected" name="thermostatMenuItemSelected" style="width:75px; display:none" onmouseover="changeNavStyle(this)" class = "navmenu2" onclick = "showPage(0)">
  &nbsp;&#149;&nbsp;Hardware Info
  </div>
  <div id="thermostatMenuItem" name="thermostatMenuItem" style="width:75px" onmouseover="changeNavStyle(this)" class = "navmenu1" onclick = "showPage(1)">
  &nbsp;&nbsp;&nbsp;Configuration
  </div>
  <div id="thermostatMenuItemSelected" name="thermostatMenuItemSelected" style="width:75px; display:none" onmouseover="changeNavStyle(this)" class = "navmenu2" onclick = "showPage(1)">
  &nbsp;&#149;&nbsp;Configuratio
  </div>
  <div id="thermostatMenuItem" name="thermostatMenuItem" style="width:75px" onmouseover="changeNavStyle(this)" class = "navmenu1" onclick = "showPage(2)">
  &nbsp;&nbsp;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_THERM_SCHED, "Schedule") %>
  </div>
  <div id="thermostatMenuItemSelected" name="thermostatMenuItemSelected" style="width:75px; display:none" onmouseover="changeNavStyle(this)" class = "navmenu2" onclick = "showPage(2)">
  &nbsp;&#149;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_THERM_SCHED, "Schedule") %>
  </div>
  <div id="thermostatMenuItem" name="thermostatMenuItem" style="width:75px" onmouseover="changeNavStyle(this)" class = "navmenu1" onclick = "showPage(3)">
  &nbsp;&nbsp;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_THERM_MANUAL, "Manual") %>
  </div>
  <div id="thermostatMenuItemSelected" name="thermostatMenuItemSelected" style="width:75px; display:none" onmouseover="changeNavStyle(this)" class = "navmenu2" onclick = "showPage(3)">
  &nbsp;&#149;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_LABEL_THERM_MANUAL, "Manual") %>
  </div>
</div>

<div id="meterMenu" class="bgMenu" style="width:75px" align="left">
  <div id="meterMenuItem" name="meterMenuItem" style="width:75px" onmouseover="changeNavStyle(this)" class = "navmenu1" onclick = "showPage(0)">
  &nbsp;&nbsp;&nbsp;Hardware Info
  </div>
  <div id="meterMenuItemSelected" name="meterMenuItemSelected" style="width:75px; display:none" onmouseover="changeNavStyle(this)" class = "navmenu2" onclick = "showPage(0)">
  &nbsp;&#149;&nbsp;Hardware Info
  </div>
  <div id="meterMenuItem" name="meterMenuItem" style="width:75px" onmouseover="changeNavStyle(this)" class = "navmenu1" onclick = "showPage(1)">
  &nbsp;&nbsp;&nbsp;Configuration
  </div>
  <div id="meterMenuItemSelected" name="meterMenuItemSelected" style="width:75px; display:none" onmouseover="changeNavStyle(this)" class = "navmenu2" onclick = "showPage(1)">
  &nbsp;&#149;&nbsp;Configuration
  </div>
  <div id="meterMenuItem" name="meterMenuItem" style="width:75px" onmouseover="changeNavStyle(this)" class = "navmenu1" onclick = "showPage(2)">
  &nbsp;&nbsp;&nbsp;Command
  </div>
  <div id="meterMenuItemSelected" name="meterMenuItemSelected" style="width:75px; display:none" onmouseover="changeNavStyle(this)" class = "navmenu2" onclick = "showPage(2)">
  &nbsp;&#149;&nbsp;Command
  </div>
</div>

<%
	if( gData != null )
	{%>

<div id="intervalDataMenu" class="bgMenu" style="width:75px" align="left">	
<%		for( int i = 0; i < gData.length; i++ )                                                          
		{
			String className = "navmenu1";
			String indicator = "&nbsp;&nbsp;&nbsp;";
			if( Integer.parseInt( (gData[i][0]).toString()) == graphBean.getGdefid())
			{
				className = "navmenu2";
				indicator = "&nbsp;&#149;&nbsp;";
			}%>
  <div id="<%=gData[i][1]%>" name="gdefid" style="width:170px;" onmouseover="changeNavStyle(this)" class = "<%=className%>" onclick = "location='<%=request.getContextPath()%>/operator/Consumer/Metering.jsp?gdefid=<%=gData[i][0]%>';">
  <%=indicator%><%=gData[i][1]%>
  </div>
		<%}%>
</div>
<%	}%>
