<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.util.StarsAdminUtil" %>
<%
	StarsServiceCompany company = null;
	int compIdx = Integer.parseInt( request.getParameter("Company") );
	if (compIdx >= 0 && compIdx < companies.getStarsServiceCompanyCount()) {
		company = companies.getStarsServiceCompany(compIdx);
	}
	else {
		company = new StarsServiceCompany();
		company.setCompanyID(-1);
		company.setCompanyName("");
		company.setMainPhoneNumber("");
		company.setMainFaxNumber("");
		company.setCompanyAddress( (CompanyAddress)StarsFactory.newStarsCustomerAddress(CompanyAddress.class) );
		company.setPrimaryContact( (PrimaryContact)StarsFactory.newStarsCustomerContact(PrimaryContact.class) );
	}
	
	String action = request.getParameter("action");
	if (action == null) action = "";
	
	if (action.equalsIgnoreCase("init")) {
		session.removeAttribute(StarsAdminUtil.SERVICE_COMPANY_TEMP);
	}
	else if (action.equalsIgnoreCase("EditAddress")) {
		StarsServiceCompany scTemp = (StarsServiceCompany) session.getAttribute(StarsAdminUtil.SERVICE_COMPANY_TEMP);
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
			session.setAttribute(StarsAdminUtil.SERVICE_COMPANY_TEMP, scTemp);
		}
		
		scTemp.setCompanyName( request.getParameter("CompanyName") );
		scTemp.setMainPhoneNumber( request.getParameter("PhoneNo") );
		scTemp.setMainFaxNumber( request.getParameter("FaxNo") );
		scTemp.getPrimaryContact().setLastName( request.getParameter("ContactLastName") );
		scTemp.getPrimaryContact().setFirstName( request.getParameter("ContactFirstName") );
		
		response.sendRedirect("Address.jsp?referer=ServiceCompany.jsp&Company=" + compIdx);
		return;
	}
	
	StarsServiceCompany sc = (StarsServiceCompany) session.getAttribute(StarsAdminUtil.SERVICE_COMPANY_TEMP);
	if (sc == null) sc = company;
	
	String address = ServletUtils.getOneLineAddress(sc.getCompanyAddress());
	if (address.length() == 0) address = "(none)";
	
	String viewOnly = company.getInherited()? "disabled" : "";
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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
      <%@ include file="include/HeaderBar.jsp" %>
<% if (company.getCompanyID() < 0 && !address.equals("(none)")) %>
      <script language="JavaScript">setContentChanged(true);</script>
<% } %>
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
              <span class="TitleHeader">ADMINISTRATION - SERVICE COMPANY</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin" onsubmit="return <%= !company.getInherited() %>">
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
                          <input type="text" name="CompanyName" value="<%= sc.getCompanyName() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Main Phone 
                          #:</td>
                        <td width="75%" class="TableCell"> 
                          <input type="text" name="PhoneNo" value="<%= sc.getMainPhoneNumber() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Main Fax 
                          #:</td>
                        <td width="75%" class="TableCell"> 
                          <input type="text" name="FaxNo" value="<%= sc.getMainFaxNumber() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Primary 
                          Contact:</td>
                        <td width="75%" class="TableCell"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                            <tr> 
                              <td>Last Name: 
                                <input type="text" name="ContactLastName" value="<%= sc.getPrimaryContact().getLastName() %>" size=14 onchange="setContentChanged(true)">
                              </td>
                              <td>First Name: 
                                <input type="text" name="ContactFirstName" value="<%= sc.getPrimaryContact().getFirstName() %>" size=14 onchange="setContentChanged(true)">
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
                              <td width="75%"><%= address %></td>
                              <td width="25%"> 
                                <input type="button" name="EditAddress" value="Edit" onClick="editAddress(this.form)" <%= viewOnly %>>
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
                    <input type="submit" name="Submit" value="Submit" <%= viewOnly %>>
                  </td>
                  <td width="205"> 
                    <input type="reset" name="Reset" value="Reset" <%= viewOnly %> onclick="setContentChanged(false)">
                  </td>
                  <td width="75" align="right"> 
                    <input type="button" name="Back" value="Back" onclick="if (warnUnsavedChanges()) location.href='AdminTest.jsp'">
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
