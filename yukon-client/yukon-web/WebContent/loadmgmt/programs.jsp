<%@ include file="lm_header.jsp" %>

<%
	if( lmSession.getResponseProgs() != null )
	{
		CTILogger.warn( 
			"Found some program violations, " +
			"showing program violation page" );
%>
		<%@ include file="include/progviolation.jsp" %>
<%
		return;
	}
   
if( lmSession.getResponseProgs() != null )
{
	CTILogger.warn( 
		"Found some program violations, " +
		"redirecting request to: progviolation.jsp" ); 
  
	response.sendRedirect( "progviolation.jsp" );
	return;
}
  
String strID = request.getParameter("areaID");
if( strID == null )
{
	CTILogger.warn( 
		"No ControlArea has been selected, redirecting request to: " + lmSession.DEF_REDIRECT ); 

	response.sendRedirect( lmSession.DEF_REDIRECT );
	return;
}

LMControlArea lmCntrArea = (LMControlArea)lcCache.getControlArea( new Integer(strID) );
if( lmCntrArea == null )
{
	CTILogger.warn( 
		"Unable to find selected ControlArea (AreaID = " + strID +
		"), redirecting request to: " + lmSession.DEF_REDIRECT ); 

	response.sendRedirect( lmSession.DEF_REDIRECT );
	return;
}

NativeIntVector intVect = new NativeIntVector(16);
String doAllProgs = request.getParameter("allChks");

if( doAllProgs != null )
{
	java.util.List progList = lmCntrArea.getLmProgramVector();	
	for( int i = 0; i < progList.size(); i++)
	{
		LMProgramBase tempPrg = (LMProgramBase)progList.get(i);
		intVect.add( tempPrg.getYukonID().intValue() );
	}

}
else
{
	String[] strProgIDs = (request.getParameterValues("pid") == null ? new String[0] : request.getParameterValues("pid"));
	
	for( int i = 0; i < strProgIDs.length; i++ )
	  intVect.add( Integer.parseInt( strProgIDs[i] ) );
}

%>         

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="css/lm.css" type="text/css">
</head>


<body class="Background" leftmargin="0" topmargin="0" onload="reload();" >

<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="images/LoadImage.jpg">&nbsp;</td>
		  <td width="1" bgcolor="#000000"><img src="images/VerticalRule.gif" width="1"></td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="images/Header.gif">&nbsp;</td>
              </tr>
              <tr> 
                <td width="353" height = "28" class="PageHeader">&nbsp;&nbsp;&nbsp; 
                  Load Management
            		<% if( lmSession.getRefreshRate() < LMSession.REF_SECONDS_DEF ) {%>
            			<font class="refreshTxt"> 
            			(Auto-refresh in <%= lmSession.getRefreshRate() %>
            			seconds)</font>
            		<%
            			lmSession.setRefreshRate(LMSession.REF_SECONDS_DEF);
            		}%>
                </td>

                <td width="235" valign="middle">&nbsp;</td>
                <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../operator/Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="images/VerticalRule.gif" width="1"></td>
          </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="759" valign="top" bgcolor="#000000"> 
		  <td width="1" bgcolor="#000000" height="1"></td>
		</tr>
        <tr> 
          <td width="759" valign="middle" bgcolor="#FFFFFF">
			<table width="740" border="0" cellspacing="0" cellpadding="0" align="center" >
              <tr>
			  <td>
				<div align="left" class="NavText"><br>
				  <a href="controlareas.jsp">Control Areas</a> >
				  <a href="programs.jsp?areaID=<%= lmCntrArea.getYukonID() %>" >Programs</a>
				</div>
			  </td>
			  			  
                <td>
                
                <form >
                    <div align="right"><span class="MainText"><br>
                      Control Areas:</span> 

                      <select name="select" onchange="location=this.options[this.selectedIndex].value">

						  <% 
							Iterator iterAreas = lcCache.getAllControlAreas(
										(LiteYukonUser)session.getAttribute(ServletUtil.ATT_YUKON_USER) );
							
							while( iterAreas.hasNext() )
							{
		  					  LMControlArea tempArea = (LMControlArea)iterAreas.next();
		  					  
								String s = ( tempArea.toString().equals(lmCntrArea.toString()) 
	                  					 ? " selected" : "" );
						  %>                      
                        <option value="programs.jsp?areaID=<%= tempArea.getYukonID() %>" <%= s %>>
                        	<%= LCUtils.getControlAreaValueAt(tempArea, ControlAreaTableModel.AREA_NAME) %>
                        </option>
							<%
							}
							%>
                      </select>
                    </div>

                </form>

				</td>
              </tr>
            </table>
							  
            <table width="740" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr>
                <td width="740" valign="top" class="MainText"> 
                  <table width="744" border="1" align="center" cellpadding="2" cellspacing="0">
                    <tr valign="top" class="HeaderCell"> 
                      <td width="111"><div align="center"><%= ControlAreaTableModel.COLUMN_NAMES[ControlAreaTableModel.AREA_NAME]%></div></td>
                      <td width="47"><div align="center"><%= ControlAreaTableModel.COLUMN_NAMES[ControlAreaTableModel.CURRENT_STATE]%></div></td>
                      <td width="67"><div align="center"><%= ControlAreaTableModel.COLUMN_NAMES[ControlAreaTableModel.VALUE_THRESHOLD]%></div></td>
                      <td width="94"><div align="center"><%= ControlAreaTableModel.COLUMN_NAMES[ControlAreaTableModel.PEAK_PROJECTION]%></div></td>
                      <td width="50"><div align="center"><%= ControlAreaTableModel.COLUMN_NAMES[ControlAreaTableModel.ATKU]%></div></td>
                      <td width="40"><div align="center"><%= ControlAreaTableModel.COLUMN_NAMES[ControlAreaTableModel.PRIORITY]%></div></td>
                      <td width="75"><div align="center"><%= ControlAreaTableModel.COLUMN_NAMES[ControlAreaTableModel.TIME_WINDOW]%></div></td>
                      <td width="97"><div align="center">Graphs</div></td>
                      <td width="83"><div align="center">Reports</div></td>
                    </tr>
										
                    <tr valign="top">
                      <td width="111" class="TableCell">
                        <div name = "subPopup" align = "left" > 
                          <%= LCUtils.getControlAreaValueAt(lmCntrArea, ControlAreaTableModel.AREA_NAME) %></div>
                      </td>
                      <td width="47" class="TableCell">
                      <div name = "areastatus" class="lm_tip_cell" onMouseOver="itemid=<%= lmCntrArea.getYukonID() %>;menuAppear(event, 'areaMenu')" onMouseOut="menuDisappear(event, 'areaMenu')" >
                      	<font color="<%= LCUtils.getFgColor(lmCntrArea) %>"> 
                        <%= LCUtils.getControlAreaValueAt(lmCntrArea, ControlAreaTableModel.CURRENT_STATE) %> 
                       </font></div></td>
					  
                      <td width="67" class="TableCell" align="center">
							  <%= LCUtils.getTriggerText( lmCntrArea, ControlAreaTableModel.VALUE_THRESHOLD ) %>
							  </td>					  
		                      <td width="94" class="TableCell" align="center">
							  <%= LCUtils.getTriggerText( lmCntrArea, ControlAreaTableModel.PEAK_PROJECTION ) %>
							  </td>					  
		                      <td width="50" class="TableCell" align="center">
							  <%= LCUtils.getTriggerText( lmCntrArea, ControlAreaTableModel.ATKU ) %>
							  </td>					  
		
		                      <td width="40" class="TableCell" align="center">
								<%= LCUtils.getControlAreaValueAt(lmCntrArea, ControlAreaTableModel.PRIORITY) %>
							  </td>
							  
		                      <td width="75" class="TableCell">
								<%= LCUtils.getControlAreaValueAt(lmCntrArea, ControlAreaTableModel.TIME_WINDOW) %>
							  </td>
					  
					  
                      <td width="97" class="TableCell"> 
                        <select name="selectGraph" onchange="location = this.options[this.selectedIndex].value;">
                          <option value="AllControlAreas.jsp">Today's Load</option>
                          <option value="AllControlAreas.jsp"></option>
                        </select>
                      </td>
                      <td width="83" class="TableCell"> 
                        <select name="select3">
                          <option>Summary</option>
                          <option></option>
                        </select>
                      </td>
                    </tr>
                  </table>
              </tr>
		</table>

            <br>
                  <table width="740" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr>
                      <td>
                        <table width="740" border="0" cellspacing="0" cellpadding="0">
					<form>  
						<input type="hidden" name="areaID" value="<%= lmCntrArea.getYukonID() %>" >					                            
                          <tr> 
                    <td class="HeaderCell">
					<div align="left">
					   &nbsp;&nbsp;Programs for : <font color="##666699"><%= lmCntrArea.getYukonName() %></font>
					   <BR>
					</div>
					</td>

					<td class="HeaderCell">
					<div align="right">
					  Control Scenario:
					  <select name="scen_box" >
<%
						LiteYukonPAObject[] scenarios = LMFuncs.getAllLMScenarios();
						for( int j = 0; j < scenarios.length; j++ )
						{
%>
						  <option value="<%=scenarios[j].getYukonID()%>"
						  <%= (j == 0 ? " selected" : "" ) %>>
						  <%= scenarios[j] %></option>
<%
						}
%>
					  </select>
					<BR>
					<span align="right" class="NavText">
					  <a href="" name="sc_start" 
					  onClick="itemid=document.getElementById('scen_box').options[document.getElementById('scen_box').selectedIndex].value; showConfirmWin(this); return false;">
					  [Start]</a> 
					  <a href="" name="sc_stop" 
					  onClick="itemid=document.getElementById('scen_box').options[document.getElementById('scen_box').selectedIndex].value; showConfirmWin(this); return false;">
					  [Stop]</a> 
					</span>
					</div>
					</td>
					</tr>
					</form>
					  
                        </table>
                      </td>
                    </tr>
                  </table>
                    
            <table width="744" border="1" align="center" cellpadding="2" cellspacing="0">

		<form name="chkFormAll" action="programs.jsp">
		<input type="hidden" name="areaID" value="<%= lmCntrArea.getYukonID() %>" >
              <tr valign="middle" class="HeaderCell">              
               <td width="137">
						<input type="checkbox" name="allChks" value="true" onClick="this.form.submit()"
					   	<% if( doAllProgs != null ) { out.print(" checked"); } %> >
							<%= ProgramTableModel.columnNames[ProgramTableModel.PROGRAM_NAME] %>
					</td>

                <td width="84"><div align="center"><%= ProgramTableModel.columnNames[ProgramTableModel.CURRENT_STATUS] %></div></td>
                <td width="133"><div align="center"><%= ProgramTableModel.columnNames[ProgramTableModel.START_TIME] %></div></td>
                <td width="129"><div align="center"><%= ProgramTableModel.columnNames[ProgramTableModel.STOP_TIME] %></div></td>
                <td width="103"><div align="center"><%= ProgramTableModel.columnNames[ProgramTableModel.CURRENT_GEAR] %></div></td>
                <td width="52"><div align="center"><%= ProgramTableModel.columnNames[ProgramTableModel.PRIORITY] %></div></td>
              </tr>
		</form>


<form name="chkForm" action="programs.jsp">
<input type="hidden" name="areaID" value="<%= lmCntrArea.getYukonID() %>" >
<%
			java.util.List progList = lmCntrArea.getLmProgramVector();
			java.util.Collections.sort( 
					progList,
					ProgramTableModel.PROGRAM_NAME_COMPARATOR );

			for( int i = 0; i < progList.size(); i++)
			{
				LMProgramBase prg = (LMProgramBase)progList.get(i);
%>
              <tr valign="top">
                <td width="137" class="TableCell">
				      <input type="checkbox" name="pid" value=<%= prg.getYukonID() %> onClick="this.form.submit()"
					     <% if( intVect.contains(prg.getYukonID().intValue()) ) { out.print(" checked"); } %> >
					  <%= LCUtils.getProgramValueAt(prg, ProgramTableModel.PROGRAM_NAME) %> 
				</td>
                
                <td width="84" class="TableCell"><font color="<%= LCUtils.getFgColor(prg) %>">
                  <div name = "prgstatus" class="lm_tip_cell" onMouseOver="itemid=<%= prg.getYukonID() %>;menuAppear(event, 'progMenu')" onMouseOut="menuDisappear(event, 'progMenu')" >
                  <%= LCUtils.getProgramValueAt(prg, ProgramTableModel.CURRENT_STATUS) %>
				  </div></font>
                </td>
                
                <td width="133" class="TableCell"><%= LCUtils.getProgramValueAt(prg, ProgramTableModel.START_TIME) %></td>
                <td width="129" class="TableCell"><%= LCUtils.getProgramValueAt(prg, ProgramTableModel.STOP_TIME) %></td>
                <td width="103" class="TableCell" align="center"><%= LCUtils.getProgramValueAt(prg, ProgramTableModel.CURRENT_GEAR) %></td>
                <td width="52" class="TableCell" align="center"><%= LCUtils.getProgramValueAt(prg, ProgramTableModel.PRIORITY) %></td>
              </tr>
<% } %>
</form>


            </table>

            <br>
            <br>

		  <table width="740" border="1" align="center" cellpadding="0" cellspacing="0">
			<tr><td><table width="736" border="0" cellspacing="0" cellpadding="0">
			  <tr><td class="HeaderCell"><span class="HeaderCell">&nbsp;&nbsp;Groups for the Selected Programs</span></td>
			  </tr>
			</table></td></tr>
		  </table>   
            <table width="740" border="1" align="center" cellpadding="2" cellspacing="0">
              <tr valign="top" class="HeaderCell"> 
                <td width="151"><div align="center"><%= GroupTableModel.columnNames[GroupTableModel.GROUP_NAME] %></div></td>
                <td width="94"><div align="center"><%= GroupTableModel.columnNames[GroupTableModel.GROUP_STATE] %></div></td>
                <td width="134"><div align="center"><%= GroupTableModel.columnNames[GroupTableModel.TIME] %></div></td>
                <td width="159"><div align="center"><%= GroupTableModel.columnNames[GroupTableModel.STATS] %></div></td>
              </tr>

<%
			for( int i = 0; i < progList.size(); i++)
			{
				LMProgramBase prg = (LMProgramBase)progList.get(i);
				
				if( intVect.contains(prg.getYukonID().intValue()) )
				{

				java.util.List grpList = prg.getLoadControlGroupVector();
				java.util.Collections.sort( grpList, GroupTableModel.GROUP_NAME_COMPARATOR );

				for( int j = 0; j < grpList.size(); j++ )
				{
					ILMGroup grp = (ILMGroup)grpList.get(j);
%>
              <tr valign="top"> 
                <td width="151" class="TableCell"> 
                  <div name = "sub" align = "left">
				  		<%= LCUtils.getGroupValueAt(grp, GroupTableModel.GROUP_NAME) %></div>
                </td>
                                
                <td width="94" class="TableCell">
                <font color="<%= LCUtils.getFgColor(grp) %>">
                <div name = "grpstatus" class="lm_tip_cell" onMouseOver="itemid=<%= grp.getYukonID() %>;menuAppear(event, 'groupMenu')" onMouseOut="menuDisappear(event, 'groupMenu')" >
                	<%= LCUtils.getGroupValueAt(grp, GroupTableModel.GROUP_STATE) %>
                </div></font></td>

                <td width="134" class="TableCell"><%= LCUtils.getGroupValueAt(grp, GroupTableModel.TIME) %></td>
                <td width="159" class="TableCell"><%= LCUtils.getGroupValueAt(grp, GroupTableModel.STATS) %></td>
              </tr>
<% } /* for j */ %>
<% } /* if contains */ %>
<% } /* for i */ %>


			 </table>
			 <br><br>			                  
				</tr>
</table>

          </td>
		  <td width="1" bgcolor="#000000"><img src="images/VerticalRule.gif" width="1"></td>
    </tr>
      </table>



<form name="doCmd" >
	<div id="areaMenu" class = "bgmenu"> 
		<div id = "StartProgsID" name = "a_start_progs" onmouseover = "changeOptionStyle(this)" class = "optmenu1"
			onclick = "showConfirmWin(this)">Start Program(s)</div>
		<div id = "StopProgsID" name = "a_stop_progs" onmouseover = "changeOptionStyle(this)" class = "optmenu1"
			onclick = "showConfirmWin(this)">Stop Program(s)</div>
		<div id = "TriggerChgID" name = "a_trigger_chg" onmouseover = "changeOptionStyle(this)" class = "optmenu1"
			onclick = "showConfirmWin(this)">Triggers Change</div>
		<div id = "DailyChgID" name = "a_daily_chg" onmouseover = "changeOptionStyle(this)" class = "optmenu1"
			onclick = "showConfirmWin(this)">Daily Time Change</div>
		<div id = "DisableProgID" name = "a_disable" onmouseover = "changeOptionStyle(this)" class = "optmenu1"
			onclick = "showConfirmWin(this)">Enable/Disable</div>
	</div>
	
	<div id="progMenu" class = "bgmenu">
		<div id = "StartProgID" name = "p_start_prog" onmouseover = "changeOptionStyle(this)" class = "optmenu1"
			onclick = "showConfirmWin(this)">Start</div>
		<div id = "StopProgID" name = "p_stop_prog" onmouseover = "changeOptionStyle(this)" class = "optmenu1"
			onclick = "showConfirmWin(this)">Stop</div>
		<div id = "EnableProgID" name = "p_disable" onmouseover = "changeOptionStyle(this)" class = "optmenu1"
			onclick = "showConfirmWin(this)">Enable/Disable</div>
	</div>
	
	<div id="groupMenu" class = "bgmenu">
		<div id = "ShedID" name = "g_shed" onmouseover = "changeOptionStyle(this)" class = "optmenu1"
			onclick = "showConfirmWin(this)">Shed</div>
		<div id = "RestoreID" name = "g_restore" onmouseover = "changeOptionStyle(this)" class = "optmenu1" 
			onclick = "showConfirmWin(this)">Restore</div>
		<div id = "TrueCycID" name = "g_truecyc" onmouseover = "changeOptionStyle(this)" class = "optmenu1"
			onclick = "showConfirmWin(this)">True Cycle</div>
		<div id = "SmartCycID" name = "g_smartcyc" onmouseover = "changeOptionStyle(this)" class = "optmenu1"
			onclick = "showConfirmWin(this)">Smart Cycle</div>
		<div id = "DisableGrpID" name = "g_disable" onmouseover = "changeOptionStyle(this)" class = "optmenu1"
			onclick = "showConfirmWin(this)">Enable/Disable</div>
		<div id = "ConfirmGrpID" name = "g_confirm" onmouseover = "changeOptionStyle(this)" class = "optmenu1"
			onclick = "showConfirmWin(this)">Confirm</div>
	</div>
</form>

</body>
</html>
