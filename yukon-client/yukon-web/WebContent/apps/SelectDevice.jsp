<%@ include file="../operator/Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.cache.functions.DBPersistentFuncs"%>
<%@ page import="com.cannontech.database.cache.functions.DeviceFuncs"%>
<%@ page import="com.cannontech.database.data.lite.LiteDeviceMeterNumber"%>
<%@ page import="com.cannontech.database.data.device.IDeviceMeterGroup"%>
<%@ page import="com.cannontech.yc.bean.CommandDeviceBean"%>
<%@ page import="com.cannontech.database.data.device.CarrierBase"%>
<%@ page import="com.cannontech.database.data.pao.PAOGroups"%>
<%@ page import="com.cannontech.database.data.pao.DeviceClasses"%>
<%@ page import="com.cannontech.roles.application.CommanderRole"%>

<jsp:useBean id="commandDeviceBean" class="com.cannontech.yc.bean.CommandDeviceBean" scope="session"/>
<%-- Grab the search criteria --%>
<jsp:setProperty name="commandDeviceBean" property="page" param="page_"/>
<jsp:setProperty name="commandDeviceBean" property="pageSize" param="pageSize"/>
<jsp:setProperty name="commandDeviceBean" property="filterBy" param="FilterBy"/>
<jsp:setProperty name="commandDeviceBean" property="filterValue" param="FilterValue"/>
<jsp:setProperty name="commandDeviceBean" property="orderBy" param="OrderBy"/>
<jsp:setProperty name="commandDeviceBean" property="orderDir" param="OrderDir"/>
<jsp:setProperty name="commandDeviceBean" property="sortBy" param="SortBy"/>
<%--<jsp:setProperty name="commandDeviceBean" property="deviceClass" param="DeviceClass"/>--%>
<%--<jsp:setProperty name="commandDeviceBean" property="collGroup" param="CollGroup"/>--%>
<jsp:setProperty name="commandDeviceBean" property="searchBy" param="SearchBy"/>
<jsp:setProperty name="commandDeviceBean" property="userID" value="<%=lYukonUser.getUserID()%>"/>
<jsp:setProperty name="commandDeviceBean" property="searchValue" param="SearchValue"/>
<jsp:setProperty name="commandDeviceBean" property="clear" param="Clear"/>

<%if( request.getParameter("SearchValue") != null && request.getParameter("SearchValue").length() <= 0)
	commandDeviceBean.setSearchValue("");

int currentSortBy = commandDeviceBean.getSortBy();
if( request.getParameter("SortBy") != null)
	currentSortBy = Integer.valueOf(request.getParameter("SortBy")).intValue();
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function changeFilter(filterBy) {
	document.getElementById("DivRoute").style.display = (filterBy == <%= CommandDeviceBean.ROUTE_FILTER %>)? "" : "none";
	document.getElementById("DivCommChannel").style.display = (filterBy == <%= CommandDeviceBean.COMM_CHANNEL_FILTER %>)? "" : "none";
	document.getElementById("DivCollectionGroup").style.display = (filterBy == <%= CommandDeviceBean.COLLECTION_GROUP_FILTER%>)? "" : "none";
}

function init() {
	var form = document.MForm;
	changeFilter(form.FilterBy.value);
}

function showAll(form) {
	form.Clear.value = "true";
	form.submit();
}

function setFilterValue(form){
	if(document.getElementById("DivRoute").style.display == "")
		form.FilterValue.value = form.RouteFilterValue.value;
	else if(document.getElementById("DivCommChannel").style.display == "")
		form.FilterValue.value = form.CommChannelFilterValue.value;
	else if( document.getElementById("DivCollectionGroup").style.display == "")
		form.FilterValue.value = form.CollGroupFilterValue.value;
	form.submit();
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="init()">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
	<td> 
	  <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
		<tr> 
		  <td width="102" height="102" background="../WebConfig/yukon/MeterImage.jpg">&nbsp;</td>
		  <td valign="bottom" height="102"> 
			<table width="657" cellspacing="0"  cellpadding="0" border="0">
			  <tr> 
               	<td colspan="4" height="74" background="../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="yukon/DemoHeader.gif"/>">&nbsp;</td>
			  </tr>
			  <tr>
				<td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Commander&nbsp;&nbsp;</td>
				<td width="253" valign="middle">&nbsp;</td>
				<td width="58" valign="middle">
                  <div align="center"><span class="MainText"><a href="../operator/Operations.jsp" class="Link3">Home</a></span></div>
				</td>
				<td width="57" valign="middle"> 
				  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
				</td>
			  </tr>
			</table>
		  </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
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
          <td  valign="top" width="101"> 
            <% String pageName = "SelectDevice.jsp"; %>
			<table width="101" border="0" cellspacing="0" cellpadding="5">
			  <tr> 
			    <td> 
			      <div align="left">
   			        <span class="NavHeader">Devices</span>
			        <table width="100%" border="0" cellspacing="0" cellpadding="0">
			          <tr>
					  	<% if( currentSortBy == DeviceClasses.CARRIER){%>
			            <td width="10"><img src='../WebConfig/<%=AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1;font-weight:bold"><span class='Nav' style="font-weight:bold; font-size: 10px">MCT</span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='SelectDevice.jsp?SortBy=<%=DeviceClasses.CARRIER%>' class='Link2'><span class='NavText' style="font-weight:bold; font-size: 10px">MCT</span></a></td>
						<%}%>						
			          </tr>
					  <tr><td height="10"></td></tr>
			          <tr>
					  	<% if( currentSortBy == DeviceClasses.IED){%>
			            <td width="10"><img src='../WebConfig/<%=AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav' style="font-weight:bold; font-size: 10px"><%=DeviceClasses.STRING_CLASS_IED%></span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='SelectDevice.jsp?SortBy=<%=DeviceClasses.IED%>' class='Link2'><span class='NavText' style="font-weight:bold; font-size: 10px"><%=DeviceClasses.STRING_CLASS_IED%></span></a></td>
						<%}%>						
			          </tr>			          
					  <tr><td height="10"></td></tr>
			          <tr>
					  	<% if( currentSortBy == DeviceClasses.RTU){%>
			            <td width="10"><img src='../WebConfig/<%=AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav' style="font-weight:bold; font-size: 10px"><%=DeviceClasses.STRING_CLASS_RTU%></span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='SelectDevice.jsp?SortBy=<%=DeviceClasses.RTU%>' class='Link2'><span class='NavText' style="font-weight:bold; font-size: 10px"><%=DeviceClasses.STRING_CLASS_RTU%></span></a></td>
						<%}%>						
			          </tr>			          
					  <tr><td height="10"></td></tr>
			          <tr>
					  	<% if( currentSortBy == DeviceClasses.TRANSMITTER){%>
			            <td width="10"><img src='../WebConfig/<%=AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav' style="font-weight:bold; font-size: 10px"><%=DeviceClasses.STRING_CLASS_TRANSMITTER%></span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='SelectDevice.jsp?SortBy=<%=DeviceClasses.TRANSMITTER%>' class='Link2'><span class='NavText' style="font-weight:bold; font-size: 10px"><%=DeviceClasses.STRING_CLASS_TRANSMITTER%></span></a></td>
						<%}%>						
			          </tr>
					  <tr><td height="20"></td></tr>
					</table>
					<span class="NavHeader">Load Management</span>
			        <table width="100%" border="0" cellspacing="0" cellpadding="0">
			          <tr>
					  	<% if( currentSortBy == DeviceClasses.GROUP){%>
			            <td width="10"><img src='../WebConfig/<%=AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav' style="font-weight:bold; font-size: 10px"><%=DeviceClasses.STRING_CLASS_GROUP%></span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='SelectDevice.jsp?SortBy=<%=DeviceClasses.GROUP%>' class='Link2'><span class='NavText' style="font-weight:bold; font-size: 10px"><%=DeviceClasses.STRING_CLASS_GROUP%></span></a></td>
						<%}%>						
			          </tr>
					  <tr><td height="10"></td></tr>
					  <cti:checkProperty propertyid="<%= CommanderRole.EXPRESSCOM_SERIAL_MODEL %>">
			          <tr>
					  	<% if( currentSortBy == CommandDeviceBean.EXPRESSCOM_SORT_BY){%>
			            <td width="10"><img src='../WebConfig/<%=AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav' style="font-weight:bold; font-size: 10px">Expresscom</span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&xcom' class='Link2'><span class='NavText' style="font-weight:bold; font-size: 10px">Expresscom</span></a></td>
						<%}%>						
			          </tr>			          
					  <tr><td height="10"></td></tr>
					  </cti:checkProperty>
					  <cti:checkProperty propertyid="<%= CommanderRole.VERSACOM_SERIAL_MODEL %>">
			          <tr>
					  	<% if( currentSortBy == CommandDeviceBean.VERSACOM_SORT_BY){%>
			            <td width="10"><img src='../WebConfig/<%=AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav' style="font-weight:bold; font-size: 10px">Versacom</span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&vcom' class='Link2'><span class='NavText' style="font-weight:bold; font-size: 10px">Versacom</span></a></td>
						<%}%>						
			          </tr>	
					  <tr><td height="10"></td></tr>
					  </cti:checkProperty>
					  <cti:checkProperty propertyid="<%= CommanderRole.DCU_SA205_SERIAL_MODEL %>">
			          <tr>
					  	<% if( currentSortBy == CommandDeviceBean.DCU_SA205_SERIAL_SORT_BY){%>
			            <td width="10"><img src='../WebConfig/<%=AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav' style="font-weight:bold; font-size: 10px">DCU-205 Serial</span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&sa205' class='Link2'><span class='NavText' style="font-weight:bold; font-size: 10px">DCU-205 Serial</span></a></td>
						<%}%>						
			          </tr>			          
					  <tr><td height="10"></td></tr>
					  </cti:checkProperty>
					  <cti:checkProperty propertyid="<%= CommanderRole.DCU_SA305_SERIAL_MODEL %>">
			          <tr>
					  	<% if( currentSortBy == CommandDeviceBean.DCU_SA305_SERIAL_SORT_BY){%>
			            <td width="10"><img src='../WebConfig/<%=AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav' style="font-weight:bold; font-size: 10px">DCU-305 Serial</span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='CommandDevice.jsp?deviceID=<%=PAOGroups.INVALID%>&manual&sa305' class='Link2'><span class='NavText' style="font-weight:bold; font-size: 10px">DCU-305 Serial</span></a></td>
						<%}%>						
			          </tr>			          
					  <tr><td height="10"></td></tr>
					  </cti:checkProperty>
					</table>
					<span class="NavHeader">Cap Control</span>
			        <table width="100%" border="0" cellspacing="0" cellpadding="0">
			          <tr>
					  	<% if( currentSortBy == PAOGroups.CLASS_CAPCONTROL){%>
			            <td width="10"><img src='../WebConfig/<%=AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED)%>' width='9' height='9'></td>
			            <td style="padding:1"><span class='Nav' style="font-weight:bold; font-size: 10px"><%=PAOGroups.STRING_CAT_CAPCONTROL%></span></td>
						<%} else {%>
			            <td width="10"></td>
			            <td style="padding:1"><a href='SelectDevice.jsp?SortBy=<%=PAOGroups.CLASS_CAPCONTROL%>' class='Link2'><span class='NavText' style="font-weight:bold; font-size: 10px"><%=PAOGroups.STRING_CAT_CAPCONTROL%></span></a></td>
						<%}%>						
			          </tr>
					  <tr><td height="10"></td></tr>
					</table>
			      </div>
			    </td>
			  </tr>
			</table>
          </td>            
          <td width="1" bgcolor="#000000"><img src="../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
          <div align="center"> 
              <% String header = "COMMAND - Device Selection"; %>
              <table width="100%" border="0" cellpadding = "5">
                <tr> 
                  <td width="50%" valign = "top" align = "left">&nbsp;</td>
                  <td align = "right" width="50%">
				  <form name="SearchForm" method="POST" action="">
				  <span class="TitleHeader">
					<select name="SearchBy">
					  <%String[] searchStrings = commandDeviceBean.getSearchByStrings();
					   for (int i = 0; i < searchStrings.length; i++){%>
					  <option value="<%=i%>"  <% if (commandDeviceBean.getSearchBy() == i) out.print("selected"); %>><%=searchStrings[i]%></option>
					  <%}%>
					</select>
					<input type="text" name="SearchValue" size = "14" value="<%=commandDeviceBean.getSearchValue()%>">
					<input type="submit" name="Submit" value="Search" >
					</span>
            		</form>					
				  </td>
                </tr>
              </table>
              <table width="100%" border="0" cellspacing="0" cellpadding="3">
                <tr> 
                  <td align="center" class="TitleHeader"><%= header %></td>
                </tr>
              </table>
              <form name="MForm" method="post" action="">
			    <input type="hidden" name="page" value="1">
			    <input type="hidden" name="Clear" value="false">
   			    <input type="hidden" name="FilterValue" value="">

                <table width="80%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td width="75%"> 
                      <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                        <tr> 
                          <td width="15%"> 
                            <div align="right">Order by:</div>
                          </td>
                          <td width="35%"> 
                            <select name="OrderBy">
                            <% 
							String [] columns = commandDeviceBean.getOrderByStrings();
                            for (int i = 0; i < columns.length; i++)
                            {%>
                              <option value="<%=i%>"  <% if (commandDeviceBean.getOrderBy() == i) out.print("selected"); %>><%=columns[i]%></option>
                          <%}%>
                            </select>
                          </td>
                          <td width="50%"> 
                            <select name="OrderDir">
                              <option value="<%= CommandDeviceBean.ORDER_DIR_ASCENDING %>" <% if (commandDeviceBean.getOrderDir() == CommandDeviceBean.ORDER_DIR_ASCENDING) out.print("selected"); %>>Ascending</option>
                              <option value="<%= CommandDeviceBean.ORDER_DIR_DESCENDING %>" <% if (commandDeviceBean.getOrderDir() == CommandDeviceBean.ORDER_DIR_DESCENDING) out.print("selected"); %>>Descending</option>
                            </select>
                          </td>
                        </tr>
                        <tr valign="top"> 
                          <td width="15%">
						    <% if (currentSortBy == DeviceClasses.IED || 
							    currentSortBy == DeviceClasses.RTU ||
							    currentSortBy == DeviceClasses.CARRIER ||
							    currentSortBy == DeviceClasses.TRANSMITTER){%>
                            <div align="right">Filter by:</div>
                            <%}%>
                          </td>
                          <td width="35%"> 
						    						    <% if (currentSortBy == DeviceClasses.CARRIER){%>
                            <select name="FilterBy" onChange="changeFilter(this.value)">
	                          <option value="<%=CommandDeviceBean.NO_FILTER%>" <%=(commandDeviceBean.getFilterBy() == CommandDeviceBean.NO_FILTER) ?  "selected" : ""%>>(none)</option>
                              <option value="<%=CommandDeviceBean.ROUTE_FILTER%>" <%=(commandDeviceBean.getFilterBy() == CommandDeviceBean.ROUTE_FILTER) ?  "selected" : ""%>>Route</option>
                              <option value="<%=CommandDeviceBean.COLLECTION_GROUP_FILTER%>" <%=(commandDeviceBean.getFilterBy() == CommandDeviceBean.COLLECTION_GROUP_FILTER) ?  "selected" : ""%>>Collection Group</option>
                            </select>
						    <%} else if (currentSortBy == DeviceClasses.IED || currentSortBy == DeviceClasses.RTU){%>
                            <select name="FilterBy" onChange="changeFilter(this.value)">
	                          <option value="<%=CommandDeviceBean.NO_FILTER%>" <%=(commandDeviceBean.getFilterBy() == CommandDeviceBean.NO_FILTER) ?  "selected" : ""%>>(none)</option>
                              <option value="<%=CommandDeviceBean.COMM_CHANNEL_FILTER%>" <%=(commandDeviceBean.getFilterBy() == CommandDeviceBean.COMM_CHANNEL_FILTER) ?  "selected" : ""%>>Comm Channel</option>
                              <option value="<%=CommandDeviceBean.COLLECTION_GROUP_FILTER%>" <%=(commandDeviceBean.getFilterBy() == CommandDeviceBean.COLLECTION_GROUP_FILTER) ?  "selected" : ""%>>Collection Group</option>
                            </select>
						    <%} else if (currentSortBy == DeviceClasses.TRANSMITTER){%>
                            <select name="FilterBy" onChange="changeFilter(this.value)">
	                          <option value="<%=CommandDeviceBean.NO_FILTER%>" <%=(commandDeviceBean.getFilterBy() == CommandDeviceBean.NO_FILTER) ?  "selected" : ""%>>(none)</option>
                              <option value="<%=CommandDeviceBean.COMM_CHANNEL_FILTER%>" <%=(commandDeviceBean.getFilterBy() == CommandDeviceBean.COMM_CHANNEL_FILTER) ?  "selected" : ""%>>Comm Channel</option>
                            </select>                            
	                         <%} else {%>
			    			<input type="hidden" name="FilterBy" value="<%=CommandDeviceBean.NO_FILTER%>">
	                         <%}%>
                          </td>
                          <td width="50%"> 
                            <div id="DivRoute" style="display:none"> 
                              <select name="RouteFilterValue">
                              <% for (int i = 0; i < commandDeviceBean.getValidRoutes().size(); i++)
                              {
                              	int id = ((Integer)commandDeviceBean.getValidRoutes().get(i)).intValue();
                              	%>
                                 <option value="<%=id%>" <%=(commandDeviceBean.getFilterValue().length() > 0 && commandDeviceBean.getFilterValue().equals(String.valueOf( id)) ? "selected" :"")%>><%=com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName(id)%></option>
                            <%}%>
                              </select>
                            </div>
                            <div id="DivCommChannel" style="display:none"> 
                              <select name="CommChannelFilterValue">
                              <% for (int i = 0; i < commandDeviceBean.getValidCommChannels().size(); i++)
                              {
                              	int id = ((Integer)commandDeviceBean.getValidCommChannels().get(i)).intValue();
                              	%>
                                 <option value="<%=id%>" <%=(commandDeviceBean.getFilterValue().length() > 0 && commandDeviceBean.getFilterValue().equals(String.valueOf( id)) ? "selected" :"")%>><%=com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName(id)%></option>
                            <%}%>
                              </select>
                            </div>
                            <div id="DivCollectionGroup" style="display:none"> 
                              <select name="CollGroupFilterValue">
                              <% for (int i = 0; i < commandDeviceBean.getValidCollGroups().size(); i++)
                              {
                              	String val = (String)commandDeviceBean.getValidCollGroups().get(i);
                              	%>
                                <option value="<%=val%>" <%=(commandDeviceBean.getFilterValue().equalsIgnoreCase(val) ? "selected" :"")%>><%=val%></option>
                                <%}%>
                              </select>
                            </div>
                          </td>
                        </tr>
                      </table>
                    </td>
                    <td width="25%"> 
                      <input type="submit" name="Submit" value="Show" onClick="setFilterValue(this.form);">
                      <% if (true) { %>
                      <input type="button" name="ShowAll" value="Show All" onClick="showAll(this.form)">
                      <% } %>
                    </td>
                  </tr>
                </table>
              </form>
<!--			  <table width="80%" border="0" cellspacing="0" cellpadding="0" class="MainText">
                <tr>
                  <td align="center">Click on an item to select it for sending commands.</td>
                </tr>
              </table>
-->
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  <br>
              <form name='DeviceForm' method='post' action="<%= request.getContextPath() %>/servlet/CommanderServlet">
		        <input id="redirect" type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>">
        		<input id="referrer" type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
                <input type='hidden' name='action' value='SelectDevice'>
                
                <%=commandDeviceBean.getDeviceTableHTML()%>
				
                <br>
                <table width='200' border='0' cellspacing='0' cellpadding='3'>
                  <tr> 
                    <td align='center'>
                      <input type='button' name='Cancel' value='Cancel' onclick='location.href="../operator/Operations.jsp"'>
                    </td>
                  </tr>
                </table>
              <p>&nbsp;</p>
              </form>
            </div>
          </td>
          <td width="1" bgcolor="#000000"><img src="../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
		</tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
