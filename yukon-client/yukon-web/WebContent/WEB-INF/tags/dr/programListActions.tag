<%@ tag body-content="empty" trimDirectiveWhitespaces="true" dynamic-attributes="attrs" %>

<%@ attribute name="pao" required="true" type="com.cannontech.common.pao.YukonPao" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr">

<c:set var="paoId" value="${pao.paoIdentifier.paoId}"/>

<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}">

    <cm:dropdown containerCssClass="fr">
        <div class="dn" data-start-action="disabled" data-pao-id="${paoId}">
            <cm:dropdownOption icon="icon-control-play-blue" disabled="true">
                <cti:msg2 key=".programDetail.actions.start"/>
            </cm:dropdownOption>
        </div>
        <div data-start-action="enabled" data-pao-id="${paoId}">
            <li>
                <cti:url var="startProgramUrl" value="/dr/program/start/details">
                    <cti:param name="programId" value="${paoId}"/>
                </cti:url>
                <tags:simpleDialogLink titleKey=".program.startProgram.title" dialogId="drDialog" 
                    actionUrl="${startProgramUrl}" icon="icon-control-play-blue"
                    labelKey=".programDetail.actions.start"/>
            </li>
        </div>
        <div class="dn" data-stop-action="disabled" data-pao-id="${paoId}">
            <cm:dropdownOption icon="icon-control-stop-blue" disabled="true">
                <cti:msg2 key=".programDetail.actions.stop"/>
            </cm:dropdownOption>
        </div>
        <div data-stop-action="enabled" data-pao-id="${paoId}">
            <li>
                 <cti:url var="stopProgramUrl" value="/dr/program/stop/details">
                    <cti:param name="programId" value="${paoId}"/>
                </cti:url>
                <tags:simpleDialogLink titleKey=".program.stopProgram.title" dialogId="drDialog"
                    actionUrl="${stopProgramUrl}" icon="icon-control-stop-blue"
                    labelKey=".programDetail.actions.stop"/>
            </li>
        </div>
    </cm:dropdown>
    <cti:dataUpdaterCallback function="yukon.dr.program.showActionDataUpdator.updateQuickLinks(${paoId})"
        initialize="true" state="DR_PROGRAM/${paoId}/SHOW_ACTION"/>
</cti:checkPaoAuthorization>

<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}" invert="true">
    <cti:msg2 var="noProgramControl" key=".programDetail.noControl"/>
    <cm:dropdown containerCssClass="fr">
        <cm:dropdownOption icon="icon-control-play-blue" disabled="true" title="${noProgramControl}">
            <cti:msg2  key=".programDetail.actions.start"/>
        </cm:dropdownOption>
        <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" title="${noProgramControl}">
            <cti:msg2  key=".programDetail.actions.stop"/>
        </cm:dropdownOption>
    </cm:dropdown>
</cti:checkPaoAuthorization>
</cti:msgScope>
