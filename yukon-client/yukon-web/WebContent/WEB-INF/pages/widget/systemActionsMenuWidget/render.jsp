<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:url var="phaseDetectUrl" value="/spring/amr/phaseDetect/home"/>
<cti:url var="archiveDataAnalysesUrl" value="/spring/bulk/archiveDataAnalysis/list/view"/>
<div>
    <cti:checkRolesAndProperties value="ARCHIVED_DATA_ANALYSIS">
        <a href="${archiveDataAnalysesUrl}"><i:inline key=".archiveDataAnalysis"/></a><br/>
    </cti:checkRolesAndProperties>
    <cti:checkRolesAndProperties value="PHASE_DETECT">
        <a href="${phaseDetectUrl}"><i:inline key=".phaseDetect"/></a><br/>
    </cti:checkRolesAndProperties>
</div>