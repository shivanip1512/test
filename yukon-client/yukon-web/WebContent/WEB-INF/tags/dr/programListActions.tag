<%@ tag body-content="empty" trimDirectiveWhitespaces="true" dynamic-attributes="attrs" %>

<%@ attribute name="pao" required="true" type="com.cannontech.common.pao.YukonPao" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr">

<c:set var="paoId" value="${pao.paoIdentifier.paoId}"/>
<cti:msg2 var="programUnknown" key=".programDetail.unknown"/>
<cti:msg2 var="notRunning" key=".programDetail.notRunning"/>
<cti:msg2 var="alreadyRunning" key=".programDetail.alreadyRunning"/>
<cti:msg2 var="noProgramControl" key=".programDetail.noControl"/>
<cti:msg2 var="startAction" key=".programDetail.actions.start"/>
<cti:msg2 var="stopAction" key=".programDetail.actions.stop"/>

<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}">
    <cm:dropdown containerCssClass="fr">
        <tags:dynamicChoose updaterString="DR_PROGRAM/${paoId}/SHOW_ACTION" suffix="${paoId}">

            <tags:dynamicChooseOption optionId="unknown">
                <%-- All actions are disabled when the DR Program is unknown --%>
                <cm:dropdownOption icon="icon-control-play-blue" disabled="true" title="${programUnknown}">${startAction}</cm:dropdownOption>
                <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" title="${programUnknown}">${stopAction}</cm:dropdownOption>
            </tags:dynamicChooseOption>

            <tags:dynamicChooseOption optionId="runningEnabled">
                <cm:dropdownOption icon="icon-control-play-blue" disabled="true" title="${alreadyRunning}">${startAction}</cm:dropdownOption>
                <li>
                    <cti:url var="stopProgramUrl" value="/dr/program/stop/details">
                        <cti:param name="programId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".program.stopProgram.title" 
                                           dialogId="drDialog"
                                           actionUrl="${stopProgramUrl}" 
                                           icon="icon-control-stop-blue"
                                           labelKey=".programDetail.actions.stop"/>
                 </li>
            </tags:dynamicChooseOption>

            <tags:dynamicChooseOption optionId="scheduledEnabled">
                <li>
                    <cti:url var="startProgramUrl" value="/dr/program/start/details">
                        <cti:param name="programId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".program.startProgram.title" 
                                           dialogId="drDialog"
                                           actionUrl="${startProgramUrl}" 
                                           icon="icon-control-play-blue"
                                           labelKey=".programDetail.actions.start"/>
                </li>
                <li>
                    <cti:url var="stopProgramUrl" value="/dr/program/stop/details">
                        <cti:param name="programId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".program.stopProgram.title" 
                                           dialogId="drDialog"
                                           actionUrl="${stopProgramUrl}" 
                                           icon="icon-control-stop-blue"
                                           labelKey=".programDetail.actions.stop"/>
                </li>
            </tags:dynamicChooseOption>

            <tags:dynamicChooseOption optionId="enabled">
                <li>
                    <cti:url var="startProgramUrl" value="/dr/program/start/details">
                        <cti:param name="programId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".program.startProgram.title" 
                                           dialogId="drDialog"
                                           actionUrl="${startProgramUrl}" 
                                           icon="icon-control-play-blue"
                                           labelKey=".programDetail.actions.start"/>
                </li>
                <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" title="${notRunning}">${stopAction}</cm:dropdownOption>
            </tags:dynamicChooseOption>

            <tags:dynamicChooseOption optionId="runningDisabled">
                <cm:dropdownOption icon="icon-control-play-blue" disabled="true" title="${alreadyRunning}">${startAction}</cm:dropdownOption>
                <li>
                    <cti:url var="stopProgramUrl" value="/dr/program/stop/details">
                        <cti:param name="programId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".program.stopProgram.title" 
                                           dialogId="drDialog"
                                           actionUrl="${stopProgramUrl}" 
                                           icon="icon-control-stop-blue"
                                           labelKey=".programDetail.actions.stop"/>
                </li>
            </tags:dynamicChooseOption>

            <tags:dynamicChooseOption optionId="scheduledDisabled">
                <li>
                    <cti:url var="startProgramUrl" value="/dr/program/start/details">
                        <cti:param name="programId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".program.startProgram.title" 
                                           dialogId="drDialog"
                                           actionUrl="${startProgramUrl}" 
                                           icon="icon-control-play-blue"
                                           labelKey=".programDetail.actions.start"/>
                 </li>
                <li>
                    <cti:url var="stopProgramUrl" value="/dr/program/stop/details">
                        <cti:param name="programId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".program.stopProgram.title" 
                                           dialogId="drDialog"
                                           actionUrl="${stopProgramUrl}" 
                                           icon="icon-control-stop-blue"
                                           labelKey=".programDetail.actions.stop"/>
                </li>
            </tags:dynamicChooseOption>

            <tags:dynamicChooseOption optionId="disabled">
                <li>
                    <cti:url var="startProgramUrl" value="/dr/program/start/details">
                        <cti:param name="programId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startProgram.title" 
                                           dialogId="drDialog"
                                           actionUrl="${startProgramUrl}"
                                           icon="icon-control-play-blue"
                                           labelKey=".programDetail.actions.start"/>
                </li>
                <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" title="${notRunning}">${stopAction}</cm:dropdownOption>
            </tags:dynamicChooseOption>

        </tags:dynamicChoose>
    </cm:dropdown>
</cti:checkPaoAuthorization>

<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}" invert="true">
    <cm:dropdown containerCssClass="fr">
        <cm:dropdownOption icon="icon-control-play-blue" disabled="true" title="${noProgramControl}">${startAction}</cm:dropdownOption>
        <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" title="${noProgramControl}">${stopAction}</cm:dropdownOption>
    </cm:dropdown>
</cti:checkPaoAuthorization>
</cti:msgScope>