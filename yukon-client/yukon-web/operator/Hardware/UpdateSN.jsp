<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">
function updateField(f, checked) {
	f.disabled = !checked;
	if (checked) f.focus();
}

function init() {
	var form = document.form1;
	form.ReceiveDate.disabled = !form.UpdateRecvDate.checked;
	form.Voltage.disabled = !form.UpdateVoltage.checked;
	form.ServiceCompany.disabled = !form.UpdateSrvCompany.checked;
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="init()">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../WebConfig/yukon/InventoryImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="310" height="28" class="PageHeader">&nbsp;&nbsp;&nbsp;Hardware 
                  Inventory </td>
                <td width="235" height = "30" valign="middle">&nbsp;</td>
                <form method="post" action="../Operations.jsp">
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
                </form>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          </tr>
      </table>
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
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
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
                            <input type="text" name="From" size="10">&nbsp;to&nbsp;<input type="text" name="To" size="10">
                          </td>
                        </tr>
                        <tr> 
                          <td width="5%">&nbsp;</td>
                          <td width="25%"> 
                            <div align="left">Device Type: </div>
                          </td>
                          <td width="70%"> 
                            <select name="DeviceType">
                              <%
	StarsCustSelectionList deviceTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
	for (int i = 0; i < deviceTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = deviceTypeList.getStarsSelectionListEntry(i);
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
                        <input type="reset" name="Cancel" value="Cancel">
                      </div>
                    </td>
                  </tr>
                </table>
              </form>
              <br>
              </div>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
