<%@ include file="include/StarsHeader.jsp" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<cti:verifyRolePropertyValue property="OPERATOR_IMPORT_CUSTOMER_ACCOUNT" expectedValue="true"/>

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
			<div align="right">
				<form id="searchForm" action="/spring/stars/operator/account/search" method="get">
					<div style="padding-top:8px;padding-bottom:8px;">
						<select name="searchBy" onchange="$('accountSearchValue').value = ''">
							<option value="ACCOUNT_NUMBER">
								<cti:msg key="yukon.web.modules.operator.accountSearchByEnum.ACCOUNT_NUMBER" />
							</option>
							<option value="PHONE_NUMBER">
								<cti:msg key="yukon.web.modules.operator.accountSearchByEnum.PHONE_NUMBER" />
							</option>
							<option value="LAST_NAME">
								<cti:msg key="yukon.web.modules.operator.accountSearchByEnum.LAST_NAME" />
							</option>
							<option value="SERIAL_NUMBER">
								<cti:msg key="yukon.web.modules.operator.accountSearchByEnum.SERIAL_NUMBER" />
							</option>
							<option value="MAP_NUMBER">
								<cti:msg key="yukon.web.modules.operator.accountSearchByEnum.MAP_NUMBER" />
							</option>
							<option value="ADDRESS">
								<cti:msg key="yukon.web.modules.operator.accountSearchByEnum.ADDRESS" />
							</option>
							<option value="ALT_TRACKING_NUMBER">
								<cti:msg key="yukon.web.modules.operator.accountSearchByEnum.ALT_TRACKING_NUMBER" />
							</option>
							<option value="COMPANY">
								<cti:msg key="yukon.web.modules.operator.accountSearchByEnum.COMPANY" />
							</option>
						</select>
						<input type="text" name="searchValue" id="accountSearchValue" value="${accountSearchResultHolder.searchValue}" />
						<input type="submit" value="Search" />
					</div>
				</form>
			</div>
			<br />
            <div align="center"> 
              <% String header = "IMPORT ACCOUNT DATA"; %>
              	<table width="100%" border="0" cellspacing="0" cellpadding="3">
  					<tr> 
    					<td align="center" class="TitleHeader"><%= header %></td>
  					</tr>
				</table>
				<br />
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg.replaceAll(System.getProperty("line.separator"), "<br>") + "</span><br>"); %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg.replaceAll(System.getProperty("line.separator"), "<br>") + "</span><br>"); %>
			  <form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/ImportManager" enctype="multipart/form-data">
                <input type="hidden" name="action" value="ImportCustAccounts">
                <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/ImportAccount.jsp">
                <table width="600" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td class="MainText" align="center">Please enter the import 
                      file(s). Leave the field empty if you do not have the corresponding 
                      file.</td>
                  </tr>
                </table>
                <br>
                <table width="500" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                  <tr> 
                    <td width="30%" align="right">Customer File: </td>
                    <td width="70%"> 
                      <input type="file" name="CustFile" size="35">
                    </td>
                  </tr>
                  <tr> 
                    <td width="30%" align="right">Hardware File: </td>
                    <td width="70%"> 
                      <input type="file" name="HwFile" size="35">
                    </td>
                  </tr>
                  <tr align="center"> 
                    <td colspan="2" class="MainText">Email (to receive log file): 
                      <input type="text" name="Email" size="35">
                    </td>
                  </tr>
                </table>
                <table width="400" border="0" cellspacing="0" cellpadding="5">
                  <tr> 
                    <td align="center"> 
                      <input type="submit" name="Submit" value="Submit">
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
