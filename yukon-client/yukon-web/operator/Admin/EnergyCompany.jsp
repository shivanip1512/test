<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.cache.functions.PAOFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonGroup" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject" %>
<%@ page import="com.cannontech.stars.web.servlet.StarsAdmin" %>
<%
	String action = request.getParameter("action");
	if (action == null) action = "";
	
	if (action.equalsIgnoreCase("init")) {
		session.removeAttribute(StarsAdmin.ENERGY_COMPANY_TEMP);
	}
	else if (action.equalsIgnoreCase("EditAddress")) {
		StarsEnergyCompany ecTemp = (StarsEnergyCompany) session.getAttribute(StarsAdmin.ENERGY_COMPANY_TEMP);
		if (ecTemp == null) {
			ecTemp = new StarsEnergyCompany();
			if (energyCompany.getCompanyAddress().getAddressID() == 0)
				ecTemp.setCompanyAddress( (CompanyAddress)StarsFactory.newStarsCustomerAddress(CompanyAddress.class) );
			else
				ecTemp.setCompanyAddress( energyCompany.getCompanyAddress() );
			session.setAttribute(StarsAdmin.ENERGY_COMPANY_TEMP, ecTemp);
		}
		ecTemp.setCompanyName( request.getParameter("CompanyName") );
		ecTemp.setMainPhoneNumber( request.getParameter("PhoneNo") );
		ecTemp.setMainFaxNumber( request.getParameter("FaxNo") );
		ecTemp.setEmail( request.getParameter("Email") );
		ecTemp.setTimeZone( request.getParameter("TimeZone") );
		
		response.sendRedirect("Address.jsp?referer=EnergyCompany.jsp");
		return;
	}
	
	StarsEnergyCompany ec = (StarsEnergyCompany) session.getAttribute(StarsAdmin.ENERGY_COMPANY_TEMP);
	if (ec == null) ec = energyCompany;
	
	String address = ServletUtils.getOneLineAddress(ec.getCompanyAddress());
	if (address.length() == 0) address = "(none)";
	
	LiteYukonGroup[] operGroups = liteEC.getWebClientOperatorGroups();
	LiteYukonGroup[] custGroups = liteEC.getResidentialCustomerGroups();
	String operGroup = "";
	String custGroup = "";
	for (int i = 0; i < operGroups.length; i++)
		operGroup += operGroups[i].getGroupName() + ",";
	for (int i = 0; i < custGroups.length; i++)
		custGroup += custGroups[i].getGroupName() + ",";
	
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
                          <input type="text" name="CompanyName" value="<%= ec.getCompanyName() %>" size="30">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Main Phone 
                          #:</td>
                        <td width="75%" class="TableCell"> 
                          <input type="text" name="PhoneNo" value="<%= ec.getMainPhoneNumber() %>" size="30">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Main Fax 
                          #:</td>
                        <td width="75%" class="TableCell"> 
                          <input type="text" name="FaxNo" value="<%= ec.getMainFaxNumber() %>" size="30">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Email:</td>
                        <td width="75%" class="TableCell"> 
                          <input type="text" name="Email" value="<%= ec.getEmail() %>" size="30">
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
                          <input type="text" name="TimeZone" value="<%= ec.getTimeZone() %>" size="14">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Default 
                          Route:</td>
                        <td class="TableCell"> 
                          <select name="Route">
                            <option value="<%= LiteStarsEnergyCompany.INVALID_ROUTE_ID %>">(none)</option>
                            <%
	LiteYukonPAObject[] routes = liteEC.getAllRoutes();
	for (int i = 0; i < routes.length; i++) {
		String selected = (routes[i].getYukonID() == liteEC.getDefaultRouteID())? "selected" : "";
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
                          <input type="text" name="OperatorGroup" size="50" value="<%= operGroup %>">
                          <br>
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
                          <input type="button" name="AddOperGrp" value="Add" onClick="addOperatorGroup(this.form)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Res. Customer 
                          Groups:</td>
                        <td class="TableCell"> 
                          <input type="text" name="CustomerGroup" size="50" value="<%= custGroup %>">
                          <br>
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
                          <input type="button" name="AddCustGrp" value="Add" onClick="addCustomerGroup(this.form)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Opt out 
                          Notif. Recipients:</td>
                        <td class="TableCell"> 
                          <input type="text" name="OptOutNotif" size="50" value="<%= liteEC.getEnergyCompanySetting(EnergyCompanyRole.OPTOUT_NOTIFICATION_RECIPIENTS) %>">
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
                    <input type="reset" name="Reset" value="Reset">
                  </td>
                  <td width="75" align="right"> 
                    <input type="button" name="Back" value="Back" onclick="location.href='AdminTest.jsp'">
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
