<%@ attribute name="pao" required="true" type="com.cannontech.common.pao.YukonPao" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="paoId" value="${pao.paoIdentifier.paoId}"/>
<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}">
    <tags:dynamicChoose updaterString="DR_CONTROLAREA/${paoId}/SHOW_ACTION" suffix="${paoId}">
        <tags:dynamicChooseOption optionId="enabled">
            <cti:url var="startControlAreaUrl" value="/spring/dr/program/startMultipleProgramsDetails">
                <cti:param name="controlAreaId" value="${paoId}"/>
            </cti:url>
            <a id="startLink_${paoId}" href="javascript:void(0)"
                onclick="openSimpleDialog('drDialog', '${startControlAreaUrl}', '<cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.title"/>')">
                <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.startIcon"/>
            </a>

            <cti:url var="stopControlAreaUrl" value="/spring/dr/program/stopMultipleProgramsDetails">
                <cti:param name="controlAreaId" value="${paoId}"/>
            </cti:url>
            <a id="stopLink_${paoId}" href="javascript:void(0)"
                onclick="openSimpleDialog('drDialog', '${stopControlAreaUrl}', '<cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.title"/>')">
                <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.stopIcon"/>
            </a>
        </tags:dynamicChooseOption>
        <tags:dynamicChooseOption optionId="fullyActiveEnabled">
            <cti:url var="stopControlAreaUrl" value="/spring/dr/program/stopMultipleProgramsDetails">
                <cti:param name="controlAreaId" value="${paoId}"/>
            </cti:url>
            <a id="stopLink_${paoId}" href="javascript:void(0)"
                onclick="openSimpleDialog('drDialog', '${stopControlAreaUrl}', '<cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.title"/>')">
                <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.stopIcon"/>
            </a>
        </tags:dynamicChooseOption>
        <tags:dynamicChooseOption optionId="disabled">
            <cti:url var="startControlAreaUrl" value="/spring/dr/program/startMultipleProgramsDetails">
                <cti:param name="controlAreaId" value="${paoId}"/>
            </cti:url>
            <a id="startLink_${paoId}" href="javascript:void(0)"
                onclick="openSimpleDialog('drDialog', '${startControlAreaUrl}', '<cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.title"/>')">
                <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.startIcon"/>
            </a>

            <cti:url var="stopControlAreaUrl" value="/spring/dr/program/stopMultipleProgramsDetails">
                <cti:param name="controlAreaId" value="${paoId}"/>
            </cti:url>
            <a id="stopLink_${paoId}" href="javascript:void(0)"
                onclick="openSimpleDialog('drDialog', '${stopControlAreaUrl}', '<cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.title"/>')">
                <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.stopIcon"/>
            </a>
        </tags:dynamicChooseOption>
        <tags:dynamicChooseOption optionId="fullyActiveDisabled">
            <cti:url var="stopControlAreaUrl" value="/spring/dr/program/stopMultipleProgramsDetails">
                <cti:param name="controlAreaId" value="${paoId}"/>
            </cti:url>
            <a id="stopLink_${paoId}" href="javascript:void(0)"
                onclick="openSimpleDialog('drDialog', '${stopControlAreaUrl}', '<cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.title"/>')">
                <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.stopIcon"/>
            </a>
        </tags:dynamicChooseOption>
    </tags:dynamicChoose>
</cti:checkPaoAuthorization>
<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}" invert="true">
    <cti:msg var="noControlAreaControl" key="yukon.web.modules.dr.controlAreaDetail.noControl"/>
    <span title="${noControlAreaControl}">
        <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.startIcon.disabled"/>
    </span>
    <span title="${noControlAreaControl}">
        <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.stopIcon.disabled"/>
    </span>
</cti:checkPaoAuthorization>
