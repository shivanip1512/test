<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">

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
          <td  valign="top" width="101"><% String pageName = "CreateAppliances.jsp"; %><%@ include file="Nav.jsp" %></td>
          <td width="1" bgcolor="#000000"><img src="switch/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div class = "Main" align="center"><% String header = "CREATE NEW APPLIANCE"; %><%@ include file="InfoSearchBar.jsp" %><br>
			<% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
           
              <form name="MForm" method="post" action="/servlet/SOAPClient">
			    <input type="hidden" name="action" value="CreateAppliance">
				<input type="hidden" name="AppNo" value="<%= appliances.getStarsApplianceCount() %>">
                <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                  <tr>
                    <td colspan = "2"  class="TableCell"> <span class="MainHeader"><b>APPLIANCE 
                      INFORMATION</b></span> 
                      <hr>
                      </td>
                    
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Description: </div>
                    </td>
                    <td width="200"> 
                      <select name="Category">
<%
	StarsCustSelectionList appCatList = (StarsCustSelectionList) selectionListTable.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_APPLIANCECATEGORY );
	for (int i = 0; i < appCatList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = appCatList.getStarsSelectionListEntry(i);
%>
						<option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
<%
	}
%>
                      </select>
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Manufacturer:</div>
                    </td>
                    <td width="200"> 
                      <select name="Manufacturer">
<%
	StarsCustSelectionList manuList = (StarsCustSelectionList) selectionListTable.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_MANUFACTURER );
	for (int i = 0; i < manuList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = manuList.getStarsSelectionListEntry(i);
%>
						<option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
<%
	}
%>
                      </select>
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Year Manufactured:</div>
                    </td>
                    <td width="200"> 
                      <input type="text" name="ManuYear" maxlength="14" size="14">
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Location:</div>
                    </td>
                    <td width="200"> 
                      <select name="Location">
<%
	StarsCustSelectionList locationList = (StarsCustSelectionList) selectionListTable.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LOCATION );
	for (int i = 0; i < locationList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = locationList.getStarsSelectionListEntry(i);
%>
						<option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
<%
	}
%>
                      </select>
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Service Company:</div>
                    </td>
                    <td width="200">
					  <select name="Company">
<%
	StarsCustSelectionList companyList = (StarsCustSelectionList) selectionListTable.get( com.cannontech.database.db.stars.report.ServiceCompany.LISTNAME_SERVICECOMPANY );
	for (int i = 0; i < companyList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = companyList.getStarsSelectionListEntry(i);
%>
						<option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
<%
	}
%>
					  </select>
                    </td>
                  </tr>
                  <tr> 
                    <td width="100" class="TableCell"> 
                      <div align="right">Notes:</div>
                    </td>
                    <td width="200"> 
                      <textarea name="Notes" rows="3" wrap="soft" cols="28" class = "TableCell"></textarea>
                    </td>
                  </tr>
                </table>
                <br>
                <table width="150" border="0">
                <tr>
                  <td align = "center" width = "50%"> 
                    <input type="submit" name="Submit" value="Save">
                  </td>
                  <td> 
                    <input type="reset" name="Cancel" value="Cancel">
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
