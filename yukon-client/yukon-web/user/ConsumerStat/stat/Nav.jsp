<%
	// Pairs of page name / link text
	String linkPairs[][] = {{"General.jsp", "General"},
						  {"TOU.jsp", "Usage"},
						  {"ProgramHist.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_CONTROL_HISTORY, "Control History")},
						  {"Util.jsp", "Contact Us"},
						  {"FAQ.jsp", "FAQ"},
						  {"Enrollment.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_ENROLLMENT, "Enrollment")},
						  {"OptOut.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_OPT_OUT, "Opt Out")},
						  {"Thermostat.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_MANUAL, "Manual")},
						  {"Thermostat2.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_MANUAL, "Manual")},
						  {"ThermSchedule.jsp", AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_SCHED, "Schedule")},
						  {"Password.jsp", "Change Login"}
						 };
						   
	Hashtable links = new Hashtable();
	for (int i = 0; i < linkPairs.length; i++) {
		if (linkPairs[i][0].equalsIgnoreCase(pageName))
			links.put(linkPairs[i][0], "<img src='../../../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED) + "' width='12' height='12'><span class='Nav'>" + linkPairs[i][1] + "</span>");
		else
			links.put(linkPairs[i][0], "<img src='../../../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET) + "' width='12' height='12'><a href='" + linkPairs[i][0] + "' class='Link2'><span class='NavText'>" + linkPairs[i][1] + "</span></a>");
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
<%
			if (thermoSettings.getStarsThermostatDynamicData() == null) {
%>
          <%= links.get("Thermostat.jsp") %><br>
<%
			}
			else {
%>
          <%= links.get("Thermostat2.jsp") %><br>
<%
			}
%>
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
<cti:checkMultiProperty propertyid="<%=Integer.toString(ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY)+','+Integer.toString(ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_ENROLLMENT)+','+Integer.toString(ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT)%>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Programs</span><br>
        <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY %>">
          <%= links.get("ProgramHist.jsp") %><br>
        </cti:checkProperty>
        <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_ENROLLMENT %>">
          <%= links.get("Enrollment.jsp") %><br>
        </cti:checkProperty>
        <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT %>">
          <%= links.get("OptOut.jsp") %><br>
        </cti:checkProperty>
      </div>
    </td>
  </tr>
</cti:checkMultiProperty>
<cti:checkMultiProperty propertyid="<%=Integer.toString(ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_UTIL)+','+Integer.toString(ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_FAQ)%>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Questions</span><br>
        <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_UTIL %>">
          <%= links.get("Util.jsp") %><br>
        </cti:checkProperty>
        <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_FAQ %>">
          <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CUSTOMIZED_FAQ_LINK %>">
            <img src="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.NAV_BULLET%>"/>" width="12" height="12"><a href="<cti:getProperty propertyid="<%=ResidentialCustomerRole.WEB_LINK_FAQ%>"/>" class="Link2" target="new"><span class="NavText">FAQ</span></a><br>
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
        <cti:checkProperty propertyid="<%= ResidentialCustomerRole.CONSUMER_INFO_ADMIN_CHANGE_LOGIN %>">
          <%= links.get("Password.jsp") %><br>
        </cti:checkProperty>
	  </div>
    </td>
  </tr>
</cti:checkProperty>
</table>
