<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.stars.xml.serialize.*" %>
<%@ page import="com.cannontech.stars.web.util.Mappings" %>
<%@ page import="com.cannontech.stars.web.util.CommonUtils" %>
<%@ page import="com.cannontech.database.db.starshardware.LMHardwareBase" %>

<%
	java.text.SimpleDateFormat histDateFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm");
	
	StarsCustomerAccountInformation accountInfo = (StarsCustomerAccountInformation) session.getAttribute("CUSTOMER_ACCOUNT_INFORMATION");
	if (accountInfo == null) response.sendRedirect("/login.jsp");
	
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
	String linkMap[][] = {{"Update.jsp", "General"},
						  {"Contacts.jsp", "Contacts"},
						  {"Calls.jsp", "Call Tracking"},
						  {"MeterData.jsp", "Interval Data"},
						  {"Usage.jsp", "Usage"},
						  {"ProgramHist.jsp", "Control History"},
						  {"Programs.jsp", "Add/Change"},
						  /* Appliances are generated dynamically
						  {"Air.jsp", "Air Conditioner"},
						  {"WH.jsp", "Water Heater"},
						  */
						  /* Inventories are generated dynamically
						  {"LCR5000.jsp", "LCR 5000"},
						  {"Thermostat.jsp", "Thermostat"},
						  {"Meter.jsp", "Meter"},
						  */
						  {"Service.jsp", "Service Request"},
						  {"ServiceSummary.jsp", "Service History"},
						  {"Password.jsp", "Change Password"},
						  {"Privileges.jsp", "Privileges"},
						  {"CreateWizard.jsp", "Create"}
						 };
						   
	Hashtable links = new Hashtable();
	for (int i = 0; i < linkMap.length; i++) {
		if (linkMap[i][0].equalsIgnoreCase(pageName))
			links.put(linkMap[i][0], "<img src=\"Bullet.gif\" width=\"12\" height=\"12\"><span class=\"NavBlue\">" + linkMap[i][1] + "</span>");
		else
			links.put(linkMap[i][0], "<img src=\"Bullet2.gif\" width=\"12\" height=\"12\"><a href=\"" + linkMap[i][0] + "\" class=\"WhiteLink\"><span class=\"NavText\">" + linkMap[i][1] + "</span></a>");
	}
	
	String[] appLinks = new String[ appliances.getStarsApplianceCount() ];
	
    for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
        StarsAppliance appliance = appliances.getStarsAppliance(i);
		
		String linkText = Mappings.getApplianceName( appliance.getStarsApplianceCategory().getCategory() );
		if (linkText == null) linkText = "Appliance";
			
		if (pageName.equalsIgnoreCase("Appliance.jsp?AppNo=" + i))
			appLinks[i] = "<img src=\"Bullet.gif\" width=\"12\" height=\"12\"><span class=\"NavBlue\">" + linkText + "</span>";
		else
			appLinks[i] = "<img src=\"Bullet2.gif\" width=\"12\" height=\"12\"><a href=\"Appliance.jsp?AppNo=" + i + "\" class=\"WhiteLink\"><span class=\"NavText\">" + linkText + "</span></a>";
    }
	
	String[] invLinks = new String[ inventories.getStarsLMHardwareCount() ];
	
	for (int i = 0; i < inventories.getStarsLMHardwareCount(); i++) {
		StarsLMHardware hardware = inventories.getStarsLMHardware(i);
		
		String linkText = Mappings.getLMHardwareName( hardware.getLMDeviceType() );
		
		if (pageName.equalsIgnoreCase("Inventory.jsp?InvNo=" + i))
			invLinks[i] = "<img src=\"Bullet.gif\" width=\"12\" height=\"12\"><span class=\"NavBlue\">" + linkText + "</span>";
		else
			invLinks[i] = "<img src=\"Bullet2.gif\" width=\"12\" height=\"12\"><a href=\"Inventory.jsp?InvNo=" + i + "\" class=\"WhiteLink\"><span class=\"NavText\">" + linkText + "</span></a>";
	}
%>

<table width="101" border="0" cellspacing="0" cellpadding="5">
  <tr> 
    <td> 
      <div align="left"><span class="YellowHeader">Account</span><br>
        <%= links.get("Update.jsp") %><br>
        <%= links.get("Contacts.jsp") %><br>
        <%= links.get("Calls.jsp") %></div>
    </td>
  </tr>
  <tr> 
    <td> 
      <div align="left"><span class="YellowHeader">Metering</span><br>
        <%= links.get("MeterData.jsp") %><br>
        <%= links.get("Usage.jsp") %></div>
    </td>
  </tr>
  <tr> 
    <td> 
      <div align="left"><span class="YellowHeader">Programs</span><br>
        <%= links.get("ProgramHist.jsp") %><br>
        <%= links.get("Programs.jsp") %></div>
    </td>
  </tr>
  <tr> 
    <td> 
      <div align="left"><span class="YellowHeader">Appliances</span><br>
        <!--
	  <%= links.get("Air.jsp") %><br>
	  <%= links.get("WH.jsp") %></div>
-->
        <%
		for (int i = 0; i < appLinks.length; i++) {
%>
        <%= appLinks[i] %><br>
        <%
		}
%>
      </div>
    </td>
  </tr>
  <tr> 
    <td> 
      <div align="left"><span class="YellowHeader">Hardware</span><br>
        <!--
        <%= links.get("LCR5000.jsp") %><br>
        <%= links.get("Thermostat.jsp") %><br>
        <%= links.get("Meter.jsp") %></div>
-->
        <%
		for (int i = 0; i < invLinks.length; i++) {
%>
        <%= invLinks[i] %><br>
        <%
		}
%>
      </div>
    </td>
  </tr>
  <tr> 
    <td> 
      <div align="left"><span class="YellowHeader">Work Orders</span><br>
        <%= links.get("Service.jsp") %><br>
        <%= links.get("ServiceSummary.jsp") %></div>
    </td>
  </tr>
  <tr> 
    <td> 
      <div align="left"><span class="YellowHeader">Administration</span><br>
        <%= links.get("Password.jsp") %><br>
        <%= links.get("Privileges.jsp")%><br>
		<%= links.get("CreateWizard.jsp")%></div>
    </td>
  </tr>
</table>
