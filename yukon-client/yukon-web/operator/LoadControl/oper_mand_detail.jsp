<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
</head>
<%
    com.cannontech.loadcontrol.data.LMCurtailCustomer customers[] = 
        cache.getCurtailmentCustomers( programID.longValue() );

    LMProgramCurtailment program = cache.getCurtailmentProgram( programID.longValue() );

    boolean showAck = true;

    if( request.getParameter("noack") != null )
        showAck = false;

%>
<body class="Background" leftmargin="0" topmargin="0" text="#FFFFFF">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="LoadImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr> 
                <td width="310" height = "28" class="Header3">&nbsp;&nbsp;&nbsp;Load Response</td>
                <td width="235" valign="middle">&nbsp;</td>
                
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
                        <table width="101" border="0" cellspacing="0" cellpadding="6" height="200">
              <tr> 
                <td height="20" valign="top" align="center"> 
                  <form name="form1" method="get" action="oper_mand.jsp?tab=current">
                    <p><br>
                      <input type="submit" name="tab" value="Current">
                    </p>
				  </form>
                  <form name="form1" method="get" action="oper_mand.jsp?tab=new">
				    <p> 
                      <input type="submit" name="tab" value="New">
                    </p>
				  </form>
                  <form name="form1" method="get" action="oper_mand.jsp?tab=history">
				    <p> 
                      <input type="submit" name="tab" value="History">
                    </p>
					</form>
				   <form name="form1" method="get" action="oper_mand.jsp?tab=programs">
                    <p> 
                      <input type="submit" name="tab" value="Programs">
                    </p>
                  </form>
                  <p>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
<table width="657" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="650"valign="top" class="Main">
      <p align="center"><br><b>NOTIFICATION - PROGRAM DETAILS FOR:</b><br>
<b><%= program.getYukonName() %></b></p>
      <p align="center">Click on a company name to view the customer profile. 
      </p>
      <table width="600" border="1" cellspacing="0" cellpadding="2" align="center">
        <tr> 
          <td width="25%" class="HeaderCell">Company</td>
          <td width="15%" class="HeaderCell">Contact</td>
          <td width="15%" class="HeaderCell">Telephone #</td>
          <td width="15%" class="HeaderCell">Acknowledged</td>
          <td width="15%" class="HeaderCell">Ack Date/Time</td>
          <td width="15%" class="HeaderCell">Ack User</td>
        </tr>
        <%
                        // Iterator through all the customers
                        for( int i = 0; i < customers.length; i++)
                        {
                            
                            String companyName = customers[i].getYukonName();
                            
                            String contact = "?";
                            String telephone = "???-????";

                            Class[] cTypes = { String.class, String.class, String.class };
                            Object[][] cData = com.cannontech.util.ServletUtil.executeSQL( dbAlias, "select contfirstname, contlastname, contphone1 from customercontact,cicustomerbase,cicustcontact where cicustomerbase.deviceid=cicustcontact.deviceid and cicustcontact.contactid=customercontact.contactid and cicustomerbase.deviceid=" + customers[i].getYukonID(), cTypes );

                            if( cData != null && cData.length > 0 && cData[0] != null && cData[0].length > 0 )
                            {
                                 contact = cData[0][0].toString() + " " + cData[0][1].toString();
                                 telephone = cData[0][2].toString();
                            }

                            String ack = customers[i].getAckStatus();
                            Date ackDateTime = customers[i].getAckDateTime();
                            String user = customers[i].getNameOfAckPerson();

                            GregorianCalendar ackCal = new GregorianCalendar();
                            ackCal.setTime(ackDateTime);

                            if( ackCal.get( Calendar.YEAR ) <= 1990 )
                                ackDateTime = null;

                            if( user.equalsIgnoreCase("null") )
                                user = "-";

                            if( showAck ||
                                (!showAck && ack.equalsIgnoreCase(com.cannontech.loadcontrol.data.LMCurtailCustomer.ACK_UNACKNOWLEDGED)) )
                            {                            
                  %>
        <tr> 
                        <td width="25%" class="TableCell"> <a href="oper_mand.jsp?tab=profile&customerid=<%= customers[i].getYukonID() %>" class="Link1"><%= companyName %></a> 
                        </td>
          <td width="15%" class="TableCell"> 
              <%= contact %> 
          </td>
          <td width="15%" class="TableCell"> 
              <%= telephone %> 
          </td>
          <td width="15%" class="TableCell"> 
              <%= ack %> 
          </td>
          <td width="15%" class="TableCell"> 
              <% if( ackDateTime != null ) { %>
              <%= timePart.format(ackDateTime) + " " + datePart.format(ackDateTime) %> 
              <% } else { %>
              - 
              <% } %>
          </td>
          <td width="15%" class="TableCell"> 
              <% if( customers[i].getUserIDname() != null && !customers[i].getUserIDname().equalsIgnoreCase("null") ) { %>
              <%= customers[i].getUserIDname() %> 
              <% } else { %>
              - 
              <% } %>
          </td>
        </tr>
        <%
                            }
                        }
                  %>
      </table>
                    <p align="center" class="Main"> 
                      <%
                    if( showAck )
                    {
                %>
                      <a href="oper_mand.jsp?tab=current&prog=<%= programID %>&noack=true" class="Link1"> 
                      Unacknowledged Customers</a> 
                      <%
                    }
                    else
                    {
                %>
                      <a href="oper_mand.jsp?tab=current&prog=<%= programID %>" class="Link1"> 
                      All Customers</a> 
                      <%
                    }
                 %>
                    </p>
                    <p align="center" class="MainHeader"> <a href="oper_mand.jsp?tab=Current" class="Link1">Back 
                      to Current Summary</a> <br>
              <br>
    </td>
  </tr>
</table>    
            </div>
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
