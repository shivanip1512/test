<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<cti:url var="phaseDetectUrl" value="/spring/amr/phaseDetect/home"/>
<cti:url var="archiveDataAnalysesUrl" value="/spring/bulk/archiveDataAnalysis/list"/>
<div>
    <a href="${archiveDataAnalysesUrl}">Archive Data Analyses</a><br>
    <cti:checkRolesAndProperties value="PHASE_DETECT">
        <a href="${phaseDetectUrl}">Phase Detection</a><br/>
    </cti:checkRolesAndProperties>
    
</div>