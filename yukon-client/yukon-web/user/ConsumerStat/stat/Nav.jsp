<%
	// Map of page name / link text
	String linkMap[][] = {{"General.jsp", ServletUtils.getECProperty(ecWebSettings.getURL(), ServletUtils.WEB_TEXT_GENERAL_LINK)},
						  {"TOU.jsp", "Usage"},
						  {"ProgramHist.jsp", ServletUtils.getECProperty(ecWebSettings.getURL(), ServletUtils.WEB_TEXT_CTRL_SUM_LINK)},
						  {"Util.jsp", "Contact Us"},
						  {"FAQ.jsp", "FAQ"},
						  {"Enrollment.jsp", ServletUtils.getECProperty(ecWebSettings.getURL(), ServletUtils.WEB_TEXT_ENROLL_LINK)},
						  {"OptOut.jsp", ServletUtils.getECProperty(ecWebSettings.getURL(), ServletUtils.WEB_TEXT_OPT_OUT_LINK)},
						  {"Thermostat.jsp", "Manual"},
						  {"ThermSchedule.jsp", "Schedule"},
						  {"Password.jsp", "Change Password"}
						 };
						   
	String bulletImg = ServletUtils.getImageNames( ecWebSettings.getLogoLocation() )[1];
	if (bulletImg == null) bulletImg = "Bullet.gif";
	String bulletImg2 = ServletUtils.getImageNames( ecWebSettings.getLogoLocation() )[2];
	if (bulletImg2 == null) bulletImg2 = "Bullet2.gif";
						   
	Hashtable links = new Hashtable();
	for (int i = 0; i < linkMap.length; i++) {
		if (linkMap[i][0].equalsIgnoreCase(pageName))
			links.put(linkMap[i][0], "<img src=\"" + bulletImg + "\" width=\"12\" height=\"12\"><span class=\"NavText2\">" + linkMap[i][1] + "</span>");
		else
			links.put(linkMap[i][0], "<img src=\"" + bulletImg2 + "\" width=\"12\" height=\"12\"><a href=\"" + linkMap[i][0] + "\" class=\"Link2\"><span class=\"NavText\">" + linkMap[i][1] + "</span></a>");
	}
%>

<table width="101" border="0" cellspacing="0" cellpadding="5">
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_THERMOSTAT %>">
<%
	if (dftThermoSettings != null) {	// Hide thermostat settings if it's not available
%>
  <tr>
    <td height="65"><br>
<div align="left"><span class="NavHeader">Thermostat</span><br>
        <%= links.get("ThermSchedule.jsp") %><br>
        <%= links.get("Thermostat.jsp") %><br>
	  </div>
    </td>
  </tr>
<%
	}
%>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_ACCOUNT %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">
        Account</span><br>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_ACCOUNT_GENERAL %>">
        <%= links.get("General.jsp") %><br>
</cti:checkRole>
	  </div>
    </td>
  </tr>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_METERING %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">
        Metering</span><br>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_METERING_USAGE %>">
        <%= links.get("TOU.jsp") %><br>
</cti:checkRole>
	  </div>
    </td>
  </tr>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_PROGRAMS %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Programs</span><br>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_PROGRAMS_CONTROL_HISTORY %>">
        <%= links.get("ProgramHist.jsp") %><br>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_PROGRAMS_ENROLLMENT %>">
		<%= links.get("Enrollment.jsp") %><br>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_PROGRAMS_OPTOUT %>">
        <%= links.get("OptOut.jsp") %><br>
</cti:checkRole>
      </div>
    </td>
  </tr>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_QUESTIONS %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Questions</span><br>
        <%= links.get("Util.jsp") %><br>
        <%= links.get("FAQ.jsp") %><br>
	  </div>
    </td>
  </tr>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_ADMIN %>">
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Administration</span><br>
<cti:checkRole roleid="<%= RoleTypes.CONSUMERINFO_ADMIN_CHANGE_PASSWORD %>">
        <%= links.get("Password.jsp") %><br>
</cti:checkRole>
	  </div>
    </td>
  </tr>
</cti:checkRole>
</table>
