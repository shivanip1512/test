<%@ include file="include/StarsHeader.jsp" %>
<%
	Properties savedReq = null;
	if (request.getParameter("Done") != null)
		session.removeAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST);
	else
		savedReq = (Properties) session.getAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST);
	if (savedReq == null) savedReq = new Properties();
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
            <div align="center"> 
              <% String header = "IMPORT OLD STARS"; %>
              <%@ include file="include/InfoSearchBar2.jsp" %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg.replaceAll(System.getProperty("line.separator"), "<br>") + "</span><br>"); %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg.replaceAll(System.getProperty("line.separator"), "<br>") + "</span><br>"); %>
              <form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/ImportManager">
                <input type="hidden" name="action" value="ImportINIData">
                <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/ImportSTARS.jsp">
                <table width="600" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td class="MainText" align="center">Import the INI files first. 
                      S3DATA.INI only needs to be imported once.<br>
                      <span class="ErrorMsg">STARS3.INI must be re-imported every 
                      time you log into the application to resume the process.</span></td>
                  </tr>
                </table>
                <br>
                <table width="400" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                  <tr> 
                    <td width="100"> 
                      <div align="right">S3DATA.INI: </div>
                    </td>
                    <td width="300"> 
                      <input type="file" name="S3DATA_INI" size="35">
                    </td>
                  </tr>
                  <tr> 
                    <td width="100"> 
                      <div align="right">STARS3.INI: </div>
                    </td>
                    <td width="300"> 
                      <input type="file" name="STARS3_INI" size="35">
                    </td>
                  </tr>
                </table>
                <table width="400" border="0" cellspacing="0" cellpadding="5">
                  <tr> 
                    <td align="center">
                      <input type="submit" name="Submit2" value="Submit">
                    </td>
                  </tr>
                </table>
			  </form>
			  <form name="form2" method="post" action="<%=request.getContextPath()%>/servlet/ImportManager">
                <input type="hidden" name="action" value="PreprocessStarsData">
                <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/ImportSTARS2.jsp">
                <table width="600" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td class="MainText" align="center">Enter the directory for 
                      the old STARS data files, and click &quot;Next&quot;.</td>
                  </tr>
                </table>
                <br>
                <table width="400" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                  <tr> 
                    <td width="100"> 
                      <div align="right">Import Directory: </div>
                    </td>
                    <td width="300"> 
                      <input type="text" name="ImportDir" size="35" value="<%= StarsUtils.forceNotNull(savedReq.getProperty("ImportDir")) %>">
                    </td>
                  </tr>
                </table>
                <table width="400" border="0" cellspacing="0" cellpadding="5">
                  <tr> 
                    <td align="center"> 
                      <input type="submit" name="Submit" value="Next">
                    </td>
                  </tr>
                </table>
			  </form>
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
