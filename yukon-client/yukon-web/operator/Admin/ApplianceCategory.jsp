<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.cache.functions.PAOFuncs" %>
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
		cfg.setLogoLocation("yukon/Icons/Load.gif");
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
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function changeIcon(form) {
	form.CategoryIcon.style.display = (form.IconName.value == "") ? "none" : "";
	form.CategoryIcon.src = "../../WebConfig/" + form.IconName.value;
}

function changeProgramIcons(form) {
	form.IconSavings.style.visibility = (form.IconNameSavings.value == "") ? "hidden" : "visible";
	form.IconSavings.src = "../../WebConfig/yukon/Icons/" + form.IconNameSavings.value;
	form.IconControl.style.visibility = (form.IconNameControl.value == "") ? "hidden" : "visible";
	form.IconControl.src = "../../WebConfig/yukon/Icons/" + form.IconNameControl.value;
	form.IconEnvrn.style.visibility = (form.IconNameEnvrn.value == "") ? "hidden" : "visible";
	form.IconEnvrn.src = "../../WebConfig/yukon/Icons/" + form.IconNameEnvrn.value;
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

var progID = new Array();
var deviceID = new Array();
var progName = new Array();
var dispName = new Array();
var shortName = new Array();
var description = new Array();
var descFile = new Array();
var ctrlOdds = new Array();
var iconNameSavings = new Array();
var iconNameControl = new Array();
var iconNameEnvrn = new Array();
<%
	for (int i = 0; i < category.getStarsEnrLMProgramCount(); i++) {
		StarsEnrLMProgram program = category.getStarsEnrLMProgram(i);
		String progName = "(none)";
		if (program.getDeviceID() > 0) progName = PAOFuncs.getYukonPAOName(program.getDeviceID());
		StarsWebConfig cfg = program.getStarsWebConfig();
		String[] dispNames = ServerUtils.splitString(cfg.getAlternateDisplayName(), ",");
		String[] imgNames = ServletUtils.getImageNames( cfg.getLogoLocation() );
%>
	progID[<%= i %>] = <%= program.getProgramID() %>;
	deviceID[<%= i %>] = <%= program.getDeviceID() %>;
	progName[<%= i %>] = "<%= progName %>";
	dispName[<%= i %>] = "<%= (dispNames.length > 0)? dispNames[0].replaceAll("\"", "&quot;") : "" %>".replace(/&quot;/g, '"').replace(/&/g, '&amp;');
	shortName[<%= i %>] = "<%= (dispNames.length > 1)? dispNames[1].replaceAll("\"", "&quot;") : "" %>".replace(/&quot;/g, '"').replace(/&/g, '&amp;');
	description[<%= i %>] = "<%= cfg.getDescription().replaceAll("\"", "&quot;") %>".replace(/&quot;/g, '"').replace(/&/g, '&amp;').replace(/<br>/g, '\r\n');
	descFile[<%= i %>] = "<%= cfg.getURL() %>";
	ctrlOdds[<%= i %>] = <%= (program.getChanceOfControl() == null) ? 0 : program.getChanceOfControl().getEntryID() %>;
	iconNameSavings[<%= i %>] = "<%= imgNames[0] %>";
	iconNameControl[<%= i %>] = "<%= imgNames[1] %>";
	iconNameEnvrn[<%= i %>] = "<%= imgNames[2] %>";
<%
	}
%>

var yukonProgID = new Array();
var yukonDeviceID = new Array();
var yukonProgName = new Array();
var yukonDescription = new Array();
	yukonProgID[0] = -1;
	yukonDeviceID[0] = 0;
	yukonProgName[0] = "(none)";
	yukonDescription[0] = "";
<%
	for (int i = 0; i < availPrograms.size(); i++) {
		LMProgramDirect program = (LMProgramDirect) availPrograms.get(i);
%>
	yukonProgID[<%= i+1 %>] = -1;
	yukonDeviceID[<%= i+1 %>] = <%= program.getYukonID() %>;
	yukonProgName[<%= i+1 %>] = "<%= program.getYukonName() %>";
	yukonDescription[<%= i+1 %>] = "<%= ServerUtils.forceNotNone(program.getYukonDescription()).replaceAll("\"", "&quot;") %>".replace(/&quot;/g, '"');
<%	} %>

var nextProgIdx = <%= category.getStarsEnrLMProgramCount() %>;
var nextYukonIdx = <%= availPrograms.size() %>;
var curProgIdx = -1;

function newProgramEntry(yukonIdx) {
	progID[nextProgIdx] = yukonProgID[yukonIdx];
	deviceID[nextProgIdx] = yukonDeviceID[yukonIdx];
	progName[nextProgIdx] = yukonProgName[yukonIdx];
	if (yukonDeviceID[yukonIdx] > 0)
		dispName[nextProgIdx] = "";
	else
		dispName[nextProgIdx] = "<New virtual program>";
	shortName[nextProgIdx] = "";
	description[nextProgIdx] = yukonDescription[yukonIdx];
	descFile[nextProgIdx] = "";
	ctrlOdds[nextProgIdx] = 0;
	iconNameSavings[nextProgIdx] = "";
	iconNameControl[nextProgIdx] = "";
	iconNameEnvrn[nextProgIdx] = "";
	return nextProgIdx++;
}

function newYukonEntry(progIdx) {
	yukonProgID[nextYukonIdx] = progID[progIdx];
	yukonDeviceID[nextYukonIdx] = deviceID[progIdx];
	yukonProgName[nextYukonIdx] = progName[progIdx];
	yukonDescription[nextYukonIdx] = description[progIdx];
	return nextYukonIdx++;
}

function addProgram(form) {
	var assgnProgList = document.getElementById("ProgramsAssigned");
	var availProgList = document.getElementById("ProgramsAvailable");
	
	if (availProgList.selectedIndex >= 0) {
		if (availProgList.value > 0)
			availProgList.remove(availProgList.selectedIndex);
		
		var idx = newProgramEntry(availProgList.value);
		
		var oOption = document.createElement("OPTION");
		assgnProgList.add(oOption);
		oOption.innerText = progName[idx];
		if (deviceID[idx] == 0) oOption.innerText = "*" + dispName[idx];
		oOption.value = idx;
		
		assgnProgList.selectedIndex = assgnProgList.length - 1;
		showProgramConfig(form);
	}
}

var removeWarned = true;

function removeProgram(form) {
	if (!removeWarned) {
		removeWarned = true;
		alert('If you remove a program and click "Submit", all customers currently enrolled in this program will be unenrolled!');
	}
	
	var assgnProgList = document.getElementById("ProgramsAssigned");
	var availProgList = document.getElementById("ProgramsAvailable");
	
	if (assgnProgList.selectedIndex >= 0) {
		assgnProgList.remove(assgnProgList.selectedIndex);
		
		if (deviceID[assgnProgList.value] > 0) {
			var idx = newYukonEntry(assgnProgList.value);
			
			var oOption = document.createElement("OPTION");
			oOption.innerText = yukonProgName[idx];
			oOption.value = idx;
			availProgList.options.add(oOption);
		}
		
		curProgIdx = -1;
		clearProgramConfig(form);
	}
}

function saveProgramConfig(form) {
	if (curProgIdx >= 0) {
		if (form.SameAsProgName.checked)
			dispName[curProgIdx] = "";
		else
			dispName[curProgIdx] = form.ProgDispName.value;
		
		if (form.SameAsDispName.checked)
			shortName[curProgIdx] = "";
		else
			shortName[curProgIdx] = form.ProgShortName.value;
		
		description[curProgIdx] = form.ProgDescription.value;
		descFile[curProgIdx] = form.ProgDescFile.value;
		ctrlOdds[curProgIdx] = form.ProgCtrlOdds.value;
		iconNameSavings[curProgIdx] = form.IconNameSavings.value;
		iconNameControl[curProgIdx] = form.IconNameControl.value;
		iconNameEnvrn[curProgIdx] = form.IconNameEnvrn.value;
		
		if (deviceID[curProgIdx] == 0) {
			if (dispName[curProgIdx] == "") {
				alert("The display name of a virtual program cannot be empty");
				return false;
			}
			else {
				// Set the option text to be the display name, add a "*" in front to indicate it is a virtual program
				for (i = 0; i < form.ProgramsAssigned.options.length; i++) {
					if (form.ProgramsAssigned.options[i].value == curProgIdx) {
						form.ProgramsAssigned.options[i].innerText = "*" + dispName[curProgIdx];
						break;
					}
				}
			}
		}
	}
	
	return true;
}

function clearProgramConfig(form) {
	document.getElementById("ProgName").innerText = "";
	sameAsProgName(form, false);
	form.SameAsProgName.disabled = false;
	sameAsDispName(form, false);
	
	form.ProgDispName.value = "";
	form.ProgShortName.value = "";
	form.ProgDescription.value = "";
	form.ProgDescFile.value = "";
	form.ProgCtrlOdds.selectedIndex = 0;
	form.IconNameSavings.value = "";
	form.IconNameControl.value = "";
	form.IconNameEnvrn.value = "";
	form.IconSavings.style.visibility = "hidden";
	form.IconControl.style.visibility = "hidden";
	form.IconEnvrn.style.visibility = "hidden";
}

function showProgramConfig(form) {
	if (form.ProgramsAssigned.selectedIndex >= 0 && form.ProgramsAssigned.value != curProgIdx) {
		if (!saveProgramConfig(form)) {	// Failed to save the program configuration
			for (i = 0; i < form.ProgramsAssigned.length; i++) {
				if (form.ProgramsAssigned.options[i].value == curProgIdx) {
					form.ProgramsAssigned.selectedIndex = i;
					return;
				}
			}
		}
		
		curProgIdx = form.ProgramsAssigned.value;
		
		document.getElementById("ProgName").innerText = progName[curProgIdx];
		form.ProgDispName.value = dispName[curProgIdx];
		if (deviceID[curProgIdx] > 0) {
			sameAsProgName(form, dispName[curProgIdx] == "");
		}
		else {
			sameAsProgName(form, false);
			form.SameAsProgName.disabled = true;
		}
		form.ProgShortName.value = shortName[curProgIdx];
		sameAsDispName(form, shortName[curProgIdx] == "");
		form.ProgDescription.value = description[curProgIdx];
		form.ProgDescFile.value = descFile[curProgIdx];
		form.ProgCtrlOdds.value = ctrlOdds[curProgIdx];
		form.IconNameSavings.value = iconNameSavings[curProgIdx];
		form.IconNameControl.value = iconNameControl[curProgIdx];
		form.IconNameEnvrn.value = iconNameEnvrn[curProgIdx];
		changeProgramIcons(form);
	}
}

function moveUp(form) {
	var entries = form.ProgramsAssigned;
	var idx = entries.selectedIndex;
	if (idx > 0) {
		var oOption = entries.options[idx];
		entries.options.remove(idx);
		entries.options.add(oOption, idx-1);
	}
}

function moveDown(form) {
	var entries = form.ProgramsAssigned;
	var idx = entries.selectedIndex;
	if (idx >= 0 && idx < entries.options.length - 1) {
		var oOption = entries.options[idx];
		entries.options.remove(idx);
		entries.options.add(oOption, idx+1);
	}
}

function prepareSubmit(form) {
	if (!saveProgramConfig(form))
		return false;
	
	var assgnProgList = document.getElementById("ProgramsAssigned");
	for (i = 0; i < assgnProgList.options.length; i++) {
		var idx = assgnProgList.options[i].value;
		
		var html = '<input type="hidden" name="ProgIDs" value="' + progID[idx] + '">';
		form.insertAdjacentHTML("beforeEnd", html);
		html = '<input type="hidden" name="DeviceIDs" value="' + deviceID[idx] + '">';
		form.insertAdjacentHTML("beforeEnd", html);
		html = '<input type="hidden" name="ProgDispNames" value="' + dispName[idx].replace(/"/g, '&amp;quot;') + '">';
		form.insertAdjacentHTML("beforeEnd", html);
		html = '<input type="hidden" name="ProgShortNames" value="' + shortName[idx].replace(/"/g, '&amp;quot;') + '">';
		form.insertAdjacentHTML("beforeEnd", html);
		html = '<input type="hidden" name="ProgDescriptions" value="' + description[idx].replace(/"/g, '&amp;quot;') + '">';
		form.insertAdjacentHTML("beforeEnd", html);
		html = '<input type="hidden" name="ProgDescFiles" value="' + descFile[idx] + '">';
		form.insertAdjacentHTML("beforeEnd", html);
		html = '<input type="hidden" name="ProgChanceOfCtrls" value="' + ctrlOdds[idx] + '">';
		form.insertAdjacentHTML("beforeEnd", html);
		var iconNames = iconNameSavings[idx] + "," + iconNameControl[idx] + "," + iconNameEnvrn[idx];
		html = '<input type="hidden" name="ProgIconNames" value="' + iconNames + '">';
		form.insertAdjacentHTML("beforeEnd", html);
	}
	
	return true;
}

function init() {
	sameAsName(document.form1, <%= category.getStarsWebConfig().getAlternateDisplayName().equals(category.getDescription()) %>);
	changeIcon(document.form1);
	removeWarned = false;
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="init()">
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
              <span class="TitleHeader">ADMINISTRATION - APPLIANCE CATEGORY</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin" onsubmit="return prepareSubmit(this)">
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
                        <td width="15%" align="right" class="TableCell">Category 
                          Name:</td>
                        <td width="85%" class="TableCell"> 
                          <input type="text" name="Name" value="<%= category.getDescription() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right" class="TableCell">Display 
                          Name:</td>
                        <td width="85%" class="TableCell"> 
                          <input type="text" name="DispName" value="<%= category.getStarsWebConfig().getAlternateDisplayName() %>">
                          <input type="checkbox" name="SameAsName" value="true" onClick="sameAsName(this.form, this.checked)">
                          Same as category name </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right" class="TableCell" height="7">Type:</td>
                        <td width="85%" class="TableCell" valign="middle" height="7"> 
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
                        <td width="15%" align="right" class="TableCell">Icon:</td>
                        <td width="85%" class="TableCell"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td width="50%"> 
                                <input type="text" name="IconName" size="20" value="<%= category.getStarsWebConfig().getLogoLocation() %>">
                                <input type="button" name="Preview" value="Preview" onClick="changeIcon(this.form)">
                              </td>
                              <td width="50%"> <img id="CategoryIcon" align="middle"></td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right" class="TableCell">Description:</td>
                        <td width="85%" class="TableCell"> 
                          <textarea name="Description" rows="4" cols="50" wrap="soft"><%= category.getStarsWebConfig().getDescription().replaceAll("<br>", System.getProperty("line.separator")) %></textarea>
                        </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right" class="TableCell"> Programs:</td>
                        <td width="85%" class="TableCell"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                            <tr> 
                              <td> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                  <tr> 
                                    <td width="175" valign="top"> Available Programs:<br>
                                      <select id="ProgramsAvailable" name="ProgramsAvailable" size="5" style="width:165">
                                        <option value="0">&lt;New virtual program&gt;</option>
<%
	for (int i = 0; i < availPrograms.size(); i++) {
		LMProgramDirect program = (LMProgramDirect) availPrograms.get(i);
%>
                                        <option value="<%= i+1 %>"><%= program.getYukonName() %></option>
<%	} %>
                                      </select>
                                    </td>
                                    <td width="50" align="center"> 
                                      <input type="button" id="AddButton" name="Remove" value=">>" onClick="addProgram(this.form)">
                                      <br>
                                      <input type="button" id="RemoveButton" name="Add" value="<<" onclick="removeProgram(this.form)">
                                    </td>
                                    <td width="175" valign="top"> Assigned Programs:<br>
                                      <select id="ProgramsAssigned" name="ProgramsAssigned" size="5" style="width:165" onclick="showProgramConfig(this.form)">
<%
	for (int i = 0; i < category.getStarsEnrLMProgramCount(); i++) {
		StarsEnrLMProgram program = category.getStarsEnrLMProgram(i);
		String progName = program.getYukonName();
		if (program.getDeviceID() == 0) progName = "*" + ServletUtils.getProgramDisplayNames(program)[0];
%>
                                        <option value="<%= i %>"><%= progName %></option>
<%	} %>
                                      </select>
                                      <br>
                                      * means virtual program<br>
                                    </td>
                                    <td align="center">
                                      <input type="button" name="MoveUp" value="Move Up" onclick="moveUp(this.form)">
                                      <br>
                                      <input type="button" name="MoveDown" value="Move Down" onclick="moveDown(this.form)">
                                    </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td colspan="2">
                          <hr>
                          <span class="MainText">Click on a program in the &quot;Assigned 
                          Programs&quot; list to show or update its configuration:</span></td>
                      </tr>
                      <tr> 
                        <td width="15%" class="TableCell" align="right">Program 
                          Configuration:</td>
                        <td width="85%" class="TableCell"> 
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
                                <input type="checkbox" name="SameAsDispName" value="true" onclick="sameAsDispName(this.form, this.checked)">
                                Same as display name </td>
                            </tr>
                            <tr> 
                              <td width="16%">Description:</td>
                              <td width="84%"> 
                                <textarea name="ProgDescription" rows="4" wrap="soft" cols="40"></textarea>
                              </td>
                            </tr>
                            <tr>
                              <td width="16%">Description File:</td>
                              <td width="84%">
                                <input type="text" name="ProgDescFile" size="40">
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
                                          <td width="30%" height="25">Savings: 
                                          </td>
                                          <td width="70%" height="25"> 
                                            <input type="text" name="IconNameSavings" size="20">
                                          </td>
                                        </tr>
                                        <tr> 
                                          <td width="30%" height="25">Control%:</td>
                                          <td width="70%" height="25"> 
                                            <input type="text" name="IconNameControl" size="20">
                                          </td>
                                        </tr>
                                        <tr> 
                                          <td width="30%" height="25">Environment:</td>
                                          <td width="70%" height="25"> 
                                            <input type="text" name="IconNameEnvrn" size="20">
                                          </td>
                                        </tr>
                                      </table>
                                    </td>
                                    <td width="20%"> 
                                      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                        <tr> 
                                          <td> 
                                            <input type="button" name="Preview2" value="Preview" onClick="changeProgramIcons(this.form)">
                                          </td>
                                        </tr>
                                      </table>
                                    </td>
                                    <td width="20%"> 
                                      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                        <tr> 
                                          <td height="25"><img id="IconSavings" src="../../../WebConfig/yukon/Icons/$$Sm.gif" style="visibility:hidden"></td>
                                        </tr>
                                        <tr> 
                                          <td height="25"><img id="IconControl" src="../../../WebConfig/yukon/Icons/HalfSm.gif" style="visibility:hidden"></td>
                                        </tr>
                                        <tr> 
                                          <td height="25"><img id="IconEnvrn" src="../../../WebConfig/yukon/Icons/Tree2Sm.gif" style="visibility:hidden"></td>
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
                    <input type="button" name="Reset" value="Reset" onclick="location.reload()">
                  </td>
                  <td width="75" align="right"> 
                    <input type="button" name="Back" value="Back" onclick="location.href='AdminTest.jsp'">
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
