<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="capcontrol" page="strategies">

    <jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>
    <%@include file="/capcontrol/capcontrolHeader.jspf" %>

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
                <table class="compact-results-table" id="strategyTable">
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
                        <c:forEach var="item" items="${searchResult.resultList}">
                            <tr id="s_${item.strategyId}">
                                <td>
                                    <cti:url var="editUrl" value="/editor/cbcBase.jsf">
                                        <cti:param name="type" value="5"/>
                                        <cti:param name="itemid" value="${item.strategyId}"/>
                                    </cti:url>
                                    <a href="${editUrl}">
                                        ${fn:escapeXml(item.strategyName)}
                                    </a>
                                </td>
                                <td>${fn:escapeXml(item.controlMethod)}</td>
                                <td>${fn:escapeXml(item.controlUnits)}</td>
                                <td>
                                    <cti:list var="arguments">
                                        <cti:item value="${item.peakStartTime}"/>
                                        <cti:item value="${item.peakStopTime}"/>
                                    </cti:list>
                                    <i:inline key=".startStopTimes" arguments="${arguments}"/>
                                </td>
                                <td>${fn:escapeXml(item.controlInterval)}</td>
                                <td>${fn:escapeXml(item.minResponseTime)}</td>
                                <td>${fn:escapeXml(item.passFailPercent)}</td>
                                <td>${fn:escapeXml(item.peakSettings)}</td>
                                <td>${fn:escapeXml(item.offPeakSettings)}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </tags:pagedBox2>
</cti:standardPage>