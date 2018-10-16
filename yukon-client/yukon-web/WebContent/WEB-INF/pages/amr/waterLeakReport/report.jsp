<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="waterLeakReport.report">

    <cti:includeScript link="/resources/js/pages/yukon.ami.water.leak.report.js"/>
    <cti:includeScript link="/resources/js/common/yukon.field.helper.js"/>

    <div id="algorithm-popup" class="dn"
        data-width="600" 
        data-title="<cti:msg2 key=".algorithm.title"/>">
        <p><i:inline key=".leakDetectionAlgorithmReason1"/></p>
        <p><i:inline key=".leakDetectionAlgorithmReason2"/></p>
        <p><i:inline key=".leakDetectionAlgorithmReason3"/></p>
        <p><i:inline key=".leakDetectionAlgorithmReason4"/></p>
        <p><i:inline key=".leakScheduledReport"/></p>
    </div>
    
    <%-- Schedule Report Popup --%>
    <c:set var="popupTitleArgs" value=""/>
    <c:if test="${not empty exportData.scheduleName}">
        <c:set var="popupTitleArgs" value="\"${fn:escapeXml(fileExportData.scheduleName)}\""/>
    </c:if>
    <cti:msg2 var="schedulePopupTitle" key=".leakScheduleDialog.title" arguments="${popupTitleArgs}"/>
    <cti:url var="schedulePopupUrl" value="schedule">
        <c:if test="${not empty jobId}">
            <cti:param name="jobId" value="${jobId}"/>
        </c:if>
    </cti:url>
    <div id="schedule-report-popup" data-dialog class="dn"
        data-title="${schedulePopupTitle}" 
        data-event="yukon.ami.water.leak.report.schedule.save" 
        data-width="650"
        data-load-event="yukon.ami.water.leak.report.schedule.load"
        data-url="${schedulePopupUrl}"></div>
    
    <tags:sectionContainer2 nameKey="filter" hideEnabled="true">
    
        <form:form id="filter-form" action="report" method="get" modelAttribute="filter">
            
            <tags:nameValueContainer2 tableClass="with-form-controls">
                <tags:nameValue2 nameKey=".filter.devices">
                    <tags:selectDevicesTabbed deviceCollection="${filter.deviceCollection}" 
                        pickerType="waterMeterPicker"
                        groupCallback="yukon.ami.waterLeakReport.filter_group_selected_callback" 
                        deviceCallback="yukon.ami.waterLeakReport.filter_individual_selected_callback"
                        uniqueId="filterSelector"/>
                </tags:nameValue2>

                <tags:nameValue2 nameKey="yukon.common.blank" excludeColon="true">
                    <label>
                        <form:checkbox path="includeDisabledPaos" id="includeDisabledPaos"/>
                        <i:inline key=".filter.includeDisabledDevices"/>
                    </label>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".filter.dateRange">
                    <dt:dateTime path="fromInstant" value="${filter.fromInstant}"
                        stepMinute="60" stepHour="1" cssDialogClass="hide_minutes" />
                    <dt:dateTime path="toInstant" value="${filter.toInstant}"
                        stepMinute="60" stepHour="1" cssDialogClass="hide_minutes" />
                </tags:nameValue2>

                <tags:nameValue2 nameKey=".filter.threshold">
                    <tags:input id="thresholdFilter" path="threshold" size="8"/>
                    <div id="threshold-help-popup" class="dn" data-title="<cti:msg2 key=".filter.threshold"/>">
                        <i:inline key=".filter.threshold.helpText"/>
                    </div>
                    <cti:icon icon="icon-help" classes="cp fn vatb" data-popup="#threshold-help-popup" data-popup-toggle=""/>
                </tags:nameValue2>
            </tags:nameValueContainer2>
            
            <div class="page-action-area">
                <cti:formatDate var="from" type="DATEHM" value="${filter.fromInstant}"/>
                <cti:formatDate var="to" type="DATEHM" value="${filter.toInstant}"/>
                <cti:url var="downloadUrl" value="leaks-csv">
                    <cti:mapParam value="${filter.deviceCollection.collectionParameters}"/>
                    <cti:param name="includeDisabledPaos" value="${filter.includeDisabledPaos}"/>
                    <cti:param name="fromInstant" value="${from}"/>
                    <cti:param name="toInstant" value="${to}"/>
                    <cti:param name="threshold" value="${filter.threshold}"/>
                </cti:url>
                <cti:button id="update-report-btn" nameKey="search" classes="primary action" busy="true" type="submit"/>
                <cti:button id="schedule-report-btn" nameKey="schedule" icon="icon-calendar-view-day"/>
                <cti:button nameKey="download" icon="icon-page-white-excel" href="${downloadUrl}"/>
                <cti:button id="intervals-btn" nameKey="intervals" classes="dn"/>
            </div>
        </form:form>
    </tags:sectionContainer2>
    
    <c:set var="controls">
        <a href="javascript:void(0);" data-popup="#algorithm-popup" data-popup-toggle><i:inline key=".algorithm"/></a>
    </c:set>
    <tags:sectionContainer2 nameKey="leaks" controls="${controls}">
        <c:choose>
            <c:when test="${fn:length(leaks.resultList) > 0}">
                <div id="leaks-container"><%@ include file="leaks.jsp" %></div>
            </c:when>
            <c:otherwise>
                <span class="empty-list"><i:inline key=".noLeaks"/></span>
            </c:otherwise>
        </c:choose>
    </tags:sectionContainer2>
    
    <tags:sectionContainer2 nameKey="scheduledReports" hideEnabled="true">
        <tags:scheduledFileExportJobs jobs="${jobs}" jobType="${jobType}" id="scheduled-reports" fromURL="/amr/waterLeakReport/"/>
    </tags:sectionContainer2>
    
</cti:standardPage>