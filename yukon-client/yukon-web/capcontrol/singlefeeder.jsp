
<%@ include file="Functions.jsp" %>
<%@ include file="cbc_header.jsp" %>

<!-- JavaScript needed for jump menu--->

<%
	Integer feederRowID = null;
	String strID = request.getParameter("feederRowID");
	try
	{
		feederRowID = new Integer(strID);
		
		CTILogger.debug(request.getServletPath() + "	FdrRowCnt = " + feederMdl.getRowCount() );
		
		if( feederRowID.intValue() >= 0 && feederRowID.intValue() < feederMdl.getRowCount() )
		{
			capBankMdl.setCapBankDevices( feederMdl.getRowAt( feederRowID.intValue() ).getCcCapBanks() );
			
			CTILogger.debug(request.getServletPath() + "	CapRowCnt = " + capBankMdl.getRowCount() );
		}
		else  //just force ourself to catch a newly created exception
			throw new ArrayIndexOutOfBoundsException(
					"Feeder Row id value out of range, fdrRowCount = " + feederMdl.getRowCount() );
	}
	catch( Exception e )
	{
		CTILogger.warn( 
				"This page did not successfully get the feederRowID needed, feederRowID=" + strID, e ); 
		
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
<meta http-equiv="refresh" content= <%= cbcSession.getRefreshRate() %> >
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
                <td width="353" height = "28" class="Header3">&nbsp;&nbsp;<font color="#99FFFF" size="2" face="Arial, Helvetica, sans-serif"><em>&nbsp;
                	Capacitor Control 
            		<% if( !cbcServlet.isConnected() ) {%><font color="#FFFF00"> (Not connected) </font><%}%>
            		<% if( cbcSession.getRefreshRate().equals(CapControlWebAnnex.REF_SECONDS_PEND) ) {%><font color="#FFFF00"> 
            			(Auto-refresh in <%= CapControlWebAnnex.REF_SECONDS_PEND %> seconds) </font><%}%>
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
	                  	for( int i = 0; i < CapControlWebAnnex.getAreaNames().size(); i++ )
	                  	{
	                  		String area = CapControlWebAnnex.getAreaNames().get(i).toString();
	                  		
	                  		String s = ( area.equalsIgnoreCase(cbcSession.getLastArea()) 
	                  						? " selected" : "" ) ;
	                  		%>
	                  		
									<option value="<%= area %>" <%= s %>><%= area %></option>
	                  <% } %>
                    </select>
			         </form>

                  </div></td>
              </tr>
            </table> 
            
            
          </td>

          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
			<table width="657" border="0" cellspacing="0" cellpadding="0">
			  <tr> 
                <td width="650" valign="top" class="MainText"> 
                  
                  <p>&nbsp;</p>
                  <form name="MForm"> 
                  <table width="600" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td>
                        <table width="600" border="0" cellspacing="0" cellpadding="0">
                          <tr bgcolor="#CCCCCC"> 
                              <td width="409"><span class="TableCell"></span><span class="HeaderCell">&nbsp;&nbsp;
                              	Single Feeder View for the Sub:                                 
                                <a href= "Feeders.jsp?subRowID=<%= cbcSession.getLastSubRowNum() %>">
                                <font color="##666699"> 
                                <%= subBusMdl.getValueAt(cbcSession.getLastSubRowNum(), SubBusTableModel.SUB_NAME_COLUMN) %>
                                </font>
                                </a>
                                </span></td>
                                

							<form name="FeederForm" method="POST" >
                        <td width="300" height="40"> 
                          <div align="right"> <span class="HeaderCell">&nbsp;&nbsp;
							    Other Feeders on Sub : </span> 
							    
		                    <select name="feederRowID" onchange="this.form.submit()" >
			                  <%
			                  	for( int i = 0; i < feederMdl.getRowCount(); i++ )
			                  	{
			                  		String s = (feederRowID.intValue() == i 
			                  						? " selected" : "" ) ;
			                  		%>
											<option value="<%= i %>" <%= s %>> 
												<%= feederMdl.getValueAt(i, FeederTableModel.NAME_COLUMN) %> 
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
                        <td width="100" class="TableCell"><%= feederMdl.getValueAt(feederRowID.intValue(), FeederTableModel.NAME_COLUMN) %></td>

                        <td width="44" class="TableCell">
                        	<a href= "capcontrols.jsp?rowID=<%= feederRowID.intValue() %>&controlType=<%= CapControlWebAnnex.CMD_FEEDER %>" >
                        	<font color="<%= CapControlWebAnnex.convertColor(feederMdl.getCellForegroundColor( feederRowID.intValue(), FeederTableModel.CURRENT_STATE_COLUMN ) ) %>">
                        	<%= feederMdl.getValueAt(feederRowID.intValue(), FeederTableModel.CURRENT_STATE_COLUMN) %>
                        </font></a></td>
                        
                        
                        <td width="44" class="TableCell"><%= feederMdl.getValueAt(feederRowID.intValue(), FeederTableModel.TARGET_COLUMN) %></td>
                        <td width="36" class="TableCell"><%= feederMdl.getValueAt(feederRowID.intValue(), FeederTableModel.VAR_LOAD_COLUMN) %></td>
								<td width="45" class="TableCell"><%= feederMdl.getValueAt(feederRowID.intValue(), FeederTableModel.TIME_STAMP_COLUMN) %></td>                        
                        <td width="45" class="TableCell"><%= feederMdl.getValueAt(feederRowID.intValue(), FeederTableModel.POWER_FACTOR_COLUMN) %></td>
                        <td width="45" class="TableCell"><%= feederMdl.getValueAt(feederRowID.intValue(), FeederTableModel.WATTS_COLUMN) %></td>
                        <td width="36" class="TableCell"><%= feederMdl.getValueAt(feederRowID.intValue(), FeederTableModel.DAILY_OPERATIONS_COLUMN) %></td>
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
                  <table width="600" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td>
                        <table width="600" border="0" cellspacing="0" cellpadding="0">
                          <tr bgcolor="#CCCCCC"> 
                              <td class="HeaderCell">&nbsp;&nbsp;Capacitor Banks for Feeder :
                                <font color="##666699"> <%= feederMdl.getValueAt(feederRowID.intValue(), FeederTableModel.NAME_COLUMN) %> </font>
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
                          <div name = "sub" align = "left" cursor:default;" > 
	                       		<% if( capBankMdl.isRowAlarmed(i) ) { %> <img src="./images/AlarmFlag.gif" width="10"> <% } %>
                          		<%= capBankMdl.getValueAt(i, CapBankTableModel.CB_NAME_COLUMN) %>
                          </div>
                        </td>
                        <td width="228" class="TableCell"><%= capBankMdl.getValueAt(i, CapBankTableModel.BANK_ADDRESS_COLUMN) %></td>
                        
                        <td width="43" class="TableCell">
                        	<a href= "capcontrols.jsp?rowID=<%= i %>&controlType=<%= CapControlWebAnnex.CMD_CAPBANK %>" >
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
				  </form>
                  <br>
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
