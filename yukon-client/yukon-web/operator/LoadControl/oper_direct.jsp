<%@ include file="oper_header.jsp" %>

<%@ page import="com.cannontech.message.macs.message.Schedule" %>
<%@ page import="com.cannontech.loadcontrol.data.LMProgramDirect" %>
<%@ page import="com.cannontech.yukon.IMACSConnection" %>

<%@ page import="java.util.TreeMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ArrayList" %> 
<%@ page import="java.util.Vector" %> 
<%@ page import="java.util.Iterator" %>
 
<cti:checkRole roleid="<%=DirectLoadcontrolRole.ROLEID%>">  
<%
   String pending = request.getParameter("pending");
     
   if( pending != null )
      out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"7;URL=oper_direct.jsp\">");
   else
      out.println("<META HTTP-EQUIV=\"refresh\" CONTENT=\"60\">");

    java.text.SimpleDateFormat dlcDateFormat = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm");
    dlcDateFormat.setTimeZone(tz);
     
    session = request.getSession(false);
                            
    // list to put this customers programs in, contains LMProgramDirect objects
    ArrayList ourPrograms = new ArrayList();

    long[] programIDs = com.cannontech.database.db.web.LMDirectOperatorList.getProgramIDs( user.getUserID() );       
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

    Class[] types2 = { Integer.class  };
    Object[][] schedIDs = com.cannontech.util.ServletUtil.executeSQL( dbAlias, "SELECT lmmacsscheduleoperatorlist.ScheduleID FROM lmmacsscheduleoperatorlist WHERE lmmacsscheduleoperatorlist.operatorloginid=" + user.getUserID()  + " ORDER BY lmmacsscheduleoperatorlist.ScheduleID", types2 );
    
    com.cannontech.servlet.MACSConnectionServlet connContainer = (com.cannontech.servlet.MACSConnectionServlet)
        application.getAttribute(com.cannontech.servlet.MACSConnectionServlet.SERVLET_CONTEXT_ID);
				
	IMACSConnection conn = connContainer.getIMACSConnection();
	
    if( connContainer != null )
    {
        conn = connContainer.getIMACSConnection();
    }
    
    // Contains 
    // key -> Schedule
    // value -> Integer (attribute)
    // Using a tree map to sort by schedulename
    // and a custom Comparator to do the ordering
    TreeMap schedules = new TreeMap(
        new java.util.Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                return ((Schedule) o1).getScheduleName().compareTo( ((Schedule) o2).getScheduleName() );
            }            
        });
    if( conn != null && schedIDs != null )
    {
        Schedule[] allSchedules = conn.retrieveSchedules();
          
        for( int i = 0; i < schedIDs.length; i++ )
        {
            if( schedIDs[0] != null )
            {
                for( int j = 0; j < allSchedules.length; j++ )
                {                    
                    if( allSchedules[j].getId() == ((Integer)schedIDs[i][0]).intValue() )
                    {                    
                        schedules.put( allSchedules[j],
                               (Integer) schedIDs[i][0] );                
                    }
                }
                
            }
        }
    }
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="LoadImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="253" height = "28" class="PageHeader">&nbsp;&nbsp;&nbsp;Load 
                  Response</td>
                <td width="235" valign="middle">&nbsp;</td>
                
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
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
            <table width="101" border="0" cellspacing="0" cellpadding="6" height="200">
              <tr> 
                <td height="20" valign="top"> 
                  <div align="left"> 
                    <p align="center" class="TableCell1">
                    <cti:checkProperty propertyid="<%=DirectLoadcontrolRole.INDIVIDUAL_SWITCH%>">
                    <b><a href="switch_commands.jsp" class="Link2"><br>
                      Individual Switch Commands</a></b>
                    </cti:checkProperty></p>
                  </div>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
<table width="657" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="650" class="Main">
      <center>
                    <p align="center"><b><br>
                      DIRECT CONTROL - PROGRAM SUMMARY</b><b><br>
                      <br>
                      </b>To start/stop a program or change the start/stop time, 
                      select the program name. </p>
                    </center>
      <p> 
      
      <%
         if( pending != null )
         {
      %>
      <center><font color="red">Request sent, please wait...</font></center>
      <%
         }
      %>


                  <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                    <tr> 
                      <td width="150" class="HeaderCell"> 
                        <div align="center">Program Name </div>
                      </td>
                      <td width="150" class="HeaderCell"> 
                        <div align="center">Current State </div>
                      </td>
                      <td width="150" class="HeaderCell"> 
                        <div align="center">Start Date/Time </div>
                      </td>
                      <td width="150" class="HeaderCell"> 
                        <div align="center">Stop Date/Time </div>
                      </td>
                    </tr>
                    <%
            //Loop through the user's programs            
                                                
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
                        actionUrl = "schedule_summary.jsp";                    
              
        %>
                    <tr> 
                      <td width="150" class="TableCell"> 
                        <center>
                          <a href="<%= actionUrl %>" class="Link1"><%= sched.getScheduleName() %></a> 
                        </center>
                      </td>
                      <td width="150" class="TableCell"> 
                        <center>
                          <%= sched.getCurrentState() %> 
                        </center>
                      </td>
                      <td width="150" class="TableCell"> 
                        <center>
                          <%= dlcDateFormat.format(sched.getNextRunTime()) + " " + tz.getDisplayName(tz.inDaylightTime(sched.getNextRunTime()), TimeZone.SHORT) %> 
                        </center>
                      </td>
                      <td width="150" class="TableCell"> 
                        <center>
                          <%= dlcDateFormat.format(sched.getNextStopTime()) + " " + tz.getDisplayName(tz.inDaylightTime(sched.getNextRunTime()), TimeZone.SHORT)%> 
                        </center>
                      </td>
                    </tr>
                    <%
            } // end schedule while loop 

            iter = ourPrograms.iterator();
            while( iter.hasNext() )
            {
                LMProgramDirect p = (LMProgramDirect) iter.next();

                String status = p.getProgramStatusString( p.getProgramStatus().intValue());
                String startStr = "-";               
                String stopStr = "-"; 
                String actionURI = "oper_direct.jsp";


                if( p.getStartTime().getTime().getTime() > com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime().getTime() )
                    startStr = dlcDateFormat.format(p.getStartTime().getTime()) + " " + tz.getDisplayName(tz.inDaylightTime(p.getStartTime().getTime()), TimeZone.SHORT);

                if( p.getStopTime().getTime().getTime() > com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime().getTime() )
                    stopStr =  dlcDateFormat.format(p.getStopTime().getTime()) + " " + tz.getDisplayName(tz.inDaylightTime(p.getStopTime().getTime()), TimeZone.SHORT);

                if( pending != null )
                    actionURI = "oper_direct.jsp";
                else
                if( status.equalsIgnoreCase("inactive") )                
                    actionURI = "oper_direct_start.jsp?id=" + p.getYukonID();               
                else
                if( status.equalsIgnoreCase("manual active") )
                    actionURI = "oper_direct_stop.jsp?id=" + p.getYukonID(); 
        %>
                    <tr> 
                      <td width="25%" class="TableCell">
                        <center>
                          <a href="<%= actionURI %>" class="Link1"><%= p.getYukonName() %></a>
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
            } // end program while loop              
       %>
                  </table>
      
      <p>&nbsp;</p></td>
  </tr>
</table>

          </td>
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
</body>
</html>
</cti:checkRole>
