<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="capcontrol" page="strategies">

    <jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>
    <%@ include file="/capcontrol/capcontrolHeader.jspf" %>

    <c:choose>
        <c:when test="${fn:length(strategies) == 0}">
            <span class="empty-list"><i:inline key=".noStrategies"/></span>
        </c:when>
        <c:otherwise>
            <table class="compact-results-table" id="strategy-table">
                <thead>
                    <tr>
                        <th><i:inline key=".strategyName"/></th>
                        <th><i:inline key=".method"/></th>
                        <th><i:inline key=".algorithm"/></th>
                        <th colspan="2"><i:inline key=".startStop"/></th>
                        <th><i:inline key=".interval"/></th>
                        <th><i:inline key=".confirmTime"/></th>
                        <th colspan="2"><i:inline key=".passFail"/></th>
                        <th><i:inline key=".peakSetting"/></th>
                        <th><i:inline key=".offPeakSetting"/></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="strategy" items="${strategies}">
                        <tr data-strategy-id="${strategy.id}">
                            <td>
                                <cti:checkGlobalRolesAndProperties value="DEVELOPMENT_MODE">
                                    <cti:url var="url" value="/capcontrol/strategies/${strategy.id}" />
                                    <a href="${url}">New Editor</a>
                                    <br>
                                </cti:checkGlobalRolesAndProperties>
                                
                                <cti:url var="editUrl" value="/editor/cbcBase.jsf">
                                    <cti:param name="type" value="5"/>
                                    <cti:param name="itemid" value="${strategy.id}"/>
                                </cti:url>
                                <a href="${editUrl}">${fn:escapeXml(strategy.name)}</a>
                            </td>
                            <td><i:inline key="${strategy.controlMethod}"/></td>
                            <td><i:inline key="${strategy.algorithm}"/></td>
                            <td>
                                <cti:formatDate value="${strategy.peakStartTime}" type="TIME"/>
                            </td>
                            <td>
                                <cti:formatDate value="${strategy.peakStopTime}" type="TIME"/>
                            </td>
                            <td><cti:formatDuration type="MS_ABBR" value="${strategy.controlInterval * 1000}"/></td>
                            <td><cti:formatDuration type="MS_ABBR" value="${strategy.minResponseTime * 1000}"/></td>
                            
                            <td><i:inline key="yukon.common.percent" arguments="${strategy.minConfirmPercent}"/></td>
                            <td><i:inline key="yukon.common.percent" arguments="${strategy.failurePercent}"/></td>
                            
                            <td>
                                <cti:list var="peakArgs">
                                    <c:forEach var="arg" items="${settingArgs}">
                                        <cti:item>${strategy.targetSettings[arg].peakValue}</cti:item>
                                    </c:forEach>
                                </cti:list>
                                
                                <cti:msg2 key=".controlAlgorithm.settings.${strategy.algorithm}" arguments="${peakArgs}" htmlEscape="true"/>
                            </td>
                            <td>
                                <cti:list var="offPeakArgs">
                                    <c:forEach var="arg" items="${settingArgs}">
                                        <cti:item>${strategy.targetSettings[arg].offPeakValue}</cti:item>
                                    </c:forEach>
                                </cti:list>
                                
                                <i:inline key=".controlAlgorithm.settings.${strategy.algorithm}" arguments="${offPeakArgs}" htmlEscape="true"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</cti:standardPage>