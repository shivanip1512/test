<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="analysis.tabular">

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle" />
        <cti:crumbLink url="/spring/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle" />
        <cti:crumbLink url="/spring/bulk/deviceSelection" title="${deviceSelectionPageTitle}" />
        <%-- collection actions --%>
        <tags:collectionActionsCrumbLink deviceCollection="${deviceCollection}" />
        <%-- archive data analysis results--%>
        <cti:msg var="analysisResultsPageTitle" key="yukon.web.modules.amr.analysis.results.pageName" />
        <cti:crumbLink url="/spring/bulk/archiveDataAnalysis/results?analysisId=${analysis.analysisId}" title="${analysisResultsPageTitle}"/>
        <%-- archive data analysis tabular --%>
        <cti:crumbLink><i:inline key="yukon.web.modules.amr.analysis.tabular.pageName"/></cti:crumbLink>

    </cti:breadCrumbs>
    
    <table class="resultsTable tabular">
        <th></th>
        <c:forEach var="dateTime" items="${dateTimeList}">
            <th>
                <cti:formatDate type="DATEHM" value="${dateTime}"/>
            </th>
        </c:forEach>
        <c:forEach var="devicePointValues" items="${devicePointValuesList}">
            <tr>
                <td class="tabular">
                ${devicePointValues.deviceName}
                </td>
                <c:forEach var="pointValue" items="${devicePointValues.pointValues}">
                    <td>
                        <c:choose>
                            <c:when test="${empty pointValue}">
                                <i:inline key="yukon.web.defaults.dashesTwo"/>
                            </c:when>
                            <c:otherwise>
                                ${pointValue}
                            </c:otherwise>
                        </c:choose>
                    </td>
                </c:forEach>
            </tr>
        </c:forEach>
    </table> 
</cti:standardPage>