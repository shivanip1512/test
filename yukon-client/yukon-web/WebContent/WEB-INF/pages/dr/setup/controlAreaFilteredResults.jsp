<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:url var="filterUrl" value="/dr/setup/filter">
    <cti:param name="name" value="${lmSetupFilter.name}"/>
    <cti:param name="filterByType" value="${lmSetupFilter.filterByType}"/>
</cti:url>

<div data-url="${filterUrl}" data-static>
    <table class="compact-results-table row-highlighting wrbw" style="table-layout:fixed;">
        <thead>
            <tr>
                <tags:sort column="${NAME}"/>
                <th><i:inline key="yukon.web.modules.dr.setup.controlArea.trigger.title"/></th>
                <th><i:inline key="yukon.web.modules.dr.setup.programs"/></th>
            </tr>
        </thead>
        <tbody>
             <c:forEach var="filteredResult" items="${filteredResults.resultList}">
                <tr>
                    <td>
                        <cti:url var="viewUrl" value="${viewUrlPrefix}${filteredResult.controlAreaId}"/>
                        <a href="${viewUrl}">${fn:escapeXml(filteredResult.controlAreaName)}</a>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${empty filteredResult.triggers || fn:length(filteredResult.triggers) == 0}">
                                <i:inline key="yukon.common.none.choice"/>
                            </c:when>
                            <c:otherwise>
                                <cti:triggerName pointId="${filteredResult.triggers[0].triggerPointId}" type="${filteredResult.triggers[0].triggerType}"/>
                                <c:if test="${fn:length(filteredResult.triggers) >1}">
                                     <i:inline key="yukon.common.comma"/>
                                     <cti:triggerName pointId="${filteredResult.triggers[1].triggerPointId}" type="${filteredResult.triggers[1].triggerType}"/>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        ${programsForControlArea[filteredResult.controlAreaId]}
                    </td>
                </tr>
             </c:forEach>
        </tbody>
    </table>
    <tags:pagingResultsControls result="${filteredResults}" adjustPageCount="true" thousands="true"/>
</div>