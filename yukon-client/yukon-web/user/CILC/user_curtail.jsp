<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<!--<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">-->
</head>
<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ include file="../user_header.jsp" %>
<%
    // From the ViewLMCurtailCustomerActivity view grab the 
    // Mandatory Curtailment notice, if there is one
    // we shouldn't have gotten here if there isn't but it could happen
       
    String startDateTimeStr = null;
    String stopDateTimeStr = null;
    Integer curtailID = null;
    boolean ackStatus = false;
	Object[][] curtailData = null;

    /*****
    SQL SERVER Workaround
    ******/
    java.util.Date nowDate = new java.util.Date();
    java.util.GregorianCalendar cal = new java.util.GregorianCalendar();	
	cal.setTime(nowDate);

	int day = cal.get(java.util.Calendar.DAY_OF_MONTH);
	int year = cal.get(java.util.Calendar.YEAR);

	java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM");
	String month = format.format(nowDate).toUpperCase();

	String sqlNowString =  day + "-" + month + "-" + year;	
    //end workaround

    try
    {
      Class[] types2 = { java.util.Date.class, java.util.Date.class, Integer.class, String.class, java.util.Date.class };
	  curtailData = ServletUtil.executeSQL( session, 
                                            "SELECT CURTAILMENTSTARTTIME, CURTAILMENTSTOPTIME, CURTAILREFERENCEID, ACKNOWLEDGESTATUS FROM LMCURTAILCUSTOMERACTIVITY_VIEW WHERE CUSTOMERID = " +  customerID + " AND CURTAILMENTSTOPTIME > '" + sqlNowString + "' ORDER BY CURTAILMENTSTOPTIME", types2);

      //let them ack the last one, (should only be 1 actually)     
      if( curtailData != null && curtailData.length > 0 && curtailData[curtailData.length-1] != null && curtailData[curtailData.length-1][0] != null )
      {
            int last = curtailData.length-1;

            java.util.Date startCurtail = (java.util.Date) curtailData[last][0];
            java.util.Date stopCurtail = (java.util.Date) curtailData[last][1];
            curtailID = (Integer) curtailData[last][2]; //new Integer(((java.math.BigDecimal) curtailData[0][2]).intValue());
            ackStatus =  ((String) curtailData[last][3]).trim().equalsIgnoreCase("acknowledged");

            long nowMillis = System.currentTimeMillis();
            if( stopCurtail.getTime() > nowMillis )
            {                
                startDateTimeStr = timePart.format(startCurtail) + " " + datePart.format(startCurtail);
				stopDateTimeStr = timePart.format(stopCurtail) + " " + datePart.format(stopCurtail);
                // durtaion? stop time?
            }
      }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
     
    Object[][] curtailHistory = null;

    try
    {
      Class[] types2 = { java.util.Date.class, java.util.Date.class, String.class, java.util.Date.class, String.class, String.class };
	  //curtailHistory = ServletUtil.executeSQL( session, "SELECT CURTAILMENTSTARTTIME, CURTAILMENTSTOPTIME, ACKNOWLEDGESTATUS, ACKDATETIME, ACKLATEFLAG, NAMEOFACKPERSON FROM LMCURTAILCUSTOMERACTIVITY_VIEW WHERE CUSTOMERID = " +  customerID + " AND CURTAILMENTSTOPTIME < '" + sqlNowString + "' ORDER BY CURTAILMENTSTARTTIME DESC");
      curtailHistory = ServletUtil.executeSQL( session, "select curtailmentstarttime,curtailmentstoptime,acknowledgestatus,ackdatetime,acklateflag,nameofackperson from lmcurtailcustomeractivity_view where CustomerID=" +  customerID + " order by curtailmentstarttime desc", types2);     
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }

    boolean foundCurtailment = (startDateTimeStr != null && stopDateTimeStr != null );

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
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr>   
                <td width="265" height="28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Buyback</td> 
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
          <td  valign="top" width="101"> 
		  <% String pageName = "user_curtail.jsp"; %>
          <%@ include file="nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          
		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
<table width="657" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="650"> 
       <% if( foundCurtailment )
       {
       %>
                  <div align="center" class="Main"><br>
                    <b>CURRENT NOTIFICATION</b></div>
       <%
       }
       %>
      <table width="500" border="0" cellspacing="0" cellpadding="0" align="center">
        
        <%
          if( foundCurtailment )
          {
        %>
        <tr> 
          <td>
            <center>
                          <span class="Main"> <br>
                          Per your contract with <cti:getProperty propertyid="<%=DirectCurtailmentRole.CURTAILMENT_PROVIDER%>"/>, 
                          a notification event is scheduled<br>
                          from <%= startDateTimeStr %> to <%= stopDateTimeStr %>.</span> 
                          <p> 
                <%			
          if( !ackStatus )
			{
%>
              <form method="post" action="/servlet/CurtailmentServlet">
                <input type="hidden" name="CUSTOMERID" value="<%= customerID %>">
                <input type="hidden" name="CURTAILID" value="<%= curtailID %>">
                <input type="hidden" name="ACKTIME" value="<%= System.currentTimeMillis() %>">
                <input type="hidden" name="redirect" value="/user/CILC/user_curtail.jsp">
                <span class="Main"> Initials:</span> 
                <input type="text" name="initials" size="8">
                <p> 
                  <input type="submit" value="Acknowledge" name="image">
                            <p class="Main">(Click Acknowledge to indicate that you 
                              have received and understand this message) 
                            <p> 
                  <%
			}
%>
                            <p class="MainHeader"><b>Curtailable Amount: </b> 
                          </form>
              <p> 
            </center>
          </td>
        </tr>
        <%
		}
		else
		{        
%>
        <tr> 
                      <td width="100%" class="MainHeader">
<center>
                          <p><br>
                            <b>NO NOTIFICATION SCHEDULED AT THIS TIME</b></p>
                        </center>
                      </td>
        </tr>
        <%
        }
%>
      </table>
      <p align="center" class="MainHeader"><b>NOTIFICATION HISTORY</b></p>
      <table width="600" border="1" cellspacing="0" cellpadding="2" height="20" align="center">
        <tr> 
          <td width="20%" class="HeaderCell">
            <center>
              Start Date/Time
            </center>
          </td>
          <td width="20%" class="HeaderCell">
            <center>
              Stop Date/Time
            </center>
          </td>
          <td width="20%" class="HeaderCell">
            <center>
              Acknowledged
            </center>
          </td>
          <td width="20%" class="HeaderCell">
            <center>
              Ack Date/Time
            </center>
          </td>
          <td width="10%" class="HeaderCell">
            <center>
              Ack Late
            </center>
          </td>
          <td width="10%" class="HeaderCell">
            <center>
              Ack User
            </center>
          </td>
        </tr>
        <% 

            if( curtailHistory != null )
            {                
                for( int i = 0; i < curtailHistory.length; i++ )
                { 
                 java.util.Date curtailStart = (java.util.Date) curtailHistory[i][0];
                 java.util.Date curtailStop = (java.util.Date) curtailHistory[i][1];
                 String ackd  = (String) curtailHistory[i][2];
				 java.util.Date respDateTime = (java.util.Date) curtailHistory[i][3];
                 String onTime = (String) curtailHistory[i][4];
                 String userStr = (String) curtailHistory[i][5];

				 String respDateTimeStr;
                 String curtailStartStr;
                 String curtailStopStr;
                
                 java.util.Date oldDate = new java.util.Date("1/1/1995");
                 if( curtailStart.before(oldDate) )
                     curtailStartStr = "-";
                 else
                     curtailStartStr = datePart.format(curtailStart) + " " + timePart.format(curtailStart);

                 if( curtailStop.before(oldDate) )
                     curtailStopStr = "-";
                 else
                     curtailStopStr = datePart.format(curtailStop) + " " + timePart.format(curtailStop);

                 if( respDateTime.before(oldDate) )
                     respDateTimeStr = "-";
                 else
                     respDateTimeStr = datePart.format(respDateTime) + " " + timePart.format(respDateTime);
                 
                 if( userStr == null || userStr.trim().equalsIgnoreCase("null") )
                     userStr = "-";

                 

        %>
        <tr> 
          <td width="20%" class="TableCell"><center><%= curtailStartStr %></center></td>
          <td width="20%" class="TableCell"><center><%= curtailStopStr %></center></td>
          <td width="20%" class="TableCell"><center><%= ackd %></center></td>
          <td width="20%" class="TableCell"><center><%= respDateTimeStr %></center></td>
          <td width="10%" class="TableCell"><center><%= onTime %></center></td>
          <td width="10%" class="TableCell"><center><%= userStr %></center></td>
        </tr>
        <%
                }
            }
        %>
      </table>
                  <p align="center"></p>
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
