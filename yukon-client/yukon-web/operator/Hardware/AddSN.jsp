<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.common.constants.YukonListEntry" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionList" %>
<%
	Properties savedReq = null;
	if (request.getParameter("failed") != null) {
		savedReq = (Properties) session.getAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST);
	}
	else if (request.getParameter("Member") != null) {
		ServletUtils.saveRequest(request, session, new String[] {"Member", "From", "To", "DeviceType", "ReceiveDate", "Voltage", "ServiceCompany", "Route"});
		savedReq = (Properties) session.getAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST);
	}
	else
		session.removeAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST);
	if (savedReq == null) savedReq = new Properties();
	
	LiteStarsEnergyCompany member = null;
	if (savedReq.getProperty("Member") != null)
		member = StarsDatabaseCache.getInstance().getEnergyCompany(Integer.parseInt(savedReq.getProperty("Member")));
	if (member == null) member = liteEC;
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function changeMember(form) {
	form.attributes["action"].value = "AddSN.jsp";
	form.submit();
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
          <td  valign="top" width="101">
            <% String pageName = "AddSN.jsp"; %>
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center"> 
              <% String header = "ADD SERIAL NUMBER RANGE"; %>
              <%@ include file="include/SearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
              <form name="form1" method="post" action="<%= request.getContextPath() %>/servlet/InventoryManager">
			    <input type="hidden" name="action" value="AddSNRange">
			    <input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>">
			    <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?failed">
                <table width="64%" border="1" cellspacing="0" cellpadding="5" align="center" height="91">
                  <tr> 
                    <td align = "left" class = "TitleHeader" bgcolor="#CCCCCC">Add Serial Number Range</td>
                  </tr>
                  <tr> 
                    <td> 
                      <table width="100%" border="0" class="TableCell">
<% if (liteEC.getChildren().size() > 0 && AuthFuncs.checkRoleProperty(lYukonUser, AdministratorRole.ADMIN_MANAGE_MEMBERS)) { %>
                        <tr>
                          <td width="25%" align="right">Member:</td>
                          <td width="75%">
                            <select name="Member" onchange="changeMember(this.form)">
<%
	ArrayList descendants = ECUtils.getAllDescendants(liteEC);
	for (int i = 0; i < descendants.size(); i++) {
		LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
		String selected = company.equals(member)? "selected" : "";
%>
                              <option value="<%= company.getLiteID() %>" <%= selected %>><%= company.getName() %></option>
<%
	}
%>
                            </select>
                          </td>
                        </tr>
<% } %>
                        <tr> 
                          <td width="25%"> 
                            <div align="right">Range:</div>
                          </td>
                          <td width="75%"> 
                            <input type="text" name="From" size="10" value="<%= ServerUtils.forceNotNull(savedReq.getProperty("From")) %>">
                            &nbsp;to&nbsp; 
                            <input type="text" name="To" size="10" value="<%= ServerUtils.forceNotNull(savedReq.getProperty("To")) %>">
                          </td>
                        </tr>
                        <tr> 
                          <td width="25%"> 
                            <div align="right">Device Type: </div>
                          </td>
                          <td width="75%"> 
                            <select name="DeviceType">
                              <%
	int savedDeviceType = 0;
	if (savedReq.getProperty("DeviceType") != null)
		savedDeviceType = Integer.parseInt(savedReq.getProperty("DeviceType"));
	
	YukonSelectionList devTypeList = member.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
	for (int i = 0; i < devTypeList.getYukonListEntries().size(); i++) {
		YukonListEntry entry = (YukonListEntry) devTypeList.getYukonListEntries().get(i);
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT) continue;
		String selected = (entry.getEntryID() == savedDeviceType)? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getEntryText() %></option>
                              <%
	}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="25%"> 
                            <div align="right">Receive Date:</div>
                          </td>
                          <td width="75%"> 
                            <input type="text" name="ReceiveDate" size="24" value="<%= ServerUtils.forceNotNull(savedReq.getProperty("ReceiveDate")) %>">
                            <span class="DefaultText">(MM/DD/YYYY)</span> </td>
                        </tr>
                        <tr> 
                          <td width="25%"> 
                            <div align="right">Voltage:</div>
                          </td>
                          <td width="75%"> 
                            <select name="Voltage">
                              <%
	int savedVoltage = 0;
	if (savedReq.getProperty("Voltage") != null)
		savedVoltage = Integer.parseInt(savedReq.getProperty("Voltage"));
	
	YukonSelectionList voltageList = member.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE );
	for (int i = 0; i < voltageList.getYukonListEntries().size(); i++) {
		YukonListEntry entry = (YukonListEntry) voltageList.getYukonListEntries().get(i);
		String selected = (entry.getEntryID() == savedVoltage)? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getEntryText() %></option>
                              <%
	}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="25%"> 
                            <div align="right">Service Company:</div>
                          </td>
                          <td width="75%"> 
                            <select name="ServiceCompany">
                              <%
	int savedServiceCompany = 0;
	if (savedReq.getProperty("ServiceCompany") != null)
		savedServiceCompany = Integer.parseInt(savedReq.getProperty("ServiceCompany"));
	
	StarsServiceCompanies companyList = member.getStarsServiceCompanies();
	for (int i = 0; i < companyList.getStarsServiceCompanyCount(); i++) {
		StarsServiceCompany servCompany = companyList.getStarsServiceCompany(i);
		String selected = (servCompany.getCompanyID() == savedServiceCompany)? "selected" : "";
%>
                              <option value="<%= servCompany.getCompanyID() %>" <%= selected %>><%= servCompany.getCompanyName() %></option>
                              <%
	}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="25%"> 
                            <div align="right">Route:</div>
                          </td>
                          <td width="75%"> 
                            <select name="Route">
                              <option value="0">(Default Route)</option>
                              <%
	int savedRoute = 0;
	if (savedReq.getProperty("Route") != null)
		savedRoute = Integer.parseInt(savedReq.getProperty("Route"));
	
	LiteYukonPAObject[] routeList = member.getAllRoutes();
	for (int i = 0; i < routeList.length; i++) {
		String selected = (routeList[i].getYukonID() == savedRoute)? "selected" : "";
%>
                              <option value="<%= routeList[i].getYukonID() %>" <%= selected %>><%= routeList[i].getPaoName() %></option>
                              <%
	}
%>
                            </select>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                <br>
                <table width="64%" border="0" cellspacing="0" cellpadding="3" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="210"> 
                      <div align="right"> 
                        <input type="submit" name="Submit" value="Submit">
                      </div>
                    </td>
                    <td width="210"> 
                      <div align="left"> 
                        <input type="reset" name="Reset" value="Reset">
                      </div>
                    </td>
                  </tr>
                </table>
              </form>
              <br>
              </div>
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
