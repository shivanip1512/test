<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionList" %>
<%@ page import="com.cannontech.common.constants.YukonListEntry" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany" %>
<%
	String listName = request.getParameter("List");
	LiteStarsEnergyCompany ec = SOAPServer.getEnergyCompany(user.getEnergyCompanyID());
	YukonSelectionList list = ec.getYukonSelectionList(listName);
	YukonSelectionList dftList = SOAPServer.getDefaultEnergyCompany().getYukonSelectionList(listName);
	if (dftList == null) dftList = new YukonSelectionList();
	
	boolean isOptOutPeriod = listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD);
	boolean isOptOutPeriodCus = listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD_CUS);
	boolean sameAsOp = false;
	if (isOptOutPeriodCus) {
		dftList = ec.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD);
		if (list == null) {
			list = ec.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD);
			sameAsOp = true;
		}
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">
var dftEntryTexts = new Array();
var dftEntryYukDefIDs = new Array();
<%
	for (int i = 0; i < dftList.getYukonListEntries().size(); i++) {
		YukonListEntry entry = (YukonListEntry) dftList.getYukonListEntries().get(i);
%>
	dftEntryTexts[<%= i %>] = "<%= entry.getEntryText() %>";
	dftEntryYukDefIDs[<%= i %>] = <%= entry.getYukonDefID() %>;
<%	} %>

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
	entryTexts[<%= i %>] = "<%= entry.getEntryText() %>";
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
		form.EntryText.value = "";
		form.YukonDefID.value = "";
		form.DefaultListEntries.selectedIndex = -1;
		form.Save.value = "Add";
	}
	else {
		curIdx = entries.selectedIndex;
		form.EntryID.value = entryIDs[curIdx];
		form.EntryText.value = entryTexts[curIdx];
		form.YukonDefID.value = entryYukDefIDs[curIdx];
		form.DefaultListEntries.selectedIndex = dftListIndices[curIdx];
		form.Save.value = "Save";
	}
}

function showDefaultEntry(form) {
	var dftEntries = form.DefaultListEntries;
	var idx = dftEntries.selectedIndex;
	form.EntryText.value = dftEntryTexts[idx];
	form.YukonDefID.value = dftEntryYukDefIDs[idx];
}

function saveEntry(form) {
	entryIDs[curIdx] = form.EntryID.value;
	entryTexts[curIdx] = form.EntryText.value;
	var yukonDefID = parseInt(form.YukonDefID.value, 10);
	if (isNaN(yukonDefID)) yukonDefID = 0;
	entryYukDefIDs[curIdx] = yukonDefID;
	dftListIndices[curIdx] = getDefaultListIndex(entryTexts[curIdx], entryYukDefIDs[curIdx]);
	
	var entries = form.ListEntries;
	if (curIdx == entries.options.length - 1) {
		var oOption = document.createElement("OPTION");
		entries.options.add(oOption, curIdx);
		entries.selectedIndex = curIdx;
	}
	entries.options[curIdx].innerText = entryTexts[curIdx];
	showEntry(form);
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
	}
}

function deleteEntry(form) {
	var entries = form.ListEntries;
	var idx = entries.selectedIndex;
	if (idx >= 0 && idx < entries.options.length - 1) {
		if (!confirm("Are you sure you want to delete this item?")) return;
		entries.options.remove(idx);
		entryIDs.splice(idx, 1);
		entryTexts.splice(idx, 1);
		entryYukDefIDs.splice(idx, 1);
		dftListIndices.splice(idx, 1);
		entries.selectedIndex = entries.options.length - 1;
		showEntry(form);
	}
}

function setAsDefault(form) {
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
		oOption.innerText = dftEntryTexts[idx];
		entryIDs[idx] = 0;
		entryTexts[idx] = dftEntryTexts[idx];
		entryYukDefIDs[idx] = dftEntryYukDefIDs[idx];
		dftListIndices[idx] = idx;
	}

	entries.selectedIndex = entries.options.length - 1;
	showEntry(form);
}

function prepareSubmit(form) {
	for (idx = 0; idx < entryTexts.length; idx++) {
		var html = "<input type='hidden' name='EntryIDs' value='" + entryIDs[idx] + "'>";
		form.insertAdjacentHTML("beforeEnd", html);
		html = "<input type='hidden' name='EntryTexts' value='" + entryTexts[idx] + "'>";
		form.insertAdjacentHTML("beforeEnd", html);
		html = "<input type='hidden' name='YukonDefIDs' value='" + entryYukDefIDs[idx] + "'>";
		form.insertAdjacentHTML("beforeEnd", html);
	}
}

<%	if (isOptOutPeriodCus) { %>
function setSameAsOp(form, checked) {
	form.SameAsOp.checked = checked;
	form.WhereIsList.disabled = checked;
	form.Ordering.disabled = checked;
	form.Label.disabled = checked;
	form.ListEntries.disabled = checked;
	form.MoveUp.disabled = checked;
	form.MoveDown.disabled = checked;
	form.Delete.disabled = checked;
	form.Default.disabled = checked;
	form.EntryText.disabled = checked;
	form.YukonDefID.disabled = checked;
	form.Save.disabled = checked;
	form.DefaultListEntries.disabled = checked;
	if (!checked) showEntry(form);
}
<%	} %>

function init() {
<%	if (isOptOutPeriodCus) { %>
	setSameAsOp(document.form1, <%= sameAsOp %>);
<%	} %>
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
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                            <tr>
                              <td width="30%"><%= listName %></td>
                              <td width="70%"> 
                                <% if (isOptOutPeriodCus) { %>
                                <input type="checkbox" name="SameAsOp" value="true" onClick="setSameAsOp(this.form, this.checked)">
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
                          <input type="text" name="WhereIsList" size="50" value="<%= list.getWhereIsList() %>">
                        </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right" class="TableCell" height="7">Ordering:</td>
                        <td width="85%" class="TableCell" valign="middle" height="7"> 
                          <select name="Ordering">
                            <option value="A" <%= list.getOrdering().equalsIgnoreCase("A")? "selected" : "" %>>Alphabetical</option>
                            <option value="O" <%= list.getOrdering().equalsIgnoreCase("O")? "selected" : "" %>>List 
                            Order</option>
                          </select>
                        </td>
                      </tr>
                      <tr> 
                        <td width="15%" align="right" class="TableCell"> Label:</td>
                        <td width="85%" class="TableCell"> 
                          <input type="text" name="Label" size="30" value="<%= list.getSelectionLabel() %>">
                        </td>
                      </tr>
<%	} %>
                      <tr> 
                        <td width="15%" align="right" class="TableCell"> Entries:</td>
                        <td width="85%" class="TableCell"> 
                          <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                            <tr> 
                              <td width="50%">Current List:<br>
                                <select name="ListEntries" size="5" style="width:150" onClick="showEntry(this.form)">
                                  <%
	for (int i = 0; i < list.getYukonListEntries().size(); i++) {
		YukonListEntry entry = (YukonListEntry) list.getYukonListEntries().get(i);
%>
                                  <option value="<%= i %>"><%= entry.getEntryText() %></option>
                                  <%	} %>
                                  <option value="-1">&lt;New List Entry&gt;</option>
                                </select>
                                <br>
                                <span class="ConfirmMsg">Click on an entry to 
                                view or edit it.</span></td>
                              <td width="50%"> 
<%	if (list.getListID() != LiteStarsEnergyCompany.FAKE_LIST_ID) { %>
                                <input type="button" name="MoveUp" value="Move Up" onclick="moveUp(this.form)">
                                <br>
<%	} %>
<%	if (list.getListID() != LiteStarsEnergyCompany.FAKE_LIST_ID) { %>
                                <input type="button" name="MoveDown" value="Move Down" onclick="moveDown(this.form)">
                                <br>
<%	} %>
                                <input type="button" name="Delete" value="Delete" onclick="deleteEntry(this.form)">
                                <br>
<%	if (list.getListID() != LiteStarsEnergyCompany.FAKE_LIST_ID) { %>
                                <input type="button" name="Default" value="Set As Default" onclick="setAsDefault(this.form)">
<%	} %>
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
                          Def: </td>
                        <td width="85%" class="TableCell"> 
                          <p>Enter entry definition, or select an entry from the 
                            default list: </p>
                          <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr class="TableCell"> 
                              <td width="40%"> Default List:<br>
                                <select name="DefaultListEntries" size="5" style="width:150" onClick="showDefaultEntry(this.form)">
                                  <%
	for (int i = 0; i < dftList.getYukonListEntries().size(); i++) {
		YukonListEntry entry = (YukonListEntry) dftList.getYukonListEntries().get(i);
%>
                                  <option><%= entry.getEntryText() %></option>
                                  <%	} %>
                                </select>
                              </td>
                              <td width="60%"> 
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                  <tr> 
                                    <td width="75%"> 
                                      <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                                        <input type="hidden" name="EntryID" value="0">
                                        <tr> 
                                          <td width="25%" align="right">Text: 
                                          </td>
                                          <td width="75%"> 
                                            <input type="text" name="EntryText" size="25">
                                          </td>
                                        </tr>
                                        <tr> 
                                          <td width="25%" align="right">YukonDefID: 
                                          </td>
                                          <td width="75%"> 
                                            <input type="text" name="YukonDefID" size="25">
                                          </td>
                                        </tr>
                                      </table>
                                    </td>
                                  </tr>
                                </table>
                                <div align="center">
                                  <input type="button" name="Save" value="Add" onClick="saveEntry(this.form)">
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
