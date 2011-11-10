<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="capcontrol" page="strategies">

<script type="text/javascript" language="JavaScript">
var firstRun = true;
function removeStrategy(strategyId, event) {
    var confirmDeleteMsg = event.findElement().next('span.confirmDelete').innerHTML;
    
    if (confirm(confirmDeleteMsg)) {
        var url = "/spring/capcontrol/strategy/deleteStrategy";

        hideErrors();
        
        new Ajax.Request(url, {'parameters': {'strategyId': strategyId}, 
            onComplete: function(transport, json) {
                $('deletionResult').removeClassName('okGreen');
                $('deletionResult').removeClassName('errorRed');
                if (json.success) {
                    deleteStrategyFromPage(strategyId);
                    $('deletionResult').addClassName('okGreen');
                } else {
                    $('deletionResult').addClassName('errorRed');
                }
                $('deletionResult').innerHTML = json.resultText;
                $('deletionResult').show();
            
            } });
    }
}

function hideErrors() {
    
    if(!firstRun) { 
        $('deletionResult').hide();
    }
    firstRun = false;
}

function deleteStrategyFromPage(strategyId) {
    var line = document.getElementById('s_' + strategyId);
    var table = document.getElementById('tableBody');

    table.removeChild(line);
}
</script>
    <div id="deletionResult" class="padded normalBoldLabel"></div>
    
    <tags:pagedBox2 nameKey="strategyContainer" 
            searchResult="${searchResult}"
            baseUrl="/spring/capcontrol/strategy/strategies"
            isFiltered="false" 
            showAllUrl="/spring/capcontrol/strategy/strategies"
            styleClass="padBottom">
        
        <c:choose>
            <c:when test="${searchResult.hitCount == 0}">
                <i:inline key=".noStrategies"/>
            </c:when>
            <c:otherwise>
                <table class="compactResultsTable" id="strategyTable">
                    
                    <thead>
                        <tr id="header">
                            <th><i:inline key=".strategyName"/></th>
                            <th><i:inline key=".actions"/></th>
                            <th><i:inline key=".method"/></th>
                            <th><i:inline key=".algorithm"/></th>
                            <th><i:inline key=".startStop"/></th>
                            <th><i:inline key=".interval"/></th>
                            <th><i:inline key=".confirmTime"/></th>
                            <th><i:inline key=".passFail"/></th>
                            <th><i:inline key=".peakSetting"/></th>
                            <th><i:inline key=".offPeakSetting"/></th>
                        </tr>
                    </thead>
                    
                    <tbody id="tableBody">
                        <c:forEach var="item" items="${strategies}">
                            <tr class="<tags:alternateRow odd="" even="altRow"/>" id="s_${item.strategyId}">
                                
                                <td>
                                    <spring:escapeBody htmlEscape="true">${item.strategyName}</spring:escapeBody>
                                </td>
                    
                                <td class="nw">
                                    <c:choose>
                                        <c:when test="${hasEditingRole}">
                                            <cti:button nameKey="edit" renderMode="image" href="/editor/cbcBase.jsf?type=5&itemid=${item.strategyId}"/>
                                            <cti:button nameKey="remove" renderMode="image" onclick="removeStrategy(${item.strategyId}, event)"/>
                                            <span class="dn confirmDelete"><i:inline key=".confirmDelete" arguments="${item.strategyName}"/></span>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="/editor/cbcBase.jsf?type=3&itemid=${item.strategyId}" class="tierIconLink">
                                                <img class="tierImg" src="/WebConfig/yukon/Icons/information.gif" />
                                            </a>
                                            <cti:button nameKey="info" renderMode="image" href="/editor/cbcBase.jsf?type=3&itemid=${item.strategyId}"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                    
                                <td><spring:escapeBody htmlEscape="true">${item.controlMethod}</spring:escapeBody></td>
                    
                                <td><spring:escapeBody htmlEscape="true">${item.controlUnits}</spring:escapeBody></td>
                    
                                <td>
                                    <i:inline key=".startStopTimes" arguments="${item.peakStartTime},${item.peakStopTime}" argumentSeparator=","/>
                                </td>
                    
                                <td><spring:escapeBody htmlEscape="true">${item.controlInterval}</spring:escapeBody></td>
                    
                                <td><spring:escapeBody htmlEscape="true">${item.minResponseTime}</spring:escapeBody></td>
                    
                                <td><spring:escapeBody htmlEscape="true">${item.passFailPercent}</spring:escapeBody></td>
                    
                                <td><spring:escapeBody htmlEscape="true">${item.peakSettings}</spring:escapeBody></td>
                    
                                <td><spring:escapeBody htmlEscape="true">${item.offPeakSettings}</spring:escapeBody></td>
                                
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </tags:pagedBox2>
</cti:standardPage>