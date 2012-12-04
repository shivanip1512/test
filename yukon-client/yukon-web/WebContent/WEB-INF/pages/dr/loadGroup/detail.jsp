<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="dr" page="loadGroupDetail">

    <tags:simpleDialog id="drDialog"/>
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>
    <dr:favoriteIconSetup/>
	
	<c:set var="loadGroupId" value="${loadGroup.paoIdentifier.paoId}"/>
	<tags:layoutHeadingPrefixPart>
		<dr:favoriteIcon paoId="${loadGroupId}" isFavorite="${isFavorite}"/>
	</tags:layoutHeadingPrefixPart>
	
    <table class="widgetColumns">
        <c:if test="${loadGroup.paoIdentifier.paoType != 'MACRO_GROUP'}">
        <tr>
            <td class="widgetColumnCell first" valign="top">

                <%-- Load Group Info section --%>

                <div class="widgetContainer">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.loadGroupDetail.heading.info"/>
                <tags:abstractContainer type="box" title="${boxTitle}">
                    <tags:nameValueContainer>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.loadGroupDetail.info.state"/>
                        <cti:checkRolesAndProperties value="LOAD_GROUP_STATE">
                            <tags:nameValue name="${fieldName}" nameColumnWidth="150px">
                                <dr:loadGroupState loadGroupId="${loadGroupId}"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="LOAD_GROUP_LAST_ACTION">
                            <cti:msg var="fieldName" key="yukon.web.modules.dr.loadGroupDetail.info.lastAction"/>
                            <tags:nameValue name="${fieldName}">
                                <cti:dataUpdaterValue type="DR_LOADGROUP" identifier="${loadGroupId}/LAST_ACTION"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="LOAD_GROUP_CONTROL_STATISTICS">
                            <cti:msg var="fieldName" key="yukon.web.modules.dr.loadGroupDetail.info.controlStatistics"/>
                            <tags:nameValue name="${fieldName}">
                                <cti:dataUpdaterValue type="DR_LOADGROUP" identifier="${loadGroupId}/CONTROL_STATISTICS"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="LOAD_GROUP_REDUCTION">
                            <cti:msg var="fieldName" key="yukon.web.modules.dr.loadGroupDetail.info.reduction"/>
                            <tags:nameValue name="${fieldName}">
                                <cti:dataUpdaterValue type="DR_LOADGROUP" identifier="${loadGroupId}/REDUCTION"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                        <%--
                        <cti:checkRolesAndProperties value="LOAD_GROUP_LOAD_CAPACITY">
                            <cti:msg var="fieldName" key="yukon.web.modules.dr.loadGroupDetail.info.loadCapacity"/>
                            <tags:nameValue name="${fieldName}">
                                <cti:dataUpdaterValue type="DR_LOADGROUP" identifier="${loadGroupId}/LOAD_CAPACITY"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                        --%>
                    </tags:nameValueContainer>
                </tags:abstractContainer>
                </div>
            </td>
            <td class="widgetColumnCell last" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.loadGroupDetail.heading.actions"/>

                <%--
                    Load Group Actions section each action has a simpleDialogLink that
                    pops open a dialog for the action.  The available actions are based
                    on the dynamically updated SHOW_ACTION value
                --%>

                <div class="widgetContainer">
                <tags:abstractContainer type="box" title="${boxTitle}">
                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${loadGroup}">

                        <%-- Actions are enabled only if the user has CONTROL_COMMAND for LM objects --%>

                        <tags:dynamicChoose updaterString="DR_LOADGROUP/${loadGroupId}/SHOW_ACTION" suffix="${loadGroupId}">
                            <tags:dynamicChooseOption optionId="unknown">

                                <%-- Actions are disabled when Load Management doesn't know about the Load group --%>

                                <cti:msg var="loadGroupUnknown" key="yukon.web.modules.dr.loadGroupDetail.unknown"/>
                                <div class="subtle" title="${loadGroupUnknown}">
                                    <cti:logo key="yukon.web.modules.dr.loadGroupDetail.actions.sendShedIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.sendShed"/>
                                </div>
                                <div class="subtle" title="${loadGroupUnknown}">
                                    <cti:logo key="yukon.web.modules.dr.loadGroupDetail.actions.sendRestoreIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.sendRestore"/>
                                </div>
                                <div class="subtle" title="${loadGroupUnknown}">
                                    <cti:logo key="yukon.web.modules.dr.loadGroupDetail.actions.disableIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.disable"/>
                                </div>
                            </tags:dynamicChooseOption>
                            <tags:dynamicChooseOption optionId="enabled">

                                <%-- Actions shown when the Load Group is enabled --%>

                                <cti:url var="sendShedUrl" value="/dr/loadGroup/sendShedConfirm">
                                    <cti:param name="loadGroupId" value="${loadGroupId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.loadGroup.sendShedConfirm.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${sendShedUrl}"
                                                       logoKey="yukon.web.modules.dr.loadGroupDetail.actions.sendShedIcon"
                                                       labelKey="yukon.web.modules.dr.loadGroupDetail.actions.sendShed"/>
                                <br>

                                <cti:url var="sendRestoreUrl" value="/dr/loadGroup/sendRestoreConfirm">
                                    <cti:param name="loadGroupId" value="${loadGroupId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.loadGroup.sendRestoreConfirm.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${sendRestoreUrl}"
                                                       logoKey="yukon.web.modules.dr.loadGroupDetail.actions.sendRestoreIcon"
                                                       labelKey="yukon.web.modules.dr.loadGroupDetail.actions.sendRestore"/>
                                <br>

                                <cti:url var="sendDisableUrl" value="/dr/loadGroup/sendEnableConfirm">
                                    <cti:param name="loadGroupId" value="${loadGroupId}"/>
                                    <cti:param name="isEnabled" value="false"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.loadGroup.sendDisableConfirm.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${sendDisableUrl}"
                                                       logoKey="yukon.web.modules.dr.loadGroupDetail.actions.disableIcon"
                                                       labelKey="yukon.web.modules.dr.loadGroupDetail.actions.disable"/>
                                <br>
                            </tags:dynamicChooseOption>
                            <tags:dynamicChooseOption optionId="disabled">

                                <%-- Actions shown when the Load Group is disabled --%>

                                <cti:url var="sendEnableUrl" value="/dr/loadGroup/sendEnableConfirm">
                                    <cti:param name="loadGroupId" value="${loadGroupId}"/>
                                    <cti:param name="isEnabled" value="true"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.loadGroup.sendEnableConfirm.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${sendEnableUrl}"
                                                       logoKey="yukon.web.modules.dr.loadGroupDetail.actions.enableIcon"
                                                       labelKey="yukon.web.modules.dr.loadGroupDetail.actions.enable"/>
                                <br>
                            </tags:dynamicChooseOption>
                        </tags:dynamicChoose>
                    </cti:checkPaoAuthorization>
                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${loadGroup}" invert="true">

                        <%-- Actions are disabled if the user does not have CONTROL_COMMAND for LM objects --%>

                        <cti:msg var="noLoadGroupControl" key="yukon.web.modules.dr.loadGroupDetail.noControl"/>
                        <div class="subtle" title="${noLoadGroupControl}">
                            <cti:logo key="yukon.web.modules.dr.loadGroupDetail.actions.sendShedIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.sendShed"/>
                        </div>
                        <div class="subtle" title="${noLoadGroupControl}">
                            <cti:logo key="yukon.web.modules.dr.loadGroupDetail.actions.sendRestoreIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.sendRestore"/>
                        </div>
                        <div class="subtle" title="${noLoadGroupControl}">
                            <cti:logo key="yukon.web.modules.dr.loadGroupDetail.actions.disableIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.disable"/>
                        </div>
                    </cti:checkPaoAuthorization>
                </tags:abstractContainer>
                </div>
            </td>
        </tr>
        </c:if>

        <%-- Child Load Groups for the Macro Load Group --%>
        <c:if test="${loadGroup.paoIdentifier.paoType == 'MACRO_GROUP'}">
        <tr>
            <td class="widgetColumnCell" colspan="2">
                <div class="widgetContainer">
                    <p><cti:msg key="yukon.web.modules.dr.loadGroupDetail.note.macroLoadGroup"/></p><br>

                    <cti:msg var="boxTitle" key="yukon.web.modules.dr.loadGroupDetail.heading.loadGroups"/>
                    <c:set var="baseUrl" value="/dr/loadGroup/detail"/>
                    <%@ include file="../loadGroup/loadGroupList.jspf" %>
                </div>
            </td>
        </tr>
        </c:if>

        <%-- Parent Programs and macro load groups --%>
        <tr>
            <td class="widgetColumnCell first" valign="top">
                <div class="widgetContainer">
                    <cti:msg var="boxTitle" key="yukon.web.modules.dr.loadGroupDetail.parents.programs"/>
                    <tags:abstractContainer title="${boxTitle}" type="box">
                        <c:if test="${empty parentPrograms}">
                            <p><cti:msg key="yukon.web.modules.dr.loadGroupDetail.parents.noPrograms"/></p>
                        </c:if>
                        <c:if test="${!empty parentPrograms}">
                            <c:forEach var="parentProgram" items="${parentPrograms}">
                                <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentProgram}">
                                    <c:url var="programURL" value="/dr/program/detail">
                                        <c:param name="programId" value="${parentProgram.paoIdentifier.paoId}"/>
                                    </c:url>
                                    <a href="${programURL}"><spring:escapeBody htmlEscape="true">${parentProgram.name}</spring:escapeBody></a><br>
                                </cti:checkPaoAuthorization>
                                <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentProgram}" invert="true">
                                    <cti:msg var="noParentPermission" key="yukon.web.modules.dr.loadGroupDetail.parents.noPermission"/>
                                    <span title="${noParentPermission}">
                                        <spring:escapeBody htmlEscape="true">${parentProgram.name}</spring:escapeBody>
                                    </span>
                                </cti:checkPaoAuthorization>
                            </c:forEach>
                        </c:if>
                    </tags:abstractContainer>
                </div>
            </td>
            <td class="widgetColumnCell last" valign="top">
                <div class="widgetContainer">
                    <cti:msg var="boxTitle" key="yukon.web.modules.dr.loadGroupDetail.parents.macroLoadGroups"/>
                    <tags:abstractContainer title="${boxTitle}" type="box">
                        <c:if test="${empty parentLoadGroups}">
                            <p><cti:msg key="yukon.web.modules.dr.loadGroupDetail.parents.noMacroLoadGroups"/></p>
                        </c:if>
                        <c:if test="${!empty parentLoadGroups}">
                            <c:forEach var="parentLoadGroup" items="${parentLoadGroups}">
                                <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentLoadGroup}">
                                    <c:url var="loadGroupURL" value="/dr/loadGroup/detail">
                                        <c:param name="loadGroupId" value="${parentLoadGroup.paoIdentifier.paoId}"/>
                                    </c:url>
                                    <a href="${loadGroupURL}"><spring:escapeBody htmlEscape="true">${parentLoadGroup.name}</spring:escapeBody></a><br>
                                </cti:checkPaoAuthorization>
                                <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentLoadGroup}" invert="true">
                                    <cti:msg var="noParentPermission" key="yukon.web.modules.dr.loadGroupDetail.parents.noPermission"/>
                                    <span title="${noParentPermission}">
                                        <spring:escapeBody htmlEscape="true">${parentLoadGroup.name}</spring:escapeBody>
                                    </span>
                                </cti:checkPaoAuthorization>
                            </c:forEach>
                        </c:if>
                    </tags:abstractContainer>
                </div>
            </td>
        </tr>
    </table>
</cti:standardPage>
