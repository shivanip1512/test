<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.servlet.StarsAdmin" %>
<%
	String action = request.getParameter("action");
	if (action == null) action = "";
	
	if (action.equalsIgnoreCase("init")) {
		session.removeAttribute(StarsAdmin.ENERGY_COMPANY_TEMP);
	}
	else if (action.equalsIgnoreCase("EditAddress")) {
		StarsEnergyCompany ecTemp = (StarsEnergyCompany) session.getAttribute(StarsAdmin.ENERGY_COMPANY_TEMP);
		if (ecTemp == null) {
			ecTemp = new StarsEnergyCompany();
			if (energyCompany.getCompanyAddress().getAddressID() == 0)
				ecTemp.setCompanyAddress( (CompanyAddress)StarsFactory.newStarsCustomerAddress(CompanyAddress.class) );
			else
				ecTemp.setCompanyAddress( energyCompany.getCompanyAddress() );
			session.setAttribute(StarsAdmin.ENERGY_COMPANY_TEMP, ecTemp);
		}
		ecTemp.setCompanyName( request.getParameter("CompanyName") );
		ecTemp.setMainPhoneNumber( request.getParameter("PhoneNo") );
		ecTemp.setMainFaxNumber( request.getParameter("FaxNo") );
		ecTemp.setEmail( request.getParameter("Email") + "," + request.getParameter("CustomizedEmail") );
		ecTemp.setTimeZone( request.getParameter("TimeZone") );
		
		response.sendRedirect("Address.jsp?referer=EnergyCompany.jsp");
		return;
	}
	
	String email = "";
	String checkedStr = "";
	StarsEnergyCompany ec = (StarsEnergyCompany) session.getAttribute(StarsAdmin.ENERGY_COMPANY_TEMP);
	if (ec != null) {
		StringTokenizer st = new StringTokenizer(ec.getEmail(), ",");
		email = st.nextToken();
		if (Boolean.valueOf(st.nextToken()).booleanValue())
			checkedStr = "checked";
	}
	else {
		ec = energyCompany;
		com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany liteEC = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		String propVal = AuthFuncs.getRolePropValueGroup(liteEC.getResidentialCustomerGroup(),
				com.cannontech.roles.consumer.ResidentialCustomerRole.WEB_LINK_UTIL_EMAIL, "(none)");
		if (ServerUtils.forceNotNone(propVal).length() > 0) {
			email = propVal;
			checkedStr = "checked";
		}
		else
			email = ec.getEmail();
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">
function editAddress(form) {
	form.attributes["action"].value = "";
	form.action.value = "EditAddress";
	form.submit();
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../WebConfig/yukon/AdminImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Administration</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
            <div align="center"> <br>
              <span class="TitleHeader">ADMINISTRATION - ENERGY COMPANY</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin">
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td class="HeaderCell">Edit Energy Company Information</td>
                </tr>
                <tr> 
                  <td height="67"> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="5">
					  <input type="hidden" name="action" value="UpdateEnergyCompany">
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Company 
                          Name:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="CompanyName" value="<%= ec.getCompanyName() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Main Phone 
                          #:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="PhoneNo" value="<%= ec.getMainPhoneNumber() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Main Fax 
                          #:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="FaxNo" value="<%= ec.getMainFaxNumber() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Email:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="Email" value="<%= email %>">
                          <input type="checkbox" name="CustomizedEmail" value="true" <%= checkedStr %>>
                          Use a link to your company's website</td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell"> Company 
                          Address:</td>
                        <td width="75%" class="TableCell">
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                            <tr>
                              <td width="75%"><%= ServletUtils.getOneLineAddress(ec.getCompanyAddress()) %></td>
                              <td width="25%">
                                <input type="button" name="EditAddress" value="Edit" onclick="editAddress(this.form)">
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Time Zone:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="TimeZone" value="<%= ec.getTimeZone() %>">
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
                    <input type="reset" name="Cancel" value="Cancel">
                  </td>
                  <td width="75" align="right"> 
                    <input type="button" name="Done" value="Done" onclick="location.href='AdminTest.jsp'">
                  </td>
                </tr>
              </table>
            </form>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
