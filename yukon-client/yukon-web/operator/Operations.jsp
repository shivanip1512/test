<%@ page import="com.cannontech.common.constants.RoleTypes" %>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
<%@ include file="Consumer/StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="StyleSheet" rel="stylesheet" href="demostyle.css" type="text/css">
<cti:checkRole roleid="<%= RoleTypes.OPERATOR_CONSUMER_INFO %>">
<script language="JavaScript">
	document.getElementById("StyleSheet").href = '<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_STYLE_SHEET %>"/>';
</script>
</cti:checkRole>
</head>
<body class="Background" text="#000000" leftmargin="0" topmargin="0" link="#000000" vlink="#000000" alink="#000000">
<table width="658" border="0" cellspacing="0" height="102" cellpadding="0">
  <tr>
    <td width="657"valign="bottom">
      <table width="657" border="0" cellspacing="0" cellpadding="3" height="102">
        <tr> 
          <td id="Header" background="Header.gif" height="77" >&nbsp;</td>
<cti:checkRole roleid="<%= RoleTypes.OPERATOR_CONSUMER_INFO %>">
<script language="JavaScript">
	document.getElementById("Header").background = '<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_HEADER %>"/>';
</script>
</cti:checkRole>
        </tr>
        <tr>
          <form name="form2" method="post" action="/login.jsp"><td>
                      <div align="right"><span class="Main"><a href="/login.jsp" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                    </td></form>
        </tr>
      </table>
    </td>
    <td width="1" bgcolor="#000000"><img src="VerticalRule.gif"></td>
  </tr>
</table>
<table width="658" border="0" cellspacing="0" cellpadding="0" align="left">
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="1" background="VerticalRule.gif" height="1"></td>
  </tr>
<cti:checkRole roleid="<%= RoleTypes.OPERATOR_CONSUMER_INFO %>">
  <tr> 
    <td width="102" background="ConsumerImage.jpg" height="102">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top"><img src="ConsumerHeader.gif" width="229" height="15" border="0"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="97"><font face="Arial, Helvetica, sans-serif" size="2"> </font></td>
          <td width="109">&nbsp;</td>
          <td width="250"><font face="Arial, Helvetica, sans-serif" size="1">Search 
            for existing customer:</font></td>
          <td width="45">&nbsp;</td>
        </tr>
        <tr> 
          <form name = "custSearchForm" method="POST" action="/servlet/SOAPClient">
            <input type="hidden" name="action" value="SearchCustAccount">
            <td width="97" class = "Main" > 
              <div align = "center" style = "border:solid 1px #666999;"><a href = "Consumer/New.jsp" class = "Link1" style = "text-decoration:none;">New 
                Account</a></div>
            </td>
            <td  class = "Main" width="109" >&nbsp;</td>
            <td  class = "Main" width="250" align = "right"> 
              <select name="SearchBy">
<%
	StarsCustSelectionList searchByList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE );
	for (int i = 0; i < searchByList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = searchByList.getStarsSelectionListEntry(i);
%>
                <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
<%
	}
%>
              </select>
              &nbsp; 
              <input type="text" name="SearchValue">
              &nbsp; </td>
            <td class = "Main" width="45" valign = "top"><img src="GoButton.gif" width="23" height="20" onClick = "Javascript:document.custSearchForm.submit();"> 
            </td>
          </form>
          <form method="get" action="Consumer/New.jsp"> 
          </form>
        </tr>
      </table>
    </td>
    <td width="1" background="VerticalRule.gif" height="102"></td>
  </tr>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.OPERATOR_COMMERCIAL_METERING %>">
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="1" background="VerticalRule.gif" height="1"></td>
  </tr>
  <tr> 
    <td width="102" height="102" background="MeterImage.jpg">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top"><img src="MeteringHeader.gif" width="161" height="15"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="80">&nbsp;</td>
          <td width="80">&nbsp;</td>
          <td >&nbsp;</td>
        </tr>
        <tr> 
          <form method="post" action="Metering/Billing.jsp">
            <td width="110" class = "Main"> 
              <div align = "center" style = "border:solid 1px #666999;"><a href = "Metering/Billing.jsp" class = "Link1" style = "text-decoration:none;">Billing 
                </a></div>
            </td>
            <td width="110" class = "Main"> 
              <div align = "center" style = "border:solid 1px #666999;"><a href = "Metering/Metering.jsp" class = "Link1" style = "text-decoration:none;">All 
                Trends </a></div>
            </td>
          </form>
          <form method="post" action="Metering/Metering.jsp">
            <td class = "Main"> </td>
          </form>
          <form method="get" action="Metering/Metering.jsp">
          </form>
        </tr>
      </table>
    </td>
    <td width="1" background="VerticalRule.gif" height="102"></td>
  </tr>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.OPERATOR_LOADCONTROL %>">
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="1" background="VerticalRule.gif" height="1"></td>
  </tr>
  <tr> 
    <td width="102" height="102" background="LoadImage.jpg">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top">
      <div align="left"><img src="LoadHeader.gif"><br>
      </div>
      <div align="left"></div>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="110" >&nbsp;</td>
          <td width="110" >&nbsp;</td>
          <td width="110" >&nbsp;</td>
          <td>&nbsp;</td>
          </tr>
        <tr> 
          <form method="post" action="LoadControl/oper_direct.jsp">
            <td width="110" class = "Main">
<cti:checkRole roleid="<%= RoleTypes.CICUSTOMER_DIRECT_CONTROL %>"> 
              <div align = "center" style = "border:solid 1px #666999;">
			    <a href = "LoadControl/oper_direct.jsp" class = "Link1" style = "text-decoration:none;">Direct</a>
              </div>
</cti:checkRole>
            &nbsp;</td>
          </form>
          <form method="post" action="LoadControl/oper_mand.jsp">
            <td width="110" class = "Main"> 
<cti:checkRole roleid="<%= RoleTypes.CICUSTOMER_CURTAILMENT %>"> 
              <div align = "center" style = "border:solid 1px #666999;">
			    <a href = "LoadControl/oper_mand.jsp" class = "Link1" style = "text-decoration:none;"><cti:text roleid="<%= RoleTypes.CURTAILMENT_TEXT %>"/></a>
			  </div>
</cti:checkRole>
            &nbsp;</td>
          </form>
          <form method="post" action="LoadControl/oper_ee.jsp">
            <td width = "110" class = "Main"> 
<cti:checkRole roleid="<%= RoleTypes.CICUSTOMER_ENERGY_EXCHANGE %>"> 
              <div align = "center" style = "border:solid 1px #666999;">
			    <a href = "LoadControl/oper_ee.jsp" class = "Link1" style = "text-decoration:none;"><cti:text roleid="<%= RoleTypes.ENERGYEXCHANGE_TEXT %>"/></a>
			  </div>
</cti:checkRole>
            &nbsp;</td>
		  </form>
            <form method="post" action="Consumer/Odds.jsp">
            <td width = "110" class = "Main">
<cti:checkRole roleid="<%= RoleTypes.LOADCONTROL_CONTROL_ODDS %>">
              <div align = "center" style = "border:solid 1px #666999;">
			    <a href = "Consumer/Odds.jsp" class = "Link1" style = "text-decoration:none;">Odds for Control</a>
			  </div>
</cti:checkRole>
            &nbsp;</td>
		  </form>
          </tr>
        <tr> 
          <td width="110" class = "Main">&nbsp;</td>
          <td width="110" class = "Main">&nbsp;</td>
          <td width = "110" class = "Main">&nbsp;</td>
          <td>&nbsp;</td>
        </tr>
      </table>
    </td>
    <td width="1" background="VerticalRule.gif" height="102"></td>
  </tr>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.OPERATOR_HARDWARE_INVENTORY %>">
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="1" background="VerticalRule.gif" height="1"></td>
  </tr>
  <tr> 
    <td width="102" height="102" background="InventoryImage.jpg">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top"><img src="InventoryHeader.gif" width="148" height="15"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td width="144" height="30" valign="bottom"><font face="Arial, Helvetica, sans-serif" size="1">Search 
            by serial number:</font></td>
          <td width="369" height="30" valign="bottom">&nbsp;</td>
        </tr>
        <tr> 
          <form name = "serialSearchForm" method="post" action="Hardware/InventoryDetail.jsp">
            <td width="144"> 
              <input type="text" name="textfield22">
              &nbsp; </td>
            <td width="369" valign = "top"> <img src="GoButton.gif" width="23" height="20" onclick = "Javascript:document.serialSearchForm.submit();" > </td>
          </form>
        </tr>
      </table>
    </td>
    <td width="1" background="VerticalRule.gif" height="102"></td>
  </tr>
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="1" background="VerticalRule.gif" height="1"></td>
  </tr>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.OPERATOR_WORK_ORDERS %>">
  <tr> 
    <td width="102" height="102" background="WorkImage.jpg">&nbsp;</td>
    <td width="555" bgcolor="#FFFFFF" height="102" valign="top"><img src="WorkHeader.gif" width="104" height="15"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td  >&nbsp;</td>
          <td  >&nbsp;</td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <form method="post" action="WorkOrder/SOList.jsp">
            <td width="110" class = "Main"> 
              <div align = "center" style = "border:solid 1px #666999;"><a href = "WorkOrder/SOList.jsp" class = "Link1" style = "text-decoration:none;">Service 
                Order List </a></div>
            </td>
            <td width="110" class = "Main" > <div align = "center" style = "border:solid 1px #666999;"><a href = "WorkOrder/InstallList.jsp" class = "Link1" style = "text-decoration:none;">New Installs 
                List </a></div>
            
            </td>
          </form>
          <form method="post" action="WorkOrder/InstallList.jsp">
            <td  >&nbsp; </td>
          </form>
        </tr>
      </table>
    </td>
    <td width="1" background="VerticalRule.gif" height="102"></td>
  </tr>
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="1" background="VerticalRule.gif" height="1"></td>
  </tr>
</cti:checkRole>
<cti:checkRole roleid="<%= RoleTypes.OPERATOR_ADMINISTRATION %>">
  <tr>
    <td width="102" bgcolor="#000000" height="102" background="AdminImage.jpg">&nbsp;</td>
    <td bgcolor="#FFFFFF" height="102" valign="top"><img src="AdminHeader.gif" width="129" height="15"><br>
      <table width="525" border="0" cellspacing="0" cellpadding="3" align="center">
        <tr> 
          <td>&nbsp;</td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <form method="post" action="Admin/Privileges.jsp">
            <td align = "center" class = "Main" width = "110"> 
              <div align = "center" style = "border:solid 1px #666999;"><a href = "Admin/Privileges.jsp" class = "Link1" style = "text-decoration:none;">Privileges 
                </a></div>
            </td>
            <td align = "center" class = "Main">&nbsp;</td>
          </form>
        </tr>
      </table>
    </td>
    <td width="1" background="VerticalRule.gif" height="16"></td>
  </tr>
  <tr> 
    <td width="102" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="555" bgcolor="#000000" height="1"><img src="VerticalRule.gif"></td>
    <td width="1" background="VerticalRule.gif" height="1"></td>
  </tr>
</cti:checkRole>
  </table>
<div align="center"></div>
</body>

</html>
