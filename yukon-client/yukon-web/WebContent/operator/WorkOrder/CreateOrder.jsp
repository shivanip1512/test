<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.database.data.lite.*" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<%
int statusPending = liteEC.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_PENDING).getEntryID();
%>
<script language="JavaScript">
function validate(form) {
	if (form.OrderNo != null && form.OrderNo.value == "") {
		alert("Work Order # cannot be empty");
		return false;
	}
	return true;
}

function changeStatus(form) {
	if( form.CurrentState.value == "<%= statusPending %>" )
	{
		document.getElementById("ServComp")[0].selected = true;
		document.getElementById("ServComp").disabled = true;
	}
	else {
		document.getElementById("ServComp").disabled = false;
	}
}

</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jspf" %>
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
            <%@ include file="include/Nav.jspf" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "CREATE SERVICE ORDER"; %>
              <%@ include file="include/SearchBar.jspf" %>
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
                            <div align="right">*Work Order #:</div>
                          </td>
                          <td width="248"> 
                            <input type="text" name="OrderNo" size="14" maxlength="20" onchange="setContentChanged(true)">
                          </td>
                        </tr>
						<% } %>
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
                            <div align="right">Service Type:</div>
                          </td>
                          <td width="248"> 
                            <select name="ServiceType" onchange="setContentChanged(true)">
                              <% StarsCustSelectionList serviceTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE );
                              	for (int i = 0; i < serviceTypeList.getStarsSelectionListEntryCount(); i++) {
                              		StarsSelectionListEntry entry = serviceTypeList.getStarsSelectionListEntry(i);
                              %>
                              	<option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
                              <% } %>
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
                            <div align="right"><cti:getProperty propertyid="<%= WorkOrderRole.ADDTL_ORDER_NUMBER_LABEL%>" defaultvalue="Addtl Order #"/>:</div>
                          </td>
                          <td width="248"> 
                            <input type="text" name="AddtlOrderNumber" size="14" onchange="setContentChanged(true)">
                          </td>
                        </tr>
                        <tr> 
                          <td width="30%" class="TableCell"> 
                            <div align="right">Current State:</div>
                          </td>
                          <td width="70%"> 
                            <select name="CurrentState" onchange="changeStatus(this.form);setContentChanged(true);">
                            <% StarsCustSelectionList serviceStatusList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS );
                            	for (int i = 0; i < serviceStatusList.getStarsSelectionListEntryCount(); i++) {
                            		StarsSelectionListEntry entry = serviceStatusList.getStarsSelectionListEntry(i);
                            %>
                              <option value="<%= entry.getEntryID() %>" ><%= entry.getContent() %></option>
                            <% } %>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width="30%" align="right" class="TableCell">Event Date:</td>
                          <td width="70%"> 
                            <input type="text" name="DateEventTimestamp" size="14" value="<%= ServletUtils.formatDate(new Date(), datePart) %>" onchange="setContentChanged(true)">
                            - 
                            <input type="text" name="TimeEventTimestamp" size="8" value="<%= ServletUtils.formatDate(new Date(), timeFormat) %>" onchange="setContentChanged(true)">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Assign to:</div>
                          </td>
                          <td width="248"> 
                            <select id="ServComp" name="ServiceCompany" onchange="setContentChanged(true)" disabled>
                              <% for (int i = 0; i < companies.getStarsServiceCompanyCount(); i++) {
									StarsServiceCompany company = companies.getStarsServiceCompany(i);
							  %>
                              	<option value="<%= company.getCompanyID() %>"><%= company.getCompanyName() %></option>
                              <% } %>
                            </select>
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
                              <input type="submit" name="Submit" value="Save">
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
