<%@ include file="StarsHeader.jsp" %>
<%
	StarsGetCallReportHistoryResponse callTrackingResp = (StarsGetCallReportHistoryResponse) operator.getAttribute("CALL_TRACKING");
	if (callTrackingResp == null) {
		response.sendRedirect("/servlet/SOAPClient?action=CallTracking"); return;
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
                <td colspan="5" height="74" background="../Header.gif">&nbsp;</td>
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
              <br>
              <form name="form1" method="post" action="Details.jsp">
                <table width="600" border="1" cellspacing="0" align="center" cellpadding="2">
                  <tr> 
                    <td class="HeaderCell" width="61">Call #</td>
                    <td class="HeaderCell" width="65">Date</td>
                    <td class="HeaderCell" width="69">Type</td>
                    <td class="HeaderCell" width="217">Description</td>
                    <td class="HeaderCell" width="69">Taken By</td>
                  </tr>
<%
	for (int i = 0; i < callTrackingResp.getStarsCallReportHistoryCount(); i++) {
		StarsCallReportHistory callHist = callTrackingResp.getStarsCallReportHistory(i);
%>
                  <tr> 
                    <td class="TableCell" width="61"><%= callHist.getCallNumber() %></td>
                    <td class="TableCell" width="65"><%= dateFormat.format( callHist.getCallDate().toDate() ) %></td>
                    <td class="TableCell" width="69"><%= callHist.getCallType().getContent() %></td>
                    <td class="TableCell" width="217"> 
                      <textarea name="textarea" rows="3"" wrap="soft" cols="50" class = "TableCell"><%= callHist.getDescription() %></textarea>
                    </td>
                    <td class="TableCell" width="69"><%= callHist.getTakenBy() %></td>
                  </tr>
<%
	}
%>
                </table>
              </form>
                <br>
                <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td width="186"> 
                      <div align="right"> 
                        <input type="submit" name="Save2" value="Save">
                      </div>
                    </td>
                    <td width="194"> 
                      <div align="left"> 
                        <input type="reset" name="Cancel2" value="Cancel">
                      </div>
                    </td>
                  </tr>
                </table>
             
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
