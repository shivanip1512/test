<%@ attribute name="pao" required="true" type="com.cannontech.common.pao.YukonPao" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="paoId" value="${pao.paoIdentifier.paoId}"/>
<%-- perhaps we should display "macro groups not controllable" or some such nonsense --%>
<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}">
    <tags:dynamicChoose updaterString="DR_LOADGROUP/${paoId}/SHOW_ACTION" suffix="${paoId}">
        <tags:dynamicChooseOption optionId="unknown">
            <cti:msg var="loadGroupUnknown" key="yukon.web.modules.dr.loadGroupDetail.unknown"/>
            <span title="${loadGroupUnknown}">
                <cti:logo key="yukon.web.modules.dr.loadGroupDetail.actions.sendShedIcon.disabled"/>
            </span>
            <span class="subtleGray" title="${loadGroupUnknown}">
                <cti:logo key="yukon.web.modules.dr.loadGroupDetail.actions.sendRestoreIcon.disabled"/>
            </span>
        </tags:dynamicChooseOption>
        <tags:dynamicChooseOption optionId="enabled">
            <cti:url var="sendShedUrl" value="/spring/dr/loadGroup/sendShedConfirm">
                <cti:param name="loadGroupId" value="${paoId}"/>
            </cti:url>
            <tags:simpleDialogLink titleKey="yukon.web.modules.dr.loadGroup.sendShedConfirm.title" 
                                   dialogId="drDialog" 
                                   actionUrl="${sendShedUrl}" 
                                   logoKey="yukon.web.modules.dr.loadGroupDetail.actions.sendShedIcon"/>

            <cti:url var="sendRestoreUrl" value="/spring/dr/loadGroup/sendRestoreConfirm">
                <cti:param name="loadGroupId" value="${paoId}"/>
            </cti:url>
            <tags:simpleDialogLink titleKey="yukon.web.modules.dr.loadGroup.sendRestoreConfirm.title" 
                                   dialogId="drDialog" 
                                   actionUrl="${sendRestoreUrl}" 
                                   logoKey="yukon.web.modules.dr.loadGroupDetail.actions.sendRestoreIcon"/>
        </tags:dynamicChooseOption>
        <tags:dynamicChooseOption optionId="disabled">
            <cti:url var="sendEnableUrl" value="/spring/dr/loadGroup/sendEnableConfirm">
                <cti:param name="loadGroupId" value="${paoId}"/>
                <cti:param name="isEnabled" value="true"/>
            </cti:url>
            <tags:simpleDialogLink titleKey="yukon.web.modules.dr.loadGroup.sendEnableConfirm.title" 
                                   dialogId="drDialog" 
                                   actionUrl="${sendEnableUrl}" 
                                   logoKey="yukon.web.modules.dr.loadGroupDetail.actions.enableIcon"/>
        </tags:dynamicChooseOption>
    </tags:dynamicChoose>
</cti:checkPaoAuthorization>
<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}" invert="true">
    <cti:msg var="noLoadGroupControl" key="yukon.web.modules.dr.loadGroupDetail.noControl"/>
    <span class="subtleGray" title="${noLoadGroupControl}">
        <cti:logo key="yukon.web.modules.dr.loadGroupDetail.actions.sendShedIcon.disabled"/>
    </span>
    <span class="subtleGray" title="${noLoadGroupControl}">
        <cti:logo key="yukon.web.modules.dr.loadGroupDetail.actions.sendRestoreIcon.disabled"/>
    </span>
</cti:checkPaoAuthorization>
