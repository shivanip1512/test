<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
</head>

<%@ include file="../include/user_header.jsp" %>
<%@ page import="com.cannontech.message.macs.messages.Schedule" %>
<%@ page import="com.cannontech.loadcontrol.data.LMProgramDirect" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Vector" %>
<%@ page import="java.util.Iterator" %>
<%  
   String pending = request.getParameter("pending");
   
   if( pending != null )
      out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"5;URL=user_direct.jsp\">");
   else
      out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"60\">");

    //java.text.SimpleDateFormat  = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm");
    
    session = request.getSession(false);
                           
    // list to put this customers programs in, contains LMProgramDirect objects
    ArrayList ourPrograms = new ArrayList();

    long[] programIDs = com.cannontech.database.db.web.LMDirectCustomerList.getProgramIDs( customerID );    
    java.util.Arrays.sort(programIDs);
    LMProgramDirect[] allPrograms = cache.getDirectPrograms(); 

    // Match our program ids with the actual programs in the cache so we know what to display
    for( int i = 0; i < allPrograms.length; i++ )
    {
        long id = allPrograms[i].getYukonID().longValue();
        if( java.util.Arrays.binarySearch(programIDs, id ) >= 0 )
        {
            // found one
            ourPrograms.add(allPrograms[i]);
        }
    }
%>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="150" height="102" background="../../WebConfig/yukon/MomWide.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="609" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr>
                <td width="265" height="28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;User 
                  Control </td> 
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="150" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="609" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="150"> 
		  <% String pageName = "user_lm_time.jsp"; %>
          <%@ include file="include/nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
		  <td width="609" valign="top" bgcolor="#FFFFFF"> 
              <div align="center">
                <table width="609" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td width="600" > 
                      <center>
                        <p class="TitleHeader"><br>DIRECT CONTROL</p>
                        <p class="MainText">To start/stop a program or change the start/stop time, select the program name.</p>
                      </center>
                      <%
         if( pending != null )
         {
      %>
                      <center>
                        <p class="MainText"><font color="red">Request sent, please wait...</font> </p>
                      </center>
                      <%
         }
      %>
                      <p> 
                      <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                        <tr> 
                          <td width="25%" class="HeaderCell"> 
                            <p> 
                              <center>Program Name</center>
                          </td>
                          <td width="25%" class="HeaderCell"> 
                            <p> 
                              <center>
                                Current State 
                              </center>
                          </td>
                          <td width="25%" class="HeaderCell"> 
                            <p> 
                              <center>
                                Start Date/Time 
                              </center>
                          </td>
                          <td width="25%" class="HeaderCell"> 
                            <p> 
                              <center>
                                Stop Date/Time 
                              </center>
                          </td>
                        </tr>
                        <%
           /* //Loop through the user's programs            
                                                
            Iterator iter = schedules.entrySet().iterator();

            while( iter.hasNext() )
            {
                Map.Entry entry = (Map.Entry) iter.next(); 
                
                Schedule sched = (Schedule) entry.getKey();                
                Integer attrib = (Integer) entry.getValue();
                String actionUrl;

              
                if( sched.getCurrentState().equals(Schedule.STATE_WAITING) )
                   actionUrl = "start_schedule.jsp?id=" + sched.getId();
                else
                  if( sched.getCurrentState().equalsIgnoreCase(Schedule.STATE_RUNNING) ||
                   sched.getCurrentState().equalsIgnoreCase(Schedule.STATE_PENDING)  )
                        actionUrl = "stop_schedule.jsp?id=" + sched.getId();
                  else                    
                        actionUrl = "schedule_summary.jsp";                    */
            Iterator iter = ourPrograms.iterator();
            while( iter.hasNext() )
            {
                LMProgramDirect p = (LMProgramDirect) iter.next();

                String status = p.getProgramStatusString( p.getProgramStatus().intValue());
                startStr = "-";               
                stopStr = "-"; 
                String actionURI = "user_direct.jsp";


                if( p.getStartTime().getTime().getTime() > com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime().getTime() )
                    startStr = datePart.format( p.getStartTime().getTime() ) + " " + timePart.format( p.getStartTime().getTime() );

                if( p.getStopTime().getTime().getTime() > com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime().getTime() )
                    stopStr =  datePart.format( p.getStopTime().getTime()) + " " + timePart.format( p.getStopTime().getTime() );

                if( pending != null )
                    actionURI = "user_direct.jsp";
                else
                if( status.equalsIgnoreCase("inactive") )                
                    actionURI = "user_direct_start.jsp?id=" + p.getYukonID();               
                else
                if( status.equalsIgnoreCase("manual active") )
                    actionURI = "user_direct_stop.jsp?id=" + p.getYukonID(); 
               
                
        %>
                        <tr> 
                          <td width="25%" class="TableCell">
                            <center>
                              <a href="<%= actionURI %>"><%= p.getYukonName() %></a>
                            </center>
                          </td>
                          <td width="25%" class="TableCell">
                            <center>
                              <%= status %>
                            </center>
                          </td>
                          <td width="25%" class="TableCell">
                            <center>
                              <%= startStr %>
                            </center>
                          </td>
                          <td width="25%" class="TableCell">
                            <center>
                              <%= stopStr %>
                            </center>
                          </td>
                        </tr>
                        <%
            } // end while loop       
       %>
                      </table>
                      <p>&nbsp;</p>
                    </td>
                  </tr>
                </table>
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
