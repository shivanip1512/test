<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:url var="filterUrl" value="/dr/setup/filterLoadGroups">
    <cti:param name="name" value="${loadGroupBase.name}"/>
    <cti:param name="type" value="${loadGroupBase.type}"/>
</cti:url>
<div id="js-load-groups-filter-container" data-url="${filterUrl}" data-static>
    <br>
    <table class="compact-results-table has-actions row-highlighting wrbw" style="table-layout:fixed;">
        <thead>
            <tr>
                <tags:sort width="30%" column="${NAME}"/>
                <tags:sort width="20%" column="${SWITCH_TYPE}"/>
                <th style="width: 50%"><i:inline key="yukon.common.details"/></th>
            </tr>
        </thead>
        <tbody>
             <c:forEach var="loadGroup" items="${filteredResults.resultList}">
                <tr>
                    <td>
                        <cti:url var="viewUrl" value="/dr/setup/loadGroup/${loadGroup.id}"/>
                        <a href="${viewUrl}">${fn:escapeXml(loadGroup.name)}</a>
                    </td>
                    <td><i:inline key="${loadGroup.type}"/></td>
                    <td>${loadGroupDetails[loadGroup.id]}</td>
                </tr>
             </c:forEach>
        </tbody>
    </table>
    <tags:pagingResultsControls result="${filteredResults}" adjustPageCount="true" thousands="true"/>
</div>