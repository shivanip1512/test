<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operations" page="dashboard">

    <div class="column-12-12">
        <div class="column one section">
            <cti:tabs>
                <cti:msg2 var="favoritesTab" key=".favorites" />
                <cti:tab title="${favoritesTab}">
                    <c:if test="${empty favorites}">
                        <span class="empty-list"><i:inline key=".favorites.emptyList" /></span>
                    </c:if>
                    <c:if test="${not empty favorites}">
                        <cti:msg2 var="undoText" key="yukon.common.undo"/>

                        <c:forEach var="module" items="${favorites}">
                            <c:if test="${!empty favorites}">
                                <h5><cti:msg2 key="${module.key}"/></h5>
                            </c:if>
                            <ul class="simple-list stacked favorites" data-undo-text="${undoText}">
                                <c:forEach items="${module.value}" var="favorite">
                                    <c:set var="favoritePageName"><cti:pageName userPage="${favorite}"/></c:set>
                                    <cti:msg2 var="removedText" key=".favorites.removed" htmlEscapeArguments="false"
                                        arguments="${favoritePageName}"/>
                                    <li class="favorite" data-removed-text="${removedText}">
                                        <cti:listAsSafeString var="labelArgs" list="${favorite.arguments}"/>
                                        <cti:button classes="b-favorite remove" nameKey="favorite" renderMode="image" icon="icon-star" 
                                            data-name="${favorite.name}"
                                            data-module="${favorite.moduleName}"
                                            data-path="${favorite.path}"
                                            data-label-args="${labelArgs}"/>
                                        <a href="<cti:url value="${favorite.path}"/>">${favoritePageName}</a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:forEach>
                    </c:if>
                </cti:tab>
                <cti:msg2 var="historyTab" key=".history" />
                <cti:tab title="${historyTab}">
                    <c:if test="${empty history}">
                        <span class="empty-list"><i:inline key=".history.emptyList" /></span>
                    </c:if>
                    <c:if test="${not empty history}">
                        <ol class="simple-list">
                            <c:forEach var="historyItem" items="${history}">
                                <li><a href="<cti:url value="${historyItem.path}"/>"><cti:pageName userPage="${historyItem}"/></a></li>
                            </c:forEach>
                        </ol>
                    </c:if>
                </cti:tab>
            </cti:tabs>
        </div>
        <div class="column two nogutter">
            <tags:widget bean="subscribedMonitorsWidget" container="section"/>
        </div>
    </div>
    
    <c:if test="${showSystemHealth}">
        <div class="column one nogutter">
        <cti:msgScope paths="modules.support.systemHealth">
            <tags:sectionContainer2 nameKey="serverBrokerStatistics">
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
                                            <i:inline key="${queue}"/>
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
                                            <i:inline key="${queue}"/>
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
            </tags:sectionContainer2>
        </cti:msgScope>
        </div>
        
        <cti:includeScript link="/resources/js/pages/yukon.support.systemHealth.js"/>
    </c:if>
</cti:standardPage>