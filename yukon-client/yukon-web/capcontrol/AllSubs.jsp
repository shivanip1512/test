<!-- JavaScript needed for jump menu--->
<%@ include file="Functions.js" %>
<%@ include file="cbc_header.jsp" %>


<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<meta http-equiv="refresh" content= <%= cbcSession.getRefreshRate() %> >
<link id="StyleSheet" rel="stylesheet" href="../WebConfig/CannonStyle.css" type="text/css">
<!--<link id="StyleSheet" rel="stylesheet" href="../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">-->
</head>

<body class="Background" leftmargin="0" topmargin="0">
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
              <tr> 
                <td width="353" height = "28" class="PageHeader">&nbsp;&nbsp;&nbsp;Capacitor Control 
            		<% if( !cbcServlet.isConnected() ) {%><font color="#FFFF00"> (Not connected) </font><%}%>
            		<% if( cbcSession.getRefreshRate().equals(CapControlWebAnnex.REF_SECONDS_PEND) ) {%><font color="#FFFF00"> 
            			(Auto-refresh in <%= CapControlWebAnnex.REF_SECONDS_PEND %> seconds) </font><%}%>
            		</td>
                <td width="235" valign="middle">&nbsp;</td>
                
                  
                <td width="58" valign="middle"> 
                  <div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>                
                  </td>
                  
                <td width="57" valign="middle"> 
                  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
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
                  <div align="center"><span class="Header">SUB AREA 
                    </span> 

						<form name="AreaForm" method="POST" >
                    <select name="area" onchange="this.form.submit()" >
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
                          <tr class="HeaderCell"> 
                              <td width="409">&nbsp;&nbsp;Substation Buses for the Area : 
                                <span class="SchedText"> <%= cbcSession.getLastArea() %> </span></td>
                              <td width="191"> 
                                <div align="right"> </div>
                              </td>
                            </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                  
                  
				    <table width="604" border="1" align="center" cellpadding="2" cellspacing="0">
                      <tr class="HeaderCell"> 
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
                      
	                  <% 
	                  	for( int i = 0; i < subBusMdl.getRowCount(); i++ )
	                  	{
	                  %>         
                      
                      <tr valign="top"> 
                        <td width="100" class="TableCell"><a href= "Feeders.jsp?subRowID=<%= i %>" class="Link1">
                          <div name = "subPopup" align = "left" cursor:default;" >
                             <%= subBusMdl.getValueAt(i, SubBusTableModel.SUB_NAME_COLUMN) %> 
                          </div></a>
                        </td>

                        <td width="44" class="TableCell">
                        	<a href= "capcontrols.jsp?rowID=<%= i %>&controlType=<%= CapControlWebAnnex.CMD_SUB %>" >
	                    		<font color="<%= CapControlWebAnnex.convertColor(subBusMdl.getCellForegroundColor( i, SubBusTableModel.CURRENT_STATE_COLUMN ) ) %>">
	                    		<%= subBusMdl.getValueAt(i, SubBusTableModel.CURRENT_STATE_COLUMN) %> 
	                    		</font></a>
                        </td>

                        <td width="44" class="TableCell"><%= subBusMdl.getValueAt(i, SubBusTableModel.TARGET_COLUMN) %></td>
                        <td width="36" class="TableCell"><%= subBusMdl.getValueAt(i, SubBusTableModel.VAR_LOAD_COLUMN) %></td>
                        <td width="45" class="TableCell"><%= subBusMdl.getValueAt(i, SubBusTableModel.TIME_STAMP_COLUMN) %></td>
                        <td width="45" class="TableCell"><%= subBusMdl.getValueAt(i, SubBusTableModel.POWER_FACTOR_COLUMN) %></td>
                        <td width="33" class="TableCell"><%= subBusMdl.getValueAt(i, SubBusTableModel.WATTS_COLUMN) %></td>
                        <td width="36" class="TableCell"><%= subBusMdl.getValueAt(i, SubBusTableModel.DAILY_OPERATIONS_COLUMN) %></td>
                        <td width="71" class="TableCell"> 

                        
                         <form name="GraphForm" >
                          <select name="selectGraph" onchange="location = this.options[this.selectedIndex].value;">
                            <option value="AllSubs.jsp">Graph A</option>
                            <option value="AllSubs.jsp">Graph B</option>
                            <option value="temp\<%= subBusMdl.getValueAt(i, SubBusTableModel.SUB_NAME_COLUMN) %>.html">One Line</option>
                          </select>
                         </form>
                          
                        </td>
                        <td width="80" class="TableCell"> 
                          <select name="select3">
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
