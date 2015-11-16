<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
<%@ include file="include/StarsHeader.jsp" %>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty property="STYLE_SHEET" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

</head>

<body class="Background" leftmargin="0" topmargin="0">
<div id = "tool" class = "tooltip" style="width: 1003px; height: 20px"></div>
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
          <td  valign="top" width="101">&nbsp; </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "PROGRAMS - ODDS FOR CONTROL"; %>
              <%@ include file="include/InfoSearchBar2.jspf" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
             
              <div align="center"><span class="MainText">Check the appropriate Odds for Control for each program.</span><br>
              </div>
			  <form name="form1" method="post" action="<%= request.getContextPath() %>/servlet/SOAPClient">
                <cti:csrfToken/>
			  	<input type="hidden" name="action" value="SendControlOdds">
                <table border="1" cellspacing="0" cellpadding="3" width="550">
                  <tr> 
                    <td width="390" class="HeaderCell" align="center" colspan="2">Program 
                      Enrollment </td>
                    <td width="148" class="HeaderCell" align="center">Odds for Control</td>
                  </tr>
                  <%
	StarsCustSelectionList oddsList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL );
	
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
		ArrayList progList = new ArrayList();	// List of programs that're eligible for notification
		for (int j = 0; j < category.getStarsEnrLMProgramCount(); j++) {
			StarsEnrLMProgram program = category.getStarsEnrLMProgram(j);
			if (program.getChanceOfControl() != null)
				progList.add( program );
		}
		if (progList.size() == 0) continue;
%>
                  <tr>
                    <td class="TableCell" align="center" style="border-right:none" colspan="3">
                      <table width="100%" border="0" cellspacing="0" cellpadding="0">
<%
		for (int j = 0; j < progList.size(); j++) {
			StarsEnrLMProgram program = (StarsEnrLMProgram) progList.get(j);
%>
                  <tr>
<%	        if (j == 0) { %>
                    <td width="64" rowspan="<%= progList.size() %>"><img src="../../WebConfig/<%= category.getStarsWebConfig().getLogoLocation() %>"></td>
<%	        } %>
                    <td width="320" class="TableCell" align="left" style="border-right:none">
                    <input type="hidden" name="ProgID" value="<%= program.getProgramID() %>">
                    <%= ServletUtils.getProgramDisplayNames(program)[0] %>
                    </td>
                    <td width="148" class="TableCell" align="left" style="border-left:none">
                            <select name="ControlOdds">
                              <%
			for (int k = 0; k < oddsList.getStarsSelectionListEntryCount(); k++) {
				StarsSelectionListEntry entry = oddsList.getStarsSelectionListEntry(k);
				String selectedStr = (entry.getEntryID() == program.getChanceOfControl().getEntryID()) ? "selected" : "";
%>
                              <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
                              <%			} %>
                            </select>
                    </td>
                  </tr>
<%		} %>
                      </table>
                    </td>
                  </tr>

                  <%	} %>
                </table>
                <p>
                <table width="100%" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td align="center"> 
                      <input type="submit" name="Submit" value="Send">
                    </td>
                  </tr>
                </table>
              </form>
			  
                
              <p align="center" class="Subtext"><br>
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
