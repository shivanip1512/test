<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:url var="phaseDetectUrl" value="/amr/phaseDetect/home"/>
<cti:url var="archiveDataAnalysesUrl" value="/bulk/archiveDataAnalysis/list/view"/>
<cti:url var="meterEventsReportUrl" value="/amr/meterEventsReport/home"/>
<cti:url var="waterLeakReportUrl" value="/amr/waterLeakReport/report"/>

<div>
    <cti:checkRolesAndProperties value="ARCHIVED_DATA_ANALYSIS">
        <a href="${archiveDataAnalysesUrl}"><i:inline key=".archiveDataAnalysis"/></a><br/>
    </cti:checkRolesAndProperties>
    <cti:checkRolesAndProperties value="PHASE_DETECT">
        <a href="${phaseDetectUrl}"><i:inline key=".phaseDetect"/></a><br/>
    </cti:checkRolesAndProperties>
    <cti:checkRolesAndProperties value="METER_EVENTS">
        <a href="${meterEventsReportUrl}"><i:inline key=".meterEventsReport"/></a><br/>
    </cti:checkRolesAndProperties>
    <cti:checkRolesAndProperties value="WATER_LEAK_REPORT">
        <a href="${waterLeakReportUrl}"><i:inline key=".waterLeakReport"/></a><br/>
    </cti:checkRolesAndProperties>
</div>