<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:url var="phaseDetectUrl" value="/amr/phaseDetect/home"/>
<cti:url var="archiveDataAnalysesUrl" value="/bulk/archiveDataAnalysis/list/view"/>
<cti:url var="meterEventsReportUrl" value="/amr/meterEventsReport/home"/>
<cti:url var="waterLeakReportUrl" value="/amr/waterLeakReport/report">
    <cti:param name="initReport" value="true"/>
</cti:url>
<cti:url var="waterLeakJobsUrl" value="/amr/waterLeakReport/jobs"/>

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
    <cti:checkRolesAndProperties value="REPORTING">
        <a href="${waterLeakReportUrl}"><i:inline key=".waterLeakReport"/></a><br/>
        <a href="${waterLeakJobsUrl}"><i:inline key=".waterLeakJobs"/></a><br/>
    </cti:checkRolesAndProperties>
</div>