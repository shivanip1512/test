
<%@ include file="js/cbc_funcs.js" %>
<%@ include file="cbc_header.jsp" %>

<%
	//-------- PARAMS ----------- 
	// rowID: The row ID of the selected item in the table
	// controlType: The type of control we are to do (SUB_CNTRL, FEEDER_CNTRL, CAPBANK_CNTRL) 
		
	//get the page we came from, we will return here (null if not found)
	String refererURL = request.getHeader("referer");
	String label = new String();
	Integer hiddenPAOid = null;
	
	Integer rowID = null;
	String strID = request.getParameter("rowID");
	String controlType = request.getParameter("controlType");
	int enableID = -1, disableID = -1;
	
	
	try
	{
		rowID = new Integer(strID);
		
		//no matter what type of control, the subbus model must have 1 or more buses
		if( subBusMdl.getRowCount() <= 0 )
			throw new IllegalArgumentException("Unable to continue execution since no Sub buses were found");
		
		if( refererURL == null )
			throw new IllegalArgumentException("The referer page attribute should not be (null)");
		
		if( controlType == null )
			throw new IllegalArgumentException("The controlType attribute should not be (null)");
		
		enableID = (controlType.equals(CapControlWebAnnex.CMD_SUB) ? CBCCommand.ENABLE_SUBBUS
						: (controlType.equals(CapControlWebAnnex.CMD_FEEDER) ? CBCCommand.ENABLE_FEEDER
						: CBCCommand.ENABLE_CAPBANK ));
		
		disableID = (controlType.equals(CapControlWebAnnex.CMD_SUB) ? CBCCommand.DISABLE_SUBBUS
						: (controlType.equals(CapControlWebAnnex.CMD_FEEDER) ? CBCCommand.DISABLE_FEEDER
						: CBCCommand.DISABLE_CAPBANK ));
	}
	catch( Exception e )
	{
		CTILogger.warn( 
				"This page did not successfully get the rowID, the referer URL, or the controlType rowID =" + strID, e ); 
		
		if( refererURL != null )
			response.sendRedirect( refererURL );
		else
		{
			//reset our filter to the default
			cbcSession.setLastArea( SubBusTableModel.ALL_FILTER );
			response.sendRedirect( "subs.jsp" );
		}
		
		return;
	}
%>


<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="StyleSheet" rel="stylesheet" href="../WebConfig/yukon/CannonStyle.css" type="text/css">
<!--<link id="StyleSheet" rel="stylesheet" href="../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">-->
</head>

<body class="Background" leftmargin="0" topmargin="0">
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
                <td width="353" height = "28" class="PageHeader">&nbsp;&nbsp;&nbsp;Capacitor Control 
                		<% if( !cbcAnnex.isConnected() ) {%><font color="#FFFF00"> (Not connected) </font><%}%>
                		<% if( cbcAnnex.getRefreshRate().equals(CapControlWebAnnex.REF_SECONDS_PEND) ) {%><font color="#FFFF00"> 
                			(Auto-refresh in <%= CapControlWebAnnex.REF_SECONDS_PEND %>seconds) </font><%
                			cbcAnnex.setRefreshRate( CapControlWebAnnex.REF_SECONDS_DEF);
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
			<table width="740" border="0" cellspacing="0" cellpadding="0" align="center">
			  <tr> 
                <td width="739" valign="top" class="MainText" align="center">
                  <% if( CapControlWebAnnex.CMD_SUB.equals(controlType) )
			{
						label = subBusMdl.getRowAt(rowID.intValue()).getCcName();
						hiddenPAOid = subBusMdl.getRowAt(rowID.intValue()).getCcId();
%>
                  <table width="740" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td>
                        <table width="736" border="0" cellspacing="0" cellpadding="0">						
                          <tr class="HeaderCell"> 
                              <td width="409">&nbsp;&nbsp;Substation Bus Data</td>
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
                        <td width="100" class="TableCell"><a href= "feeders.jsp?subRowID=<%= rowID.intValue() %>" class="Link1">
                          <div name = "subPopup" align = "left" cursor:default;" >
                             <%= subBusMdl.getValueAt(rowID.intValue(), SubBusTableModel.SUB_NAME_COLUMN) %> 
                          </div></a>
                        </td>
                          

                        <td width="44" class="TableCell">
                        	<font color="<%= CapControlWebAnnex.convertColor(subBusMdl.getCellForegroundColor( rowID.intValue(), SubBusTableModel.CURRENT_STATE_COLUMN ) ) %>">
                        	<%= subBusMdl.getValueAt(rowID.intValue(), SubBusTableModel.CURRENT_STATE_COLUMN) %>
                        </font></td>
                        
                        <td width="44" class="TableCell"><%= subBusMdl.getValueAt(rowID.intValue(), SubBusTableModel.TARGET_COLUMN) %></td>
                        <td width="36" class="TableCell"><%= subBusMdl.getValueAt(rowID.intValue(), SubBusTableModel.VAR_LOAD_COLUMN) %></td>
                        <td width="45" class="TableCell"><%= subBusMdl.getValueAt(rowID.intValue(), SubBusTableModel.TIME_STAMP_COLUMN) %></td>
                        <td width="45" class="TableCell"><%= subBusMdl.getValueAt(rowID.intValue(), SubBusTableModel.POWER_FACTOR_COLUMN) %></td>
                        <td width="33" class="TableCell"><%= subBusMdl.getValueAt(rowID.intValue(), SubBusTableModel.WATTS_COLUMN) %></td>
                        <td width="36" class="TableCell"><%= subBusMdl.getValueAt(rowID.intValue(), SubBusTableModel.DAILY_OPERATIONS_COLUMN) %></td>
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
		<% }
			else if( CapControlWebAnnex.CMD_FEEDER.equals(controlType) )
			{
						label = feederMdl.getRowAt(rowID.intValue()).getCcName();
						hiddenPAOid = feederMdl.getRowAt(rowID.intValue()).getCcId();
%>                    
                  <table width="740" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr>
                      <td>
                        <table width="736" border="0" cellspacing="0" cellpadding="0">
                          <tr > 
                              <td class="HeaderCell">&nbsp;&nbsp; Feeder Data 
                              </td>
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
                      
                      <tr valign="top"> 
                        <td width="100" class="TableCell">
                          <div name = "sub" align = "left" cursor:default;">
									<%= feederMdl.getValueAt(rowID.intValue(), FeederTableModel.NAME_COLUMN) %> </div>
                          </td>
                        <td width="44" class="TableCell">
                        	<font color="<%= CapControlWebAnnex.convertColor(feederMdl.getCellForegroundColor( rowID.intValue(), FeederTableModel.CURRENT_STATE_COLUMN ) ) %>">
                        	<%= feederMdl.getValueAt(rowID.intValue(), FeederTableModel.CURRENT_STATE_COLUMN) %>
                        </font></td>
                        
                        
                        <td width="44" class="TableCell"><%= feederMdl.getValueAt(rowID.intValue(), FeederTableModel.TARGET_COLUMN) %></td>
                        <td width="36" class="TableCell"><%= feederMdl.getValueAt(rowID.intValue(), FeederTableModel.VAR_LOAD_COLUMN) %></td>
								<td width="45" class="TableCell"><%= feederMdl.getValueAt(rowID.intValue(), FeederTableModel.TIME_STAMP_COLUMN) %></td>                        
                        <td width="45" class="TableCell"><%= feederMdl.getValueAt(rowID.intValue(), FeederTableModel.POWER_FACTOR_COLUMN) %></td>
                        <td width="45" class="TableCell"><%= feederMdl.getValueAt(rowID.intValue(), FeederTableModel.WATTS_COLUMN) %></td>
                        <td width="36" class="TableCell"><%= feederMdl.getValueAt(rowID.intValue(), FeederTableModel.DAILY_OPERATIONS_COLUMN) %></td>
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
                    </table>
                    <br>
		<% }
			else if( CapControlWebAnnex.CMD_CAPBANK.equals(controlType) )
			{
						label = capBankMdl.getRowAt(rowID.intValue()).getCcName();
						hiddenPAOid = capBankMdl.getRowAt(rowID.intValue()).getCcId();
%>                    
                  <table width="740" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td>
                        <table width="736" border="0" cellspacing="0" cellpadding="0">
                          <tr> 
                            <td class="HeaderCell">&nbsp;&nbsp;Capacitor Bank Data</td>
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
                      
                      <tr valign="top"> 
                        <td width="130" class="TableCell">
                          <div name = "sub" align = "left" cursor:default;"> 
	                       		<% if( capBankMdl.isRowAlarmed(rowID.intValue()) ) { %> <img src="images/AlarmFlag.gif" width="10"> <% } %>
                          		<%= capBankMdl.getValueAt(rowID.intValue(), CapBankTableModel.CB_NAME_COLUMN) %>
                          </div>
                        </td>
                        <td width="228" class="TableCell"><%= capBankMdl.getValueAt(rowID.intValue(), CapBankTableModel.BANK_ADDRESS_COLUMN) %></td>
                        
                        <td width="43" class="TableCell">
                        	<font color="<%= CapControlWebAnnex.convertColor(capBankMdl.getCellForegroundColor( rowID.intValue(), CapBankTableModel.STATUS_COLUMN ) ) %>">
                        	<%= capBankMdl.getValueAt(rowID.intValue(), CapBankTableModel.STATUS_COLUMN) %>
                        </font></td>
                        
                        <td width="98" class="TableCell"> <%= capBankMdl.getValueAt(rowID.intValue(), CapBankTableModel.TIME_STAMP_COLUMN) %></td>
                        <td width="33" class="TableCell"> <%= capBankMdl.getValueAt(rowID.intValue(), CapBankTableModel.BANK_SIZE_COLUMN) %></td>
                        <td width="34" class="TableCell"> <%= capBankMdl.getValueAt(rowID.intValue(), CapBankTableModel.OP_COUNT_COLUMN) %></td>
                      </tr>
                    </table>
		<%  } %>


                  <br>
                  <p>&nbsp;</p>


	<form name="form1" method="POST" action="<%=request.getContextPath()%>/servlet/CBCConnServlet" >
				  <table width="600" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td> 
                        <table width="600" border="0" cellspacing="0" cellpadding="0">
                          <tr height=32> 
                            <td class="HeaderCell" align="center">&nbsp;&nbsp;Available Controls for : 
                            	<span class="SchedText"> <%= label %> </span> </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>

                  <table width="604" border="1" align="center" cellpadding="2" cellspacing="0">
                    <tr class="HeaderCell"> 
                      <td width="16%" align="center"> 
                        <input type="radio" name="cmdID" value="<%= enableID %>">Enable</td>
                      <td width="16%" align="center"> 
                        <input type="radio" name="cmdID" value="<%= disableID %>">Disable</td>

<%                    if( CapControlWebAnnex.CMD_SUB.equals(controlType)
  	                  	  || CapControlWebAnnex.CMD_CAPBANK.equals(controlType) )
                       { 
                         //any confirm can be set here since it will send the real confirm based on the state  %>
                      <td width="16%" align="center"> 
                        <input type="radio" name="cmdID" value="<%= CBCCommand.CONFIRM_CLOSE %>">Confirm</td>
<%                     } %>


<%                    if( CapControlWebAnnex.CMD_CAPBANK.equals(controlType) )
                       { %>
                      <td width="16%" align="center"> 
                        <input type="radio" name="cmdID" value="<%= CBCCommand.OPEN_CAPBANK %>">Open Bank</td>
<%                     } %>

<%                    if( CapControlWebAnnex.CMD_CAPBANK.equals(controlType) )
                       { %>
                      <td width="16%" align="center"> 
                        <input type="radio" name="cmdID" value="<%= CBCCommand.CLOSE_CAPBANK %>">Close Bank</td>
<%                     } %>


<%                    if( CapControlWebAnnex.CMD_CAPBANK.equals(controlType) )
                       { %>
                      <td width="16%" align="center"> 
		                  <div align="left">
                        <input type="radio" id="ManCh" name="cmdID" value="<%= CBCCommand.CMD_MANUAL_ENTRY %>">Manual Change		                  
			               <select name="manualChange" onchange="ManCh.checked=true" >
<%	                  	for( int i = 0; i < CapBankTableModel.getStateNames().length; i++ )
	                  	{ %>
									<option value="<%= i %>">
										<%= CapBankTableModel.getStateNames()[i] %></option>
								<% } %>
                        </div>
							 </td>
<%                     } %>


                    </tr>
                  </table>				  

                  <p>
                    <input name="cmdExecute" type="submit" value="Submit">
                    <input name="cmdExecute" type="submit" value="Cancel">
                                        
                    <input name="redirectURL" type="hidden" value="<%= refererURL %>">
                    <input name="controlType" type="hidden" value="<%= controlType %>">
                    <input name="paoID" type="hidden" value="<%= hiddenPAOid %>">
                  </p>

                  <p>&nbsp; </p>
                </tr>
					</table>
	</form>


          </td>
        <td width="1" bgcolor="#000000"><img src="images/VerticalRule.gif" width="1"></td>
	    </tr>
      </table>

	  
	  
    </td>
	</tr>
</table>
</body>
</html>
