<%@ include file="../operator/Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.cache.functions.DBPersistentFuncs"%>
<%@ page import="com.cannontech.database.cache.functions.DeviceFuncs"%>
<%@ page import="com.cannontech.database.data.lite.LiteDeviceMeterNumber"%>
<%@ page import="com.cannontech.database.data.device.IDeviceMeterGroup"%>
<%@ page import="com.cannontech.yc.bean.CommandDeviceBean"%>
<%@ page import="com.cannontech.database.data.device.CarrierBase"%>
<%@ page import="com.cannontech.database.data.pao.PAOGroups"%>


<jsp:useBean id="commandDeviceBean" class="com.cannontech.yc.bean.CommandDeviceBean" scope="session"/>
<%-- Grab the search criteria --%>
<jsp:setProperty name="commandDeviceBean" property="filterBy" param="FilterBy"/>
<jsp:setProperty name="commandDeviceBean" property="sortBy" param="SortBy"/>
<jsp:setProperty name="commandDeviceBean" property="sortOrder" param="SortOrder"/>
<jsp:setProperty name="commandDeviceBean" property="deviceClass" param="DeviceClass"/>
<jsp:setProperty name="commandDeviceBean" property="collGroup" param="CollGroup"/>
<%
int page_ = 1;
if( request.getParameter("page_") != null)
	page_ = Integer.valueOf(request.getParameter("page_")).intValue();
	%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="JavaScript">
function changeFilter(filterBy) {
	document.getElementById("DivDeviceClass").style.display = (filterBy == <%= CommandDeviceBean.DEVICE_CLASS_FILTER %>)? "" : "none";
	document.getElementById("DivCollectionGroup").style.display = (filterBy == <%= CommandDeviceBean.COLLECTION_GROUP_FILTER%>)? "" : "none";
}

function init() {
	var form = document.MForm;
	changeFilter(form.FilterBy.value);
}

function showAll(form) {
	form.FilterBy.value = 0;
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
          </td>
          <td width="1" bgcolor="#000000"><img src="../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
          <div align="center"> 
              <% String header = "COMMAND - DEVICE SELECTION"; %>
              <br>
<%--    TODO          <%@ include file="../operator/Hardware/include/SearchBar.jsp" %>--%>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <form name="MForm" method="post" action="">
			    <input type="hidden" name="page" value="1">
			    
                <table width="80%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td width="75%"> 
                      <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                        <tr> 
                          <td width="15%"> 
                            <div align="right">Sort by:</div>
                          </td>
                          <td width="35%"> 
                            <select name="SortBy">
                            <% for (int i = 0; i < CommandDeviceBean.sortByStrings.length; i++)
                            {%>
                              <option value="<%=i%>"  <% if (commandDeviceBean.getSortBy() == i) out.print("selected"); %>><%=CommandDeviceBean.sortByStrings[i]%></option>
                          <%}%>
                            </select>
                          </td>
                          <td width="50%"> 
                            <select name="SortOrder">
                              <option value="<%= CommandDeviceBean.SORT_ORDER_ASCENDING %>" <% if (commandDeviceBean.getSortOrder() == CommandDeviceBean.SORT_ORDER_ASCENDING) out.print("selected"); %>>Ascending</option>
                              <option value="<%= CommandDeviceBean.SORT_ORDER_DESCENDING %>" <% if (commandDeviceBean.getSortOrder() == CommandDeviceBean.SORT_ORDER_DESCENDING) out.print("selected"); %>>Descending</option>
                            </select>
                          </td>
                        </tr>
                        <tr valign="top"> 
                          <td width="15%"> 
                            <div align="right">Filter by:</div>
                          </td>
                          <td width="35%"> 
                            <select name="FilterBy" onChange="changeFilter(this.value)">
                              <option value="<%=CommandDeviceBean.NO_FILTER%>" <%=(commandDeviceBean.getFilterBy() == CommandDeviceBean.NO_FILTER) ?  "selected" : ""%>>(none)</option>
                              <option value="<%=CommandDeviceBean.DEVICE_CLASS_FILTER%>" <%=(commandDeviceBean.getFilterBy() == CommandDeviceBean.DEVICE_CLASS_FILTER) ?  "selected" : ""%>>Device Class</option>
                              <option value="<%=CommandDeviceBean.COLLECTION_GROUP_FILTER%>" <%=(commandDeviceBean.getFilterBy() == CommandDeviceBean.COLLECTION_GROUP_FILTER) ?  "selected" : ""%>>Collection Group</option>
                            </select>
                          </td>
                          <td width="50%"> 
                            <div id="DivDeviceClass" style="display:none"> 
                              <select name="DeviceClass">
                              <% for (int i = 0; i < commandDeviceBean.getValidDeviceClasses().size(); i++)
                              {
                              	int id = ((Integer)commandDeviceBean.getValidDeviceClasses().get(i)).intValue();
                              	%>
                                <option value="<%=id%>" <%=(commandDeviceBean.getDeviceClass() == id ? "selected" :"")%>><%=com.cannontech.database.data.pao.DeviceClasses.getClass(id)%></option>
                                <%}%>
                              </select>
                            </div>
                            <div id="DivCollectionGroup" style="display:none"> 
                              <select name="CollGroup">
                              <% for (int i = 0; i < commandDeviceBean.getValidCollGroups().size(); i++)
                              {
                              	String val = (String)commandDeviceBean.getValidCollGroups().get(i);
                              	%>
                                <option value="<%=val%>" <%=(commandDeviceBean.getCollGroup().equalsIgnoreCase(val) ? "selected" :"")%>><%=val%></option>
                                <%}%>
                              </select>
                            </div>
                            <div id="DivAlternateGroup" style="display:none"> 
                              <select name="FilterValue">
                                <option value="Default" selected>Default</option>
                              </select>
                            </div>
                            <div id="DivRoute" style="display:none"> 
                              <select name="FilterValue">
                                <option value="SomeRoute" selected>Some Route</option>
                              </select>
                            </div>
                          </td>
                        </tr>
                      </table>
                    </td>
                    <td width="25%"> 
                      <input type="submit" name="Submit" value="Show">
						<% if (true) { %>
                      <input type="button" name="ShowAll" value="Show All" onClick="showAll(this.form)">
						<% } %>
                    </td>
                  </tr>
                </table>
              </form>
			  <table width="80%" border="0" cellspacing="0" cellpadding="0" class="MainText">
                <tr>
                  <td align="center">Click on an item to select it for sending commands.</td>
                </tr>
              </table>
			  <br>          
              <table width="100%" border="0" cellspacing="0" cellpadding="3">
                <tr> 
                  <td align="center" class="TitleHeader"><%= header %></td>
                </tr>
              </table>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  <br>
              <form name='DeviceForm' method='post' action="<%= request.getContextPath() %>/servlet/CommanderServlet">
		        <input id="redirect" type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>">
        		<input id="referrer" type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
                <input type='hidden' name='action' value='SelectDevice'>
                
                <table width='95%' border='0' cellspacing='0' cellpadding='3'>
                <%
				  	java.util.List allDevices =	commandDeviceBean.getDeviceList();

				    int displaySize = 25;
				    int endIndex = (page_ * displaySize);
				    int startIndex = endIndex - displaySize;
				    int maxPageNo = (int)Math.ceil(allDevices.size() * 1.0 / displaySize);
				    if( endIndex > allDevices.size())
				    	endIndex = allDevices.size();
        		  %>
                  <tr> 
                    <td> 
                      <table width='100%' border='0' cellspacing='0' cellpadding='3' class='TableCell'>
                        <tr> 
                          <td><%=startIndex+1%>-<%=endIndex%>&nbsp;of&nbsp;<%=allDevices.size()%>
                            |
							<%if (page_ == 1){%><font color='#CCCCCC'>First</font><%}
							else{%><a class='Link1' href='<%=pageName%>?page_=1'>First</a><%}%>
                  			|
							<%if (page_ == 1){%><font color='#CCCCCC'>Previous</font><%}
							else{%><a class='Link1' href='<%=pageName%>?page_=<%=page_-1%>'>Previous</a><%}%>
							|
							<%if (page_ == maxPageNo){%><font color='#CCCCCC'>Next</font><%}
							else{%><a class='Link1' href='<%=pageName%>?page_=<%=page_+1%>'>Next</a><%}%>
							|
							<%if (page_ == maxPageNo){%><font color='#CCCCCC'>Last</font><%}
							else{%><a class='Link1' href='<%=pageName%>?page_=<%=maxPageNo%>'>Last</a><%}%>
					     </td>
                         <td align='right'>Page(<%=page_%>-<%=maxPageNo%>): 
                            <input type='text' id='GoPage' style='border:1px solid #666699; font:11px' size='1' value='<%=page_%>'>
                            <input type='button' style='font:11px; margin-bottom:-1px' value='Go' onclick='location.href="SelectDevice.jsp?page_=" + document.getElementById("GoPage").value;'>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                  <tr> 
                    <td>
                    	<%=commandDeviceBean.getDeviceTableHTML(startIndex, endIndex)%>
					</td>
                  </tr>
                  <tr> 
                    <td> 
                      <table width='100%' border='0' cellspacing='0' cellpadding='3' class='TableCell'>
                        <tr> 
                          <td><%=startIndex+1%>-<%=endIndex%>&nbsp;of&nbsp;<%=allDevices.size()%>
                            |
							<%if (page_ == 1){%><font color='#CCCCCC'>First</font><%}
							else{%><a class='Link1' href='<%=pageName%>?page_=1'>First</a><%}%>
                  			|
							<%if (page_ == 1){%><font color='#CCCCCC'>Previous</font><%}
							else{%><a class='Link1' href='<%=pageName%>?page_=<%=page_-1%>'>Previous</a><%}%>
							|
							<%if (page_ == maxPageNo){%><font color='#CCCCCC'>Next</font><%}
							else{%><a class='Link1' href='<%=pageName%>?page_=<%=page_+1%>'>Next</a><%}%>
							|
							<%if (page_ == maxPageNo){%><font color='#CCCCCC'>Last</font><%}
							else{%><a class='Link1' href='<%=pageName%>?page_=<%=maxPageNo%>'>Last</a><%}%>
					     </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                <br>
                <table width='200' border='0' cellspacing='0' cellpadding='3'>
                  <tr> 
                    <td align='right'> 
                      <input type='submit' name='Submit' value='Select'>
                    </td>
                    <td> 
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
