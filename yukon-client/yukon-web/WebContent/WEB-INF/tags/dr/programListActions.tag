<%@ attribute name="pao" required="true" type="com.cannontech.common.pao.YukonPao" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<c:set var="paoId" value="${pao.paoIdentifier.paoId}"/>
<cti:msg var="programUnknown" key="yukon.web.modules.dr.programDetail.unknown"/>
<cti:msg var="notRunning" key="yukon.web.modules.dr.programDetail.notRunning"/>
<cti:msg var="alreadyRunning" key="yukon.web.modules.dr.programDetail.alreadyRunning"/>

<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}">
    <tags:dynamicChoose updaterString="DR_PROGRAM/${paoId}/SHOW_ACTION" suffix="${paoId}">
        <tags:dynamicChooseOption optionId="unknown">
            <span class="subtle" title="${programUnknown}">
                <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon.disabled"/>
            </span>
            <span class="subtle" title="${programUnknown}">
                <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon.disabled"/>
            </span>
        </tags:dynamicChooseOption>
        <tags:dynamicChooseOption optionId="runningEnabled">
            <span class="subtle" title="${alreadyRunning}">
                <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon.disabled"/>
            </span>
            <cti:url var="stopProgramUrl" value="/dr/program/stop/details">
                <cti:param name="programId" value="${paoId}"/>
            </cti:url>
            <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopProgram.title" 
                dialogId="drDialog" actionUrl="${stopProgramUrl}" 
                logoKey="yukon.web.modules.dr.programDetail.actions.stopIcon"/>
        </tags:dynamicChooseOption>
        <tags:dynamicChooseOption optionId="scheduledEnabled">
            <cti:url var="startProgramUrl" value="/dr/program/start/details">
                <cti:param name="programId" value="${paoId}"/>
            </cti:url>
            <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startProgram.title" 
                dialogId="drDialog" actionUrl="${startProgramUrl}" 
                logoKey="yukon.web.modules.dr.programDetail.actions.startIcon"/>
            <cti:url var="stopProgramUrl" value="/dr/program/stop/details">
                <cti:param name="programId" value="${paoId}"/>
            </cti:url>
            <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopProgram.title" 
                dialogId="drDialog" actionUrl="${stopProgramUrl}" 
                logoKey="yukon.web.modules.dr.programDetail.actions.stopIcon"/>
        </tags:dynamicChooseOption>
        <tags:dynamicChooseOption optionId="enabled">
            <cti:url var="startProgramUrl" value="/dr/program/start/details">
                <cti:param name="programId" value="${paoId}"/>
            </cti:url>
            <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startProgram.title" 
                dialogId="drDialog" actionUrl="${startProgramUrl}" 
                logoKey="yukon.web.modules.dr.programDetail.actions.startIcon"/>
            <span title="${notRunning}">
                <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon.disabled"/>
            </span>
        </tags:dynamicChooseOption>
        <tags:dynamicChooseOption optionId="runningDisabled">
            <span class="subtle" title="${alreadyRunning}">
                <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon.disabled"/>
            </span>
            <cti:url var="stopProgramUrl" value="/dr/program/stop/details">
                <cti:param name="programId" value="${paoId}"/>
            </cti:url>
            <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopProgram.title" 
                dialogId="drDialog" actionUrl="${stopProgramUrl}" 
                logoKey="yukon.web.modules.dr.programDetail.actions.stopIcon"/>
        </tags:dynamicChooseOption>
        <tags:dynamicChooseOption optionId="scheduledDisabled">
            <cti:url var="startProgramUrl" value="/dr/program/start/details">
                <cti:param name="programId" value="${paoId}"/>
            </cti:url>
            <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startProgram.title" 
                dialogId="drDialog" actionUrl="${startProgramUrl}" 
                logoKey="yukon.web.modules.dr.programDetail.actions.startIcon"/>
            <cti:url var="stopProgramUrl" value="/dr/program/stop/details">
                <cti:param name="programId" value="${paoId}"/>
            </cti:url>
            <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopProgram.title" 
                dialogId="drDialog" actionUrl="${stopProgramUrl}" 
                logoKey="yukon.web.modules.dr.programDetail.actions.stopIcon"/>
        </tags:dynamicChooseOption>
        <tags:dynamicChooseOption optionId="disabled">
            <cti:url var="startProgramUrl" value="/dr/program/start/details">
                <cti:param name="programId" value="${paoId}"/>
            </cti:url>
            <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startProgram.title" 
                dialogId="drDialog" actionUrl="${startProgramUrl}" 
                logoKey="yukon.web.modules.dr.programDetail.actions.startIcon"/>
            <span title="${notRunning}">
                <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon.disabled"/>
            </span>
        </tags:dynamicChooseOption>
    </tags:dynamicChoose>
</cti:checkPaoAuthorization>                    
<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}" invert="true">
    <cti:msg var="noProgramControl" key="yukon.web.modules.dr.programDetail.noControl"/>
    <span class="subtle" title="${noProgramControl}">
        <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon.disabled"/>
    </span>
    <span class="subtle" title="${noProgramControl}">
        <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon.disabled"/>
    </span>
</cti:checkPaoAuthorization>
