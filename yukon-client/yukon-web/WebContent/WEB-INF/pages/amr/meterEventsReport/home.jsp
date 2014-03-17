<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:standardPage page="meterEventsReport.report" module="amr">
    <c:if test="${not empty deviceCollection}">
        <cti:includeScript link="/JavaScript/yukon.tag.scheduledFileExportInputs.js"/>
        <cti:includeScript link="/JavaScript/yukon.field.helper.js"/>
        <cti:includeScript link="/JavaScript/yukon.ami.meter.events.report.js"/>
        <cti:includeScript link="/JavaScript/yukon.cron.js"/>

        <c:set var="popupTitleArg" value=""/>
        <c:if test="${not empty exportData.scheduleName}">
            <c:set var="popupTitleArg" value="\"${fn:escapeXml(exportData.scheduleName)}\""/>
        </c:if>
        <cti:msg2 var="popupTitle" key=".schedulePopup.title" argument="${popupTitleArg}" htmlEscapeArguments="false"/>
        <cti:msg2 var="newPopupTitle" key=".schedulePopup.title" argument="" htmlEscapeArguments="false"/>
        <script type="text/json" id="modelData">
        {
            "meterEventTypesMap" : ${meterEventTypesMapJson},
            "generalEvents" : ${generalEventsJson},
            "hardwareEvents" : ${hardwareEventsJson},
            "tamperEvents" : ${tamperEventsJson},
            "outageEvents" : ${outageEventsJson},
            "meteringEvents" : ${meteringEventsJson},
            "allTitle" : "<cti:msg2 key=".filter.tree.all" javaScriptEscape="true"/>",
            "generalTitle" : "<cti:msg2 key=".filter.tree.general" javaScriptEscape="true"/>",
            "hardwareTitle" : "<cti:msg2 key=".filter.tree.hardware" javaScriptEscape="true"/>",
            "tamperTitle" : "<cti:msg2 key=".filter.tree.tamper" javaScriptEscape="true"/>",
            "outageTitle" : "<cti:msg2 key=".filter.tree.outage" javaScriptEscape="true"/>",
            "meteringTitle" : "<cti:msg2 key=".filter.tree.metering" javaScriptEscape="true"/>",
            "schedulePopupTitle" : ${cti:jsonString(popupTitle)},
            "newSchedulePopupTitle" : ${cti:jsonString(newPopupTitle)},
            "confirmScheduleDeletion" : "<cti:msg2 key="modules.tools.scheduledFileExport.jobs.deleteSchedule.title"
                                         javaScriptEscape="true"/>",
            "okBtnLbl" : "<cti:msg2 key="components.button.ok.label" javaScriptEscape="true"/>",
            "cancelBtnLbl" : "<cti:msg2 key="components.button.cancel.label" javaScriptEscape="true"/>",
            "openScheduleDialog" : ${not empty jobId}
        }
        </script>

        <tags:sectionContainer2 nameKey="filterSectionHeader">
            <form id="filterForm" action="meterEventsTable">
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
                    <tags:nameValue2 nameKey=".report.selectedDevicesCount">
                        ${deviceCollection.deviceCount}
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".filter.dateFrom">
                        <dt:dateRange startValue="${meterEventsFilter.fromInstant}" 
                            endValue="${meterEventsFilter.toInstant}" startName="fromInstant" endName="toInstant"/>
                    </tags:nameValue2>
    
                    <tags:nameValue2 nameKey=".filter.onlyLatestEvent">
                        <input type="checkbox" name="onlyLatestEvent" class="fl" id="filter_onlyLatestEvent"/>
                        <span class="focusableFieldHolder">
                            <a id="latestEventsHelp"><i class="icon icon-help"></i></a>
                        </span>
                        <span class="focused-field-description"><i:inline key=".filter.onlyLatestEvents.help.text"/></span>
                    </tags:nameValue2>
    
                    <tags:nameValue2 nameKey=".filter.onlyAbnormalEvents">
                        <input type="checkbox" name="onlyAbnormalEvents" class="fl" id="filter_onlyAbnormalEvents"/>
                        <span class="focusableFieldHolder">
                            <a id="activeEventsHelp"><i class="icon icon-help"></i></a>
                        </span>
                        <span class="focused-field-description"><i:inline key=".filter.onlyAbnormalEvents.help.text"/></span>
                    </tags:nameValue2>
    
                    <tags:nameValue2 nameKey=".filter.includeDisabledDevices">
                        <input type="checkbox" name="includeDisabledPaos" id="filter_includeDisabledPaos"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".filter.eventTypesRow">
                        <div id="eventTypeInputs"></div>
                        <cti:msg2 key=".filter.cog.title" var="cogTitle"/>
                        <a href="javascript:void(0);" id="eventTypesCogFilter" title="${cogTitle}">
                            <i class="icon icon-filter"></i>
                            <span class="numEventTypes">
                                <span id="numEventTypes">${numSelectedEventTypes}</span>
                                <i:inline key=".filter.selected"/>
                            </span>
                        </a>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
    
                <div class="page-action-area">
                    <cti:button id="updateMeterEventsBtn" nameKey="update" classes="primary action" busy="true"/>
                    <cti:button id="scheduleMeterEventsBtn" nameKey="schedule" icon="icon-calendar-view-day"/>
                    <cti:button id="exportCsvBtn" nameKey="csv" icon="icon-page-white-excel" />
                </div>
            </form>
            <form id="downloadForm" action="csv" method="post" style="display:none">
                <cti:csrfToken/>
                <div id="downloadFormContents"></div>
            </form>
        </tags:sectionContainer2>
    
        <div id="meterEventsTable" class="f-table-container" data-reloadable>
            <%@ include file="meterEventsTable.jsp" %>
        </div>
        
        <i:simplePopup titleKey=".filter.eventTypes" id="filterPopupEventTypes" 
            onClose="yukon.ami.meter.events.report.updateEventTypes()" on="#eventTypesCogFilter, #eventTypesCogSchedule">
            <t:inlineTree id="eventTree"
                treeCss="/WebConfig/yukon/styles/lib/dynatree/deviceGroup.css"
                treeParameters="{checkbox: true, selectMode: 3}"
                includeControlBar="true" />
    
            <div class="action-area">
                <cti:button id="eventTypesFilterOkBtn" nameKey="ok" classes="primary action"/>
            </div>
        </i:simplePopup>

        <div id="scheduleDialog" class="dn">
            <%@ include file="scheduledMeterEventsDialog.jsp" %>
        </div>
    </c:if>

    <c:if test="${empty deviceCollection }">
        <tags:sectionContainer2 nameKey="filterSectionHeader">
            <span class="empty-list">No devices selected</span>
            <a href="selectDevices">Select Devices</a>
        </tags:sectionContainer2>
    </c:if>
    <div id="scheduledJobsTable" class="f-table-container" data-reloadable>
        <%@ include file="scheduledJobsTable.jsp" %>
    </div>

</cti:standardPage>