<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%
	String referer = request.getParameter("referer");
	if (referer == null) {
		response.sendRedirect("ConfigEnergyCompany.jsp"); return;
	}
	
	if (referer.equalsIgnoreCase("ServiceCompany.jsp")) {
		int compIdx = Integer.parseInt( request.getParameter("CompanyID") );
		referer += "?CompanyID=" + compIdx;
	}
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
            <div align="center"> <br>
              <span class="TitleHeader">ADMINISTRATION - CONTRACTOR ZIP CODES</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin">
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td class="HeaderCell">Assign New Codes</td>
                </tr>
                <tr> 
                  <td height="67"> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="5">
					  <input type="hidden" name="action" value="AddContractorCodes">
					  <input type="hidden" name="REFERER" value="<%= referer %>">
					  <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Admin/<%= referer %>">
					  <tr> 
                        <td width="25%" align="right" class="TableCell">New Code 1:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="NewCode1" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">New Code 2:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="NewCode2" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">New Code 3:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="NewCode3" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">New Code 4:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="NewCode4" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell"> New Code 5:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="NewCode5" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
              <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr>
                  <td width="290" align="right"> 
                    <input type="submit" name="Submit" value="Submit">
                  </td>
                  <td width="205"> 
                    <input type="reset" name="Reset" value="Reset" onclick="setContentChanged(false)">
                  </td>
                  <td width="75" align="right"> 
                    <input type="button" name="Back" value="Back" onclick="if (warnUnsavedChanges()) location.href='<%= referer %>'">
                  </td>
                </tr>
              </table>
            </form>
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
