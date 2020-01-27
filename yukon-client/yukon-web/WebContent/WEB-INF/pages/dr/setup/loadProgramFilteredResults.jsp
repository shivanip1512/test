<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:url var="filterUrl" value="/dr/setup/filter">
    <cti:param name="filterByType" value="${lmSetupFilter.filterByType}" />
    <cti:param name="name" value="${lmSetupFilter.name}" />
    <c:forEach var="type" items="${lmSetupFilter.types}">
        <cti:param name="types" value="${type}" />
    </c:forEach>
    <c:forEach var="operationalState" items="${lmSetupFilter.operationalStates}">
        <cti:param name="operationalStates" value="${operationalState}" />
    </c:forEach>
</cti:url>

<div data-url="${filterUrl}" data-static>
    <table class="compact-results-table row-highlighting wrbw" style="table-layout: fixed;">
        <thead>
            <tr>
                <tags:sort column="${NAME}" width="15%"/>
                <tags:sort column="${TYPE}" width="15%"/>
                <tags:sort column="${OPERATIONAL_STATE}" width="15%"/>
                <tags:sort column="${CONSTRAINT}" width="15%"/>
                <th style="width: 20%"><i:inline key="yukon.web.modules.dr.setup.GEAR" /></th>
                <th style="width: 20%"><i:inline key="yukon.web.modules.dr.setup.LOAD_GROUP" /></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="filteredResult" items="${filteredResults.resultList}">
                <tr>
                    <td>
                        <cti:url var="viewUrl" value="${viewUrlPrefix}${filteredResult.program.id}" />
                        <a href="${viewUrl}"> ${fn:escapeXml(filteredResult.program.name)} </a>
                    </td>
                    <td>
                        <i:inline key="${filteredResult.type}" />
                    </td>
                    <td>
                        <i:inline key="${filteredResult.operationalState}" />
                    </td>
                    <td>
                        <cti:url var="viewUrl" value="/dr/setup/constraint/${filteredResult.constraint.constraintId}" />
                        <a href="${viewUrl}"> ${fn:escapeXml(filteredResult.constraint.constraintName)} </a>
                    </td>
                    <td>
                        ${fn:escapeXml(gearsForProgram[filteredResult.program.id])}
                    </td>
                    <td>
                        ${fn:escapeXml(loadGroupsForProgram[filteredResult.program.id])}
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <tags:pagingResultsControls result="${filteredResults}" adjustPageCount="true" thousands="true"/>
</div>
