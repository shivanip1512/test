<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/jsTree" %>

<cti:standardPage module="amr" page="meterEventsReport.report">

    <cti:includeScript link="/resources/js/common/yukon.field.helper.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.ami.meter.events.report.js"/>
    
    <c:if test="${not empty deviceCollection}">

        <cti:toJson id="modelData" object="${jsonModel}"/>

        <tags:sectionContainer2 nameKey="filterSectionHeader">
            <form id="filter-form" action="meterEventsTable">
                <c:forEach var="entry" items="${deviceCollection.collectionParameters}">
                    <input type="hidden" name="${entry.key}" value="${entry.value}"/>
                </c:forEach>
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".report.selectedDevices">
                        <c:set var="isDeviceGroup" 
                            value="${deviceCollection.collectionParameters['collectionType'] == 'group'}"/>
                        <span>
                            <c:choose>
                                <c:when test="${isDeviceGroup}">
                                    <i:inline key=".filter.deviceGroup" arguments="${deviceCollection.collectionParameters['group.name']}"/>
                                </c:when>
                                <c:otherwise>
                                    <cti:msg key="${deviceCollection.description}" />
                                </c:otherwise>
                            </c:choose>
                        </span>
                        <c:if test="${deviceCollection.deviceCount > 0}">
                            <c:if test="${isDeviceGroup}">
                                <span class="viewGroupLink fr">
                                    <cti:url var="deviceGroupUrl" value="/group/editor/home">
                                        <cti:param name="groupName">
                                            ${deviceCollection.collectionParameters['group.name']}
                                        </cti:param>
                                    </cti:url>
                                    (<a href="${deviceGroupUrl}"><i:inline key=".filter.viewDeviceGroup"/></a>)
                                </span>
                            </c:if>
                            <tags:selectedDevicesPopup deviceCollection="${deviceCollection}"/>
                        </c:if>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey="yukon.common.blank" excludeColon="true">
                        <label>
                            <input type="checkbox" name="includeDisabledPaos"><i:inline key=".filter.includeDisabledDevices"/>
                        </label>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".report.selectedDevicesCount">
                        ${deviceCollection.deviceCount}
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".filter.range">
                        <dt:dateRange startValue="${meterEventsFilter.fromInstant}" 
                            endValue="${meterEventsFilter.toInstant}" startName="fromInstant" endName="toInstant"/>
                    </tags:nameValue2>
    
                    <tags:nameValue2 nameKey="yukon.common.blank" excludeColon="true">
                        <label>
                            <input type="checkbox" name="onlyLatestEvent" class="fl" id="filter_onlyLatestEvent">
                            <i:inline key=".filter.onlyLatestEvent"/>
                        </label>
                        <cti:icon icon="icon-help" classes="fn cp vatb" data-popup=".js-only-lastest-help" data-popup-toggle=""/>
                        <div class="dn js-only-lastest-help" data-width="500" data-title="<cti:msg2 key=".filter.onlyLatestEvent"/>">
                            <i:inline key=".filter.onlyLatestEvents.help.text"/>
                        </div>
                    </tags:nameValue2>
    
                    <tags:nameValue2 nameKey="yukon.common.blank" excludeColon="true">
                        <label>
                            <input type="checkbox" name="onlyAbnormalEvents" class="fl">
                            <i:inline key=".filter.onlyAbnormalEvents"/>
                        </label>
                        <cti:icon icon="icon-help" classes="fn cp vatb" data-popup=".js-only-abnormal-help" data-popup-toggle=""/>
                        <div class="dn js-only-abnormal-help" data-width="500" data-title="<cti:msg2 key=".filter.onlyAbnormalEvents"/>">
                            <i:inline key=".filter.onlyAbnormalEvents.help.text"/>
                        </div>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".filter.eventTypesRow">
                        <div id="eventTypeInputs"></div>
                        <cti:msg2 key=".filter.cog.title" var="cogTitle"/>
                        <a href="javascript:void(0);" title="${cogTitle}" data-popup="#filter-events-popup" data-popup-toggle>
                            <cti:icon icon="icon-filter"/>
                            <span class="numEventTypes">
                                <span id="numEventTypes">${numSelectedEventTypes}</span>
                                <i:inline key=".filter.selected"/>
                            </span>
                        </a>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
    
                <div class="page-action-area">
                    <cti:button id="update-meter-events-btn" nameKey="update" classes="primary action" busy="true"/>
                    <cti:button id="schedule-meter-events-btn" nameKey="schedule" icon="icon-calendar-view-day"/>
                    <cti:button id="download-meter-events-btn" nameKey="download" icon="icon-page-white-excel" />
                </div>
            </form>
            <form id="downloadForm" action="csv" method="post" style="display:none">
                <cti:csrfToken/>
                <div id="downloadFormContents"></div>
            </form>
        </tags:sectionContainer2>
        
        <cti:formatDate var="fromInstantFormatted" value="${meterEventsFilter.fromInstant}" type="DATE"/>
        <cti:formatDate var="toInstantFormatted" value="${meterEventsFilter.toInstant}" type="DATE"/>
        <cti:url var="filteredUrl" value="meterEventsTable">
            <c:if test="${not empty meterEventsFilter.fromInstant}"><cti:param name="fromInstant" value="${fromInstantFormatted}" /></c:if>
            <c:if test="${not empty meterEventsFilter.toInstant}"><cti:param name="toInstant" value="${toInstantFormatted}" /></c:if>
            <c:if test="${not empty meterEventsFilter.onlyLatestEvent}"><cti:param name="onlyLatestEvent" 
                value="${meterEventsFilter.onlyLatestEvent}" /></c:if>
            <c:if test="${not empty meterEventsFilter.onlyAbnormalEvents}"><cti:param name="onlyAbnormalEvents" 
                value="${meterEventsFilter.onlyAbnormalEvents}" /></c:if>
            <c:if test="${not empty meterEventsFilter.includeDisabledPaos}"><cti:param name="includeDisabledPaos" 
                value="${meterEventsFilter.includeDisabledPaos}" /></c:if>
            <c:forEach items="${meterEventsFilter.attributes}" var="attribute">
                <cti:param name="attributes" value="${attribute}" />
            </c:forEach>
            <cti:mapParam value="${deviceCollection.collectionParameters}"/>
        </cti:url>
        <div id="meterEventsTable" data-url="${filteredUrl}">
            <%@ include file="meterEventsTable.jsp" %>
        </div>
        
        <div data-dialog  id="filter-events-popup" class="dn" 
            data-title="<cti:msg2 key=".filter.eventTypes"/>" 
            data-event="yukon.ami.meter.events.filter">
            <t:inlineTree id="eventTree"
                treeCss="/resources/js/lib/dynatree/skin/device.group.css"
                treeParameters="{checkbox: true, selectMode: 3}"
                includeControlBar="true" />
        </div>

        <div id="scheduleDialog" class="dn"><%@ include file="scheduledMeterEventsDialog.jsp" %></div>
    </c:if>

    <c:if test="${empty deviceCollection}">
        <tags:sectionContainer2 nameKey="filterSectionHeader">
            <a href="selectDevices" class="clearfix fl" title="<cti:msg2 key="yukon.common.chooseDevices.tooltip"/>">
                <c:if test="${empty deviceCollection}">
                    <span class="empty-list fl"><i:inline key="yukon.common.noDevices"/></span>
                </c:if>
                <c:if test="${not empty deviceCollection}">
                    <span class="b-label fl"><i:inline key="${deviceCollection.description}"/></span>
                </c:if>
                <i class="icon icon-folder-edit"></i>
            </a>
        </tags:sectionContainer2>
    </c:if>
    <div id="scheduledJobsTable" data-url="scheduledJobsTable">
        <%@ include file="scheduledJobsTable.jsp" %>
    </div>

</cti:standardPage>