
<%@ include file="js/cbc_funcs.js" %>
<%@ include file="cbc_header.jsp" %>

<!-- JavaScript needed for jump menu--->

<%
	SubBus subBus = subBusMdl.getSubBus(cbcSession.getLastSubID());
	String strID = request.getParameter("paoID");
	Integer paoID = null;
	Feeder feeder = null;

	try
	{
		feederMdl.setCurrentSubBus( subBus );

		paoID = new Integer(strID);
		feeder = feederMdl.getFeeder(paoID.intValue());

		CTILogger.debug(request.getServletPath() + "	FdrRowCnt = " + feederMdl.getRowCount() );
		
		capBankMdl.setCapBankDevices( feeder.getCcCapBanks() );
		
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
<title>CapControl - Single Feeder</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<meta http-equiv="refresh" content= <%= cbcAnnex.getRefreshRate() %> >
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<!--<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">-->
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
		  
		  
		  
			<table width="740" border="1" align="center" cellspacing="0" cellpadding="0">
			  <tr> 
                <td>                   
                  <table width="740" border="0" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td>
                          <tr class="HeaderCell"> 
                              <td width="409">&nbsp;&nbsp;Single Feeder View for the Sub:
                                <a href= "feeders.jsp?paoID=<%= cbcSession.getLastSubID() %>" class="Link4">
                                <span class="SchedText"> 
                                <%= subBus %>
                                </span>
                                </a>
                                </td>
                                

							<form name="FeederForm" method="POST" action="singlefeeder.jsp">
                        <td width="300" height="40"> 
                          <div align="right">&nbsp;&nbsp;Other Feeders on Sub :
							    
		                    <select name="paoID" onchange="this.form.submit()" >
			                  <%
			                  	for( int i = 0; i < feederMdl.getRowCount(); i++ )
			                  	{
			                  		Feeder tempFdr = feederMdl.getRowAt(i);
			                  		String s = (paoID.intValue() == tempFdr.getCcId().intValue()
			                  						? " selected" : "" ) ;
			                  		%>
											<option value="<%= tempFdr.getCcId() %>" <%= s %>> 
												<%= tempFdr.getCcName() %> 
											</option>
			                  <% } %>

		                    </select>                            
                        </div>
                      </td>
				         </form>
                              
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
                        	<%= cbcAnnex.getCBCDisplay().getFeederValueAt(feeder, FeederTableModel.NAME_COLUMN, subBus) %>
                       	</td>

                        <td width="44" class="TableCell">
                        	<a href= "capcontrols.jsp?paoID=<%= paoID %>&controlType=<%= CapControlWebAnnex.CMD_FEEDER %>" >
                        	<font color="<%= CapControlWebAnnex.convertColor( feederMdl.getCellColor(feeder) ) %>">
                        	<%= cbcAnnex.getCBCDisplay().getFeederValueAt(feeder, FeederTableModel.CURRENT_STATE_COLUMN, subBus) %>
                        </font></a></td>
                        
                        
                        <td width="44" class="TableCell"><%= cbcAnnex.getCBCDisplay().getFeederValueAt(feeder, FeederTableModel.TARGET_COLUMN, subBus) %></td>
                        <td width="36" class="TableCell"><%= cbcAnnex.getCBCDisplay().getFeederValueAt(feeder, FeederTableModel.VAR_LOAD_COLUMN, subBus) %></td>
						<td width="45" class="TableCell"><%= cbcAnnex.getCBCDisplay().getFeederValueAt(feeder, FeederTableModel.TIME_STAMP_COLUMN, subBus) %></td>
                        <td width="45" class="TableCell"><%= cbcAnnex.getCBCDisplay().getFeederValueAt(feeder, FeederTableModel.POWER_FACTOR_COLUMN, subBus) %></td>
                        <td width="45" class="TableCell"><%= cbcAnnex.getCBCDisplay().getFeederValueAt(feeder, FeederTableModel.WATTS_COLUMN, subBus) %></td>
                        <td width="36" class="TableCell"><%= cbcAnnex.getCBCDisplay().getFeederValueAt(feeder, FeederTableModel.DAILY_OPERATIONS_COLUMN, subBus) %></td>
                        <td width="71" class="TableCell"> 
                          <select name="select5">
                            <option>Feeder kVar</option>
                            <option>Graph B</option>
                          </select>
                        </td>
                        <td width="80" class="TableCell"> 
                          <select name="select5">
                            <option>Peaks</option>
                            <option>Ops.</option>
                          </select>
                        </td>
                      </tr>
                    </table>
				</td>
				</tr>
			</table>
					
                    <br>
                  <table width="740" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td>
                        <table width="736" border="0" cellspacing="0" cellpadding="0">
                          <tr> 
                              <td class="HeaderCell">&nbsp;&nbsp;Capacitor Banks for Feeder :
                                <span class="SchedText"> <%= feeder.getCcName() %> </span>
                              </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                    <table width="740" border="1" align="center" cellpadding="2" cellspacing="0">
                      <tr class="HeaderCell"> 
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
