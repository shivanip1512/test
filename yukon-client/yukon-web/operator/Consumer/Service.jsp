<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
function validate(form) {
	if (form.OrderNo != null && form.OrderNo.value == "") {
		alert("Order # cannot be empty");
		return false;
	}
	return true;
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../WebConfig/yukon/ConsumerImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="-" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                  </td>
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
		  <% String pageName = "Service.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center">
			  <% String header = "WORK ORDERS - SERVICE REQUEST"; %>
			  <%@ include file="include/InfoSearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
			  <form name="soForm" method="POST" action="<%= request.getContextPath() %>/servlet/SOAPClient" onsubmit="return validate(this)">
                <input type="hidden" name="action" value="CreateWorkOrder">
                <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/SOHistory.jsp?OrderNo=0">
                <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
                <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
                  <tr> 
                    <td valign="top" bgcolor="#FFFFFF"> 
                      <table width="360" border="0" cellspacing="0" cellpadding="3" align="center" class="TableCell">
                        <cti:checkNoProperty propertyid="<%= ConsumerInfoRole.ORDER_NUMBER_AUTO_GEN %>"> 
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Service Order #:</div>
                          </td>
                          <td width="248"> 
                            <input type="text" name="OrderNo" size="14" maxlength="20">
                          </td>
                        </tr>
                        </cti:checkNoProperty> 
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Date Reported:</div>
                          </td>
                          <td width="248"> 
                            <input type="text" name="DateReported" size="14" value="<%= ServletUtils.formatDate(new Date(), datePart) %>">
                            - 
                            <input type="text" name="TimeReported" size="8" value="<%= ServletUtils.formatDate(new Date(), timeFormat) %>">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Service Type:</div>
                          </td>
                          <td width="248"> 
                            <select name="ServiceType">
                              <%
	StarsCustSelectionList serviceTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE );
	for (int i = 0; i < serviceTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = serviceTypeList.getStarsSelectionListEntry(i);
%>
                              <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
                              <%
	}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Assign to:</div>
                          </td>
                          <td width="248"> 
                            <select name="ServiceCompany">
                              <%
	for (int i = 0; i < companies.getStarsServiceCompanyCount(); i++) {
		StarsServiceCompany company = companies.getStarsServiceCompany(i);
%>
                              <option value="<%= company.getCompanyID() %>"><%= company.getCompanyName() %></option>
                              <%
	}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Ordered By:</div>
                          </td>
                          <td width="248"> 
                            <input type="text" name="OrderedBy" size="14">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Notes: </div>
                          </td>
                          <td width="248"> 
                            <textarea name="Description" rows="3" wrap="soft" cols="35" class = "TableCell"></textarea>
                          </td>
                        </tr>
                      </table>
                      <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                        <tr> 
                          <td width="169"> 
                            <div align="right"> 
                              <input type="submit" name="Send" value="Send">
                            </div>
                          </td>
                          <td width="211"> 
                            <div align="left"> 
                              <input type="reset" name="Cancel" value="Cancel">
                            </div>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
              </form>
            </div>
            <p>&nbsp;</p>
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
