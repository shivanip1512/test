<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.support.systemHealth">
    <div id="systemMessagingTable">
        <c:if test="${not empty extendedQueueData}">
            <table class="compact-results-table stacked-md">
                <thead>
                    <th class="vab"></th>
                    <th class="vab"><i:inline key=".queue"/></th>
                    <th class="tar vab"><i:inline key=".archivedReadings"/></th>
                    <th class="tar vab"><i:inline key=".processedArchiveRequests"/></th>
                    <th class="tar vab"><i:inline key=".enqueuedCount"/></th>
                    <th class="tar vab"><i:inline key=".dequeuedCount"/></th>
                    <th class="tar vab"><i:inline key=".queueSize"/></th>
                    <th class="tar vab"><i:inline key=".averageEnqueueTime"/></th>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="queue" items="${extendedQueueData}">
                        <tr>
                            <td id="${queue.metricIdentifier}-status"><cti:icon icon="${queue.status.iconName}" title="${queue.status.allMessages}"/></td>
                            <td class="wsnw">
                                <span title="${queue.queueName}">
                                    <cti:url var="detailUrl" value="/support/systemHealth/${queue.metricIdentifier}/detail"/>
                                    <a href="${detailUrl}" target="_blank">
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
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
        <c:if test="${not empty queueData}">
            <table class="compact-results-table stacked-md">
                <thead>
                    <th class="vab"></th>
                    <th class="vab"><i:inline key=".queue"/></th>
                    <th class="tar vab"><i:inline key=".enqueuedCount"/></th>
                    <th class="tar vab"><i:inline key=".dequeuedCount"/></th>
                    <th class="tar vab"><i:inline key=".queueSize"/></th>
                    <th class="tar vab"><i:inline key=".averageEnqueueTime"/></th>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="queue" items="${queueData}">
                        <tr>
                            <td id="${queue.metricIdentifier}-status"><cti:icon icon="${queue.status.iconName}" title="${queue.status.allMessages}"/></td>
                            <td class="wsnw">
                                <span title="${queue.queueName}">
                                    <cti:url var="detailUrl" value="/support/systemHealth/${queue.metricIdentifier}/detail"/>
                                    <a href="${detailUrl}" target="_blank">
                                        <i:inline key="${queue}"/>
                                    </a>
                                </span>
                            </td>
                            <td class="tar" id="${queue.metricIdentifier}-enq">${queue.enqueuedCount}</td>
                            <td class="tar" id="${queue.metricIdentifier}-deq">${queue.dequeuedCount}</td>
                            <td class="tar" id="${queue.metricIdentifier}-size">${queue.queueSize}</td>
                            <td class="tar" id="${queue.metricIdentifier}-avg">${queue.averageEnqueueTime}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>
</cti:msgScope>

<style>
#systemMessagingTable {
    overflow-x: scroll;
}
</style>
