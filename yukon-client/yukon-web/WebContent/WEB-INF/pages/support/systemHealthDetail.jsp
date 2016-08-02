<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="support" page="systemHealth.detail">
    
    <cti:msg2 var="statusInfoTitle" key=".moreStatusInfo.title"/>
    <div class="dn" id="statusInfoPopup" data-title="${statusInfoTitle}" data-width="600px">
        <i:inline key=".moreStatusInfo.text"/>
        <ul>
            <c:forEach var="criteria" items="${pertinentCriteria}">
                <li><i:inline key="${criteria}"/></li>
            </c:forEach>
        </ul>
    </div>
    
    <!-- These i18ned strings are used by javascript to update the status -->
    <c:forEach var="metricStatus" items="${metricStatusLabels}">
        <span class="dn" id="metric-status-${metricStatus.key}">${metricStatus.value}</span>
    </c:forEach>
    
    <div class="column-12-12 clearfix">
        <div class="column one">
            <tags:sectionContainer2 nameKey="metricInfo">
                <table class="name-value-table">
                    <c:if test="${metric.type eq 'JMS_QUEUE_EXTENDED'}">
                        <tr>
                            <td class="name"><i:inline key=".archivedReadings"/></td>
                            <td class="value" id="${queue.metricIdentifier}-arc">${queue.archivedReadingsCount}</td>
                        </tr>
                        <tr>
                            <td class="name"><i:inline key=".processedArchiveRequests"/></td>
                            <td class="value" id="${queue.metricIdentifier}-arp">${queue.archiveRequestsProcessed}</td>
                        </tr>
                    </c:if>
                    <tr>
                        <td class="name"><i:inline key=".enqueuedCount"/></td>
                        <td class="value" id="${queue.metricIdentifier}-enq">${queue.enqueuedCount}</td>
                    </tr> 
                    <tr>
                        <td class="name"><i:inline key=".dequeuedCount"/></td>
                        <td class="value" id="${queue.metricIdentifier}-deq">${queue.dequeuedCount}</td>
                    </tr>
                    <tr>
                        <td class="name"><i:inline key=".queueSize"/></td>
                        <td class="value" id="${queue.metricIdentifier}-size">${queue.queueSize}</td>
                    </tr>
                    <tr>
                        <td class="name"><i:inline key=".averageEnqueueTime"/></td>
                        <td class="value" id="${queue.metricIdentifier}-avg">${queue.averageEnqueueTime}</td>
                    </tr>
                </table>
            </tags:sectionContainer2>
        </div>
        <div class="column two nogutter">
            <tags:sectionContainer2 nameKey="status">
                <div class="stacked-md">
                    <span id="${queue.metricIdentifier}-status">
                        <cti:icon icon="${queue.status.iconName}"/>
                    </span>
                    <span id="${queue.metricIdentifier}-status-name">
                        <i:inline key="${queue.status.metricStatus}"/>
                    </span>
                </div>
                <div id="${queue.metricIdentifier}-status-messages">
                    ${queue.status.allMessages}
                </div>
                <div data-popup="#statusInfoPopup">
                    <a href="javascript:void(0)"><i:inline key=".moreStatusInfo"/></a>
                </div>
            </tags:sectionContainer2>
        </div>
    </div>
    
    <cti:includeScript link="/resources/js/pages/yukon.support.systemHealth.js"/>
</cti:standardPage>