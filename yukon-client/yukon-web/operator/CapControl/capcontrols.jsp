
<%@ include file="Functions.jsp" %>
<%@ include file="cbc_header.jsp" %>

<!-- JavaScript needed for jump menu--->

<%
	//-------- PARAMS ----------- 
	// rowID: The row number of the selected item as it appears in the table
	// controlType: The type of control we are to do (SUB_CNTRL, FEEDER_CNTRL, CAPBANK_CNTRL
		
		
	//get the page we came from, we will return here (null if not found)
	String refererURL = request.getHeader("referer");
	
	Integer rowID = null;
	String strID = request.getParameter("rowID");
	String controlType = request.getParameter("controlType");
	
	try
	{
		rowID = new Integer(strID);
		
		if( refererURL == null )
			throw new IllegalArgumentException("The referer page attribute should not be (null)");
		
		if( controlType == null )
			throw new IllegalArgumentException("The controlType attribute should not be (null)");
	}
	catch( Exception e )
	{
		CTILogger.warn( 
				"This page did not successfully get the rowID, the referer URL, or the controlType rowID =" + strID, e ); 
		
		//reset our filter to the default
		if( refererURL != null )
			response.sendRedirect( refererURL );
		else
		{
			cbcSession.setLastArea( SubBusTableModel.ALL_FILTER );		
			response.sendRedirect( "AllSubs.jsp" );
		}
		
		return;
	}
%>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="demostyle.css" type="text/css">
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
                  <div align="center"><span><a href="../Operations.jsp" class="Link3"></a></span></div>
                  </td>
                  
                <td width="57" valign="middle"> 
                  <div align="left"><span ><a href="../../login.jsp" class="Link3"></a><font color="99FFFF" size="2" face="Arial, Helvetica, sans-serif">&nbsp;</font></span></div>
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
                  <div align="center"><span class="Header">Controls View</span>

                  </div></td>
              </tr>
            </table> 
            
            
          </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
			<table width="657" border="0" cellspacing="0" cellpadding="0">
			  <tr> 
                <td width="650" valign="top" class="Main" align="center"> 
                  <p>&nbsp;</p>
                  <form name="MForm"> 

		<% if( "SUB_CNTRL".equals(controlType) )
			{ %>
                  <table width="600" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td>
                        <table width="600" border="0" cellspacing="0" cellpadding="0">
                          <tr bgcolor="#CCCCCC"> 
                              
                              <td width="409"><span class="TableCell"></span><span class="HeaderCell">&nbsp;&nbsp; 
                                Substation Bus Data</span></td>
                              <td width="191"> 
                                
                              <div align="right">
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
                        <td width="100" class="TableCell"><a href= "Feeders.jsp?subRowID=<%= rowID.intValue() %>" >
                          <div name = "subPopup" align = "left" cursor:default;" onMouseOver = "menuAppear(event, 'subMenu')" >
                             <%= subBusMdl.getValueAt(rowID.intValue(), SubBusTableModel.SUB_NAME_COLUMN) %> 
                          </div></a>
                        </td>
                          

                        <td width="44" class="TableCell">
                        	<font color="<%= cbcServlet.convertColor(subBusMdl.getCellForegroundColor( rowID.intValue(), SubBusTableModel.CURRENT_STATE_COLUMN ) ) %>">
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
			else if( "FEEDER_CNTRL".equals(controlType) )
			{ %>
                  <table width="600" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr>
                      <td>
                        <table width="600" border="0" cellspacing="0" cellpadding="0">
                          <tr bgcolor="#CCCCCC"> 
                              
                              <td class="HeaderCell">&nbsp;&nbsp; Feeder Data 
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
                      
                      <tr valign="top"> 
                        <td width="100" class="TableCell">
                          <div name = "sub" align = "left" cursor:default;" onMouseOver = "menuAppear(event, 'FeederMenu')" >
									<%= feederMdl.getValueAt(rowID.intValue(), FeederTableModel.NAME_COLUMN) %> </div>
                          </td>
                        <td width="44" class="TableCell">
                        	<font color="<%= cbcServlet.convertColor(feederMdl.getCellForegroundColor( rowID.intValue(), FeederTableModel.CURRENT_STATE_COLUMN ) ) %>">
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
			else if( "CAPBANK_CNTRL".equals(controlType) )
			{ %>
                    
                  <table width="600" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td>
                        <table width="600" border="0" cellspacing="0" cellpadding="0">
                          <tr bgcolor="#CCCCCC"> 
                              
                              <td class="HeaderCell">&nbsp;&nbsp;Capacitor Bank 
                                Data<font color="##666699"> </font> </td>
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
                      
                      <tr valign="top"> 
                        <td width="130" class="TableCell">
                          <div name = "sub" align = "left" cursor:default;" onMouseOver = "menuAppear(event, 'CapMenu')" > 
                          		<%= capBankMdl.getValueAt(rowID.intValue(), CapBankTableModel.CB_NAME_COLUMN) %>
                          </div>
                        </td>
                        <td width="228" class="TableCell"><%= capBankMdl.getValueAt(rowID.intValue(), CapBankTableModel.BANK_ADDRESS_COLUMN) %></td>
                        
                        <td width="43" class="TableCell">
                        	<font color="<%= cbcServlet.convertColor(capBankMdl.getCellForegroundColor( rowID.intValue(), CapBankTableModel.STATUS_COLUMN ) ) %>">
                        	<%= capBankMdl.getValueAt(rowID.intValue(), CapBankTableModel.STATUS_COLUMN) %>
                        </font></td>
                        
                        <td width="98" class="TableCell"> <%= capBankMdl.getValueAt(rowID.intValue(), CapBankTableModel.TIME_STAMP_COLUMN) %></td>
                        <td width="33" class="TableCell"> <%= capBankMdl.getValueAt(rowID.intValue(), CapBankTableModel.BANK_SIZE_COLUMN) %></td>
                        <td width="34" class="TableCell"> <%= capBankMdl.getValueAt(rowID.intValue(), CapBankTableModel.OP_COUNT_COLUMN) %></td>
                      </tr>
                    </table>
		<%  } %>

				  </form>


                  <br>
                  <p>&nbsp;</p>
				  <table width="600" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td> 
                        <table width="600" border="0" cellspacing="0" cellpadding="0">
                          <tr bgcolor="#CCCCCC"> 
                            <td class="HeaderCell" align="center">&nbsp;&nbsp;<font size="2">Valid 
                              Controls</font> </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                  <table width="604" border="0" align="center" cellpadding="2" cellspacing="0">
                    <tr bgcolor="#CCCCCC" class="HeaderCell1"> 
                      <td width="90" align="center"> 
                        <input type="radio" name="cmd" value="confirmButton">
                        Confirm </td>
                      <td width="90" align="center"> 
                        <input type="radio" name="cmd" value="disableButton">
                        Disable </td>
                      <td width="90" align="center"> 
                        <input type="radio" name="cmd" value="enableButton">
                        Enable </td>
                      <td width="90" align="center">&nbsp; </td>
                      <td width="90" align="center">&nbsp; </td>
                      <td width="90" align="center">&nbsp; </td>
                    </tr>
                  </table>				  
                  <p> 
                    <input name="Submit_Cntrl" type="submit" id="Submit_Cntrl" value="Submit">
                    <input name="Cancel_Cntrl" type="submit" id="Cancel_Cntrl" value="Cancel">
                  </p>
                  <p>&nbsp; </p>
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
