<%
	// Table of (link, page name) pairs
	String linkPairs[][] = {{"General.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_GENERAL, "General")},
						  {"TOU.jsp", "Usage"},
						  {"ProgramHist.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_CONTROL_HISTORY, "Control History")},
						  {"Enrollment.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_ENROLLMENT, "Enrollment")},
						  {"OptOut.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_OPT_OUT, "Opt Out")},
						  {"Util.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_CONTACT_US, "Contact Us")},
						  {"FAQ.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_FAQ, "FAQ")},
						  {"Password.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_CHANGE_LOGIN, "Change Login")},
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
      <div align="left">
	    <span class="NavHeader"><cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_HEADING_ACCOUNT %>" defaultvalue="Account"/></span><br>
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
	int tstatCnt = thermostats.getStarsInventoryCount();
	if (tstatCnt > 0) {
%>
  <tr>
    <td>
      <div align="left">
	    <span class="NavHeader"><cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_HEADING_THERMOSTAT %>" defaultvalue="Thermostat"/></span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
<cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_THERMOSTATS_ALL %>"> 
<%
			String linkHtml = null;
			String linkImgExp = null;
			if (pageName.indexOf("AllTherm") >= 0) {
				linkHtml = "<span class='Nav' style='cursor:default'>All</span>";
				linkImgExp = bulletImg;
			}
			else {
				linkHtml = "<span class='NavTextNoLink' style='cursor:default'>All</span>";
				linkImgExp = bulletImgExp;
			}
%>
          <tr onMouseOver="hardwareMenuAppear(event, this, 'thermostatAllMenu', <%= tstatCnt %>)"> 
            <td width="10" class="Nav">&nbsp;</td>
            <td><%= linkHtml %></td>
            <td width="10" valign="bottom" style="padding-bottom:1"><%= linkImgExp %></td>
          </tr>
</cti:checkProperty>
<%
		int selectedItemNo = -1;	// selected thermostat no.
		
		if (pageName.indexOf("Item=") >= 0) {
			StringTokenizer st = new StringTokenizer(pageName, "?&");
			while (st.hasMoreTokens()) {
				String param = st.nextToken();
				if (param.startsWith("Item=")) {
					selectedItemNo = Integer.parseInt(param.substring(5));
					break;
				}
			}
		}
		
		for (int i = 0; i < thermostats.getStarsInventoryCount(); i++) {
			StarsInventory inv = thermostats.getStarsInventory(i);
			
			String linkLabel = ServletUtils.getInventoryLabel(inv);
			String linkHtml = null;
			String linkImgExp = null;
			
			if (i == selectedItemNo) {
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
      <div align="left">
	    <span class="NavHeader"><cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_HEADING_METERING %>" defaultvalue="Metering"/></span><br>
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
      <div align="left">
	    <span class="NavHeader"><cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_HEADING_PROGRAMS %>" defaultvalue="Programs"/></span><br>
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
<cti:checkRole roleid="<%=CommercialMeteringRole.ROLEID%>">
  <tr> 
    <td height="30" valign="bottom">
      <div align="left">
	    <span class="NavHeader"><cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_HEADING_TRENDING %>" defaultvalue="Trending"/></span><br>
        <%   /* Retrieve all the predefined graphs for this user*/                       
		if( gData != null )
		{%>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
			<%
			for( int i = 0; i < gData.length; i++ )                                                          
			{
				if( Integer.parseInt(gData[i][0].toString()) == graphBean.getGdefid() && linkPairs[5][0].equalsIgnoreCase(pageName))
				{%>
          <tr> 
            <td width="10"><%= bulletImg %></td>
            <td style="padding:1"><span class="Nav"><%=gData[i][1] %></span></td>
          </tr>
				<%}
				else 
				{%>
          <tr> 
            <td width="10">&nbsp;</td>
            <td style="padding:1"><a href="<%=request.getContextPath()%>Metering.jsp?<%= "gdefid=" + gData[i][0]%>" class = "link2"><span class="NavText"><%=gData[i][1] %></span></a></td>
          </tr>
				<%}
			}%>
        </table>
		<%}%>
	  </div>
    </td>
  </tr>
</cti:checkRole>
<cti:checkMultiProperty propertyid="<%=Integer.toString(ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_UTIL)+','+Integer.toString(ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_FAQ)%>">
  <tr> 
    <td> 
      <div align="left">
	    <span class="NavHeader"><cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_HEADING_QUESTIONS %>" defaultvalue="Questions"/></span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
		  <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_UTIL %>"> 
          <tr> 
            <td width="10"><%= ((String[]) links.get("Util.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("Util.jsp"))[1] %></td>
          </tr>
		  </cti:checkProperty>
		  <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_FAQ %>"> 
<%
	String faqLink = AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LINK_FAQ);
	if (ServerUtils.forceNotNone(faqLink).length() > 0) {
%>
          <tr> 
            <td width="10">&nbsp;</td>
            <td style="padding:1"><a href="<%= faqLink %>" class="Link2" target="faqs"><span class="NavText"><%= ((String[]) links.get("FAQ.jsp"))[1] %></span></a></td>
          </tr>
<%	} else { %>
          <tr> 
            <td width="10"><%= ((String[]) links.get("FAQ.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("FAQ.jsp"))[1] %></td>
          </tr>
<%	} %>
		  </cti:checkProperty>
        </table>
	  </div>
    </td>
  </tr>
</cti:checkMultiProperty>
<!--This checkProperty is meant to be the checkMultiProperty when more options are available-->
<cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_ADMIN_CHANGE_LOGIN %>">
  <tr> 
    <td> 
      <div align="left">
	    <span class="NavHeader"><cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_HEADING_ADMINISTRATION %>" defaultvalue="Administration"/></span><br>
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
pageLinks = new Array(<%= thermostats.getStarsInventoryCount() %>);
<%
	int tstatCnt = thermostats.getStarsInventoryCount();
	for (int i = 0; i < tstatCnt; i++) {
%>
	pageLinks[<%= i %>] = new Array(4);
	pageLinks[<%= i %>][0] = "NewLabel.jsp?Item=<%= i %>";
	pageLinks[<%= i %>][1] = "ThermSchedule.jsp?Item=<%= i %>";
	pageLinks[<%= i %>][2] = "Thermostat.jsp?Item=<%= i %>";
	pageLinks[<%= i %>][3] = "SavedSchedules.jsp?Item=<%= i %>";
<%
	}
%>
	// Defines links for the thermostat "All" menu
	pageLinks[<%= tstatCnt %>] = new Array(3);
	pageLinks[<%= tstatCnt %>][0] = "AllTherm.jsp";
	pageLinks[<%= tstatCnt %>][1] = "ThermSchedule.jsp?AllTherm";
	pageLinks[<%= tstatCnt %>][2] = "Thermostat.jsp?AllTherm";
</script>

<div id="thermostatMenu" class="bgMenu" style="width:100px" align="left">
  <div id="thermostatMenuItem" name="thermostatMenuItem" style="width:100px" onmouseover="changeNavStyle(this)" class = "navmenu1" onclick = "showPage(0)">
  &nbsp;&nbsp;&nbsp;Change Label
  </div>
  <div id="thermostatMenuItemSelected" name="thermostatMenuItemSelected" style="width:100px; display:none" onmouseover="changeNavStyle(this)" class = "navmenu2" onclick = "showPage(0)">
  &nbsp;&#149;&nbsp;Change Label
  </div>
  <div id="thermostatMenuItem" name="thermostatMenuItem" style="width:100px" onmouseover="changeNavStyle(this)" class = "navmenu1" onclick = "showPage(1)">
  &nbsp;&nbsp;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_SCHED, "Schedule") %>
  </div>
  <div id="thermostatMenuItemSelected" name="thermostatMenuItemSelected" style="width:100px; display:none" onmouseover="changeNavStyle(this)" class = "navmenu2" onclick = "showPage(1)">
  &nbsp;&#149;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_SCHED, "Schedule") %>
  </div>
  <div id="thermostatMenuItem" name="thermostatMenuItem" style="width:100px" onmouseover="changeNavStyle(this)" class = "navmenu1" onclick = "showPage(2)">
  &nbsp;&nbsp;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_MANUAL, "Manual") %>
  </div>
  <div id="thermostatMenuItemSelected" name="thermostatMenuItemSelected" style="width:100px; display:none" onmouseover="changeNavStyle(this)" class = "navmenu2" onclick = "showPage(2)">
  &nbsp;&#149;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_MANUAL, "Manual") %>
  </div>
  <div id="thermostatMenuItem" name="thermostatMenuItem" style="width:100px" onmouseover="changeNavStyle(this)" class = "navmenu1" onclick = "showPage(3)">
  &nbsp;&nbsp;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_SAVED_SCHED, "Saved Schedules") %>
  </div>
  <div id="thermostatMenuItemSelected" name="thermostatMenuItemSelected" style="width:100px; display:none" onmouseover="changeNavStyle(this)" class = "navmenu2" onclick = "showPage(3)">
  &nbsp;&#149;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_SAVED_SCHED, "Saved Schedules") %>
  </div>
</div>

<div id="thermostatAllMenu" class="bgMenu" style="width:100px" align="left">
  <div id="thermostatAllMenuItem" name="thermostatAllMenuItem" style="width:100px" onmouseover="changeNavStyle(this)" class = "navmenu1" onclick = "showPage(0)">
  &nbsp;&nbsp;&nbsp;Select Thermostats
  </div>
  <div id="thermostatAllMenuItemSelected" name="thermostatAllMenuItemSelected" style="width:100px; display:none" onmouseover="changeNavStyle(this)" class = "navmenu2" onclick = "showPage(0)">
  &nbsp;&#149;&nbsp;Select Thermostats
  </div>
  <div id="thermostatAllMenuItem" name="thermostatAllMenuItem" style="width:100px" onmouseover="changeNavStyle(this)" class = "navmenu1" onclick = "showPage(1)">
  &nbsp;&nbsp;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_SCHED, "Schedule") %>
  </div>
  <div id="thermostatAllMenuItemSelected" name="thermostatAllMenuItemSelected" style="width:100px; display:none" onmouseover="changeNavStyle(this)" class = "navmenu2" onclick = "showPage(1)">
  &nbsp;&#149;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_SCHED, "Schedule") %>
  </div>
  <div id="thermostatAllMenuItem" name="thermostatAllMenuItem" style="width:100px" onmouseover="changeNavStyle(this)" class = "navmenu1" onclick = "showPage(2)">
  &nbsp;&nbsp;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_MANUAL, "Manual") %>
  </div>
  <div id="thermostatAllMenuItemSelected" name="thermostatAllMenuItemSelected" style="width:100px; display:none" onmouseover="changeNavStyle(this)" class = "navmenu2" onclick = "showPage(2)">
  &nbsp;&#149;&nbsp;<%= AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_MANUAL, "Manual") %>
  </div>
</div>
