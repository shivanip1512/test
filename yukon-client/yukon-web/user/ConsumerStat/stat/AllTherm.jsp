<%@ include file="include/StarsHeader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
function selectAll(checked) {
	var checkboxes = document.getElementsByName("InvID");
	for (i = 0; i < checkboxes.length; i++)
		checkboxes[i].checked = checked;
}

function selectSingle(form) {
	var checkboxes = document.getElementsByName("InvID");
	var allChecked = true;
	for (i = 0; i < checkboxes.length; i++)
		if (!checkboxes[i].checked) {
			allChecked = false;
			break;
		}
	form.All.checked = allChecked;
}

function goToSchedule(form) {
}

function goToManual(form) {
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../../WebConfig/<cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_IMG_CORNER %>"/>">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height="28">&nbsp;</td>
				  <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle">&nbsp;</td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
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
		  <% String pageName = "AllTherm.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <% String header = "THERMOSTAT - APPLY TO ALL"; %>
              <%@ include file="include/InfoBar.jsp" %>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td>
                    <hr>
                  </td>
                </tr>
              </table>
              
              <form name="form1" method="post" action="">
                <span class="MainText"> Select the thermostats you want to control:</span><br>
                <table width="200" border="0" cellspacing="3" cellpadding="0" align="center">
                  <tr> 
                    <td width="32"> 
                      <div align="right" class="TableCell"> 
                        <input type="checkbox" name="All" value="true" onclick="selectAll(this.checked)">
                      </div>
                    </td>
                    <td width="256" class="TableCell">All</td>
                  </tr>
<%
	for (int i = 0; i < thermostats.getStarsLMHardwareCount(); i++) {
		StarsLMHardware thermostat = thermostats.getStarsLMHardware(i);
%>
                  <tr> 
                    <td width="32"> 
                      <div align="right"> 
                        <input type="checkbox" name="InvID" value="<%= thermostat.getInventoryID() %>" onclick="selectSingle(this.form)">
                      </div>
                    </td>
                    <td width="256" class="TableCell"><%= thermostat.getDeviceLabel() %></td>
                  </tr>
<%
	}
%>
                </table>
                <br>
                <table width="200" border="0" cellspacing="5" cellpadding="0" align="center">
                  <tr> 
                    <td> 
                      <div align="center" style="border:solid 1px #666999; cursor:pointer" onclick="goToSchedule(this.form)">
					    <span class="MainText"><%= AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_SCHED, "Schedule") %></span>
					  </div>
                    </td>
                    <td> 
                      <div align="center" style="border:solid 1px #666999; cursor:pointer" onclick="goToManual(this.form)">
					    <span class="MainText"><%= AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LABEL_THERM_MANUAL, "Manual") %></span>
					  </div>
                    </td>
                  </tr>
                </table>
              </form>
            </div>
            <p>&nbsp;</p>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
