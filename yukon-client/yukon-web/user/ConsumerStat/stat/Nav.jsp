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

	ArrayList linkList = new ArrayList();
	for (int i = 0; i < linkPairs.length; i++)
		linkList.add( linkPairs[i] );

	String[] thermostatNames = new String[ thermostats.getStarsLMHardwareCount() ];
	int lastItemType = 0;
	int itemNo = 1;
	
	for (int i = 0; i < thermostats.getStarsLMHardwareCount(); i++) {
		StarsLMHardware hw = thermostats.getStarsLMHardware(i);
		thermostatNames[i] = hw.getLMDeviceType().getContent();
		
		if (hw.getLMDeviceType().getEntryID() != lastItemType) {
			lastItemType = hw.getLMDeviceType().getEntryID();
			itemNo = 1;
		}
		else {
			itemNo++;
			thermostatNames[i] += " (" + Integer.toString(itemNo) + ")";
		}
		
		if (hw.getStarsThermostatSettings().getStarsThermostatDynamicData() == null) {
			linkList.add( new String[] {"ThermSchedule.jsp?InvNo=" + i, AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_SCHED, "Schedule")} );
			linkList.add( new String[] {"Thermostat.jsp?InvNo=" + i, AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_MANUAL, "Manual")} );
		}
		else {
			linkList.add( new String[] {"ThermSchedule2.jsp?InvNo=" + i, AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_SCHED, "Schedule")} );
			linkList.add( new String[] {"Thermostat2.jsp?InvNo=" + i, AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_MANUAL, "Manual")} );
		}
	}

	String bulletImg = "../../../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED);
	if (bulletImg == null) bulletImg = "../../../WebConfig/Bullet.gif";
	String bulletImg2 = "../../../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET);
	if (bulletImg2 == null) bulletImg2 = "../../../WebConfig/Bullet2.gif";
	
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
	if (thermostats.getStarsLMHardwareCount() > 0) {
%>
  <tr>
    <td>
      <div align="left"><span class="NavHeader">Thermostat</span><br>
<%
		for (int i = 0; i < thermostats.getStarsLMHardwareCount(); i++) {
%>
        <img src="<%= bulletImg2 %>" width="12" height="12"><span class="NavText" style="color:#FFFFFF"><%= thermostatNames[i] %></span><br>
	    <table width="90" border="0" cellspacing="0" cellpadding="0">
          <tr> 
            <td width="12">&nbsp;</td>
            <td width="78">
<%
			if (thermostats.getStarsLMHardware(i).getStarsThermostatSettings().getStarsThermostatDynamicData() == null) {
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
%>
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
