<%@ include file="StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	StarsLMHardwareHistory hwHist = null;
	if (inventories.getStarsLMHardwareCount() > 0) {
		StarsLMHardware hw = inventories.getStarsLMHardware(0);
		hwHist = hw.getStarsLMHardwareHistory();
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
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
		  <% String pageName = "OptOut.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_TITLE_OPT_OUT, "PROGRAMS - OPT OUT"); %>
              <%@ include file="InfoSearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <table width="550" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td><div align="center">
                <p class="Main"><cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_TEXT_OPT_OUT_DESC %>"/></p>
              </div>
              </td>
                </tr>
              </table>
			<form name="form1" method="post" action="/servlet/SOAPClient">
			  <input type="hidden" name="action" value="OptOutProgram">
			  <input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/operator/Consumer/Programs.jsp">
			  <input type="hidden" name="REDIRECT2" value="<%=request.getContextPath()%>/operator/Consumer/OptForm.jsp">
			  <input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/operator/Consumer/OptOut.jsp">
              <table width="200" border="1" cellspacing="0" cellpadding="3" bgcolor="#CCCCCC" align="center">
                <tr> 
                  <td> 
                    <div align="center"> 
                      <p class="HeaderCell">Temporarily <cti:getProperty propertyid="<%= ConsumerInfoRole.WEB_TEXT_OPT_OUT_VERB %>"/> 
					  all programs</p>
                    </div>
                    <table width="180" border="0" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
                        <td width="180" align="center"> 
                          <select name="OptOutPeriod">
<%
	StarsCustSelectionList periodList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD );
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
              <p align="center" class="MainHeader"><br>
              <table width="150" border="0" cellspacing="0" cellpadding="3" align="center">
                <tr> 
                  <td align="center">
                    <input type="submit" value="Re-enable" onclick="this.form.action.value='ReenableProgram'"
					 <% if (programs.getStarsLMProgramCount() == 0) out.print("disabled"); %>>
                  </td>
                </tr>
              </table>
			</form>
              <br>
              <form name="form2" method="post" action="OptHist.jsp">
                <p align="center" class="MainHeader"><b>Program History </b> 
                <table width="366" border="1" cellspacing="0" align="center" cellpadding="3">
                  <tr> 
                    <td class="HeaderCell" width="100">Date</td>
                    <td class="HeaderCell" width="154">Type - Duration</td>
                    <td class="HeaderCell" width="100">Program</td>
                  </tr>
                  <%
	ServletUtils.ProgramHistory[] progHist = ServletUtils.getProgramHistory( account.getAccountID(), programs );
	for (int i = progHist.length - 1; i >= 0 && i >= progHist.length - 5; i--) {
%>
                  <tr> 
                    <td class="TableCell" width="100" ><%= datePart.format(progHist[i].getDate()) %></td>
                    <td class="TableCell" width="154" ><%= progHist[i].getAction() %> 
                      <% if (progHist[i].getDuration() != null) { %>
                      - <%= progHist[i].getDuration() %>
                      <% } %>
                    </td>
                    <td class="TableCell" width="100" > 
                      <%
		String[] progNames = progHist[i].getPrograms();
		for (int j = 0; j < progNames.length; j++) {
%>
                      <%= progNames[j] %><br>
                      <%
		}
%>
                    </td>
                  </tr>
                  <%
	}
%>
                </table>
<%
	if (progHist.length > 5) {
%>
                <table width="300" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td> 
                      <div align="right">
                        <input type="button" name="More" value="More" onclick="location='OptHist.jsp'">
                      </div>
                    </td>
                  </tr>
                </table>
<%
	}
%>
              </form>
              <br>
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
