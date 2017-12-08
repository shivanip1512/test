<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:standardPage module="support" page="systemHealth">
    
    <cti:checkRolesAndProperties value="OPERATOR_ADMINISTRATOR">
        <div id="page-actions" class="dn">
            <cm:dropdownOption id="sync-nm-data" key=".sync" data-ok-event="yukon:support:systemhealth:sync" icon="icon-arrow-refresh" />
            <d:confirm on="#sync-nm-data" nameKey="confirmSync"/>
        </div>
    </cti:checkRolesAndProperties>
    
    <tags:sectionContainer2 nameKey="serverBrokerStatistics">
        <table class="compact-results-table stacked-md has-alerts row-highlighting">
            <thead>
                <th></th>
                <th><i:inline key=".queue"/></th>
                <th class="tar"><i:inline key=".archivedReadings"/></th>
                <th class="tar"><i:inline key=".processedArchiveRequests"/></th>
                <th class="tar"><i:inline key=".enqueuedCount"/></th>
                <th class="tar"><i:inline key=".dequeuedCount"/></th>
                <th class="tar"><i:inline key=".queueSize"/></th>
                <th class="tar"><i:inline key=".averageEnqueueTime"/></th>
                
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="queue" items="${extendedQueueData}">
                    <tr>
                        <td id="${queue.metricIdentifier}-status"><cti:icon icon="${queue.status.iconName}" title="${queue.status.allMessages}"/></td>
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
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        <table class="compact-results-table stacked-md has-alerts row-highlighting">
            <thead>
                <th></th>
                <th><i:inline key=".queue"/></th>
                <th class="tar"><i:inline key=".enqueuedCount"/></th>
                <th class="tar"><i:inline key=".dequeuedCount"/></th>
                <th class="tar"><i:inline key=".queueSize"/></th>
                <th class="tar"><i:inline key=".averageEnqueueTime"/></th>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="queue" items="${queueData}">
                    <tr>
                        <td id="${queue.metricIdentifier}-status"><cti:icon icon="${queue.status.iconName}" title="${queue.status.allMessages}"/></td>
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
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </tags:sectionContainer2>
    
    <div class="column-10-14 clearfix">
        <div class="column one"> 
            <tags:sectionContainer2 nameKey="porterQueueStatistics">
                <div class="scroll-md">
                    <table class="compact-results-table stacked-md has-alerts row-highlighting">
                        <thead>
                            <th></th>
                            <th><i:inline key=".port"/></th>
                            <th class="tar"><i:inline key=".queueSize"/></th>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                            <c:forEach var="port" items="${portData}">
                                <tr>
                                    <td></td>
                                    <td class="wsnw">
                                        <cti:paoDetailUrl yukonPao="${port.paoIdentifier}">
                                            <c:if test="${!empty port}">${fn:escapeXml(port.paoName)}</c:if>
                                        </cti:paoDetailUrl>
                                    </td>
                                    <td class="tar"><cti:pointValue pointId="${port.pointId}" format="VALUE"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </tags:sectionContainer2>
        </div>
    </div>
    
    <cti:includeScript link="/resources/js/pages/yukon.support.systemHealth.js"/>
</cti:standardPage>