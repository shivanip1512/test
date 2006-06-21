<%@ include file="include/StarsHeader.jsp" %>
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
          <td  valign="top" width="101"> 
		  <% String pageName = "Util.jsp"; %>
          <%@ include file="include/Nav.jspf" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          
		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
              <br>
              
            <div align="center">
              <% String header = DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_TITLE_UTILITY); %>
              <%@ include file="include/InfoBar.jspf" %>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr> 
                  <td> 
                    <hr>
                  </td>
                </tr>
              </table>
              <br>
              <table width="80%" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td align="center" class="MainText"> 
                    <%
	String desc = DaoFactory.getAuthDao().getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_DESC_UTILITY);
	String address = ServletUtils.formatAddress(energyCompany.getCompanyAddress()) + "<br>";
	String phoneNo = (energyCompany.getMainPhoneNumber().trim().length() > 0)?
			"Ph: " + energyCompany.getMainPhoneNumber() + "<br>" : "";
	String faxNo = (energyCompany.getMainFaxNumber().trim().length() > 0)?
			"Fax: " + energyCompany.getMainFaxNumber() + "<br>" : "";
	String email = (energyCompany.getEmail().trim().length() > 0)?
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
          <td width="1" bgcolor="#000000"><img src="../../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</body>
</html>
