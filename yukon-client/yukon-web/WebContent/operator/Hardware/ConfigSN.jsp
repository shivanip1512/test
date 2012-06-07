<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.common.util.Pair" %>
<%@ page import="com.cannontech.stars.database.data.lite.LiteStarsLMHardware" %>
<%@ page import="com.cannontech.core.dao.NotFoundException" %>
<jsp:useBean id="configBean" class="com.cannontech.stars.web.bean.ConfigBean" scope="page"/>
 
<%
	String trackHwAddr = liteEC.getEnergyCompanySetting(EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING);
	boolean useHardwareAddressing = Boolean.valueOf(trackHwAddr).booleanValue();
	
	int hwConfigType = 0;
	StarsLMConfiguration configuration = null;
	
	ArrayList invToConfig = (ArrayList) session.getAttribute(InventoryManagerUtil.SN_RANGE_TO_CONFIG);
	if (invToConfig == null) {
		invToConfig = new ArrayList();
		session.setAttribute(InventoryManagerUtil.SN_RANGE_TO_CONFIG, invToConfig);
	}
	else if (invToConfig.size() > 0) {
		int devTypeID = 0;
		if (invToConfig.get(0) instanceof Long[])
			devTypeID = ((Long[])invToConfig.get(0))[0].intValue();
		else if (invToConfig.get(0) instanceof Pair)
			devTypeID = ((LiteStarsLMHardware)((Pair)invToConfig.get(0)).getFirst()).getLmHardwareTypeID();
		else
			devTypeID = ((LiteStarsLMHardware)invToConfig.get(0)).getLmHardwareTypeID();
		
		hwConfigType = InventoryUtils.getHardwareConfigType(devTypeID);
	}
	
	if (request.getParameter("AddRange") != null) {
		ServletUtils.saveRequest(request, session, new String[] {"From", "To", "DeviceType"});
		
		Integer devTypeID = Integer.valueOf(request.getParameter("DeviceType"));
		int newHwConfigType = InventoryUtils.getHardwareConfigType(devTypeID.intValue());
		
		if (hwConfigType > 0 && newHwConfigType != hwConfigType) {
			errorMsg = "The devices you tried to add don't have the same addressing scheme as those already added.";
		}
		else {
			try {
			    Long snFrom = Long.valueOf(request.getParameter("From"));
			    Long snTo = Long.valueOf(request.getParameter("To"));
				if (snFrom > snTo) {
				    errorMsg = "The Serial range 'from' value cannot be greater than the 'to' value";   
				} else {
					boolean foundRange = false;
					for (int i = 0; i < invToConfig.size(); i++) {
						if (invToConfig.get(i) instanceof Long[]) {
						    Long[] snRange = (Long[]) invToConfig.get(i);
							if (snRange[0].intValue() == devTypeID.intValue() && snRange[1].longValue() == snFrom.longValue() && snRange[2].longValue() == snTo.longValue()) {
								foundRange = true;
								break;
							}
						}
					}
					
					if (!foundRange) {
						invToConfig.add(new Long[] {devTypeID.longValue(), snFrom, snTo});
						hwConfigType = newHwConfigType;
					}
				}
			}
			catch (NumberFormatException e) {
				errorMsg = "Invalid number format in the Serial range";
			}
		}
	}
	else if (request.getParameter("Add") != null) {
		Object invObj = session.getAttribute(InventoryManagerUtil.INVENTORY_SELECTED);
		if (invObj != null) {
			session.removeAttribute(InventoryManagerUtil.INVENTORY_SELECTED);
			
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware)
					((invObj instanceof Pair)? ((Pair)invObj).getFirst() : invObj);
			int newHwConfigType = InventoryUtils.getHardwareConfigType(liteHw.getLmHardwareTypeID());
			
			if (hwConfigType > 0 && newHwConfigType != hwConfigType) {
				errorMsg = "The device you tried to add doesn't have the same addressing scheme as those already added.";
			}
			else {
				boolean foundHardware = false;
				for (int i = 0; i < invToConfig.size(); i++) {
					Object obj = invToConfig.get(i);
					if (obj instanceof Pair) obj = ((Pair)obj).getFirst();
					if (obj.equals(liteHw)) {
						foundHardware = true;
						break;
					}
				}
				
				if (!foundHardware) {
					invToConfig.add(invObj);
					hwConfigType = newHwConfigType;
				}
			}
		}
	}
	else if (request.getParameter("Remove") != null) {
		try {
			invToConfig.remove( Integer.parseInt(request.getParameter("Remove")) );
		}
		catch (IndexOutOfBoundsException e) {}
	}
	else if (request.getParameter("RemoveAll") != null) {
		invToConfig.clear();
	}
	
	Properties savedReq = null;
	if (errorMsg != null)
		savedReq = (Properties) session.getAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST);
	else
		session.removeAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST);
	if (savedReq == null) savedReq = new Properties();
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<script language="Javascript">
function addConfigRange(form) {
	if (form.From.value == "" || form.To.value == "") {
		alert("The Serial range 'From' and 'To' fields cannot be empty");
		return;
	}
	form.attributes["action"].value = "ConfigSN.jsp?AddRange";
	form.submit();
}

function removeConfig(form, idx) {
	if (!confirm("Are you sure you want to remove this configuration entry?"))
		return;
	form.attributes["action"].value = "ConfigSN.jsp?Remove=" + idx;
	form.submit();
}

function removeAllConfig(form) {
	if (!confirm("Are you sure you want to remove all configuration entries?"))
		return;
	form.attributes["action"].value = "ConfigSN.jsp?RemoveAll";
	form.submit();
}
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jspf" %>
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
            <% String pageName = "ConfigSN.jsp"; %>
            <%@ include file="include/Nav.jspf" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center"> 
              <% String header = "CONFIGURE SERIAL NUMBER RANGE"; %>
              <%@ include file="include/SearchBar.jspf" %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
			  
              <table width="80%" border="0" cellspacing="0" cellpadding="0" class="MainText">
                <tr> 
                  <td align="center">Add serial numbers to be configured by range 
                    (Add Range),<br>
                  </td>
                </tr>
              </table>
              <form name="form1" method="post" action="<%= request.getContextPath() %>/servlet/InventoryManager">
			    <input type="hidden" name="action" value="ConfigSNRange">
				<input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>">
				<input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
				<input type="hidden" name="<%= ServletUtils.CONFIRM_ON_MESSAGE_PAGE %>">
                <table width="64%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                  <tr>
                    <td align="center"> 
                      <table width="100%" border="1" cellspacing="0" cellpadding="3" bgcolor="CCCCCC">
                        <tr> 
                          <td align="center"> 
                            <table width="100%" border="0" class="TableCell2">
                              <tr> 
                                <td width="25%"> 
                                  <div align="right">Range:</div>
                                </td>
                                <td width="75%"> 
                                  <input type="text" name="From" size="19" value="<%= StarsUtils.forceNotNull(savedReq.getProperty("From")) %>">
                                  &nbsp;to&nbsp; 
                                  <input type="text" name="To" size="19" value="<%= StarsUtils.forceNotNull(savedReq.getProperty("To")) %>">
                                </td>
                              </tr>
                              <tr> 
                                <td width="25%"> 
                                  <div align="right">Device Type:</div>
                                </td>
                                <td width="75%"> 
                                  <select name="DeviceType">
<%
	int savedDeviceType = 0;
	if (savedReq.getProperty("DeviceType") != null)
		savedDeviceType = Integer.parseInt(savedReq.getProperty("DeviceType"));
	
	StarsCustSelectionList deviceTypeList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
	for (int i = 0; i < deviceTypeList.getStarsSelectionListEntryCount(); i++) {
		StarsSelectionListEntry entry = deviceTypeList.getStarsSelectionListEntry(i);
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT) continue;
		String selected = (entry.getEntryID() == savedDeviceType)? "selected" : "";
%>
                                    <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getContent() %></option>
<%
	}
%>
                                  </select>
                                </td>
                              </tr>
                            </table>
                            <input type="button" name="AddConfigRange" value="Add Range" onClick="addConfigRange(this.form)">
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                  
                </table>
                <p> 
                <table width="64%" border="1" cellspacing="0" cellpadding="5">
                  <tr> 
                    <td class="MainText" bgcolor="#CCCCCC"><b>Serial Numbers to 
                      be Configured</b></td>
                  </tr>
                  <tr> 
                    <td align="center"> 
                      <table width="400" border="0" cellspacing="0" cellpadding="0">
                        <tr> 
                          <td class="SubtitleHeader" width="128">Device Type</td>
                          <td class="SubtitleHeader" width="158">Serial # (Range)</td>
                          <td class="SubtitleHeader" width="100">&nbsp;</td>
                        </tr>
                        <tr> 
                          <td class="TableCell" colspan="3"> 
                            <hr>
                          </td>
                        </tr>
<%
	for (int i = 0; i < invToConfig.size(); i++) {
		int devTypeID = 0;
		String serialNo = null;
		if (invToConfig.get(i) instanceof Long[]) {
		    Long[] snRange = (Long[]) invToConfig.get(i);
			devTypeID = snRange[0].intValue();
			serialNo = snRange[1].toString() + " to " + snRange[2].toString();
		}
		else {
			Object invObj = invToConfig.get(i);
			if (invObj instanceof Pair) invObj = ((Pair)invObj).getFirst();
			devTypeID = ((LiteStarsLMHardware)invObj).getLmHardwareTypeID();
			serialNo = ((LiteStarsLMHardware)invObj).getManufacturerSerialNumber();
		}
		String deviceType = DaoFactory.getYukonListDao().getYukonListEntry(devTypeID).getEntryText();
%>
                        <tr> 
                          <td class="TableCell" width="128"><%= deviceType %></td>
                          <td class="TableCell" width="158"><%= serialNo %></td>
                          <td class="TableCell" width="100"> 
                            <input type="button" name="Submit3" value="Remove" onClick="removeConfig(this.form, <%= i %>)">
                          </td>
                        </tr>
<%
	}
%>
                      </table>
                      <br>
<%
	if (invToConfig.size() > 0) {
%>
                      <input type="button" name="RemoveAllConfig" value="Remove All" onClick="removeAllConfig(this.form)">
<%
	}
%>
                    </td>
                  </tr>
                </table>
<%
	if (invToConfig.size() > 0) {
%>
                <p> 
                <table width="64%" border="1" cellspacing="0" cellpadding="5">
                  <tr> 
                    <td class="MainText" bgcolor="#CCCCCC"><b>Hardware Configuration</b></td>
                  </tr>
                  <tr> 
                    <td align="center" class="TableCell"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                        <tr> 
                          <td> 
                            <input type="radio" name="UseRoute" value="false" checked onclick="this.form.Route.disabled = true">
                            Use routes assigned to the switches.</td>
                        </tr>
                        <tr> 
                          <td> 
                            <input type="radio" name="UseRoute" value="true" onclick="this.form.Route.disabled = false">
                            Specify a route: 
                            <select name="Route" disabled>
<%
	String dftRoute;
    try
    {
        dftRoute = DaoFactory.getPaoDao().getYukonPAOName(liteEC.getDefaultRouteId());
        dftRoute = "Default - " + dftRoute;
    }
    catch(NotFoundException e)
    {
        dftRoute = "Default - (None)";
    }
%>
                              <option value="0"><%= dftRoute %></option>
<%
    List<LiteYukonPAObject> routes = liteEC.getAllRoutes();
    for (LiteYukonPAObject route : routes) {
%>
                              <option value="<%= route.getYukonID() %>"><%= route.getPaoName() %></option>
<%
    }
%>
                            </select>
                          </td>
                        </tr>
                      </table>
                      <hr>
                      <table width="300" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                        <tr> 
                          <td> 
                            <input type="radio" name="UseConfig" value="false" checked onClick="document.getElementById('Configuration').style.display = 'none'">
                            Use configuration stored in the switches.</td>
                        </tr>
                        <tr> 
                          <td> 
                            <input type="radio" name="UseConfig" value="true" onClick="document.getElementById('Configuration').style.display = ''">
                            Specify a new configuration:</td>
                        </tr>
                      </table>
                      <div id="Configuration" style="display:none"> 
<%
		if (useHardwareAddressing) {
%>
                        <input type="hidden" name="UseHardwareAddressing" value="true">
                        <table border="0" cellspacing="0" cellpadding="10">
                          <tr> 
                            <td align="center" valign="top"> 
                              <%@ include file="../../include/hwconfig_addressing.jspf" %>
                            </td>
                            <td align="center" valign="top"> 
                              <%@ include file="../../include/hwconfig_relays.jspf" %>
                            </td>
                          </tr>
                        </table>
<%
		}
		else if(configBean.isWriteToFileAllowed()) 
		{%>
			            Group: 
                        <select id="Group" name="Group" onchange="setContentChanged(true)">
		                  <option value="0" selected> <c:out value="(none)"/> </option>
	                      <c:forEach var="group" items="${configBean.allStaticGroups}">
								<c:if test="${group.loadGroupID == groupID}">
									<option value='<c:out value="${group.loadGroupID}"/>' selected> <c:out value="${group.loadGroupName}"/> </option>
								</c:if>	 
								<c:if test="${group.loadGroupID != groupID}">
									<option value='<c:out value="${group.loadGroupID}"/>'> <c:out value="${group.loadGroupName}"/> </option>
								</c:if>	
					  	  </c:forEach>
                       </select>
<%
		}
		else {
			TreeMap map = new TreeMap();
			for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
				for (int j = 0; j < categories.getStarsApplianceCategory(i).getStarsEnrLMProgramCount(); j++) {
					StarsEnrLMProgram program = categories.getStarsApplianceCategory(i).getStarsEnrLMProgram(j);
					for (int k = 0; k < program.getAddressingGroupCount(); k++) {
						AddressingGroup group = program.getAddressingGroup(k);
						if (group.getEntryID() > 0) map.put(group.getContent(), group);
					}
				}
			}
%>
                        Group: 
                        <select name="Group">
<%
			Iterator it = map.values().iterator();
			while (it.hasNext()) {
				AddressingGroup group = (AddressingGroup) it.next();
%>
                          <option value="<%= group.getEntryID() %>"><%= group.getContent() %></option>
<%
			}
%>
                        </select>
<%
		}
%>
                      </div>
                    </td>
                  </tr>
                </table>
<p>
                <table width="64%" border="0" cellspacing="0" cellpadding="3" align="center" bgcolor="#FFFFFF">
                  <tr> 
						<%
					  	if(configBean.isWriteToFileAllowed()) 
					  	{%> 
		                    <td> 
		                      <div align="center"> 
		                        <input type="submit" name="ScheduleConfig" value="Save To Batch">
		                      </div>
		                    </td>
                    	<%} 
                    	else 
                    	{%>
		                    <td> 
		                      <div align="right"> 
		                        <input type="submit" name="ConfigNow" value="Configure Now">
		                      </div>
		                    </td>
		                    <td> 
		                      <div align="left"> 
		                        <input type="submit" name="ScheduleConfig" value="Save To Batch">
		                      </div>
		                    </td>
		             <% } %>
                  </tr>
                </table>
<%
	}
%>
                </form>
              <br>
              </div>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
