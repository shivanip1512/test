<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="capcontrol" page="strategies">

<form id="deleteForm" action="deleteStrategy">
    <input id="deleteStrategyId" type="hidden" name="strategyId"> 
</form>

    <div id="deletionResult" class="padded normalBoldLabel"></div>
    
    <tags:pagedBox2 nameKey="strategyContainer" 
            searchResult="${searchResult}"
            baseUrl="/capcontrol/strategy/strategies"
            isFiltered="false" 
            showAllUrl="/capcontrol/strategy/strategies"
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
                                    <a href="/editor/cbcBase.jsf?type=5&itemid=${item.strategyId}">
                                        <spring:escapeBody htmlEscape="true">${item.strategyName}</spring:escapeBody>
                                    </a>
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