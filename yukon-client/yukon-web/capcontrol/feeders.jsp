
<%@ include file="js/cbc_funcs.js" %>
<%@ include file="cbc_header.jsp" %>

<!-- JavaScript needed for jump menu--->

<%
	Integer paoID = null;
	String strID = request.getParameter("paoID");
	SubBus subBus = null;
	
	try
	{
		paoID = new Integer(strID);
		subBus = subBusMdl.getSubBus(paoID.intValue());
		
		
		CTILogger.debug(request.getServletPath() + "	SubRowCnt = " + subBusMdl.getRowCount() + ", paoID=" + paoID );
		
		cbcSession.setLastSubID( paoID.intValue() );
		
		feederMdl.setCurrentSubBus( subBus );
		
		CTILogger.debug(request.getServletPath() + "	FdrRowCnt = " + feederMdl.getRowCount() );
		
		//set all the banks in the model to the selected SubBuses banks	
		java.util.Vector banks = new java.util.Vector(32);
		for( int i = 0; i < feederMdl.getRowCount(); i++ )
			banks.addAll( feederMdl.getRowAt(i).getCcCapBanks() );
			
		capBankMdl.setCapBankDevices( banks );
		
		CTILogger.debug(request.getServletPath() + "	CapRowCnt = " + capBankMdl.getRowCount() );			
	}
	catch( Exception e )
	{
		CTILogger.warn( 
				"This page did not successfully get the paoID needed, paoID=" + strID, e );
		
		//reset our filter to the default				
		response.sendRedirect( "subs.jsp" );
		return;
	}
%>

<html>
<head>
<title>CapControl - Feeders</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<meta http-equiv="refresh" content= <%= cbcAnnex.getRefreshRate() %> >
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<!--<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">-->
</head>

<body bgcolor="#666699" leftmargin="0" topmargin="0" text="#CCCCCC" link="#000000" vlink="#000000" alink="#000000">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="images/LoadImage.gif">&nbsp;</td>
		  <td width="1" bgcolor="#000000"><img src="images/VerticalRule.gif" width="1"></td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="images/Header.gif">&nbsp;</td>
              </tr>
              <tr bgcolor="#666699"> 
                <td width="353" height = "28" class="Header3">&nbsp;&nbsp;<font color="#99FFFF" size="2" face="Arial, Helvetica, sans-serif"><em>&nbsp;
                		Capacitor Control 
                		<% if( !cbcAnnex.isConnected() ) {%><font color="#FFFF00"> (Not connected) </font><%}%>
	            		<% if( cbcAnnex.getRefreshRate().equals(CapControlWebAnnex.REF_SECONDS_PEND) ) {%><font color="#FFFF00"> 
	            			(Auto-refresh in <%= CapControlWebAnnex.REF_SECONDS_PEND %> seconds) </font><%
	            			cbcAnnex.setRefreshRate( CapControlWebAnnex.REF_SECONDS_DEF);
	            			}%>
                		</em></font></td>
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
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="759" valign="top" bgcolor="#000000"> 
		  <td width="1" bgcolor="#000000" height="2"></td>
		</tr>
        <tr>
		  <td width="5" bgcolor="#FFFFFF" height="5"></td>
        </tr>		
        <tr> 
          <td width="759" valign="middle" bgcolor="#FFFFFF">
			<table width="740" border="0" cellspacing="0" cellpadding="0" align="center" >
              <tr>
                <td><form name="AreaForm" method="POST" action="subs.jsp">
                      
                          <div align="left"><span class="MainText">Substation Area:</span> 
                            <select name="area" onchange="this.form.submit()" >
                          <%
	                  	for( int i = 0; i < subBusMdl.getAreaNames().size(); i++ )
	                  	{
	                  		String area = subBusMdl.getAreaNames().get(i).toString();
	                  		
	                  		String s = ( area.equalsIgnoreCase(cbcSession.getLastArea()) 
	                  						? " selected" : "" ) ;
	                  		%>
                          <option value="<%= area %>" <%= s %>><%= area %></option>
                          <% } %>
                        </select>
                      </div>
                    </form>
				</td>
              </tr>
            </table>
							  
                  <table width="740" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td>
                        <table width="736" border="0" cellspacing="0" cellpadding="0">

                          <tr class="HeaderCell"> 
                              <td width="409"><span class="HeaderCell">&nbsp;&nbsp;Single Substation Bus View for the Area:</span>
                                <a href= "subs.jsp" class="Link4">
                                <span class="SchedText"> 
                                <%= cbcSession.getLastArea() %> 
                                </span>
                                </a>
                              </td>


				    		  <form name="SubBusForm" method="POST" action="feeders.jsp">
                            <td width="300" height="40"> 
                              <div align="right"><span class="HeaderCell">Other Subs in Area:</span>
                                <select name="paoID" onchange="this.form.submit()" >
                                  <%
			                  	for( int i = 0; i < subBusMdl.getRowCount(); i++ )
			                  	{
			                  		SubBus tempBus = subBusMdl.getRowAt(i);
			                  		String s = (paoID.intValue() == tempBus.getCcId().intValue() 
			                  						? " selected" : "" ) ;
			                  	%>
                                  <option value="<%= tempBus.getCcId() %>" <%= s %>> <%= tempBus.getCcName() %> 
                                  </option>
                                <% } %>
                                </select>
                                </div>
									</td>
								</form>


                        </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
				    <table width="740" border="1" align="center" cellpadding="2" cellspacing="0">
                      <tr valign="top" class="HeaderCell"> 
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
                          <div name = "sub" align = "left" cursor:default;" >
							<%= cbcAnnex.getCBCDisplay().getSubBusValueAt(subBus, SubBusTableModel.SUB_NAME_COLUMN) %> </div>
                          </td>

                        <td width="44" class="TableCell">
                        	<a href= "capcontrols.jsp?paoID=<%= paoID %>&controlType=<%= CapControlWebAnnex.CMD_SUB %>" >
                        	<font color="<%= CapControlWebAnnex.convertColor(subBusMdl.getCellColor(subBus)) %>">
                        	<%= cbcAnnex.getCBCDisplay().getSubBusValueAt(subBus, SubBusTableModel.CURRENT_STATE_COLUMN) %>
                        </font></a></td>
                        
                        <td width="44" class="TableCell"><%= cbcAnnex.getCBCDisplay().getSubBusValueAt(subBus, SubBusTableModel.TARGET_COLUMN) %></td>
                        <td width="36" class="TableCell"><%= cbcAnnex.getCBCDisplay().getSubBusValueAt(subBus, SubBusTableModel.VAR_LOAD_COLUMN) %></td>
                        <td width="45" class="TableCell"><%= cbcAnnex.getCBCDisplay().getSubBusValueAt(subBus, SubBusTableModel.TIME_STAMP_COLUMN) %></td>
                        <td width="45" class="TableCell"><%= cbcAnnex.getCBCDisplay().getSubBusValueAt(subBus, SubBusTableModel.POWER_FACTOR_COLUMN) %></td>
                        <td width="33" class="TableCell"><%= cbcAnnex.getCBCDisplay().getSubBusValueAt(subBus, SubBusTableModel.WATTS_COLUMN) %></td>
                        <td width="36" class="TableCell"><%= cbcAnnex.getCBCDisplay().getSubBusValueAt(subBus, SubBusTableModel.DAILY_OPERATIONS_COLUMN) %></td>
                        <td width="71" class="TableCell"> 
							<select name="selectGraph" onchange="showGraphWin(this.options[this.selectedIndex].value);">
							  <option value="subs.jsp">Feeder kVar</option>
							  <option value="<%=request.getContextPath()%>/servlet/GraphGenerator?action=EncodeGraph&pointid=<%=subBus.getCurrentVarLoadPointID().intValue()%>&period=<%=ServletUtil.PREVTHIRTYDAYS%>">Sub kVAR</option>
							  <option value="oneline/<%= cbcAnnex.getCBCDisplay().getSubBusValueAt(subBus, SubBusTableModel.SUB_NAME_COLUMN) %>.html">One Line</option>
							</select>
                        </td>
                        <td width="80" class="TableCell"> 
                          <select name="select3">
                            <option>Peaks</option>
                            <option>Ops.</option>
                          </select>
                        </td>
                      </tr>
                    </table>
                  <br>
                  <table width="740" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr>
                      <td>
                        <table width="736" border="0" cellspacing="0" cellpadding="0">
                          <tr> 
                              
                      <td class="HeaderCell">&nbsp;&nbsp;Feeders for Substation:
					  <span class="SchedText"> <%= subBus.getCcName() %> 
                        </span> </td>
                            </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                    <table width="740" border="1" align="center" cellpadding="2" cellspacing="0">
                      <tr valign="top" class="HeaderCell"> 
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
                        <td width="100" class="TableCell"><a href= "singlefeeder.jsp?paoID=<%= feederMdl.getRowAt(i).getCcId() %>" class="Link1">
                          <div name = "sub" align = "left" cursor:default;" >
							<%= feederMdl.getValueAt(i, FeederTableModel.NAME_COLUMN) %> </div>
                          </a></td>
                        <td width="44" class="TableCell">
                        	<a href= "capcontrols.jsp?paoID=<%= feederMdl.getRowAt(i).getCcId().intValue() %>&controlType=<%= CapControlWebAnnex.CMD_FEEDER %>" >
                        	<font color="<%= CapControlWebAnnex.convertColor(feederMdl.getCellForegroundColor( i, FeederTableModel.CURRENT_STATE_COLUMN ) ) %>">
                        	<%= feederMdl.getValueAt(i, FeederTableModel.CURRENT_STATE_COLUMN) %>
                        </font></a></td>
                        
                        
                        <td width="44" class="TableCell"><%= feederMdl.getValueAt(i, FeederTableModel.TARGET_COLUMN) %></td>
                        <td width="36" class="TableCell"><%= feederMdl.getValueAt(i, FeederTableModel.VAR_LOAD_COLUMN) %></td>
						<td width="45" class="TableCell"><%= feederMdl.getValueAt(i, FeederTableModel.TIME_STAMP_COLUMN) %></td>                        
                        <td width="45" class="TableCell"><%= feederMdl.getValueAt(i, FeederTableModel.POWER_FACTOR_COLUMN) %></td>
                        <td width="45" class="TableCell"><%= feederMdl.getValueAt(i, FeederTableModel.WATTS_COLUMN) %></td>
                        <td width="36" class="TableCell"><%= feederMdl.getValueAt(i, FeederTableModel.DAILY_OPERATIONS_COLUMN) %></td>
                        <td width="71" class="TableCell"> 
						  <select name="selectGraph" onchange ="showGraphWin(this.options[this.selectedIndex].value);">
                            <option>Graph B</option>
							<option value="<%=request.getContextPath()%>/servlet/GraphGenerator?action=EncodeGraph&pointid=<%=feederMdl.getRowAt(i).getCurrentVarLoadPointID().intValue()%>&period=<%=ServletUtil.PREVTHIRTYDAYS%>">Feeder kVAR</option>
                          </select>
                        </td>
                        <td width="80" class="TableCell"> 
                          <select name="select5">
                            <option>Peaks</option>
                            <option>Ops.</option>
                          </select>
                        </td>
                      </tr>
						<%
						}
						%>                      
                    </table>
                    <br>
                  <table width="740" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td>
                        <table width="736" border="0" cellspacing="0" cellpadding="0">
                          <tr> 
                              <td class="HeaderCell"><span class="HeaderCell">&nbsp;&nbsp;Capacitor Banks for Substation:</span>
                                <span class="SchedText"> <%= subBus.getCcName() %> </span>
                              </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                    <table width="740" border="1" align="center" cellpadding="2" cellspacing="0">
                      <tr valign="top" class="HeaderCell"> 
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
                          <div name = "sub" align = "left" cursor:default;">
	                       		<% if( capBankMdl.isRowAlarmed(i) ) { %> <img src="images/AlarmFlag.gif" width="10"> <% } %>
                          		<%= capBankMdl.getValueAt(i, CapBankTableModel.CB_NAME_COLUMN) %>
                          </div>
                        </td>
                        <td width="228" class="TableCell"><%= capBankMdl.getValueAt(i, CapBankTableModel.BANK_ADDRESS_COLUMN) %></td>
                        
                        <td width="43" class="TableCell">
                        	<a href= "capcontrols.jsp?paoID=<%= capBankMdl.getRowAt(i).getCcId().intValue() %>&controlType=<%= CapControlWebAnnex.CMD_CAPBANK %>" >
                        	<font color="<%= CapControlWebAnnex.convertColor(capBankMdl.getCellForegroundColor( i, CapBankTableModel.STATUS_COLUMN ) ) %>">
                        	<%= capBankMdl.getValueAt(i, CapBankTableModel.STATUS_COLUMN) %>
                        </font></a></td>
                        
                        <td width="98" class="TableCell"> <%= capBankMdl.getValueAt(i, CapBankTableModel.TIME_STAMP_COLUMN) %></td>
                        <td width="33" class="TableCell"> <%= capBankMdl.getValueAt(i, CapBankTableModel.BANK_SIZE_COLUMN) %></td>
                        <td width="34" class="TableCell"> <%= capBankMdl.getValueAt(i, CapBankTableModel.OP_COUNT_COLUMN) %></td>
                      </tr>
						<%
						}
						%>                      

                    </table>
                  <br>
				</tr>
</table>

          </td>
		  <td width="1" bgcolor="#000000"><img src="images/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
</body>
</html>
