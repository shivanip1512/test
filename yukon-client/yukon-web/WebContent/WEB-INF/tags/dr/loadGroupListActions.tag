<%@ tag body-content="empty" trimDirectiveWhitespaces="true" dynamic-attributes="attrs" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="pao" required="true" type="com.cannontech.common.pao.YukonPao" %>
<%@ attribute name="allowShed" required="false" type="java.lang.Boolean" %>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean" description="If 'true' cog will be disabled with no options in it.  Default: false." %>

<cti:default var="disabled" value="${false}"/>

<cti:msgScope paths="modules.dr">

<c:if test="${empty allowShed}">
    <c:set var="allowShed" value="true"/>
</c:if>

<c:set var="paoId" value="${pao.paoIdentifier.paoId}"/>
<cti:msg2 var="loadGroupUnknown" key=".loadGroupDetail.unknown"/>
<cti:msg2 var="loadGroupDisabled" key=".loadGroupDetail.disabled"/>
<cti:msg2 var="noLoadGroupControl" key=".loadGroupDetail.noControl"/>
<cti:msg2 var="startAction" key=".loadGroupDetail.actions.sendShed"/>
<cti:msg2 var="stopAction" key=".loadGroupDetail.actions.sendRestore"/>

<c:set var="cogClass" value="${disabled ? 'very-disabled-look' : ''}"/>

<%-- perhaps we should display "macro groups not controllable" or some such nonsense --%>
<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}">
    <cm:dropdown triggerClasses="${cogClass}">
        <c:if test="${!disabled}">
            <tags:dynamicChoose updaterString="DR_LOADGROUP/${paoId}/SHOW_ACTION" suffix="${paoId}">
        
                <tags:dynamicChooseOption optionId="unknown">
                    <%-- All actions are disabled when the DR Program is unknown --%>
                    <c:if test="${allowShed}">
                        <cm:dropdownOption icon="icon-control-play-blue" disabled="true" title="${loadGroupUnknown}">
                            ${startAction}
                        </cm:dropdownOption>
                    </c:if>
                    <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" title="${loadGroupUnknown}">
                        ${stopAction}
                    </cm:dropdownOption>
                </tags:dynamicChooseOption>
        
                <tags:dynamicChooseOption optionId="enabled">
                    <c:if test="${allowShed}">
                        <li>
                            <cti:url var="sendShedUrl" value="/dr/loadGroup/sendShedConfirm">
                                <cti:param name="loadGroupId" value="${paoId}"/>
                            </cti:url>
                            <tags:simpleDialogLink titleKey=".loadGroup.sendShedConfirm.title" 
                                               dialogId="drDialog" actionUrl="${sendShedUrl}" 
                                               icon="icon-control-play-blue" labelKey=".loadGroupDetail.actions.sendShed"/>
                        </li>
                    </c:if>
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
                    <c:if test="${allowShed}">
                        <cm:dropdownOption icon="icon-control-play-blue" disabled="true" title="${loadGroupDisabled}">
                            ${startAction}
                        </cm:dropdownOption>
                    </c:if>
                    <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" title="${loadGroupDisabled}">
                        ${stopAction}
                    </cm:dropdownOption>
                </tags:dynamicChooseOption>
        
            </tags:dynamicChoose>
        </c:if>
    </cm:dropdown>
</cti:checkPaoAuthorization>

<cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${pao}" invert="true">
    <cm:dropdown triggerClasses="${cogClass}">
        <c:if test="${!disabled}">
            <c:if test="${allowShed}">
                <cm:dropdownOption icon="icon-control-play-blue" disabled="true" title="${loadGroupDisabled}">
                    ${startAction}
                </cm:dropdownOption>
            </c:if>
            <cm:dropdownOption icon="icon-control-stop-blue" disabled="true" title="${loadGroupDisabled}">
                ${stopAction}
            </cm:dropdownOption>
        </c:if>
    </cm:dropdown>
</cti:checkPaoAuthorization>
</cti:msgScope>

