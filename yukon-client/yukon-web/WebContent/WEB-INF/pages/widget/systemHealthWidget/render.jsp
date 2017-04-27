<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<c:if test="${showSystemHealth}">
    <div id="systemHealthTable">
        <cti:msgScope paths="modules.support.systemHealth">
            <c:if test="${not empty extendedQueueData}">
                <table class="compact-results-table stacked-md">
                    <thead>
                        <th><i:inline key=".queue"/></th>
                        <th class="tar"><i:inline key=".archivedReadings"/></th>
                        <th class="tar"><i:inline key=".processedArchiveRequests"/></th>
                        <th class="tar"><i:inline key=".enqueuedCount"/></th>
                        <th class="tar"><i:inline key=".dequeuedCount"/></th>
                        <th class="tar"><i:inline key=".queueSize"/></th>
                        <th class="tar"><i:inline key=".averageEnqueueTime"/></th>
                        <th><i:inline key=".status"/></th>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="queue" items="${extendedQueueData}">
                            <tr>
                                <td class="wsnw">
                                    <cti:button id="favMetricButton" renderMode="image" icon="icon-star" 
                                                data-metric="${queue.metricIdentifier}" data-action="unfavorite"
                                                nameKey="unfavMetricButton"/>
                                    <span title="${queue.queueName}">
                                        <cti:url var="detailUrl" value="/support/systemHealth/${queue.metricIdentifier}/detail"/>
                                        <a href="${detailUrl}">
                                            <i:inline key="${queue}"/>
                                        </a>
                                    </span>
                                </td>
                                <td class="tar" id="${queue.metricIdentifier}-arc">${queue.archivedReadingsCount}</td>
                                <td class="tar" id="${queue.metricIdentifier}-arp">${queue.archiveRequestsProcessed}</td>
                                <td class="tar" id="${queue.metricIdentifier}-enq">${queue.enqueuedCount}</td>
                                <td class="tar" id="${queue.metricIdentifier}-deq">${queue.dequeuedCount}</td>
                                <td class="tar" id="${queue.metricIdentifier}-size">${queue.queueSize}</td>
                                <td class="tar" id="${queue.metricIdentifier}-avg">${queue.averageEnqueueTime}</td>
                                <td id="${queue.metricIdentifier}-status"><cti:icon icon="${queue.status.iconName}" title="${queue.status.allMessages}"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
            <c:if test="${not empty queueData}">
                <table class="compact-results-table stacked-md">
                    <thead>
                        <th><i:inline key=".queue"/></th>
                        <th class="tar"><i:inline key=".enqueuedCount"/></th>
                        <th class="tar"><i:inline key=".dequeuedCount"/></th>
                        <th class="tar"><i:inline key=".queueSize"/></th>
                        <th class="tar"><i:inline key=".averageEnqueueTime"/></th>
                        <th><i:inline key=".status"/></th>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="queue" items="${queueData}">
                            <tr>
                                <td class="wsnw">
                                    <cti:button id="favMetricButton" renderMode="image" icon="icon-star" 
                                                data-metric="${queue.metricIdentifier}" data-action="unfavorite"
                                                nameKey="unfavMetricButton"/>
                                    <span title="${queue.queueName}">
                                        <cti:url var="detailUrl" value="/support/systemHealth/${queue.metricIdentifier}/detail"/>
                                        <a href="${detailUrl}">
                                            <i:inline key="${queue}"/>
                                        </a>
                                    </span>
                                </td>
                                <td class="tar" id="${queue.metricIdentifier}-enq">${queue.enqueuedCount}</td>
                                <td class="tar" id="${queue.metricIdentifier}-deq">${queue.dequeuedCount}</td>
                                <td class="tar" id="${queue.metricIdentifier}-size">${queue.queueSize}</td>
                                <td class="tar" id="${queue.metricIdentifier}-avg">${queue.averageEnqueueTime}</td>
                                <td id="${queue.metricIdentifier}-status"><cti:icon icon="${queue.status.iconName}" title="${queue.status.allMessages}"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </cti:msgScope>
    </div>
</c:if>
<style>
#systemHealthTable {
    overflow-x: scroll;
}
</style>
<cti:includeScript link="/resources/js/pages/yukon.support.systemHealth.js"/>
