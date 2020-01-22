<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:url var="filterUrl" value="/dr/setup/filter">
    <cti:param name="name" value="${lmSetupFilter.name}"/>
    <c:forEach var="gearType" items="${lmSetupFilter.gearTypes}">
        <cti:param name="gearTypes" value="${gearType}"/>
    </c:forEach>
    <c:forEach var="programId" items="${lmSetupFilter.programIds}">
        <cti:param name="programIds" value="${programId}"/>
    </c:forEach>
    <cti:param name="filterByType" value="${lmSetupFilter.filterByType}"/>
</cti:url> 
<div id="js-filtered-gear-results-container" data-url="${filterUrl}" data-static>
    <table class="compact-results-table row-highlighting wrbw" style="table-layout:fixed;">
        <thead>
            <tr>
                <tags:sort column="${NAME}" width="15%"/>
                <tags:sort column="${TYPE}" width="15%"/>
                <tags:sort column="${ORDER}" width="5%"/>
                <tags:sort column="${PROGRAM}" width="15%"/>
                <th width="60%"><i:inline key="yukon.common.details"/></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="filteredResult" items="${filteredResults.resultList}">
                <tr>
                    <td>
                        <cti:url var="viewUrl" value="/dr/setup/loadProgram/programGear/${filteredResult.gearId}"/> 
                        <a href="${viewUrl}" data-gear-id="${filteredResult.gearId}" data-program-id="${filteredResult.loadProgram.id}" class="js-gear-link">
                            ${fn:escapeXml(filteredResult.gearName)}
                        </a>
                    </td>
                    <td>
                        <i:inline key="${filteredResult.controlMethod}"/>
                    </td>
                    <td>${filteredResult.gearNumber}</td>
                    <td>
                        <cti:url var="viewUrl" value="/dr/setup/loadProgram/${filteredResult.loadProgram.id}"/>
                        <a href="${viewUrl}">${fn:escapeXml(filteredResult.loadProgram.name)}</a>
                    </td>
                    <td>${filteredResult.gearDetails}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <tags:pagingResultsControls result="${filteredResults}" adjustPageCount="true" thousands="true"/>
</div>
