<%@ tag body-content="empty" trimDirectiveWhitespaces="true" dynamic-attributes="attrs" %>

<%@ attribute name="pao" required="true" type="com.cannontech.common.pao.YukonPao" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr">
<c:set var="paoId" value="${pao.paoIdentifier.paoId}"/>

<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}">
    <cm:dropdown triggerClasses="fr">
        <div data-start-action="off" data-pao-id="${paoId}" class="dn">
            <cm:dropdownOption icon="icon-control-play-blue" disabled="true">
                <cti:msg2 key=".controlAreaDetail.actions.start"/>
            </cm:dropdownOption>
        </div>
        <div data-start-action="on" data-pao-id="${paoId}">
            <li>
                <cti:url var="startControlAreaUrl" value="/dr/program/start/multipleDetails">
                    <cti:param name="controlAreaId" value="${paoId}"/>
                </cti:url>
                <tags:simpleDialogLink titleKey=".program.startMultiplePrograms.title" 
                       dialogId="drDialog" actionUrl="${startControlAreaUrl}" 
                       icon="icon-control-play-blue" labelKey=".controlAreaDetail.actions.start"/>
            </li>
        </div>
        <div data-stop-action="off" data-pao-id="${paoId}" class="dn">
            <cm:dropdownOption icon="icon-control-stop-blue" disabled="true">
                <cti:msg2 key=".controlAreaDetail.actions.stop"/>
            </cm:dropdownOption>
        </div>
        <div data-stop-action="on" data-pao-id="${paoId}">
            <li>
                <cti:url var="stopControlAreaUrl" value="/dr/program/stop/multipleDetails">
                    <cti:param name="controlAreaId" value="${paoId}"/>
                </cti:url>
                <tags:simpleDialogLink titleKey=".program.stopMultiplePrograms.title" 
                           dialogId="drDialog" actionUrl="${stopControlAreaUrl}"
                           icon="icon-control-stop-blue" labelKey=".controlAreaDetail.actions.stop"/>
            </li>
        </div>
    </cm:dropdown>
    <cti:dataUpdaterCallback function="yukon.dr.dataUpdater.showAction.updateControlAreaMenu(${paoId})" 
        initialize="true" state="DR_CONTROLAREA/${paoId}/SHOW_ACTION"/>
</cti:checkPaoAuthorization>

<cti:msg2 var="noControlAreaControl" key=".controlAreaDetail.noControl"/>
<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}" invert="true">
    <cm:dropdown triggerClasses="fr">
        <cm:dropdownOption icon="icon-control-play-blue" disabled="true" title="${noControlAreaControl}">
            <cti:msg2 key=".controlAreaDetail.actions.start"/>
        </cm:dropdownOption>
        <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" title="${noControlAreaControl}">
            <cti:msg2 key=".controlAreaDetail.actions.stop"/>
        </cm:dropdownOption>
    </cm:dropdown>
</cti:checkPaoAuthorization>
</cti:msgScope>
