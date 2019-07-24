<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:choose>
    <c:when test="${lmSetupFilter.filterByType == 'LOAD_GROUP' || lmSetupFilter.filterByType == 'LOAD_PROGRAM'}">
        <cti:url var="filterUrl" value="/dr/setup/filter">
            <cti:param name="name" value="${lmSetupFilter.name}"/>
            <c:forEach var="type" items="${lmSetupFilter.types}">
                <cti:param name="types" value="${type}"/>
            </c:forEach>
            <cti:param name="filterByType" value="${lmSetupFilter.filterByType}"/>
        </cti:url>
        <div id="js-filtered-results-container" data-url="${filterUrl}" data-static>
            <table class="compact-results-table has-actions row-highlighting wrbw" style="table-layout:fixed;">
                <thead>
                    <tr>
                        <tags:sort column="${NAME}"/>
                        <tags:sort column="${TYPE}"/>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="filteredResult" items="${filteredResults.resultList}">
                        <tr>
                            <td>
                                <cti:url var="viewUrl" value="${viewUrlPrefix}${filteredResult.id}"/>
                                <a href="${viewUrl}">${fn:escapeXml(filteredResult.name)}</a>
                            </td>
                            <td><i:inline key="${filteredResult.type}"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <tags:pagingResultsControls result="${filteredResults}" adjustPageCount="true" thousands="true"/>
        </div>
    </c:when>
    <c:otherwise>
        <cti:url var="filterUrl" value="/dr/setup/filter">
            <cti:param name="name" value="${lmSetupFilter.name}"/>
            <cti:param name="filterByType" value="${lmSetupFilter.filterByType}"/>
        </cti:url>
        <div id="js-filtered-results-container" data-url="${filterUrl}" data-static>
            <br>
            <table class="compact-results-table">
                <thead>
                    <tr><tags:sort column="${NAME}"/></tr>
                </thead>
                <tbody>
                    <c:forEach var="filteredResult" items="${filteredResults.resultList}">
                        <tr>
                            <td>
                                <cti:url var="viewUrl" value="${viewUrlPrefix}${filteredResult.id}"/>
                                <a href="${viewUrl}">${fn:escapeXml(filteredResult.name)}</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <tags:pagingResultsControls result="${filteredResults}" adjustPageCount="true" thousands="true"/>
        </div>
    </c:otherwise>
</c:choose>
