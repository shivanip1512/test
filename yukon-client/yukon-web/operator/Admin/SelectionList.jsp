<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionList" %>
<%@ page import="com.cannontech.common.constants.YukonListEntry" %>
<%
	String listName = request.getParameter("List");
	YukonSelectionList list = liteEC.getYukonSelectionList(listName);
	YukonSelectionList dftList = StarsDatabaseCache.getInstance().getDefaultEnergyCompany().getYukonSelectionList(listName);
	if (dftList == null) dftList = new YukonSelectionList();
	
	boolean isOptOutPeriod = listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD);
	boolean isOptOutPeriodCus = listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD_CUS);
	boolean sameAsOp = false;
	if (isOptOutPeriodCus) {
		dftList = liteEC.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD);
		if (list == null) {
			list = dftList;
			sameAsOp = true;
		}
	}
	
	String viewOnly = (liteEC.getParent() != null)? "disabled" : "";
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
var dftEntryTexts = new Array();
var dftEntryYukDefIDs = new Array();
<%
	for (int i = 0; i < dftList.getYukonListEntries().size(); i++) {
		YukonListEntry entry = (YukonListEntry) dftList.getYukonListEntries().get(i);
%>
	dftEntryTexts[<%= i %>] = "<%= entry.getEntryText().replaceAll("\"", "&quot;") %>";
	dftEntryYukDefIDs[<%= i %>] = <%= entry.getYukonDefID() %>;
<%	} %>
	dftEntryTexts[<%= dftList.getYukonListEntries().size() %>] = "";
	dftEntryYukDefIDs[<%= dftList.getYukonListEntries().size() %>] = 0;

var entryIDs = new Array();
var entryTexts = new Array();
var entryYukDefIDs = new Array();
var dftListIndices = new Array();
<%
	for (int i = 0; i < list.getYukonListEntries().size(); i++) {
		YukonListEntry entry = (YukonListEntry) list.getYukonListEntries().get(i);
		int entryID = entry.getEntryID();
		if (isOptOutPeriodCus && sameAsOp) entryID = 0;
%>
	entryIDs[<%= i %>] = <%= entryID %>;
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
		var oOption = document.createElement("OPTION");
		entries.options.add(oOption, curIdx);
		entries.selectedIndex = curIdx;
	}
	entries.options[curIdx].innerText = form.EntryText.value;
	showEntry(form);
	setContentChanged(true);
}

function moveUp(form) {
	var entries = form.ListEntries;
	var idx = entries.selectedIndex;
	if (idx > 0 && idx < entries.options.length - 1) {
		var oOption = entries.options[idx];
		entries.options.remove(idx);
		entries.options.add(oOption, idx-1);
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
		setContentChanged(true);
	}
}

function moveDown(form) {
	var entries = form.ListEntries;
	var idx = entries.selectedIndex;
	if (idx >= 0 && idx < entries.options.length - 2) {
		var oOption = entries.options[idx];
		entries.options.remove(idx);
		entries.options.add(oOption, idx+1);
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
		setContentChanged(true);
	}
}

function deleteEntry(form) {
	var entries = form.ListEntries;
	var idx = entries.selectedIndex;
	if (idx >= 0 && idx < entries.options.length - 1) {
		if (!confirm("Are you sure you want to delete this entry?")) return;
		entries.options.remove(idx);
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
			entries.options.remove(idx);
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
<% if (liteEC.getParent() == null) { %>
	for (idx = 0; idx < entryTexts.length; idx++) {
		var html = '<input type="hidden" name="EntryIDs" value="' + entryIDs[idx] + '">';
		form.insertAdjacentHTML("beforeEnd", html);
		html = '<input type="hidden" name="EntryTexts" value="' + entryTexts[idx] + '">';
		form.insertAdjacentHTML("beforeEnd", html);
		html = '<input type="hidden" name="YukonDefIDs" value="' + entryYukDefIDs[idx] + '">';
		form.insertAdjacentHTML("beforeEnd", html);
	}
	return true;
<% } else { %>
	return false;
<% } %>
}

<%	if (isOptOutPeriodCus) { %>
function setSameAsOp(form, checked) {
	form.SameAsOp.checked = checked;
<% if (liteEC.getParent() == null) { %>
	form.WhereIsList.disabled = checked;
	form.Ordering.disabled = checked;
	form.Label.disabled = checked;
	form.ListEntries.disabled = checked;
	form.MoveUp.disabled = checked;
	form.MoveDown.disabled = checked;
	form.Delete.disabled = checked;
	form.Default.disabled = checked;
	form.EntryText.disabled = checked;
	form.Save.disabled = checked;
	form.DefaultListEntries.disabled = checked;
<% } %>
	if (!checked) showEntry(form);
}
<%	} %>

function changeOrdering(form) {
<% if (liteEC.getParent() == null) { %>
	var disabled = (form.Ordering.value == "A");
	form.MoveUp.disabled = disabled;
	form.MoveDown.disabled = disabled;
<% } %>
}

function init() {
<%	if (isOptOutPeriodCus) { %>
	setSameAsOp(document.form1, <%= sameAsOp %>);
<%	} %>
<%	if (list.getListID() != LiteStarsEnergyCompany.FAKE_LIST_ID) { %>
	changeOrdering(document.form1);
<%	} %>
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
              <span class="TitleHeader">ADMINISTRATION - CUSTOMER SELECTION LIST</span><br>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin" onsubmit="return prepareSubmit(this)">
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
                              <td width="30%" class="MainText"><%= listName %></td>
                              <td width="70%" class="TableCell"> 
                                <% if (isOptOutPeriodCus) { %>
                                <input type="checkbox" name="SameAsOp" value="true" onclick="setSameAsOp(this.form, this.checked);setContentChanged(true);" <%= viewOnly %>>
                                Same As Operator Side List 
                                <% } %>
                              </td>
                            </tr>
                          </table>
                          
                        </td>
                      </tr>
<%	if (isOptOutPeriod || isOptOutPeriodCus) { %>
                      <tr> 
                        <td width="15%" align="right" class="TableCell">&nbsp;</td>
                        <td width="85%" class="TableCell"> 
                          <% if (isOptOutPeriod) { %>
                          <a href="SelectionList.jsp?List=OptOutPeriodCustomer">View 
                          Customer Side List</a> 
                          <% } else { %>
                          <a href="SelectionList.jsp?List=OptOutPeriod">View 
                          Operator Side List</a> 
                          <% } %>
                        </td>
                      </tr>
<%	} %>
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
                            <option value="A" <%= list.getOrdering().equalsIgnoreCase("A")? "selected" : "" %>>Alphabetical</option>
                            <option value="O" <%= list.getOrdering().equalsIgnoreCase("O")? "selected" : "" %>>List 
                            Order</option>
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
	for (int i = 0; i < list.getYukonListEntries().size(); i++) {
		YukonListEntry entry = (YukonListEntry) list.getYukonListEntries().get(i);
%>
                                  <option value="<%= i %>"><%= entry.getEntryText() %></option>
<%
	}
%>
                                  <option value="-1">&lt;New List Entry&gt;</option>
                                </select>
                                <br>
                                <span class="ConfirmMsg">Click on an entry to 
                                view or edit it.</span></td>
                              <td width="50%"> 
<%	if (list.getListID() != LiteStarsEnergyCompany.FAKE_LIST_ID) { %>
                                <input type="button" name="MoveUp" value="Move Up" style="width:80" onclick="moveUp(this.form)" <%= viewOnly %>>
                                <br>
                                <input type="button" name="MoveDown" value="Move Down" style="width:80" onclick="moveDown(this.form)" <%= viewOnly %>>
                                <br>
<%	} %>
                                <input type="button" name="Delete" value="Delete" style="width:80" onclick="deleteEntry(this.form)" <%= viewOnly %>>
                                <br>
                                <input type="button" name="DeleteAll" value="Delete All" style="width:80" onclick="deleteAllEntries(this.form)" <%= viewOnly %>>
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
                        <td width="15%" align="right" class="TableCell">Entry 
                          Definition: </td>
                        <td width="85%" class="MainText"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr class="TableCell"> 
                              <td width="50%" height="20">1. Select an entry from 
                                the default list: </td>
                              <td width="50%" height="20">2. Then enter/update 
                                the entry text:</td>
                            </tr>
                            <tr class="TableCell" valign="top"> 
                              <td width="50%"> 
                                <select name="DefaultListEntries" size="7" style="width:200" onclick="showDefaultEntry(this.form)">
                                  <%
	for (int i = 0; i < dftList.getYukonListEntries().size(); i++) {
		YukonListEntry entry = (YukonListEntry) dftList.getYukonListEntries().get(i);
%>
                                  <option><%= entry.getEntryText() %></option>
                                  <%
	}
%>
                                  <option>&lt;New Entry&gt;</option>
                                </select>
                                <br>
                                <%	if (list.getListID() != LiteStarsEnergyCompany.FAKE_LIST_ID) { %>
                                <input type="button" name="Default" value="Restore Default List" onclick="restoreDefault(this.form)" <%= viewOnly %>>
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
                                  <input type="button" name="Save" value="Add" onclick="saveEntry(this.form)" <%= viewOnly %>>
                                </div>
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
                    <input type="submit" name="Submit" value="Submit" <%= viewOnly %>>
                  </td>
                  <td width="205"> 
                    <input type="reset" name="Reset" value="Reset" <%= viewOnly %> onclick="setContentChanged(false)">
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
