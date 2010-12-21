<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsLMHardware" %>
<%@ page import="com.cannontech.stars.util.SwitchCommandQueue" %>
<%@ page import="com.cannontech.stars.core.dao.StarsInventoryBaseDao" %>
<jsp:useBean id="configBean" class="com.cannontech.stars.web.bean.ConfigBean" scope="page"/>
<%
	if (!DaoFactory.getAuthDao().checkRoleProperty(lYukonUser, AdministratorRole.ADMIN_VIEW_BATCH_COMMANDS)) {
	    response.sendRedirect("../Operations.jsp");
	    return;
	}

	boolean showEnergyCompany = liteEC.hasChildEnergyCompanies() && DaoFactory.getAuthDao().checkRoleProperty(lYukonUser, AdministratorRole.ADMIN_MANAGE_MEMBERS);
	List<LiteStarsEnergyCompany> descendants = ECUtils.getAllDescendants(liteEC);
	
	int memberID = -1;
	LiteStarsEnergyCompany member = null;
	if (request.getParameter("Member") != null) {
		memberID = Integer.parseInt(request.getParameter("Member"));
		if (memberID >= 0)
			member = StarsDatabaseCache.getInstance().getEnergyCompany(memberID);
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function changeMember(form) {
	form.attributes["action"].value = "";
	form.submit();
}

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

function removeCommands(form) {
	if (!validate(form) ||
		!confirm("Are you sure you want to remove the selected switch commands?"))
		return;
	form.action.value = "RemoveSwitchCommands";
	form.submit();
}

function fileWriteCommands(form) {
	if (!validate(form) ||
		!confirm("Are you sure you want to write the selected switch commands?"))
		return;
	form.action.value = "FileWriteSwitchCommands";
	form.submit();
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
            <div align="center"> <br>
              <span class="TitleHeader">ADMINISTRATION - SWITCH COMMANDS</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
              <br>
              <table width="600" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <c:choose>
						<c:when test="${configBean.writeToFileAllowed}">
                            <td align="center" class="MainText">All switch commands marked<div class="ErrorMsg">manual</div>must 
                                be moved to output files from this screen.</td> 
						</c:when>
	                    <c:otherwise>
	             			<td align="center" class="MainText">All switch commands marked<div class="ErrorMsg">auto</div>will 
		                    be sent out automatically at midnight.</td>
		               	</c:otherwise>
	             </c:choose> 	
                </tr>
              </table>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/InventoryManager" onsubmit="return validate(this)">
              <input type="hidden" name="action" value="SendSwitchCommands">
              <input type="hidden" name="<%= ServletUtils.CONFIRM_ON_MESSAGE_PAGE %>">
<% if (showEnergyCompany) { %>
              <table width="400" border="0" cellspacing="0" cellpadding="1" align="center">
                <tr>
                  <td align="center" class="MainText">Member: 
                    <select name="Member" onchange="changeMember(this.form)">
                      <option value="-1">All</option>
                      <%
	for (int i = 0; i < descendants.size(); i++) {
		LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
		String selected = (member != null && company.equals(member))? "selected" : "";
%>
                      <option value="<%= company.getLiteID() %>" <%= selected %>><%= company.getName() %></option>
                      <%
	}
%>
                    </select>
                  </td>
                </tr>
              </table>
              <br>
<% } %>
              <table width="500" border="1" cellspacing="0" cellpadding="1" align="center">
                <tr> 
                  <td class="HeaderCell" width="5%"> 
                    <input type="checkbox" name="All" value="<%= memberID %>" onclick="selectAll(this.checked)">
                  </td>
                  <td class="HeaderCell" width="19%">Serial #</td>
                  <td class="HeaderCell" width="19%">Account #</td>
                  <td class="HeaderCell" width="19%">Command Type</td>
<% if (showEnergyCompany) { %>
                  <td class="HeaderCell" width="19%">Member</td>
                  <td class="HeaderCell" width="19%">Process</td>
<% } %>
                </tr>
<%

    StarsInventoryBaseDao starsInventoryBaseDao = 
        YukonSpringHook.getBean("starsInventoryBaseDao", StarsInventoryBaseDao.class);

	for (int i = 0; i < descendants.size(); i++) {
		LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
		if (member != null && !company.equals(member)) continue;
		
		SwitchCommandQueue.SwitchCommand[] commands = SwitchCommandQueue.getInstance().getCommands(company.getLiteID(), false);
		
		TreeMap serialMap = new TreeMap();
		for (int j = 0; j < commands.length; j++) {
			String serialNo = ((LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(commands[j].getInventoryID())).getManufacturerSerialNumber();
			try {
				Integer num = Integer.valueOf(serialNo);
				serialMap.put(num, commands[j]);
			}
			catch (NumberFormatException e) {
				serialMap.put(serialNo, commands[j]);
			}
		}
		
		Iterator it = serialMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object serialNo = entry.getKey();
			SwitchCommandQueue.SwitchCommand cmd = (SwitchCommandQueue.SwitchCommand) entry.getValue();
			String accountNo = "(none)";
			if (cmd.getAccountID() > 0)
				accountNo = starsCustAccountInformationDao.getById(cmd.getAccountID(), company.getEnergyCompanyID()).getCustomerAccount().getAccountNumber();
%>
                <tr> 
                  <td width="5%" class="TableCell"> 
                    <input type="checkbox" name="InvID" value="<%= cmd.getInventoryID() %>" onclick="selectSingle(this.form)">
                  </td>
                  <td width="19%" class="TableCell"><%= serialNo.toString() %></td>
                  <td width="19%" class="TableCell"><%= accountNo %></td>
                  <td width="19%" class="TableCell"><%= cmd.getCommandType() %></td>
<% if (showEnergyCompany) { %>
                  <td width="19%" class="TableCell"><%= company.getName() %></td>
                  <c:choose>
                        <c:when test="${configBean.writeToFileAllowed}"> 
                            <td width="19%" class="TableCell"><div class="ErrorMsg">Manual<div></td>
                        </c:when>
                        <c:otherwise>
                            <td width="19%" class="TableCell"><div class="ErrorMsg">Auto<div></td>
                        </c:otherwise>
                  </c:choose> 
                  
<% } %>
                </tr>
                <%
		}
	}
%>
              </table>
              <br>
              <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr> 
                	<c:choose>
						<c:when test="${configBean.writeToFileAllowed}"> 
							<td align="right" width="50%"> 
                    			<input type="button" name="fileWrite" value="Write to File" onclick="fileWriteCommands(this.form)">
                    		</td>
						</c:when>
	                    <c:otherwise>
	             			<td align="right" width="50%"> 
                    			<input type="submit" name="Submit" value="Send">
                  			</td>
		               	</c:otherwise>
	             	</c:choose> 
                 	<td width="50%"> 
                    	<input type="button" name="Remove" value="Remove" onclick="removeCommands(this.form)">
                  	</td>
                </tr>
              </table>
              </form>
            <div align="center"><span class="TitleHeader">Last Batch Submission</span><br>
<%
	Map<Integer,Object[]> batchConfig = InventoryManagerUtil.getBatchConfigSubmission();
	if (showEnergyCompany) {
%>
              <table border="0" cellspacing="0" cellpadding="3" class="MainText">
                <%
		for (int i = 0; i < descendants.size(); i++) {
			LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
			Object[] lastSubmission = (Object[]) batchConfig.get(company.getEnergyCompanyID());
			if (lastSubmission == null) continue;
%>
                <tr>
                  <td width="150" align="right"><%= company.getName() %>:</td>
                  <td width="100" align="center"><%= ServletUtils.formatDate((Date)lastSubmission[0], dateTimeFormat) %></td>
                  <td width="250"><%= (String)lastSubmission[1] %></td>
                </tr>
<%
		}
%>
              </table>
<%
	}
	else {
		Object[] lastSubmission = (Object[]) batchConfig.get(liteEC.getEnergyCompanyID());
		if (lastSubmission != null) {
%>
              <table border="0" cellspacing="0" cellpadding="3" class="MainText">
                <tr> 
                  <td width="100"><%= ServletUtils.formatDate((Date)lastSubmission[0], dateTimeFormat) %></td>
                  <td width="250"><%= (String)lastSubmission[1] %></td>
                </tr>
              </table>
<%
		}
	}
%>
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
