<%@ include file="StarsHeader.jsp" %>
<%
	String orderNumber = "";
	Integer orderNo = operator.getIncAttribute("NEXT_ORDER_NUMBER");
	if (orderNo != null)
		orderNumber = orderNo.toString();
%>

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
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="-" border="0">
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
		  <% String pageName = "Service.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center"><% String header = "WORK ORDERS - SERVICE REQUEST"; %><%@ include file="InfoSearchBar.jsp" %><br>
			<form name="form5" method="POST" action="/servlet/SOAPClient">
			  <input type="hidden" name="action" value="CreateOrder">
              <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
                  <tr> 
                    <td valign="top" bgcolor="#FFFFFF"> 
                        
                      <table width="400" border="0" cellspacing="0" cellpadding="3" align="center">
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Service Order #:</div>
                          </td>
                          <td width="210"> 
                            <input type="text" name="OrderNumber" size="14" value="<%= orderNumber %>">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Date Reported:</div>
                          </td>
                          <td width="210"> 
                            <input type="text" name="DateAssigned" size="14" value="<%= datePart.format(Calendar.getInstance().getTime()) %>">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Service Type:</div>
                          </td>
                          <td width="210"> 
                            <select name="ServiceType">
                              <%
	StarsCustSelectionList serviceTypeList = (StarsCustSelectionList) selectionListTable.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_SERVICETYPE );
	for (int i = 0; i < serviceTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = serviceTypeList.getStarsSelectionListEntry(i);
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
                            <div align="right">Assign to:</div>
                          </td>
                          <td width="210"> 
                            <select name="ServiceCompany">
                              <%
	StarsCustSelectionList serviceCompanyList = (StarsCustSelectionList) selectionListTable.get( com.cannontech.database.db.stars.report.ServiceCompany.LISTNAME_SERVICECOMPANY );
	for (int i = 0; i < serviceCompanyList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = serviceCompanyList.getStarsSelectionListEntry(i);
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
                            <div align="right">Ordered By:</div>
                          </td>
                          <td width="210">
                            <input type="text" name="OrderedBy" size="14">
                          </td>
                        </tr>
                        <tr> 
                          <td width="100" class="TableCell"> 
                            <div align="right">Notes: </div>
                          </td>
                          <td width="210"> 
                            <textarea name="Notes" rows="3" wrap="soft" cols="25" class = "TableCell"></textarea>
                          </td>
                        </tr>
                      </table>
                      <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                        <tr> 
                          <td width="169"> 
                            <div align="right"> 
                                <input type="submit" name="Send" value="Send">
                              </div>
                            </td>
                          <td width="211"> 
                            <div align="left"> 
                                <input type="reset" name="Cancel2" value="Cancel">
                              </div>
                            </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
			  </form>
                <div align="center"><br>
              </div>
            </div>
              <p>&nbsp;</p>
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
