<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionList" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany" %>
<%@ page import="com.cannontech.stars.web.servlet.StarsAdmin" %>
<%
	Hashtable unassignedLists = (Hashtable) session.getAttribute(StarsAdmin.UNASSIGNED_LISTS);
	boolean hasUnassigned = false;
	
	LiteStarsEnergyCompany ec = SOAPServer.getEnergyCompany(user.getEnergyCompanyID());
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
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
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin">
			  <input type="hidden" name="action" value="ImportStarsData">
			  <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/ImportAccount.jsp">
              <table width="300" border="0" cellspacing="0" cellpadding="3" align="center" class="MainText">
<%
	for (int i = 0; i < StarsAdmin.LIST_NAMES.length; i++) {
		String listName = StarsAdmin.LIST_NAMES[i][0];
		String dispName = StarsAdmin.LIST_NAMES[i][1];
		
		if (unassignedLists.get(listName) == null) continue;
		
		boolean unAssigned = ((Boolean) unassignedLists.get(listName)).booleanValue();
		if (unAssigned) hasUnassigned = true;
		
		YukonSelectionList list = ec.getYukonSelectionList(listName);
%>
                <tr> 
                  <td width="50%"><%= dispName %>
				    <% if (unAssigned) { %><font color="#FF0000">*</font><% } %>
				  </td>
                  <td width="25%"> 
                    <div align="center"> 
                      <input type="button" name="Assign" value="Assign" onclick="location.href='AssignSelectionList.jsp?List=<%= listName %>'">
                    </div>
                  </td>
                  <td width="25%">
                    <div align="center"> 
<%
		if (list != null && list.getUserUpdateAvailable().equalsIgnoreCase("Y") ||
			listName.equals("ServiceCompany"))
		{
%> 
                      <input type="button" name="New" value="New" onclick="location.href='AssignSelectionList.jsp?List=<%= listName %>&New=true'">
<%
		}
%>
                    </div>
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
                    <input type="button" name="Cancel" value="Cancel" onclick="location.href='ImportAccount.jsp'">
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
