<%
	// Map of page name / link text
	String linkMap[][] = {{"General.jsp", "General"},
						  {"TOU.jsp", "Usage"},
						  {"ProgramHist.jsp", "Control History"},
						  {"Programs.jsp", "Add/Change"},
						  {"Util.jsp", "Contact Us"},
						  {"FAQ.jsp", "FAQ"},
						  {"Enrollment.jsp", "Enrollment"},
						  {"OptOut.jsp", "Opt Out"},
						  {"Thermostat.jsp", "Manual"},
						  {"ThermSchedule.jsp", "Schedule"},
						  {"Password.jsp", "Change Password"}
						 };
						   
	Hashtable links = new Hashtable();
	for (int i = 0; i < linkMap.length; i++) {
		if (linkMap[i][0].equalsIgnoreCase(pageName))
			links.put(linkMap[i][0], "<img src=\"Bullet.gif\" width=\"12\" height=\"12\"><span class=\"NavText2\">" + linkMap[i][1] + "</span>");
		else
			links.put(linkMap[i][0], "<img src=\"Bullet2.gif\" width=\"12\" height=\"12\"><a href=\"" + linkMap[i][0] + "\" class=\"Link2\"><span class=\"NavText\">" + linkMap[i][1] + "</span></a>");
	}
%>

<table width="101" border="0" cellspacing="0" cellpadding="5">
<%
	if (dftThermoSettings != null) {	// Hide thermostat settings if it's not available
%>
  <tr>
    <td height="65"><br>
<div align="left"><span class="NavHeader">Thermostat</span><br>
        <%= links.get("ThermSchedule.jsp") %><br>
        <%= links.get("Thermostat.jsp") %></div>
    </td>
  </tr>
<%
	}
%>
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">
        Account</span><br>
        <%= links.get("General.jsp") %></div>
    </td>
  </tr>
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">
        Metering</span><br>
        <%= links.get("TOU.jsp") %></div>
    </td>
  </tr>
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Programs</span><br>
        <%= links.get("ProgramHist.jsp") %><br>
		<%= links.get("Enrollment.jsp") %><br>
		<%= links.get("OptOut.jsp") %></div>
    </td>
  </tr>
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Questions</span><br>
        <%= links.get("Util.jsp") %><br>
        <%= links.get("FAQ.jsp") %></div>
    </td>
  </tr>
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Administration</span><br>
        <%= links.get("Password.jsp") %></div>
    </td>
  </tr>
</table>
