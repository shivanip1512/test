<%@ include file="include/StarsHeader.jsp" %>
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
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../../WebConfig/<cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_IMG_CORNER %>"/>">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.HEADER_LOGO %>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height="28">&nbsp;</td>
				  <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle">&nbsp;</td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
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
<%
	String desc = ServerUtils.forceNotNone(AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_DESC_PROGRAM));
	if (desc.length() > 0) {
%>
            <div align="center">
              <table width="600" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td class="TableCell"><%= desc %></td>
                </tr>
              </table>
            </div>
<%	}
	else { %>
            <table width="95%" border="1" class = "TableCell" align = "center" height="28" cellspacing = "0" cellpadding = "4">
              <tr> 
                <td width="17%"><b>The following symbols represent:</b></td>
                <td width="8%"><img src="../../../Images/Icons/$$Sm.gif" ></td>
                <td width="21%">Savings: More dollar signs means more savings!</td>
                <td width="8%"><img src="../../../Images/Icons/ThirdSm.gif"></td>
                <td width="13%" valign="top">Percent of Control</td>
                <td width="8%"><img src="../../../Images/Icons/Tree2Sm.gif"></td>
                <td width="25%" valign="top">Environment: More trees means mean 
                  a healthier environment.</td>
              </tr>
            </table>
            <table width="100%" border="0" cellspacing="20" >
              <%
	int numProgs = 0;
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
		for (int j = 0; j < category.getStarsEnrLMProgramCount(); j++) {
			StarsEnrLMProgram program = category.getStarsEnrLMProgram(j);
			
			String[] imgNames = ServletUtils.getImageNames( program.getStarsWebConfig().getLogoLocation() );
			if (numProgs % 2 == 0) {	// Two programs in a row
%>
              <tr> 
<%
			}
			/* Table of program */
%>
                <td width="50%" valign="top"> 
                  <table width="280" border="0">
                    <tr> 
                      <td class = "TableCell"><b><%= category.getStarsWebConfig().getAlternateDisplayName() %> 
                        - <%= program.getStarsWebConfig().getAlternateDisplayName() %></b></td>
                    </tr>
                    <tr> 
                      <td class = "TableCell" valign = "top"><%= program.getStarsWebConfig().getDescription() %><br>
                        <table width="210" border="0" cellspacing="0" cellpadding="0">
                          <tr> 
                            <td><% if (!imgNames[1].equals("")) { %><img src="../../../Images/Icons/<%= imgNames[1] %>"><% } %></td>
                            <td><% if (!imgNames[2].equals("")) { %><img src="../../../Images/Icons/<%= imgNames[2] %>"><% } %></td>
                            <td><% if (!imgNames[3].equals("")) { %><img src="../../../Images/Icons/<%= imgNames[3] %>"><% } %></td>
                            <td><% if (!imgNames[4].equals("")) { %><img src="../../../Images/Icons/<%= imgNames[4] %>"><% } %></td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </td>
<%
			if (numProgs % 2 == 1) {
%>
              </tr>
<%
			}
			numProgs++;
		}
	}	// Enf of all programs
	
	if (numProgs % 2 == 1) {
		/* If number of programs is odd, fill in the last table cell */
%>
              <td width="50%">&nbsp;</td>
              </tr>
<%
	}
%>
            </table>
<%	} %>
            <div align="center"> 
              <form method="post" action="Enrollment.jsp">
                <input type="submit" name="Back" value="Back">
                <br>
                <br>
              </form>
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
