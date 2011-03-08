<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ page import="com.cannontech.loadcontrol.data.LMProgramDirectGear" %>
<%@ page import="com.cannontech.database.db.device.lm.GearControlMethod"%>
<%@ page import="java.util.Date" %>
<%@ page import="com.cannontech.util.ParamUtil" %>
<%@ include file="lm_header.jsp" %>

<%

String cmd = request.getParameter("cmd");
String itemid = request.getParameter("itemid");


boolean isPageGood = true;

if( cmd == null || itemid == null )
{
    String redirect = ServletUtil.createSafeRedirectUrl(request, lmSession.DEF_REDIRECT);
    CTILogger.warn(
		"No command or item has been selected, redirecting request to: " + redirect);
	response.sendRedirect(redirect);
	return;
}

WebCmdMsg cmdMsg = LMCmdMsgFactory.createCmdMsg( cmd, new Integer(itemid), null, lcCache );
if( cmdMsg.getLMData() == null )
{
	CTILogger.warn( 
		"Unable to create valid message for the given command (cmd = " + cmd +
		", ItemID = " + itemid + "), ignoring request" );

	isPageGood = false;
}

%>
<%@page import="com.cannontech.core.service.DateFormattingService"%>
<%@page import="com.cannontech.database.data.device.lm.LMScenario"%>
<%@page import="com.cannontech.user.YukonUserContext"%>
<%@page import="com.cannontech.servlet.YukonUserContextUtils"%>
<% YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(pageContext); %>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" type="text/css" href="css/lm.css">
<link rel="stylesheet" type="text/css" href="../WebConfig/yukon/styles/calendarControl.css">
<script type="text/javascript" src="../JavaScript/calendarControl.js"></script>
<script type="text/javascript" src="../JavaScript/drag.js"></script>

<script language="JavaScript">

function validateInput()
{
	//validate the inputs first
	if( document.cmdForm.startdate != null && document.cmdForm.stopdate != null
		&& !document.cmdForm.stopbutton[0].checked //see if the Manual Stop option is checked
		&& !validateDate(
			document.cmdForm.startdate.value + ' ' + document.cmdForm.startTime1.value,
			document.cmdForm.stopdate.value + ' ' +	document.cmdForm.stopTime1.value) )
	{
		alert('The Start date/time must be before the Stop date/time');
		return false;
	}
	
	//Check for a colon so as to prevent accidental control starts or incorrect control times
	if( document.cmdForm.startTime1 != null && !document.cmdForm.startTime1.disabled && document.cmdForm.startTime1.value.indexOf(":") != 2) {
		alert('Unable to determine a valid start time.  Start time should be in the form HH:mm.');
		return false;
	}
	
	//Check for a colon so as to prevent accidental control stops or incorrect control times
	if( document.cmdForm.stopTime1 != null && !document.cmdForm.stopTime1.disabled && document.cmdForm.stopTime1.value.indexOf(":") != 2) {
		alert('Unable to determine a valid stop time.  Stop time should be in the form HH:mm.');
		return false;
	}
	
	//Check length so as to prevent accidental control starts or incorrect control times
	if( document.cmdForm.startTime1 != null && !document.cmdForm.startTime1.disabled && document.cmdForm.startTime1.value.length != 5) {
		alert('Unable to determine a valid start time.  Start time should be in the form HH:mm.');
		return false;
	}
	
	//Check length so as to prevent accidental control stops or incorrect control times
	if( document.cmdForm.stopTime1 != null && !document.cmdForm.stopTime1.disabled && document.cmdForm.stopTime1.value.length != 5) {
		alert('Unable to determine a valid stop time.  Stop time should be in the form HH:mm.');
		return false;
	}
	
	//Check length so as to prevent accidental control stops or incorrect control times
    if( document.cmdForm.startTime1 != null && !document.cmdForm.startTime1.disabled ) {
        var time = document.cmdForm.startTime1.value.split(':');
        if(parseInt(time[0]) > 24 ||
            parseInt(time[0]) < 0){
            alert('Unable to determine a valid start time.  Start time should be in the form HH:mm.');
            return false;
        }
        
    }
	
	//Check length so as to prevent accidental control stops or incorrect control times
    if( document.cmdForm.stopTime1 != null && !document.cmdForm.stopTime1.disabled ) {
        
        var time = document.cmdForm.stopTime1.value.split(':');
        if(parseInt(time[0]) > 24 ||
            parseInt(time[0]) < 0){
            alert('Unable to determine a valid stop time.  Stop time should be in the form HH:mm.');
            return false;
        }
        
    }
	opener.setTimeout("window.location.reload(true)", 2000);
    
   	return true;
}

function setStartAble( radioChk )
{
	var val = radioChk.value == "startat" && radioChk.checked;	
	document.cmdForm.startdate.disabled = !val;
	document.cmdForm.startTime1.disabled = !val;
}

function setStopAble( radioChk )
{
	var val = radioChk.value == "stopat" && radioChk.checked;	
	document.cmdForm.stopdate.disabled = !val;
	document.cmdForm.stopTime1.disabled = !val;
	<% if( ILCCmds.PROG_STOP.equals(cmd) ) {   %>
		document.cmdForm.useStopGear.disabled = !val;
	<% }
    if( ILCCmds.PROG_START.equals(cmd) ) {   %>
	    if (radioChk.value == "stopmanual" && radioChk.checked) {
	        document.getElementById('tgconfig').style.display = 'none';
	    }
	    if (val) {
            showtgconfig ($('gearnum').options[$('gearnum').selectedIndex].value);
	    }
	<%}%>
}

function activateStopGear()
{
	var val = document.getElementById('useStopGear').checked;	
	<% if( ILCCmds.PROG_STOP.equals(cmd) ) {   %>
		document.cmdForm.stopGearNum.disabled = !val;
	<% } %>
}

<%if (ILCCmds.PROG_START.equals(cmd)) {%>
    Event.observe (window, 'load', function () {
                                                dateChanged ('start');
                                                dateChanged ('stop');
                                                });
    Event.observe (window, 'load', function () {showtgconfig (1)});
    Event.observe (window, 'load', function () {GreyBox.preloadGreyBoxImages()});

<%}%>
</script>
</head>

<%Date nowStartOrStop = new Date();
pageContext.setAttribute("nowDate", nowStartOrStop);%>

<body leftmargin="0" topmargin="0" bgcolor="#FFFFFF">
	<div align="center">

	<form name="cmdForm" method="post" action="<%=request.getContextPath()%>/servlet/LCConnectionServlet" onsubmit="return validateInput()">
		<input type="hidden" name="cmd" value="<%= cmd %>" >
		<input type="hidden" name="itemid" value="<%= itemid %>" >
        <input type="hidden" name="redirectURL" value="/close.jsp" >
        <input type="hidden" name="adjustments" id="h_adjustments" value=""/>                       
        <input type="hidden" name="cancelPrev" id="cancelPrev" value=""/>  
        <input type="hidden" name="currentUserID" value="<%=((LiteYukonUser)session.getAttribute(ServletUtil.ATT_YUKON_USER)).getUserID()%>" />                     
    
    <div class="confMsg"><BR><%= cmdMsg.getHTMLTextMsg() %></div>
		<BR>
<% if ( isPageGood ) { %>


<%
	if( ILCCmds.PROG_START.equals(cmd) || ILCCmds.AREA_START_PROGS.equals(cmd) || ILCCmds.SC_START.equals(cmd) )
	{
		LMProgramBase prg = null;
%>
	<div class="TableCell"> 
	  <div align="center">Select either Start Now or Start at and select a Date and Time:</div>
	</div>

     <input type="hidden" name="h_starttime" id="h_starttime" value=""/>                       
     <input type="hidden" name="h_stoptime" id="h_stoptime" value=""/>                       
    <cti:formatDate value="${nowDate}" type="DATE" var="startAtThisMoment" />
    <cti:formatDate value="${nowDate}" type="TIME24H" var="startAtThisMomentHHMM" />
    
    <table width="350" border="1" cellspacing="0" cellpadding="6" align="center" valign="top" bgcolor="#FFFFFF">
      <tr> 
        <td> 
          <table width="349" border="0" cellspacing="0" cellpadding="3" align="center">
<%
			String gearName = "";
            Integer gearPeriod = new Integer (0);
            if( ILCCmds.PROG_START.equals(cmd) )
			{
                    prg = (LMProgramBase)lcCache.getProgram( new Integer(itemid) );

                    java.util.List gearList = 
                        ( prg instanceof IGearProgram
                            ? ((IGearProgram)prg).getDirectGearVector()
                            : new java.util.Vector() );
                    for (int i=0; i < gearList.size(); i++) {
                        LMProgramDirectGear gear = (LMProgramDirectGear)gearList.get(i);
                        if (gear.getControlMethod().equals(GearControlMethod.TargetCycle)) {
                            gearName = gear.getGearName();
                            gearPeriod = gear.getMethodPeriod();
                        %>
                         <input type="hidden" name="targetcyclegear" id="tcg_<%=(i + 1)%>" />                       
                        
<%
                        }
                    }                    

%>	  
            <tr valign="top"> 
              <td width="85" class="TableCell"> 
                <div align="right"><b>Start gear: </b></div>
              </td>
              <td width="25">&nbsp;</td>
              <td width="36">&nbsp;</td>
              <td width="179">
                  <select id="gearnum" name="gearnum" onchange="showtgconfig (this.options[this.selectedIndex].value);" >
				<%
					if( gearList.size() <= 0 )
						gearList = java.util.Arrays.asList(
							new String[]{"Gear 1","Gear 2","Gear 3","Gear 4"} );

					for( int i = 0; i < gearList.size(); i++ )
					{
				%>
						<option value=<%= i+1 %> <%= (i == 0 ? "selected" : "") %>  >
							<%= gearList.get(i).toString() %>
						</option>
				<%	}
               %>
         
              
              </select>
			  
			  </td>
              <td width="10" class="TableCell" >  
                <a href="javascript:void(0);" 
                 onclick="return openConfigWin('<%=gearName%>', gearnum.options[gearnum.selectedIndex].value, <%=gearPeriod%>);"
                 name="tgconfig" id="tgconfig" > Add Target Adjustments </a>
              </td>
            </tr>

<%
			}
%>
            <tr valign="top"> 
              <td width="85" class="TableCell"> 
                <div align="right"><b>Start now: </b></div>
              </td>
              <td width="25"> 
                <input type="radio" id="startbutton" name="startbutton" value="startnow" onClick="setStartAble(this);dateChanged('start'); " checked>
                <br>
              </td>
              <td width="36">&nbsp;</td>
              <td width="179">&nbsp;</td>
            </tr>
			
            <tr> 
              <td width="85" class="TableCell"> 
                <div align="right"><b>Start at: </b></div>
              </td>
              <td width="25"> 
                <input type="radio" name="startbutton" value="startat" onClick="setStartAble(this);dateChanged('start');">
              </td>
              <td width="36" class="TableCell"> 
                <div align="right">Date: </div>
              </td>
              <td width="179"> 
                
                <tags:dateInputCalendar fieldName="startdate" 
                                        fieldValue="${startAtThisMoment}" />
				
                </td>
            </tr>
            <tr> 
              <td width="85" height="85">&nbsp; </td>
	            <td width="25" height="85">&nbsp;</td>
              <td width="36" height="85" class="TableCell"> 
                <div align="right">Time: </div>
              </td>
              <td width="179" height="85" > 
                  <div> 
                    <input type="text" name="startTime1"
                    	value="${startAtThisMomentHHMM}"
                    	size="5" onchange="dateChanged('start');" disabled/>
                   	<font class="TableCell">(HH:mm)</font>
                  </div>
              </td>
            </tr>
			
          </table>
        </td>
      </tr>
    </table>
	<br/>
<%	
	}
	
	if( ILCCmds.PROG_CHANGE_GEAR.equals(cmd) ) {
		LMProgramBase prg = null;
%>
	<div class="TableCell"> 
	  <div align="center">Select the gear you wish to change to:</div>
	</div>

    <table width="350" border="1" cellspacing="0" cellpadding="6" align="center" valign="top" bgcolor="#FFFFFF">
      <tr> 
        <td> 
          <table width="349" border="0" cellspacing="0" cellpadding="3" align="center">
<%
            prg = (LMProgramBase)lcCache.getProgram( new Integer(itemid) );
            if(prg.getProgramStatus() == LMProgramBase.STATUS_MANUAL_ACTIVE) {
				String gearName = "";
	            Integer gearPeriod = new Integer (0);
	            prg = (LMProgramBase)lcCache.getProgram( new Integer(itemid) );
	            java.util.List gearList = ( prg instanceof IGearProgram
	                            ? ((IGearProgram)prg).getDirectGearVector()
	                            : new java.util.Vector() );
	%>	  
	            <tr valign="top"> 
	              <td width="85" class="TableCell"> 
	                <div align="right"><b>Gear: </b></div>
	              </td>
	              <td width="25">&nbsp;</td>
	              <td width="36">&nbsp;</td>
	              <td width="179">
	                  <select name="gearnum" onchange="showtgconfig (this.options[this.selectedIndex].value);" >
					<%
	
						if( gearList.size() <= 0 )
							gearList = java.util.Arrays.asList(
								new String[]{"Gear 1","Gear 2","Gear 3","Gear 4"} );
	
						for( int i = 0; i < gearList.size(); i++ )
						{
					%>
							<option value=<%= i+1 %> <%= (i == 0 ? "selected" : "") %>  >
								<%= gearList.get(i).toString() %>
							</option>
					<%	}
	               %>
	         
	              
	              </select>
				  
				  </td>
	            </tr>
	          </table>
	        </td>
	      </tr>
	    </table>
		<br/>
<%	
			}
			else { 
	%>
				<tr valign="top"> 
		              <td width="200" class="TableCell"> 
		                <div align="center"><b>Gear Change Unavailable </b></div>
		              </td>
		            </tr>
		            
		         	<tr valign="top"> 
		              <td width="200" class="TableCell"> 
		                <div align="center">Program must be in a Manually Active state.</div>
		              </td>
		            </tr>
		          </table>
		        </td>
		      </tr>
		    </table>
			<br/>
<% 			}
	}
	if( ILCCmds.PROG_START.equals(cmd) || ILCCmds.PROG_STOP.equals(cmd) 
		|| ILCCmds.AREA_START_PROGS.equals(cmd) || ILCCmds.AREA_STOP_PROGS.equals(cmd) 
		|| ILCCmds.SC_START.equals(cmd) || ILCCmds.SC_STOP.equals(cmd) )
	{
%>

	<div class="TableCell"> 
	  <div align="center">Select either of the following stop options:</div>
	</div>	
    <table width="350" border="1" cellspacing="0" cellpadding="6" align="center" class="TableCell" bgcolor="#FFFFFF">
      <tr> 
        <td height="145"> 
          <table width="350" border="0" cellspacing="0" cellpadding="3" align="center">
            <tr valign="top"> 
              <td width="81"> 
                <div align="right" class="TableCell"><b>
				<%= (ILCCmds.PROG_STOP.equals(cmd) || ILCCmds.AREA_STOP_PROGS.equals(cmd) || ILCCmds.SC_STOP.equals(cmd) ? "Stop now: " : "Manual stop: ") %></b></div>
              </td>
              <td width="17"> 
                <input id="manualstop" type="radio" name="stopbutton" onClick="setStopAble(this)" 
				value="<%= (ILCCmds.PROG_STOP.equals(cmd) || ILCCmds.AREA_STOP_PROGS.equals(cmd) || ILCCmds.SC_STOP.equals(cmd) ? "stopnow" : "stopmanual") %>"
				<%= (ILCCmds.PROG_STOP.equals(cmd) || ILCCmds.AREA_STOP_PROGS.equals(cmd) || ILCCmds.SC_STOP.equals(cmd) ? "checked" : "") %>>
                <br>
              </td>
              <td width="34">&nbsp;</td>
              <td width="170">&nbsp;</td>
            </tr>
            <tr> 
              <td width="81">
                <div align="right" class="TableCell"><b>Stop at: </b></div>
              </td>
              <td width="17"> 
                <input type="radio" name="stopbutton" onClick="setStopAble(this)" 
				value="stopat"
				<%= (ILCCmds.PROG_START.equals(cmd) || ILCCmds.AREA_START_PROGS.equals(cmd) || ILCCmds.SC_START.equals(cmd) ? "checked" : "") %>>
              </td>
              <td width="34"> 
                <div align="right" class="TableCell">Date: </div>
              </td>
              <td width="170"> 
<%
 //add 4 hours worth of millis = 14400000
 Date stpDate = new Date();
 stpDate.setTime( nowStartOrStop.getTime() + 14400000 );
 if (ILCCmds.PROG_STOP.equals(cmd)) { 
     Date stopDate = null;
     LMProgramBase lmProg = lcCache.getProgram(Integer.parseInt(itemid));
     String hourMinStr = (String) LCUtils.getProgramValueAt(lmProg, ProgramTableModel.STOP_TIME, userContext);
     if((hourMinStr != null) && (!hourMinStr.contains("-"))) {
         stopDate = new Date(hourMinStr);
         stpDate.setTime( stopDate.getTime());
     }
 } else if(ILCCmds.AREA_STOP_PROGS.equals(cmd)) {
     Date stopDate = new Date();
     LMControlArea lmContArea = lcCache.getControlArea(Integer.parseInt(itemid));
     String hourMinStr = (String) LCUtils.getControlAreaValueAt(lmContArea, ControlAreaTableModel.STOP_TIME, userContext);
     if((hourMinStr != null) && (!hourMinStr.contains("-"))) {
         String[] temp = hourMinStr.split(":");
         int hours = Integer.parseInt(temp[0]);
         int mins = Integer.parseInt(temp[1]);
         stopDate.setHours(hours);
         stopDate.setMinutes(mins);
         stopDate.setSeconds(0);
         stpDate.setTime( stopDate.getTime());
     }
 }
 
 pageContext.setAttribute("stopNowDate", stpDate);
%>
<cti:formatDate value="${stopNowDate}" type="DATE" var="stopAtThisMoment" />
<cti:formatDate value="${stopNowDate}" type="TIME24H" var="stopAtThisMomentHHMM" />
 
                <tags:dateInputCalendar fieldName="stopdate" 
                                        fieldValue="${stopAtThisMoment}"
                                        disabled="<%= (ILCCmds.PROG_STOP.equals(cmd) || ILCCmds.AREA_STOP_PROGS.equals(cmd) || ILCCmds.SC_STOP.equals(cmd) ? true : false) %>" />
 
              </td>
            </tr>
            <tr> 
              <td width="81" height="85">&nbsp; </td>
              <td width="17" height="85">&nbsp;</td>
              <td width="34" height="85"> 
                <div align="right" class="TableCell">Time: </div>
              </td>
              <td width="170" height="85" >
	              <input type="text" name="stopTime1" 
	              		value="${stopAtThisMomentHHMM}" size="5"
						<%= (ILCCmds.PROG_STOP.equals(cmd) || ILCCmds.AREA_STOP_PROGS.equals(cmd) || ILCCmds.SC_STOP.equals(cmd) ? "disabled" : "") %> 
                        onchange="dateChanged('stop');"/>
	              	<font class="TableCell">(HH:mm)</font>
              </td>
            </tr>
            <%if (ILCCmds.PROG_STOP.equals(cmd)) {%>
	            <tr>
	            	<cti:checkProperty property="DirectLoadcontrolRole.ALLOW_STOP_GEAR_ACCESS">
	            		<tr valign="top"> 
			              <td width="85" class="TableCell"> 
			                <div id="stopGearLabel" align="right"><b>Stop gear: </b></div>
			              </td>
			              <td width="25">&nbsp;</td>
			              <td width="36">&nbsp;
				              <input type="checkbox" id="useStopGear" name="useStopGear" onClick="activateStopGear()" disabled/>
			              </td>
			              <td width="179">
				                <select id="stopGearNum" name="stopGearNum" disabled>
								<%
									LMProgramBase prgForStop = (LMProgramBase)lcCache.getProgram( new Integer(itemid) );
	            					java.util.List stopGearList = ( prgForStop instanceof IGearProgram 
	            							? ((IGearProgram)prgForStop).getDirectGearVector()
	                            			: new java.util.Vector() );
									if( stopGearList.size() <= 0 )
										stopGearList = java.util.Arrays.asList(
											new String[]{"Gear 1","Gear 2","Gear 3","Gear 4"} );
				
									for( int i = 0; i < stopGearList.size(); i++ )
									{
								%>
										<option value=<%= i+1 %> <%= (i == 0 ? "selected" : "") %>  >
											<%= stopGearList.get(i).toString() %>
										</option>
								<%	}
				               %>
				              </select>
						  </td>
			            </tr>			
	            	</cti:checkProperty>	
	            </tr>
	      	<%} %>
          </table>
        </td>
      </tr>
    </table>
	<br/>
	
	<div class="TableCell">
        <% if (!(ILCCmds.PROG_STOP.equals(cmd)) && 
               !(ILCCmds.AREA_STOP_PROGS.equals(cmd)) && 
               !(ILCCmds.SC_STOP.equals(cmd))) { %>
		Constraint Handling:
       <select name="constraint">
        <%
        	java.util.List constraints = lmSession.getConstraintOptions(
        		(LiteYukonUser)session.getAttribute(ServletUtil.ATT_YUKON_USER) );

        	String defConstraint = lmSession.getConstraintDefault(
        		(LiteYukonUser)session.getAttribute(ServletUtil.ATT_YUKON_USER) );

			for( int j = 0; j < constraints.size(); j++ )
			{
				String curr = (String)constraints.get(j);
		%>
          <option value="<%= curr %>"
          		<%= (curr.equals(defConstraint) ? "selected" : "") %> >
              <%= curr %>
		  </option>
        <%
			}
		%>
        </select>
        <% 
        }
        %>
	</div>
	<br/>
	
<%
	}

	if( ILCCmds.GRP_SHED.equals(cmd) )
	{
%>

<table width="350" border="1" cellspacing="0" cellpadding="6" align="center" valign="top" bgcolor="#FFFFFF">
      <tr> 
        <td> 
          <table width="349" border="0" cellspacing="0" cellpadding="3" align="center">
            <tr valign="top"> 
              <td width="85" class="TableCell"> 
                <div align="right"><b>Shed time: </b></div>
              </td>
              <td width="25">&nbsp;</td>
              <td width="36">&nbsp;</td>
              <td width="179"> 
                <select name="duration">
                  <%
				  		for( int i = 0; i < LCUtils.SHED_STRS.length; i++ )
						{
				%>
                  <option value="<%= LCUtils.SHED_STRS[i] %>" <%= (i == 0 ? "selected" : "") %> > 
                  <%= LCUtils.SHED_STRS[i] %> </option>
                  <%		
				  		}
				%>
                </select>
              </td>
            </tr>
			
            <tr valign="top"> 
              <td width="85">&nbsp;</td>
              <td width="25">&nbsp;</td>
              <td width="36">&nbsp;</td>
              <td width="179"><div align="right" class="TableCell"><b>Note: Ripple switches ignore this setting</b></div></td>
            </tr>
			
          </table>
        </td>
      </tr>
    </table>

<%
	}

	if( ILCCmds.GRP_TRUE_CY.equals(cmd) || ILCCmds.GRP_SMRT_CY.equals(cmd) )
	{
%>

<table width="350" border="1" cellspacing="0" cellpadding="6" align="center" valign="top" bgcolor="#FFFFFF">
      <tr> 
        <td> 
          <table width="349" border="0" cellspacing="0" cellpadding="3" align="center">
            <tr valign="top"> 
              <td width="85" class="TableCell"> 
                <div align="right"><b>Cycle %: </b></div>
              </td>
              <td width="25">&nbsp;</td>
              <td width="36">&nbsp;</td>
              <td width="179"> 
                <select name="cyclepercent">
                  <%
				  		for( int i = 1; i <= 100; i++ )
						{
				%>				
                  <option value="<%= i %>" <%= (i == 50 ? "selected" : "") %> > 
                  <%= i %> </option>
                  <%		
				  		}
				%>
                </select>
              </td>
            </tr>

            <tr valign="top"> 
              <td width="85" class="TableCell"> 
                <div align="right"><b>Period length: </b></div>
              </td>
              <td width="25">&nbsp;</td>
              <td width="36">&nbsp;</td>
              <td width="179"> 
                <select name="duration">
                <%
				  		for( int i = 1; i <= 60; i++ )
						{
				%>				
                  <option value="<%= i + (i == 1 ? " minute" : " minutes") %>" <%= (i == 30 ? "selected" : "") %> > 
                  <%= i + (i == 1 ? " minute" : " minutes") %> </option>
                <%		
				  		}
				%>
                </select>
              </td>
            </tr>
			
            <tr valign="top"> 
              <td width="85" class="TableCell"> 
                <div align="right"><b>Period count: </b></div>
              </td>
              <td width="25">&nbsp;</td>
              <td width="36">&nbsp;</td>
              <td width="179"> 
                <select name="periodcnt">
                  <%
				  		for( int i = 1; i < 49; i++ )
						{
				%>				
                  <option value="<%= i %>" <%= (i == 8 ? "selected" : "") %> > 
                  <%= i %> </option>
                  <%		
				  		}
				%>
                </select>
              </td>
            </tr>

			
          </table>
        </td>
      </tr>
    </table>

<%
	}

	if( ILCCmds.AREA_TRIG_CHG.equals(cmd) )
	{	
		LMControlArea cntrlArea = (LMControlArea)lcCache.getControlArea( new Integer(itemid) );
		for( int i = 0; i < cntrlArea.getTriggerVector().size(); i++ )
		{
			LMControlAreaTrigger trigger = (LMControlAreaTrigger)cntrlArea.getTriggerVector().get(i);
%>
	<div class="TableCell"> 
	  <div align="center">Trigger #<%= i+1 %> settings</div>
	</div>	
	<table width="350" border="1" cellspacing="0" cellpadding="6" align="center" valign="top" bgcolor="#FFFFFF">
      <tr> 
        <td> 
          <table width="349" border="0" cellspacing="0" cellpadding="3" align="center">
            <tr> 
              <td width="85" class="TableCell"> 
                <div align="right"><b>New threshold: </b></div>
              </td>
              <td width="25">&nbsp;</td>
              <td width="36">&nbsp;</td>
              <td width="179">
			  	<input type="text" name="dblarray1" value="<%= trigger.getThreshold() %>" size="10">
              </td>
            </tr>

            <tr> 
              <td width="85" class="TableCell"> 
                <div align="right"><b>New restore offset: </b></div>
              </td>
              <td width="25">&nbsp;</td>
              <td width="36">&nbsp;</td>
              <td width="179">
			  	<input type="text" name="dblarray2" value="<%= trigger.getMinRestoreOffset() %>" size="10">
              </td>
            </tr>

          </table>
        </td>
      </tr>
    </table>
	<br>
<%
		}
	}

	if( ILCCmds.AREA_DAILY_CHG.equals(cmd) )
	{
	    LMControlArea cntrlArea = lcCache.getControlArea( new Integer(itemid) );
        String winStart = (String)LCUtils.getControlAreaValueAt(cntrlArea, ControlAreaTableModel.START_TIME, userContext);
        String winStop = (String)LCUtils.getControlAreaValueAt(cntrlArea, ControlAreaTableModel.STOP_TIME, userContext);
%>
	<div class="TableCell"> 
	  <div align="center">Select the new window Start and Stop times:</div>
	</div>
    <table width="350" border="1" cellspacing="0" cellpadding="6" align="center" valign="top" bgcolor="#FFFFFF">
      <tr> 
        <td> 
          <table width="349" border="0" cellspacing="0" cellpadding="3" align="center">
		  
            <tr valign="top"> 
            <tr> 
              <td width="100" class="TableCell"> 
                <div align="right"><b>New start time: </b></div>
              </td>
              <td width="179" height="85" > 
                  <div> 
                    <input type="text" name="startTime1"
                    	value="<%= winStart %>" size="5"/>
                    	<font class="TableCell">(HH:mm)</font>
                  </div>
              </td>
              
            </tr>

            <tr valign="top"> 
            <tr> 
              <td width="100" class="TableCell"> 
                <div align="right"><b>New stop time: </b></div>
              </td>
              <td width="179" height="85" > 
                  <div> 
                    <input type="text" name="stopTime1"
                    	value="<%= winStop %>" size="5"/>
                    	<font class="TableCell">(HH:mm)</font>
                  </div>
              </td>
            </tr>

          </table>
        </td>
      </tr>
    </table>
	<BR>
<%	
	}

	if( ILCCmds.AREA_START_PROGS.equals(cmd) || ILCCmds.AREA_STOP_PROGS.equals(cmd) )
	{
		LMControlArea cntrlArea = (LMControlArea)lcCache.getControlArea( new Integer(itemid) );
%>
	<div class="TableCell"> 
	  <div align="center">Select the programs you want to operate:</div>
	</div>	
    <table width="350" border="1" cellspacing="0" cellpadding="6" align="center" class="TableCell" bgcolor="#FFFFFF">

      <tr> 
        <td height="145"> 
          <table width="350" border="1" cellspacing="0" cellpadding="3" align="center">

		  <tr valign="top" class="HeaderCell"> 
			<td width="40"><div align="center">
				<input type="checkbox" name="allChks"
					CHECKED
					value="true" onClick="checkAll(cmdForm.allChks, document.getElementsByName('dblarray1') )">
				All</div>
			</td>
			<td width="147"><div align="center">Program</div></td>

<% if( ILCCmds.AREA_START_PROGS.equals(cmd) ) { %>			
			<td width="34"><div align="center">Gear</div></td>
<% } %>

			<td width="81"><div align="center">State</div></td>
		  </tr>

<%
		java.util.List progList = cntrlArea.getLmProgramVector();
		java.util.Collections.sort( 
				progList,
				ProgramTableModel.PROGRAM_NAME_COMPARATOR );

		for( int i = 0; i < progList.size(); i++)
		{
			LMProgramBase prg = (LMProgramBase)progList.get(i);
%>
            <tr valign="top">
              <td width="40">
				<input type="checkbox" name="dblarray1" CHECKED
					value=<%= prg.getYukonID() %> >
              </td>
              <td width="147">
                <div class="TableCell">
				<%= LCUtils.getProgramValueAt(prg, ProgramTableModel.PROGRAM_NAME, userContext) %>
				</div>
              </td>
              
              
              
<% 
    if( ILCCmds.AREA_START_PROGS.equals(cmd) ) {

        java.util.List<LMProgramDirectGear> gearList = 
                        (prg instanceof IGearProgram
                            ? (java.util.List<LMProgramDirectGear>)((IGearProgram)prg).getDirectGearVector()
                            : new java.util.Vector() );
        
%>

              <td width="34"> 
                <div align="right" class="TableCell">
                <select name="dblarray2">
<%
        for(LMProgramDirectGear gear : gearList) {
%>				
                  <option value="<%= gear.getGearNumber() %>"> 
	                  <%= gear.getGearName() %>
				  </option>
<%		
        }
%>
                </select></div>			
              </td>
<%
    }
%>




              <td width="81">
                <div class="TableCell" align="center"><font color="<%= LCUtils.getFgColor(prg) %>">
				  <%= LCUtils.getProgramValueAt(prg, ProgramTableModel.CURRENT_STATUS, userContext) %>
				</font></div>
			  </td>
            </tr>
<%
		}
%>
			
          </table>
        </td>
      </tr>
	  
    </table>
<%	
	}

	if( ILCCmds.SC_START.equals(cmd) || ILCCmds.SC_STOP.equals(cmd) )
	{
		LiteLMProgScenario[] programs = 
				DaoFactory.getLmDao().getLMScenarioProgs( new Integer(itemid).intValue() );
				
		LiteYukonPAObject[] scenarios = DaoFactory.getLmDao().getAllLMScenarios();
%>
	<div class="TableCell"> 
	  <div align="center">Select the programs you want to operate:</div>
	</div>	
    <table width="350" border="1" cellspacing="0" cellpadding="6" align="center" class="TableCell" bgcolor="#FFFFFF">

      <tr> 
        <td height="145"> 
          <table width="350" border="1" cellspacing="0" cellpadding="3" align="center">
		  <tr valign="top" class="HeaderCell"> 
			<td width="40"><div align="center">
				<input type="checkbox" name="allChks" value="true" onClick="checkAll(cmdForm.allChks, document.getElementsByName('dblarray1'))" checked>
				All</div>
			</td>
			<td width="127"><div align="center">Program</div></td>

<% if( ILCCmds.SC_START.equals(cmd) ) { %>			
			<td width="34"><div align="center">Gear</div></td>
<% } %>

			<td width="61"><div align="center">State</div></td>

			<td width="20"><div align="center">Start</div></td>

			<td width="20"><div align="center">Stop</div></td>

		  </tr>

<%
		for( int i = 0; i < programs.length; i++)
		{
			LiteLMProgScenario prg = programs[i];
			
			LMProgramBase prgBase = 
				(LMProgramBase)lcCache.getProgram( new Integer(programs[i].getProgramID()) );
			
			//program may not belong to a ControlArea, therefore would not be in the cache
			if( prgBase == null )
				continue;
%>
            <tr valign="top">
              <td width="40">
				<input type="checkbox" name="dblarray1" value=<%= prg.getProgramID() %> checked>
              </td>
              <td width="127">
                <div class="TableCell">
				<%= LCUtils.getProgramValueAt(prgBase, ProgramTableModel.PROGRAM_NAME, userContext) %>
				</div>
              </td>
<% 
    if( ILCCmds.SC_START.equals(cmd) ) { 

    	java.util.List<LMProgramDirectGear> gearList = 
            (prgBase instanceof IGearProgram
                ? (java.util.List<LMProgramDirectGear>)((IGearProgram)prgBase).getDirectGearVector()
                : new java.util.Vector() );

%>
              <td width="34"> 
                <div align="right" class="TableCell">
                <select name="dblarray2">
<%
        for(LMProgramDirectGear gear : gearList) {
%>				
                  <option value="<%= gear.getGearNumber() %>" <%= (gear.getGearNumber() == prg.getStartGear() ? "selected" : "") %> > 
                      <%= gear.getGearName() %>
                  </option>
<%		
        }
%>
                </select></div>			
              </td>
<% 
    }
%>
              <td width="61">
                <div class="TableCell" align="center"><font color="<%= LCUtils.getFgColor(prgBase) %>">
				  <%= LCUtils.getProgramValueAt(prgBase, ProgramTableModel.CURRENT_STATUS, userContext) %>
				</font></div>
			  </td>

              <td width="20">
                <div class="TableCell" align="center">
				  <%= CtiUtilities.decodeSecondsToTime(prg.getStartOffset()) %>
				</div>
			  </td>

              <td width="20">
                <div class="TableCell" align="center">
				  <%= CtiUtilities.decodeSecondsToTime(prg.getStopOffset()) %>
				</div>
			  </td>

            </tr>
<%
		}
%>
			
          </table>
        </td>
      </tr>
	  
    </table>
<%
	}
%>





	  <BR>
		<input type="submit" name="Submit2" value="Ok" class="defButton">
<% }  /* Ending of the isPageGood check */ %>
		<input type="submit" name="Submit" value="Cancel" class="defButton" onclick = "self.close(); return false;">
	</form>
	</div>

</body>
</html>