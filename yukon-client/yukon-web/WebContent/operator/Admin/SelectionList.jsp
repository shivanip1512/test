<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%
	String listName = request.getParameter("List");
	YukonSelectionList list = liteEC.getYukonSelectionList(listName);
	YukonSelectionList dftList = StarsDatabaseCache.getInstance().getDefaultEnergyCompany().getYukonSelectionList(listName);
	if (dftList == null) dftList = new YukonSelectionList();
	
	boolean inherited = liteEC.getParent() != null && liteEC.getYukonSelectionList(listName, false, false) == null;
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">

if(typeof HTMLElement!="undefined" && !HTMLElement.prototype.insertAdjacentElement){
    HTMLElement.prototype.insertAdjacentElement = function(where,parsedNode){
        switch (where){
		    case 'beforeBegin':
		        this.parentNode.insertBefore(parsedNode,this)
		        break;
	        case 'afterBegin':
	            this.insertBefore(parsedNode,this.firstChild);
	            break;
	        case 'beforeEnd':
	            this.appendChild(parsedNode);
	            break;
	        case 'afterEnd':
	            if (this.nextSibling) 
	                this.parentNode.insertBefore(parsedNode,this.nextSibling);
	            else 
		            this.parentNode.appendChild(parsedNode);
	            break;
	    }
	}

    HTMLElement.prototype.insertAdjacentHTML = function(where,htmlStr){
        var r = this.ownerDocument.createRange();
        r.setStartBefore(this);
        var parsedHTML = r.createContextualFragment(htmlStr);
        this.insertAdjacentElement(where,parsedHTML)
    }

    HTMLElement.prototype.insertAdjacentText = function(where,txtStr){
        var parsedText = document.createTextNode(txtStr)
        this.insertAdjacentElement(where,parsedText)
    }
}

var dftEntryTexts = new Array();
var dftEntryYukDefIDs = new Array();
<%
    boolean showAddtlProtocols = ECUtils.hasRight(liteEC, ECUtils.RIGHT_SHOW_ADDTL_PROTOCOLS);
    List<YukonListEntry> entryDefList = dftList.getYukonListEntries();
    int validEntryCounter = 0;
    for (YukonListEntry entry : entryDefList) {
        // Show SA switches only when allowed
        if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE)
            && InventoryUtils.isAdditionalProtocol(entry.getYukonDefID())
            && !showAddtlProtocols){
            continue;
        }
%>
        dftEntryTexts[<%= validEntryCounter %>] = "<%= entry.getEntryText().replaceAll("\"", "&quot;") %>";
        dftEntryYukDefIDs[<%= validEntryCounter %>] = <%= entry.getYukonDefID() %>;
<%	
        validEntryCounter++;
    } 
%>

var entryIDs = new Array();
var entryTexts = new Array();
var entryYukDefIDs = new Array();
var dftListIndices = new Array();
<%
	for (int i = 0; i < list.getYukonListEntries().size(); i++) {
		YukonListEntry entry = (YukonListEntry) list.getYukonListEntries().get(i);
%>
	entryIDs[<%= i %>] = <%= entry.getEntryID() %>;
	entryTexts[<%= i %>] = "<%= entry.getEntryText().replaceAll("\"", "&quot;") %>";
	entryYukDefIDs[<%= i %>] = <%= entry.getYukonDefID() %>;
	dftListIndices[<%= i %>] = getDefaultListIndex(entryTexts[<%= i %>], entryYukDefIDs[<%= i %>]);
<%	} %>

var curIdx = entryTexts.length;

function getDefaultListIndex(entryText, yukDefID) {
	var dftListIdx = -1;
	for (idx = 0; idx < dftEntryTexts.length; idx++) {
		if (dftEntryYukDefIDs[idx] == yukDefID) {
			if (dftEntryTexts[idx] == entryText) {
				// If both yukonDefID and entryText matches, we find it!
				dftListIdx = idx;
				break;
			}
			// If only yukonDefID matches, and it's not 0,
			// then we mark this down, and keep searching for a perfect match
			if (yukDefID != 0) dftListIdx = idx;
		}
	}
	return dftListIdx;
}

function showEntry(form) {
	var entries = form.ListEntries;
	if (entries.selectedIndex < 0 || entries.value == -1) {
		curIdx = entryTexts.length;
		form.EntryID.value = 0;
		form.YukonDefID.value = 0;
		form.EntryText.value = "";
		form.DefaultListEntries.selectedIndex = -1;
		form.Save.value = "Add";
	}
	else {
		curIdx = entries.selectedIndex;
		form.EntryID.value = entryIDs[curIdx];
		form.YukonDefID.value = entryYukDefIDs[curIdx];
		form.EntryText.value = entryTexts[curIdx].replace(/&quot;/g, '"');
		form.DefaultListEntries.selectedIndex = dftListIndices[curIdx];
		form.Save.value = "Update";
	}
}

function showDefaultEntry(form) {
	var dftEntries = form.DefaultListEntries;
    if (dftEntries.selectedIndex >= 0) {
        form.YukonDefID.value = dftEntryYukDefIDs[dftEntries.selectedIndex];
		form.EntryText.value = dftEntryTexts[dftEntries.selectedIndex].replace(/&quot;/g, '"');
	}
}

function saveEntry(form) {
	entryIDs[curIdx] = form.EntryID.value;
	entryTexts[curIdx] = form.EntryText.value.replace(/"/g, '&quot;');
	entryYukDefIDs[curIdx] = form.YukonDefID.value;
	dftListIndices[curIdx] = getDefaultListIndex(entryTexts[curIdx], entryYukDefIDs[curIdx]);
	var entries = form.ListEntries;
	if (curIdx == entries.options.length - 1) {
		var oOption = new Option(entryTexts[curIdx],curIdx);
		entries.options.add(oOption, curIdx);
	} else {
		var oOption = new Option(form.EntryText.value,entries.options[curIdx].value);
		entries.options[curIdx] = oOption;
	}
	entries.selectedIndex = curIdx;
	showEntry(form);
    setContentChanged(true);
}

function moveUp(form) {
	var entries = form.ListEntries;
	var idx = entries.selectedIndex;
	if (idx > 0 && idx < entries.options.length - 1) {
		var prevOption = new Option(entries.options[idx-1].text, idx-1);
        var oOption = new Option(entries.options[idx].text, idx);
        entries.options[idx] = prevOption;
        entries.options[idx-1] = oOption;
		var value = entryIDs[idx];
		entryIDs[idx] = entryIDs[idx-1];
		entryIDs[idx-1] = value;
		value = entryTexts[idx];
		entryTexts[idx] = entryTexts[idx-1];
		entryTexts[idx-1] = value;
		value = entryYukDefIDs[idx];
		entryYukDefIDs[idx] = entryYukDefIDs[idx-1];
		entryYukDefIDs[idx-1] = value;
		value = dftListIndices[idx];
		dftListIndices[idx] = dftListIndices[idx-1];
		dftListIndices[idx-1] = value;
		curIdx--;
		entries.selectedIndex = idx-1;
        setContentChanged(true);
	}
}

function moveDown(form) {
	var entries = form.ListEntries;
	var idx = entries.selectedIndex;
	if (idx >= 0 && idx < entries.options.length - 2) {

		var oOption = new Option(entries.options[idx].text, idx);
		var nextOption = new Option(entries.options[idx+1].text, idx+1);
        entries.options[idx] = nextOption;
 		entries.options[idx+1] = oOption;
		var value = entryIDs[idx];
		entryIDs[idx] = entryIDs[idx+1];
		entryIDs[idx+1] = value;
		value = entryTexts[idx];
		entryTexts[idx] = entryTexts[idx+1];
		entryTexts[idx+1] = value;
		value = entryYukDefIDs[idx];
		entryYukDefIDs[idx] = entryYukDefIDs[idx+1];
		entryYukDefIDs[idx+1] = value;
		value = dftListIndices[idx];
		dftListIndices[idx] = dftListIndices[idx+1];
		dftListIndices[idx+1] = value;
		curIdx++;
		entries.selectedIndex = idx+1;
		setContentChanged(true);
	}
}

function deleteEntry(form) {
	var entries = form.ListEntries;
	var idx = entries.selectedIndex;
	if (idx >= 0 && idx < entries.options.length - 1) {
		if (!confirm("Are you sure you want to delete this entry?")) return;
		entries.removeChild(entries.options[idx]);
		entryIDs.splice(idx, 1);
		entryTexts.splice(idx, 1);
		entryYukDefIDs.splice(idx, 1);
		dftListIndices.splice(idx, 1);
		entries.selectedIndex = entries.options.length - 1;
		showEntry(form);
		setContentChanged(true);
	}
}

function deleteAllEntries(form) {
	var entries = form.ListEntries;
	if (entries.options.length > 1) {
		if (!confirm("Are you sure you want to delete all entries?")) return;
		for (idx = entries.options.length - 2; idx >= 0; idx--)
			entries.removeChild(entries.options[idx]);
		entryIDs.splice(0, entryIDs.length);
		entryTexts.splice(0, entryTexts.length);
		entryYukDefIDs.splice(0, entryYukDefIDs.length);
		dftListIndices.splice(0, dftListIndices.length);
		entries.selectedIndex = 0;
		showEntry(form);
		setContentChanged(true);
	}
}

function restoreDefault(form) {
	if (!confirm("Are you sure you want to replace the current list with the default list?")) return;
	
	var entries = form.ListEntries;
	for (idx = entries.options.length - 2; idx >= 0; idx--)
		entries.options.remove(idx);
	entryIDs.splice(0, entryIDs.length);
	entryTexts.splice(0, entryTexts.length);
	entryYukDefIDs.splice(0, entryYukDefIDs.length);
	dftListIndices.splice(0, dftListIndices.length);
	
	for (idx = 0; idx < dftEntryTexts.length; idx++) {
		var oOption = document.createElement("OPTION");
		entries.options.add(oOption, idx);
		oOption.innerText = dftEntryTexts[idx].replace(/&quot;/g, '"');
		entryIDs[idx] = 0;
		entryTexts[idx] = dftEntryTexts[idx];
		entryYukDefIDs[idx] = dftEntryYukDefIDs[idx];
		dftListIndices[idx] = idx;
	}

	entries.selectedIndex = entries.options.length - 1;
	showEntry(form);
	setContentChanged(true);
}

function prepareSubmit(form) {
	for (idx = 0; idx < entryTexts.length; idx++) {
		var html = '<input type="hidden" name="EntryIDs" value="' + entryIDs[idx] + '">';
		form.insertAdjacentHTML("beforeEnd", html);
		html = '<input type="hidden" name="EntryTexts" value="' + entryTexts[idx] + '">';
		form.insertAdjacentHTML("beforeEnd", html);
		html = '<input type="hidden" name="YukonDefIDs" value="' + entryYukDefIDs[idx] + '">';
		form.insertAdjacentHTML("beforeEnd", html);
	}
}

function changeOrdering(form) {
<% if (liteEC.getParent() == null) { %>
	var disabled = (form.Ordering.value == "A");
	form.MoveUp.disabled = disabled;
	form.MoveDown.disabled = disabled;
<% } %>
}

function populateDefaultList(form) {
	var entries = form.DefaultListEntries;
	for (i = 0; i < dftEntryTexts.length; i++) {
        var oOption = new Option(dftEntryTexts[i],i);
		entries.options.add(oOption, i);
	}
}

function setInherited(inherited) {
	document.form1.MoveUp.disabled = inherited || <%= inherited %>;
	document.form1.MoveDown.disabled = inherited || <%= inherited %>;
	document.form1.Delete.disabled = inherited || <%= inherited %>;
	document.form1.DeleteAll.disabled = inherited || <%= inherited %>;
	document.form1.Default.disabled = inherited || <%= inherited %>;
	document.form1.Save.disabled = inherited || <%= inherited %>;
}

function init() {
<%	if (list.getListID() != LiteStarsEnergyCompany.FAKE_LIST_ID) { %>
	changeOrdering(document.form1);
<%	} %>
	populateDefaultList(document.form1);
	setInherited(<%= inherited %>);
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
              <span class="TitleHeader">ADMINISTRATION - CUSTOMER SELECTION LIST</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin" onsubmit="prepareSubmit(this)">
              <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                <tr> 
                  <td class="HeaderCell">Edit Customer Selection List</td>
                </tr>
                <tr> 
                  <td height="252"> 
                    <table width="100%" border="0" cellspacing="0" cellpadding="5">
                      <input type="hidden" name="action" value="UpdateSelectionList">
                      <input type="hidden" name="ListName" value="<%= listName %>">
                      <tr> 
                        <td width="15%" align="right" class="TableCell">List Name:</td>
                        <td width="85%" class="TableCell"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td class="MainText" width="50%"><%= listName %></td>
                              <td class="MainText" width="50%"> 
<% if (liteEC.getParent() != null) { %>
                                <input type="checkbox" name="Inherited" value="true" <% if (inherited) out.print("checked"); %> onclick="setInherited(this.checked)">
                                Inherited</td>
<% } %>
                            </tr>
                          </table>
                          
                        </td>
                      </tr>
<%	if (list.getListID() != LiteStarsEnergyCompany.FAKE_LIST_ID) { %>
					  <tr> 
                        <td width="15%" align="right" class="TableCell">Description:</td>
                        <td width="85%" class="TableCell"> 
                          <input type="text" name="WhereIsList" size="50" value="<%= list.getWhereIsList() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right" class="TableCell" height="7">Ordering:</td>
                        <td width="85%" class="TableCell" valign="middle" height="7"> 
                          <select name="Ordering" onchange="changeOrdering(this.form);setContentChanged(true);">
                            <option value="O" <% if (list.getOrdering().equalsIgnoreCase("O")) out.print("selected"); %>>List 
                            Order</option>
                            <option value="A" <% if (list.getOrdering().equalsIgnoreCase("A")) out.print("selected"); %>>Alphabetical</option>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right" class="TableCell"> Label:</td>
                        <td width="85%" class="TableCell"> 
                          <input type="text" name="Label" size="30" value="<%= list.getSelectionLabel() %>" onchange="setContentChanged(true)">
                        </td>
                      </tr>
<%	} %>
                      <tr> 
                        <td width="15%" align="right" class="TableCell"> Entries:</td>
                        <td width="85%" class="TableCell"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                            <tr> 
                              <td width="50%">
							    <select name="ListEntries" size="7" style="width:200" onclick="showEntry(this.form)">
<%
    List<YukonListEntry> entryList = list.getYukonListEntries();
    validEntryCounter = 0;
    for (YukonListEntry entry : entryList) {
        // Show SA switches only when allowed
        if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE)
            && InventoryUtils.isAdditionalProtocol(entry.getYukonDefID())
            && !showAddtlProtocols){
            continue;
        }
%>
                                  <option value="<%= validEntryCounter %>"><%= entry.getEntryText() %></option>
<%
        validEntryCounter++;
    }
%>
                                  <option value="-1">&lt;New List Entry&gt;</option>
                                </select>
                                <br>
                                <span class="ConfirmMsg">Click on an entry to 
                                view or edit it.</span></td>
                              <td width="50%"> 
<%	if (list.getListID() != LiteStarsEnergyCompany.FAKE_LIST_ID) { %>
                                <input type="button" name="MoveUp" value="Move Up" style="width:80" onclick="moveUp(this.form)">
                                <br>
                                <input type="button" name="MoveDown" value="Move Down" style="width:80" onclick="moveDown(this.form)">
                                <br>
<%	} %>
                                <input type="button" name="Delete" value="Delete" style="width:80" onclick="deleteEntry(this.form)">
                                <br>
                                <input type="button" name="DeleteAll" value="Delete All" style="width:80" onclick="deleteAllEntries(this.form)">
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
                      
                      <% if( listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_SETTLEMENT_TYPE) ){%> 
					  <tr> 
                        <td colspan="2" width="85%" class="MainText"> 
                        <input type="hidden" name="EntryID" value="0">
                        <%-- The hidden fields are here for compilation, they are used on the "else" clause--%>
                        <input type="hidden" name="DefaultListEntries">
                        <input type="hidden" name="Default">
                          <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr class="TableCell"> 
                              <td width="50%" valign="top" > 
                                <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
									<tr>
		                              <td colspan=2 width="50%" height="20">1. Enter/update the entry text:</td>
									</tr>
                                    <tr> 
                                      <td width="15%" align="right">Text: 
                                      </td>
                                      <td width="85%"> 
                                        <input type="text" name="EntryText" size="30">
                                      </td>
                                    </tr>
									<tr>
		                              <td colspan=2 width="50%" height="20">2. Then enter/update the Yukon Definition ID:<BR>&nbsp;&nbsp;&nbsp;&nbsp;(ID provided by Cannon)</td>
									</tr>
                                    <tr> 
                                      <td width="15%" align="right">ID: 
                                      </td>
                                      <td width="85%"> 
                                        <input type="text" name="YukonDefID" size="30">
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td width="15%"></td>
                                      <td width="85%" align="left" valign="middle" height="60"> 
										<input type="button" name="Save" value="Add" onclick="saveEntry(this.form)">
                                      </td>
                                    </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>                      
                      <% } else {%>
                      <tr> 
                        <td width="15%" align="right" class="TableCell">Entry Definition: </td>
                        <td width="85%" class="MainText"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr class="TableCell">
                              <td width="50%" height="20">1. Select an entry from the default list: </td>
                              <td width="50%" height="20">2. Then enter/update the entry text:</td>
                            </tr>
                            <tr class="TableCell" valign="top"> 
                              <td width="50%"> 
                                <select name="DefaultListEntries" size="7" style="width:200" onclick="showDefaultEntry(this.form)"></select>
                                <br>
                                <%	if (list.getListID() != LiteStarsEnergyCompany.FAKE_LIST_ID) { %>
                                <input type="button" name="Default" value="Restore Default List" onclick="restoreDefault(this.form)">
                                <%	} %>
                              </td>
                              <td width="50%"> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                  <tr> 
                                    <td width="75%"> 
                                      <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                                        <input type="hidden" name="EntryID" value="0">
										<input type="hidden" name="YukonDefID" value="0">
                                        <tr> 
                                          <td width="20%" align="right">Text: 
                                          </td>
                                          <td width="80%"> 
                                            <input type="text" name="EntryText" size="30">
                                          </td>
                                        </tr>
                                      </table>
                                    </td>
                                  </tr>
                                </table>
                                <div align="center"> 
                                  <input type="button" name="Save" value="Add" onclick="saveEntry(this.form)">
                                </div>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
					  <%}%>                      
                      
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
                    <input type="reset" name="Reset" value="Reset" onclick="location.reload()">
                  </td>
                  <td width="75" align="right"> 
                    <input type="button" name="Back" value="Back" onclick="if (warnUnsavedChanges()) location.href='ConfigEnergyCompany.jsp'">
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
