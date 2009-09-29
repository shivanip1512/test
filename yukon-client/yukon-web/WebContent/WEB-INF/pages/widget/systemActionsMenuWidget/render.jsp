<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<cti:url var="phaseDetectUrl" value="/spring/amr/phaseDetect/home"/>
<div>
    <cti:checkRolesAndProperties value="PHASE_DETECT">
        <a href="${phaseDetectUrl}">Phase Detection</a><br/>
    </cti:checkRolesAndProperties>
</div>