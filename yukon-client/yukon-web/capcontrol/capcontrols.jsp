
<%@ include file="js/cbc_funcs.js" %>
<%@ include file="cbc_header.jsp" %>

<%
	//-------- PARAMS ----------- 
	// paoID: The pao ID of the selected item in the table
	// controlType: The type of control we are to do (SUB_CNTRL, FEEDER_CNTRL, CAPBANK_CNTRL) 
		
	//get the page we came from, we will return here (null if not found)
	String refererURL = request.getHeader("referer");
	if( refererURL == null )
	{
		refererURL = request.getParameter("redirectURL");		
		//no need to refresh since we are moving to a different page
		cbcAnnex.setRefreshRate( CapControlWebAnnex.REF_SECONDS_DEF);
	}
		

	String strID = request.getParameter("paoID");
	String controlType = request.getParameter("controlType");
	String lastSubid = request.getParameter("lastSubID");

	String label = new String();	
	Integer paoID = null;
	int enableID = -1, disableID = -1;	
	
	
	try
	{
		paoID = new Integer(strID);
		
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
			
		if( lastSubid != null )
		{
			Integer subID = new Integer(lastSubid);
			cbcSession.setLastSubID( subID.intValue() );
			feederMdl.setCurrentSubBus( subBusMdl.getSubBus(subID.intValue()) );
			
			//set all the banks in the model to the selected SubBuses banks	
			java.util.Vector banks = new java.util.Vector(32);
			for( int i = 0; i < feederMdl.getRowCount(); i++ )
				banks.addAll( feederMdl.getRowAt(i).getCcCapBanks() );
				
			capBankMdl.setCapBankDevices( banks );
		}

	}
	catch( Exception e )
	{
		CTILogger.warn( 
				"This page did not successfully get the paoID, the referer URL, or the controlType paoID =" + strID, e ); 
		
		if( refererURL != null )
			response.sendRedirect( refererURL );
		else
		{
			//reset our filter to the default
			response.sendRedirect( "subs.jsp" );
		}
		
		return;
	}
%>


<html>
<head>
<title>CapControl - Controls</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="StyleSheet" rel="stylesheet" href="../WebConfig/yukon/CannonStyle.css" type="text/css">
<!--<link id="StyleSheet" rel="stylesheet" href="../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">-->
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
					SubBus subBus = subBusMdl.getSubBus(paoID.intValue());
					label = subBus.getCcName();
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

                        <cti:isPropertyFalse propertyid="<%= CBCSettingsRole.HIDE_GRAPHS %>">
                        <td width="71">Graphs</td>
                        </cti:isPropertyFalse>

                        <cti:isPropertyFalse propertyid="<%= CBCSettingsRole.HIDE_REPORTS %>">
                        <td width="80">Reports</td>
                        </cti:isPropertyFalse>

                      </tr>
                      <tr valign="top"> 
                        <td width="100" class="TableCell"><a href= "feeders.jsp?paoID=<%= paoID.intValue() %>" class="Link1">
                          <div name = "subPopup" align = "left" cursor:default;" >
                             <%= cbcAnnex.getCBCDisplay().getSubBusValueAt(subBus, SubBusTableModel.SUB_NAME_COLUMN) %> 
                          </div></a>
                        </td>
                          

                        <td width="44" class="TableCell">
                        	<font color="<%= CapControlWebAnnex.convertColor(subBusMdl.getCellColor(subBus) ) %>">
                        	<%= cbcAnnex.getCBCDisplay().getSubBusValueAt(subBus, SubBusTableModel.CURRENT_STATE_COLUMN) %>
                        </font></td>
                        
                        <td width="44" class="TableCell"><%= cbcAnnex.getCBCDisplay().getSubBusValueAt(subBus, SubBusTableModel.TARGET_COLUMN) %></td>
                        <td width="36" class="TableCell"><%= cbcAnnex.getCBCDisplay().getSubBusValueAt(subBus, SubBusTableModel.VAR_LOAD_COLUMN) %></td>
                        <td width="45" class="TableCell"><%= cbcAnnex.getCBCDisplay().getSubBusValueAt(subBus, SubBusTableModel.TIME_STAMP_COLUMN) %></td>
                        <td width="45" class="TableCell"><%= cbcAnnex.getCBCDisplay().getSubBusValueAt(subBus, SubBusTableModel.POWER_FACTOR_COLUMN) %></td>
                        <td width="33" class="TableCell"><%= cbcAnnex.getCBCDisplay().getSubBusValueAt(subBus, SubBusTableModel.WATTS_COLUMN) %></td>
                        <td width="36" class="TableCell"><%= cbcAnnex.getCBCDisplay().getSubBusValueAt(subBus, SubBusTableModel.DAILY_OPERATIONS_COLUMN) %></td>

                        <cti:isPropertyFalse propertyid="<%= CBCSettingsRole.HIDE_GRAPHS %>">
                        <td width="71" class="TableCell"> 
                          <select name="select2">
                            <option>Graph A</option>
                            <option>Graph B</option>
                          </select>
                        </td>
                        </cti:isPropertyFalse>

                        <cti:isPropertyFalse propertyid="<%= CBCSettingsRole.HIDE_REPORTS %>">
                        <td width="80" class="TableCell"> 
                          <select name="select3">
                            <option>Report A</option>
                            <option>Report B</option>
                          </select>
                        </td>
                        </cti:isPropertyFalse>

                      </tr>
                    </table>
                  <br>
		<% }
			else if( CapControlWebAnnex.CMD_FEEDER.equals(controlType) )
			{
				Feeder feeder = feederMdl.getFeeder(paoID.intValue());
				SubBus parentSub = subBusMdl.getSubBus(cbcSession.getLastSubID());
				
				label = feeder.getCcName();
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

                        <cti:isPropertyFalse propertyid="<%= CBCSettingsRole.HIDE_GRAPHS %>">
                        <td width="71">Graphs</td>
                        </cti:isPropertyFalse>

                        <cti:isPropertyFalse propertyid="<%= CBCSettingsRole.HIDE_REPORTS %>">
                        <td width="80">Reports</td>
                        </cti:isPropertyFalse>

                      </tr>
                      
                      <tr valign="top"> 
                        <td width="100" class="TableCell">
                          <div name = "sub" align = "left" cursor:default;">
								<%= cbcAnnex.getCBCDisplay().getFeederValueAt(feeder, FeederTableModel.NAME_COLUMN, parentSub) %> </div>
                          </td>
                        <td width="44" class="TableCell">
                        	<font color="<%= CapControlWebAnnex.convertColor( feederMdl.getCellColor(feeder) ) %>">
                        	<%= cbcAnnex.getCBCDisplay().getFeederValueAt(feeder, FeederTableModel.CURRENT_STATE_COLUMN, parentSub) %>
                        </font></td>
                        
                        
                        <td width="44" class="TableCell"><%= cbcAnnex.getCBCDisplay().getFeederValueAt(feeder, FeederTableModel.TARGET_COLUMN, parentSub) %></td>
                        <td width="36" class="TableCell"><%= cbcAnnex.getCBCDisplay().getFeederValueAt(feeder, FeederTableModel.VAR_LOAD_COLUMN, parentSub) %></td>
						<td width="45" class="TableCell"><%= cbcAnnex.getCBCDisplay().getFeederValueAt(feeder, FeederTableModel.TIME_STAMP_COLUMN, parentSub) %></td>
                        <td width="45" class="TableCell"><%= cbcAnnex.getCBCDisplay().getFeederValueAt(feeder, FeederTableModel.POWER_FACTOR_COLUMN, parentSub) %></td>
                        <td width="45" class="TableCell"><%= cbcAnnex.getCBCDisplay().getFeederValueAt(feeder, FeederTableModel.WATTS_COLUMN, parentSub) %></td>
                        <td width="36" class="TableCell"><%= cbcAnnex.getCBCDisplay().getFeederValueAt(feeder, FeederTableModel.DAILY_OPERATIONS_COLUMN, parentSub) %></td>

                        <cti:isPropertyFalse propertyid="<%= CBCSettingsRole.HIDE_GRAPHS %>">
                        <td width="71" class="TableCell"> 
                          <select name="select5">
                            <option>Graph A</option>
                            <option>Graph B</option>
                          </select>
                        </td>
                        </cti:isPropertyFalse>
                        
                        <cti:isPropertyFalse propertyid="<%= CBCSettingsRole.HIDE_REPORTS %>">
                        <td width="80" class="TableCell"> 
                          <select name="select5">
                            <option>Report A</option>
                            <option>Report B</option>
                          </select>
                        </td>
                        </cti:isPropertyFalse>

                      </tr>
                    </table>
                    <br>
		<% }
			else if( CapControlWebAnnex.CMD_CAPBANK.equals(controlType) )
			{
				CapBankDevice capBank = capBankMdl.getCapbank(paoID.intValue());				
				label = capBank.getCcName();
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
                          	<%= cbcAnnex.getCBCDisplay().getCapBankValueAt(capBank, CapBankTableModel.CB_NAME_COLUMN) %>
                          </div>
                        </td>
                        <td width="228" class="TableCell"><%= cbcAnnex.getCBCDisplay().getCapBankValueAt(capBank, CapBankTableModel.BANK_ADDRESS_COLUMN) %></td>
                        
                        <td width="43" class="TableCell">
                        	<font color="<%= CapControlWebAnnex.convertColor( capBankMdl.getCellColor(capBank) ) %>">
                        	<%= cbcAnnex.getCBCDisplay().getCapBankValueAt(capBank, CapBankTableModel.STATUS_COLUMN) %>
                        </font></td>
                        
                        <td width="98" class="TableCell"> <%= cbcAnnex.getCBCDisplay().getCapBankValueAt(capBank, CapBankTableModel.TIME_STAMP_COLUMN) %></td>
                        <td width="33" class="TableCell"> <%= cbcAnnex.getCBCDisplay().getCapBankValueAt(capBank, CapBankTableModel.BANK_SIZE_COLUMN) %></td>
                        <td width="34" class="TableCell"> <%= cbcAnnex.getCBCDisplay().getCapBankValueAt(capBank, CapBankTableModel.OP_COUNT_COLUMN) %></td>
                      </tr>
                    </table>
		<%  } %>


                  <br>
                  <p>&nbsp;</p>


	<form name="form1" method="POST" action="<%=request.getContextPath()%>/servlet/CBCConnServlet" >
	
<%			    if( CapControlWebAnnex.CMD_SUB.equals(controlType)
  	                 || CapControlWebAnnex.CMD_CAPBANK.equals(controlType) )
                { %>
				  <table width="600" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td> 
                        <table width="600" border="0" cellspacing="0" cellpadding="0">
                          <tr height=20> 
                            <td class="HeaderCell" align="center">&nbsp;&nbsp;Available Field Commands
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
<%              } %>

                  <table width="604" border="1" align="center" cellpadding="2" cellspacing="0">
                    <tr class="HeaderCell"> 

<%                    if( CapControlWebAnnex.CMD_SUB.equals(controlType)
  	                  	  || CapControlWebAnnex.CMD_CAPBANK.equals(controlType) )
                       { 
                         //any confirm can be set here since it will send the real confirm based on the state  %>
                      <td width="16%" align="center"> 
                        <input type="radio" name="cmdID" value="<%= CBCCommand.CONFIRM_CLOSE %>">Confirm</td>
<%                     } %>

<%                    if( CapControlWebAnnex.CMD_CAPBANK.equals(controlType) )
                       { %>
					<cti:checkProperty propertyid="<%= TDCRole.CBC_ALLOW_OVUV %>"> 
                      <td width="16%" align="center"> 
                        <input type="radio" name="cmdID" value="<%= CBCCommand.BANK_ENABLE_OVUV %>">Enable OV/UV</td>
                      <td width="16%" align="center">	 
                        <input type="radio" name="cmdID" value="<%= CBCCommand.BANK_DISABLE_OVUV %>">Disable OV/UV</td>
					</cti:checkProperty>
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

                    </tr>
                  </table>				  

				  <table width="600" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr height=10><td></td></tr>				  
                    <tr>
                      <td> 
                        <table width="600" border="0" cellspacing="0" cellpadding="0">
                          <tr height=20> 
                            <td class="HeaderCell" align="center">&nbsp;&nbsp;Available Local Commands 
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>

                  <table width="604" border="1" align="center" cellpadding="2" cellspacing="0">
                    <tr class="HeaderCell"> 
                      <td width="16%" align="center"> 
                        <input type="radio" name="cmdID" value="<%= enableID %>">Enable Control</td>
                      <td width="16%" align="center"> 
                        <input type="radio" name="cmdID" value="<%= disableID %>">Disable Control</td>

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
	                <cti:isPropertyTrue propertyid="<%= CBCSettingsRole.ALLOW_CONTROLS %>">
	                    <input name="cmdExecute" type="submit" value="Submit">
	                </cti:isPropertyTrue>
                    <input name="cmdExecute" type="submit" value="Cancel">
                                        
                    <input name="redirectURL" type="hidden" value="<%= refererURL %>">
                    <input name="controlType" type="hidden" value="<%= controlType %>">
                    <input name="paoID" type="hidden" value="<%= paoID %>">
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
