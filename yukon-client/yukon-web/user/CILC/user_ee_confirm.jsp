<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<SCRIPT LANGUAGE="JAVASCRIPT" TYPE="TEXT/JAVASCRIPT">
  <!-- Hide the script from older browsers

  function goBack() {
  location = "user_ee.jsp?tab=accept&confirmed=false"
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
          <td width="102" height="102" background="../Mom.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr>
                <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Buyback</td> 
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="Main"><a href="../../login.jsp" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
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
          <td  valign="top" width="101">&nbsp; </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
		  <td width="657" valign="top" bgcolor="#FFFFFF">
<table width="657" border="0" cellspacing="0" cellpadding="0">
  <tr> 
                <td width="650" class="Main"> 
                  <div align="center"><b><br>
                    CURTAILMENT TO CUSTOMER</b><br>
                    <br>
                  </div>
                  <table
    width="480" border="0" cellspacing="0" cellpadding="5" align="center" class="Main">
        <tr> 
          <td width="15%"> 
            <p align=RIGHT>&nbsp;Offer 
              ID:
          </td>
          <td width="10%"> <%= checker.get("offer") + " - " + checker.get("rev") %></td>
          <td width="15%"> 
            <p align=RIGHT>Date:
          </td>
          <td width="20%"><%= checker.get("offerdate") %></td>
          <td width="15%"> 
            <p align=RIGHT>Expires:
          </td> <% System.out.println("HERO " + checker.get("expiredatetime")); %>
          <td width="25%"> <%= java.net.URLDecoder.decode( checker.get("expiredatetime") ) %></td>
        </tr>
      </table>
      <form action="user_ee.jsp?tab=confirm" method="post">
      <input type=hidden name="confirmed" value="true">
      <input type=hidden name="program" value=<%= checker.get("program") %>>
      <input type=hidden name="offer" value=<%= checker.get("offer") %>>
      <input type=hidden name="rev" value=<%= checker.get("rev") %>>
      <input type=hidden name="initials" value=<%= checker.get("initials") %>>
      <%
		for (int i = 0; i < 24; i++) {    
            	%>
      <input type=hidden name="amount" value=<%= newAmountStrs[i] %>>
      <%
		}
	%>
      <table width="650" border="0" cellspacing="0" cellpadding="5" align="center">
        <tr> 
          <td width="50%">

            <center>
              <table width="300" border="1" cellspacing="0" cellpadding="2">
                <tr> 
                  <td width="75" height="23" valign="TOP" class="HeaderCell"> 
                    Hour</td>
                  <td width="75" height="23" valign="TOP" class="HeaderCell">Offer 
                    in $ per Kwh</td>
                  <td width="75" height="23" valign="TOP" class="HeaderCell">SCL 
                    in Kw</td>
                  <td width="75" height="23" valign="TOP" class="HeaderCell">Baseline</td>
                </tr>
                <%
               for( int i = 0; i < 12; i++ ) {
                   String hourStr = hourFormat.format(i) + ":00";
          %>
                <tr> 
                  <td width="75" height="10" valign="TOP" class="TableCell"><%= hourStr %></td>
                  <td width="75" height="10" valign="TOP" class="TableCell"><%= priceStrs[i] %></td>
                  <td width="75" height="10" valign="TOP" class="TableCell"><%= amountStrs[i] %></td>
                  <td width="75" height="10" valign="TOP" class="TableCell">&nbsp;</td>
                </tr>
                <%
		  		}
		  %>
              </table>
            </center>
          </td>
                      <td width="50%" valign="top"> 
                        <table width="300" border="1" cellspacing="0" cellpadding="2" align="center">
              <tr> 
                <td width="75" height="23" valign="TOP" class="HeaderCell"> Hour</td>
                <td width="75" height="23" valign="TOP" class="HeaderCell">Offer 
                  in $ per Kwh</td>
                <td width="75" height="23" valign="TOP" class="HeaderCell">SCL 
                  in Kw</td>
                <td width="75" height="23" valign="TOP" class="HeaderCell">Baseline</td>
              </tr>
              <%
               for( int i = 12; i < 24; i++ ) {
                   String hourStr = hourFormat.format(i) + ":00";
          %>
              <tr> 
                <td width="75" height="10" valign="TOP" class="TableCell"><%= hourStr %></td>
                <td width="75" height="10" valign="TOP" class="TableCell"><%= priceStrs[i] %></td>
                <td width="75" height="10" valign="TOP" class="TableCell"><%= amountStrs[i] %></td>
                <td width="75" height="10" valign="TOP" class="TableCell">&nbsp;</td>
              </tr>
              <%
		  		}
		  %>
            </table>
            
          </td>
        </tr>        
      </table>            
      <br clear="ALL"> 
	<table width="600" border="0" cellspacing="0" cellpadding="0" align="center" class="Main">
          <tr> 
            <td width="83" height="29"  class="Main"> 
              <div align="right">Comments:&nbsp; </div>
            </td>
            <td class="Main" width="511" height="29"><%= checker.get("comments") %> 
            </td>
          </tr>
        </table>
        <br>
      <table
    width="580" border="0" cellspacing="0" cellpadding="4" height="10" align="center" class="Main">
        <tr> 
          <td width="36%" valign="TOP" height="10"> 
            <p align=RIGHT>Initials: 
          </td>
          <td width="13%" valign="TOP" height="10"> 
            <p align=LEFT><%= checker.get("initials") %>
          </td>
          <td width="17%" valign="TOP" height="10"> 
            <div align="right"> 
              <input type="submit" value="Send" name="image">
            </div>
          </td>
          <td width="34%" valign="TOP" height="10"><input type = "button" value="Cancel" name = "cancel" onClick = "goBack()"></td>
        </tr>
      </table>
                  
                </td>
  </tr>
</table>
</FORM>
            <div align="center">
              <span class="Main">If you would like to review the terms of your 
                energy contract, click</span>
            </div>
            <form method="get" action="">
              <div align="center">
                <input type="submit" name="" value="Here">
              </div>
            </form>
            <p class="Main" align="center"><br>
              If you have questions or problems, call 1-800-555-1212.</p>
            <p>&nbsp;</p>
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
