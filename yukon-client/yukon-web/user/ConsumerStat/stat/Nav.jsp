<%
	// Map of page name / link text
	String linkMap[][] = {{"General.jsp", ServletUtils.getECProperty(ecWebSettings.getURL(), ServletUtils.WEB_TEXT_GENERAL_LINK)},
						  {"TOU.jsp", "Usage"},
						  {"ProgramHist.jsp", ServletUtils.getECProperty(ecWebSettings.getURL(), ServletUtils.WEB_TEXT_CTRL_HIST_LINK)},
						  {"Util.jsp", "Contact Us"},
						  {"FAQ.jsp", "FAQ"},
						  {"Enrollment.jsp", ServletUtils.getECProperty(ecWebSettings.getURL(), ServletUtils.WEB_TEXT_ENROLL_LINK)},
						  {"OptOut.jsp", ServletUtils.getECProperty(ecWebSettings.getURL(), ServletUtils.WEB_TEXT_OPT_OUT_LINK)},
						  {"Thermostat.jsp", "Manual"},
						  {"ThermSchedule.jsp", "Schedule"},
						  {"Password.jsp", "Change Login"}
						 };
						   
	Hashtable links = new Hashtable();
	for (int i = 0; i < linkMap.length; i++) {
		if (linkMap[i][0].equalsIgnoreCase(pageName))
			links.put(linkMap[i][0], "<img src=\"../../WebConfig/" + AuthFuncs.getRolePropertyValue(WebClientRole.NAV_BULLET_SELECTED) + "\" width=\"12\" height=\"12\"><span class=\"Nav\">" + linkMap[i][1] + "</span>");
		else
			links.put(linkMap[i][0], "<img src=\"../../WebConfig/" + AuthFuncs.getRolePropertyValue(WebClientRole.NAV_BULLET) + "\" width=\"12\" height=\"12\"><a href=\"" + linkMap[i][0] + "\" class=\"Link2\"><span class=\"NavText\">" + linkMap[i][1] + "</span></a>");
	}
%>

<table width="101" border="0" cellspacing="0" cellpadding="5">
<!--This checkProperty is meant to be the checkMultiProperty when more options are available-->
<cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_ACCOUNT_GENERAL %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Account</span><br>
        <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_ACCOUNT_GENERAL %>">
          <%= links.get("General.jsp") %><br>
        </cti:checkProperty>
	  </div>
    </td>
  </tr>
</cti:checkProperty>
<!--This checkProperty is meant to be the checkMultiProperty when more options are available-->
<cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_HARDWARES_THERMOSTAT %>">
<%
	if (thermoSettings != null) {	// Hide thermostat settings if it's not available
%>
  <tr>
    <td>
      <div align="left"><span class="NavHeader">Thermostat</span><br>
        <cti:checkProperty propertyid="<%=ResidentialCustomerRole.CONSUMER_INFO_HARDWARES_THERMOSTAT%>">
          <%= links.get("ThermSchedule.jsp") %><br>
          <%= links.get("Thermostat.jsp") %><br>
        </cti:checkProperty>
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
        <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_METERING_USAGE %>">
          <%= links.get("TOU.jsp") %><br>
        </cti:checkProperty>
	  </div>
    </td>
  </tr>
</cti:checkProperty>
<cti:checkMultiProperty propertyid="<%=Integer.toString(ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY)+","+Integer.toString(ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_ENROLLMENT)+","+Integer.toString(ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT)%>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Programs</span><br>
        <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY %>">
          <%= links.get("ProgramHist.jsp") %><br>
        </cti:checkProperty>
        <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMERINFO_PROGRAMS_ENROLLMENT %>">
          <%= links.get("Enrollment.jsp") %><br>
        </cti:checkProperty>
        <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMERINFO_PROGRAMS_OPTOUT %>">
          <%= links.get("OptOut.jsp") %><br>
        </cti:checkProperty>
      </div>
    </td>
  </tr>
</cti:checkMultiProperty>
<cti:checkMultiProperty propertyid="<%=Integer.toString(ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_UTIL)+","+Integer.toString(ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_FAQ)%>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Questions</span><br>
        <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_UTIL %>">
          <%= links.get("Util.jsp") %><br>
        </cti:checkProperty>
        <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_FAQ %>">
          <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CUSTOMIZED_FAQ_LINK %>">
            <img src="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.NAV_BULLET%>"/>" width="12" height="12"><a href="<cti:getProperty propertyid="<%=ResidentialCustomerRole.WEB_LINK_FAQ%>"/>" class="Link2" target="new"><span class="NavText">FAQ</span></a><br>
          </cti:checkProperty>
          <cti:checkNoProperty propertyid="<%= ResidentialCustomerRole.CUSTOMIZED_FAQ_LINK %>">
            <%= links.get("FAQ.jsp") %><br>
          </cti:checkNoProperty>
        </cti:checkProperty>
	  </div>
    </td>
  </tr>
</cti:checkMultiProperty>
<!--This checkProperty is meant to be the checkMultiProperty when more options are available-->
<cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_ADMIN_CHANGE_LOGIN %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Administration</span><br>
        <cti:checkProperty Propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_ADMIN_CHANGE_LOGIN %>">
          <%= links.get("Password.jsp") %><br>
        </cti:checkProperty>
	  </div>
    </td>
  </tr>
</cti:checkProperty>
</table>
