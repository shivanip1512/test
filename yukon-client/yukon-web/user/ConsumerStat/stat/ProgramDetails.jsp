<%@ include file="include/StarsHeader.jsp" %>
<%
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
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
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
            <% String pageName = "Enrollment.jsp"; %>
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
		    <td width="657" valign="top" bgcolor="#FFFFFF">
              
            <div align="center"><br>
              <% String header = "PROGRAM - DETAILS"; %>
              <%@ include file="include/InfoBar.jsp" %>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr> 
                  <td> 
                    <hr>
                  </td>
                </tr>
              </table>
            </div>
            <div align="center">
              <table width="280" border="0" align="center">
                <tr> 
                  <td class="TitleHeader" align="center"> <%= ServletUtils.getProgramDisplayNames(program)[0] %> 
                  </td>
                </tr>
              </table>
              <br>
<%
	if (program.getStarsWebConfig().getURL().length() > 0) {
%>
              <table width="600" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td class="TableCell"><jsp:include page='<%= "../../../WebConfig/" + program.getStarsWebConfig().getURL() %>'/></td>
                </tr>
              </table>
<%
	} else {
		String[] progIcons = ServletUtils.getImageNames(program.getStarsWebConfig().getLogoLocation());
		if (progIcons[0].length() > 0 || progIcons[1].length() > 0 || progIcons[2].length() > 0) {
%>
              <table width="80%" border="1" class = "TableCell" align = "center" height="28" cellspacing = "0" cellpadding = "4">
                <tr valign="middle"> 
                  <td width="27%">Savings: More dollar signs means more savings!</td>
                  <td width="8%">
				    <% if (progIcons[0].length() > 0) { %>
				    <img src="../../../Images/Icons/<%= progIcons[0] %>" >
				    <% } else { %>N/A<% } %>
				  </td>
                  <td width="19%">Percent of Control</td>
                  <td width="8%">
				    <% if (progIcons[1].length() > 0) { %>
				    <img src="../../../Images/Icons/<%= progIcons[1] %>" >
				    <% } else { %>N/A<% } %>
				  </td>
                  <td width="30%">Environment: More trees means healthier environment.</td>
                  <td width="8%">
				    <% if (progIcons[2].length() > 0) { %>
				    <img src="../../../Images/Icons/<%= progIcons[2] %>" >
				    <% } else { %>N/A<% } %>
				  </td>
                </tr>
              </table>
              <br>
<%
		}
%>
              <table width="60%" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td class="TableCell" align="center"><%= program.getStarsWebConfig().getDescription() %></td>
                </tr>
              </table>
<%
	}
%>
              <br>
              <input type="button" name="Back" value="Back" onclick="history.back()">
              <p></p>
            </div>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<div align="center"></div>
</body>
</html>
