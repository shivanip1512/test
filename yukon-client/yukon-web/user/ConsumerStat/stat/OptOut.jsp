<%@ include file="include/StarsHeader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
<!--
function confirmSubmit(form) { //v1.0
  if (form.OptOutPeriod.value == 0) return false;
  return confirm('Are you sure you would like to temporarily <cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_TEXT_OPT_OUT_VERB %>"/> all programs?');
}
//-->
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
		  <% String pageName = "OptOut.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_TITLE_OPT_OUT, "PROGRAMS - OPT OUT");%>
              <%@ include file="include/InfoBar.jsp" %>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td>
                    <hr>
                  </td>
                </tr>
              </table>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
		   
              <p><table width="500" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td class="MainText">
				    <cti:getProperty propertyid="<%=ResidentialCustomerRole.WEB_DESC_OPT_OUT %>"/>
				  </td>
                </tr>
              </table>
              <br>
              <cti:checkNoProperty propertyid="<%= ResidentialCustomerRole.HIDE_OPT_OUT_BOX %>">
              <table  border="0" cellspacing="0" cellpadding="0">
                <tr align = "center"> 
                  <td width="304" valign="top" align = "center"> 
                  <form method="post" action="<%=request.getContextPath()%>/servlet/SOAPClient" onsubmit="return confirmSubmit(this)">
				  	<input type="hidden" name="action" value="OptOutProgram">
					<input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/user/ConsumerStat/stat/General.jsp">
					<input type="hidden" name="REDIRECT2" value="<%=request.getContextPath()%>/user/ConsumerStat/stat/OptForm.jsp">
					<input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/user/ConsumerStat/stat/OptOut.jsp">
                      <table width="200" border="1" cellspacing="0" cellpadding="3" bgcolor="#CCCCCC" >
                        <tr> 
                          <td align = "center"> 
                              <p class="HeaderCell">Temporarily <cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_TEXT_OPT_OUT_VERB %>"/> 
							  all programs </p>
                            <table width="180" border="0" cellspacing="0" cellpadding="0" align="center">
                              <tr> 
                                <td width="180" align="center"> 
                          		  <select name="OptOutPeriod">
<%
	StarsCustSelectionList periodList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD_CUS );
	if (periodList != null) {
		for (int i = 0; i < periodList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = periodList.getStarsSelectionListEntry(i);
			if (entry.getYukonDefID() > 0) {	// This is a special entry, e.g. "Today"
%>
									<option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
<%
			}
			else {	// If entry.getYukonDefID() = x (<=0), then -x is the number of days to be opted out
%>
									<option value="<%= entry.getYukonDefID() %>"><%= entry.getContent() %></option>
<%
			}
		}
	}
	else {
%>
									<option value="-1">One Day</option>
									<option value="-2">Two Days</option>
									<option value="-3">Three Days</option>
									<option value="-7">One Week</option>
									<option value="-14">Two Weeks</option>
<%
	}
%>
                          		  </select>
                                </td>
                                <td width="180" align="center"> 
                                  <input type="submit" name="Submit" value="Submit" <% if (programs.getStarsLMProgramCount() == 0) out.print("disabled"); %>>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                      <br>
                    </form>
                  </td>
                </tr>
              </table>
              </cti:checkNoProperty>              
            </div>
            <p>&nbsp;</p>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
