<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:msgScope paths="yukon.web.modules.capcontrol.bankMove">

<script type="text/javascript"> 
updateFeederBankInfo = function () {
    var params = {'feederId': $("#selectedFeeder").val()};
    $.ajax({
        url: yukon.url('/capcontrol/move/feederBankInfo'),
        method: 'post',
        data: params
    }).done(function (data, textStatus, jqXHR) {
        $('#controlOrders').html(data);
    });
}

selectFeeder = function (fid) {
    $("#selectedFeeder").val(fid);
    $("#newFeederId").val(fid);
    updateFeederBankInfo();
}

function handleNodeClick(node, event) {
    if (node.getLevel() == 4) {
        node.select();
        selectFeeder(node.data.key);
    } else {
        if (node.getEventTargetType(event) == "title") {
            node.toggleExpand();
            return false; // Prevent bubbling event
        }
    }
}

function getTreeStructure() {
    var areas = [];
    <c:forEach var="area" items="${allAreas}">
        var area = {title: '${cti:escapeJavaScript(area.name)}', icon: false};
        var stations = [];
        <c:forEach var="station" items="${area.substations}">
            var station = {title: '${cti:escapeJavaScript(station.name)}', icon: false};
            var buses = [];
            <c:forEach var="bus" items="${station.substationBuses}">
                var bus = {title: '${cti:escapeJavaScript(bus.name)}', icon: false};
                var feeders = [];
                <c:forEach var="feeder" items="${bus.feeders}">
                    <c:if test="${!(feeder.id == oldFeederId)}">
                        var feeder = {title: '${cti:escapeJavaScript(feeder.name)}', icon: false, key: '${feeder.id}'};
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
    return areas;
}
</script>

<div>
    <cti:url var="moveUrl" value="/capcontrol/command/bankMove"/>
    <form:form modelAttribute="bankMoveBean" action="${moveUrl}">
        <cti:csrfToken/>
        <input type="hidden" name="substationId" value="${substationId}">
        <input type="hidden" name="selectedFeeder" value="${oldFeederId}" id="selectedFeeder">
        <form:hidden path="bankId"/>
        <form:hidden path="oldFeederId"/>
        <form:hidden path="newFeederId" id="newFeederId"/>
        
        <tags:nameValueContainer2 tableClass="stacked">
            <tags:nameValue2 nameKey=".capbank">
                <span>${fn:escapeXml(bankName)}</span>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".currentLocation">
                <span>${fn:escapeXml(path)}</span>
            </tags:nameValue2>
        </tags:nameValueContainer2>

        <h3><i:inline key=".feedersContainer.title"/></h3>

        <jsTree:inlineTree id="feederTree" maxHeight="250" styleClass="stacked" 
            treeParameters="{children: getTreeStructure(),
                             minExpandLevel:1,
                             clickFolderMode: 3,
                             onClick: handleNodeClick,
                             selectMode: 1}"/>

        <div id="controlOrders"></div>
        
        <div class="action-area">
            <c:if test="${!isIVVC}">
                <cti:button nameKey="tempMove" type="submit" name="tempMove"/>
            </c:if>
            <cti:button nameKey="move" type="submit" name="move"/>
            <span><i:inline key=".controlOrder"/>&nbsp;<form:input path="displayOrder" size="1" maxlength="3"/></span>
            <span><i:inline key=".closeOrder"/>&nbsp;<form:input path="closeOrder" size="1" maxlength="3"/></span>
            <span><i:inline key=".tripOrder"/>&nbsp;<form:input path="tripOrder" size="1" maxlength="3"/></span>
        </div>
    </form:form>
</div>
<script type="text/javascript">
updateFeederBankInfo();
</script>
</cti:msgScope>