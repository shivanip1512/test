<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:url var="filterUrl" value="/dr/setup/filter">
    <cti:param name="name" value="${lmSetupFilter.name}"/>
    <c:forEach var="type" items="${lmSetupFilter.types}">
        <cti:param name="types" value="${type}"/>
    </c:forEach>
    <cti:param name="filterByType" value="${lmSetupFilter.filterByType}"/>
</cti:url>
<div data-url="${filterUrl}" data-static>
    <table class="compact-results-table row-highlighting wrbw" style="table-layout:fixed;">
        <thead>
            <tr>
                <tags:sort column="${NAME}" width="30%"/>
                <th width="60%"><i:inline key="yukon.web.modules.dr.setup.programs"/></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="filteredResult" items="${filteredResults.resultList}">
                <tr>
                    <td>
                        <cti:url var="viewUrl" value="/dr/setup/controlScenario/${filteredResult.scenario.id}"/>
                        <a href="${viewUrl}">${fn:escapeXml(filteredResult.scenario.name)}</a>
                    </td>
                    <td>
                        ${fn:escapeXml(loadProgramForScenario[filteredResult.scenario.id])}
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <tags:pagingResultsControls result="${filteredResults}" adjustPageCount="true" thousands="true"/>
</div>