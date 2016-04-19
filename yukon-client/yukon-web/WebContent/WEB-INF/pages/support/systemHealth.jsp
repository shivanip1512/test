<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="support" page="systemHealth">
    
    <tags:sectionContainer2 nameKey="serverBrokerStatistics">
        <table class="compact-results-table stacked-md">
            <thead>
                <th><i:inline key=".queue"/></th>
                <th class="tar"><i:inline key=".archivedReadings"/></th>
                <th class="tar"><i:inline key=".processedArchiveRequests"/></th>
                <th class="tar"><i:inline key=".enqueuedCount"/></th>
                <th class="tar"><i:inline key=".dequeuedCount"/></th>
                <th class="tar"><i:inline key=".queueSize"/></th>
                <th class="tar"><i:inline key=".averageEnqueueTime"/></th>
                <%-- <th><i:inline key=".status"/></th> --%>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="queue" items="${extendedQueueData}">
                    <tr>
                        <td class="wsnw">
                            <c:choose>
                                <c:when test="${favorites[queue.metricIdentifier]}">
                                    <c:set var="favIcon" value="icon-star"/>
                                    <c:set var="action" value="unfavorite"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="favIcon" value="icon-favorite-not"/>
                                    <c:set var="action" value="favorite"/>
                                </c:otherwise>
                            </c:choose>
                            <cti:button id="favMetricButton" renderMode="image" icon="${favIcon}" data-metric="${queue.metricIdentifier}" data-action="${action}"/>
                            <span title="${queue.queueName}">
                                <i:inline key="${queue}"/>
                            </span>
                        </td>
                        <td class="tar" id="${queue.metricIdentifier}-arc">${queue.archivedReadingsCount}</td>
                        <td class="tar" id="${queue.metricIdentifier}-arp">${queue.archiveRequestsProcessed}</td>
                        <td class="tar" id="${queue.metricIdentifier}-enq">${queue.enqueuedCount}</td>
                        <td class="tar" id="${queue.metricIdentifier}-deq">${queue.dequeuedCount}</td>
                        <td class="tar" id="${queue.metricIdentifier}-size">${queue.queueSize}</td>
                        <td class="tar" id="${queue.metricIdentifier}-avg">${queue.averageEnqueueTime}</td>
                        <%-- <td id="${queue.metricIdentifier}-status">${queue.status}</td> --%>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        <table class="compact-results-table stacked-md">
            <thead>
                <th><i:inline key=".queue"/></th>
                <th class="tar"><i:inline key=".enqueuedCount"/></th>
                <th class="tar"><i:inline key=".dequeuedCount"/></th>
                <th class="tar"><i:inline key=".queueSize"/></th>
                <th class="tar"><i:inline key=".averageEnqueueTime"/></th>
                <%-- <th><i:inline key=".status"/></th> --%>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="queue" items="${queueData}">
                    <tr>
                        <td class="wsnw">
                            <c:choose>
                                <c:when test="${favorites[queue.metricIdentifier]}">
                                    <c:set var="favIcon" value="icon-star"/>
                                    <c:set var="action" value="unfavorite"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="favIcon" value="icon-favorite-not"/>
                                    <c:set var="action" value="favorite"/>
                                </c:otherwise>
                            </c:choose>
                            <cti:button id="favMetricButton" renderMode="image" icon="${favIcon}" data-metric="${queue.metricIdentifier}" data-action="${action}"/>
                            <span title="${queue.queueName}">
                                <i:inline key="${queue}"/>
                            </span>
                        </td>
                        <td class="tar" id="${queue.metricIdentifier}-enq">${queue.enqueuedCount}</td>
                        <td class="tar" id="${queue.metricIdentifier}-deq">${queue.dequeuedCount}</td>
                        <td class="tar" id="${queue.metricIdentifier}-size">${queue.queueSize}</td>
                        <td class="tar" id="${queue.metricIdentifier}-avg">${queue.averageEnqueueTime}</td>
                        <%-- <td id="${queue.metricIdentifier}-status">${queue.status}</td> --%>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </tags:sectionContainer2>
    
    <cti:includeScript link="/resources/js/pages/yukon.support.systemHealth.js"/>
</cti:standardPage>