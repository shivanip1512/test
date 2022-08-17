<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:url var="phaseDetectUrl" value="/amr/phaseDetect/home"/>
<cti:url var="archiveDataAnalysesUrl" value="/bulk/archiveDataAnalysis/list/view"/>
<cti:url var="meterEventsReportUrl" value="/amr/meterEventsReport/home"/>
<cti:url var="waterLeakReportUrl" value="/amr/waterLeakReport/report"/>
<cti:url var="usageThresholdReportUrl" value="/amr/usageThresholdReport/report"/>

<div>
    <cti:checkRolesAndProperties value="ARCHIVED_DATA_ANALYSIS">
        <a href="${archiveDataAnalysesUrl}" target="_blank"><i:inline key=".archiveDataAnalysis"/></a><br/>
    </cti:checkRolesAndProperties>
    <cti:checkRolesAndProperties value="PHASE_DETECT">
        <a href="${phaseDetectUrl}" target="_blank"><i:inline key=".phaseDetect"/></a><br/>
    </cti:checkRolesAndProperties>
    <cti:checkRolesAndProperties value="METER_EVENTS">
        <a href="${meterEventsReportUrl}" target="_blank"><i:inline key=".meterEventsReport"/></a><br/>
    </cti:checkRolesAndProperties>
    <c:if test="${showWaterLeak}">
        <cti:checkRolesAndProperties value="WATER_LEAK_REPORT">
            <a href="${waterLeakReportUrl}" target="_blank"><i:inline key=".waterLeakReport"/></a><br/>
        </cti:checkRolesAndProperties>
    </c:if>
    <cti:checkRolesAndProperties value="USAGE_THRESHOLD_REPORT">
        <a href="${usageThresholdReportUrl}" target="_blank"><i:inline key=".usageThresholdReport"/></a><br/>
    </cti:checkRolesAndProperties>
</div>