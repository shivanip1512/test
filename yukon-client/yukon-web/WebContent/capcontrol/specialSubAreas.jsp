<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib tagdir="/WEB-INF/tags/capcontrol" prefix="capTags"%>

<%@ page import="com.cannontech.common.constants.LoginController" %>
<%@ page import="com.cannontech.cbc.cache.FilterCacheFactory" %>
<%@ page import="com.cannontech.spring.YukonSpringHook" %>
<%@ page import="com.cannontech.cbc.cache.CapControlCache" %>

<cti:standardPage title="Special Substation Bus Areas" module="capcontrol">

<%@include file="cbc_inc.jspf"%>

<%
	FilterCacheFactory cacheFactory = YukonSpringHook.getBean("filterCacheFactory", FilterCacheFactory.class);
	LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);
	final CBCDisplay cbcDisplay = new CBCDisplay(user);
	CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(user);
	String popupEvent = DaoFactory.getAuthDao().getRolePropertyValue(user, WebClientRole.POPUP_APPEAR_STYLE);
	if (popupEvent == null) popupEvent = "onmouseover";
%>
<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="<%=request.getRequestURL().toString()%>"/>

<!-- necessary DIV element for the OverLIB popup library -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

<cti:standardMenu/>

<cti:breadCrumbs>
    <cti:crumbLink url="subareas.jsp" title="Home"/>
    <cti:crumbLink url="specialSubAreas.jsp" title="Special Substation Areas"/>
</cti:breadCrumbs>

<%
String allowCtlVal = DaoFactory.getAuthDao().getRolePropertyValue(user, CBCSettingsRole.SYSTEM_WIDE_CONTROLS);
if (allowCtlVal!=null) {
	boolean allowControl = Boolean.valueOf(allowCtlVal);
	if (allowControl) {%>
			<div id="systemCommandLink" align="right" > </div>
	<%} 
}%>    

<cti:titledContainer title="Special Substation Areas" id="last_titled_container">
	<form id="areaForm" action="substations.jsp" method="post">
		<input type="hidden" name="<%=CCSessionInfo.STR_CC_AREA%>" />
		<input type="hidden" name="<%=CCSessionInfo.STR_CC_AREAID%>" />
        <div>
			<table id="areaTable" width="100%" cellspacing="0" cellpadding="0" >
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
	List<CBCSpecialArea> areas = filterCapControlCache.getSpecialCbcAreas();
	for( CBCSpecialArea area : areas ) {
		css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
		List<SubStation> areaStations = filterCapControlCache.getSubstationsBySpecialArea(area.getPaoID());
%>
			<c:set var="thisAreaId" value="<%=area.getPaoID()%>"/>
            <input type="hidden" id="paoId_${thisAreaId}" value="${thisAreaId}"></input>
            
	        <tr class="<%=css%>">
				<td width="260">
				<input type="checkbox" name="cti_chkbxAreas" value="${thisAreaId}"/>
				<input type="image" id="showAreas${thisAreaId}"
					src="images/nav-plus.gif"
					onclick="showRowElems( 'allAreas${thisAreaId}', 'showAreas${thisAreaId}'); return false;"/>
				<a href="javascript:postMany('areaForm', '<%=CCSessionInfo.STR_CC_AREAID%>', '${thisAreaId}');" class="<%=css%>">
				<cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
                        <a class="editImg" href="/editor/cbcBase.jsf?type=2&itemid=<%=area.getPaoID()%>&ignoreBookmark=true">
                            <img class="rAlign editImg" src="/editor/images/edit_item.gif"/>
                        </a>
                        <a class="editImg" href="/editor/deleteBasePAO.jsf?value=<%=area.getPaoID()%>">
                            <img class="rAlign editImg" src="/editor/images/delete_item.gif"/>
                        </a>
                    </cti:checkProperty>
				<%=area.getPaoName()%></a>
				</td>
                
                <td>
                    <capTags:warningImg paoId="${thisAreaId}" type="CBCSPECIALAREA"/>
                </td>
                
                <td>
					<a id="area_state_${thisAreaId}"
                   	   name="area_state" 
                       class="<%=css%>"
                       href="javascript:void(0);"
					   <%=popupEvent%>="getSpecialAreaMenu('${thisAreaId}');">
						<cti:capControlValue paoId="${thisAreaId}" type="CBCSPECIALAREA" format="STATE" />
					</a>
					<cti:dataUpdaterCallback function="updateStateColorGenerator('area_state_${thisAreaId}')" initialize="true" value="CBCSPECIALAREA/${thisAreaId}/STATE"/>
                </td>
				<td><cti:capControlValue paoId="<%=area.getPaoID()%>" type="CBCSPECIALAREA" format="SETUP" /> Substation(s)</td>
				<td><cti:capControlValue paoId="<%=area.getPaoID()%>" type="CBCSPECIALAREA" format="KVARS_AVAILABLE" /></td>
				<td><cti:capControlValue paoId="<%=area.getPaoID()%>" type="CBCSPECIALAREA" format="KVARS_UNAVAILABLE" /></td>
				<td><cti:capControlValue paoId="<%=area.getPaoID()%>" type="CBCSPECIALAREA" format="KVARS_CLOSED" /></td>
				<td><cti:capControlValue paoId="<%=area.getPaoID()%>" type="CBCSPECIALAREA" format="KVARS_TRIPPED" /></td>
				<td><cti:capControlValue paoId="<%=area.getPaoID()%>" type="CBCSPECIALAREA" format="PFACTOR" /></td>
			</tr>
			<tr>
				<td colspan="3">
					<table id="allAreas${thisAreaId}" width="100%" cellspacing="0" cellpadding="2">
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
            method: 'post', 
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