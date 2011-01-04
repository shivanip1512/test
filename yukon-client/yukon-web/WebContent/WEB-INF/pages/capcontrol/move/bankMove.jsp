<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<link rel="stylesheet" type="text/css" href="/WebConfig/yukon/styles/YukonGeneralStyles.css" >
<link rel="stylesheet" type="text/css" href="/capcontrol/css/CapcontrolGeneralStyles.css" >

<cti:includeScript link="PROTOTYPE"/>
<script type="text/javascript" src="/capcontrol/js/capcontrolGeneral.js" ></script>

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
    if( oneline == true ){
        $("redirectURL").value += "&subbusID=" + subBusId;
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
               <td class="boldLabel" colspan="5">Move for: ${path}</td>
            </tr>
            <tr height="5px"></tr>
            <tr class="boldLabel">
                <td>
                    <input id="submitOne" type="button" value="Temp Move" 
                        onclick="setFeederIDinRedirectURL(getSelectedFeeder(),${oneline},${subBusId});postMany('frmCapBankMove', 'opt[1]', getSelectedFeeder(), 'opt[2]', getOrder('Display'), 'opt[3]', getOrder('Close'), 'opt[4]', getOrder('Trip'), 'opt[5]', 0 );"/>
                </td>
                <td>
                    <input id="submitOne" type="button" value="Move" 
                        onclick="setFeederIDinRedirectURL(getSelectedFeeder(),${oneline},${subBusId});postMany('frmCapBankMove', 'opt[1]', getSelectedFeeder(), 'opt[2]', getOrder('Display'), 'opt[3]', getOrder('Close'), 'opt[4]', getOrder('Trip'), 'opt[5]', 1 );"/>
                </td>
                <td>Display Order: <input type="text" id="txtDisplayOrder" class="tableCell" size="1" maxlength="3" value="1.5"></td>
                <td>Close Order: <input type="text" id="txtCloseOrder" class="tableCell" size="1" maxlength="3" value="1.5"></td>
                <td>Trip Order: <input type="text" id="txtTripOrder" class="tableCell" size="1" maxlength="3" value="1.5"></td>
            </tr>
        </table>   
        
        <div id="ControlOrders"></div>
        <div style="margin-left: 2%; margin-right: 2%;  padding-bottom:2%; padding-top:2%;">
            <ct:abstractContainer type="box" title="Feeders Eligible for the Move | Select a feeder below:" hideEnabled="false">
                <div style=" height: 275px; overflow: auto;">
                    <c:forEach var="simpleArea" items="${allAreas}">
                        <div>
                            <img id="chkBxArea${simpleArea.id}"src="/capcontrol/images/nav-plus.gif" onclick="showDiv('areaId${simpleArea.id}');toggleExpandImg('chkBxArea${simpleArea.id}');"/>
                            ${simpleArea.name}
                            <div class="tableCell" style="display:none" id="areaId${simpleArea.id}">
                                <c:forEach var="simpleStation" items="${simpleArea.substations}">
                                    <div>       
                                        <img class="smallIndent" id="chkBxStation${simpleStation.id}" src="/capcontrol/images/nav-plus.gif" onclick="showDiv( 'stationId${simpleStation.id}' );toggleExpandImg( 'chkBxStation${simpleStation.id}');"/>
                                        ${simpleStation.name}
                                        <div class="tableCell" style="display:none" id="stationId${simpleStation.id}">
                                            <c:forEach var="simpleSubBus" items="${simpleStation.substationBuses}">
                                                <div>
                                                    <img class="mediumIndent" id="chkBxSub${simpleSubBus.id}" src="/capcontrol/images/nav-plus.gif" onclick="showDiv( 'subId${simpleSubBus.id}' );toggleExpandImg('chkBxSub${simpleSubBus.id}');"/>
                                                    ${simpleSubBus.name}
                                                    <div class="altTableCell" style="display:none" id="subId${simpleSubBus.id}">
                                                        <c:forEach var="simpleFeeder" items="${simpleSubBus.feeders}">
                                                            <c:if test="${!(simpleFeeder.id == oldFeederId)}">
                                                                <div>
                                                                    <input class="capbankTempMoveLink" type="radio" name="feeder" id="feederId${simpleFeeder.id}" onclick="selectFeeder(${simpleFeeder.id});"/>
                                                                    ${simpleFeeder.name}
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