<%@ include file="StarsHeader.jsp" %>
<%
	String callNo = null;
%>
<%
	callNo = (String) user.getAttribute(ServletUtils.ATT_CALL_TRACKING_NUMBER);
	if (callNo == null) {
		if (request.getParameter("getCallNo") == null)
			response.sendRedirect("/servlet/SOAPClient?action=GetNextCallNo");
		else
			callNo = "";
	}
%>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<SCRIPT  LANGUAGE="JavaScript1.2" SRC="../../JavaScript/Calendar1-82.js"></SCRIPT>
<SCRIPT LANGUAGE="JAVASCRIPT" TYPE="TEXT/JAVASCRIPT">
  <!-- Hide the script from older browsers
  
function goBack() {
 document.location = "CreateWizard.jsp";
}  
  
function getCurrentDateFormatted() {
	
	var strDate;
	var myDate = new Date();
	
	var month = myDate.getMonth() + 1;
	var day = myDate.getDate();
	var year = myDate.getFullYear();
	
	if (month < 10)
		month = "0" + month;
	if (day < 10)
		day = "0" + day;
	
	strDate = month+"/"+day+"/"+year;
	
	return strDate;
}

function checkCallNo(form) {
	if (form.callNo.value == '') {
		alert("Tracking# cannot be empty");
		return false;
	}
	return true;
}

  //End hiding script -->
  </SCRIPT>
</head>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_HEADER %>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
          <td width="1" height="102" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
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
          <td  valign="top" width="101"><% String pageName = "CreateCalls.jsp"; %><%@ include file="Nav.jsp" %></td>
          <td width="1" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div class = "Main" align="center"><% String header = "ACCOUNT - CREATE NEW CALL"; %><%@ include file="InfoSearchBar.jsp" %>
			<% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              
              <form name = "MForm" method="POST" action="/servlet/SOAPClient">
			    <input type="hidden" name="action" value="CreateCall">
                <span class="MainHeader"><b>&nbsp;</b></span> 
                <table width="34%" border="0" height="179" cellspacing = "0">
                  <tr>
                    <td>
                      <table class = "TableCell" width="100%" border="0" cellspacing = "0" cellpadding = "1" height="174">
                        <tr>
                          <td colspan = "2"><span class="MainHeader"><b>CALL INFORMATION</b></span> 
                            <hr>
                          </td>
                          </tr>
                        <tr> 
                          <td width = "50%" align = "right">Date:</td>
                          <td width="50%" > 
                            <input type="text" name="CallDate" size = "10" value="<%= datePart.format(Calendar.getInstance().getTime()) %>">
                            <a href="javascript:show_calendar('MForm.date')"
						onMouseOver="window.status='Pop Calendar';return true;"
						onMouseOut="window.status='';return true;"> <img src="../../Images/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a> 
                          </td>
                        </tr>
                        <tr> 
                          <td width = "50%" align = "right">Tracking #:</td>
                          <td width="50%"> 
                            <input type="text" name="CallNo" size="10" value="<%= callNo %>">
                          </td>
                        </tr>
						<tr> 
                          <td width = "50%" align = "right">Type:</td>
                          <td width="50%"> 
                            <select name="CallType">
<%
	StarsCustSelectionList callTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE );
	for (int i = 0; i < callTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = callTypeList.getStarsSelectionListEntry(i);
%>
                              <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
<%
	}
%>
                            </select>
                          </td>
                        </tr>
                        <tr> 
                          <td width = "50%" align = "right">Taken By:</td>
                          <td width="50%"> 
                            <input type="text" name="TakenBy">
                          </td>
                        </tr>
                        <tr> 
                          <td width = "50%" align = "right">Description:</td>
                          <td width="50%"> 
                            <textarea name="Description" rows="3" wrap="soft" cols="28" class = "TableCell"></textarea>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                <br>
                <table width="150" border="0">
                <tr>
                  <td align = "center" width = "50%"> 
                    <input type="submit" name="Submit" value="Save" onclick="return checkCallNo(this.form)">
                  </td>
                  <td width = "50%"> 
                    <input type="reset" name="Submit2" value="Cancel" >
                  </td>
                </tr>
              </table><br>
              </form>
            </div>
          
           </td>
          <td width="1" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
