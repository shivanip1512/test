<%@ attribute name="pao" required="true" type="com.cannontech.common.pao.YukonPao" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="paoId" value="${pao.paoIdentifier.paoId}"/>
<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}">
    <cti:url var="startScenarioUrl" value="/spring/dr/program/startMultipleProgramsDetails">
        <cti:param name="scenarioId" value="${paoId}"/>
    </cti:url>
    <a id="startLink_${paoId}" href="javascript:void(0)"
        onclick="openSimpleDialog('drDialog', '${startScenarioUrl}', '<cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.title"/>')">
        <cti:logo key="yukon.web.modules.dr.scenarioDetail.actions.startIcon"/>
    </a>

    <cti:url var="stopScenarioUrl" value="/spring/dr/program/stopMultipleProgramsDetails">
        <cti:param name="scenarioId" value="${paoId}"/>
    </cti:url>
    <a id="stopLink_${paoId}" href="javascript:void(0)"
        onclick="openSimpleDialog('drDialog', '${stopScenarioUrl}', '<cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.title"/>')">
        <cti:logo key="yukon.web.modules.dr.scenarioDetail.actions.stopIcon"/>
    </a>
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
