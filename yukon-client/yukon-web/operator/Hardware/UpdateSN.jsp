<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function updateField(f, checked) {
	f.disabled = !checked;
	if (checked) f.focus();
}

function init() {
	var form = document.form1;
	form.NewDeviceType.disabled = !form.UpdateDeviceType.checked;
	form.ReceiveDate.disabled = !form.UpdateRecvDate.checked;
	form.Voltage.disabled = !form.UpdateVoltage.checked;
	form.ServiceCompany.disabled = !form.UpdateSrvCompany.checked;
	form.Route.disabled = !form.UpdateRoute.checked;
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="init()">
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
            <% String pageName = "UpdateSN.jsp"; %>
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center"> 
              <% String header = "UPDATE SERIAL NUMBER RANGE"; %>
              <%@ include file="include/SearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
			  
              <form name="form1" method="post" action="<%= request.getContextPath() %>/servlet/InventoryManager">
			    <input type="hidden" name="action" value="UpdateSNRange">
                <table width="64%" border="1" cellspacing="0" cellpadding="5" align="center" height="91">
                  <tr> 
                    <td align = "left" class = "MainText" bgcolor="#CCCCCC"><b>Update 
                      Serial Number Range</b></td>
                  </tr>
                  <tr> 
                    <td> 
                      <table width="100%" border="0" class="TableCell">
                        <tr> 
                          <td width="5%">&nbsp;</td>
                          <td width="25%"> 
                            <div align="left">Range:</div>
                          </td>
                          <td width="70%"> 
                            <input type="text" name="From" size="10">
                            &nbsp;to&nbsp; 
                            <input type="text" name="To" size="10">
                          </td>
                        </tr>
                        <tr> 
                          <td width="5%">&nbsp;</td>
                          <td width="25%"> 
                            <div align="left">Device Type: </div>
                          </td>
                          <td width="70%"> 
                            <select name="DeviceType" onchange="this.form.NewDeviceType.value = this.value">
<%
	StarsCustSelectionList deviceTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
	for (int i = 0; i < deviceTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = deviceTypeList.getStarsSelectionListEntry(i);
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT) continue;
%>
                              <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
<%
	}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="5%"> 
                            <input type="checkbox" name="UpdateDeviceType" value="true" onClick="updateField(this.form.NewDeviceType, this.checked)">
                          </td>
                          <td width="25%"> 
                            <div align="left">New Device Type:</div>
                          </td>
                          <td width="70%"> 
                            <select name="NewDeviceType">
<%
	for (int i = 0; i < deviceTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = deviceTypeList.getStarsSelectionListEntry(i);
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT) continue;
%>
                              <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
<%
	}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="5%"> 
                            <input type="checkbox" name="UpdateRecvDate" value="true" onclick="updateField(this.form.ReceiveDate, this.checked)">
                          </td>
                          <td width="25%"> 
                            <div align="left">Receive Date:</div>
                          </td>
                          <td width="70%"> 
                            <input type="text" name="ReceiveDate" size="24">
                          </td>
                        </tr>
                        <tr> 
                          <td width="5%"> 
                            <input type="checkbox" name="UpdateVoltage" value="true" onclick="updateField(this.form.Voltage, this.checked)">
                          </td>
                          <td width="25%"> 
                            <div align="left">Voltage:</div>
                          </td>
                          <td width="70%"> 
                            <select name="Voltage">
<%
	StarsCustSelectionList voltageList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE );
	for (int i = 0; i < voltageList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = voltageList.getStarsSelectionListEntry(i);
%>
                              <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
<%
	}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="5%"> 
                            <input type="checkbox" name="UpdateSrvCompany" value="true" onclick="updateField(this.form.ServiceCompany, this.checked)">
                          </td>
                          <td width="25%"> 
                            <div align="left">Service Company:</div>
                          </td>
                          <td width="70%"> 
                            <select name="ServiceCompany">
<%
	for (int i = 0; i < companies.getStarsServiceCompanyCount(); i++) {
		StarsServiceCompany servCompany = companies.getStarsServiceCompany(i);
%>
                              <option value="<%= servCompany.getCompanyID() %>"><%= servCompany.getCompanyName() %></option>
<%
	}
%>
                            </select>
                          </td>
                        </tr>
                        <tr>
                          <td width="5%">
                            <input type="checkbox" name="UpdateRoute" value="true" onClick="updateField(this.form.Route, this.checked)">
                          </td>
                          <td width="25%">Route:</td>
                          <td width="70%">
                            <select name="Route">
                              <option value="0">(Default Route)</option>
<%
	LiteYukonPAObject[] routes = liteEC.getAllRoutes();
	for (int i = 0; i < routes.length; i++) {
%>
                              <option value="<%= routes[i].getYukonID() %>"><%= routes[i].getPaoName() %></option>
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
