<!-- JavaScript needed for jump menu--->
<%@ include file="js/cbc_funcs.js" %>
<%@ include file="cbc_header.jsp" %>


<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<meta http-equiv="refresh" content= <%= cbcAnnex.getRefreshRate() %> >
<link rel="stylesheet" href="../WebConfig/yukon/CannonStyle.css" type="text/css">
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
                  <div align="center"><span><a href="../operator/Operations.jsp" class="Link3"><font color="99FFFF" size="2" face="Arial, Helvetica, sans-serif">Home</font></a></span></div>
                  </td>
                  
                <td width="57" valign="middle"> 
                  <div align="left"><span ><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">
                  	<font color="99FFFF" size="2" face="Arial, Helvetica, sans-serif">
                  	 Log Off</font></a><font color="99FFFF" size="2" face="Arial, Helvetica, sans-serif">&nbsp;</font></span></div>
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
          <td width="759" bgcolor="#000000" valign="top"></td>
		  <td width="1" bgcolor="#000000" height="2"></td>
        </tr>
        <tr>
		  <td width="5" bgcolor="#FFFFFF" height="5"></td>
        </tr>
        <tr> 
          <td width="759" valign="top" bgcolor="#FFFFFF"> 
            <table width="740" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr>
                <td><form name="AreaForm" method="POST" >                      
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
            <table width="740" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr>
                <td width="740" valign="top" class="MainText"> 
                  <table width="740" border="1" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td>
                        <table width="740" border="0" cellspacing="0" cellpadding="0">
                          <tr class="HeaderCell"> 
                              <td width="409"><span class="HeaderCell">&nbsp;&nbsp;
                                Substation Buses for the Area : 
                                <font color="##666699"> <%= cbcSession.getLastArea() %> </font></span></td>

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
                        <td width="110"><%= subBusMdl.getColumnName(SubBusTableModel.SUB_NAME_COLUMN) %></td>
                        <td width="68"> <%= subBusMdl.getColumnName(SubBusTableModel.CURRENT_STATE_COLUMN) %></td>
                        <td width="70"> <%= subBusMdl.getColumnName(SubBusTableModel.TARGET_COLUMN) %></td>
                        <td width="70"> <%= subBusMdl.getColumnName(SubBusTableModel.VAR_LOAD_COLUMN) %></td>
                        <td width="68"> <%= subBusMdl.getColumnName(SubBusTableModel.TIME_STAMP_COLUMN) %></td>
                        <td width="70"> <%= subBusMdl.getColumnName(SubBusTableModel.POWER_FACTOR_COLUMN) %></td>
                        <td width="50"> <%= subBusMdl.getColumnName(SubBusTableModel.WATTS_COLUMN) %></td>
                        <td width="66"> <%= subBusMdl.getColumnName(SubBusTableModel.DAILY_OPERATIONS_COLUMN) %></td>
                        <td width="72">Graphs</td>
                        <td width="72">Reports</td>
                      </tr>       
                      
	                  <% 
	                  	for( int i = 0; i < subBusMdl.getRowCount(); i++ )
	                  	{
	                  %>         
                      
                      <tr valign="top"> 
                        <td width="110" class="TableCell"><a href= "feeders.jsp?subRowID=<%= i %>" >
                          <div name = "subPopup" align = "left" cursor:default;" >
                             <%= subBusMdl.getValueAt(i, SubBusTableModel.SUB_NAME_COLUMN) %> 
                          </div></a>
                        </td>

                        <td width="68" class="TableCell">
                        	<a href= "capcontrols.jsp?rowID=<%= i %>&controlType=<%= CapControlWebAnnex.CMD_SUB %>" >
	                    		<font color="<%= CapControlWebAnnex.convertColor(subBusMdl.getCellForegroundColor( i, SubBusTableModel.CURRENT_STATE_COLUMN ) ) %>">
	                    		<%= subBusMdl.getValueAt(i, SubBusTableModel.CURRENT_STATE_COLUMN) %> 
	                    		</font></a>
                        </td>

                        <td width="70" class="TableCell"><%= subBusMdl.getValueAt(i, SubBusTableModel.TARGET_COLUMN) %></td>
                        <td width="70" class="TableCell"><%= subBusMdl.getValueAt(i, SubBusTableModel.VAR_LOAD_COLUMN) %></td>
                        <td width="68" class="TableCell"><%= subBusMdl.getValueAt(i, SubBusTableModel.TIME_STAMP_COLUMN) %></td>
                        <td width="70" class="TableCell"><%= subBusMdl.getValueAt(i, SubBusTableModel.POWER_FACTOR_COLUMN) %></td>
                        <td width="50" class="TableCell"><%= subBusMdl.getValueAt(i, SubBusTableModel.WATTS_COLUMN) %></td>
                        <td width="66" class="TableCell"><%= subBusMdl.getValueAt(i, SubBusTableModel.DAILY_OPERATIONS_COLUMN) %></td>
                        <td width="72" class="TableCell"> 
                          <select name="selectGraph" onchange="location = this.options[this.selectedIndex].value;">
                            <option value="subs.jsp">Sub kVar</option>
                            <option value="subs.jsp">Feeder kVar</option>
                            <option value="temp\<%= subBusMdl.getValueAt(i, SubBusTableModel.SUB_NAME_COLUMN) %>.html">One Line</option>
                          </select>
                          
                        </td>
                        <td width="72" class="TableCell"> 
                          <select name="select3">
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
				</tr>
</table>

          </td>
        <td width="1" bgcolor="#000000"><img src="images/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>



</body>
</html>
