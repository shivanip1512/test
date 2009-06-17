<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@page import="java.util.*" %>
<%@page import="com.cannontech.yukon.cbc.*" %>
<%@page import="com.cannontech.util.*" %>
<%@page import="com.cannontech.cbc.util.*" %>
<%@page import="com.cannontech.database.data.lite.*" %>
<%@page import="com.cannontech.database.data.pao.CapControlType" %>
<%@ page import="com.cannontech.common.constants.LoginController" %>
<%@ page import="com.cannontech.spring.YukonSpringHook" %>
<%@ page import="com.cannontech.cbc.cache.CapControlCache" %>
<%@ page import="com.cannontech.cbc.cache.FilterCacheFactory" %>
<%@ page import="com.cannontech.core.dao.PaoDao" %>

<link rel="stylesheet" type="text/css" href="/WebConfig/yukon/CannonStyle.css" >
<link rel="stylesheet" type="text/css" href="/WebConfig/yukon/styles/StandardStyles.css" >
<link rel="stylesheet" type="text/css" href="/WebConfig/yukon/styles/YukonGeneralStyles.css" >
<link rel="stylesheet" type="text/css" href="/WebConfig/yukon/styles/InnerStyles.css" >
<link rel="stylesheet" type="text/css" href="/capcontrol/css/base.css" >

<script type="text/javascript" src="/JavaScript/prototype.js" ></script>
<script type="text/javascript" src="/capcontrol/js/cbc_funcs.js" ></script>

<cti:url var="executeURL" value="/spring/capcontrol/commandexecutor?action=executeCommandTier"/>
<cti:url var="controlOrderPage" value="/capcontrol/feederBankInfo.jsp"/>

<%
    FilterCacheFactory cacheFactory = YukonSpringHook.getBean("filterCacheFactory", FilterCacheFactory.class);
	LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);
	final CBCDisplay cbcDisplay = new CBCDisplay(user);
	CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(user);
	CapControlCache capControlCache = YukonSpringHook.getBean("capControlCache", CapControlCache.class);
	PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
	
	int bankid = ParamUtil.getInteger(request, "bankid");
	int subbusID = 0;
	boolean oneline = ParamUtil.getBoolean(request,"oneline",false);
	List<CCArea> allAreas = filterCapControlCache.getCbcAreas();	
	CapBankDevice capBank = filterCapControlCache.getCapBankDevice( new Integer(bankid) );
	int oldfdrid = 0;
	if( oneline ){
		subbusID = filterCapControlCache.getParentSubBusID(bankid);
	}
	if( capBank != null )
	{
		oldfdrid = capBank.getParentID();
	}
	String path = capBank.getCcName(); 
    path += " > ";
    path += paoDao.getYukonPAOName(capBank.getParentID());
    path += " > ";
    path += paoDao.getYukonPAOName(capControlCache.getFeeder(capBank.getParentID()).getParentID());
    path += " > ";
    path += paoDao.getYukonPAOName(capControlCache.getSubBus(capControlCache.getFeeder(capBank.getParentID()).getParentID()).getParentID());
    path += " > ";
    path += paoDao.getYukonPAOName(capControlCache.getSubstation(capControlCache.getSubBus(capControlCache.getFeeder(capBank.getParentID()).getParentID()).getParentID()).getParentID());
%>

<script type="text/javascript"> 
function updateFeederBankInfo()
{
	var params = {'FeederID': getSelectedFeeder()};
    new Ajax.Updater('ControlOrders', '${controlOrderPage}', {method: 'post', parameters: params});
}
function showDiv( v ){
	$(v).toggle();
}
function getSelectedFeeder()
{
	var selectedFeeder = $("selectedFeeder");
	return selectedFeeder.value;
}
function getOrder( str )
{
	var str = "txt" + str + "Order";
	var Obj = $(str);
	return Obj.value;
}

function setFeederIDinRedirectURL( id, oneline, subbusID ) {
	$("redirectURL").value = "/capcontrol/moved.jsp?FeederID=" + id;
	if( oneline == 'true' ){
		$("redirectURL").value = "/capcontrol/moved.jsp&subbusID=" + subbusID;
	}
}

function selectFeeder( fid ){
	$("selectedFeeder").value = fid;
	updateFeederBankInfo();
}

function toggleExpandImg( imgID ) {   
    var imgElem = $(imgID);

    if( imgElem.src.indexOf('/capcontrol/images/nav-minus.gif') > 0 ) {
        imgElem.src='/capcontrol/images/nav-plus.gif';
    }
    else {
        imgElem.src = '/capcontrol/images/nav-minus.gif';
    }
}

Event.observe(window, 'load', updateFeederBankInfo, false);

</script>

<div >
	<!--sort of a hack on REDIRECTURL input for now, but I can not figure out how to get around the use of the proxy in XmlHTTP calls-->
	<form id="frmCapBankMove" action="${executeURL}" method="POST" >
		<input type="hidden" name="redirectURL" value="/capcontrol/moved.jsp" id="redirectURL" >
		<input type="hidden" name="controlType" value="<%=CapControlType.CAPBANK%>">
		<input type="hidden" name="paoId" value="<%=bankid%>">
		<input type="hidden" name="cmdId" value="<%=CapControlCommand.CMD_BANK_TEMP_MOVE%>">
		<input type="hidden" name="opt" value="<%=oldfdrid%>">  <!--Old Feeder ID-->
		<input type="hidden" name="opt"> <!--New Feeder ID-->
		<input type="hidden" name="opt"> <!--Display Order-->
		<input type="hidden" name="opt"> <!--Close Order-->
		<input type="hidden" name="opt"> <!--Trip Order-->
		<input type="hidden" name="opt"> <!--Permanent/Temp-->
		<input type="hidden" name="selectedFeeder" value="<%=oldfdrid%>" id="selectedFeeder">
	
	    <table id="innerTable" style="margin-left: 5%; margin-right: 5%;" border="0" cellspacing="0" cellpadding="0">
	        <tr>
	           <td class="columnHeader" colspan="5">Move for: <%=path %></td>
            </tr>
	        <tr height="5px"/>
	        <tr class="columnHeader lAlign">
	            <td class="rAlign">
                    <input id="submitOne" type="button" value="Temp Move" 
                        onclick="setFeederIDinRedirectURL(getSelectedFeeder(),<%=oneline%>,<%=subbusID %>);postMany('frmCapBankMove', 'opt[1]', getSelectedFeeder(), 'opt[2]', getOrder('Display'), 'opt[3]', getOrder('Close'), 'opt[4]', getOrder('Trip'), 'opt[5]', 0 );"/>
                </td>
                <td class="rAlign">
                    <input id="submitOne" type="button" value="Move" 
                        onclick="setFeederIDinRedirectURL(getSelectedFeeder(),<%=oneline%>,<%=subbusID %>);postMany('frmCapBankMove', 'opt[1]', getSelectedFeeder(), 'opt[2]', getOrder('Display'), 'opt[3]', getOrder('Close'), 'opt[4]', getOrder('Trip'), 'opt[5]', 1 );"/>
                </td>
	            <td class="rAlign">Display Order: <input type="text" id="txtDisplayOrder" class="tableCell" size="1" maxlength="3" value="1.5"></td>
	            <td class="rAlign">Close Order: <input type="text" id="txtCloseOrder" class="tableCell" size="1" maxlength="3" value="1.5"></td>
	            <td class="rAlign">Trip Order: <input type="text" id="txtTripOrder" class="tableCell" size="1" maxlength="3" value="1.5"></td>
	        </tr>
	    </table>   
	
		<div id="ControlOrders" style="margin-left: 5%; margin-right: 5%;height:25%;max-height:25%;margin-bottom:2%;margin-top:2%;"></div>
		
		<div style="margin-left: 5%; margin-right: 5%;" >
			<cti:titledContainer title="Feeders Eligible for the Move | Select a feeder below:" >
			
				<div style=" height: 275px; overflow: auto">
					<%
					String css = "altTableCell";
					String indent = "\t";
					for( CCArea area : allAreas ) {
						List<SubStation> stationsOnArea = filterCapControlCache.getSubstationsByArea( area.getPaoId() );%>
						<div>
							<img id="chkBxArea<%=area.getCcId()%>"src="images/nav-plus.gif"
								onclick="showDiv('areaId<%=area.getCcId()%>');toggleExpandImg('chkBxArea<%=area.getCcId()%>');"/>
							<%=area.getPaoName()%>
							<div class="tableCell" style="display:none" id="areaId<%=area.getCcId()%>">
							<%for( SubStation station : stationsOnArea ){
								List<SubBus> subsOnStation = filterCapControlCache.getSubBusesBySubStation(station);%>
								<div>		
					                <img class="lIndent" id="chkBxStation<%=station.getCcId()%>" src="images/nav-plus.gif"
					                    onclick="showDiv( 'stationId<%=station.getCcId() %>' );toggleExpandImg( 'chkBxStation<%=station.getCcId()%>');"/>
					                <%=station.getCcName() %>
					                <div class="tableCell" style="display:none" id="stationId<%=station.getCcId() %>">
									<%for( SubBus subBus : subsOnStation ) {
					                    List<Feeder> feeders = filterCapControlCache.getFeedersBySubBus(subBus.getCcId());%>
					                    <div>
					                        <img class="llIndent" id="chkBxSub<%=subBus.getCcId()%>" src="images/nav-plus.gif"
					                            onclick="showDiv( 'subId<%=subBus.getCcId()%>' );toggleExpandImg('chkBxSub<%=subBus.getCcId()%>');"/>
					                        <%=cbcDisplay.getSubBusValueAt(subBus, CBCDisplay.SUB_NAME_COLUMN) %>
					                        <div class="altTableCell" style="display:none" id="subId<%=subBus.getCcId()%>">
					                        <%for( Feeder feeder : feeders ) {
					                            if( feeder.getCcId().intValue() == oldfdrid )
					                                continue;%>
					                            <div>
					                                <input class="capbankTempMoveLink" type="radio" name="feeder" id="feederId<%=feeder.getCcId()%>" 
					                                    onclick="selectFeeder(<%=feeder.getCcId()%>);"/>
					                                    <%=cbcDisplay.getFeederValueAt(feeder, CBCDisplay.FDR_NAME_COLUMN)%>
					                            </div>			
									        <%}%>
					                        </div>
					                    </div>
					                <%}%>
					                </div>
					            </div>
					        <%}%>
					        </div>
					    </div>
					<%}%>
				</div>
			</cti:titledContainer>
		</div>
	</form>
</div>