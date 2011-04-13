<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.cannontech.common.util.CtiUtilities" %>
<%@ page import="com.cannontech.core.dao.DaoFactory" %>
<%@ page import="com.cannontech.roles.consumer.ResidentialCustomerRole" %>
<%@ page import="com.cannontech.web.navigation.CtiNavObject" %>
<%@ page import="com.cannontech.common.inventory.HardwareType" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>

<%	if (!DaoFactory.getAuthDao().checkRoleProperty(lYukonUser, AdministratorRole.ADMIN_EDIT_ENERGY_COMPANY)
		|| ecSettings == null) {
		response.sendRedirect("../Operations.jsp"); return;
	}
%>
<%

    final Comparator<StarsEnrLMProgram> programComparator = StarsUtils.createLMProgramComparator();

	com.cannontech.database.data.lite.LiteYukonGroup[] custGroups = liteEC.getResidentialCustomerGroups();
	
	boolean canMembersChangeLogins = false; 
    boolean canMembersChangeRoutes = false;
    boolean isMember = false;
    
	Set<LiteStarsEnergyCompany> members = Sets.newHashSet(liteEC.getChildren());
	List<LiteStarsEnergyCompany> memberCandidates = new ArrayList<LiteStarsEnergyCompany>();
	if (DaoFactory.getAuthDao().checkRoleProperty(lYukonUser, AdministratorRole.ADMIN_MANAGE_MEMBERS)) {
		List<LiteStarsEnergyCompany> energyCompanies = StarsDatabaseCache.getInstance().getAllEnergyCompanies();
		for (int i = 0; i < energyCompanies.size(); i++) {
			LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) energyCompanies.get(i);
			if (ECUtils.isDefaultEnergyCompany(company)) continue;	// exclude default energy company
			if (members.contains(company)) continue;	// exclude existing members
			if (ECUtils.getAllDescendants(company).contains(liteEC)) continue;	// prevent circular reference
			memberCandidates.add(company);
		}
	}
	
	LiteStarsEnergyCompany parentEC = liteEC.getParent();
	//is this company a member company of someone else?
	if(parentEC != null)
    {
		isMember = true;
		CtiNavObject nav = (CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE);
		//see if this an admin of the parent energy company accessing member pages
		if(nav.isMemberECAdmin())
		{
			canMembersChangeRoutes = true;
			canMembersChangeLogins = true;
		}
		//if it isn't an admin, then check to see if this member has access specifically
		else
		{
			canMembersChangeRoutes = DaoFactory.getAuthDao().checkRoleProperty(lYukonUser, AdministratorRole.ADMIN_MEMBER_ROUTE_SELECT);
			canMembersChangeLogins = DaoFactory.getAuthDao().checkRoleProperty(lYukonUser, AdministratorRole.ADMIN_MEMBER_LOGIN_CNTRL);	
		}
	}
%>

<%@page import="com.google.common.collect.Sets"%>
<%@page import="com.cannontech.database.data.lite.LiteYukonGroup"%>
<%@page import="com.cannontech.stars.dr.hardware.model.SchedulableThermostatType"%><html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">

function confirmDeleteAppCat() {
	return confirm("If you delete an appliance category, all the programs and appliances under this category will also be deleted. Are you sure you want to continue?");
}

function confirmDeleteAllAppCats() {
	return confirm("Are you sure you want to delete all appliance categories, programs, and appliances under these categories?");
}

function confirmDeleteSubstation() {
	return confirm("Are you sure you want to delete the substation?");
}

function confirmDeleteAllSubstations() {
	return confirm("Are you sure you want to delete all substations?");
}

var memberLoginList = new Array(<%= memberCandidates.size() %>);
<%
	for (int i = 0; i < memberCandidates.size(); i++) {
		LiteStarsEnergyCompany candidate = (LiteStarsEnergyCompany) memberCandidates.get(i);
		List<Integer> loginIDs = candidate.getOperatorLoginIDs();
%>
memberLoginList[<%= i %>] = new Array(<%= loginIDs.size() %>);
<%
		for (int j = 0; j < loginIDs.size(); j++) {
			int loginID = ((Integer) loginIDs.get(j)).intValue();
			LiteYukonUser login = DaoFactory.getYukonUserDao().getLiteYukonUser(loginID);
%>
memberLoginList[<%= i %>][<%= j %>] = new Array(2);
memberLoginList[<%= i %>][<%= j %>][0] = <%= loginID %>;
memberLoginList[<%= i %>][<%= j %>][1] = '<%= login.getUsername() %>';
<%
		}
	}
%>

function updateMemberLoginList(form) {
	var idx = form.NewMember.selectedIndex - 1;
	for (i = form.NewMemberLogin.options.length - 1; i > 0; i--)
		form.NewMemberLogin.options.remove(i);
	if (idx >= 0 && idx < memberLoginList.length) {
		for (i = 0; i < memberLoginList[idx].length; i++) {
			form.NewMemberLogin.options.add(document.createElement("OPTION"));
			form.NewMemberLogin.options[i+1].value = memberLoginList[idx][i][0];
			form.NewMemberLogin.options[i+1].innerText = memberLoginList[idx][i][1];
		}
	}
}

function addMember(form) {
	if (form.NewMember.value == -1) {
		alert("Please select an energy company from the list");
		return;
	}
	form.MemberID.value = form.NewMember.value;
	form.LoginID.value = form.NewMemberLogin.value;
	form.action.value = "AddMemberEnergyCompany";
	form.submit();
}

function updateMember(form, idx) {
	form.MemberID.value = document.getElementsByName("Member")[idx].value;
	form.LoginID.value = document.getElementsByName("MemberLogin")[idx].value;
	form.submit();
}

function removeMember(form, idx) {
	if (!confirm("Are you sure you want to remove the member?"))
		return;
	form.MemberID.value = document.getElementsByName("Member")[idx].value;
	form.action.value = "RemoveMemberEnergyCompany";
	form.submit();
}

function removeAllMembers(form) {
	if (!confirm("Are you sure you want to remove all members?"))
		return;
	form.MemberID.value = -1;
	form.action.value = "RemoveMemberEnergyCompany";
	form.submit();
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
              <span class="TitleHeader">ADMINISTRATION</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
            <br>
            <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
              <tr> 
                <td class="HeaderCell">Edit Energy Company Settings</td>
              </tr>
              <tr> 
                <td height="118"> 
                  <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                    <tr> 
                      <td> 
                        <form name="form6" method="post" action="EnergyCompany.jsp">
                          <input type="hidden" name="action" value="init">
                          <b><font color="#0000FF">Energy Company:</font></b> 
                          <table width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <td> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                  <tr> 
                                    <td width="5%">&nbsp;</td>
                                    <td width="70%"><%= StringEscapeUtils.escapeHtml(energyCompany.getCompanyName()) %></td>
                                    <td width="25%"> 
                                      <input type="submit" name="Edit3" value="Edit">
                                    </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </form>
                      </td>
                    </tr>
<% if (!ECUtils.isSingleEnergyCompany(liteEC) && 
		(!isMember || (isMember && canMembersChangeRoutes))) { %>
                    <tr> 
                      <td><b><font color="#0000FF">Routes:</font></b> 
                        <table width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
                          <tr> 
                            <td> 
                              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <tr> 
                                  <td class="TableCell" width="5%">&nbsp;</td>
                                  <td class="TableCell" width="70%"> 
                                    <%
    List<LiteYukonPAObject> inheritedRoutes = null;
    if (liteEC.getParent() != null) {
        inheritedRoutes= liteEC.getParent().getAllRoutes();
    }
    List<LiteYukonPAObject> routes = liteEC.getRoutes();
    
    for (int i = 0; i < routes.size() && i < 3; i++) {
%>
                                    <%= routes.get(i).getPaoName() %><br>
                                    <%
    }
    if (routes.size() < 3 && inheritedRoutes != null) {
        for (int i = 0; i < inheritedRoutes.size() && i < 3 - routes.size(); i++) {
%>
                                    <%= inheritedRoutes.get(i).getPaoName() %> (Inherited)<br>
                                    <%
        }
    }
    if (routes.size() > 3) {
%>
                                    And more...<br>
                                    <%
    }
%>
                                  </td>
                                  <td width="25%" class="TableCell"> 
                                    <input type="button" name="Edit" value="Edit" onClick="location.href='Routes.jsp'">
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                        </table>
                        <br>
                      </td>
                    </tr>
<% } %>
                    <tr> 
                      <td> 
                        <form name="form3" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin">
                          <b><font color="#0000FF">Appliance Categories &amp; 
                          Published Programs:</font></b> 
                          <table width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <td> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="3">
                                  <input type="hidden" name="action" value="DeleteApplianceCategory">
                                  <input type="hidden" name="AppCatID" value="0">
                                  <%
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
%>
                                  <tr> 
                                    <td width="15%" class="TableCell" align="center"> 
                                      <%
		if (!category.getStarsWebConfig().getLogoLocation().equals("")) {
%>
                                      <img src="../../WebConfig/<%= StringEscapeUtils.escapeHtml(category.getStarsWebConfig().getLogoLocation()) %>"> 
                                      <%
		}
%>
                                    </td>
                                    <td width="60%" class="TableCell" valign="top"><%= StringEscapeUtils.escapeHtml(category.getDescription()) %> 
                                      <%
		if (!category.getStarsWebConfig().getAlternateDisplayName().equals( category.getDescription() )) {
%>
                                      (<%= StringEscapeUtils.escapeHtml(category.getStarsWebConfig().getAlternateDisplayName()) %>) 
                                      <%
		}
%>
                                      <table width="100%" border="0" cellspacing="3" cellpadding="0">
                                        <%
        final List<StarsEnrLMProgram> programList = Arrays.asList(category.getStarsEnrLMProgram());
        Collections.sort(programList, programComparator);                                
		for (int j = 0; j < programList.size() && j < 6; j++) {
			StarsEnrLMProgram program = programList.get(j);
			String[] dispNames = StarsUtils.splitString(program.getStarsWebConfig().getAlternateDisplayName(), ",");
			
			String progName = program.getYukonName();
			if (progName == null) progName = "&lt;Virtual Program&gt;";
			
			String progAlias = "";
			if (dispNames.length > 0) {
				if (dispNames[0].length() > 0) {
					progAlias += dispNames[0];
					if (dispNames.length > 1 && dispNames[1].length() > 0)
						progAlias += " / ";
				}
				if (dispNames.length > 1 && dispNames[1].length() > 0)
					progAlias += dispNames[1];
			}
			if (progAlias.length() > 0) progAlias = "(" + progAlias + ")";
%>
                                        <tr> 
                                          <td width="15" class="TableCell">&nbsp;</td>
                                          <td width="458" class="TableCell"><%= progName %> 
                                            <%= StringEscapeUtils.escapeHtml(progAlias) %></td>
                                        </tr>
                                        <%
		}
		if (programList.size() > 6) {
		    %>
		    <tr>
                <td width="15" class="TableCell">&nbsp;</td>
                <td width="458" class="TableCell">(List truncated.  Edit program to see full list of programs.)</td>
		    </tr>
		    <%
		}
%>
                                      </table>
                                    </td>
                                    <%
		if (category.getInherited()) {
%>
                                    <td width="25%" class="TableCell" colspan="2"> 
                                      <input type="button" name="Edit9" value="View" onClick="location.href='/spring/adminSetup/energyCompany/applianceCategory/view?applianceCategoryId=<%= category.getApplianceCategoryID() %>&ecId=<%= liteEC.getLiteID() %>'">
                                      (Inherited) </td>
                                    <%
		} else {
%>
                                    <td width="10%" class="TableCell"> 
                                      <input type="button" name="Edit" value="Edit" onclick="location.href='/spring/adminSetup/energyCompany/applianceCategory/view?applianceCategoryId=<%= category.getApplianceCategoryID() %>&ecId=<%= liteEC.getLiteID() %>'">
                                    </td>
                                    <td width="15%" class="TableCell"> 
                                      <input type="submit" name="Delete" value="Delete" onclick="this.form.AppCatID.value=<%= category.getApplianceCategoryID() %>; return confirmDeleteAppCat();">
                                    </td>
                                    <%
		}
%>
                                  </tr>
                                  <%
	}
%>
                                </table>
                              </td>
                            </tr>
                          </table>
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <td width="20%"> 
                                <input type="submit" name="DeleteAll" value="Delete All" onclick="this.form.AppCatID.value=-1; return confirmDeleteAllAppCats();">
                              </td>
                              <td width="80%"> 
                                <input type="button" name="New" value="New" onclick="location.href='/spring/adminSetup/energyCompany/applianceCategory/create?ecId=<%= liteEC.getLiteID() %>'">
                              </td>
                            </tr>
                          </table>
                        </form>
                      </td>
                    </tr>
                    <tr> 
                      <td><b><font color="#0000FF">Customer Selection Lists:</font></b> 
                        <table width="100%" border="1" cellspacing="0" cellpadding="1" align="center">
                          <tr> 
                            <td> 
                              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <%
	List<YukonSelectionList> userLists = StarsAdminUtil.getSelectionListsInUse(liteEC, user.getYukonUser());
	for (int i = 0; i < userLists.size(); i++) {
		com.cannontech.common.constants.YukonSelectionList cList = (com.cannontech.common.constants.YukonSelectionList) userLists.get(i);
		if (!cList.isUserUpdateAvailable()) continue;
%>
                                <tr> 
                                  <td class="TableCell" width="5%">&nbsp;</td>
                                  <td class="TableCell" width="30%"><a href="SelectionList.jsp?List=<%= cList.getListName() %>"><%= cList.getListName() %></a></td>
                                  <td class="TableCell" width="40%"> 
                                    <hr width="90%" align="left">
                                  </td>
                                  <td class="TableCell" width="25%"> 
                                    <input type="button" name="Edit" value="Edit" onclick="location.href='SelectionList.jsp?List=<%= cList.getListName() %>'">
                                    <% if (liteEC.getYukonSelectionList(cList.getListName(), false, false) == null) out.print("(Inherited)"); %>
                                  </td>
                                </tr>
                                <%
	}
%>
                              </table>
                            </td>
                          </tr>
                        </table>
                        <br>
                      </td>
                    </tr>

                    <%//TODO add some role property for settlement%>
					<tr> 
                      <td><b><font color="#0000FF">Settlement Setup:</font></b> 
                        <table width="100%" border="1" cellspacing="0" cellpadding="1" align="center">
                          <tr> 
                            <td> 
                              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <%
                               	YukonSelectionList list = liteEC.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SETTLEMENT_TYPE);
								for (int i = 0; i < list.getYukonListEntries().size(); i++) {
									YukonListEntry entry = (YukonListEntry) list.getYukonListEntries().get(i);
								%>
                                <tr> 
                                  <td class="TableCell" width="5%">&nbsp;</td>
                                  <td class="TableCell" width="30%"><%= entry.getEntryText() %></td>
                                  <td class="TableCell" width="40%"> 
                                    <hr width="90%" align="left">
                                  </td>
                                  <td class="TableCell" width="25%"> 
                                 	<input type="button" name="Edit" value="Edit" onclick="location.href='SettlementConfig.jsp?YukDefID=<%= entry.getYukonDefID() %>'">
                                  </td>
                                </tr>
                                <% } %>
                              </table>
                            </td>
                          </tr>
                        </table>
                        <br>
                      </td>
                    </tr>

					<tr>
                      <td>
                        <form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin">
                          <b><font color="#0000FF">Substations:</font></b> 
                          <table width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <td> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                  <input type="hidden" name="action" value="DeleteSubstation">
                                  <input type="hidden" name="SubID" value="0">
<%
		// The first substation is always "(none)"
		for (int i = 1; i < substations.getStarsSubstationCount(); i++) {
			StarsSubstation substation = substations.getStarsSubstation(i);
%>
                                  <tr> 
                                    <td class="TableCell" width="5%">&nbsp;</td>
                                    <td class="TableCell" width="70%"><%= StringEscapeUtils.escapeHtml(substation.getSubstationName()) %></td>
<%
			if (substation.getInherited()) {
%>
                                    <td width="25%" class="TableCell" colspan="2"> 
                                      <input type="button" name="Edit" value="View" onclick="location.href = 'Substation.jsp?Sub=<%= i %>'">
                                      (Inherited) </td>
                                    <%
			} else {
%>
                                    <td width="10%" class="TableCell"> 
                                      <input type="button" name="Edit" value="Edit" onclick="location.href = 'Substation.jsp?Sub=<%= i %>'">
                                    </td>
                                    <td width="15%" class="TableCell"> 
                                      <input type="submit" name="Delete" value="Delete" onclick="this.form.SubID.value=<%= substation.getSubstationID() %>; return confirmDeleteSubstation();">
                                    </td>
<%
			}
%>
                                  </tr>
<%
		}
%>
                                </table>
                              </td>
                            </tr>
                          </table>
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <td width="20%"> 
                                <input type="submit" name="DeleteAll" value="Delete All" onclick="this.form.SubID.value=-1; return confirmDeleteAllSubstations();">
                              </td>
                              <td width="80%"> 
                                <input type="button" name="New" value="New" onclick="location.href = 'Substation.jsp?Sub=<%= substations.getStarsSubstationCount() %>'">
                              </td>
                            </tr>
                          </table>
                        </form>
                      </td>
                    </tr>
<%
	if (DaoFactory.getAuthDao().checkRoleProperty(lYukonUser, ConsumerInfoRole.CONSUMER_INFO_HARDWARES_THERMOSTAT) ||
		custGroups.length > 0 && !CtiUtilities.isFalse(DaoFactory.getRoleDao().getRolePropValueGroup(custGroups[0], ResidentialCustomerRole.CONSUMER_INFO_HARDWARES_THERMOSTAT, "false")))
	{
%>
                    <tr> 
                      <td><b><font color="#0000FF">Default Thermostat Schedule:</font></b> 
                        <table width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
                          <tr> 
                            <td> 
                              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
<%
        List<HardwareType> hardwareTypeList = liteEC.getAvailableThermostatTypes();
		for (HardwareType hardwareType : hardwareTypeList) {

			SchedulableThermostatType schedulableThermostatType = SchedulableThermostatType.getByHardwareType(hardwareType);
			
%>
                                <tr> 
                                  <td width="5%">&nbsp;</td>
                                  <td width="70%">
                                    <cti:msg key="<%= schedulableThermostatType.getHardwareType().getDisplayKey() %>"></cti:msg>
                                  </td>
                                  <td width="25%"> 
                                    <input type="button" name="Edit2" value="Edit" onclick="location.href = 'ThermSchedule.jsp?type=<%= schedulableThermostatType.toString() %>'">
                                  </td>
                                </tr>
                                <%
		}
%>
                              </table>
                            </td>
                          </tr>
                        </table>
                        <br>
                      </td>
                    </tr>
<% } %>                   
                    <cti:checkProperty propertyid="<%= AdministratorRole.ADMIN_MANAGE_MEMBERS %>"> 
                    <tr> 
                      <td> 
                        <form name="form2" method="post" action="<%= request.getContextPath() %>/servlet/StarsAdmin">
                          <b><font color="#0000FF">Member Energy Companies:</font></b> 
                          <table width="100%" border="1" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <td> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                  <input type="hidden" name="action" value="UpdateMemberEnergyCompany">
                                  <input type="hidden" name="MemberID" value="-1">
                                  <input type="hidden" name="LoginID" value="-1">
                                  <tr> 
                                    <td class="HeaderCell" width="5%">&nbsp;</td>
                                    <td class="HeaderCell" width="30%">Company 
                                      Name</td>
                                    <td class="HeaderCell" width="40%">Member 
                                      Login </td>
                                    <td width="10%" class="HeaderCell">&nbsp;</td>
                                    <td width="15%" class="HeaderCell">&nbsp;</td>
                                  </tr>
                                  <%
	List<Integer> memberLoginIDs = liteEC.getMemberLoginIDs();
    int memberIndex = 0;
	for (LiteStarsEnergyCompany member : liteEC.getChildren()) {
		
%>
                                  <tr> 
                                    <input type="hidden" name="Member" value="<%= member.getLiteID() %>">
                                    <td class="TableCell" width="5%">&nbsp;</td>
                                    <td class="TableCell" width="30%"><%= member.getName() %></td>
                                    <td class="TableCell" width="40%"> 
                                      <select name="MemberLogin">
                                        <option value="-1">(none)</option>
                                        <%
		for (int j = 0; j < member.getOperatorLoginIDs().size(); j++) {
			Integer loginID = (Integer) member.getOperatorLoginIDs().get(j);
			LiteYukonUser login = DaoFactory.getYukonUserDao().getLiteYukonUser(loginID.intValue());
			if (login == null) continue;
			String selected = memberLoginIDs.contains(loginID)? "selected" : "";
%>
                                        <option value="<%= loginID %>" <%= selected %>><%= login.getUsername() %></option>
                                        <%
		}
%>
                                      </select>
                                    </td>
                                    <td width="10%" class="TableCell"> 
                                      <input type="button" name="Update" value="Update" onclick="updateMember(this.form, <%= memberIndex %>)">
                                    </td>
                                    <td width="15%" class="TableCell"> 
                                      <input type="button" name="Remove" value="Remove" onclick="removeMember(this.form, <%= memberIndex %>)">
                                    </td>
                                  </tr>
                                  <%
                                  memberIndex++;
	}
%>
                                  <tr> 
                                    <td class="TableCell" colspan="5"> 
                                      <hr>
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td class="TableCell" width="5%">&nbsp;</td>
                                    <td class="TableCell" width="30%"> 
                                      <select name="NewMember" onchange="updateMemberLoginList(this.form)">
                                        <option value="-1">(Select)</option>
                                        <%
	for (int i = 0; i < memberCandidates.size(); i++) {
		LiteStarsEnergyCompany candidate = (LiteStarsEnergyCompany) memberCandidates.get(i);
%>
                                        <option value="<%= candidate.getLiteID() %>"><%= candidate.getName() %></option>
                                        <%
	}
%>
                                      </select>
                                    </td>
                                    <td class="TableCell" width="40%"> 
                                      <select name="NewMemberLogin">
                                        <option value="-1">(Select)</option>
                                      </select>
                                    </td>
                                    <td width="10%" class="TableCell"> 
                                      <input type="button" name="Add" value="Add" onclick="addMember(this.form)">
                                    </td>
                                    <td width="15%" class="TableCell">&nbsp; </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
                            <tr> 
                              <td width="20%"> 
                                <input type="button" name="RemoveAll" value="Remove All" onclick="removeAllMembers(this.form)">
                              </td>
                              <td width="80%">&nbsp; </td>
                            </tr>
                          </table>
                        </form>
                      </td>
                    </tr>
                    </cti:checkProperty> 
                  </table>
                </td>
              </tr>
            </table>
            <p>&nbsp;</p>
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
