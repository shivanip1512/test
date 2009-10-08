<%@ attribute name="pao" required="true" type="com.cannontech.common.pao.YukonPao" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="paoId" value="${pao.paoIdentifier.paoId}"/>
<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}">
    <tags:dynamicChoose updaterString="DR_PROGRAM/${paoId}/SHOW_ACTION" suffix="${paoId}">
        <tags:dynamicChooseOption optionId="unknown">
            <cti:msg var="programUnknown" key="yukon.web.modules.dr.programDetail.unknown"/>
            <span class="subtleGray" title="${programUnknown}">
                <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon.disabled"/>
            </span>
            <span class="subtleGray" title="${programUnknown}">
                <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon.disabled"/>
            </span>
        </tags:dynamicChooseOption>
        <tags:dynamicChooseOption optionId="runningEnabled">
            <cti:url var="stopProgramUrl" value="/spring/dr/program/stopProgramDetails">
                <cti:param name="programId" value="${paoId}"/>
            </cti:url>
            <a href="javascript:void(0)" class="simpleLink"
                onclick="openSimpleDialog('drDialog', '${stopProgramUrl}', '<cti:msg key="yukon.web.modules.dr.program.stopProgram.title"/>')">
                <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon"/>
            </a>
        </tags:dynamicChooseOption>
        <tags:dynamicChooseOption optionId="enabled">
            <cti:url var="startProgramUrl" value="/spring/dr/program/startProgramDetails">
                <cti:param name="programId" value="${paoId}"/>
            </cti:url>
            <a href="javascript:void(0)" class="simpleLink"
                onclick="openSimpleDialog('drDialog', '${startProgramUrl}', '<cti:msg key="yukon.web.modules.dr.program.startProgram.title"/>')">
                <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon"/>
            </a>

            <cti:url var="stopProgramUrl" value="/spring/dr/program/stopProgramDetails">
                <cti:param name="programId" value="${paoId}"/>
            </cti:url>
            <a href="javascript:void(0)" class="simpleLink"
                onclick="openSimpleDialog('drDialog', '${stopProgramUrl}', '<cti:msg key="yukon.web.modules.dr.program.stopProgram.title"/>')">
                <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon"/>
            </a>
        </tags:dynamicChooseOption>
        <tags:dynamicChooseOption optionId="runningDisabled">
            <cti:url var="startProgramUrl" value="/spring/dr/program/startProgramDetails">
                <cti:param name="programId" value="${paoId}"/>
            </cti:url>
            <a href="javascript:void(0)" class="simpleLink"
                onclick="openSimpleDialog('drDialog', '${startProgramUrl}', '<cti:msg key="yukon.web.modules.dr.program.startProgram.title"/>')">
                <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon"/>
            </a>

            <cti:url var="stopProgramUrl" value="/spring/dr/program/stopProgramDetails">
                <cti:param name="programId" value="${paoId}"/>
            </cti:url>
            <a href="javascript:void(0)" class="simpleLink"
                onclick="openSimpleDialog('drDialog', '${stopProgramUrl}', '<cti:msg key="yukon.web.modules.dr.program.stopProgram.title"/>')">
                <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon"/>
            </a>
        </tags:dynamicChooseOption>
        <tags:dynamicChooseOption optionId="disabled">
            <cti:url var="startProgramUrl" value="/spring/dr/program/startProgramDetails">
                <cti:param name="programId" value="${paoId}"/>
            </cti:url>
            <a href="javascript:void(0)" class="simpleLink"
                onclick="openSimpleDialog('drDialog', '${startProgramUrl}', '<cti:msg key="yukon.web.modules.dr.program.startProgram.title"/>')">
                <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon"/>
            </a>

            <cti:url var="stopProgramUrl" value="/spring/dr/program/stopProgramDetails">
                <cti:param name="programId" value="${paoId}"/>
            </cti:url>
            <a href="javascript:void(0)" class="simpleLink"
                onclick="openSimpleDialog('drDialog', '${stopProgramUrl}', '<cti:msg key="yukon.web.modules.dr.program.stopProgram.title"/>')">
                <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon"/>
            </a>
        </tags:dynamicChooseOption>
    </tags:dynamicChoose>
</cti:checkPaoAuthorization>                    
<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}" invert="true">
    <cti:msg var="noProgramControl" key="yukon.web.modules.dr.programDetail.noControl"/>
    <span class="subtleGray" title="${noProgramControl}">
        <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon.disabled"/>
    </span>
    <span class="subtleGray" title="${noProgramControl}">
        <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon.disabled"/>
    </span>
</cti:checkPaoAuthorization>
