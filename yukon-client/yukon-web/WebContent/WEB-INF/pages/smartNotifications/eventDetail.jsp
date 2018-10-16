<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>

<cti:standardPage module="smartNotifications" page="detail">
    <c:set var="infraOrDDMEvent" value="${eventType == 'INFRASTRUCTURE_WARNING' || eventType == 'DEVICE_DATA_MONITOR'}"/>
    <c:set var="watchdogEvent" value="${eventType == 'YUKON_WATCHDOG'}"/>
    <cti:toJson id="eventsjson" object="${events.resultList}"/>
    <c:set var="urlPath" value="/notifications/events/${eventType.urlPath}"/>
    <c:if test="${!empty parameter}">
        <c:set var="urlPath" value="${urlPath}/${parameter}"/>
    </c:if>
    <cti:url var="detailUrl" value="${urlPath}"/>
    <form:form id="filter-form" action="${detailUrl}" method="GET" modelAttribute="filter">
        <div class="column-14-10 clearfix stacked">
            <h3><i:inline key="yukon.common.filters"/></h3>
            <div class="column one filter-container">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".type">
                        <i:inline key="${eventType.formatKey}"/>
                        <input type="hidden" id = "eventType" name="eventType" value="${eventType}"/>
                    </tags:nameValue2>
                    <c:if test="${!empty monitorName}">
                        <tags:nameValue2 nameKey=".monitor">
                            ${monitorName}
                            <input type="hidden" name="parameter" value="${parameter}"/>
                        </tags:nameValue2>
                    </c:if>
                    <c:if test="${!empty types}">
                        <tags:nameValue2 nameKey=".deviceTypes">
                            <div class="button-group stacked">
                                <c:forEach var="type" items="${types}">
                                    <c:set var="selected" value="${false}"/>
                                    <c:if test="${fn:contains(filter.categories, type)}">
                                        <c:set var="selected" value="${true}"/>
                                    </c:if>
                                    <cti:msg2 var="deviceType" key="yukon.web.widgets.infrastructureWarnings.category.${type}"/>
                                    <tags:check name="categories" classes="M0" value="${type}" label="${deviceType}" checked="${selected}"></tags:check>
                                </c:forEach>
                            </div>
                        </tags:nameValue2>
                    </c:if>
                    <tags:nameValue2 nameKey=".dateRange">
                        <dt:dateTime id="startDateFilter" name="startDate" value="${filter.startDate}"/>
                        <dt:dateTime id="endDateFilter" name="endDate" value="${filter.endDate}"/>
                        <input type="hidden" id="startInstant" value="${filter.startInstant}"/>
                        <input type="hidden" id="endInstant" value="${filter.endInstant}"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <div class="action-area">
                    <cti:button classes="primary action" nameKey="filter" type="submit" busy="true"/>
                </div>
                
            </div>
        </div>
    </form:form>

    <c:choose>
        <c:when test="${events.hitCount == 0}">
            <span class="empty-list"><i:inline key=".noEvents"/></span>
        </c:when>
        <c:otherwise>
            <div class="js-events-timeline clear" style="margin-top:30px;margin-bottom:30px;"></div>
            <span class="fwn"><i:inline key=".filteredResults"/></span>
            <span class="badge">${events.hitCount}</span>&nbsp;<i:inline key=".events"/>
            <span class="js-cog-menu">
                <cm:dropdown icon="icon-cog">
                    <c:if test="${infraOrDDMEvent}">
                        <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                            <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
                                <cti:param name="${cp.key}" value="${cp.value}"/>
                            </c:forEach>
                        </cti:url>
                        <cm:dropdownOption key=".collectionActions" href="${collectionActionsUrl}" icon="icon-cog-go" newTab="true"/> 
                    </c:if>
                        <cm:dropdownOption icon="icon-csv" key=".download" classes="js-download"/>  
                    <c:if test="${infraOrDDMEvent}">
                        <cti:url var="mapUrl" value="/tools/map">
                            <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                        </cti:url>
                        <cm:dropdownOption icon="icon-map-sat" key=".mapDevices" href="${mapUrl}" newTab="true"/>
                        <cti:url var="readUrl" value="/group/groupMeterRead/homeCollection">
                            <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
                                <cti:param name="${cp.key}" value="${cp.value}"/>
                            </c:forEach>
                        </cti:url>
                        <cm:dropdownOption icon="icon-read" key=".readAttribute" href="${readUrl}" newTab="true"/>
                        <cti:url var="commandUrl" value="/group/commander/collectionProcessing">
                            <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
                                <cti:param name="${cp.key}" value="${cp.value}"/>
                            </c:forEach>
                        </cti:url>	
                        <cm:dropdownOption icon="icon-ping" key=".sendCommand" href="${commandUrl}" newTab="true"/>
                     </c:if>
                </cm:dropdown>
            </span>

            <c:if test="${infraOrDDMEvent}">
                <%@ include file="ddmAndInfraWarnEventDetails.jsp" %>
            </c:if>

            <c:if test="${watchdogEvent}">
                <%@ include file="watchdogWarningEventDetails.jsp" %>
            </c:if>
        </c:otherwise>
    </c:choose>
    <cti:includeScript link="/resources/js/pages/yukon.smart.notifications.js"/>

    <script>
        yukon.smart.notifications.initEventsTimeline();
    </script>

</cti:standardPage>