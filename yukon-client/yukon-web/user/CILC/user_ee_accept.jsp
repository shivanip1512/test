<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<script language="JavaScript">

function confirm_form()
{
    checker.submitted.value = "true";
	return true;
}

function decline_form()
{
	checker.submitted.value = "false";
	checker.submit();
}
</script>
</head>
<%
  //Look up the customer curtailable amount, use this as the default amount
  Object[][] gData2 = com.cannontech.util.ServletUtil.executeSQL( session, "select curtailamount from cicustomerbase where customerid=" +  customerID);  
  String curtailAmount = gData2[0][0].toString();

  // Grab the 24 customers baseline values
  double[] baseLineValues = cache.getCustomerBaseLine( customerID);

  // If we couldn't find a baseline create an empty
  // array so below doesn't bomb
  if( baseLineValues == null )
    baseLineValues = new double[24];

%>
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
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
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
                <td width="650" class="Main" valign="top"> 
                  <p align="center"><b><br>
                    OFFER TO CUSTOMER</b><br>
                    <br>
                  
                  <center>
                    <table
    width="600" border="0" cellspacing="0" cellpadding="5">
          <tr valign="top"> 
            <td width="60" class="Main"> 
              <p align=RIGHT><b>Offer ID:</b> 
            </td>
            <td width="45" class="Main"> <%= revision.getOfferID() + " - " + revision.getRevisionNumber()%></td>
            <td width="40" class="Main"> 
              <p align=RIGHT><b>Date:</b> 
            </td>
            <td width="70" class="Main"><%= datePart.format( offer.getOfferDate()) %></td>
            <td width="55" class="Main"> 
              <p align=RIGHT><b>Expires:</b> 
            </td>
            <td width="120" class="Main"><%= timePart.format( revision.getOfferExpirationDateTime()) + " " + datePart.format( revision.getOfferExpirationDateTime()) %></td>
            <td width="135" class="Main"><b>Curtailable Amount:</b></td>
            <td width="75" class="Main">&nbsp;</td>
          </tr>
        </table>
        <struts:form name="checker" type="com.cannontech.validate.PageBean" scope="session" action="user_ee.jsp?tab=accept"> 
        <input type=hidden name="submitted">
        <input type=hidden name="offer" value="<%= revision.getOfferID() %>">
        <input type=hidden name="rev" value="<%= revision.getRevisionNumber() %>">
        <input type=hidden name="offerdate" value="<%= datePart.format(offer.getOfferDate()) %>">
        <input type=hidden name="expiredatetime" value="<%= java.net.URLEncoder.encode( timePart.format(revision.getOfferExpirationDateTime()) + " " + datePart.format(revision.getOfferExpirationDateTime()) ) %>">
        <font size = "1"><%= checker.getError("formaterror") %></font>
        <table width="650" border="0" cellspacing="0" cellpadding="5" valign="top">
          <tr> 
            <td width="50%" valign="top"> 
              <p> 
              <center>
                <table width="300" border="1" cellspacing="0" cellpadding="2">
                  <tr> 
                    <td width="75" height="23" valign="TOP" class="HeaderCell">Hour 
                      Ending </td>
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
                    <td width="75" height="10" class="TableCell"><%= hourStr %></td>
                    <td width="75" height="10" class="TableCell"><%= priceStrs[i] %></td>
                    <td width="75" height="10" class="TableCell"> 
                      <%
				if (amountStrs[i].equals("----")) {
			%>
                      <input type=hidden name="amount" value="0">
                      ---- 
                      <%
				}
				else {
			%>
                      <struts:text property="amount" size="8" value="<%= curtailAmount %>"/> 
                      <%
				}
			%>
                    </td>
            <%
                if( baseLineValues[i] > 0 )
                {                
            %>
                    <td width="75" height="10" class="TableCell"><%= numberFormat.format(baseLineValues[i]) %></td>
            <%
                }
                else
                {                
            %>
                    <td width="75" height="10" class="TableCell">-</td>
            <%
                }
            %>
                  </tr>
                  <%
               }
          %>
                </table>
              </center>
            </td>
            <td width="50%" valign="top"> 
              <p> 
              <center>
                <table width="300" border="1" cellspacing="0" cellpadding="2">
                  <tr> 
                    <td width="75" height="23" valign="TOP" class="HeaderCell">Hour 
                      Ending </td>
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
                    <td width="75" height="10" valign="TOP" class="TableCell"> 
                      <%
				if (amountStrs[i].equals("----")) {
			%>
                      <input type=hidden name="amount" value="0">
                      ---- 
                      <%
				}
				else {
			%>
                      <struts:text property="amount" size="8" value="<%= curtailAmount %>"/> 
                      <%
				}
			%>
                    </td>
                    <%
                if( baseLineValues[i] > 0 )
                {                
            %>
                    <td width="75" height="10" class="TableCell"><%= numberFormat.format(baseLineValues[i]) %></td>
            <%
                }
                else
                {                
            %>
                    <td width="75" height="10" class="TableCell">-</td>
            <%
                }
            %>

                  </tr>
                  <%
               }
          %>
                </table>
              </center>
            </td>
          </tr>
        </table>
               <p>IF 
          YOU ACCEPT THIS OFFER, INDICATE YOUR ACCEPTANCE ALONG WITH YOUR <br>
          SCHEDULED CURTAILMENT LEVEL (SCL). 
		  </p>
	<table width="600" border="0" cellspacing="0" cellpadding="0" align="center" class="Main">
          <tr> 
            <td width="83" height="29"  class="Main"> 
              <div align="right">Comments:&nbsp; </div>
            </td>
            <td class="Main" width="511" height="29"><struts:text property="comments" size="80"/> 
            </td>
          </tr>
        </table>
        <br>        
	<table
    width="580" border="0" cellspacing="0" cellpadding="4">
          <tr> 
            <td width="36%" valign="TOP"> 
              <p align=RIGHT><span class="Main">Initials:</span></td>
            <td width="13%" valign="TOP"> 
              <p align=RIGHT> <struts:text property="initials" size="8" pattern="@not-empty"/> 
                <%= checker.getError("initials") %>&nbsp; 
            </td>
            <td width="16%" valign="TOP"> 
              <div align="right"> 
                <input type="submit" value="Confirm" border="0" ONCLICK="return confirm_form()">
              </div>
            </td>
            <td width="35%" valign="TOP"> <input TYPE="submit" value="Decline" ONCLICK="return decline_form()"> 
            </td>
          </tr>
        </table>
	                
                  </struts:form> <div align="center"> 
                  <div align="center"><span class="Main">If you would like to 
                    review the terms of your energy contract, click</span> </div>
                  <form method="get" action="">
                    <div align="center"> 
                      <input type="submit" name="Input" value="Here">
                    </div>
                  </form>
                  <p class="Main" align="center"><br>
                    If you have questions or problems, call <cti:text roleid="<%= RoleTypes.ENERGYEXCHANGE_PHONE_TEXT %>"/></p>
                 <div align = "center"><a href = "user_ee.jsp" class = "Link1">Back</a></div>
          </p>
               
                    </center>


   </td>
  </tr>
</table>


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
