<%@ include file="StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.web.servlet.InventoryManager" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	String serialNumber = "";
	if (request.getParameter("Change") != null) {
		StarsLMHardware hardware = (StarsLMHardware) session.getAttribute(InventoryManager.STARS_LM_HARDWARE_TEMP);
		if (hardware != null) serialNumber = hardware.getManufactureSerialNumber();
	}
	else
		session.removeAttribute(InventoryManager.STARS_LM_HARDWARE_TEMP);
	
	boolean inWizard = (request.getParameter("Wizard") != null);
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
function validate(form) {
	if (form.SerialNo.value == "") {
		alert("Serial # cannot be empty!");
		return false;
	}
	return true;
}

function selectInventory(form) {
	form.attributes["action"].value = "../Hardware/SelectInv.jsp";
	form.submit();
}
</script>
</head>
<body class="Background" leftmargin="0" topmargin="0" >
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
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
          <td width="1" height="102" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
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
<%	if (inWizard) out.print("&nbsp;");
	else { %>
		    <% String pageName = "SerialNumber.jsp"; %>
			<%@ include file="Nav.jsp" %>
<%	} %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div class = "MainText" align="center">
              <% String header = "CREATE NEW HARDWARE"; %>
              <%@ include file="InfoSearchBar.jsp" %>
			  
              <p>&nbsp;</p>
			  <form name="form1" method="post" action="<%= request.getContextPath() %>/servlet/InventoryManager" onsubmit="return validate(this)">
			    <input type="hidden" name="action" value="CheckInventory">
				<input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/CreateHardware.jsp<% if (inWizard) out.print("?Wizard=true"); %>">
                <table width="480" border="0" cellspacing="0" cellpadding="3" class="MainText">
                  <tr>
                    <td width="322">Please select from the inventory:</td>
                    <td width="146"> 
                      <input type="button" name="SelectInv" value="Select Inventory" onclick="selectInventory(this.form)">
                    </td>
                  </tr>
                  <tr>
                    <td width="322">Or enter the serial #: 
                      <input type="text" name="SerialNo" maxlength="30" size="24" value="<%= serialNumber %>">
                    </td>
                    <td width="146"> 
                      <input type="submit" name="CheckInv" value="Check Inventory">
                    </td>
                  </tr>
                </table>
              </form>
              <p>&nbsp;</p>
              <form name="form2" method="post" action="CreateHardware.jsp<% if (inWizard) out.print("?Wizard=true"); %>">
                <table width="480" border="0" cellspacing="0" cellpadding="3">
                  <tr> 
                    <td width="322" class="MainText">Skip inventory checking. 
                      Add hardware directly (Not Recommended):</td>
                    <td width="146" class="MainText"> 
                      <input type="submit" name="Skip" value="Skip">
                    </td>
                  </tr>
                </table>
              </form>
              <p>&nbsp;</p>
              <p>&nbsp;</p>
            </div>
          
           </td>
          <td width="1" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
