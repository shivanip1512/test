<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionList" %>
<%@ page import="com.cannontech.stars.web.util.ImportManagerUtil" %>
<%
	Hashtable unassignedLists = (Hashtable) session.getAttribute(ImportManagerUtil.UNASSIGNED_LISTS);
	boolean hasUnassigned = false;
%>
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
            <div align="center"> 
              <% String header = "IMPORT ACCOUNTS - EDIT SELECTION LISTS"; %>
              <%@ include file="include/InfoSearchBar2.jsp" %>
              <table width="500" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td class="MainText"> 
                    <div align="center">Click &quot;Assign&quot; to assign the 
                      current selection list entries to the values appeared in 
                      the import file, or click &quot;New&quot; to create a new 
                      selection list.<br>
                      A list name followed by a &quot;<font color="#FF0000">*</font>&quot; 
                      means the list is not assigned yet.</div>
                  </td>
                </tr>
              </table>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/ImportManager">
			  <input type="hidden" name="action" value="ImportStarsData">
			  <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/ImportSTARS.jsp">
              <table width="300" border="0" cellspacing="0" cellpadding="3" align="center" class="MainText">
<%
	for (int i = 0; i < ImportManagerUtil.LIST_NAMES.length; i++) {
		String listName = ImportManagerUtil.LIST_NAMES[i][0];
		String dispName = ImportManagerUtil.LIST_NAMES[i][1];
		
		if (unassignedLists.get(listName) == null) continue;
		
		boolean unAssigned = ((Boolean) unassignedLists.get(listName)).booleanValue();
		if (unAssigned) hasUnassigned = true;
		
		YukonSelectionList list = liteEC.getYukonSelectionList(listName);
%>
                <tr> 
                  <td width="60%"><%= dispName %>
				    <% if (unAssigned) { %><font color="#FF0000">*</font><% } %>
				  </td>
                  <td width="40%" align="center"> 
                    <input type="button" name="Assign" value="Assign" onclick="location.href='AssignSelectionList.jsp?List=<%= listName %>'">
                  </td>
                </tr>
<%
	}
%>
              </table>
              <br>
              <table width="300" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr> 
                  <td width="50%" align="right"> 
                    <input type="submit" name="Submit" value="Submit"
					<% if (hasUnassigned) { %>onclick="return confirm('There are still unassigned lists, are you sure you want to continue?')"<% } %>>
                  </td>
                  <td width="50%"> 
                    <input type="button" name="Cancel" value="Cancel" onclick="location.href='ImportSTARS.jsp'">
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
