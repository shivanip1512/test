<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.servlet.StarsAdmin" %>
<%
	String referer = request.getParameter("referer");
	if (referer == null) {
		response.sendRedirect("AdminTest.jsp"); return;
	}
	
	StarsCustomerAddress address = null;
	if (referer.equalsIgnoreCase("EnergyCompany.jsp")) {
		StarsEnergyCompany ecTemp = (StarsEnergyCompany) session.getAttribute(StarsAdmin.ENERGY_COMPANY_TEMP);
		address = ecTemp.getCompanyAddress();
	}
	else if (referer.equalsIgnoreCase("ServiceCompany.jsp")) {
		StarsServiceCompany scTemp = (StarsServiceCompany) session.getAttribute(StarsAdmin.SERVICE_COMPANY_TEMP);
		address = scTemp.getCompanyAddress();
		int compIdx = Integer.parseInt( request.getParameter("Company") );
		referer += "?Company=" + compIdx;
	}
	if (address.getCounty() == null) address.setCounty("");
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
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
					  <input type="hidden" name="REDIRECT" value="/operator/Consumer/<%= referer %>">
					  <input type="hidden" name="AddressID" value="<%= address.getAddressID() %>">
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Street 
                          Address 1:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="StreetAddr1" value="<%= address.getStreetAddr1() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">Street 
                          Address 2:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="StreetAddr2" value="<%= address.getStreetAddr2() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">City:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="City" value="<%= address.getCity() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell">State:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="State" value="<%= address.getState() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell"> Zip Code:</td>
                        <td width="75%" class="TableCell">
                          <input type="text" name="Zip" value="<%= address.getZip() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="25%" align="right" class="TableCell" height="2">County:</td>
                        <td width="75%" class="TableCell" height="2"> 
                          <input type="text" name="County" value="<%= address.getCounty() %>">
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
                    <input type="button" name="Done" value="Done" onclick="location.href='<%= referer %>'">
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
