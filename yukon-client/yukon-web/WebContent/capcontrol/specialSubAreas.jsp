<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ page import="com.cannontech.common.constants.LoginController" %>
<%@ page import="com.cannontech.spring.YukonSpringHook" %>
<%@ page import="com.cannontech.cbc.cache.CapControlCache" %>
<%@ page import="com.cannontech.cbc.cache.FilterCacheFactory" %>
<cti:standardPage title="Special Substation Bus Areas" module="capcontrol">
<%@include file="cbc_inc.jspf"%>

	<%
        FilterCacheFactory cacheFactory = YukonSpringHook.getBean("filterCacheFactory", FilterCacheFactory.class);
		LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);	
		CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(user);
			    String nd = "\"return nd();\"";
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
String allowCtlVal = DaoFactory.getAuthDao().getRolePropertyValue(user, CBCSettingsRole.ALLOW_CONTROLS);
if (allowCtlVal!=null) {
	boolean allowControl = Boolean.valueOf(allowCtlVal);
		if (allowControl) {
%>
			<div id="systemCommandLink" align="right" > </div>
	<%
	} 
	}
	%>    
<cti:titledContainer title="Special Substation Areas" id="last_titled_container">
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
	List<CBCSpecialArea> areas = filterCapControlCache.getSpecialCbcAreas();
	for( CBCSpecialArea area : areas ) {
		css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
	
		List<SubStation> areaStations = filterCapControlCache.getSubstationsBySpecialArea(area.getPaoID());
		List<CapBankDevice> areaCapBanks = filterCapControlCache.getCapBanksBySpecialArea(area.getPaoID());
	
		//new additions for the available and closed vars
		String varsAvailable = CBCUtils.format( CBCUtils.calcVarsAvailableForSubStations(areaStations, user) );
		String varsUnavailable =  CBCUtils.format (CBCUtils.calcVarsUnavailableForSubStations(areaStations, user) );
		String closedVars = CBCUtils.format( CBCUtils.calcVarsClosedForCapBanks(areaCapBanks, user) );
		String trippedVars = CBCUtils.format( CBCUtils.calcVarsTrippedForCapBanks(areaCapBanks, user) );
		String currPF = CBCDisplay.getPowerFactorText(CBCUtils.calcAvgPF(areaStations), true);
		String estPF = CBCDisplay.getPowerFactorText(CBCUtils.calcAvgEstPF(areaStations), true);
		Boolean b = (Boolean)(filterCapControlCache.getSpecialAreaStateMap().get(area.getPaoName()));
		String areaState;
		if( b == null ){// was here, this shouldn't ever appear
			areaState = "UNKNOWN";
		} else {
			areaState = (b.booleanValue()?"ENABLED":"DISABLED");
		}
		if( area.getOvUvDisabledFlag() ) {
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
                    <%=popupEvent%>="getSpecialAreaMenu('<%=area.getPaoID()%>');">
                <%=areaState%>
                </a>
                </td>
				<td><%=areaStations.size()%> Substation(s)</td>
				<td><%=varsAvailable%></td>
				<td><%=varsUnavailable%></td>
				<td><%=closedVars%></td>
				<td><%=trippedVars%></td>
				<td><%=currPF%> / <%=estPF%></td>
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
		<%}%>
	<%}%>
  					</table>
  				</td>
  			</tr>
<%}%>

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
	var strings = els[i].id.split('_');
        var id = strings[2];
	     new Ajax.PeriodicalUpdater("serverMessage"+id, '/servlet/CBCServlet', {
	     method:'post', 
	     asynchronous:true, 
	     parameters:'specialAreaId='+id, 
	     frequency:5, 
	     onSuccess: updateAreaMenu});
	}
}

function updateAreaMenu (resp) {
    var msgs = resp.responseText.split(':');
    var areaname = msgs[0];
    var areaId = msgs[1];
    var areastate = new String(msgs[2]);
    //update state
    var stateElem = document.getElementById ('area_state_' + areaId);
    stateElem.innerHTML = areastate;
    //update menu
    if (areastate == 'ENABLED' || areastate == 'ENABLED-V') {
        var html = generateAreaMenu(areaId,areaname, 0);
        var elem = document.getElementById('cmd_area_' + areaId); 
        elem.value = html;
        stateElem.style.color = '#3C8242';
    } else if (areastate == 'DISABLED' || areastate == 'DISABLED-V') {
        var html = generateAreaMenu(areaId,areaname, 1);
        var elem = document.getElementById('cmd_area_' + areaId); 
        elem.value = html;
        stateElem.style.color = '#FF0000';
    }
}

function getSpecialAreaMenu(id){
    var html = new String($F('cmd_area_'+id));
    overlib(html, FULLHTML, STICKY);
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
