<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%
	String referer = request.getParameter("referer");
    referer = ServletUtil.createSafeRedirectUrl(request, referer);

	if (referer == null) {
		response.sendRedirect("ConfigEnergyCompany.jsp"); return;
	}
	
	StarsCustomerAddress address = null;
	if (referer.equalsIgnoreCase("EnergyCompany.jsp")) {
		StarsEnergyCompany ecTemp = (StarsEnergyCompany) session.getAttribute(StarsAdminUtil.ENERGY_COMPANY_TEMP);
		address = ecTemp.getCompanyAddress();
	}
	if (address.getCounty() == null) address.setCounty("");
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
              <span class="TitleHeader">ADMINISTRATION - ADDRESS</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin">
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td class="HeaderCell">Edit Address Information</td>
                </tr>
                <tr> 
                  <td height="67"> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="5">
					  <input type="hidden" name="action" value="UpdateAddress">
					  <input type="hidden" name="REFERER" value="<%= referer %>">
					  <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Admin/<%= referer %>">
					  <input type="hidden" name="AddressID" value="<%= address.getAddressID() %>">
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Street 
                          Address 1:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="StreetAddr1" value="<%= StringEscapeUtils.escapeHtml(address.getStreetAddr1()) %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Street 
                          Address 2:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="StreetAddr2" value="<%= StringEscapeUtils.escapeHtml(address.getStreetAddr2()) %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">City:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="City" value="<%= StringEscapeUtils.escapeHtml(address.getCity()) %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">State:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="State" value="<%= address.getState() %>" maxlength="2" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell"> Zip Code:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="Zip" value="<%= address.getZip() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell" height="2">County:</td>
                        <td width="75%" class="TableCell" height="2"> 
                          <input type="text" name="County" value="<%= StringEscapeUtils.escapeHtml(address.getCounty()) %>" onchange="setContentChanged(true)">
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
