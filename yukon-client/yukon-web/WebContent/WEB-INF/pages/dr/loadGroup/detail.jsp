<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.loadGroupDetail.pageTitle" argument="${loadGroup.name}"/>
<cti:standardPage module="dr" page="loadGroupDetail" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|loadGroups"/>

    <cti:includeScript link="/JavaScript/demandResponseAction.js"/>

    <tags:iframeDialog id="drDialog"/>

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
                    <span id="actionSpan_${loadGroupId}">
                        <cti:url var="sendShedUrl" value="/spring/dr/loadGroup/sendShedConfirm">
                            <cti:param name="loadGroupId" value="${loadGroupId}"/>
                        </cti:url>
                        <a href="javascript:void(0)"
                            onclick="openIFrameDialog('drDialog', '${sendShedUrl}', '<cti:msg key="yukon.web.modules.dr.loadGroup.sendShedConfirm.title"/>')">
                            <cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.sendShed"/>
                        </a><br>
    
                        <cti:url var="sendRestoreUrl" value="/spring/dr/loadGroup/sendRestoreConfirm">
                            <cti:param name="loadGroupId" value="${loadGroupId}"/>
                        </cti:url>
                        <a href="javascript:void(0)"
                            onclick="openIFrameDialog('drDialog', '${sendRestoreUrl}', '<cti:msg key="yukon.web.modules.dr.loadGroup.sendRestoreConfirm.title"/>')">
                            <cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.sendRestore"/>
                        </a><br>
                        
                        <cti:url var="sendEnableUrl" value="/spring/dr/loadGroup/sendEnableConfirm">
                            <cti:param name="loadGroupId" value="${loadGroupId}"/>
                            <cti:param name="isEnabled" value="true"/>
                        </cti:url>
                        <a id="enableLink_${loadGroupId}" href="javascript:void(0)"
                            onclick="openIFrameDialog('drDialog', '${sendEnableUrl}', '<cti:msg key="yukon.web.modules.dr.loadGroup.sendEnableConfirm.title"/>')">
                            <cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.enable"/>
                        </a>
                        <cti:url var="sendDisableUrl" value="/spring/dr/loadGroup/sendEnableConfirm">
                            <cti:param name="loadGroupId" value="${loadGroupId}"/>
                            <cti:param name="isEnabled" value="false"/>
                        </cti:url>
                        <a id="disableLink_${loadGroupId}" href="javascript:void(0)"
                            onclick="openIFrameDialog('drDialog', '${sendDisableUrl}', '<cti:msg key="yukon.web.modules.dr.loadGroup.sendDisableConfirm.title"/>')">
                            <cti:msg key="yukon.web.modules.dr.loadGroupDetail.actions.disable"/>
                        </a><br>
                        <cti:dataUpdaterCallback function="updateEnabled('${loadGroupId}')" initialize="true" state="DR_LOADGROUP/${loadGroupId}/ENABLED" />
                    </span>
                </tags:abstractContainer>
            </td>
        </tr>
    </table>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.modules.dr.loadGroupDetail.heading.parents"/>
    <tags:abstractContainer type="box" title="${boxTitle}">

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
    </tags:abstractContainer>
</cti:standardPage>
