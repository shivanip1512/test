
<%@ include file="Functions.jsp" %>
<%@ include file="cbc_header.jsp" %>

<!-- JavaScript needed for jump menu--->

<%
	
//request.getHeader("referer");  //null if not found
	
	Integer subRowID = null;
	String strID = request.getParameter("subRowID");
	try
	{
		subRowID = new Integer(strID);

CTILogger.debug("  SubRowCnt=" + subBusMdl.getRowCount() );
		
		if( subRowID.intValue() >= 0 && subRowID.intValue() < subBusMdl.getRowCount() )
		{
			feederMdl.setCurrentSubBus( subBusMdl.getRowAt(subRowID.intValue()) );
			
CTILogger.debug("  FdrRowCnt=" + feederMdl.getRowCount() );
			
			//set all the banks in the model to the selected SubBuses banks	
			java.util.Vector banks = new java.util.Vector(32);
			for( int i = 0; i < feederMdl.getRowCount(); i++ )
				banks.addAll( feederMdl.getRowAt(i).getCcCapBanks() );
				
			capBankMdl.setCapBankDevices( banks );
			
CTILogger.debug("  CBRowCnt=" + capBankMdl.getRowCount() );
		
		}
		else  //just force ourself to catch a newly created exception
			throw new ArrayIndexOutOfBoundsException(
					"Sub Row id value out of range, subRowCount = " + subBusMdl.getRowCount() );
	}
	catch( Exception e )
	{
		CTILogger.warn( 
				"This page did not successfully get the subRowID needed, subRowID=" + strID, e ); 
		
		//reset our filter to the default				
		cbcSession.setLastArea( SubBusTableModel.ALL_FILTER );
		
		response.sendRedirect( "AllSubs.jsp" );
		return;
	}
%>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<meta http-equiv="refresh" content= <%= CapControlWebAnnex.REFRESH_SECONDS %> >
<link id="StyleSheet" rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link id="StyleSheet" rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
</head>

<body bgcolor="#666699" leftmargin="0" topmargin="0" text="#CCCCCC" link="#000000" vlink="#000000" alink="#000000">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="LoadImage.gif">&nbsp;</td>
		  <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="Header.gif">&nbsp;</td>
              </tr>
              <tr bgcolor="#666699"> 
                <td width="253" height = "28" class="Header3">&nbsp;&nbsp;<font color="#99FFFF" size="2" face="Arial, Helvetica, sans-serif"><em>&nbsp;
                		Capacitor Control 
                		<% if( !cbcServlet.isConnected() ) {%><font color="#FFFF00"> (Not connected) </font><%}%>
                		</em></font></td>
                <td width="235" valign="middle">&nbsp;</td>
                
                  
                <td width="58" valign="middle"> 
                  <div align="center"><span><a href="../Operations.jsp" class="Link3"><font color="99FFFF" size="2" face="Arial, Helvetica, sans-serif">Home</font></a></span></div>
                  </td>
                  
                <td width="57" valign="middle"> 
                  <div align="left"><span ><a href="../../login.jsp" class="Link3"><font color="99FFFF" size="2" face="Arial, Helvetica, sans-serif">Log 
                    Off</font></a><font color="99FFFF" size="2" face="Arial, Helvetica, sans-serif">&nbsp;</font></span></div>
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
          <td width="101"  valign="top" bgcolor="#666699">
		    <table width="100" border="0" cellpadding="2" cellspacing="0" height="150">
              <tr> 
                <td width="378"> 
                  <div align="center"><span class="Header">SUB AREA</span>
						<form name="AreaForm" action='AllSubs.jsp' method="POST" >
                    <select name="area" onchange="this.form.submit();" >
	                  <%
	                  	for( int i = 0; i < cbcServlet.getAreaNames().size(); i++ )
	                  	{
	                  		String area = cbcServlet.getAreaNames().get(i).toString();
	                  		
	                  		String s = ( area.equalsIgnoreCase(cbcSession.getLastArea()) 
	                  						? " selected" : "" ) ;
	                  		%>
	                  		
									<option value="<%= area %>" <%= s %>><%= area %></option>
	                  <% } %>
                    </select>
			         </form>

                  </div></td>
              </tr>
              <tr> 
                <td><div align="center"> 
                    <input name="Confirm3" type="submit" id="Confirm32" value="Confirm">
                  </div></td>
              </tr>
              <tr> 
                <td><div align="center"> 
                    <input name="Enable4" type="submit" id="Enable42" value="Enable">
                  </div></td>
              </tr>
              <tr> 
                </tr>
            </table> 
            
            
          </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
			<table width="657" border="0" cellspacing="0" cellpadding="0">
			  <tr> 
                <td width="650" valign="top" class="Main">                   
                  <p>&nbsp;</p>
                  <form name="MForm"> 

                  <table width="600" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td>
                        <table width="600" border="0" cellspacing="0" cellpadding="0">
                          <tr bgcolor="#CCCCCC"> 
                              <td width="409"><span class="TableCell"></span><span class="HeaderCell">&nbsp;&nbsp;
                              	Single Substation Bus View for the Area : 
                                <font color="##666699"> <%= cbcSession.getLastArea() %> </font></span></td>
                              <td width="191"> 
                                <div align="right">

								<form name="SubBusForm" method="POST" >
		                    <select name="subRowID" onchange="this.form.submit()" >
			                  <%
			                  	for( int i = 0; i < subBusMdl.getRowCount(); i++ )
			                  	{
			                  		//String busName = cbcServlet.getAreaNames().get(i).toString();
			                  		
			                  		String s = ( subRowID.intValue() == i 
			                  						? " selected" : "" ) ;
			                  		%>
											<option value="<%= i %>" <%= s %>> 
												<%= subBusMdl.getValueAt(i, SubBusTableModel.SUB_NAME_COLUMN) %> 
											</option>
			                  <% } %>
		
		                    </select>
					         </form>
                                    
                                </div>
                              </td>
                            </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
				    <table width="604" border="1" align="center" cellpadding="2" cellspacing="0">
                      <tr bgcolor="#CCCCCC" class="HeaderCell1"> 
                        <td width="100"><%= subBusMdl.getColumnName(SubBusTableModel.SUB_NAME_COLUMN) %></td>
                        <td width="44"> <%= subBusMdl.getColumnName(SubBusTableModel.CURRENT_STATE_COLUMN) %></td>
                        <td width="44"> <%= subBusMdl.getColumnName(SubBusTableModel.TARGET_COLUMN) %></td>
                        <td width="36"> <%= subBusMdl.getColumnName(SubBusTableModel.VAR_LOAD_COLUMN) %></td>
                        <td width="45"> <%= subBusMdl.getColumnName(SubBusTableModel.TIME_STAMP_COLUMN) %></td>
                        <td width="45"> <%= subBusMdl.getColumnName(SubBusTableModel.POWER_FACTOR_COLUMN) %></td>
                        <td width="33"> <%= subBusMdl.getColumnName(SubBusTableModel.WATTS_COLUMN) %></td>
                        <td width="36"> <%= subBusMdl.getColumnName(SubBusTableModel.DAILY_OPERATIONS_COLUMN) %></td>
                        <td width="71">Graphs</td>
                        <td width="80">Reports</td>
                      </tr>
                      <tr valign="top"> 
                        <td width="100" class="TableCell">
                          <div name = "sub" align = "left" cursor:default;" onMouseOver = "menuAppear(event, 'subMenu')" >
									<%= subBusMdl.getValueAt(subRowID.intValue(), SubBusTableModel.SUB_NAME_COLUMN) %> </div>
                          </a></td>

                        <td width="44" class="TableCell">
                        	<font color="<%= cbcServlet.convertColor(subBusMdl.getCellForegroundColor( subRowID.intValue(), SubBusTableModel.CURRENT_STATE_COLUMN ) ) %>">
                        	<%= subBusMdl.getValueAt(subRowID.intValue(), SubBusTableModel.CURRENT_STATE_COLUMN) %>
                        </font></td>
                        
                        <td width="44" class="TableCell"><%= subBusMdl.getValueAt(subRowID.intValue(), SubBusTableModel.TARGET_COLUMN) %></td>
                        <td width="36" class="TableCell"><%= subBusMdl.getValueAt(subRowID.intValue(), SubBusTableModel.VAR_LOAD_COLUMN) %></td>
                        <td width="45" class="TableCell"><%= subBusMdl.getValueAt(subRowID.intValue(), SubBusTableModel.TIME_STAMP_COLUMN) %></td>
                        <td width="45" class="TableCell"><%= subBusMdl.getValueAt(subRowID.intValue(), SubBusTableModel.POWER_FACTOR_COLUMN) %></td>
                        <td width="33" class="TableCell"><%= subBusMdl.getValueAt(subRowID.intValue(), SubBusTableModel.WATTS_COLUMN) %></td>
                        <td width="36" class="TableCell"><%= subBusMdl.getValueAt(subRowID.intValue(), SubBusTableModel.DAILY_OPERATIONS_COLUMN) %></td>
                        <td width="71" class="TableCell"> 
                          <select name="select2">
                            <option>Graph A</option>
                            <option>Graph B</option>
                          </select>
                        </td>
                        <td width="80" class="TableCell"> 
                          <select name="select3">
                            <option>Report A</option>
                            <option>Report B</option>
                          </select>
                        </td>
                      </tr>
                    </table>
                  <br>
                  <table width="600" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr>
                      <td>
                        <table width="600" border="0" cellspacing="0" cellpadding="0">
                          <tr bgcolor="#CCCCCC"> 
                              <td class="HeaderCell">&nbsp;&nbsp;
                              Feeders for Substation : 
                              <font color="##666699"> <%= subBusMdl.getValueAt(subRowID.intValue(), SubBusTableModel.SUB_NAME_COLUMN) %> </font>
                              </td>
                            </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                    <table width="604" border="1" align="center" cellpadding="2" cellspacing="0">
                      <tr bgcolor="#CCCCCC" class="HeaderCell1"> 

                        <td width="100"><%= feederMdl.getColumnName(FeederTableModel.NAME_COLUMN) %></td>
                        <td width="44"> <%= feederMdl.getColumnName(FeederTableModel.CURRENT_STATE_COLUMN) %></td>
                        <td width="44"> <%= feederMdl.getColumnName(FeederTableModel.TARGET_COLUMN) %></td>
                        <td width="36"> <%= feederMdl.getColumnName(FeederTableModel.VAR_LOAD_COLUMN) %></td>
                        <td width="45"> <%= feederMdl.getColumnName(FeederTableModel.TIME_STAMP_COLUMN) %></td>                        
                        <td width="45"> <%= feederMdl.getColumnName(FeederTableModel.POWER_FACTOR_COLUMN) %></td>
                        <td width="45"> <%= feederMdl.getColumnName(FeederTableModel.WATTS_COLUMN) %></td>
                        <td width="36"> <%= feederMdl.getColumnName(FeederTableModel.DAILY_OPERATIONS_COLUMN) %></td>
                        <td width="71">Graphs</td>
                        <td width="80">Reports</td>
                      </tr>
                      
	                  <% 
                  	for( int i = 0; i < feederMdl.getRowCount(); i++ )
                  	{	     
	                  %>         
                      <tr valign="top"> 
                        <td width="100" class="TableCell"><a href= "SingleFeeder.jsp?feederRowID=<%= i %>" >
                          <div name = "sub" align = "left" cursor:default;" onMouseOver = "menuAppear(event, 'FeederMenu')" >
									<%= feederMdl.getValueAt(i, FeederTableModel.NAME_COLUMN) %> </div>
                          </a></td>
                        <td width="44" class="TableCell">
                        	<font color="<%= cbcServlet.convertColor(feederMdl.getCellForegroundColor( i, FeederTableModel.CURRENT_STATE_COLUMN ) ) %>">
                        	<%= feederMdl.getValueAt(i, FeederTableModel.CURRENT_STATE_COLUMN) %>
                        </font></td>
                        
                        
                        <td width="44" class="TableCell"><%= feederMdl.getValueAt(i, FeederTableModel.TARGET_COLUMN) %></td>
                        <td width="36" class="TableCell"><%= feederMdl.getValueAt(i, FeederTableModel.VAR_LOAD_COLUMN) %></td>
								<td width="45" class="TableCell"><%= feederMdl.getValueAt(i, FeederTableModel.TIME_STAMP_COLUMN) %></td>                        
                        <td width="45" class="TableCell"><%= feederMdl.getValueAt(i, FeederTableModel.POWER_FACTOR_COLUMN) %></td>
                        <td width="45" class="TableCell"><%= feederMdl.getValueAt(i, FeederTableModel.WATTS_COLUMN) %></td>
                        <td width="36" class="TableCell"><%= feederMdl.getValueAt(i, FeederTableModel.DAILY_OPERATIONS_COLUMN) %></td>
                        <td width="71" class="TableCell"> 
                          <select name="select5">
                            <option>Graph A</option>
                            <option>Graph B</option>
                          </select>
                        </td>
                        <td width="80" class="TableCell"> 
                          <select name="select5">
                            <option>Report A</option>
                            <option>Report B</option>
                          </select>
                        </td>
                      </tr>
						<%
						}
						%>                      
                    </table>
                    <br>
                  <table width="600" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td>
                        <table width="600" border="0" cellspacing="0" cellpadding="0">
                          <tr bgcolor="#CCCCCC"> 
                              <td class="HeaderCell">&nbsp;&nbsp;Capacitor Banks for Substation :
                                <font color="##666699"> <%= subBusMdl.getValueAt(subRowID.intValue(), SubBusTableModel.SUB_NAME_COLUMN) %> </font>
                              </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                    <table width="604" border="1" align="center" cellpadding="2" cellspacing="0">
                      <tr bgcolor="#CCCCCC" class="HeaderCell1"> 
                        <td width="130"><%=capBankMdl.getColumnName(CapBankTableModel.CB_NAME_COLUMN) %></td>
                        <td width="228"><%=capBankMdl.getColumnName(CapBankTableModel.BANK_ADDRESS_COLUMN) %></td>
                        <td width="43"> <%=capBankMdl.getColumnName(CapBankTableModel.STATUS_COLUMN) %></td>
                        <td width="98"> <%=capBankMdl.getColumnName(CapBankTableModel.TIME_STAMP_COLUMN) %></td>
                        <td width="33"> <%=capBankMdl.getColumnName(CapBankTableModel.BANK_SIZE_COLUMN) %></td>
                        <td width="34"> <%=capBankMdl.getColumnName(CapBankTableModel.OP_COUNT_COLUMN) %></td>
                      </tr>
                      
	                  <% 
                  	for( int i = 0; i < capBankMdl.getRowCount(); i++ )
                  	{	                  	
	                  %>         
                      <tr valign="top"> 
                        <td width="130" class="TableCell">
                          <div name = "sub" align = "left" cursor:default;" onMouseOver = "menuAppear(event, 'CapMenu')" > 
                          		<%= capBankMdl.getValueAt(i, CapBankTableModel.CB_NAME_COLUMN) %>
                          </div>
                        </td>
                        <td width="228" class="TableCell"><%= capBankMdl.getValueAt(i, CapBankTableModel.BANK_ADDRESS_COLUMN) %></td>
                        
                        <td width="43" class="TableCell">
                        	<font color="<%= cbcServlet.convertColor(capBankMdl.getCellForegroundColor( i, CapBankTableModel.STATUS_COLUMN ) ) %>">
                        	<%= capBankMdl.getValueAt(i, CapBankTableModel.STATUS_COLUMN) %>
                        </font></td>
                        
                        <td width="98" class="TableCell"> <%= capBankMdl.getValueAt(i, CapBankTableModel.TIME_STAMP_COLUMN) %></td>
                        <td width="33" class="TableCell"> <%= capBankMdl.getValueAt(i, CapBankTableModel.BANK_SIZE_COLUMN) %></td>
                        <td width="34" class="TableCell"> <%= capBankMdl.getValueAt(i, CapBankTableModel.OP_COUNT_COLUMN) %></td>
                      </tr>
						<%
						}
						%>                      

                    </table>
				  </form>
                  <br>
                  <div id="subMenu" class = "bgmenu" style = "width:75px; left: 214px; top: 216px"> 
                    <div id = "Enable1ID" name = "enable"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "???">&nbsp;&nbsp;&nbsp;Enable</div>
					<div id = "Disable1ID" name = "disable" style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;Disable</div>
				<div id = "Confirm1ID" name = "confirm" style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;Confirm</div>
				</div>
				<div id="FeederMenu" class = "bgmenu" style = "width:75px; left: 214px; top: 216px"> 
                    <div id = "Enable2ID" name = "enable"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "???">&nbsp;&nbsp;&nbsp;Enable</div>
					<div id = "Disable2ID" name = "disable" style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;Disable</div>
				</div>
				<div id="CapMenu" class = "bgmenu" style = "width:75px; left: 214px; top: 216px"> 
                    <div id = "AckID" name = "acknowledge"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "???">&nbsp;&nbsp;&nbsp;Ack Alarm</div>
					<div id = "ClearID" name = "clear" style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;Clear Alarm</div>
					<div id = "ManualID" name = "manual" style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;Manual</div>
					<div id = "Enable3ID" name = "enable" style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;Enable</div>
					<div id = "Disable3ID" name = "disable" style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;Disable</div>
					<div id = "Confirm2ID" name = "confirm" style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;Confirm</div>
					<div id = "OpenID" name = "open" style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;Open</div>
					<div id = "CloseID" name = "close" style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "????">&nbsp;&nbsp;&nbsp;Close</div>
				</div>
				</tr>
</table>

          </td>
        <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
</body>
</html>
