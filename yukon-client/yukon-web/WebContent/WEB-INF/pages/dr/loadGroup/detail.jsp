<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.loadGroupDetail.pageTitle" argument="${loadGroup.name}"/>
<cti:standardPage module="dr" page="loadGroupDetail" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|loadGroups"/>

    <tags:simpleDialog id="drDialog"/>
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>
    <dr:favoriteIconJS/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
            <cti:msg key="yukon.web.modules.dr.loadGroupDetail.breadcrumb.operationsHome"/>
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
    <table cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.loadGroupDetail.heading.info"/>
                <tags:abstractContainer type="box" title="${boxTitle}">
                    <tags:nameValueContainer>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.loadGroupDetail.info.state"/>
                        <tags:nameValue name="${fieldName}" nameColumnWidth="150px">
                            <cti:dataUpdaterValue type="DR_LOADGROUP" identifier="${loadGroupId}/STATE"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.loadGroupDetail.info.lastAction"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue type="DR_LOADGROUP" identifier="${loadGroupId}/LAST_ACTION"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.loadGroupDetail.info.controlStatistics"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue type="DR_LOADGROUP" identifier="${loadGroupId}/CONTROL_STATISTICS"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.loadGroupDetail.info.reduction"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue type="DR_LOADGROUP" identifier="${loadGroupId}/REDUCTION"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.loadGroupDetail.info.loadCapacity"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue type="DR_LOADGROUP" identifier="${loadGroupId}/LOAD_CAPACITY"/>
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </tags:abstractContainer>
            </td>
            <td width="10">&nbsp;</td>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.loadGroupDetail.heading.actions"/>
                <tags:abstractContainer type="box" title="${boxTitle}">
                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${loadGroup}">
                        <tags:dynamicChoose updaterString="DR_LOADGROUP/${loadGroupId}/SHOW_ACTION" suffix="${loadGroupId}">
                            <tags:dynamicChooseOption optionId="unknown">
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
                    <dr:favoriteIcon paoId="${loadGroupId}" includeText="true"/><br>
                </tags:abstractContainer>
            </td>
        </tr>
    </table>
    <br>

    <c:if test="${empty parentPrograms}">
        <p><cti:msg key="yukon.web.modules.dr.loadGroupDetail.parents.noPrograms"/></p>
    </c:if>
    <c:if test="${!empty parentPrograms}">
        <p><cti:msg key="yukon.web.modules.dr.loadGroupDetail.parents.programs"/></p>
        <c:forEach var="parentProgram" items="${parentPrograms}">
            <c:url var="programURL" value="/spring/dr/program/detail">
                <c:param name="programId" value="${parentProgram.paoIdentifier.paoId}"/>
            </c:url>
            <a href="${programURL}"><spring:escapeBody htmlEscape="true">${parentProgram.name}</spring:escapeBody></a><br>
        </c:forEach>
    </c:if>
</cti:standardPage>
