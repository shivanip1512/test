<%@ page import="com.cannontech.common.constants.LoginController" %>
<%@ page import="com.cannontech.cbc.cache.FilterCacheFactory" %>
<%@ page import="com.cannontech.spring.YukonSpringHook" %>
<%@ page import="com.cannontech.cbc.cache.CapControlCache" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:standardPage title="Substation Bus Areas" module="capcontrol">
<%@include file="cbc_inc.jspf"%>
    <%
            FilterCacheFactory filterCacheFactory = YukonSpringHook.getBean("filterCacheFactory", FilterCacheFactory.class);
			LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);	
			CapControlCache filterCapControlCache = filterCacheFactory.createUserAccessFilteredCache(user);
		    String nd = "\"return nd();\"";
		    String popupEvent = DaoFactory.getAuthDao().getRolePropertyValue(user, WebClientRole.POPUP_APPEAR_STYLE);
		    if (popupEvent == null) popupEvent = "onmouseover";
	%>
<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="<%=request.getRequestURL().toString()%>"/>

<!-- necessary DIV element for the OverLIB popup library -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

<cti:standardMenu/>

<cti:breadCrumbs>
    <cti:crumbLink url="subareas.jsp" title="SubBus Areas"/>
</cti:breadCrumbs>

<%
String allowCtlVal = DaoFactory.getAuthDao().getRolePropertyValue(user, CBCSettingsRole.ALLOW_CONTROLS);
if (allowCtlVal!=null) {
	boolean allowControl = Boolean.valueOf(allowCtlVal);
	if (allowControl) {%>
			<div id="systemCommandLink" align="right" > </div>
<%} 
}%>    
<cti:titledContainer title="Substation Bus Areas" id="last_titled_container">
	<form id="areaForm" action="substations.jsp" method="post">
		<input type="hidden" name="<%=CCSessionInfo.STR_CC_AREA%>" />
		<input type="hidden" name="<%=CCSessionInfo.STR_CC_AREAID%>" />
        <table id="areaHeaderTable" width="100%" border="0" cellspacing="0" cellpadding="0">
    	    <tr class="columnHeader lAlign">				
				<td>Area Name</td>
                <td>State</td>
                <td>Setup</td>
                <td>Available<br/> kVARS</td>
                <td>Disabled <br/>kVARS</td>
                <td>Closed <br/>kVARS</td>
                <td>Tripped <br/>kVARS</td>
                <td>PFactor/Est.</td>
			</tr>
        </table>
        <div >
			<table id="areaTable" width="98%" border="0" cellspacing="0" cellpadding="0" >
<%
	String css = "tableCell";
	String cssSub = "tableCell";
	List<CBCArea> cbcAreas = filterCapControlCache.getCbcAreas();
	for( CBCArea area : cbcAreas ) {
		css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
		List<SubStation> areaStations = filterCapControlCache.getSubstationsByArea(area.getPaoID());
		List<CapBankDevice> areaCapBanks = filterCapControlCache.getCapBanksByArea(area.getPaoID());
		
		//new additions for the available and closed vars
		String varsAvailable = CBCUtils.format( CBCUtils.calcVarsAvailableForSubStations(areaStations, user) );
		String varsUnavailable =  CBCUtils.format (CBCUtils.calcVarsUnavailableForSubStations(areaStations, user) );
		String closedVars = CBCUtils.format( CBCUtils.calcVarsClosedForCapBanks(areaCapBanks, user) );
		String trippedVars = CBCUtils.format( CBCUtils.calcVarsTrippedForCapBanks(areaCapBanks, user) );
		String currPF = CBCDisplay.getPowerFactorText(CBCUtils.calcAvgPF(areaStations), true);
		String estPF = CBCDisplay.getPowerFactorText(CBCUtils.calcAvgEstPF(areaStations), true);
		String areaState = ((Boolean)(filterCapControlCache.getAreaStateMap().get(area.getPaoName())))?"ENABLED":"DISABLED";
		if( area.getOvUvDisabledFlag() ){
			areaState += "-V";
		}
%>
	        <tr class="<%=css%>">
				<td>				
				<input type="checkbox" name="cti_chkbxAreas" value="<%=area.getPaoID()%>"/>
				<input type="image" id="showAreas<%=area.getPaoID()%>"
					src="images/nav-plus.gif"
					onclick="showRowElems( 'allAreas<%=area.getPaoID()%>', 'showAreas<%=area.getPaoID()%>'); return false;"/>
				<a href="#" class="<%=css%>" onclick="postMany('areaForm', '<%=CCSessionInfo.STR_CC_AREAID%>', '<%=area.getPaoID()%>')">
				<%=area.getPaoName()%></a>
				</td>
                <td>
                <!--Create  popup menu html-->               
                <div id = "serverMessage<%=area.getPaoID()%>" style="display:none" > </div>
                
                <input id="cmd_area_<%=area.getPaoID()%>" type="hidden" name = "cmd_dyn" value= "" />
                <a id="area_state_<%=area.getPaoID()%>" name="area_state" 
                    style="<%=css%>"
                    href="javascript:void(0);"
                    <%=popupEvent%> ="return overlib(
                        $F('cmd_area_<%=area.getPaoID()%>'),
                        STICKY, WIDTH,210, HEIGHT,170, OFFSETX,-15,OFFSETY,-15,
                        MOUSEOFF, FULLHTML);"
                    onmouseout= <%=nd%> >
                <%=areaState%>
                </a>
                </td>
				<td><%=areaStations.size()%> Substation(s)</td>
				<td><%=varsAvailable%></td>
				<td><%=varsUnavailable%></td>
				<td><%=closedVars%></td>
				<td><%=trippedVars%></td>
				<td><a type="param1" name="cti_dyn" id="<%=area.getPaoID()%>">
                <%=CBCUtils.CBC_DISPLAY.getAreaValueAt(area, CBCDisplay.AREA_POWER_FACTOR_COLUMN)%></a>
                </td>
			</tr>
			<tr>
				<td>
					<table id="allAreas<%=area.getPaoID()%>">
<%
if (areaStations.size() > 0) {		
	for( int j = 0; j < areaStations.size(); j++ ) {
		SubStation substation = areaStations.get(j);
		List<Feeder> subFeeders = filterCapControlCache.getFeedersBySubStation(substation);
		List<CapBankDevice> subCapBanks = filterCapControlCache.getCapBanksBySubStation(substation);
		cssSub = ("tableCell".equals(cssSub) ? "altTableCell" : "tableCell");
%>
				        <tr class="<%=cssSub%>" style="display: none;">
							<td><font class="lIndent"><%=CBCUtils.CBC_DISPLAY.getSubstationValueAt(substation, CBCDisplay.SUB_NAME_COLUMN)%></font></td>
							<td><%=subFeeders.size()%> Feeder(s), <%=subCapBanks.size()%> Bank(s)</td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
<% 		} %>
	<%}%>
  					</table>
  				</td>
  			</tr>
<% } %>

        </table>
	</div>
</form>
			
<script type="text/javascript">
Event.observe(window, 'load', function() { new CtiNonScrollTable('areaTable','areaHeaderTable');});
Event.observe(window, 'load', function () {
    getServerData();     
});

//register the event handler for the system command
if ($('systemCommandLink')) {
	Event.observe(window, 'load', function () { new Ajax.PeriodicalUpdater('systemCommandLink', 
	    '/spring/capcontrol/cbcAjaxController?action=updateSystemCommandMenu', {
	    method:'post', 
	    asynchronous:true, 
	    frequency: 5, 
	    onFailure: shitHappened,
	    onSuccess: allIsWell});
	});
}

function shitHappened() {
  $('bigFatErrorDiv').show();
}

function allIsWell() {
	$('bigFatErrorDiv').hide();
}

function getServerData() {
	var els = document.getElementsByName('area_state');
	for (var i=0; i < els.length; i++) {
	     new Ajax.PeriodicalUpdater("serverMessage"+i, '/servlet/CBCServlet', {
	     method:'post', 
	     asynchronous:true, 
	     parameters:'areaIndex='+i, 
	     frequency:5, 
	     onSuccess: updateAreaMenu});
	}
}


function updateAreaMenu (resp) {
var msgs = resp.responseText.split(':');
var areaname = msgs[0];
var areaindex = msgs[1];
var areaID = msgs[2];
var areastate = msgs[3];
//update state
document.getElementById ('area_state_' + areaindex).innerHTML = areastate;
//update menu
if (areastate == 'ENABLED' || areastate == 'ENABLED-V')
    document.getElementById('cmd_area_' + areaID).value = generate_SubAreaMenu(areaID,areaname, 0);
if (areastate == 'DISABLED' || areastate == 'DISABLED-V')
    document.getElementById('cmd_area_' + areaID).value = generate_SubAreaMenu(areaID,areaname, 1);
}
</script>
</cti:titledContainer>
<div id="bigFatErrorDiv" style="background: red none repeat scroll 0%; display: none; position: fixed; bottom: 0pt; left: 0pt; width: auto; -moz-background-clip: -moz-initial; -moz-background-origin: -moz-initial; -moz-background-inline-policy: -moz-initial; color: white; font-weight: bold;">
	Your session is invalid.  Refresh this page.
</div>
<div style = "display:none" id = "outerDiv">
	<cti:titledContainer title="Current Status">
    	<div id="cmd_msg_div" />
     </cti:titledContainer>
</div>

</cti:standardPage>
