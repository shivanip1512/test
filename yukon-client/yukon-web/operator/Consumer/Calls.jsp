<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="CssLink" rel="stylesheet" href="../demostyle.css" type="text/css">
<% if (ecWebSettings.getURL().length() > 0) { %>
	<script language="JavaScript">document.getElementById("CssLink").href = "../<%= ecWebSettings.getURL() %>";</script>
<% } %>
<script language="JavaScript">
var changed = false;

function setChanged(form, idx) {
	form.changed[idx].value = 'true';
	changed = true;
}

function checkCallNo(form) {
	if (!changed) return false;
	for (i = 0; i < form.changed.length; i++) {
		if (form.changed[i].value == 'true' && form.CallNo[i].value == '') {
			alert("Call # cannot be empty");
			return false;
		}
	}
	return true;
}
</script>
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
                <td id="Header" colspan="5" height="74" background="../Header.gif">&nbsp;</td>
<% if (ecWebSettings.getLogoLocation().length() > 0) { %>
	<script language="JavaScript">document.getElementById("Header").background = "../<%= ecWebSettings.getLogoLocation() %>";</script>
<% } %>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="Header3" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../login.jsp" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
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
          <td  valign="top" width="101">
		  <% String pageName = "Calls.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "ACCOUNT - CALL TRACKING"; %>
              <%@ include file="InfoSearchBar.jsp" %>
			  <% if (errorMsg != null) out.write("<br><span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
              <form name="form1" method="post" action="/servlet/SOAPClient">
			    <input type="hidden" name="action" value="UpdateCalls">
                <table width="600" border="1" cellspacing="0" align="center" cellpadding="2">
                  <tr> 
                    <td class="HeaderCell" width="61">Call #</td>
                    <td class="HeaderCell" width="65">Date</td>
                    <td class="HeaderCell" width="69">Type</td>
                    <td class="HeaderCell" width="217">Description</td>
                    <td class="HeaderCell" width="69">Taken By</td>
                  </tr>
<%
	for (int i = 0; i < callHist.getStarsCallReportCount(); i++) {
		StarsCallReport call = callHist.getStarsCallReport(i);
%>
				  <input type="hidden" name="CallID" value="<%= call.getCallID() %>">
				  <input type="hidden" name="changed" value="false">
                  <tr> 
                    <td class="TableCell" width="61">
					  <input type="text" name="CallNo" size="10" value="<%= call.getCallNumber() %>" onchange="setChanged(this.form, <%= i %>)">
					</td>
                    <td class="TableCell" width="65"><%= datePart.format( call.getCallDate() ) %></td>
                    <td class="TableCell" width="69">
					  <select name="CallType" onchange="setChanged(this.form, <%= i %>)">
<%
	StarsCustSelectionList callTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE );
	for (int j = 0; j < callTypeList.getStarsSelectionListEntryCount(); j++) {
		StarsSelectionListEntry entry = callTypeList.getStarsSelectionListEntry(j);
		String selectedStr = (call.getCallType().getEntryID() == entry.getEntryID()) ? "selected" : "";
%>
						<option value="<%= entry.getEntryID() %>" <%= selectedStr %>><%= entry.getContent() %></option>
<%
	}
%>
					  </select>
					</td>
                    <td class="TableCell" width="217"> 
                      <textarea name="Description" rows="3" wrap="soft" cols="50" class="TableCell" onchange="setChanged(this.form, <%= i %>)"><%= call.getDescription() %></textarea>
                    </td>
                    <td class="TableCell" width="69"><%= call.getTakenBy() %></td>
                  </tr>
<%
	}
%>
                </table>
                <br>
                <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="186"> 
                      <div align="right"> 
                        <input type="submit" name="Save2" value="Save" onclick="return checkCallNo(this.form)">
                      </div>
                    </td>
                    <td width="194"> 
                      <div align="left"> 
                        <input type="reset" name="Cancel2" value="Cancel">
                      </div>
                    </td>
                  </tr>
                </table>
              </form>
             
            </div>
            <p align="center">&nbsp;</p>
          </td>
        <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
