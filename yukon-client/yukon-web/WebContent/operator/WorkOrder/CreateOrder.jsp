<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.lite.stars.*" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<script language="JavaScript">
function validate(form) {
	if (form.OrderNo != null && form.OrderNo.value == "") {
		alert("Work Order # cannot be empty");
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
            <% String pageName = "CreateOrder.jsp"; %>
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "CREATE SERVICE ORDER"; %>
              <%@ include file="include/SearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              
			  <form name="soForm" method="post" action="<%= request.getContextPath() %>/servlet/WorkOrderManager" onsubmit="return validate(this)">
                <input type="hidden" name="action" value="CreateWorkOrder">
                <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/WorkOrder/WorkOrder.jsp?OrderId=">
                <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
                <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
                  <tr> 
                    <td valign="top" bgcolor="#FFFFFF"> 
                      <table width="360" border="0" cellspacing="0" cellpadding="3" align="center" class="TableCell">
<%
	String autoGen = liteEC.getEnergyCompanySetting(ConsumerInfoRole.ORDER_NUMBER_AUTO_GEN);
	if (autoGen == null || CtiUtilities.isFalse(autoGen)) {
%>
                        <tr> 
                          <td width="100" class="SubtitleHeader"> 
                            <div align="right">*Service Order #:</div>
                          </td>
                          <td width="248"> 
                            <input type="text" name="OrderNo" size="14" maxlength="20" onchange="setContentChanged(true)">
                          </td>
                        </tr>
<%
	}
%>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Account #:</div>
                          </td>
                          <td width="248"> 
                            <input type="text" name="AcctNo" size="14" onchange="setContentChanged(true)">
                            (Optional) </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Date Reported:</div>
                          </td>
                          <td width="248"> 
                            <input type="text" name="DateReported" size="14" value="<%= ServletUtils.formatDate(new Date(), datePart) %>" onchange="setContentChanged(true)">
                            - 
                            <input type="text" name="TimeReported" size="8" value="<%= ServletUtils.formatDate(new Date(), timeFormat) %>" onchange="setContentChanged(true)">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Service Type:</div>
                          </td>
                          <td width="248"> 
                            <select name="ServiceType" onchange="setContentChanged(true)">
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
                            <select name="ServiceCompany" onchange="setContentChanged(true)">
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
                            <input type="text" name="OrderedBy" size="14" onchange="setContentChanged(true)">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Notes: </div>
                          </td>
                          <td width="248"> 
                            <textarea name="Description" rows="3" wrap="soft" cols="35" class = "TableCell" onchange="setContentChanged(true)"></textarea>
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
                              <input type="reset" name="Reset" value="Reset" onclick="setContentChanged(false)">
                            </div>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
              </form>
              <p>&nbsp;</p>
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
