<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.bean.InventoryBean" %>
<%@ page import="com.cannontech.stars.web.servlet.InventoryManager" %>
<%
	boolean inWizard = ((String) session.getAttribute(ServletUtils.ATT_REFERRER)).indexOf("Wizard=true") >= 0;
	if (!inWizard && accountInfo == null) {
		response.sendRedirect("../Operations.jsp");
		return;
	}
	
	if (request.getParameter("SerialNo") != null) {
		session.setAttribute(ServletUtils.ATT_REFERRER2, request.getHeader("referer"));
		session.setAttribute(ServletUtils.ATT_REDIRECT, request.getParameter(ServletUtils.ATT_REDIRECT));
	}
	String referer = (String) session.getAttribute(ServletUtils.ATT_REFERRER2);
%>

<jsp:useBean id="selectInvBean" class="com.cannontech.stars.web.bean.InventoryBean" scope="session">
	<%-- this body is executed only if the bean is created --%>
	<jsp:setProperty name="selectInvBean" property="energyCompanyID" value="<%= user.getEnergyCompanyID() %>"/>
	<jsp:setProperty name="selectInvBean" property="sortBy" value="<%= YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO %>"/>
	<jsp:setProperty name="selectInvBean" property="htmlStyle" value="<%= InventoryBean.HTML_STYLE_SELECT_INVENTORY %>"/>
</jsp:useBean>
	
<% if (request.getParameter("page") == null) { %>
	<%-- intialize bean properties --%>
	<jsp:setProperty name="selectInvBean" property="filterBy" value="<%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_LOCATION %>"/>
	<jsp:setProperty name="selectInvBean" property="location" value="<%= InventoryBean.INV_LOCATION_WAREHOUSE %>"/>
	<jsp:setProperty name="selectInvBean" property="page" value="1"/>
	<jsp:setProperty name="selectInvBean" property="referer" value="<%= referer %>"/>
<% } %>

<%-- Grab the search criteria --%>
<jsp:setProperty name="selectInvBean" property="filterBy" param="FilterBy"/>
<jsp:setProperty name="selectInvBean" property="location" param="Location"/>
<jsp:setProperty name="selectInvBean" property="page" param="page"/>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">
function submitIt(filterBy) {
	document.MForm.FilterBy.value = filterBy;
	document.MForm.submit();
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jsp" %>
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
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "SELECT INVENTORY"; %>
<% if (!inWizard) { %>
              <%@ include file="include/InfoSearchBar.jsp" %>
<% } else { %>
              <%@ include file="include/InfoSearchBar2.jsp" %>
<% } %>

			  <form name="MForm" method="post" action="">
			    <input type="hidden" name="FilterBy" value="<%= selectInvBean.getFilterBy() %>">
				<input type="hidden" name="Location" value="<%= InventoryBean.INV_LOCATION_WAREHOUSE %>">
				<input type="hidden" name="page" value="1">
                <table width="80%" border="0" cellspacing="0" cellpadding="1">
                  <tr> 
                    <td class="MainText" align="center">Check the radio button 
                      of the hardware you want to select, then click Select.</td>
                  </tr>
                  <tr> 
                    <td class="MainText" align="center"> 
                      <input type="button" name="ShowAll" value="Show All" onclick="submitIt(0)"
                      <% if (selectInvBean.getFilterBy() == 0) { %>disabled<% } %>>
                      <input type="button" name="ShowWarehouse" value="Show Warehouse" onclick="submitIt(<%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_LOCATION %>)"
                      <% if (selectInvBean.getFilterBy() == YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_LOCATION && selectInvBean.getLocation() == InventoryBean.INV_LOCATION_WAREHOUSE) { %>disabled<% } %>>
                    </td>
                  </tr>
                </table>
              </form>
              <%= selectInvBean.getHTML(request) %> 
              <!--              <form name='form1' method='post' action='<%= request.getContextPath() %>/servlet/InventoryManager'>
			    <input type='hidden' name='action' value='SelectInventory'>
                <table width='80%' border='0' cellspacing='0' cellpadding='3' class='TableCell'>
                  <tr> 
                    <td>1-4 of 4 | <font color='#CCCCCC'>First</font> | <font color='#CCCCCC'>Previous</font> 
                      | <font color='#CCCCCC'>Next</font> | <font color='#CCCCCC'>Last</font></td>
                  </tr>
                  <tr> 
                    <td> 
                      <table width='100%' border='1' cellspacing='0' cellpadding='3'>
                        <tr> 
                          <td class='HeaderCell' width='5%'>&nbsp;</td>
                          <td class='HeaderCell' width='17%'>Serial #</td>
                          <td class='HeaderCell' width='17%'>Device Type</td>
                          <td class='HeaderCell' width='17%'>Install Date</td>
                          <td class='HeaderCell' width='49%'>Location</td>
                        </tr>
                        <tr> 
                          <td class='TableCell' width='5%'><input type='radio' name='InvID' value='0'></td>
                          <td class='TableCell' width='17%'><a href='InventoryDetail.jsp?InvId=7'>500000000</a></td>
                          <td class='TableCell' width='17%'>LCR-5000</td>
                          <td class='TableCell' width='17%'>08/24/2003</td>
                          <td class='TableCell' width='49%'><a href='' class='Link1' onclick='selectAccount(7); return false;'>Acct 
						    # 12345</a> Robert Livingston, 8301 Golden Valley Rd, Suite 300...</td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                  <tr> 
                    <td>1-4 of 4 | <font color='#CCCCCC'>First</font> | <font color='#CCCCCC'>Previous</font> 
                      | <font color='#CCCCCC'>Next</font> | <font color='#CCCCCC'>Last</font></td>
                  </tr>
                </table>
                <br>
                <table width='200' border='0' cellspacing='0' cellpadding='3'>
                  <tr> 
                    <td align='right'>
                      <input type='submit' name='Submit' value='Submit'>
                    </td>
                    <td>
                      <input type='button' name='Cancel' value='Cancel' onclick='location.href="<%= referer %>"'>
                    </td>
                  </tr>
                </table>
              </form>
			  <form name='cusForm' method='post' action='/servlet/SOAPClient'>
                <input type='hidden' name='action' value='GetCustAccount'>
                <input type='hidden' name='AccountID' value=''>
                <input type='hidden' name='REDIRECT' value='/operator/Consumer/Update.jsp'>
                <input type='hidden' name='REFERRER' value='/operator/Hardware/Inventory.jsp'>
			  </form>
<script language='JavaScript'>
function selectAccount(accountID) {
  var form = document.cusForm;
  form.AccountID.value = accountID;
  form.submit();
}
</script>-->
              <p>&nbsp; </p>
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
