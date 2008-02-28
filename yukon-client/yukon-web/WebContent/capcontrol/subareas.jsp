<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib tagdir="/WEB-INF/tags/capcontrol" prefix="capTags"%>

<%@ page import="com.cannontech.common.constants.LoginController" %>
<%@ page import="com.cannontech.cbc.cache.FilterCacheFactory" %>
<%@ page import="com.cannontech.spring.YukonSpringHook" %>
<%@ page import="com.cannontech.cbc.cache.CapControlCache" %>

<cti:standardPage title="Substation Bus Areas" module="capcontrol">

<%@include file="cbc_inc.jspf"%>

<script type="text/javascript" language="JavaScript" >
// handles analysis links (which are not functional for a substation area - show error alert)
function loadPointChartGreyBox(title, url) {
    alert(title + ' is not available for a Substation Area.\n\nChoose specific Substation Bus or Feeder within a Substation');
    return void(0);
}
</script>

<%
	FilterCacheFactory filterCacheFactory = YukonSpringHook.getBean("filterCacheFactory", FilterCacheFactory.class);
	LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);
	final CBCDisplay cbcDisplay = new CBCDisplay(user);
	CapControlCache filterCapControlCache = filterCacheFactory.createUserAccessFilteredCache(user);
	String popupEvent = DaoFactory.getAuthDao().getRolePropertyValue(user, WebClientRole.POPUP_APPEAR_STYLE);
	if (popupEvent == null) popupEvent = "onmouseover";
%>
<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="<%=request.getRequestURL().toString()%>"/>

<!-- necessary DIV element for the OverLIB popup library -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

<cti:standardMenu/>

<cti:breadCrumbs>
    <cti:crumbLink url="subareas.jsp" title="Home"/>
    <cti:crumbLink url="subareas.jsp" title="Substation Areas"/>
</cti:breadCrumbs>

<%
String allowCtlVal = DaoFactory.getAuthDao().getRolePropertyValue(user, CBCSettingsRole.SYSTEM_WIDE_CONTROLS);
if (allowCtlVal!=null) {
	boolean allowControl = Boolean.valueOf(allowCtlVal);
	if (allowControl) {%>
			<div id="systemCommandLink" align="right" > </div>
	<%} 
}%>    

<cti:titledContainer title="Substation Areas" id="last_titled_container">
	<form id="areaForm" action="substations.jsp" method="post">
		<input type="hidden" name="<%=CCSessionInfo.STR_CC_AREA%>" />
		<input type="hidden" name="<%=CCSessionInfo.STR_CC_AREAID%>" />
        <div>
			<table id="areaTable" width="100%" border="0" cellspacing="0" cellpadding="0" >
			<tr class="columnHeader lAlign">                
                <td>Area Name</td>
                <td width="2%"></td>
                <td>State</td>
                <td>Setup</td>
                <td>Available<br/> kVARS</td>
                <td>Unavailable <br/>kVARS</td>
                <td>Closed <br/>kVARS</td>
                <td>Tripped <br/>kVARS</td>
                <td>PFactor/Est.</td>
            </tr>
<%
	String css = "tableCell";
	List<CBCArea> cbcAreas = filterCapControlCache.getCbcAreas();
	for( CBCArea area : cbcAreas ) {
		css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
		List<SubStation> areaStations = filterCapControlCache.getSubstationsByArea(area.getPaoID());
%>
			<c:set var="thisAreaId" value="<%=area.getPaoID()%>"/>
            <input type="hidden" id="paoId_${thisAreaId}" value="${thisAreaId}"></input>
            
	        <tr class="<%=css%>">
				<td width="260">
					<input type="checkbox" name="cti_chkbxAreas" value="<%=area.getPaoID()%>"/>
					<input type="image" id="showAreas<%=area.getPaoID()%>"
						src="images/nav-plus.gif"
						onclick="showRowElems( 'allAreas${thisAreaId}', 'showAreas${thisAreaId}'); return false;"/>
					<a href="javascript:postMany('areaForm', '<%=CCSessionInfo.STR_CC_AREAID%>', '${thisAreaId}');" class="<%=css%>">
					<%=area.getPaoName()%></a>
				</td>
                
                <td>
                    <capTags:warningImg paoId="${thisAreaId}" type="CBCAREA"/>
                </td>
                
                <td>
				    <a id="area_state_${thisAreaId}"
                       name="area_state"
                       class="<%=css%>"
                       href="javascript:void(0);" 
					   <%=popupEvent%>="getAreaMenu('${thisAreaId}');">
						<cti:capControlValue paoId="${thisAreaId}" type="CBCAREA" format="STATE" />
					</a>
					<cti:dataUpdaterCallback function="updateStateColorGenerator('area_state_${thisAreaId}')" initialize="true" value="CBCAREA/${thisAreaId}/STATE"/>
                </td>
                
				<td><cti:capControlValue paoId="<%=area.getPaoID()%>" type="CBCAREA" format="SETUP" /> Substation(s)</td>
				<td><cti:capControlValue paoId="<%=area.getPaoID()%>" type="CBCAREA" format="KVARS_AVAILABLE" /></td>
				<td><cti:capControlValue paoId="<%=area.getPaoID()%>" type="CBCAREA" format="KVARS_UNAVAILABLE" /></td>
				<td><cti:capControlValue paoId="<%=area.getPaoID()%>" type="CBCAREA" format="KVARS_CLOSED" /></td>
				<td><cti:capControlValue paoId="<%=area.getPaoID()%>" type="CBCAREA" format="KVARS_TRIPPED" /></td>
				<td><cti:capControlValue paoId="<%=area.getPaoID()%>" type="CBCAREA" format="PFACTOR" /></td>
			</tr>
			<tr>
				<td colspan="3">
					<table id="allAreas${thisAreaId}" width="100%" border="0" cellspacing="0" cellpadding="0">
<%
if (areaStations.size() > 0) {		
	for( int j = 0; j < areaStations.size(); j++ ) {
		SubStation substation = areaStations.get(j);
		List<Feeder> subFeeders = filterCapControlCache.getFeedersBySubStation(substation);
		List<CapBankDevice> subCapBanks = filterCapControlCache.getCapBanksBySubStation(substation);
%>
				        <tr class="<%=css%>" style="display: none;">
							<td><font class="lIndent"><%=cbcDisplay.getSubstationValueAt(substation, CBCDisplay.SUB_NAME_COLUMN)%></font></td>
							<td align="right"><%=subFeeders.size()%> Feeder(s)</td>
							<td align="right"><%=subCapBanks.size()%> Bank(s)</td>
						</tr>
		<%}%>
	<%}%>
  					</table>
  				</td>
				<td class="<%=css%>" colspan="6"></td>
  			</tr>
<%}%>

		</table>
	</div>
</form>
			
<script type="text/javascript" language="JavaScript">
//Event.observe(window, 'load', function() { new CtiNonScrollTable('areaTable','areaHeaderTable');});
Event.observe(window, 'load', checkPageExpire);

//register the event handler for the system command
if ($('systemCommandLink')) {
    Event.observe(window, 'load', function () { 
        new Ajax.PeriodicalUpdater('systemCommandLink', 
            '/spring/capcontrol/cbcAjaxController?action=updateSystemCommandMenu', {
            method:'post', 
            asynchronous:true, 
            frequency: 5, 
            onFailure: function() { $('cannonUpdaterErrorDiv').show();}
        });
    });
}    
</script>

</cti:titledContainer>

<capTags:commandMsgDiv/>

    <ct:disableUpdaterHighlights/>
</cti:standardPage>