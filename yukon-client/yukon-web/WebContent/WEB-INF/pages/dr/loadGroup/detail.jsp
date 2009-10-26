<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.loadGroupDetail.pageTitle" argument="${loadGroup.name}"/>
<cti:standardPage module="dr" page="loadGroupDetail" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|loadgroups"/>

    <tags:simpleDialog id="drDialog"/>
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
            <cti:msg key="yukon.web.modules.dr.loadGroupDetail.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/spring/dr/home">
            <cti:msg key="yukon.web.modules.dr.loadGroupDetail.breadcrumb.drHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/spring/dr/loadGroup/list">
            <cti:msg key="yukon.web.modules.dr.loadGroupDetail.breadcrumb.loadGroups"/>
        </cti:crumbLink>
        <cti:crumbLink>
            <cti:msg key="yukon.web.modules.dr.loadGroupDetail.breadcrumb.loadGroup"
                htmlEscape="true" argument="${loadGroup.name}"/>
        </cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.loadGroupDetail.loadGroup"
        htmlEscape="true" argument="${loadGroup.name}"/></h2>
    <br>

    <c:set var="loadGroupId" value="${loadGroup.paoIdentifier.paoId}"/>
    <c:if test="${loadGroup.paoIdentifier.paoType != 'MACRO_GROUP'}">
    <table cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td width="50%" valign="top">
            
                <%-- Load Group Info section --%>
            
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
                        <cti:checkRolesAndProperties value="LOAD_GROUP_LOAD_CAPACITY">
                            <cti:msg var="fieldName" key="yukon.web.modules.dr.loadGroupDetail.info.loadCapacity"/>
                            <tags:nameValue name="${fieldName}">
                                <cti:dataUpdaterValue type="DR_LOADGROUP" identifier="${loadGroupId}/LOAD_CAPACITY"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                    </tags:nameValueContainer>
                </tags:abstractContainer>
            </td>
            <td width="10">&nbsp;</td>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.loadGroupDetail.heading.actions"/>
                
                <%-- 
                    Load Group Actions section each action has a simpleDialogLink that
                    pops open a dialog for the action.  The available actions are based
                    on the dynamically updated SHOW_ACTION value
                --%>
                
                <tags:abstractContainer type="box" title="${boxTitle}">
                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${loadGroup}">
                        
                        <%-- Actions are enabled only if the user has CONTROL_COMMAND for LM objects --%>
                    
                        <tags:dynamicChoose updaterString="DR_LOADGROUP/${loadGroupId}/SHOW_ACTION" suffix="${loadGroupId}">
                            <tags:dynamicChooseOption optionId="unknown">
                            
                                <%-- Actions are disabled when Load Management doesn't know about the Load group --%>
                            
                                <cti:msg var="loadGroupUnknown" key="yukon.web.modules.dr.loadGroupDetail.unknown"/>
                                <div class="subtleGray" title="${loadGroupUnknown}">
                                    <cti:logo key="yukon.web.modules.dr.loadGroupDetail.actions.sendShedIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.sendShed"/>
                                </div>
                                <div class="subtleGray" title="${loadGroupUnknown}">
                                    <cti:logo key="yukon.web.modules.dr.loadGroupDetail.actions.sendRestoreIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.sendRestore"/>
                                </div>
                                <div class="subtleGray" title="${loadGroupUnknown}">
                                    <cti:logo key="yukon.web.modules.dr.loadGroupDetail.actions.disableIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.disable"/>
                                </div>
                            </tags:dynamicChooseOption>
                            <tags:dynamicChooseOption optionId="enabled">
                            
                                <%-- Actions shown when the Load Group is enabled --%>
                            
                                <cti:url var="sendShedUrl" value="/spring/dr/loadGroup/sendShedConfirm">
                                    <cti:param name="loadGroupId" value="${loadGroupId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.loadGroup.sendShedConfirm.title" 
                                                       dialogId="drDialog" 
                                                       actionUrl="${sendShedUrl}" 
                                                       logoKey="yukon.web.modules.dr.loadGroupDetail.actions.sendShedIcon"
                                                       labelKey="yukon.web.modules.dr.loadGroupDetail.actions.sendShed"/>
                                <br>
            
                                <cti:url var="sendRestoreUrl" value="/spring/dr/loadGroup/sendRestoreConfirm">
                                    <cti:param name="loadGroupId" value="${loadGroupId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.loadGroup.sendRestoreConfirm.title" 
                                                       dialogId="drDialog" 
                                                       actionUrl="${sendRestoreUrl}" 
                                                       logoKey="yukon.web.modules.dr.loadGroupDetail.actions.sendRestoreIcon"
                                                       labelKey="yukon.web.modules.dr.loadGroupDetail.actions.sendRestore"/>
                                <br>
                                
                                <cti:url var="sendDisableUrl" value="/spring/dr/loadGroup/sendEnableConfirm">
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
                            
                                <cti:url var="sendEnableUrl" value="/spring/dr/loadGroup/sendEnableConfirm">
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
                        <div class="subtleGray" title="${noLoadGroupControl}">
                            <cti:logo key="yukon.web.modules.dr.loadGroupDetail.actions.sendShedIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.sendShed"/>
                        </div>
                        <div class="subtleGray" title="${noLoadGroupControl}">
                            <cti:logo key="yukon.web.modules.dr.loadGroupDetail.actions.sendRestoreIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.sendRestore"/>
                        </div>
                        <div class="subtleGray" title="${noLoadGroupControl}">
                            <cti:logo key="yukon.web.modules.dr.loadGroupDetail.actions.disableIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.disable"/>
                        </div>
                    </cti:checkPaoAuthorization>
                    <dr:favoriteIcon paoId="${loadGroupId}" isFavorite="${isFavorite}" includeText="true"/><br>
                </tags:abstractContainer>
            </td>
        </tr>
    </table>
    <br>
    </c:if>

    <c:if test="${loadGroup.paoIdentifier.paoType == 'MACRO_GROUP'}">
        <%-- Child Load Groups for the Macro Load Group --%>

        <p><cti:msg key="yukon.web.modules.dr.loadGroupDetail.note.macroLoadGroup"/></p><br>

        <cti:msg var="boxTitle" key="yukon.web.modules.dr.loadGroupDetail.heading.loadGroups"/>
        <c:set var="baseUrl" value="/spring/dr/loadGroup/detail"/>
        <%@ include file="../loadGroup/loadGroupList.jspf" %>
        <br>
    </c:if>

    <%-- Parent Programs for the Load Group --%>

    <c:if test="${empty parentPrograms}">
        <p><cti:msg key="yukon.web.modules.dr.loadGroupDetail.parents.noPrograms"/></p>
    </c:if>
    <c:if test="${!empty parentPrograms}">
        <p><cti:msg key="yukon.web.modules.dr.loadGroupDetail.parents.programs"/></p>
        <c:forEach var="parentProgram" items="${parentPrograms}">
            <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentProgram}">
                <c:url var="programURL" value="/spring/dr/program/detail">
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
    <br>

    <c:if test="${empty parentLoadGroups}">
        <p><cti:msg key="yukon.web.modules.dr.loadGroupDetail.parents.noLoadGroups"/></p>
    </c:if>
    <c:if test="${!empty parentLoadGroups}">
        <p><cti:msg key="yukon.web.modules.dr.loadGroupDetail.parents.loadGroups"/></p>
        <c:forEach var="parentLoadGroup" items="${parentLoadGroups}">
            <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentLoadGroup}">
                <c:url var="loadGroupURL" value="/spring/dr/loadGroup/detail">
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
</cti:standardPage>
