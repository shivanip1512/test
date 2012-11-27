<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree"%>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:msgScope paths="yukon.web.modules.capcontrol.bankMove">

<cti:url var="controlOrderPage" value="/capcontrol/move/feederBankInfo"/>

<script type="text/javascript"> 
updateFeederBankInfo = function () {
    var params = {'feederId': jQuery("#selectedFeeder").val()};
    jQuery.ajax({
        url: '${controlOrderPage}',
        method: 'post',
        data: params,
        success: function(data) {
            jQuery('#controlOrders').html(data);
        }
    });
}

selectFeeder = function (fid) {
    jQuery("#selectedFeeder").val(fid);
    jQuery("#newFeederId").val(fid);
    updateFeederBankInfo();
}

function handleNodeClick(node, event) {
    if (node.getLevel() == 4) {
        selectFeeder(node.data.key);
    } else {
        if (node.getEventTargetType(event) == "title") {
            node.toggleExpand();
            return false; // Prevent bubbling event
        }
    }
}

treeInit = function() {
    
    var areas = [];
    <c:forEach var="area" items="${allAreas}">
        var area = {title: '${area.name}', icon: false};
        var stations = [];
        <c:forEach var="station" items="${area.substations}">
            var station = {title: '${station.name}', icon: false};
            var buses = [];
            <c:forEach var="bus" items="${station.substationBuses}">
                var bus = {title: '${bus.name}', icon: false};
                var feeders = [];
                <c:forEach var="feeder" items="${bus.feeders}">
                    <c:if test="${!(feeder.id == oldFeederId)}">
                        var feeder = {title: '${feeder.name}', icon: false, key: '${feeder.id}'};
                        feeders.push(feeder);
                    </c:if>
                </c:forEach>
                bus.children = feeders;
                buses.push(bus);
            </c:forEach>
            station.children = buses;
            stations.push(station);
        </c:forEach>
        area.children = stations;
        areas.push(area);
    </c:forEach>
        
    jQuery("div.bankMoveContainer div.boxContainer_content").dynatree({children: areas, keyboard: true, clickFolderMode: 3, selectMode: 1, onClick: handleNodeClick});
}
</script>

<div style="padding: 5px;">
    <form:form commandName="bankMoveBean" action="/capcontrol/command/bankMove">
        <input type="hidden" name="oneline" value="${oneline}">
        <input type="hidden" name="substationId" value="${substationId}">
        <input type="hidden" name="selectedFeeder" value="${oldFeederId}" id="selectedFeeder">
        <form:hidden path="bankId"/>
        <form:hidden path="oldFeederId"/>
        <form:hidden path="newFeederId" id="newFeederId"/>
        
        <tags:nameValueContainer2 tableClass="marginBottomSmall">
            <tags:nameValue2 nameKey=".capbank">
                <span style="font-size: 12px;"><spring:escapeBody htmlEscape="true">${bankName}</spring:escapeBody></span>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".currentLocation">
                <span style="font-size: 12px;"><spring:escapeBody htmlEscape="true">${path}</spring:escapeBody></span>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    
        <tags:boxContainer2 nameKey="feedersContainer" hideEnabled="false" styleClass="padBottom bankMoveContainer">
            <jsTree:inlineTree id="feederTree" width="500" height="300" treeParameters="{onPostInit: treeInit()}"/>
        </tags:boxContainer2>

        <div id="controlOrders" class="padBottom clear"></div>
        <div style="float:right;">
            <cti:button nameKey="tempMove" type="submit" name="tempMove"/>
            <cti:button nameKey="move" type="submit" name="move"/>
        </div>
        <div style="float:left;">
            <span><i:inline key=".displayOrder"/><form:input path="displayOrder" size="1" maxlength="3" cssStyle="margin-left:3px;"/></span>
            <span><i:inline key=".closeOrder"/><form:input path="closeOrder" size="1" maxlength="3"  cssStyle="margin-left:3px;"/></span>
            <span><i:inline key=".tripOrder"/><form:input path="tripOrder" size="1" maxlength="3"  cssStyle="margin-left:3px;"/></span>
        </div>
    </form:form>
</div>
<script type="text/javascript">
updateFeederBankInfo();
</script>
</cti:msgScope>