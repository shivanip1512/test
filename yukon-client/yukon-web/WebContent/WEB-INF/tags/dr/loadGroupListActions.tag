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
                <cti:param name="paoId" value="${paoId}"/>
            </cti:url>
            <a href="javascript:void(0)" class="simpleLink"
                onclick="openSimpleDialog('drDialog', '${sendShedUrl}', '<cti:msg key="yukon.web.modules.dr.loadGroup.sendShedConfirm.title"/>')">
                <cti:logo key="yukon.web.modules.dr.loadGroupDetail.actions.sendShedIcon"/>
            </a>

            <cti:url var="sendRestoreUrl" value="/spring/dr/loadGroup/sendRestoreConfirm">
                <cti:param name="paoId" value="${paoId}"/>
            </cti:url>
            <a href="javascript:void(0)" class="simpleLink"
                onclick="openSimpleDialog('drDialog', '${sendRestoreUrl}', '<cti:msg key="yukon.web.modules.dr.loadGroup.sendRestoreConfirm.title"/>')">
                <cti:logo key="yukon.web.modules.dr.loadGroupDetail.actions.sendRestoreIcon"/>
            </a>
        </tags:dynamicChooseOption>
        <tags:dynamicChooseOption optionId="disabled">
            <cti:url var="sendEnableUrl" value="/spring/dr/loadGroup/sendEnableConfirm">
                <cti:param name="paoId" value="${paoId}"/>
                <cti:param name="isEnabled" value="true"/>
            </cti:url>
            <a id="enableLink_${paoId}" href="javascript:void(0)" class="simpleLink"
                onclick="openSimpleDialog('drDialog', '${sendEnableUrl}', '<cti:msg key="yukon.web.modules.dr.loadGroup.sendEnableConfirm.title"/>')">
                <cti:logo key="yukon.web.modules.dr.loadGroupDetail.actions.enableIcon"/>
            </a>
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
