<%@ include file="lm_header.jsp" %>

<%
	if( lmSession.getResponseProgs() != null )
	{
		CTILogger.warn( 
			"Found some program violations, " +
			"showing program violation page" );
%>
		<%@ include file="include/progviolation.jspf" %>
<%
		return;
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<link rel="stylesheet" href="../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="css/lm.css" type="text/css">
</head>


<body class="Background" leftmargin="0" topmargin="0" onload="reload();">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="images/LoadImage.jpg">&nbsp;</td>
		  <td width="1" bgcolor="#000000"><img src="images/VerticalRule.jpg" width="1"></td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr>
                <td id="header" colspan="4" height="74" background="../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="images/Header.gif"/>">&nbsp;</td>
              </tr>
              <tr bgcolor="#0066CC"> 
                
                <td width="353" height = "28" class="PageHeader">&nbsp;&nbsp;&nbsp; 
                  Load Management
                </td>
                                
                <td width="235" valign="middle">&nbsp;</td>
                <td width="58" valign="middle" class="MainText"> 
                  <div align="center"><span><a href="../operator/Operations.jsp" class="Link3">Home</a></span></div>
                </td>
                <td width="57" valign="middle" class="MainText"> 
                  <div align="left"><span ><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
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
          <td width="759" bgcolor="#000000" valign="top"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr>	
          <td width="759" valign="top" bgcolor="#FFFFFF" height="261"> 
            <table width="740" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr>


				  <td>
					<div align="left" class="NavText"><br>
					  <a href="controlareas.jsp">Control Areas</a>
					</div>
				  </td>

                <td valign="bottom">
 
                  <form name="areaSelect" action="controlareas.jsp" >
                    <div align="right">					
					<span class="MainText"><br>
                      Control Area State:</span> 
                      <select name="area_state" onchange="this.form.submit()" >

<%
if( DaoFactory.getAuthDao().hasPAOAccess((LiteYukonUser) session.getAttribute(ServletUtil.ATT_YUKON_USER)) ) {
%>
					  <option value="<%= ControlAreaActionListener.SEL_ALL_CONTROL_AREAS %>" 
					  <%= (lmSession.getAreaView().equalsIgnoreCase(ControlAreaActionListener.SEL_ALL_CONTROL_AREAS) ? " selected" : "" ) %>>
					  <%= ControlAreaActionListener.SEL_ALL_CONTROL_AREAS %></option>
<% } %>

					  <option value="<%= ControlAreaActionListener.SEL_ACTIVE_AREAS %>" 
					  <%= (lmSession.getAreaView().equalsIgnoreCase(ControlAreaActionListener.SEL_ACTIVE_AREAS) ? " selected" : "" ) %>>
					  <%= ControlAreaActionListener.SEL_ACTIVE_AREAS %></option>

					  <option value="<%= ControlAreaActionListener.SEL_INACTIVE_AREAS %>" 
					  <%= (lmSession.getAreaView().equalsIgnoreCase(ControlAreaActionListener.SEL_INACTIVE_AREAS) ? " selected" : "" ) %>>
					  <%= ControlAreaActionListener.SEL_INACTIVE_AREAS %></option>

						  
                      </select>
					  
                    </div>
                  </form>

				</td>
              </tr>
            </table>
            
            <table width="740" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr>
                <td width="740" valign="top" class="MainText" height="187"> 
                  <table width="740" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td>
                        <table width="740" border="0" cellspacing="0" cellpadding="0">
                          <tr class="HeaderCell"> 
                              
                            <td width="409"><span class="HeaderCell">&nbsp;&nbsp; 
                              Control Areas : <font color="##0066CC"><%= lmSession.getAreaView() %> </font> </span></td>

                              <td width="191"> 
                                <div align="right"> </div>
                              </td>
                            </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                  <table width="744" border="1" align="center" cellpadding="2" cellspacing="0">
                    <tr valign="top" class="HeaderCell"> 
                      <td width="111"><div align="center"><%= ControlAreaTableModel.COLUMN_NAMES[ControlAreaTableModel.AREA_NAME]%></div></td>
                      <td width="47"><div align="center"><%= ControlAreaTableModel.COLUMN_NAMES[ControlAreaTableModel.CURRENT_STATE]%></div></td>
                      <td width="67"><div align="center"><%= ControlAreaTableModel.COLUMN_NAMES[ControlAreaTableModel.VALUE_THRESHOLD]%></div></td>
                      <td width="94"><div align="center"><%= ControlAreaTableModel.COLUMN_NAMES[ControlAreaTableModel.PEAK_PROJECTION]%></div></td>
                      <td width="50"><div align="center"><%= ControlAreaTableModel.COLUMN_NAMES[ControlAreaTableModel.ATKU]%></div></td>
                      <td width="40"><div align="center"><%= ControlAreaTableModel.COLUMN_NAMES[ControlAreaTableModel.PRIORITY]%></div></td>
                      <td width="75"><div align="center"><%= ControlAreaTableModel.COLUMN_NAMES[ControlAreaTableModel.TIME_WINDOW]%></div></td>

<!--
                      <td width="97"><div align="center">Graphs</div></td>
                      <td width="83"><div align="center">Reports</div></td>
-->
                    </tr>
					
					
					
				  <%
					Iterator iterAreas = lcCache.getAllControlAreas(
								(LiteYukonUser)session.getAttribute(ServletUtil.ATT_YUKON_USER) );

					while( iterAreas.hasNext() )
					{
  					  LMControlArea lmCntrArea = (LMControlArea)iterAreas.next();
					  
					  if( !LCUtils.isAreaDisplayed(lmSession.getAreaView(), lmCntrArea.getControlAreaState().intValue()) )
					  	continue;
				  %>         

                    <tr valign="top">
                      <td width="111" class="TableCell"><a href="programs.jsp?areaID=<%= lmCntrArea.getYukonID() %>" class="Link1"> 
                        <div name = "subPopup" align = "left" cursor:default;" > 
                          <%= LCUtils.getControlAreaValueAt(lmCntrArea, ControlAreaTableModel.AREA_NAME) %></div></a>
                      </td>
                      <td width="47" class="TableCell">
                      	<div name = "areastatus" class="lm_tip_cell" onMouseOver="itemid=<%= lmCntrArea.getYukonID() %>;menuAppear(event, 'areaMenu')" onMouseOut="menuDisappear(event, 'areaMenu')">
                      	<font color="<%= LCUtils.getFgColor(lmCntrArea) %>"> 
                        <%= LCUtils.getControlAreaValueAt(lmCntrArea, ControlAreaTableModel.CURRENT_STATE) %> 
                        </font></div>
                      </td>
					  
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
					  
					  
<!--
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
-->
                      
                    </tr>
					
					<%
					}
					%>                      
					
                  </table>
                  <br>
              </tr>
</table>

          </td>
          <td width="1" bgcolor="#000000" height="261"><img src="images/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
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
		<div id = "ResetPkVal" name = "a_reset_peak" onmouseover = "changeOptionStyle(this)" class = "optmenu1"
			onclick = "showConfirmWin(this)">Reset Peak</div>
	</div>
</form>


</body>
</html>
