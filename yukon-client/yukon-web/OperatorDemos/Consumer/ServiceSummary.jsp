<%@ include file="StarsHeader.jsp" %>
<%
	StarsGetServiceRequestHistoryResponse getServHistResp = (StarsGetServiceRequestHistoryResponse) operator.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + "SERVICE_HISTORY");
	if (getServHistResp == null) {
		response.sendRedirect("/servlet/SOAPClient?action=GetServiceHistory"); return;
	}
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
		  <% String pageName = "ServiceSummary.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
              
            <div align="center">
              <% String header = "WORK ORDERS - SERVICE HISTORY"; %>
              <%@ include file="InfoSearchBar.jsp" %>
              <br>
              <span class="Main">Click on an Order # to view the complete order 
              history.</span>
              </div>
              
            <table width="615" border="1" cellspacing="0" cellpadding="3" align="center">
              <tr> 
                <td width="53" class="HeaderCell">Order # </td>
                <td width="65" class="HeaderCell">Date/Time</td>
                <td width="49" class="HeaderCell">Type</td>
                <td width="52" class="HeaderCell">Status</td>
                <td width="42" class="HeaderCell">By Who</td>
                <td width="74" class="HeaderCell">Assigned</td>
                <td width="222" class="HeaderCell">Desription</td>
              </tr>
<%
	for (int i = 0; i < getServHistResp.getStarsServiceRequestHistoryCount(); i++) {
		StarsServiceRequestHistory servHist = getServHistResp.getStarsServiceRequestHistory(i);
%>
              <tr valign="top"> 
                <td width="53" class="TableCell"><a href="SOHistory.jsp" class="Link1"><%= servHist.getOrderNumber() %></a></td>
                <td width="65" class="TableCell"><% if (servHist.getDateReported() != null) out.print( histDateFormat.format(servHist.getDateReported()) ); %></td>
                <td width="49" class="TableCell"><%= servHist.getServiceType().getContent() %></td>
                <td width="52" class="TableCell"><%= servHist.getCurrentState().getContent() %></td>
                <td width="42" class="TableCell"><%= servHist.getOrderedBy() %></td>
				<td width="74" class="TableCell"><%= servHist.getServiceCompany().getContent() %></td>
				<td width="222"> 
				  <textarea name="textarea2" rows="3" wrap="soft" cols="28" class = "TableCell"><%= servHist.getDescription() %></textarea>
				</td>
              </tr>
<%
	}
%>
            </table>
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
