<%@ include file="StarsHeader.jsp" %>
<%
	String callNumber = "";
	Integer callNo = operator.getIncAttribute("NEXT_CALL_NUMBER");
	if (callNo != null)
		callNumber = callNo.toString();
%>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<SCRIPT  LANGUAGE="JavaScript1.2" SRC="Calendar1-82.js"></SCRIPT>
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



  //End hiding script -->
  </SCRIPT>
</head>
<body class="Background" leftmargin="0" topmargin="0" onload = "javascript:document.MForm.date.value = getCurrentDateFormatted();">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
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
          <td width="1" height="102" bgcolor="#000000"><img src="switch/VerticalRule.gif" width="1"></td>
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
          <td width="1" bgcolor="#000000"><img src="switch/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div class = "Main" align="center"><% String header = "ACCOUNT - CREATE NEW CALL"; %><%@ include file="InfoSearchBar.jsp" %><br>
     
              
              <form name = "MForm" method="POST" action="/servlet/SOAPClient">
			    <input type="hidden" name="action" value="CreateCall">
				<input type="hidden" name="CallNumber" value="<%= callNumber %>">
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
                            <input type="text" name="CallDate" size = "10" value="<%= dateFormat.format(Calendar.getInstance().getTime()) %>">
                            <a href="javascript:show_calendar('MForm.date')"
						onMouseOver="window.status='Pop Calendar';return true;"
						onMouseOut="window.status='';return true;"> <img src="StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a> 
                          </td>
                        </tr>
                        <tr> 
                          <td width = "50%" align = "right">Tracking #:</td>
                          <td width="50%"> 
                            <select>
                              <option>4268723</option>
                            </select>
                          </td>
                        </tr>
						<tr> 
                          <td width = "50%" align = "right">Type:</td>
                          <td width="50%"> 
                            <select name="CallType">
<%
	Hashtable selectionListTable = (Hashtable) operator.getAttribute( "CUSTOMER_SELECTION_LIST" );
	StarsCustSelectionList callTypeList = (StarsCustSelectionList) selectionListTable.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_CALLTYPE );
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
                    <input type="submit" name="Submit" value=" Save ">
                  </td>
                  <td width = "50%"> 
                    <input type="reset" name="Submit2" value="Cancel" >
                  </td>
                </tr>
              </table><br>
              </form>
            </div>
          
           </td>
          <td width="1" bgcolor="#000000"><img src="switch/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
