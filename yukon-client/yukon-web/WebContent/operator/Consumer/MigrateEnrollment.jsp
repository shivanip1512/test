<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
<%@ include file="include/StarsHeader.jsp" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<cti:checkRolesAndProperties value="ENABLE_MIGRATE_ENROLLMENT" />

<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
            <div align="center"> 
              <% String header = "MIGRATE ENROLLMENT DATA (for current energy company ONLY)"; %>
              <%@ include file="include/InfoSearchBar2.jspf" %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg.replaceAll(System.getProperty("line.separator"), "<br>") + "</span><br>"); %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg.replaceAll(System.getProperty("line.separator"), "<br>") + "</span><br>"); %>
			  <form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/ImportManager" enctype="multipart/form-data">
                <input type="hidden" name="action" value="MigrateEnrollment">
                <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/MigrateEnrollment.jsp">
                <br>
                <br>
                <table width="400" border="0" cellspacing="0" cellpadding="5">
                  <tr> 
                    <td align="center"> 
                      <span>(restart of webserver should be done when finished)</span>  
                      <br>
                      <input type="submit" name="Begin" value="Begin">
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
