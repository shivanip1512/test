<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.servlet.LCConnectionServlet" %>
<%@ page import="com.cannontech.web.loadcontrol.LoadcontrolCache" %>
<%@ page import="com.cannontech.loadcontrol.data.LMProgramDirect" %>
<%@ page import="com.cannontech.loadcontrol.data.LMProgramBase" %>
<%@ page import="com.cannontech.core.dao.NotFoundException" %>

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
    
	final List<StarsEnrLMProgram> programList = Arrays.asList(category.getStarsEnrLMProgram());
    Collections.sort(programList, StarsUtils.createLMProgramComparator(liteEC));

    LCConnectionServlet cs = (LCConnectionServlet) application.getAttribute(LCConnectionServlet.SERVLET_CONTEXT_ID);
    LMProgramBase[] allPrograms;
    if(cs != null)
    {
        LoadcontrolCache cache = cs.getCache();
        allPrograms = cache.getDirectPrograms(); 
    }
    else
    {
        allPrograms = new LMProgramBase[0];
    }

    // list to put available programs in, contains LMProgramDirect objects
    List<LMProgramDirect> availPrograms = new ArrayList<LMProgramDirect>();
	for (int i = 0; i < allPrograms.length; i++) {
		boolean programFound = false;
		for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
			StarsApplianceCategory cat = categories.getStarsApplianceCategory(j);
			for (int k = 0; k < cat.getStarsEnrLMProgramCount(); k++) {
				if (cat.getStarsEnrLMProgram(k).getDeviceID() == allPrograms[i].getYukonID().intValue()) {
					programFound = true;
					break;
				}
			}
			if (programFound) break;
		}
		
		if (!programFound) availPrograms.add( (LMProgramDirect)allPrograms[i] );
	}
	
	String viewOnly = category.getInherited()? "disabled" : "";
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<script type="text/javascript" src="/JavaScript/stars/program.js"></script>
<script language="JavaScript">

Event.observe(window, 'load', updateSelectElements);

var programArray = $A();
var availableProgramArray = $A();

var removeWarned = true;
var virtualDeviceId = 0;
var virtualProgramId = -1;
var virtualProgramName = '<New virtual program>';

function updateSelectElements() {
    updateSelectElement('ProgramsAvailable', 1, availableProgramArray);
    updateSelectElement('ProgramsAssigned', 0, programArray);
    updateSelectElement('Program', 1, programArray, true);
}

function updateSelectElement(elementId, startIndex, array, reset) {
    var optionsArray = $(elementId).options;
    
    //special case for IE
    while (optionsArray.length > startIndex) {
        optionsArray[startIndex] = null;              
    }
    
    array.each(function(program) {
        var index = optionsArray.length;
        optionsArray[index] = new Option(program.getProgramName(), index);
    });
    
    if (reset) resetConfig();        
}

function selectProgramInSelectElement(program, selectElementId) {
    if (!program) return;
    var selectElement = $(selectElementId);    
    var deviceId = program.getDeviceId();
    var programName = (deviceId != virtualDeviceId) ? program.getProgramName() : '*' + program.getDisplayName(); 
    
    var options = selectElement.options;
    for (var x = 0; x < options.length; x++) {
        var option = options[x];
        var name = option.text;
        if (programName != name) continue;
        selectElement.selectedIndex = x;        
    }
}

function changeIcon(form) {
	form.CategoryIcon.style.display = (form.IconName.value == "") ? "none" : "";
	form.CategoryIcon.src = "../../WebConfig/" + form.IconName.value;
}

function changeProgramIcons() {
    updateProgramIcon('iconNameSavings', 'IconSavings');
    updateProgramIcon('iconNameControl', 'IconControl');
    updateProgramIcon('iconNameEnvrn', 'IconEnvrn');
}

function updateProgramIcon(valueElementId, imageElementId) {
    var value = $(valueElementId).value
    var image = $(imageElementId);
    image.src = '../../WebConfig/yukon/Icons/' + value;
    if (value == '') {
        image.hide();
    } else {
        image.show();
    }     
}

function sameAsProgramName(isChecked) {
    <% if (category.getInherited()) { %>
        return;
    <% } %>

    var selectElement = $('Program');
    var program = getProgramBySelectElement(selectElement);
    if (!program) return;
    
    var value = (isChecked) ? '' : program.getDisplayName();       
    $('displayName').value = value;
    $('displayName').disabled = isChecked;
    setProgramChanged(program);
}

function sameAsDisplayName(isChecked) {
    <% if (category.getInherited()) { %>
        return;
    <% } %>

    var selectElement = $('Program');
    var program = getProgramBySelectElement(selectElement);
    if (!program) return;
    
    var value = (isChecked) ? '' : program.getShortName();    
    $('shortName').value = displayName;
    $('shortName').disabled = isChecked;
    setProgramChanged(program);    
}

function setProgramChanged(program) {
    if (!program) program = getProgramBySelectElement($('Program'));
    if (!program) return;

    <% if (!category.getInherited()) { %>
        $('saveButton').disabled = false;
    <% } %>

    program.setHasUnsavedChanges(true);
	setContentChanged(true);
}

<% 
    for (int i = 0; i < programList.size(); i++) {
		StarsEnrLMProgram program = programList.get(i);
        
		StarsWebConfig cfg = program.getStarsWebConfig();
		String[] dispNames = StarsUtils.splitString(cfg.getAlternateDisplayName(), ",");
		String[] imgNames = ServletUtils.getImageNames( cfg.getLogoLocation() );
        
		String progName = "(none)";
		if (program.getDeviceID() > 0) {
            try {
                progName = DaoFactory.getPaoDao().getYukonPAOName(program.getDeviceID());    
            } catch(NotFoundException e) {}
        } else {
            progName = ServletUtils.getProgramDisplayNames(program)[0];    
        }
%>
    var jsProgram = new Program();
    
    var programId = <%=program.getProgramID()%>;
    jsProgram.setProgramId(programId);
    
    var deviceId = <%=program.getDeviceID()%>;
    jsProgram.setDeviceId(deviceId);
    
    var programName = (deviceId == 0) ? '*' + '<%=progName%>' : '<%=progName%>';
    jsProgram.setProgramName(programName);
    
    var displayName = '<%=(dispNames.length > 0)? dispNames[0].replaceAll("\"", "&quot;") : ""%>'.replace(/&quot;/g, '"').replace(/&/g, '&amp;'); 
    jsProgram.setDisplayName(displayName);
    
    var shortName = '<%=(dispNames.length > 1)? dispNames[1].replaceAll("\"", "&quot;") : ""%>'.replace(/&quot;/g, '"').replace(/&/g, '&amp;');
    jsProgram.setShortName(shortName);
    
    var description = '<%=cfg.getDescription().replaceAll("\"", "&quot;")%>'.replace(/&quot;/g, '"').replace(/&/g, '&amp;').replace(/<br>/g, '\r\n');
    jsProgram.setDescription(description);
    
    var descFile = '<%=cfg.getURL()%>';
    jsProgram.setDescFile(descFile);
    
    var ctrlOdds = <%=(program.getChanceOfControl() == null) ? 0 : program.getChanceOfControl().getEntryID()%>;
    jsProgram.setCtrlOdds(ctrlOdds);
    
    var iconNameSavings = '<%=imgNames[0]%>';
    jsProgram.setIconNameSavings(iconNameSavings);
    
    var iconNameControl = '<%=imgNames[1]%>';
    jsProgram.setIconNameControl(iconNameControl);
    
    var iconNameEnvrn = '<%=imgNames[2]%>';
    jsProgram.setIconNameEnvrn(iconNameEnvrn);
    
    programArray.push(jsProgram);
<%
	}

    for (int i = 0; i < availPrograms.size(); i++) {
		LMProgramDirect program = availPrograms.get(i);
%>

    var jsAvailProgram = new Program();
    
    var programId = -1;
    jsAvailProgram.setProgramId(programId);
    
    var deviceId = <%= program.getYukonID() %>;
    jsAvailProgram.setDeviceId(deviceId);
    
    var programName = '<%=program.getYukonName()%>';
    jsAvailProgram.setProgramName(programName);
    
    var description = '<%= StarsUtils.forceNotNone(program.getYukonDescription()).replaceAll("\"", "&quot;") %>'.replace(/&quot;/g, '"');
    jsAvailProgram.setDescription(description);
    
    var ctrlOdds = 0;
    jsAvailProgram.setCtrlOdds(ctrlOdds);
    
    availableProgramArray.push(jsAvailProgram);
    
<%	} %>

function addProgram() {
    var availableOptions = $('ProgramsAvailable').options;
    var selectedIndex = availableOptions.selectedIndex;
    if (selectedIndex == -1) return;
    var option = availableOptions[selectedIndex];
    var programName = option.text;
    
    var program = availableProgramArray.find(function(program) {
        if (program.getProgramName() == programName) {
            var index = availableProgramArray.indexOf(program);
            availableProgramArray.splice(index, 1);
            return program;
        }    
    });
    
    //create virtual program
    if ((!program) && (programName == virtualProgramName)) {
        program = new Program();
        program.setDeviceId(virtualDeviceId);
        program.setProgramId(virtualProgramId);
        program.setProgramName('*' + virtualProgramName);
        program.setDisplayName(virtualProgramName);
    }
    
    if (program) programArray.push(program);
    updateSelectElements();

    setContentChanged(true);
}

function removeProgram() {
    var assignedOptions = $('ProgramsAssigned').options;
    var selectedIndex = assignedOptions.selectedIndex;
    if (selectedIndex == -1) return;
    var option = assignedOptions[selectedIndex];
    var programName = option.text;

   if (!removeWarned) {
        removeWarned = true;
        alert('WARNING: Removing a program will cause all customers currently enrolled in this program being taken out of it!');
    }

    var program = programArray.find(function(program) {
        if (program.getProgramName() == programName) {
            var index = programArray.indexOf(program);
            programArray.splice(index, 1);
            return program;
        }
    });

    if (program) availableProgramArray.push(program);
    updateSelectElements();
    
    setContentChanged(true);
}

function saveProgramConfig(program) {
    if (!program) program = getProgramBySelectElement($('Program'));
    if (!program) return;

    var deviceId = program.getDeviceId();

    var isSameAsProgramName = $('displayNameCheckBox').checked;
    var displayName = (isSameAsProgramName) ? '' : $('displayName').value;
    program.setDisplayName(displayName);
    if (deviceId == virtualDeviceId) {
        program.setProgramName('*' + displayName);
        updateSelectElement('ProgramsAssigned', 0, programArray);
        updateSelectElement('Program', 1, programArray, false);
        selectProgramInSelectElement(program, 'Program');
    }

    var isSameAsDisplayName = $('shortNameCheckBox').checked;
    var shortName = (isSameAsDisplayName) ? '' : $('shortName').value;
    program.setShortName(shortName);
    
    var description = $('descriptionTextArea').value;
    program.setDescription(description);
    
    var descFile = $('descFile').value;
    program.setDescFile(descFile);
    
    var ctrlOdds = $('ctrlOdds').selectedIndex;
    program.setCtrlOdds(ctrlOdds);
    
    var iconNameSavings = $('iconNameSavings').value;
    program.setIconNameSavings(iconNameSavings);
    
    var iconNameControl = $('iconNameControl').value;
    program.setIconNameControl(iconNameControl);
    
    var iconNameEnvrn = $('iconNameEnvrn').value;
    program.setIconNameEnvrn(iconNameEnvrn);
    
    program.setHasUnsavedChanges(false);
    //resetConfig();
    $('saveButton').disabled = true;
}

function resetConfig() {
    checkForUnsavedChanges()

    $('displayName').value = '';
    $('displayNameCheckBox').checked = false;
    $('shortName').value = '';
    $('shortNameCheckBox').checked = false;
    $('descriptionTextArea').value = '';
    $('descFile').value = '';
    $('ctrlOdds').selectedIndex = 0;
    $('iconNameSavings').value = '';
    $('iconNameControl').value = '';
    $('iconNameEnvrn').value = '';
    
    $('saveButton').disabled = true;
    
    <% if (!category.getInherited()) { %>
        $('displayName').disabled = false; 
        $('shortName').disabled = false;        
    <% } %>
}

function checkForUnsavedChanges() {
    var unsavedProgram = programArray.find(function(program) {
        var hasUnsavedChanges = program.getHasUnsavedChanges();
        if (hasUnsavedChanges) return program;
    });
    
    if (!unsavedProgram) return;
    
    var result = confirm(unsavedProgram.getProgramName() + ' has configuration changes, do you want to save the changes?');
    if (result) {
        saveProgramConfig(unsavedProgram);
    } else {
        unsavedProgram.setHasUnsavedChanges(false);
    }    
}

function getProgramBySelectElement(element) {
    var options = element.options;
    var option = options[element.selectedIndex];
    var programName = option.text;
    var program = getProgramByName(programName, programArray);
    return program;
}

function showProgramConfig(element) {
    resetConfig();
    
    var program = getProgramBySelectElement(element);
    if (!program) return;
    
    var deviceId = program.getDeviceId();
    
    var displayName = program.getDisplayName();
    var isSameAsProgramName = (!(displayName) || displayName == '');
    
    $('displayName').value = displayName;
    if (deviceId != virtualDeviceId) {    
        $('displayNameCheckBox').checked = isSameAsProgramName;
    } else {
        $('displayNameCheckBox').disabled = true;    
    }
    
    var shortName = program.getShortName();
    var isSameAsDisplayName = (!(shortName) || shortName == '');

    $('shortName').value = shortName;
    $('shortNameCheckBox').checked = isSameAsDisplayName;

    var description = program.getDescription();
    $('descriptionTextArea').value = description;

    var descFile = program.getDescFile();
    $('descFile').value = descFile;

    var ctrlOdds = program.getCtrlOdds();
    $('ctrlOdds').selectedIndex = ctrlOdds;

    var iconNameSavings = program.getIconNameSavings();
    $('iconNameSavings').value = iconNameSavings;

    var iconNameControl = program.getIconNameControl();
    $('iconNameControl').value = iconNameControl;

    var iconNameEnvrn = program.getIconNameEnvrn();
    $('iconNameEnvrn').value = iconNameEnvrn;

    <% if (!category.getInherited()) { %>
        if (deviceId != virtualDeviceId) $('displayNameCheckBox').disabled = false;
        $('displayName').disabled = isSameAsProgramName;
        $('shortName').disabled = isSameAsDisplayName;
        $('saveButton').disabled = false;
    <% } %>
}

function move(selectElement, direction) {
    var optionsArray = $A(selectElement.options).clone();
    var selectedIndex = selectElement.selectedIndex;
    
    var isSuccess = yukonGeneral_moveOptionPositionInSelect(selectElement, direction);
    if (isSuccess) {
        var option = optionsArray[selectedIndex];
        var option2 = optionsArray[selectedIndex + direction];
        switchArrayElementsByProgramName(option.text, option2.text);
        updateSelectElement('Program', 1, programArray, true);
        setContentChanged(true);
    }
}

function switchArrayElementsByProgramName(programName1, programName2) {
    var program1 = getProgramByName(programName1, programArray);
    var program2 = getProgramByName(programName2, programArray);
    if (!(program1 && program2)) return;
    
    var index1 = programArray.indexOf(program1);
    var index2 = programArray.indexOf(program2);
    
    programArray[index1] = program2;
    programArray[index2] = program1;
}

function getProgramByName(programName, array) {
    return array.find(function(program) {
        if (program.getProgramName() == programName) return program;
    });
}

function createHiddenInput(name, value) {
    var input = document.createElement('input');
    input.setAttribute('type', 'hidden');
    input.setAttribute('name', name);
    input.setAttribute('value', value);
    return input;   
}

function prepareSubmit(form) {
<% if (category.getInherited()) { %>
    return false;
<% } %>

    checkForUnsavedChanges();
    
    programArray.each(function(program) {
        var iconNames = program.getIconNameSavings() + ',' +
                        program.getIconNameControl() + ',' +
                        program.getIconNameEnvrn();
    
        form.appendChild(createHiddenInput('ProgIDs', program.getProgramId()));
        form.appendChild(createHiddenInput('DeviceIDs', program.getDeviceId()));
        form.appendChild(createHiddenInput('ProgDispNames', program.getDisplayName()));
        form.appendChild(createHiddenInput('ProgShortNames', program.getShortName()));
        form.appendChild(createHiddenInput('ProgDescriptions', program.getDescription()));
        form.appendChild(createHiddenInput('ProgDescFiles', program.getDescFile()));
        form.appendChild(createHiddenInput('ProgChanceOfCtrls', program.getCtrlOdds()));
        form.appendChild(createHiddenInput('ProgIconNames', iconNames));                            
    });
	
	return true;
}

function init() {
	//sameAsName(document.form1, <%= category.getStarsWebConfig().getAlternateDisplayName().equals(category.getDescription()) %>);
	changeIcon(document.form1);
	removeWarned = false;
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="init()">
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
              <span class="TitleHeader">ADMINISTRATION - APPLIANCE CATEGORY</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin" onsubmit="return prepareSubmit(this)">
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td class="HeaderCell">Appliance Category</td>
                </tr>
                <tr> 
                  <td height="252"> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="3">
                      <input type="hidden" name="action" value="UpdateApplianceCategory">
                      <input type="hidden" name="AppCatID" value="<%= category.getApplianceCategoryID() %>">
                      <tr> 
                        <td width="15%" align="right" class="TableCell">Category 
                          Name:</td>
                        <td width="85%" class="TableCell"> 
                          <input type="text" name="Name" value="<%= category.getDescription() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right" class="TableCell">Display 
                          Name:</td>
                        <td width="85%" class="TableCell"> 
                          <input type="text" name="DispName" value="<%= category.getStarsWebConfig().getAlternateDisplayName() %>" onchange="setContentChanged(true)">
                          <input type="checkbox" name="SameAsName" value="true" onclick="sameAsName(this.form, this.checked);setContentChanged(true);" <%= viewOnly %>>
                          Same as category name </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right" class="TableCell" height="7">Type:</td>
                        <td width="85%" class="TableCell" valign="middle" height="7"> 
                          <select name="Category" onchange="setContentChanged(true)">
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
                                <input type="text" name="IconName" size="20" value="<%= category.getStarsWebConfig().getLogoLocation() %>" onchange="setContentChanged(true)">
                                <input type="button" name="Preview" value="Preview" onClick="changeIcon(this.form)" <%= viewOnly %>>
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
                                      </select>
                                    </td>
                                    <td width="50" align="center"> 
                                      <input type="button" id="AddButton" name="Remove" value=">>" onClick="addProgram()" <%= viewOnly %>>
                                      <br>
                                      <input type="button" id="RemoveButton" name="Add" value="<<" onclick="removeProgram()" <%= viewOnly %>>
                                    </td>
                                    <td width="175" valign="top"> Assigned Programs:<br>
                                      <select id="ProgramsAssigned" name="ProgramsAssigned" size="5" style="width:165"></select>
                                      <br>
                                      * means virtual program<br>
                                    </td>
                                    <td align="center"> 
                                      <input type="button" name="MoveUp" value="Move Up" onclick="move(ProgramsAssigned, -1)" <%= viewOnly %>>
                                      <br>
                                      <input type="button" name="MoveDown" value="Move Down" onclick="move(ProgramsAssigned, 1)" <%= viewOnly %>>
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
              <br>
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td class="HeaderCell">Program Configuration</td>
                </tr>
                <tr> 
                  <td>
                    <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                      <tr> 
                        <td width="15%" align="right">Program:</td>
                        <td width="85%">
                          <select id="Program" onchange="showProgramConfig(this)">
                            <option value="-1">&lt;Select a program&gt;</option>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right">Display Name:</td>
                        <td width="85%"> 
                          <input id="displayName" type="text" size="20" onchange="setProgramChanged()" <%=viewOnly%>>
                          <input id="displayNameCheckBox" type="checkbox" value="true" onclick="sameAsProgramName(this.checked);" <%= viewOnly %>>
                          Same as program name</td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right">Short Name:</td>
                        <td width="85%"> 
                          <input id="shortName" type="text" size="20" onchange="setProgramChanged()" <%=viewOnly%>>
                          <input id="shortNameCheckBox" type="checkbox" value="true" onclick="sameAsDisplayName(this.checked);" <%= viewOnly %>>
                          Same as display name </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right">Description:</td>
                        <td width="85%"> 
                          <textarea id="descriptionTextArea" rows="4" wrap="soft" cols="40" onchange="setProgramChanged()" <%=viewOnly%>></textarea>
                        </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right">Description File:</td>
                        <td width="85%"> 
                          <input id="descFile" type="text" size="40" onchange="setProgramChanged()" <%=viewOnly%>>
                        </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right">Chance of Control:</td>
                        <td width="85%"> 
                          <select id="ctrlOdds" onchange="setProgramChanged()" <%=viewOnly%>>
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
<%
		}
	}
%>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right">Program Description Icons:</td>
                        <td width="85%"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td width="60%"> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                  <tr> 
                                    <td width="30%" height="25">Savings: </td>
                                    <td width="70%" height="25"> 
                                      <input id="iconNameSavings" type="text" size="20" onchange="setProgramChanged()" <%=viewOnly%>>
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="30%" height="25">Control%:</td>
                                    <td width="70%" height="25"> 
                                      <input id="iconNameControl" type="text" size="20" onchange="setProgramChanged()" <%=viewOnly%>>
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td width="30%" height="25">Environment:</td>
                                    <td width="70%" height="25"> 
                                      <input id="iconNameEnvrn" type="text" size="20" onchange="setProgramChanged()" <%=viewOnly%>>
                                    </td>
                                  </tr>
                                </table>
                              </td>
                              <td width="20%"> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                  <tr> 
                                    <td> 
                                      <input type="button" name="Preview2" value="Preview" onClick="changeProgramIcons()" <%= viewOnly %>>
                                    </td>
                                  </tr>
                                </table>
                              </td>
                              <td width="20%"> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                  <tr> 
                                    <td height="25"><img id="IconSavings" src="../../../WebConfig/yukon/Icons/$$Sm.gif" style="display: none;"></td>
                                  </tr>
                                  <tr> 
                                    <td height="25"><img id="IconControl" src="../../../WebConfig/yukon/Icons/HalfSm.gif" style="display: none;"></td>
                                  </tr>
                                  <tr> 
                                    <td height="25"><img id="IconEnvrn" src="../../../WebConfig/yukon/Icons/Tree2Sm.gif" style="display: none;"></td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr align="center"> 
                        <td colspan="2"> 
                          <input id="saveButton" type="button" value="Save" onclick="saveProgramConfig()" disabled>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
              <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr>
                  <td width="290" align="right"> 
                    <input type="submit" name="Submit" value="Submit" <%= viewOnly %>>
                  </td>
                  <td width="205"> 
                    <input type="button" name="Reset" value="Reset" onclick="location.reload()" <%= viewOnly %>>
                  </td>
                  <td width="75" align="right"> 
                    <input type="button" name="Back" value="Back" onclick="if (warnUnsavedChanges()) location.href='AdminTest.jsp'">
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
