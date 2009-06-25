<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<%@ page import="java.util.*" %>
<%@ page import="com.cannontech.yukon.cbc.*" %>
<%@ page import="com.cannontech.util.*" %>
<%@ page import="com.cannontech.cbc.util.*" %>
<%@ page import="com.cannontech.database.data.lite.*" %>
<%@ page import="com.cannontech.database.data.pao.CapControlType" %>
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
<cti:url var="controlOrderPage" value="/spring/capcontrol/move/feederBankInfo"/>

<script type="text/javascript"> 
function updateFeederBankInfo() {
    var params = {'FeederID': getSelectedFeeder()};
    new Ajax.Updater('ControlOrders', '${controlOrderPage}', {method: 'post', parameters: params});
}

function showDiv( v ){
    $(v).toggle();
}

function getSelectedFeeder() {
    var selectedFeeder = $("selectedFeeder");
    return selectedFeeder.value;
}

function getOrder( str ) {
    var str = "txt" + str + "Order";
    var Obj = $(str);
    return Obj.value;
}

function setFeederIDinRedirectURL( id, oneline, subBusId ) {
    $("redirectURL").value = "/capcontrol/moved.jsp?FeederID=" + id;
    if( oneline == 'true' ){
        $("redirectURL").value = "/capcontrol/moved.jsp&subBusId=" + subBusId;
    }
}

function selectFeeder( fid ) {
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
        <input type="hidden" name="controlType" value="${controlType}">
        <input type="hidden" name="paoId" value="${bankId}">
        <input type="hidden" name="cmdId" value="${commandId}">
        <input type="hidden" name="opt" value="${oldFeederId}">  <!--Old Feeder ID-->
        <input type="hidden" name="opt"> <!--New Feeder ID-->
        <input type="hidden" name="opt"> <!--Display Order-->
        <input type="hidden" name="opt"> <!--Close Order-->
        <input type="hidden" name="opt"> <!--Trip Order-->
        <input type="hidden" name="opt"> <!--Permanent/Temp-->
        <input type="hidden" name="selectedFeeder" value="${oldFeederId}" id="selectedFeeder">
    
        <table id="innerTable" style="margin-left: 5%; margin-right: 5%; margin-bottom:2%; margin-top:2%;" border="0" cellspacing="0" cellpadding="0">
            <tr>
               <td class="columnHeader" colspan="5">Move for: ${path}</td>
            </tr>
            <tr height="5px"/>
            <tr class="columnHeader lAlign">
                <td class="rAlign">
                    <input id="submitOne" type="button" value="Temp Move" 
                        onclick="setFeederIDinRedirectURL(getSelectedFeeder(),${oneline},${subBusId});postMany('frmCapBankMove', 'opt[1]', getSelectedFeeder(), 'opt[2]', getOrder('Display'), 'opt[3]', getOrder('Close'), 'opt[4]', getOrder('Trip'), 'opt[5]', 0 );"/>
                </td>
                <td class="rAlign">
                    <input id="submitOne" type="button" value="Move" 
                        onclick="setFeederIDinRedirectURL(getSelectedFeeder(),${oneline},${subBusId});postMany('frmCapBankMove', 'opt[1]', getSelectedFeeder(), 'opt[2]', getOrder('Display'), 'opt[3]', getOrder('Close'), 'opt[4]', getOrder('Trip'), 'opt[5]', 1 );"/>
                </td>
                <td class="rAlign">Display Order: <input type="text" id="txtDisplayOrder" class="tableCell" size="1" maxlength="3" value="1.5"></td>
                <td class="rAlign">Close Order: <input type="text" id="txtCloseOrder" class="tableCell" size="1" maxlength="3" value="1.5"></td>
                <td class="rAlign">Trip Order: <input type="text" id="txtTripOrder" class="tableCell" size="1" maxlength="3" value="1.5"></td>
            </tr>
        </table>   
        
        <div id="ControlOrders"></div>
        <div style="margin-left: 2%; margin-right: 2%;  padding-bottom:2%; padding-top:2%;">
            <ct:abstractContainer styleClass="boxContainerPadding" type="box" title="Feeders Eligible for the Move | Select a feeder below:" hideEnabled="false">
                <div style=" height: 275px; overflow: auto;">
                    <c:forEach var="viewableArea" items="${allAreas}">
                        <div>
                            <img id="chkBxArea${viewableArea.area.ccId}"src="/capcontrol/images/nav-plus.gif" onclick="showDiv('areaId${viewableArea.area.ccId}');toggleExpandImg('chkBxArea${viewableArea.area.ccId}');"/>
                            ${viewableArea.area.paoName}
                            <div class="tableCell" style="display:none" id="areaId${viewableArea.area.ccId}">
                                <c:forEach var="viewableStation" items="${viewableArea.subStations}">
                                    <div>       
                                        <img class="lIndent" id="chkBxStation${viewableStation.substation.ccId}" src="/capcontrol/images/nav-plus.gif" onclick="showDiv( 'stationId${viewableStation.substation.ccId}' );toggleExpandImg( 'chkBxStation${viewableStation.substation.ccId}');"/>
                                        ${viewableStation.subStationName}
                                        <div class="tableCell" style="display:none" id="stationId${viewableStation.substation.ccId}">
                                            <c:forEach var="viewableSubBus" items="${viewableStation.subBuses}">
                                                <div>
                                                    <img class="llIndent" id="chkBxSub${viewableSubBus.subBus.ccId}" src="/capcontrol/images/nav-plus.gif" onclick="showDiv( 'subId${viewableSubBus.subBus.ccId}' );toggleExpandImg('chkBxSub${viewableSubBus.subBus.ccId}');"/>
                                                    ${viewableSubBus.subBus.ccName}
                                                    <div class="altTableCell" style="display:none" id="subId${viewableSubBus.subBus.ccId}">
                                                        <c:forEach var="viewableFeeder" items="${viewableSubBus.feeders}">
                                                            <c:if test="${!(viewableFeeder.feeder.ccId == oldFeederId)}">
                                                                <div>
                                                                    <input class="capbankTempMoveLink" type="radio" name="feeder" id="feederId${viewableFeeder.feeder.ccId}"onclick="selectFeeder(${viewableFeeder.feeder.ccId});"/>
                                                                    ${viewableFeeder.feeder.ccName}
                                                                </div>
                                                            </c:if>
                                                        </c:forEach>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </ct:abstractContainer>
        </div>
    </form>
</div>