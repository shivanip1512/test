<%@ include file="include/StarsHeader.jsp" %>
<%
	boolean inWizard = request.getParameter("Wizard") != null;
	if (!inWizard && accountInfo == null) {
		response.sendRedirect("../Operations.jsp");
		return;
	}
	
	String catNoStr = request.getParameter("Cat");
	String progNoStr = request.getParameter("Prog");
	
	int catNo = 0;
	if (catNoStr != null) catNo = Integer.parseInt(catNoStr);
	int progNo = 0;
	if (progNoStr != null) progNo = Integer.parseInt(progNoStr);
	
	StarsEnrLMProgram program = categories.getStarsApplianceCategory(catNo).getStarsEnrLMProgram(progNo);
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0">
<div id = "tool" class = "tooltip" style="width: 1003px; height: 20px"></div>
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
<% if (!inWizard) { %>
		  <% String pageName = "Programs.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
<% } %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center"> 
              <% String header = "PROGRAMS - DETAILS"; %>
<% if (!inWizard) { %>
              <%@ include file="include/InfoSearchBar.jsp" %>
<% } else { %>
              <%@ include file="include/InfoSearchBar2.jsp" %>
<% } %>
<% if (program.getStarsWebConfig().getURL().length() > 0) { %>
              <table width="600" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td class="TableCell"><jsp:include page='<%= "../../WebConfig/" + program.getStarsWebConfig().getURL() %>'/></td>
                </tr>
              </table>
<% } else { %>
              <table width="280" border="0" align="center">
                <tr> 
                  <td class="TitleHeader" align="center"> <%= ServletUtils.getProgramDisplayNames(program)[0] %> 
                  </td>
                </tr>
              </table>
              <br>
              <table width="60%" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td class="TableCell" align="center"><%= program.getStarsWebConfig().getDescription() %></td>
                </tr>
              </table>
<% } %>
              <br>
              <input type="button" name="Back" value="Back" onclick="history.back()">
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
