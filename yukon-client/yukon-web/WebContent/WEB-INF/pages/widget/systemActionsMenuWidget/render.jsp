<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>

<cti:url var="phaseDetectUrl" value="/spring/amr/phaseDetect/home"/>
<cti:url var="archiveDataAnalysesUrl" value="/spring/bulk/archiveDataAnalysis/list/view"/>
<div>
    <cti:checkRolesAndProperties value="PHASE_DETECT">
        <a href="${phaseDetectUrl}"><i:inline key="yukon.web.widgets.systemActionsMenuWidget.phaseDetect"/></a><br/>
    </cti:checkRolesAndProperties>
</div>