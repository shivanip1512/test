<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonGroup" %>
<%
	String action = request.getParameter("action");
	if (action == null) action = "";
	
	StarsEnergyCompany ec = null;
	Properties savedReq = null;
	
	if (action.equalsIgnoreCase("init")) {
		// Remove all saved form fields
		session.removeAttribute(StarsAdminUtil.ENERGY_COMPANY_TEMP);
		session.removeAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST);
	}
	else if (action.equalsIgnoreCase("EditAddress")) {
		// Save all form fields
		StarsEnergyCompany ecTemp = (StarsEnergyCompany) session.getAttribute(StarsAdminUtil.ENERGY_COMPANY_TEMP);
		if (ecTemp == null) {
			ecTemp = new StarsEnergyCompany();
			if (energyCompany.getCompanyAddress().getAddressID() == 0)
				ecTemp.setCompanyAddress( (CompanyAddress)StarsFactory.newStarsCustomerAddress(CompanyAddress.class) );
			else
				ecTemp.setCompanyAddress( energyCompany.getCompanyAddress() );
			session.setAttribute(StarsAdminUtil.ENERGY_COMPANY_TEMP, ecTemp);
		}
		
		ecTemp.setCompanyName( request.getParameter("CompanyName") );
		ecTemp.setMainPhoneNumber( request.getParameter("PhoneNo") );
		ecTemp.setMainFaxNumber( request.getParameter("FaxNo") );
		ecTemp.setEmail( request.getParameter("Email") );
		ecTemp.setTimeZone( request.getParameter("TimeZone") );
		ServletUtils.saveRequest(request, session, new String[] {"Route", "OperatorGroup", "CustomerGroup", "AdminEmail", "OptOutNotif"});
		
		response.sendRedirect("Address.jsp?referer=EnergyCompany.jsp");
		return;
	}
	else {
		ec = (StarsEnergyCompany) session.getAttribute(StarsAdminUtil.ENERGY_COMPANY_TEMP);
		savedReq = (Properties) session.getAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST);
		session.removeAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST);
	}
	
	if (ec == null) ec = energyCompany;
	if (savedReq == null) savedReq = new Properties();
	
	String address = ServletUtils.getOneLineAddress(ec.getCompanyAddress());
	if (address.length() == 0) address = "(none)";
	
	String operGroup = savedReq.getProperty("OperatorGroup");
	if (operGroup == null) {
		operGroup = "";
		LiteYukonGroup[] operGroups = liteEC.getWebClientOperatorGroups();
		for (int i = 0; i < operGroups.length; i++)
			operGroup += operGroups[i].getGroupName() + ",";
	}
	
	String custGroup = savedReq.getProperty("CustomerGroup");
	if (custGroup == null) {
		custGroup = "";
		LiteYukonGroup[] custGroups = liteEC.getResidentialCustomerGroups();
		for (int i = 0; i < custGroups.length; i++)
			custGroup += custGroups[i].getGroupName() + ",";
	}
	
	String adminEmail = savedReq.getProperty("AdminEmail");
	if (adminEmail == null)
		adminEmail = liteEC.getAdminEmailAddress();
	
	String optOutNotif = savedReq.getProperty("OptOutNotif");
	if (optOutNotif == null)
		optOutNotif = liteEC.getEnergyCompanySetting(EnergyCompanyRole.OPTOUT_NOTIFICATION_RECIPIENTS).trim();
	
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	ArrayList yukonGroups = null;
	synchronized (cache) {
		List groups = cache.getAllYukonGroups();
		Collections.sort(groups, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);
		yukonGroups = new ArrayList(groups);
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function editAddress(form) {
	form.attributes["action"].value = "";
	form.action.value = "EditAddress";
	form.submit();
}

function appendString(str1, str2) {
	var i = str1.length - 1;
	while (i >= 0) {
		if (str1.charAt(i) != ' ' && str1.charAt(i) != '\t') break;
		i--;
	}
	
	if (i < 0)
		str1 = str2;
	else if (str1.charAt(i) == ',')
		str1 = str1.substr(0, i+1) + str2;
	else
		str1 = str1.substr(0, i+1) + "," + str2;
	
	return str1;
}

function addOperatorGroup(form) {
	if (form.OperGroupList.value == "") return;
	form.OperatorGroup.value = appendString(form.OperatorGroup.value, form.OperGroupList.value);
}

function addCustomerGroup(form) {
	if (form.CustGroupList.value == "") return;
	form.CustomerGroup.value = appendString(form.CustomerGroup.value, form.CustGroupList.value);
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jsp" %>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
            <div align="center"> <br>
              <span class="TitleHeader">ADMINISTRATION - ENERGY COMPANY</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin">
              <input type="hidden" name="action" value="UpdateEnergyCompany">
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td class="HeaderCell">Edit Energy Company Information</td>
                </tr>
                <tr> 
                  <td height="67"> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="5">
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Company 
                          Name:</td>
                        <td width="75%" class="TableCell"> 
                          <input type="text" name="CompanyName" value="<%= ec.getCompanyName() %>" size="30" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Main Phone 
                          #:</td>
                        <td width="75%" class="TableCell"> 
                          <input type="text" name="PhoneNo" value="<%= ec.getMainPhoneNumber() %>" size="30" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Main Fax 
                          #:</td>
                        <td width="75%" class="TableCell"> 
                          <input type="text" name="FaxNo" value="<%= ec.getMainFaxNumber() %>" size="30" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Email:</td>
                        <td width="75%" class="TableCell"> 
                          <input type="text" name="Email" value="<%= ec.getEmail() %>" size="30" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell"> Company 
                          Address:</td>
                        <td width="75%" class="TableCell"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                            <tr> 
                              <td width="75%"><%= address %></td>
                              <td width="25%"> 
                                <input type="button" name="EditAddress" value="Edit" onclick="editAddress(this.form)">
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
              <br>
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td class="HeaderCell">Edit Energy Company Settings</td>
                </tr>
                <tr> 
                  <td height="67"> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="5">
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Time Zone:</td>
                        <td class="TableCell"> 
                          <input type="text" name="TimeZone" value="<%= ec.getTimeZone() %>" size="14" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Default 
                          Route:</td>
                        <td class="TableCell"> 
                          <select name="Route" onchange="setContentChanged(true)">
                            <option value="<%= LiteStarsEnergyCompany.INVALID_ROUTE_ID %>">(none)</option>
<%
	int routeID = liteEC.getDefaultRouteID();
	if (savedReq.getProperty("Route") != null)
		routeID = Integer.parseInt(savedReq.getProperty("Route"));
	
	LiteYukonPAObject[] routes = liteEC.getAllRoutes();
	for (int i = 0; i < routes.length; i++) {
		String selected = (routes[i].getYukonID() == routeID)? "selected" : "";
%>
                            <option value="<%= routes[i].getYukonID() %>" <%= selected %>><%= routes[i].getPaoName() %></option>
<%
	}
%>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Operator 
                          Groups: </td>
                        <td class="TableCell"> 
                          <input type="text" name="OperatorGroup" size="50" value="<%= operGroup %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">&nbsp;</td>
                        <td class="TableCell"> 
                          <select name="OperGroupList">
<%
	for (int i = 0; i < yukonGroups.size(); i++) {
		LiteYukonGroup group = (LiteYukonGroup) yukonGroups.get(i);
%>
                            <option value="<%= group.getGroupName() %>"><%= group.getGroupName() %></option>
<%
	}
%>
                          </select>
                          <input type="button" name="AddOperGrp" value="Add" onclick="addOperatorGroup(this.form);setContentChanged(true);">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Res. Customer 
                          Groups:</td>
                        <td class="TableCell"> 
                          <input type="text" name="CustomerGroup" size="50" value="<%= custGroup %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr>
                        <td width="25%" align="right" class="TableCell">&nbsp;</td>
                        <td class="TableCell">
                          <select name="CustGroupList">
<%
	for (int i = 0; i < yukonGroups.size(); i++) {
		LiteYukonGroup group = (LiteYukonGroup) yukonGroups.get(i);
%>
                            <option value="<%= group.getGroupName() %>"><%= group.getGroupName() %></option>
<%
	}
%>
                          </select>
                          <input type="button" name="AddCustGrp" value="Add" onclick="addCustomerGroup(this.form);setContentChanged(true);">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Admin. Email 
                          Sender:</td>
                        <td class="TableCell"> 
                          <input type="text" name="AdminEmail" size="50" value="<%= adminEmail %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Opt out 
                          Notif. Recipients:</td>
                        <td class="TableCell"> 
                          <input type="text" name="OptOutNotif" size="50" value="<%= optOutNotif %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
              <br>
              <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr>
                  <td width="290" align="right"> 
                    <input type="submit" name="Submit" value="Submit">
                  </td>
                  <td width="205"> 
                    <input type="reset" name="Reset" value="Reset" onclick="setContentChanged(false)">
                  </td>
                  <td width="75" align="right"> 
                    <input type="button" name="Back" value="Back" onclick="if (warnUnsavedChanges()) location.href='AdminTest.jsp'">
                  </td>
                </tr>
              </table>
            </form>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
