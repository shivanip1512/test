<%@ include file="include/StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function prepareSubmit(form) {
	var checkboxes = document.getElementsByName("InvID");
	for (i = 0; i < checkboxes.length; i++) {
		if (checkboxes[i].checked)
			return true;
	}
	alert("You must select at least one hardware");
	return false;
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<div id = "tool" class = "tooltip" style="width: 1003px; height: 20px"></div>
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jspf" %>
      <!-- This is not the first page in a wizard, so always set the content changed to be true -->
      <script language="JavaScript">setContentChanged(true);</script>
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
			<% String pageName = "OptOutChoice.jsp"; %>
        	<%@ include file="include/Nav.jspf" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center"> 
              <% String header = DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_TITLE_OPT_OUT);%>
              <%@ include file="include/InfoBar.jspf" %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <table width="80%" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td align="center" class="MainText">Please select the hardware 
                    you want to <cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_TEXT_OPT_OUT_VERB %>"/>.</td>
                </tr>
              </table>
              <form name="form1" method="post" action="<%= request.getContextPath() %>/servlet/SOAPClient" onsubmit="return prepareSubmit(this);">
			    <input type="hidden" name="action" value="OptOutProgram">
				<input type="hidden" name="StartDate" value="<%= request.getParameter("StartDate") %>">
				<input type="hidden" name="Duration" value="<%= request.getParameter("Duration") %>">
			    <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/user/ConsumerStat/stat/General.jsp">
			    <input type="hidden" name="REFERRER" value="<%= request.getContextPath() %>/user/ConsumerStat/stat/OptOut.jsp">
			    <input type="hidden" name="<%= ServletUtils.CONFIRM_ON_MESSAGE_PAGE %>">
<% if (exitQuestions != null && exitQuestions.getStarsExitInterviewQuestionCount() > 0) { %>
			    <input type="hidden" name="<%= ServletUtils.NEED_MORE_INFORMATION %>">
			    <input type="hidden" name="REDIRECT2" value="<%= request.getContextPath() %>/user/ConsumerStat/stat/OptForm.jsp">
<% } %>
                <table width="70%" border="1" cellspacing="0" cellpadding="3">
                  <tr align="center">
                    <td class="HeaderCell" width="5%">&nbsp;</td>
                    <td class="HeaderCell" width="35%">Hardware</td>
                    <td class="HeaderCell" width="60%">Program(s) Assigned</td>
                  </tr>
                  <%
	for (int i = 0; i < optOutChoices.getStarsInventoryCount(); i++) {
		StarsInventory inventory = optOutChoices.getStarsInventory(i);
		if (inventory.getLMHardware() == null) continue;
		
		ArrayList assignedProgs = new ArrayList();
		for (int j = 0; j < appliances.getStarsApplianceCount(); j++) {
			StarsAppliance app = appliances.getStarsAppliance(j);
			if (app.getInventoryID() == inventory.getInventoryID() && app.getProgramID() > 0) {
				String[] prog = new String[2];
				prog[0] = ServletUtils.getProgramDisplayNames(ServletUtils.getEnrollmentProgram(categories, app.getProgramID()))[0];
				prog[1] = (app.getLoadNumber() > 0)? String.valueOf(app.getLoadNumber()) : "(N/A)";
				assignedProgs.add(prog);
			}
		}
		
		String label = inventory.getDeviceLabel();
		if (label.equals("")) label = inventory.getLMHardware().getManufacturerSerialNumber();
%>
                  <tr>
                    <td width="5%" class="TableCell">
                      <input type="checkbox" name="InvID" value="<%= inventory.getInventoryID() %>" checked>
                    </td>
                    <td width="35%" class="TableCell"><%= label %></td>
                    <td width="60%" class="TableCell"> 
                      <table width="100%" border="0" cellspacing="0" cellpadding="1" class="TableCell">
                        <%
		for (int j = 0; j < assignedProgs.size(); j++) {
			String[] prog = (String[]) assignedProgs.get(j);
%>
                        <tr> 
                          <td width="65%"><%= prog[0] %></td>
                          <td width="35%">Relay <%= prog[1] %></td>
                        </tr>
                        <%
		}
%>
                      </table>
                    </td>
                  </tr>
                  <%
	}
%>
                </table>
                <br>
                <table width="50%" border="0" cellspacing="0" cellpadding="5" align="center">
                  <tr> 
                    <td width="50%" align="right"> 
                      <input type="submit" name="Submit" value="Submit">
                    </td>
                    <td width="50%" align="left"> 
					  <input type="button" name="Cancel" value="Cancel" onclick="history.back()">
                    </td>
                  </tr>
                </table>
                </form>
              <p>&nbsp;</p>
            </div>
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
