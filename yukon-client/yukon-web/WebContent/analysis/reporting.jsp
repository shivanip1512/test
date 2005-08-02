<!-- DEPRECATED  THIS IS AN OLD VERSION -->
<!- This is setup to run with OPERATOR side only, as of now anyway 8/24/04-->
<html>
<%@ include file="../operator/Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.roles.application.WebClientRole" %>
<%@ page import="com.cannontech.analysis.*" %>
<%@ page import="com.cannontech.roles.application.ReportingRole" %>
<%@ page import="com.cannontech.analysis.ReportTypes"%>
<%@ page import="com.cannontech.database.cache.functions.AuthFuncs" %>
<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ page import="com.cannontech.analysis.gui.ReportBean" %>
<%@ page import="com.cannontech.database.db.device.DeviceMeterGroup"%>
<%@ page import="com.cannontech.database.model.ModelFactory"%>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
<cti:checklogin/> 
<jsp:useBean id="REPORT_BEAN" class="com.cannontech.analysis.gui.ReportBean" scope="session"/>
<%-- Grab the search criteria --%>
<jsp:setProperty name="REPORT_BEAN" property="type" param="type"/>
<jsp:setProperty name="REPORT_BEAN" property="groupType" param="groupType"/>
<jsp:setProperty name="REPORT_BEAN" property="start" param="startDate"/>
<jsp:setProperty name="REPORT_BEAN" property="stop" param="stopDate"/>
<!-- Java script needed for the Calender Function--->
<SCRIPT  LANGUAGE="JavaScript" SRC="../JavaScript/calendar.js"></SCRIPT>
<head>
<title>Consumer Energy Services</title>
<link rel="stylesheet" href="../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css"> 
</head>
<body class="Background" leftmargin="0" topmargin="0">
<META NAME="robots" CONTENT="noindex, nofollow">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script>

function loadTarget(form)
{
	var extGroup = form.ext;
	for (var i = 0; i < extGroup.length; i++)
	{
		if( extGroup[i].checked)
		{
			if( extGroup[i].value == 'png')
			{
				form.target = '_blank';
				form.REDIRECT.value = '../analysis/reporting_png.jsp';
			}
			else
			{
				form.target = "";
			}
		}
	}
}
function changeFilter(filterBy) {
	document.getElementById("selectAll").disabled = (filterBy == -1);

	document.MForm.billGroupValues[0].selectedIndex = -1;
	document.MForm.billGroupValues[1].selectedIndex = -1;
	document.MForm.billGroupValues[2].selectedIndex = -1;

	document.getElementById("DivCollGroup").style.display = (filterBy == <%= DeviceMeterGroup.COLLECTION_GROUP %>)? "" : "none";
	document.getElementById("DivAltGroup").style.display = (filterBy == <%= DeviceMeterGroup.TEST_COLLECTION_GROUP%>)? "" : "none";
	document.getElementById("DivBillGroup").style.display = (filterBy == <%= DeviceMeterGroup.BILLING_GROUP%>)? "" : "none";
}
function changePaoFilter(filterBy) {

	document.getElementById("selectAll").disabled = (filterBy == -1);

	var typeGroup = document.MForm.paoIDs;
	for (var i = 0; i < typeGroup.length; i++)
	{
		typeGroup[i].selectedIndex = -1;
	}
	
	document.getElementById("DivControlArea").style.display = (filterBy == <%= ModelFactory.LMCONTROLAREA %>)? "" : "none";
	document.getElementById("DivMCT").style.display = (filterBy == <%=ModelFactory.MCT%>)? "" : "none";
}

function disableGroup(form)
{
	var typeGroup = form.paoIDs;
	for (var i = 0; i < typeGroup.length; i++)
	{
		typeGroup[i].disabled = form.selectAll.checked;	
		typeGroup[i].selectedIndex = -1;
	}
	var billGroup = form.billGroupValues;
	for (var i = 0; i < billGroup.length; i++)
	{
		billGroup[i].disabled = form.selectAll.checked;	
		billGroup[i].selectedIndex = -1;
	}
}

function enableDates(value)
{
	if( value)
	{
		document.getElementById("StartCalHref").style.cursor = 'pointer';
		document.getElementById("StopCalHref").style.cursor = 'pointer';
		document.getElementById("StartCalHref").href = 'javascript:openCalendar(document.MForm.startCal)'
		document.getElementById("StopCalHref").href = 'javascript:openCalendar(document.MForm.stopCal)'
	}
	else
	{
		document.getElementById("StartCalHref").style.cursor = 'default';
		document.getElementById("StopCalHref").style.cursor = 'default';
		document.getElementById("StartCalHref").href = 'javascript:;'
		document.getElementById("StopCalHref").href = 'javascript:;'
	}

	document.getElementById("startCal").disabled = !value;
	document.getElementById("stopCal").disabled = !value;

}
</script>

<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../WebConfig/yukon/AnalysisImage.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="yukon/DemoHeader.gif"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Reporting</td>
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle"> 
                  <div align="center"><span class="MainText"><a href="../operator/Operations.jsp" class="Link3">Home</a></span></div>
                </td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
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
            <% String pageName = "reporting.jsp"; %>
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF" > 
            <% if( REPORT_BEAN.getType() < 0  ){%>
            <table width="600" border="0" align="center" cellpadding="4" cellspacing="0" class="TitleHeader">
              <tr> 
                <td width="80%" valign="middle" align="center"><BR>
                  Please select a Report from the options on the left. </td>
              </tr>
            </table>
            <%} else { %>
            <table width="600" border="0" align="center" cellpadding="4" cellspacing="0">
              <tr> 
                <td width="80%" valign="top" align="center"> 
                  <div> <br>
                    <br>
                    <form id="MForm" name="MForm" method="POST" action="<%=request.getContextPath()%>/servlet/ReportGenerator?" onSubmit="loadTarget(document.MForm)">
                      <!-- THE EXTRA INPUT TYPES ARE MAKING THE DOWNLOAD DIALOG APPEAR TWO TIMES -->
                      <input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>">
                      <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
                      <input type="hidden" name="ACTION" value="DownloadReport">
                      <table width="95%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                        <tr> 
                          <td width="55%" height="200"> 
                            <table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
                              <tr> 
                                <td width="50%" height="100%" valign="top"> 
                                  <table width="100%" border="1" cellspacing="0" cellpadding="0" class="TableCell" style="height:100px">
                                    <tr> 
                                      <td valign="top"> 
                                        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                          <tr> 
                                            <td class="HeaderCell" align="center" colspan="3">Options</td>
                                          </tr>
                                        </table>
                                        <%=REPORT_BEAN.buildOptionsHTML()%> </td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                              <tr> 
                                <td height="18" colspan="2"></td>
                              </tr>
                              <tr> 
                                <td> 
                                  <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                    <tr> 
                                      <td> 
                                        <div id="DivBillGroupType" style='display:<%=(REPORT_BEAN.getModel().useBillingGroup() ? "true" : "none")%>'> 
                                          <select id="billGroupType" name="billGroupType"  onChange="changeFilter(this.value)">
                                            <%{
										    String [] billGroupTypes = DeviceMeterGroup.getValidBillGroupTypeDisplayStrings();
										  	for (int i = 0; i < billGroupTypes.length; i++){%>
                                            <option value="<%=DeviceMeterGroup.getValidBillGroupTypeIDs()[i]%>"> <%=billGroupTypes[i]%></option>
                                            <%}}%>
                                          </select>
                                        </div>
										<div id="DivPaoModelType" style='display:<%=(REPORT_BEAN.getModel().usePaobjects() ? "true" : "none")%>'> 
										<% if( REPORT_BEAN.getModel().getPaoModelTypes() != null) {%>
                                          <select id="paoModelType" name="paoModelType"  onChange="changePaoFilter(this.value)">
										  <%
										  	for(int i = 0; i < REPORT_BEAN.getModel().getPaoModelTypes().length; i++) {%>
                                            <option value="<%=REPORT_BEAN.getModel().getPaoModelTypes()[i]%>">
												<%=((com.cannontech.database.model.DBTreeModel) ModelFactory.create(REPORT_BEAN.getModel().getPaoModelTypes()[i])).toString()%>
											</option>
											<%}%>
                                          </select>
										<%}%>
                                        </div>
                                      </td>
                                      <td width="47%" valign="top" rowspan="4" height="100%"> 
                                        <table width="100%" border="1" cellspacing="0" cellpadding="0" class="TableCell" height="100%">
                                          <tr> 
                                            <td> 
                                              <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell" height="100%">
                                                <tr class="HeaderCell"> 
                                                  <td class="HeaderCell" align="center" colspan="3">Generate Report</td>
                                                </tr>
                                                <tr> 
                                                  <td colspan="3" height="9"></td>
                                                </tr>
                                                <tr> 
                                                  <td> 
                                                    <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell" height="100%">
                                                        <tr> 
                                                          <td  width="47%" valign="top" align="center"><font face="Arial, Helvetica, sans-serif" size="1">Start 
                                                            Date:</font> </td>
                                                        </tr>
                                                        <tr> 
                                                          <td valign="top" align="center"> 
                                                            <input id="startCal" type="text" name="startDate"  <%=(REPORT_BEAN.getModel().useStartDate() ? "" : "DISABLED")%> value="<%= datePart.format(REPORT_BEAN.getStartDate()) %>" size="8">
															<%=(REPORT_BEAN.getModel().useStartDate() ? 
                                                            "<a id='startCalHref' href='javascript:openCalendar(document.MForm.startCal)'><img src='"+ request.getContextPath() + "/WebConfig/yukon/Icons/StartCalendar.gif' width='20' height='15' align='ABSMIDDLE' border='0'></a>" : "<img src='"+ request.getContextPath() + "/WebConfig/yukon/Icons/StartCalendar.gif' width='20' height='15' align='ABSMIDDLE' border='0'>")%> 
														  </td>
                                                        </tr>
                                                    </table>
                                                  </td>
                                                  <td>
                                                    <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell" height="100%">
                                                      <tr> 
                                                        <td width="47%" valign="top" align="center"><font face="Arial, Helvetica, sans-serif" size="1">Stop 
                                                          Date:</font> </td>
                                                      </tr>
                                                      <tr> 
                                                        <td valign="top" align="center"> 
                                                          <input id="stopCal" type="text" name="stopDate"  <%=(REPORT_BEAN.getModel().useStopDate() ? "" : "DISABLED")%> value="<%= datePart.format(REPORT_BEAN.getStopDate()) %>" size="8">
															<%=(REPORT_BEAN.getModel().useStopDate() ? 
                                                            "<a id='stopCalHref' href='javascript:openCalendar(document.MForm.stopCal)'><img src='"+ request.getContextPath() + "/WebConfig/yukon/Icons/StartCalendar.gif' width='20' height='15' align='ABSMIDDLE' border='0'></a>" : "<img src='"+ request.getContextPath() + "/WebConfig/yukon/Icons/StartCalendar.gif' width='20' height='15' align='ABSMIDDLE' border='0'>")%> 
                                                        </td>
                                                      </tr>
                                                    </table>
                                                  </td>
                                                </tr>
                                                <tr> 
                                                  <td colspan="3">
                                                    <hr>
                                                  </td>
                                                </tr>
                                                <tr> 
                                                  <td width="50%"> 
                                                    <table class="TableCell" align="right">
                                                      <tr> 
                                                        <td align="center"> 
                                                          <input type="radio" name="ext" value="pdf" checked=true>
                                                          PDF</td>
                                                      </tr>
                                                      <tr> 
                                                        <td align="center"> 
                                                          <input type="radio" name="ext" value="png">
                                                          PNG</td>
                                                      </tr>
                                                    </table>
                                                  </td>
                                                  <td width="50%" colspan="2"> 
                                                    <table class="TableCell" align="center" valign="middle">
                                                      <tr> 
                                                        <td align="center"> 
                                                          <input type="image" src="<%=request.getContextPath()%>/WebConfig/yukon/Buttons/GoButton.gif" name="Generate" border="0" alt="Generate" align="middle"">
                                                      </tr>
                                                    </table>
                                                  </td>
                                                </tr>
                                              </table>
                                            </td>
                                          </tr>
                                        </table>
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td height="9"> </td>
                                    </tr>
                                    <tr> 
                                      <td align="left" valign="top" height="19"> 
                                        <div id="DivCollGroup" style='display:<%=(REPORT_BEAN.getModel().useBillingGroup() ? "true" : "none")%>'>
                                          <select name="billGroupValues" size="10" multiple style="width:250px;">
                                            <%
										  String [] groups = DeviceMeterGroup.getDeviceCollectionGroups();
										   for (int i = 0; i < groups.length; i++)
										  {
											String val = groups[i];
											%>
                                            <option value="<%=val%>"><%=val%></option>
                                            <%}%>
                                          </select>
                                        </div>
                                        <div id="DivAltGroup" style="display:none"> 
                                          <select name="billGroupValues" size="10" multiple style="width:250px;">
                                            <%
										    groups = DeviceMeterGroup.getDeviceTestCollectionGroups();
										     for (int i = 0; i < groups.length; i++)
										     {
											 	String val = groups[i];
												%>
                                            <option value="<%=val%>"><%=val%></option>
                                            <%}%>
                                          </select>
                                        </div>
                                        <div id="DivBillGroup" style="display:none"> 
                                          <select name="billGroupValues" size="10" multiple style="width:250px;">
                                            <%
										   groups = DeviceMeterGroup.getDeviceBillingGroups();
										   for (int i = 0; i < groups.length; i++)
										  {
											String val = groups[i];
											%>
                                            <option value="<%=val%>"><%=val%></option>
                                            <%}%>
                                          </select>
                                        </div>
										<div id="DivControlArea" style='display:<%=(REPORT_BEAN.getModel().usePaobjects() ? "true" : "none")%>'> 
                                          <select name="paoIDs" size="10" multiple style="width:250px;">
                                            <%{
											java.util.List litePaos = REPORT_BEAN.getModel().getPaobjectsByModel(ModelFactory.LMCONTROLAREA);
											if( litePaos != null)
											{
												for (int i = 0; i < litePaos.size(); i++)
											    {
													LiteYukonPAObject lpao = (LiteYukonPAObject)litePaos.get(i);
													%>
                                            <option value="<%=lpao.getYukonID()%>"><%=lpao.getPaoName()%></option>
                                            <%}
											}}%>
                                          </select>
                                        </div>
										<div id="DivMCT" style="display:none">
                                          <select name="paoIDs" size="10" multiple style="width:250px;">
                                            <%{
											java.util.List litePaos = REPORT_BEAN.getModel().getPaobjectsByModel(ModelFactory.MCT);
											if( litePaos != null)
											{
												for (int i = 0; i < litePaos.size(); i++)
											    {
													LiteYukonPAObject lpao = (LiteYukonPAObject)litePaos.get(i);
													%>
                	                            	<option value="<%=lpao.getYukonID()%>"><%=lpao.getPaoName()%></option>
                    	                        <%}
											}}%>
                                          </select>
                                        </div>
                                      </td>
                                    </tr>
                                    <tr> 
                                      <td align="left" height="10"> 
	                                    <div id="DivSelectAll" style='display:<%=((REPORT_BEAN.getModel().useBillingGroup() ||REPORT_BEAN.getModel().usePaobjects()) ? "true" : "none")%>'> 
                                        <input type="checkbox" name="selectAll" value="selectAll" onclick="disableGroup(document.MForm);">Select All
                                        </div>
                                      </td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </form>
                  </div>
                </td>
              </tr>
            </table>
            <%}%>
            <br>
          </td>
        </tr>
      </table>
</table>
</body>
</html>
