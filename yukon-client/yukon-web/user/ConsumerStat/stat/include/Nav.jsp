<%
	// Table of (link, page name) pairs
	String linkPairs[][] = {{"General.jsp", "General"},
						  {"TOU.jsp", "Usage"},
						  {"ProgramHist.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_CONTROL_HISTORY, "Control History")},
						  {"Util.jsp", "Contact Us"},
						  {"FAQ.jsp", "FAQ"},
						  {"Enrollment.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_ENROLLMENT, "Enrollment")},
						  {"OptOut.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_OPT_OUT, "Opt Out")},
						  {"Password.jsp", "Change Login"},
						  {"AllTherm.jsp", "All"}
						 };

	String bulletImg = "<img src='../../../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED, "Bullet.gif") + "' width='9' height='9'>";
	String bulletImgExp = "<img src='../../../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_EXPAND, "BulletExpand.gif") + "' width='9' height='9'>";
	
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
<!--This checkProperty is meant to be the checkMultiProperty when more options are available-->
<cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_ACCOUNT_GENERAL %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Account</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr> 
            <td width="10"><%= ((String[]) links.get("General.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("General.jsp"))[1] %></td>
          </tr>
        </table>
      </div>
    </td>
  </tr>
</cti:checkProperty>
<!--This checkProperty is meant to be the checkMultiProperty when more options are available-->
<cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_HARDWARES_THERMOSTAT %>">
<%
	if (thermostats.getStarsLMHardwareCount() > 0) {
%>
  <tr>
    <td>
      <div align="left"><span class="NavHeader">Thermostat</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
		  <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_THERMOSTATS_ALL %>"> 
          <tr> 
            <td width="10"><%= ((String[]) links.get("AllTherm.jsp"))[0] %></td>
            <td><%= ((String[]) links.get("AllTherm.jsp"))[1] %></td>
            <td width="10">&nbsp;</td>
          </tr>
          </cti:checkProperty>
<%
		for (int i = 0; i < thermostats.getStarsLMHardwareCount(); i++) {
			StarsLMHardware hw = thermostats.getStarsLMHardware(i);
			String linkLabel = hw.getDeviceLabel();
			
			String linkHtml = null;
			String linkImgExp = null;
			int pos = pageName.lastIndexOf("Item=");
			if (pos >= 0 && pageName.substring(pos).equals("Item=" + i)) {
				linkHtml = "<span class='Nav' style='cursor:default'>" + linkLabel + "</span>";
				linkImgExp = bulletImg;
			}
			else {
				linkHtml = "<span class='NavTextNoLink' style='cursor:default'>" + linkLabel + "</span>";
				linkImgExp = bulletImgExp;
			}
%>
          <tr onMouseOver="hardwareMenuAppear(event, this, 'thermostatMenu', <%= i %>)"> 
            <td width="10" class="Nav">&nbsp;</td>
            <td><%= linkHtml %></td>
            <td width="10" valign="bottom" style="padding-bottom:1"><%= linkImgExp %></td>
          </tr>
<%
		}
%>
        </table>
      </div>
    </td>
  </tr>
<%
	}
%>
</cti:checkProperty>
<!--This checkProperty is meant to be the checkMultiProperty when more options are available-->
<cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_METERING_USAGE%>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Metering</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr> 
            <td width="10"><%= ((String[]) links.get("TOU.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("TOU.jsp"))[1] %></td>
          </tr>
        </table>
      </div>
    </td>
  </tr>
</cti:checkProperty>
<cti:checkMultiProperty propertyid="<%=Integer.toString(ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY)+','+Integer.toString(ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_ENROLLMENT)+','+Integer.toString(ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT)%>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Programs</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY %>"> 
          <tr>
            <td width="10"><%= ((String[]) links.get("ProgramHist.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("ProgramHist.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty>
		  <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_ENROLLMENT %>"> 
          <tr>
            <td width="10"><%= ((String[]) links.get("Enrollment.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("Enrollment.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty>
		  <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT %>">
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
<cti:checkMultiProperty propertyid="<%=Integer.toString(ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_UTIL)+','+Integer.toString(ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_FAQ)%>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Questions</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
		  <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_UTIL %>"> 
          <tr> 
            <td width="10"><%= ((String[]) links.get("Util.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("Util.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty>
<%
	String faqLink = AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LINK_FAQ);
	if (ServerUtils.forceNotNone(faqLink).length() > 0) {
%>
          <tr> 
            <td width="10">&nbsp;</td>
            <td style="padding:1"><a href="<%= faqLink %>" class="Link2" target="faqs"><span class="NavText">FAQ</span></a></td>
          </tr>
<%	} else { %>
          <tr> 
            <td width="10"><%= ((String[]) links.get("FAQ.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("FAQ.jsp"))[1] %></td>
          </tr>
<%	} %>
        </table>
	  </div>
    </td>
  </tr>
</cti:checkMultiProperty>
<!--This checkProperty is meant to be the checkMultiProperty when more options are available-->
<cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_ADMIN_CHANGE_LOGIN %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Administration</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr> 
            <td width="10"><%= ((String[]) links.get("Password.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("Password.jsp"))[1] %></td>
          </tr>
        </table>
      </div>
    </td>
  </tr>
</cti:checkProperty>
</table>

<script language="JavaScript" src="../../../JavaScript/nav_menu.js"></script>
<script language="JavaScript">
// Initialize variables defined in nav_menu.js
pageName = "<%= pageName %>";
pageLinks = new Array(<%= thermostats.getStarsLMHardwareCount() %>);
<%
	for (int i = 0; i < thermostats.getStarsLMHardwareCount(); i++) {
		StarsThermostatSettings settings = thermostats.getStarsLMHardware(i).getStarsThermostatSettings();
%>
	pageLinks[<%= i %>] = new Array(3);
	pageLinks[<%= i %>][0] = "NewLabel.jsp?Item=<%= i %>";
<%
		if (settings.getStarsThermostatDynamicData() == null) {
%>
	pageLinks[<%= i %>][1] = "ThermSchedule.jsp?Item=<%= i %>";
	pageLinks[<%= i %>][2] = "Thermostat.jsp?Item=<%= i %>";
<%
		}
		else {
			StarsThermoModeSettings navMode = settings.getStarsThermostatDynamicData().getMode();
			String navModeStr = (navMode != null)? "&mode=" + navMode.toString() : "";
%>
	pageLinks[<%= i %>][1] = "ThermSchedule2.jsp?Item=<%= i %><%= navModeStr %>";
	pageLinks[<%= i %>][2] = "Thermostat2.jsp?Item=<%= i %><%= navModeStr %>";
<%
		}
	}
%>
</script>

<div id="thermostatMenu" class="bgMenu" style="width:85px" align="left">
  <div id="MenuItem" style="width:85px" onmouseover="changeNavStyle(this)" class = "navmenu1" onclick = "showPage(0)">
  &nbsp;&nbsp;&nbsp;Change Label
  </div>
  <div id="MenuItemSelected" style="width:85px; display:none" onmouseover="changeNavStyle(this)" class = "navmenu2" onclick = "showPage(0)">
  &nbsp;&#149;&nbsp;Change Label
  </div>
  <div id="MenuItem" style="width:85px" onmouseover="changeNavStyle(this)" class = "navmenu1" onclick = "showPage(1)">
  &nbsp;&nbsp;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_SCHED, "Schedule") %>
  </div>
  <div id="MenuItemSelected" style="width:85px; display:none" onmouseover="changeNavStyle(this)" class = "navmenu2" onclick = "showPage(1)">
  &nbsp;&#149;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_SCHED, "Schedule") %>
  </div>
  <div id="MenuItem" style="width:85px" onmouseover="changeNavStyle(this)" class = "navmenu1" onclick = "showPage(2)">
  &nbsp;&nbsp;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_MANUAL, "Manual") %>
  </div>
  <div id="MenuItemSelected" style="width:85px; display:none" onmouseover="changeNavStyle(this)" class = "navmenu2" onclick = "showPage(2)">
  &nbsp;&#149;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_MANUAL, "Manual") %>
  </div>
</div>
