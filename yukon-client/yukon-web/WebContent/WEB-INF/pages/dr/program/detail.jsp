<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.programDetail.pageTitle" argument="${program.name}"/>
<cti:standardPage module="dr" page="programDetail" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|programs"/>

    <tags:simpleDialog id="drDialog"/>
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>
    <dr:favoriteIconJS/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
            <cti:msg key="yukon.web.modules.dr.programDetail.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/spring/dr/program/list">
            <cti:msg key="yukon.web.modules.dr.programDetail.breadcrumb.programs"/>
        </cti:crumbLink>
        <cti:crumbLink>
            <cti:msg key="yukon.web.modules.dr.programDetail.breadcrumb.program"
                htmlEscape="true" argument="${program.name}"/>
        </cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.programDetail.program"
        htmlEscape="true" argument="${program.name}"/></h2>
    <br>

    <c:set var="programId" value="${program.paoIdentifier.paoId}"/>
    <table cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.programDetail.heading.info"/>
                <tags:abstractContainer type="box" title="${boxTitle}">
                    <tags:nameValueContainer>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.state"/>
                        <tags:nameValue name="${fieldName}" nameColumnWidth="150px">
                            <cti:dataUpdaterValue identifier="${programId}/STATE" type="DR_PROGRAM"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.start"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue identifier="${programId}/START" type="DR_PROGRAM"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.stop"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue identifier="${programId}/STOP" type="DR_PROGRAM"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.currentGear"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue identifier="${programId}/CURRENT_GEAR" type="DR_PROGRAM"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.priority"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue identifier="${programId}/PRIORITY" type="DR_PROGRAM"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.reduction"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue identifier="${programId}/REDUCTION" type="DR_PROGRAM"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.loadCapacity"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue identifier="${programId}/LOAD_CAPACITY" type="DR_PROGRAM"/>
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </tags:abstractContainer>
            </td>
            <td width="10">&nbsp;</td>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.programDetail.heading.actions"/>
                <tags:abstractContainer type="box" title="${boxTitle}">
                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${program}">
                        <tags:dynamicChoose updaterString="DR_PROGRAM/${programId}/SHOW_ACTION" suffix="${programId}">
                            <tags:dynamicChooseOption optionId="unknown">
                                <cti:msg var="programUnknown" key="yukon.web.modules.dr.programDetail.unknown"/>
                                <div class="subtleGray" title="${programUnknown}">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.start"/>
                                </div>
                                <div class="subtleGray" title="${programUnknown}">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.stop"/>
                                </div>
                                <div class="subtleGray" title="${programUnknown}">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.changeGearsIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.changeGears"/>
                                </div>
                                <div class="subtleGray" title="${programUnknown}">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.disableIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.disable"/>
                                </div>
                            </tags:dynamicChooseOption>
                            <tags:dynamicChooseOption optionId="runningEnabled">
                                <cti:url var="stopProgramUrl" value="/spring/dr/program/stopProgramDetails">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopProgram.title" 
                                                       dialogId="drDialog" 
                                                       actionUrl="${stopProgramUrl}" 
                                                       logoKey="yukon.web.modules.dr.programDetail.actions.stopIcon"
                                                       labelKey="yukon.web.modules.dr.programDetail.actions.stop"/>
                                <br>

                                <cti:url var="changeGearsUrl" value="/spring/dr/program/getChangeGearValue">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.getChangeGearValue.title" 
                                                       dialogId="drDialog" 
                                                       actionUrl="${changeGearsUrl}" 
                                                       logoKey="yukon.web.modules.dr.programDetail.actions.changeGearsIcon"
                                                       labelKey="yukon.web.modules.dr.programDetail.actions.changeGears"/>
                                <br>
                                
                                <cti:url var="sendDisableUrl" value="/spring/dr/program/sendEnableConfirm">
                                    <cti:param name="programId" value="${programId}"/>
                                    <cti:param name="isEnabled" value="false"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendDisableConfirm.title" 
                                                       dialogId="drDialog" 
                                                       actionUrl="${sendDisableUrl}" 
                                                       logoKey="yukon.web.modules.dr.programDetail.actions.disableIcon"
                                                       labelKey="yukon.web.modules.dr.programDetail.actions.disable"/>
                                <br>
                            </tags:dynamicChooseOption>
                            <tags:dynamicChooseOption optionId="enabled">
                                <cti:url var="startProgramUrl" value="/spring/dr/program/startProgramDetails">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startProgram.title" 
                                                       dialogId="drDialog" 
                                                       actionUrl="${startProgramUrl}" 
                                                       logoKey="yukon.web.modules.dr.programDetail.actions.startIcon"
                                                       labelKey="yukon.web.modules.dr.programDetail.actions.start"/>
                                <br>
    
                                <cti:url var="stopProgramUrl" value="/spring/dr/program/stopProgramDetails">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopProgram.title" 
                                                       dialogId="drDialog" 
                                                       actionUrl="${stopProgramUrl}" 
                                                       logoKey="yukon.web.modules.dr.programDetail.actions.stopIcon"
                                                       labelKey="yukon.web.modules.dr.programDetail.actions.stop"/>
                                <br>

                                <cti:msg var="changeGearsDisabled" key="yukon.web.modules.dr.programDetail.actions.changeGears.disabled"/>
                                <span id="changeGearDisabled" title="${changeGearsDisabled}">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.changeGearsIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.changeGears"/>
                                </span>
                                <br>

                                <cti:url var="sendDisableUrl" value="/spring/dr/program/sendEnableConfirm">
                                    <cti:param name="programId" value="${programId}"/>
                                    <cti:param name="isEnabled" value="false"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendDisableConfirm.title" 
                                                       dialogId="drDialog" 
                                                       actionUrl="${sendDisableUrl}" 
                                                       logoKey="yukon.web.modules.dr.programDetail.actions.disableIcon"
                                                       labelKey="yukon.web.modules.dr.programDetail.actions.disable"/>
                                <br>
                            </tags:dynamicChooseOption>
                            <tags:dynamicChooseOption optionId="runningDisabled">
                                <cti:url var="stopProgramUrl" value="/spring/dr/program/stopProgramDetails">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopProgram.title" 
                                                       dialogId="drDialog" 
                                                       actionUrl="${stopProgramUrl}" 
                                                       logoKey="yukon.web.modules.dr.programDetail.actions.stopIcon"
                                                       labelKey="yukon.web.modules.dr.programDetail.actions.stop"/>
                                <br>

                                <cti:url var="changeGearsUrl" value="/spring/dr/program/getChangeGearValue">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.getChangeGearValue.title" 
                                                       dialogId="drDialog" 
                                                       actionUrl="${changeGearsUrl}" 
                                                       logoKey="yukon.web.modules.dr.programDetail.actions.changeGearsIcon"
                                                       labelKey="yukon.web.modules.dr.programDetail.actions.changeGears"/>
                                <br>

                                <cti:url var="sendEnableUrl" value="/spring/dr/program/sendEnableConfirm">
                                    <cti:param name="programId" value="${programId}"/>
                                    <cti:param name="isEnabled" value="true"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendEnableConfirm.title" 
                                                       dialogId="drDialog" 
                                                       actionUrl="${sendEnableUrl}" 
                                                       logoKey="yukon.web.modules.dr.programDetail.actions.enableIcon"
                                                       labelKey="yukon.web.modules.dr.programDetail.actions.enable"/>
                                <br>
                            </tags:dynamicChooseOption>
                            <tags:dynamicChooseOption optionId="disabled">
                                <cti:url var="startProgramUrl" value="/spring/dr/program/startProgramDetails">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startProgram.title" 
                                                       dialogId="drDialog" 
                                                       actionUrl="${startProgramUrl}" 
                                                       logoKey="yukon.web.modules.dr.programDetail.actions.startIcon"
                                                       labelKey="yukon.web.modules.dr.programDetail.actions.start"/>
                                <br>

                                <cti:url var="stopProgramUrl" value="/spring/dr/program/stopProgramDetails">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopProgram.title" 
                                                       dialogId="drDialog" 
                                                       actionUrl="${stopProgramUrl}" 
                                                       logoKey="yukon.web.modules.dr.programDetail.actions.stopIcon"
                                                       labelKey="yukon.web.modules.dr.programDetail.actions.stop"/>
                                <br>

                                <cti:msg var="changeGearsDisabled" key="yukon.web.modules.dr.programDetail.actions.changeGears.disabled"/>
                                <span id="changeGearDisabled" title="${changeGearsDisabled}">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.changeGearsIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.changeGears"/>
                                </span>
                                <br>

                                <cti:url var="sendEnableUrl" value="/spring/dr/program/sendEnableConfirm">
                                    <cti:param name="programId" value="${programId}"/>
                                    <cti:param name="isEnabled" value="true"/>
                                </cti:url>
                                <a id="enableLink_${programId}" href="javascript:void(0)" class="simpleLink"
                                    onclick="openSimpleDialog('drDialog', '${sendEnableUrl}', '<cti:msg key="yukon.web.modules.dr.program.sendEnableConfirm.title"/>')">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.enableIcon"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.enable"/>
                                </a>
                                <br>
                            </tags:dynamicChooseOption>
                        </tags:dynamicChoose>
                    </cti:checkPaoAuthorization>                    
                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${program}" invert="true">
                        <cti:msg var="noProgramControl" key="yukon.web.modules.dr.programDetail.noControl"/>
                        <div class="subtleGray" title="${noProgramControl}">
                            <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.programDetail.actions.start"/>
                        </div>
                        <div class="subtleGray" title="${noProgramControl}">
                            <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.programDetail.actions.stop"/>
                        </div>
                        <div class="subtleGray" title="${noProgramControl}">
                            <cti:logo key="yukon.web.modules.dr.programDetail.actions.changeGearsIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.programDetail.actions.changeGears"/>
                        </div>
                        <div class="subtleGray" title="${noProgramControl}">
                            <cti:logo key="yukon.web.modules.dr.programDetail.actions.disableIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.programDetail.actions.disable"/>
                        </div>
                    </cti:checkPaoAuthorization>
                    <dr:favoriteIcon paoId="${programId}" includeText="true"/><br>
                </tags:abstractContainer>
            </td>
        </tr>
    </table>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.modules.dr.programDetail.heading.loadGroups"/>
    <c:set var="baseUrl" value="/spring/dr/program/detail"/>
    <%@ include file="../loadGroup/loadGroupList.jspf" %>
    <br>

    <c:if test="${empty parentControlArea}">
        <p><cti:msg key="yukon.web.modules.dr.programDetail.parents.noControlArea"/></p>
    </c:if>
    <c:if test="${!empty parentControlArea}">
        <p><cti:msg key="yukon.web.modules.dr.programDetail.parents.controlArea"/></p>
        <c:url var="controlAreaURL" value="/spring/dr/controlArea/detail">
            <c:param name="controlAreaId" value="${parentControlArea.paoIdentifier.paoId}"/>
        </c:url>
        <a href="${controlAreaURL}"><spring:escapeBody htmlEscape="true">${parentControlArea.name}</spring:escapeBody></a><br>
    </c:if>
    <br>

    <c:if test="${empty parentScenarios}">
        <p><cti:msg key="yukon.web.modules.dr.programDetail.parents.noScenarios"/></p>
    </c:if>
    <c:if test="${!empty parentScenarios}">
        <p><cti:msg key="yukon.web.modules.dr.programDetail.parents.scenarios"/></p>
        <c:forEach var="parentScenario" items="${parentScenarios}">
            <c:url var="scenarioURL" value="/spring/dr/scenario/detail">
                <c:param name="scenarioId" value="${parentScenario.paoIdentifier.paoId}"/>
            </c:url>
            <a href="${scenarioURL}"><spring:escapeBody htmlEscape="true">${parentScenario.name}</spring:escapeBody></a><br>
        </c:forEach>
    </c:if>

</cti:standardPage>
