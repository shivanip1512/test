<%@ page import="java.util.Hashtable" %>
<%@ page import="com.cannontech.stars.xml.serialize.*" %>
<%@ page import="com.cannontech.stars.web.util.Mappings" %>
<%@ page import="com.cannontech.stars.web.util.CommonUtils" %>

<%
	java.text.SimpleDateFormat histDateFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm");
	
	StarsCustomerAccountInformation accountInfo = (StarsCustomerAccountInformation) session.getAttribute("CUSTOMER_ACCOUNT_INFORMATION");
	if (accountInfo == null) response.sendRedirect("/UserDemos/ConsumerSwitch/login.jsp");
	
	StarsCustomerAccount account = accountInfo.getStarsCustomerAccount();
    StreetAddress propAddr = account.getStreetAddress();
    StarsSiteInformation siteInfo = account.getStarsSiteInformation();
    BillingAddress billAddr = account.getBillingAddress();
    PrimaryContact primContact = account.getPrimaryContact();
	
	StarsAppliances appliances = accountInfo.getStarsAppliances();
	StarsInventories inventories = accountInfo.getStarsInventories();
	StarsLMPrograms programs = accountInfo.getStarsLMPrograms();
%>

<%
	// Map of page name / link text
	String linkMap[][] = {{"TOU.jsp", "Usage"},
						  {"ProgramHist.jsp", "Control Summary"},
						  {"Programs.jsp", "Add/Change"},
						  {"Thermostat.jsp", "Manual"},
						  {"Util.jsp", "Utility"},
						  {"Installer.jsp", "Installer"},
						  {"FAQ.jsp", "FAQ"},
						  {"Enrollment.jsp","Enrollment"},
						  {"OptOut.jsp","Opt Out"},
						  {"WeekdayS.jsp", "Schedule"}
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
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader"><br>
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
    <td height="65">
<div align="left"><span class="NavHeader">Thermostat</span><br>
        <%= links.get("WeekdayS.jsp") %><br>
        <%= links.get("Thermostat.jsp") %></div>
    </td>
  </tr>
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">Questions</span><br>
        <%= links.get("Util.jsp") %><br>
        <%= links.get("Installer.jsp") %><br>
        <%= links.get("FAQ.jsp") %></div>
    </td>
  </tr>
</table>
