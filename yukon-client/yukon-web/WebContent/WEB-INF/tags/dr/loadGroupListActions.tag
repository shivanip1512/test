<%@ tag body-content="empty" trimDirectiveWhitespaces="true" dynamic-attributes="attrs" %>

<%@ attribute name="pao" required="true" type="com.cannontech.common.pao.YukonPao" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr">

<c:set var="paoId" value="${pao.paoIdentifier.paoId}"/>
<cti:msg2 var="loadGroupUnknown" key=".loadGroupDetail.unknown"/>
<cti:msg2 var="loadGroupDisabled" key=".loadGroupDetail.disabled"/>
<cti:msg2 var="noLoadGroupControl" key=".loadGroupDetail.noControl"/>
<cti:msg2 var="startAction" key=".loadGroupDetail.actions.sendShed"/>
<cti:msg2 var="stopAction" key=".loadGroupDetail.actions.sendRestore"/>

<%-- perhaps we should display "macro groups not controllable" or some such nonsense --%>
<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}">
    <cm:dropdown triggerClasses="fr">
        <tags:dynamicChoose updaterString="DR_LOADGROUP/${paoId}/SHOW_ACTION" suffix="${paoId}">
    
            <tags:dynamicChooseOption optionId="unknown">
                <%-- All actions are disabled when the DR Program is unknown --%>
                <cm:dropdownOption icon="icon-control-play-blue" disabled="true" title="${loadGroupUnknown}">
                    ${startAction}
                </cm:dropdownOption>
                <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" title="${loadGroupUnknown}">
                    ${stopAction}
                </cm:dropdownOption>
            </tags:dynamicChooseOption>
    
            <tags:dynamicChooseOption optionId="enabled">
                <li>
                    <cti:url var="sendShedUrl" value="/dr/loadGroup/sendShedConfirm">
                        <cti:param name="loadGroupId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".loadGroup.sendShedConfirm.title" 
                                       dialogId="drDialog" actionUrl="${sendShedUrl}" 
                                       icon="icon-control-play-blue" labelKey=".loadGroupDetail.actions.sendShed"/>
                </li>
    
                <li>
                    <cti:url var="sendRestoreUrl" value="/dr/loadGroup/sendRestoreConfirm">
                        <cti:param name="loadGroupId" value="${paoId}"/>
                    </cti:url>
                    <tags:simpleDialogLink titleKey=".loadGroup.sendRestoreConfirm.title" 
                                       dialogId="drDialog" actionUrl="${sendRestoreUrl}" 
                                       icon="icon-control-stop-blue" labelKey=".loadGroupDetail.actions.sendRestore"/>
                </li>
            </tags:dynamicChooseOption>
    
            <tags:dynamicChooseOption optionId="disabled">
                <cm:dropdownOption icon="icon-control-play-blue" disabled="true" title="${loadGroupDisabled}">
                    ${startAction}
                </cm:dropdownOption>
                <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" title="${loadGroupDisabled}">
                    ${stopAction}
                </cm:dropdownOption>
            </tags:dynamicChooseOption>
    
        </tags:dynamicChoose>
    </cm:dropdown>
</cti:checkPaoAuthorization>

<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}" invert="true">
    <cm:dropdown triggerClasses="fr">
        <cm:dropdownOption icon="icon-control-play-blue" disabled="true" title="${loadGroupDisabled}">
            ${startAction}
        </cm:dropdownOption>
        <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" title="${loadGroupDisabled}">
            ${stopAction}
        </cm:dropdownOption>
    </cm:dropdown>
</cti:checkPaoAuthorization>
</cti:msgScope>

