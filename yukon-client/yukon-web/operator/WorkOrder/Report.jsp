<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.analysis.tablemodel.WorkOrderModel" %>
<%@ page import="com.cannontech.util.ServletUtil" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<SCRIPT  LANGUAGE="JavaScript" SRC="<%= request.getContextPath() %>/JavaScript/calendar.js"></SCRIPT>
</head>

<body class="Background" leftmargin="0" topmargin="0">
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
          <td  valign="top" width="101">
            <% String pageName = "Report.jsp"; %>
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "SERVICE ORDER REPORT"; %>
              <%@ include file="include/SearchBar.jsp" %>
              
			  <form name="rptForm" method="post" action="<%= request.getContextPath() %>/servlet/ReportGenerator">
                <input type="hidden" name="ACTION" value="DownloadReport">
                <input type="hidden" name="type" value="<%= com.cannontech.analysis.ReportTypes.EC_WORK_ORDER_DATA %>">
                <input type="hidden" name="fileName" value="WorkOrder">
                <input type="hidden" name="NoCache">
                <input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>">
                <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
                <table width="500" border="0" cellspacing="2" cellpadding="0">
                  <tr> 
                    <td width="50%" height="100%" valign="top"> 
                      <table width="100%" border="1" cellspacing="0" cellpadding="0" class="TableCell">
                        <tr> 
                          <td> 
                            <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell" height="111%">
                              <tr> 
                                <td class="HeaderCell" align="center">Report Options</td>
                              </tr>
                              <tr> 
                                <td align="center">
                                  <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                    <tr>
                                      <td width="45%" align="right">Current Status:</td>
                                      <td width="3%">&nbsp;</td>
                                      <td width="52%"> 
                                        <select name="ServiceStatus">
                                          <option value="0">Any</option>
                                          <%
	StarsCustSelectionList serviceStatusList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS );
	for (int i = 0; i < serviceStatusList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = serviceStatusList.getStarsSelectionListEntry(i);
%>
                                          <option value="<%= entry.getEntryID() %>"><%= entry.getContent() %></option>
                                          <%
	}
%>
                                        </select>
                                      </td>
                                    </tr>
                                  </table>
                                  
                                </td>
                              </tr>
                              <tr> 
                                <td align="center">&nbsp;</td>
                              </tr>
                              <tr> 
                                <td> 
                                  <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                    <tr> 
                                      <td width="45%" align="right">Search By:</td>
                                      <td width="3%">&nbsp;</td>
                                      <td width="52%"> 
                                        <input type="radio" name="SearchColumn" value="<%= WorkOrderModel.SEARCH_COL_DATE_REPORTED %>" checked>
                                        Date Reported<br>
                                        <input type="radio" name="SearchColumn" value="<%= WorkOrderModel.SEARCH_COL_DATE_SCHEDULED %>">
                                        Date Scheduled<br>
                                        <input type="radio" name="SearchColumn" value="<%= WorkOrderModel.SEARCH_COL_DATE_CLOSED %>">
                                        Date Closed</td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                              <tr>
                                <td>&nbsp;</td>
                              </tr>
                              <tr> 
                                <td> 
                                  <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                    <tr> 
                                      <td align="right" width="47%">Start Date: 
                                        <input id="startCal" type="text" name="start" value="<%= datePart.format(ServletUtil.getToday()) %>" size="8">
                                        <a href="javascript:openCalendar(document.getElementById('rptForm').startCal)"
						                      onMouseOver="window.status='Start Date Calendar';return true;"
            						          onMouseOut="window.status='';return true;"> 
                                        <img src="<%=request.getContextPath()%>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a></td>
                                      <td>&nbsp;</td>
                                      <td width="47%">Stop Date: 
                                        <input id="stopCal" type="text" name="stop" value="<%= datePart.format(ServletUtil.getTomorrow()) %>" size="8">
                                        <a href="javascript:openCalendar(document.getElementById('rptForm').stopCal)"
						                      onMouseOver="window.status='Stop Date Calendar';return true;"
            						          onMouseOut="window.status='';return true;"> 
                                        <img src="<%=request.getContextPath()%>/WebConfig/yukon/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"> 
                                        </a></td>
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
                  <tr> 
                    <td>&nbsp;</td>
                  </tr>
                  <tr> 
                    <td width="50%" height="100%" valign="top" align="center">
                      <input type="submit" name="Submit" value="Get Report">
                    </td>
                  </tr>
                </table>
              </form>
              <p>&nbsp;</p>
            </div>
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
