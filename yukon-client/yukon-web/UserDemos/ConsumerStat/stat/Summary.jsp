<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<link rel="stylesheet" href="../../demostyle.css" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../Mom.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../Header.gif">&nbsp;</td>
              </tr>
              <tr> 
				  <td width="265" height="28">&nbsp;</td>
                  <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle">&nbsp;</td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../../login.jsp" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../VerticalRule.gif" width="1"></td>
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
            <% String pageName = "ProgramHist.jsp"; %>
            <%@ include file="Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../VerticalRule.gif" width="1"></td>
		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
              
            <div align="center"><br> 
              <table width="600" border="0" cellspacing="0">
                <tr> 
                  <td width="202"> 
                    <table width="200" border="0" cellspacing="0" cellpadding="3">
                      <tr> 
                        <td><span class="Main"><b>Acct #<%= account.getAccountNumber() %></b></span><br>
                          <span class="NavText"><%= primContact.getFirstName() %> 
                          <%= primContact.getLastName() %><br>
                          <!--<%= account.getCompany() %><br> -->
                          <%= propAddr.getStreetAddr1() %>, <%= propAddr.getStreetAddr2() %><br>
                          <%= propAddr.getCity() %>, <%= propAddr.getState() %> 
                          <%= propAddr.getZip() %><br>
                          <%= primContact.getHomePhone() %></span></td>
                      </tr>
                    </table>
                  </td>
                  <td width="187" valign="top"> 
                    <div align="center"><b><span class="Main">PROGRAMS - CONTROL 
                      SUMMARY </span></b></div>
                  </td>
                  <td valign="top" width="205" align = "right">
                    <%@ include file="Notice.jsp" %>
                  </td>
                </tr>
              </table>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr> 
                  <td> 
                    <hr>
                  </td>
                </tr>
              </table>
              
                <table width="450" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td width="107" height="92"> 
                      
                    <div align="center"><img src="../<%= category.getStarsWebConfig().getLogoLocation() %>" width="60" height="59"><br>
                        <br>
                      </div>
                    </td>
                    <td width="343" valign="top" height="92"> 
                      <table width="300" border="0" cellspacing="0" bgcolor="white" cellpadding="2" align="center">
                        <tr valign="top" bgcolor="#CCCCCC"> 
                          <td width="117" class="HeaderCell">Time Frame</td>
                          <td width="95" class="HeaderCell">Total</td>
                        </tr>
                        <tr> 
                          <td height="23" class="TableCell" width="117">Today</td>
                          
                        <td height="23" class="TableCell" width="95"><%= ServletUtils.getDurationString(summary.getDailyTime()) %></td>
                        </tr>
                        <tr> 
                          <td height="23" class="TableCell" width="117">Past Month</td>
                          
                        <td height="23" class="TableCell" width="95"><%= ServletUtils.getDurationString(summary.getMonthlyTime()) %></td>
                        </tr>
                        <tr> 
                          <td height="23" class="TableCell" width="117">Seasonal</td>
                          
                        <td height="23" class="TableCell" width="95"><%= ServletUtils.getDurationString(summary.getSeasonalTime()) %></td>
                        </tr>
                        <tr> 
                          <td height="23" class="TableCell" width="117">Annual</td>
                          
                        <td height="23" class="TableCell" width="95"><%= ServletUtils.getDurationString(summary.getAnnualTime()) %></td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                
             
              <%
	if (referrer == null) {
%>
              <form name="form1" method="get" action="ProgramHist.jsp">
                <input type="submit" name="Back" value="Back">
              </form>
              <%
	}
	else {
%>
              <input type="button" name="Back2" value="Back" onClick="document.URL='<%= referrer %>'">
              <%
	}
%>
            </div>
			
          </td>
		  
		  
        <td width="1" bgcolor="#000000"><img src="../VerticalRule.gif" width="1"></td>
    </tr>
      </table>
	  
    </td>
	</tr>
</table>
<br>
<p align="center" class="TableCell2">&copy; 2002 Cannon Technologies, Inc. 
  All rights reserved. </p>
<div align="center"></div>
</body>
</html>
