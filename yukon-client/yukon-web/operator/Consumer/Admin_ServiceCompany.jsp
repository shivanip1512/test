<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.servlet.StarsAdmin" %>
<%
	StarsServiceCompany company = null;
	int compIdx = Integer.parseInt( request.getParameter("Company") );
	if (compIdx < companies.getStarsServiceCompanyCount())
		company = companies.getStarsServiceCompany(compIdx);
	else {
		company = (StarsServiceCompany) session.getAttribute(StarsAdmin.SERVICE_COMPANY_TEMP);
		if (company == null) {
			company = new StarsServiceCompany();
			company.setCompanyID(-1);
			company.setCompanyName("");
			company.setMainPhoneNumber("");
			company.setMainFaxNumber("");
			company.setCompanyAddress( (CompanyAddress)StarsFactory.newStarsCustomerAddress(CompanyAddress.class) );
			company.setPrimaryContact( (PrimaryContact)StarsFactory.newStarsCustomerContact(PrimaryContact.class) );
			session.setAttribute(StarsAdmin.SERVICE_COMPANY_TEMP, company);
		}
	}
	
	String action = request.getParameter("action");
	if (action == null) action = "";
	
	if (action.equalsIgnoreCase("init")) {
		session.removeAttribute(StarsAdmin.SERVICE_COMPANY_TEMP);
	}
	else if (action.equalsIgnoreCase("EditAddress")) {
		StarsServiceCompany scTemp = (StarsServiceCompany) session.getAttribute(StarsAdmin.SERVICE_COMPANY_TEMP);
		if (scTemp == null) {
			scTemp = new StarsServiceCompany();
			if (company.getCompanyAddress().getAddressID() == 0)
				scTemp.setCompanyAddress( (CompanyAddress)StarsFactory.newStarsCustomerAddress(CompanyAddress.class) );
			else
				scTemp.setCompanyAddress( company.getCompanyAddress() );
			if (company.getPrimaryContact().getContactID() == 0)
				scTemp.setPrimaryContact( (PrimaryContact)StarsFactory.newStarsCustomerContact(PrimaryContact.class) );
			else
				scTemp.setPrimaryContact( (PrimaryContact)StarsFactory.newStarsCustomerContact(company.getPrimaryContact(), PrimaryContact.class) );
			session.setAttribute(StarsAdmin.SERVICE_COMPANY_TEMP, scTemp);
		}
		scTemp.setCompanyName( request.getParameter("CompanyName") );
		scTemp.setMainPhoneNumber( request.getParameter("PhoneNo") );
		scTemp.setMainFaxNumber( request.getParameter("FaxNo") );
		scTemp.getPrimaryContact().setLastName( request.getParameter("ContactLastName") );
		scTemp.getPrimaryContact().setFirstName( request.getParameter("ContactFirstName") );
		
		response.sendRedirect("Admin_Address.jsp?referer=Admin_ServiceCompany.jsp&Company=" + compIdx);
		return;
	}
	
	StarsServiceCompany sc = (StarsServiceCompany) session.getAttribute(StarsAdmin.SERVICE_COMPANY_TEMP);
	if (sc == null) sc = company;
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
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
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
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
              
            <div align="center">
              <% String header = "ADMINISTRATION - SERVICE COMPANY"; %>
              <%@ include file="include/InfoSearchBar2.jsp" %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin">
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td class="HeaderCell">Edit Service Company Information</td>
                </tr>
                <tr> 
                  <td height="67"> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="5">
                      <input type="hidden" name="action" value="UpdateServiceCompany">
					  <input type="hidden" name="CompanyID" value="<%= company.getCompanyID() %>">
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Company 
                          Name:</td>
                        <td width="75%" class="TableCell"> 
                          <input type="text" name="CompanyName" value="<%= sc.getCompanyName() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Main Phone 
                          #:</td>
                        <td width="75%" class="TableCell"> 
                          <input type="text" name="PhoneNo" value="<%= sc.getMainPhoneNumber() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Main Fax 
                          #:</td>
                        <td width="75%" class="TableCell"> 
                          <input type="text" name="FaxNo" value="<%= sc.getMainFaxNumber() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Primary 
                          Contact:</td>
                        <td width="75%" class="TableCell"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                            <tr> 
                              <td>Last Name: 
                                <input type="text" name="ContactLastName" value="<%= sc.getPrimaryContact().getLastName() %>" size=14>
                              </td>
                              <td>First Name: 
                                <input type="text" name="ContactFirstName" value="<%= sc.getPrimaryContact().getFirstName() %>" size=14>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell"> Company 
                          Address:</td>
                        <td width="75%" class="TableCell"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                            <tr> 
                              <td width="75%"><%= ServletUtils.getOneLineAddress(sc.getCompanyAddress()) %></td>
                              <td width="25%"> 
                                <input type="button" name="EditAddress" value="Edit" onClick="editAddress(this.form)">
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell"> Company 
                          Type:</td>
                        <td width="75%" class="TableCell"> 
                          <select name="Type">
                            <option>Electric</option>
                          </select>
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
