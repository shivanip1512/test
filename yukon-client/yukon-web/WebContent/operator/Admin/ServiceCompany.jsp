<%@ page import="com.cannontech.user.UserUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="servComp" scope="page" class="com.cannontech.stars.web.bean.ServiceCompanyBean" />
<%
	StarsServiceCompany company = null;
	int compIdx = Integer.parseInt( request.getParameter("CompanyID") );
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
	else if (action.equalsIgnoreCase("EditAddress") || action.equalsIgnoreCase("NewCode")) {
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
			scTemp.setCompanyID(company.getCompanyID());
			session.setAttribute(StarsAdminUtil.SERVICE_COMPANY_TEMP, scTemp);
		}
		
		scTemp.setCompanyName( request.getParameter("CompanyName") );
		scTemp.setMainPhoneNumber( ServletUtils.formatPhoneNumberForStorage(request.getParameter("PhoneNo")) );
		scTemp.setMainFaxNumber( ServletUtils.formatPhoneNumberForStorage(request.getParameter("FaxNo")) );
		scTemp.getPrimaryContact().setLastName( request.getParameter("ContactLastName") );
		scTemp.getPrimaryContact().setFirstName( request.getParameter("ContactFirstName") );
		
		ContactNotification email = ServletUtils.getContactNotification(scTemp.getPrimaryContact(), YukonListEntryTypes.YUK_ENTRY_ID_EMAIL);
		if (email != null) {
			email.setNotification( request.getParameter("Email") );
		}
		else {
			email = ServletUtils.createContactNotification(request.getParameter("Email"), YukonListEntryTypes.YUK_ENTRY_ID_EMAIL);
			scTemp.getPrimaryContact().addContactNotification(email);
		}
		
		if(action.equalsIgnoreCase("EditAddress"))
		{	
			response.sendRedirect("Address.jsp?referer=ServiceCompany.jsp&CompanyID=" + compIdx);
			return;
		}
		
		if(action.equalsIgnoreCase("NewCode"))
		{	
			response.sendRedirect("NewCodes.jsp?referer=ServiceCompany.jsp&CompanyID=" + compIdx);
			return;
		}
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

function newCode(form) {
	form.attributes["action"].value = "";
	form.action.value = "NewCode";
	form.submit();
}

</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jspf" %>
<% if (company.getCompanyID() < 0 && !address.equals("(none)")) { %>
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
                          <input type="text" name="PhoneNo" value="<%= ServletUtils.formatPhoneNumberForDisplay(sc.getMainPhoneNumber()) %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Main Fax 
                          #:</td>
                        <td width="75%" class="TableCell"> 
                          <input type="text" name="FaxNo" value="<%= ServletUtils.formatPhoneNumberForDisplay(sc.getMainFaxNumber()) %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Email:</td>
<%
	ContactNotification email = ServletUtils.getContactNotification(sc.getPrimaryContact(), YukonListEntryTypes.YUK_ENTRY_ID_EMAIL);
	String emailAddr = (email != null)? email.getNotification() : "";
%>
                        <td width="75%" class="TableCell"> 
                          <input type="text" name="Email" value="<%= emailAddr %>" onchange="setContentChanged(true)">
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
                              <td>Contact Login: 
                                <select name="ContactLogin" onchange="setContentChanged(true)">
                                <%
                                	int[] availUserids = com.cannontech.database.db.contact.Contact.findAvailableUserIDs( sc.getPrimaryContact().getLoginID());
                                	for( int i = 0; i < availUserids.length; i++ ){
                                		if(availUserids[i] == UserUtils.USER_DEFAULT_ID || availUserids[i] > 0 ){%>
                                		<option value='<%=availUserids[i]%>' <%=availUserids[i]==sc.getPrimaryContact().getLoginID()?"selected":""%>><%=DaoFactory.getYukonUserDao().getLiteYukonUser(availUserids[i])%></option>
                                	<%}}
                                %>
                                </select>
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
              <br clear="all">
              <c:set target="${servComp}" property="serviceCompanyID"> <%=company.getCompanyID()%> </c:set>
              <c:if test="${servComp.serviceCompanyID > -1}">
	              <cti:checkProperty propertyid="<%= AdministratorRole.ADMIN_ALLOW_DESIGNATION_CODES %>">
		              <input type="hidden" name="hasCodes" value="true">
		              <table width="300" border="1" cellspacing="0" cellpadding="0" align="center">
		              	<tr> 
		                  <td class="HeaderCell">Contractor Zip Codes</td>
		                </tr>
		                <tr>
		                	<td>
			                	<table width="100%" border="0" cellspacing="0" cellpadding="5" align="center">
					                <c:forEach items="${servComp.designationCodes}" var="thisCode">
						                <tr> 
						                  <td class="TableCell" align="center" width="30%"> 	
						                  	<input type="text" name='CodeUpdate_<c:out value="${thisCode.designationCodeID}"/>' size="16" value='<c:out value="${thisCode.designationCodeValue}"/>' onchange="setContentChanged(true)"/>
	                                 	  </td>
						                </tr>
					                </c:forEach>
					        		<td class="TableCell" align="center" width="30%">                                    
	                                	<input type="button" name="AddCodes" value="Create New" onClick="newCode(this.form)">
	                           </table>
							</td>
						</tr>
					 </table>
		  		  <br clear="all">
		  		  </cti:checkProperty>
		  	  </c:if>
              <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr>
                  <td width="290" align="right"> 
                    <input type="submit" name="Submit" value="Submit" <%= viewOnly %>>
                  </td>
                  <td width="205"> 
                    <input type="reset" name="Reset" value="Reset" <%= viewOnly %> onclick="setContentChanged(false)">
                  </td>
                  <td width="75" align="right"> 
                    <input type="button" name="Back" value="Back" onclick="if (warnUnsavedChanges()) location.href='ConfigEnergyCompany.jsp'">
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
