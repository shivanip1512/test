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
<script language="JavaScript">
function generateConfigFiles(form) {
	form.action.value = "GenerateConfigFiles";
	form.REDIRECT.value = form.REFERRER.value;
	form.submit();
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
            <div align="center"> 
              <% String header = "IMPORT DSM DATABASE"; %>
              <%@ include file="include/InfoSearchBar2.jsp" %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg.replaceAll(System.getProperty("line.separator"), "<br>") + "</span><br>"); %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg.replaceAll(System.getProperty("line.separator"), "<br>") + "</span><br>"); %>
              <form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/ImportManager">
                <input type="hidden" name="action" value="ImportDSM">
                <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/ImportDSM.jsp?Done">
                <input type="hidden" name="REFERRER" value="<%= request.getContextPath() %>/operator/Consumer/ImportDSM.jsp">
                <table width="500" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td class="MainText"> 
                      <ol>
                        <li>Enter the directory for the DSM data files.</li>
                        <li>Click the &quot;Generate Config Files&quot; button 
                          to generate configuration files &quot;_route.map&quot;, 
                          &quot;_receivertype.map&quot;, &quot;_controltype.map&quot;, 
                          and &quot;_loadtype.map&quot;. Follow the instructions 
                          in these files to complete them.</li>
                        <li>Click the &quot;Submit&quot; button to start the conversion 
                          program.</li>
                      </ol>
                    </td>
                  </tr>
                </table>
                <br>
                <table width="400" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                  <tr> 
                    <td width="150"> 
                      <div align="right">DSM Data Directory: </div>
                    </td>
                    <td width="250"> 
                      <input type="text" name="ImportDir" size="35" value="<%= StarsUtils.forceNotNull(savedReq.getProperty("ImportDir")) %>">
                    </td>
                  </tr>
                </table>
                <br>
                <table width="400" border="0" cellspacing="0" cellpadding="5">
                  <tr> 
                    <td align="center"> 
                      <input type="button" name="Submit" value="Generate Config Files" onClick="generateConfigFiles(this.form)">
                      <input type="submit" name="Submit2" value="Submit">
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
