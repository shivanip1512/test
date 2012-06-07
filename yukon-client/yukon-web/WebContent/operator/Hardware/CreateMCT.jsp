<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.database.data.lite.*" %>
<%@ page import="com.cannontech.database.data.pao.RouteTypes" %>
<%@ page import="com.cannontech.stars.core.dao.StarsInventoryBaseDao" %>
<%
	if (request.getParameter("Init") != null) {
		// The "Create MCT" link in the nav is clicked
		session.removeAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST);
		session.removeAttribute(InventoryManagerUtil.DEVICE_SELECTED);
	}
	else if (request.getParameter("Member") != null) {
		// Request from the same page while member selection has been changed
		ServletUtils.saveRequest(request, session, new String[] {"Member", "DeviceLabel", "AltTrackNo", "ReceiveDate", "Voltage", "ServiceCompany", "Notes"});
	}
	else if (request.getParameter("InvID") != null) {
        
		StarsInventoryBaseDao starsInventoryBaseDao = 
	        YukonSpringHook.getBean("starsInventoryBaseDao", StarsInventoryBaseDao.class);
        
		// Request from InventoryDetail.jsp to copy a hardware device
		LiteInventoryBase liteInv = starsInventoryBaseDao.getByInventoryId(Integer.parseInt(request.getParameter("InvID")));
		LiteYukonPAObject litePao = DaoFactory.getPaoDao().getLiteYukonPAO(liteInv.getDeviceID());
		Properties savedReq = new Properties();
		savedReq.setProperty("DeviceLabel", liteInv.getDeviceLabel());
		savedReq.setProperty("ReceiveDate", dateFormattingService.format(new Date(liteInv.getReceiveDate()), DateFormatEnum.DATE, userContext));
		savedReq.setProperty("Voltage", String.valueOf(liteInv.getVoltageID()));
		savedReq.setProperty("ServiceCompany", String.valueOf(liteInv.getInstallationCompanyID()));
		savedReq.setProperty("Notes", liteInv.getNotes().replaceAll("<br>", System.getProperty("line.separator")));
		session.setAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST, savedReq);
		session.removeAttribute(InventoryManagerUtil.DEVICE_SELECTED);
	}
	
	Properties savedReq = (Properties) session.getAttribute(ServletUtils.ATT_LAST_SUBMITTED_REQUEST);
	if (savedReq == null) savedReq = new Properties();
	
	int deviceID = 0;
	LiteYukonPAObject selectedMCT = (LiteYukonPAObject) session.getAttribute(InventoryManagerUtil.DEVICE_SELECTED);
	if (selectedMCT != null) {
		deviceID = selectedMCT.getYukonID();
	}
	
	LiteStarsEnergyCompany member = null;
	if (savedReq.getProperty("Member") != null)
		member = StarsDatabaseCache.getInstance().getEnergyCompany(Integer.parseInt(savedReq.getProperty("Member")));
	if (member == null) member = liteEC;
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<script language="JavaScript">
function selectMCT(form) {
	form.attributes["action"].value = "SelectMCT.jsp";
	form.submit();
}

function changeMember(form) {
	form.attributes["action"].value = "CreateMCT.jsp";
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
		    <% String pageName = "CreateMCT.jsp"; %>
			<%@ include file="include/Nav.jspf" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "ADD YUKON MCT"; %>
              <%@ include file="include/SearchBar.jspf" %>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
              <form name="MForm" method="post" action="<%= request.getContextPath() %>/servlet/InventoryManager" onsubmit="return validate(this)">
			    <input type="hidden" name="action" value="CreateMCT">
				<input type="hidden" name="DeviceID" value="<%= deviceID %>">
				<input type="hidden" name="DeviceType" value="<%= liteEC.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT).getEntryID() %>">
				<input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Hardware/InventoryDetail.jsp?InvId=">
                <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
                <table width="610" border="0" cellspacing="0" cellpadding="10" align="center">
<% if (liteEC.hasChildEnergyCompanies() && DaoFactory.getAuthDao().checkRoleProperty(lYukonUser, AdministratorRole.ADMIN_MANAGE_MEMBERS)) { %>
                  <tr align="center"> 
                    <td colspan="2" valign="top" bgcolor="#FFFFFF" class="TableCell"> 
                      Member: 
                      <select name="Member" onChange="changeMember(this.form)">
                        <%
	List<LiteStarsEnergyCompany> descendants = ECUtils.getAllDescendants(liteEC);
	for (int i = 0; i < descendants.size(); i++) {
		LiteStarsEnergyCompany company = (LiteStarsEnergyCompany) descendants.get(i);
		String selected = company.equals(member)? "selected" : "";
%>
                        <option value="<%= company.getLiteID() %>" <%= selected %>><%= company.getName() %></option>
                        <%
	}
%>
                      </select>
                    </td>
                  </tr>
<% } %>
                  <tr> 
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
                        <tr> 
                          <td><span class="SubtitleHeader">Yukon MCT</span> 
                            <hr>
                            <br>
                          </td>
                        </tr>
                      </table>
                      <div id="SelectMCTDiv">
	                      <script language="JavaScript">setContentChanged(<%= request.getParameter("Selected") != null %>);</script>
	                      <table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
	                        <tr> 
	                          <td> <br>
	                          	<div align="center">
                            		<span class="MainText">Select from the list of all Yukon MCTs: </span>
                           		</div> 
	<% if (selectedMCT != null) { %>
	                            <br>
	                            <table width="300" border="0" cellspacing="0" cellpadding="3" class="TableCell">
	                              <tr> 
	                                <td align="right" width="88">Type:</td>
	                                <td width="210"><%= selectedMCT.getPaoType().getDbString() %></td>
	                              </tr>
	                              <tr> 
	                                <td align="right" width="88">Device Name:</td>
	                                <td width="210"><%= selectedMCT.getPaoName()%></td>
	                              </tr>
	                            </table>
	                            <br>
	                            <div align="center">
	                            	<input type="button" name="ChangeMCT" value="Change" onClick="selectMCT(this.form)">
	                           	</div>
	<% } else { %>
	                            <div align="center">
		                            <p> 
		                              <input type="button" name="SelectMCT" value="Choose Yukon MCT" onClick="selectMCT(this.form)">
		                            </p>
		                        </div>
	<% } %>
	                          </td>
	                        </tr>
	                      </table>
	                	</div>
                    </td>
                    <%boolean alreadySelectedMCT = request.getParameter("Selected") != null; %>
                    <td width="300" valign="top" bgcolor="#FFFFFF"> 
                      <table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
                        <tr> 
                          <td><span class="SubtitleHeader">DEVICE</span> 
                            <hr>
                            <table width="300" border="0" cellspacing="0" cellpadding="1" align="center">
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Label:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="DeviceLabel" maxlength="30" size="24" <%=alreadySelectedMCT ? "" : "disabled"%> value="<%= StarsUtils.forceNotNull(savedReq.getProperty("DeviceLabel")) %>" onChange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Alt Tracking #:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="AltTrackNo" maxlength="30" size="24" <%=alreadySelectedMCT ? "" : "disabled"%> value="<%= StarsUtils.forceNotNull(savedReq.getProperty("AltTrackNo")) %>" onChange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Field Date Received:</div>
                                </td>
                                <td width="210"> 
                                  <input type="text" name="fieldReceiveDate" maxlength="30" size="24" <%=alreadySelectedMCT ? "" : "disabled"%> value="<%= StarsUtils.forceNotNull(savedReq.getProperty("fieldReceiveDate")) %>" onChange="setContentChanged(true)">
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Voltage:</div>
                                </td>
                                <td width="210"> 
                                  <select name="Voltage" <%=alreadySelectedMCT ? "" : "disabled"%> onChange="setContentChanged(true)">
                                    <%
	int savedVoltage = 0;
	if (savedReq.getProperty("Voltage") != null)
		savedVoltage = Integer.parseInt(savedReq.getProperty("Voltage"));
	
	YukonSelectionList voltageList = member.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE );
	for (int i = 0; i < voltageList.getYukonListEntries().size(); i++) {
		YukonListEntry entry = (YukonListEntry) voltageList.getYukonListEntries().get(i);
		String selected = (entry.getEntryID() == savedVoltage)? "selected" : "";
%>
                                    <option value="<%= entry.getEntryID() %>" <%= selected %>><%= entry.getEntryText() %></option>
                                    <%
	}
%>
                                  </select>
                                </td>
                              </tr>
                              <tr>
                                <td width="88" class="TableCell" align="right">Service 
                                  Company: </td>
                                <td width="210">
                                  <select name="ServiceCompany" <%=alreadySelectedMCT ? "" : "disabled"%> >
                                    <%
	int savedServiceCompany = 0;
	if (savedReq.getProperty("ServiceCompany") != null)
		savedServiceCompany = Integer.parseInt(savedReq.getProperty("ServiceCompany"));
	
	StarsServiceCompanies companyList = member.getStarsServiceCompanies();
	for (int i = 0; i < companyList.getStarsServiceCompanyCount(); i++) {
		StarsServiceCompany servCompany = companyList.getStarsServiceCompany(i);
		String selected = (servCompany.getCompanyID() == savedServiceCompany)? "selected" : "";
%>
                                    <option value="<%= servCompany.getCompanyID() %>" <%= selected %>><%= servCompany.getCompanyName() %></option>
                                    <%
	}
%>
                                  </select>
                                </td>
                              </tr>
                              <tr> 
                                <td width="88" class="TableCell"> 
                                  <div align="right">Notes:</div>
                                </td>
                                <td width="210"> 
                                  <textarea name="Notes" rows="3" wrap="soft" cols="28" class = "TableCell" <%=alreadySelectedMCT ? "" : "disabled"%> onChange="setContentChanged(true)"><%= StarsUtils.forceNotNull(savedReq.getProperty("Notes")) %></textarea>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                <table width="300" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                  <tr> 
                    <td align="center"> 
                      <input type="submit" name="Save" <%=alreadySelectedMCT ? "" : "disabled"%> value="Save">
                    </td>
                  </tr>
                </table>
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
