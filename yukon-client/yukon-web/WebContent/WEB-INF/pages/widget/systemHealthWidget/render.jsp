<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<div id="systemHealthTable">
    <table class="compact-results-table stacked-md">
        <thead>
            <th><i:inline key="yukon.web.modules.support.systemHealth.queue"/></th>
            <th class="tar"><i:inline key="yukon.web.modules.support.systemHealth.archivedReadings"/></th>
            <th class="tar"><i:inline key="yukon.web.modules.support.systemHealth.processedArchiveRequests"/></th>
            <th class="tar"><i:inline key="yukon.web.modules.support.systemHealth.enqueuedCount"/></th>
            <th class="tar"><i:inline key="yukon.web.modules.support.systemHealth.dequeuedCount"/></th>
            <th class="tar"><i:inline key="yukon.web.modules.support.systemHealth.queueSize"/></th>
            <th class="tar"><i:inline key="yukon.web.modules.support.systemHealth.averageEnqueueTime"/></th>
            <th><i:inline key="yukon.web.modules.support.systemHealth.status"/></th>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="queue" items="${extendedQueueData}">
                <tr>
                    <td class="wsnw">
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
    
    <table class="compact-results-table stacked-md">
        <thead>
            <th><i:inline key="yukon.web.modules.support.systemHealth.queue"/></th>
            <th class="tar"><i:inline key="yukon.web.modules.support.systemHealth.enqueuedCount"/></th>
            <th class="tar"><i:inline key="yukon.web.modules.support.systemHealth.dequeuedCount"/></th>
            <th class="tar"><i:inline key="yukon.web.modules.support.systemHealth.queueSize"/></th>
            <th class="tar"><i:inline key="yukon.web.modules.support.systemHealth.averageEnqueueTime"/></th>
            <th><i:inline key="yukon.web.modules.support.systemHealth.status"/></th>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="queue" items="${queueData}">
                <tr>
                    <td class="wsnw">
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
</div>
<style>
#systemHealthTable {
    overflow-x: scroll;
}
</style>