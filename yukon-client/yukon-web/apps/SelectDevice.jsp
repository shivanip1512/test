<%@ include file="../operator/Consumer/include/StarsHeader.jsp" %>
<%
String sortBy = "";
String sortOrder = "asc";
int filterBy = 0;
int member = 0;	//user.getEnergyCompanyID()
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
	document.getElementById("DivDeviceType").style.display = (filterBy == <%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_TYPE %>)? "" : "none";
	document.getElementById("DivServiceCompany").style.display = (filterBy == <%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_SRV_COMPANY %>)? "" : "none";
	document.getElementById("DivLocation").style.display = (filterBy == <%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_LOCATION %>)? "" : "none";
	document.getElementById("DivAddressingGroup").style.display = (filterBy == <%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_CONFIG %>)? "" : "none";
	document.getElementById("DivDeviceStatus").style.display = (filterBy == <%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_STATUS %>)? "" : "none";
	document.getElementById("DivEnergyCompany").style.display = (filterBy == <%= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_ENERGY_COMPANY %>)? "" : "none";
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

<body class="Background" leftmargin="0" topmargin="0">
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
				<td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Commercial Metering&nbsp;&nbsp;</td>
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
                
                <table width='80%' border='0' cellspacing='0' cellpadding='3'>
                <%
				  DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
				  synchronized( cache )
				  { 
				  	java.util.List allDevices = cache.getAllDevices();
				  	java.util.Collections.sort( allDevices, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );

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
                      <table width='100%' border='1' cellspacing='0' cellpadding='3'>
                        <tr> 
                          <td class='HeaderCell' width='10%'>&nbsp;</td>
                          <td class='HeaderCell' width='30%'>Device Name</td>
                          <td class='HeaderCell' width='20%'>Device Type</td>
						  <td class='HeaderCell' width='20%'>Address</td>
                          <td class='HeaderCell' width='20%'>Route</td>						  
                        </tr>
						<% 
						  boolean first = true;
						  for (int i = startIndex; i < endIndex; i++)
						  {
							LiteYukonPAObject lPao = (LiteYukonPAObject) allDevices.get(i);%>
                        <tr> 
                          <td class='TableCell' width='10%'>
                            <input type='radio' name='deviceID' value='<%=lPao.getYukonID()%>' <%if (first){first=false;%>checked<%}%>>
                          </td>
                          <td class='TableCell' width='30%'><%= lPao.getPaoName()%></td>
                          <td class='TableCell' width='20%'><%= com.cannontech.database.data.pao.PAOGroups.getPAOTypeString(lPao.getType())%></td>
                          <td class='TableCell' width='20%'><%if(lPao.getAddress()!=com.cannontech.database.data.pao.PAOGroups.INVALID)%><%=lPao.getAddress()%><%else{%>---<%}%></td>
                     	  <td class='TableCell' width='20%'><%if(lPao.getRouteID()!=com.cannontech.database.data.pao.PAOGroups.INVALID)%><%=com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName(lPao.getRouteID())%><%else{%>---<%}%></td>
                        </tr>
						<%}%>
                      </table>
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
                <%}%>
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
