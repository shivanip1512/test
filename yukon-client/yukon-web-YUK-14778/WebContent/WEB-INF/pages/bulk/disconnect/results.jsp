<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.disconnect.results.recent">

    <c:if test="${empty results}">
        <p><i:inline key="yukon.common.search.noResultsFound"/></p>
    </c:if>
    <c:if test="${not empty results}">
        <table class="compact-results-table">
            <thead>
                <tr>
                    <th><i:inline key=".bulk.disconnect.results.recent.command"/></th>
                    <th><i:inline key=".bulk.disconnect.results.recent.devices"/></th>
                    <th class="tar"><i:inline key=".bulk.disconnect.results.recent.success"/></th>
                    <th class="tar"><i:inline key=".bulk.disconnect.results.recent.failed"/></th>
                    <th><i:inline key=".bulk.disconnect.results.recent.startTime"/></th>
                    <th class="tar"><i:inline key=".bulk.disconnect.results.recent.notAttempted"/></th>
                    <th><i:inline key=".bulk.disconnect.results.recent.status"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="result" items="${results}">
                    <cti:formatDate var="startDate" type="BOTH" value="${result.commandRequestExecution.startTime}"/>
                    <cti:url var="detailUrl" value="/bulk/disconnect/resultDetail">
                        <cti:param name="resultKey" value="${result.key}"/>
                        <cti:param name="command" value="${result.command}"/>
                    </cti:url>
                    <tr>
                        <td><a href="${detailUrl}"><i:inline key="${result.command}"/></a></td>
                        <td><i:inline key="${result.allDevicesCollection.description}"/></td>
                        <td class="tar"><cti:dataUpdaterValue type="DISCONNECT" identifier="${result.key}/SUCCESS_COUNT"/></td>
                        <td class="tar"><cti:dataUpdaterValue type="DISCONNECT" identifier="${result.key}/FAILED_COUNT"/></td>
                        <td>${startDate}</td>
                        <td class="tar"><cti:dataUpdaterValue type="DISCONNECT" identifier="${result.key}/NOT_ATTEMPTED_COUNT"/></td>
                        <td>
                            <div id="statusDiv_${result.key}">
                                <cti:classUpdater type="DISCONNECT" identifier="${result.key}/STATUS_CLASS">
                                    <cti:dataUpdaterValue type="DISCONNECT" identifier="${result.key}/STATUS_TEXT"/>
                                </cti:classUpdater>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</cti:standardPage>