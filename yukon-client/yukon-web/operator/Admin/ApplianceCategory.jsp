<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.servlet.LCConnectionServlet" %>
<%@ page import="com.cannontech.web.loadcontrol.LoadcontrolCache" %>
<%@ page import="com.cannontech.loadcontrol.data.LMProgramDirect" %>
<%
	StarsApplianceCategory category = null;
	int catIdx = Integer.parseInt( request.getParameter("Category") );
	if (catIdx < categories.getStarsApplianceCategoryCount())
		category = categories.getStarsApplianceCategory(catIdx);
	else {
		category = new StarsApplianceCategory();
		category.setApplianceCategoryID(-1);
		category.setDescription("");
		StarsWebConfig cfg = new StarsWebConfig();
		cfg.setLogoLocation("AC.gif");
		cfg.setAlternateDisplayName("");
		cfg.setDescription("");
		category.setStarsWebConfig( cfg );
	}

    LCConnectionServlet cs = (LCConnectionServlet) application.getAttribute(LCConnectionServlet.SERVLET_CONTEXT_ID);
    LoadcontrolCache cache = cs.getCache();

    // list to put this customers programs in, contains LMProgramDirect objects
    ArrayList ourPrograms = new ArrayList();

    long[] programIDs = com.cannontech.database.db.web.LMDirectOperatorList.getProgramIDs( user.getUserID() );       
    java.util.Arrays.sort(programIDs);
    LMProgramDirect[] allPrograms = cache.getDirectPrograms(); 

    // Match our program ids with the actual programs in the cache so we know what to display
    for( int i = 0; i < allPrograms.length; i++ )
    {
        long id = allPrograms[i].getYukonID().longValue();
        if( java.util.Arrays.binarySearch(programIDs, id ) >= 0 )
        {
            // found one
            ourPrograms.add(allPrograms[i]);
        }
    }

    // list to put available programs in, contains LMProgramDirect objects
    ArrayList availPrograms = new ArrayList();
	for (int i = 0; i < ourPrograms.size(); i++) {
		int id = ((LMProgramDirect) ourPrograms.get(i)).getYukonID().intValue();
		boolean assigned = false;
		for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
			StarsApplianceCategory appCat = categories.getStarsApplianceCategory(j);
			for (int k = 0; k < appCat.getStarsEnrLMProgramCount(); k++) {
				if (appCat.getStarsEnrLMProgram(k).getProgramID() == id) {
					assigned = true;
					break;
				}
			}
			if (assigned) break;
		}
		if (!assigned) availPrograms.add( ourPrograms.get(i) );
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">
function changeIcon(form) {
	form.CategoryIcon.style.display = (form.IconName.value == "") ? "none" : "";
	form.CategoryIcon.src = "../../Images/Icons/" + form.IconName.value;
}

function changeProgramIcons(form) {
	form.IconSmall.style.visibility = (form.IconNameSmall.value == "") ? "hidden" : "visible";
	form.IconSmall.src = "../../Images/Icons/" + form.IconNameSmall.value;
	form.IconSavings.style.visibility = (form.IconNameSavings.value == "") ? "hidden" : "visible";
	form.IconSavings.src = "../../Images/Icons/" + form.IconNameSavings.value;
	form.IconControl.style.visibility = (form.IconNameControl.value == "") ? "hidden" : "visible";
	form.IconControl.src = "../../Images/Icons/" + form.IconNameControl.value;
	form.IconEnvrn.style.visibility = (form.IconNameEnvrn.value == "") ? "hidden" : "visible";
	form.IconEnvrn.src = "../../Images/Icons/" + form.IconNameEnvrn.value;
}

function addProgram(form) {
	var assgnProgList = document.getElementById("ProgramsAssigned");
	var availProgList = document.getElementById("ProgramsAvailable");
	if (availProgList.value > 0) {
		var oOption = availProgList.options[availProgList.selectedIndex];
		availProgList.remove(availProgList.selectedIndex);
		if (assgnProgList.options[0].value == 0) assgnProgList.remove(0);
		assgnProgList.add(oOption);
	}
	if (availProgList.options.length == 0) {
		var emptyOption = document.createElement("OPTION");
		availProgList.add(emptyOption);
		emptyOption.innerText = "<No Program Available>";
		emptyOption.value = "0";
	}
	showProgramConfig(form);
}

var removeWarned = true;

function removeProgram(form) {
	if (!removeWarned) {
		removeWarned = true;
		alert('If you remove a program and click "Submit", all customers currently enrolled in this program will be unenrolled!');
	}
	
	var assgnProgList = document.getElementById("ProgramsAssigned");
	var availProgList = document.getElementById("ProgramsAvailable");
	if (assgnProgList.value > 0) {
		var oOption = assgnProgList.options[assgnProgList.selectedIndex];
		assgnProgList.remove(assgnProgList.selectedIndex);
		if (availProgList.options[0].value == 0) availProgList.remove(0);
		availProgList.add(oOption);
	}
	if (assgnProgList.options.length == 0) {
		var emptyOption = document.createElement("OPTION");
		assgnProgList.add(emptyOption);
		emptyOption.innerText = "<No Program Assigned>";
		emptyOption.value = "0";
	}
	
	curProgID = 0;
	clearProgramConfig(form);
}

function sameAsName(form, checked) {
	form.SameAsName.checked = checked;
	form.DispName.disabled = checked;
}

function sameAsProgName(form, checked) {
	form.SameAsProgName.checked = checked;
	form.ProgDispName.disabled = checked;
}

function sameAsDispName(form, checked) {
	form.SameAsDispName.checked = checked;
	form.ProgShortName.disabled = checked;
}

function init() {
	sameAsName(document.form1, <%= category.getStarsWebConfig().getAlternateDisplayName().equals(category.getDescription()) %>);
	removeProgram(document.form1);
	addProgram(document.form1);
	removeWarned = false;
}

var progID = new Array();
var progName = new Array();
var dispName = new Array();
var shortName = new Array();
var description = new Array();
var ctrlOdds = new Array();
var iconNameSmall = new Array();
var iconNameSavings = new Array();
var iconNameControl = new Array();
var iconNameEnvrn = new Array();

var idx = 0;
<%
	for (int i = 0; i < category.getStarsEnrLMProgramCount(); i++) {
		StarsEnrLMProgram program = category.getStarsEnrLMProgram(i);
		StarsWebConfig cfg = program.getStarsWebConfig();
		String[] dispNames = cfg.getAlternateDisplayName().split(",");
		String[] imgNames = ServletUtils.getImageNames( cfg.getLogoLocation() );
%>
	progID[idx] = <%= program.getProgramID() %>;
	progName[idx] = "<%= program.getProgramName() %>";
	dispName[idx] = "<%= (dispNames.length > 0)? dispNames[0] : "" %>";
	shortName[idx] = "<%= (dispNames.length > 1)? dispNames[1] : "" %>";
	description[idx] = "<%= cfg.getDescription().replaceAll("\"", "&quot;") %>".replace(/&quot;/g, '"').replace(/<br>/g, '\r\n');
	ctrlOdds[idx] = <%= (program.getChanceOfControl() == null) ? 0 : program.getChanceOfControl().getEntryID() %>;
	iconNameSmall[idx] = "<%= imgNames[1] %>";
	iconNameSavings[idx] = "<%= imgNames[2] %>";
	iconNameControl[idx] = "<%= imgNames[3] %>";
	iconNameEnvrn[idx] = "<%= imgNames[4] %>";
	idx++;
<%
	}
	for (int i = 0; i < availPrograms.size(); i++) {
		LMProgramDirect program = (LMProgramDirect) availPrograms.get(i);
%>
	progID[idx] = <%= program.getYukonID() %>;
	progName[idx] = "<%= program.getYukonName() %>";
	dispName[idx] = "";
	shortName[idx] = "";
	description[idx] = "<%= ServerUtils.forceNotNone(program.getYukonDescription()).replaceAll("\"", "&quot;") %>".replace(/&quot;/g, '"');
	ctrlOdds[idx] = 0;
	iconNameSmall[idx] = "";
	iconNameSavings[idx] = "";
	iconNameControl[idx] = "";
	iconNameEnvrn[idx] = "";
	idx++;
<%	} %>

var curProgID = 0;

function saveProgramConfig(form) {
	if (curProgID > 0) {
		for (idx = 0; idx < progID.length; idx++)
			if (progID[idx] == curProgID) break;
		if (idx >= progID.length) return;
		
		if (form.SameAsProgName.checked)
			dispName[idx] = "";
		else
			dispName[idx] = form.ProgDispName.value;
		if (form.SameAsDispName.checked)
			shortName[idx] = "";
		else
			shortName[idx] = form.ProgShortName.value;
		description[idx] = form.ProgDescription.value;
		ctrlOdds[idx] = form.ProgCtrlOdds.value;
		iconNameSmall[idx] = form.IconNameSmall.value;
		iconNameSavings[idx] = form.IconNameSavings.value;
		iconNameControl[idx] = form.IconNameControl.value;
		iconNameEnvrn[idx] = form.IconNameEnvrn.value;
	}
}

function clearProgramConfig(form) {
	document.getElementById("ProgName").innerText = "";
	form.ProgDispName.value = "";
	sameAsProgName(form, false);
	sameAsDispName(form, false);
	form.ProgShortName.value = "";
	form.ProgDescription.value = "";
	form.ProgCtrlOdds.selectedIndex = 0;
	form.IconNameSmall.value = "";
	form.IconNameSavings.value = "";
	form.IconNameControl.value = "";
	form.IconNameEnvrn.value = "";
	form.IconSmall.style.visibility = "hidden";
	form.IconSavings.style.visibility = "hidden";
	form.IconControl.style.visibility = "hidden";
	form.IconEnvrn.style.visibility = "hidden";
}

function showProgramConfig(form) {
	var newProgID = form.ProgramsAssigned.value;
	if (newProgID == curProgID) return;
	
	saveProgramConfig(form);
	
	if (newProgID > 0) {
		curProgID = newProgID;
		for (idx = 0; idx < progID.length; idx++)
			if (progID[idx] == curProgID) break;
		if (idx < progID.length) {
			document.getElementById("ProgName").innerText = progName[idx];
			form.ProgDispName.value = dispName[idx];
			sameAsProgName(form, dispName[idx] == "");
			form.ProgShortName.value = shortName[idx];
			sameAsDispName(form, shortName[idx] == "");
			form.ProgDescription.value = description[idx];
			form.ProgCtrlOdds.value = ctrlOdds[idx];
			form.IconNameSmall.value = iconNameSmall[idx];
			form.IconNameSavings.value = iconNameSavings[idx];
			form.IconNameControl.value = iconNameControl[idx];
			form.IconNameEnvrn.value = iconNameEnvrn[idx];
			changeProgramIcons(form);
		}
		else
			clearProgramConfig(form);
	}
	else {
		curProgID = 0;
		clearProgramConfig(form);
	}
}

function prepareSubmit(form) {
	saveProgramConfig(form);
	
	var assgnProgList = document.getElementById("ProgramsAssigned");
	if (assgnProgList.options[0].value > 0) {
		for (i = 0; i < assgnProgList.options.length; i++) {
			var programID = assgnProgList.options[i].value;
			var html = '<input type="hidden" name="ProgIDs" value="' + programID + '">';
			form.insertAdjacentHTML("beforeEnd", html);
			
			for (idx = 0; idx < progID.length; idx++)
				if (progID[idx] == programID) break;
			html = '<input type="hidden" name="ProgDispNames" value="' + dispName[idx] + '">';
			form.insertAdjacentHTML("beforeEnd", html);
			html = '<input type="hidden" name="ProgShortNames" value="' + shortName[idx] + '">';
			form.insertAdjacentHTML("beforeEnd", html);
			html = '<input type="hidden" name="ProgDescriptions" value="' + description[idx].replace(/"/g, "&quot;") + '">';
			form.insertAdjacentHTML("beforeEnd", html);
			html = '<input type="hidden" name="ProgChanceOfCtrls" value="' + ctrlOdds[idx] + '">';
			form.insertAdjacentHTML("beforeEnd", html);
			var iconNames = iconNameSmall[idx] + "," + iconNameSavings[idx] + "," + iconNameControl[idx] + "," + iconNameEnvrn[idx];
			html = '<input type="hidden" name="ProgIconNames" value="' + iconNames + '">';
			form.insertAdjacentHTML("beforeEnd", html);
		}
	}
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="init()">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="AdminImage.jpg">&nbsp;</td>
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
              <span class="TitleHeader">ADMINISTRATION - APPLIANCE CATEGORY</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin" onsubmit="prepareSubmit(this)">
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td class="HeaderCell">Edit Appliance Category</td>
                </tr>
                <tr> 
                  <td height="252"> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="5">
                      <input type="hidden" name="action" value="UpdateApplianceCategory">
                      <input type="hidden" name="AppCatID" value="<%= category.getApplianceCategoryID() %>">
                      <tr> 
                        <td width="18%" align="right" class="TableCell">Category 
                          Name:</td>
                        <td width="82%" class="TableCell"> 
                          <input type="text" name="Name" value="<%= category.getDescription() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="18%" align="right" class="TableCell">Display 
                          Name:</td>
                        <td width="82%" class="TableCell"> 
                          <input type="text" name="DispName" value="<%= category.getStarsWebConfig().getAlternateDisplayName() %>">
                          <input type="checkbox" name="SameAsName" value="true" onClick="sameAsName(this.form, this.checked)">
                          Same as category name </td>
                      </tr>
                      <tr> 
                        <td width="18%" align="right" class="TableCell" height="7">Type:</td>
                        <td width="82%" class="TableCell" valign="middle" height="7"> 
                          <select name="Category">
<%
	StarsCustSelectionList categoryList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY );
	for (int i = 0; i < categoryList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = categoryList.getStarsSelectionListEntry(i);
		String selectedStr = (entry.getEntryID() == category.getCategoryID()) ? "selected" : "";
%>
                            <option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%	} %>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="18%" align="right" class="TableCell">Icon:</td>
                        <td width="82%" class="TableCell"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td width="50%"> 
                                <input type="text" name="IconName" size="20" value="<%= category.getStarsWebConfig().getLogoLocation() %>">
                                <input type="button" name="Preview" value="Preview" onClick="changeIcon(this.form)">
                              </td>
                              <td width="50%"> <img id="CategoryIcon" src="../../Images/Icons/<%= category.getStarsWebConfig().getLogoLocation() %>" align="middle"></td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td width="18%" align="right" class="TableCell">Description:</td>
                        <td width="82%" class="TableCell"> 
                          <textarea name="Description" rows="4" cols="50" wrap="soft"><%= category.getStarsWebConfig().getDescription().replaceAll("<br>", "\r\n") %></textarea>
                        </td>
                      </tr>
                      <tr> 
                        <td width="18%" align="right" class="TableCell"> Programs:</td>
                        <td width="82%" class="TableCell"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                            <tr> 
                              <td> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                  <tr> 
                                    <td width="175" valign="top"> Assigned Programs:<br>
                                      <select id="ProgramsAssigned" name="ProgramsAssigned" size="5" style="width:150" onclick="showProgramConfig(this.form)">
<%
	for (int i = 0; i < category.getStarsEnrLMProgramCount(); i++) {
		StarsEnrLMProgram program = category.getStarsEnrLMProgram(i);
%>
                                        <option value="<%= program.getProgramID() %>"><%= program.getProgramName() %></option>
<%	} %>
                                      </select>
                                      <br>
                                      <font color="#0000FF">Click on a program 
                                      to show and update its configuration.</font></td>
                                    <td width="50" align="center"> 
                                      <p> 
                                        <input type="button" id="AddButton" name="Add" value="<<" onclick="addProgram(this.form)">
                                        <br>
                                        <input type="button" id="RemoveButton" name="Remove" value=">>" onclick="removeProgram(this.form)">
                                      </p>
                                      <p>&nbsp; </p>
                                    </td>
                                    <td width="175" valign="top"> Available Programs<br>
                                      <select id="ProgramsAvailable" name="ProgramsAvailable" size="5" style="width:150">
<%
	for (int i = 0; i < availPrograms.size(); i++) {
		LMProgramDirect program = (LMProgramDirect) availPrograms.get(i);
%>
                                        <option value="<%= program.getYukonID() %>"><%= program.getYukonName() %></option>
<%	} %>
                                      </select>
                                    </td>
                                    <td align="center">&nbsp;</td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr align="center"> 
                        <td colspan="2" class="TableCell" height="2">
                          <hr>
                        </td>
                      </tr>
                      <tr> 
                        <td width="18%" class="TableCell" align="right">Program 
                          Configuration:</td>
                        <td width="82%" class="TableCell"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                            <tr> 
                              <td width="16%">Program Name:</td>
                              <td width="84%"><span id="ProgName">&nbsp;</span></td>
                            </tr>
                            <tr> 
                              <td width="16%">Display Name:</td>
                              <td width="84%"> 
                                <input type="text" name="ProgDispName" size="20">
                                <input type="checkbox" name="SameAsProgName" value="true" onclick="sameAsProgName(this.form, this.checked)">
                                Same as program name</td>
                            </tr>
                            <tr> 
                              <td width="16%">Short Name:</td>
                              <td width="84%"> 
                                <input type="text" name="ProgShortName" size="20">
                                <input type="checkbox" name="SameAsDispName" value="true" onClick="sameAsDispName(this.form, this.checked)">
                                Same as display name </td>
                            </tr>
                            <tr> 
                              <td width="16%">Description:</td>
                              <td width="84%"> 
                                <textarea name="ProgDescription" rows="4" wrap="soft" cols="40"></textarea>
                              </td>
                            </tr>
                            <tr> 
                              <td width="16%">Chance of Control:</td>
                              <td width="84%"> 
                                <select name="ProgCtrlOdds">
                                  <%
	StarsCustSelectionList ctrlOddsList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_CHANCE_OF_CONTROL );
	if (ctrlOddsList == null || ctrlOddsList.getStarsSelectionListEntry(0).getEntryID() > 0) {
%>
                                  <option value="0">(none)</option>
                                  <%
	}
	if (ctrlOddsList != null) {
		for (int i = 0; i < ctrlOddsList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = ctrlOddsList.getStarsSelectionListEntry(i);
%>
                                  <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
                                  <%		}
	}
%>
                                </select>
                              </td>
                            </tr>
                            <tr> 
                              <td width="16%">Program Description Icons:</td>
                              <td width="84%">
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                  <tr>
                                    <td width="60%"> 
                                      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                        <tr> 
                                          <td width="30%" height="40">Small: </td>
                                          <td width="70%" height="40"> 
                                            <input type="text" name="IconNameSmall" size="20">
                                          </td>
                                        </tr>
                                        <tr> 
                                          <td width="30%" height="40">Savings: 
                                          </td>
                                          <td width="70%" height="40"> 
                                            <input type="text" name="IconNameSavings" size="20">
                                          </td>
                                        </tr>
                                        <tr> 
                                          <td width="30%" height="40">Control%:</td>
                                          <td width="70%" height="40"> 
                                            <input type="text" name="IconNameControl" size="20">
                                          </td>
                                        </tr>
                                        <tr> 
                                          <td width="30%" height="40">Environment:</td>
                                          <td width="70%" height="40"> 
                                            <input type="text" name="IconNameEnvrn" size="20">
                                          </td>
                                        </tr>
                                      </table>
</td>
                                    <td width="40%"> 
                                      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                        <tr> 
                                          <td width="40%" rowspan="4" height="40"> 
                                            <input type="button" name="Preview2" value="Preview" onClick="changeProgramIcons(this.form)">
                                          </td>
                                          <td width="60%" height="40"><img id="IconSmall" src="../../../Images/Icons/ACSm.gif" style="visibility:hidden"></td>
                                        </tr>
                                        <tr> 
                                          <td width="60%" height="40"><img id="IconSavings" src="../../../Images/Icons/$$Sm.gif" style="visibility:hidden"></td>
                                        </tr>
                                        <tr> 
                                          <td width="60%" height="40"><img id="IconControl" src="../../../Images/Icons/HalfSm.gif" style="visibility:hidden"></td>
                                        </tr>
                                        <tr> 
                                          <td width="60%" height="40"><img id="IconEnvrn" src="../../../Images/Icons/Tree2Sm.gif" style="visibility:hidden"></td>
                                        </tr>
                                      </table>
                                    </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
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
                    <input type="button" name="Done" value="Done" onclick="location.href='AdminTest.jsp'">
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
