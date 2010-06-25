<%@ attribute name="pao" required="true" type="com.cannontech.common.pao.YukonPao" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="paoId" value="${pao.paoIdentifier.paoId}"/>
<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}">
    <cti:url var="startScenarioUrl" value="/spring/dr/program/start/multipleDetails">
        <cti:param name="scenarioId" value="${paoId}"/>
    </cti:url>
    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startMultiplePrograms.title" 
                           dialogId="drDialog" 
                           actionUrl="${startScenarioUrl}" 
                           logoKey="yukon.web.modules.dr.scenarioDetail.actions.startIcon"/>

    <cti:url var="stopScenarioUrl" value="/spring/dr/program/stop/multipleDetails">
        <cti:param name="scenarioId" value="${paoId}"/>
    </cti:url>
    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopMultiplePrograms.title" 
                           dialogId="drDialog" 
                           actionUrl="${stopScenarioUrl}" 
                           logoKey="yukon.web.modules.dr.scenarioDetail.actions.stopIcon"/>
</cti:checkPaoAuthorization>
<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}" invert="true">
    <cti:msg var="noScenarioControl" key="yukon.web.modules.dr.scenarioDetail.noControl"/>
    <span title="${noScenarioControl}">
        <cti:logo key="yukon.web.modules.dr.scenarioDetail.actions.startIcon.disabled"/>
    </span>
    <span title="${noScenarioControl}">
        <cti:logo key="yukon.web.modules.dr.scenarioDetail.actions.stopIcon.disabled"/>
    </span>
</cti:checkPaoAuthorization>
