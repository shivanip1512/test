<%@ include file="include/StarsHeader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../WebConfig/CannonStyle.css" type="text/css">
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
                <td colspan="4" height="74" background="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
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
		  <% String pageName = "Util.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
          
		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
              <br>
              
            <div align="center">
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_TITLE_UTILITY, "QUESTIONS - UTILITY"); %>
              <%@ include file="include/InfoBar.jsp" %>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr> 
                  <td> 
                    <hr>
                  </td>
                </tr>
              </table>
              <br>
              <br>
              <table width="80%" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td align="center" class="MainText"> 
                    <%
	String desc = AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_DESC_UTILITY, "");
	String address = ServletUtils.getFormattedAddress(energyCompany.getCompanyAddress()) + "<br>";
	String phoneNo = (energyCompany.getMainPhoneNumber().trim().length() > 0)?
			"Ph: " + energyCompany.getMainPhoneNumber() + "<br>" : "";
	String faxNo = (energyCompany.getMainFaxNumber().trim().length() > 0)?
			"Fax: " + energyCompany.getMainFaxNumber() + "<br>" : "";
	String email = AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_LINK_UTIL_EMAIL);
	if (ServerUtils.forceNotNone(email).length() > 0)
		email = "<a href='" + email + "' class='Link1' target='new'>Send us an email</a><br>";
	else
		email = (energyCompany.getEmail().trim().length() > 0)?
				"<a href='mailto:" + energyCompany.getEmail() + "' class='Link1'>Email: " + energyCompany.getEmail() + "</a><br>" : "";
	
	desc = desc.replaceAll(ServletUtils.UTIL_COMPANY_ADDRESS, address);
	desc = desc.replaceAll(ServletUtils.UTIL_PHONE_NUMBER, phoneNo);
	desc = desc.replaceAll(ServletUtils.UTIL_FAX_NUMBER, faxNo);
	desc = desc.replaceAll(ServletUtils.UTIL_EMAIL, email);
%>
                    <%= energyCompany.getCompanyName() %><br>
                    <%= desc %></td>
                </tr>
              </table>
              <br>
            </div>
            <p>&nbsp;</p>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</body>
</html>
