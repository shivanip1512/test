<%@ include file="include/StarsHeader.jsp" %>
<%
	String catNoStr = request.getParameter("Cat");
	String progNoStr = request.getParameter("Prog");
	
	int catNo = 0;
	if (catNoStr != null) catNo = Integer.parseInt(catNoStr);
	int progNo = 0;
	if (progNoStr != null) progNo = Integer.parseInt(progNoStr);
	
	StarsApplianceCategory category = categories.getStarsApplianceCategory(catNo);
	StarsEnrLMProgram program = category.getStarsEnrLMProgram(progNo);
%>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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
          <td width="1" bgcolor="#000000"><img src="../../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
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
%>
              <table width="60%" border="0"  cellspacing="0" cellpadding="5" class="TableCell">
<%
		String categoryName = category.getStarsWebConfig().getAlternateDisplayName();
		String categoryDesc = category.getStarsWebConfig().getDescription();
		if (category.getStarsEnrLMProgramCount() == 1) {	// Use the program display name and description instead
			categoryName = ServletUtils.getProgramDisplayNames(category.getStarsEnrLMProgram(0))[0];
			categoryDesc = category.getStarsEnrLMProgram(0).getStarsWebConfig().getDescription();
		}
%>
                <tr> 
                  <td class="TitleHeader"><%= categoryName %></td>
                </tr>
                <tr> 
                  <td><%= categoryDesc %></td>
                </tr>
<%
		for (int i = 0; i < category.getStarsEnrLMProgramCount(); i++) {
			StarsEnrLMProgram enrProg = category.getStarsEnrLMProgram(i);
%>
                <tr> 
                  <td>
                    <span class="TitleHeader"><%= ServletUtils.getProgramDisplayNames(enrProg)[1] %></span> -
                    <%= enrProg.getStarsWebConfig().getDescription() %>
                  </td>
                </tr>
<%
		}
%>
              </table>
<%
	}
%>
              <br>
              <input type="button" name="Back" value="Back" onclick="history.back()">
              <p></p>
            </div>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<div align="center"></div>
</body>
</html>
