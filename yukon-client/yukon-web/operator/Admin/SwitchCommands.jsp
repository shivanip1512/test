<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsLMHardware" %>
<%@ page import="com.cannontech.stars.util.SwitchCommandQueue" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function selectAll(checked) {
	var checkboxes = document.getElementsByName("InvID");
	for (i = 0; i < checkboxes.length; i++)
		checkboxes[i].checked = checked;
}

function selectSingle(form) {
	var checkboxes = document.getElementsByName("InvID");
	var allChecked = true;
	for (i = 0; i < checkboxes.length; i++) {
		if (!checkboxes[i].checked) {
			allChecked = false;
			break;
		}
	}
	form.All.checked = allChecked;
}

function validate(form) {
	var checkboxes = document.getElementsByName("InvID");
	for (i = 0; i < checkboxes.length; i++) {
		if (checkboxes[i].checked)
			return true;
	}
	
	alert("No switch command has been selected");
	return false;
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
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
            <div align="center"> <br>
              <span class="TitleHeader">ADMINISTRATION - SWITCH COMMANDS</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
              <table width="600" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td align="center" class="MainText">All switch commands will 
                    be sent out automatically at midnight.</td>
                </tr>
              </table>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/InventoryManager" onsubmit="return validate(this)">
              <input type="hidden" name="action" value="SendSwitchCommands">
              <table width="400" border="1" cellspacing="0" cellpadding="1" align="center">
                <tr> 
                  <td class="HeaderCell" width="26"> 
                    <input type="checkbox" name="All" value="true" onclick="selectAll(this.checked)">
                  </td>
                  <td class="HeaderCell" width="113">Serial #</td>
                  <td class="HeaderCell" width="118">Account #</td>
                  <td class="HeaderCell" width="125">Command Type</td>
                </tr>
<%
	SwitchCommandQueue.SwitchCommand[] commands = liteEC.getSwitchCommandQueue().getCommands(liteEC.getLiteID(), false);
	
	TreeMap serialMap = new TreeMap();
	for (int i = 0; i < commands.length; i++) {
		String serialNo = ((LiteStarsLMHardware) liteEC.getInventoryBrief(commands[i].getInventoryID(), true)).getManufacturerSerialNumber();
		try {
			Integer num = Integer.valueOf(serialNo);
			serialMap.put(num, commands[i]);
		}
		catch (NumberFormatException e) {
			serialMap.put(serialNo, commands[i]);
		}
	}
	
	Iterator it = serialMap.entrySet().iterator();
	while (it.hasNext()) {
		Map.Entry entry = (Map.Entry) it.next();
		Object serialNo = entry.getKey();
		SwitchCommandQueue.SwitchCommand cmd = (SwitchCommandQueue.SwitchCommand) entry.getValue();
		String accountNo = "(none)";
		if (cmd.getAccountID() > 0)
			accountNo = liteEC.getBriefCustAccountInfo(cmd.getAccountID(), true).getCustomerAccount().getAccountNumber();
%>
                <tr> 
                  <td width="26" class="TableCell"> 
                    <input type="checkbox" name="InvID" value="<%= cmd.getInventoryID() %>" onclick="selectSingle(this.form)">
                  </td>
                  <td width="113" class="TableCell"><%= serialNo.toString() %></td>
                  <td width="118" class="TableCell"><%= accountNo %></td>
                  <td width="125" class="TableCell"><%= cmd.getCommandType() %></td>
                </tr>
                <%
	}
%>
              </table>
              <br>
              <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr> 
                  <td align="right" width="47%"> 
                    <input type="submit" name="Submit" value="Send">
                  </td>
                  <td width="53%"> 
                    <input type="submit" name="Submit2" value="Remove" onclick="this.form.action.value = 'RemoveSwitchCommands'">
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
