<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="dr" page="programDetail">

    <tags:simpleDialog id="drDialog"/>
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>
    <dr:favoriteIconSetup/>

    <c:set var="programId" value="${program.paoIdentifier.paoId}"/>
    <tags:layoutHeadingPrefixPart>
        <dr:favoriteIcon paoId="${programId}" isFavorite="${isFavorite}"/>
    </tags:layoutHeadingPrefixPart>

    <div class="column_12_12 clear">
        <div class="column one">

                <%-- Program Info section --%>
                <tags:boxContainer2 nameKey="heading.info">
                    <tags:nameValueContainer>
                        <cti:checkRolesAndProperties value="PROGRAM_STATE">
                            <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.state"/>
                            <tags:nameValue name="${fieldName}" nameColumnWidth="150px">
                                <dr:programState programId="${programId}"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="PROGRAM_START">
                            <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.start"/>
                            <tags:nameValue name="${fieldName}">
                                <cti:dataUpdaterValue identifier="${programId}/START" type="DR_PROGRAM"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="PROGRAM_STOP">
                            <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.stop"/>
                            <tags:nameValue name="${fieldName}">
                                <cti:dataUpdaterValue identifier="${programId}/STOP" type="DR_PROGRAM"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="PROGRAM_CURRENT_GEAR">
                            <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.currentGear"/>
                            <tags:nameValue name="${fieldName}">
                                <cti:dataUpdaterValue identifier="${programId}/CURRENT_GEAR" type="DR_PROGRAM"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="PROGRAM_PRIORITY">
                            <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.priority"/>
                            <tags:nameValue name="${fieldName}">
                                <cti:dataUpdaterValue identifier="${programId}/PRIORITY" type="DR_PROGRAM"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="PROGRAM_REDUCTION">
                            <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.reduction"/>
                            <tags:nameValue name="${fieldName}">
                                <cti:dataUpdaterValue identifier="${programId}/REDUCTION" type="DR_PROGRAM"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                        <%--
                        <cti:checkRolesAndProperties value="PROGRAM_LOAD_CAPACITY">
                            <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.loadCapacity"/>
                            <tags:nameValue name="${fieldName}">
                                <cti:dataUpdaterValue identifier="${programId}/LOAD_CAPACITY" type="DR_PROGRAM"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                        --%>
                    </tags:nameValueContainer>
                </tags:boxContainer2>
        </div>
        <div class="column two nogutter">
                <%-- 
                    Program Actions section each action has a simpleDialogLink that
                    pops open a dialog for the action.  The available actions are based
                    on the dynamically updated SHOW_ACTION value
                --%>
                <tags:boxContainer2 nameKey="heading.actions">
                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${program}">

                        <%-- Actions are enabled only if the user has CONTROL_COMMAND for LM objects --%>

                        <tags:dynamicChoose updaterString="DR_PROGRAM/${programId}/SHOW_ACTION" suffix="${programId}">
                            <tags:dynamicChooseOption optionId="unknown">
                                <cti:msg var="programUnknown" key="yukon.web.modules.dr.programDetail.unknown"/>
                                <div class="disabledAction" title="${programUnknown}">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.start"/>
                                </div>
                                <div class="disabledAction" title="${programUnknown}">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.stop"/>
                                </div>
                                <div class="disabledAction" title="${programUnknown}">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.changeGearsIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.changeGears"/>
                                </div>
                                <div class="disabledAction" title="${programUnknown}">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.disableIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.disable"/>
                                </div>
                            </tags:dynamicChooseOption>

                            <tags:dynamicChooseOption optionId="runningEnabled">
                                <cti:msg var="alreadyRunning" key="yukon.web.modules.dr.programDetail.alreadyRunning"/>
                                <span class="disabledAction" title="${alreadyRunning}">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.start"/>
                                </span>
                                <br>
                                <cti:url var="stopProgramUrl" value="/dr/program/stop/details">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopProgram.title" 
                                    dialogId="drDialog" actionUrl="${stopProgramUrl}" 
                                    logoKey="yukon.web.modules.dr.programDetail.actions.stopIcon"
                                    labelKey="yukon.web.modules.dr.programDetail.actions.stop"/>
                                <br>
                                <cti:url var="changeGearsUrl" value="/dr/program/getChangeGearValue">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.getChangeGearValue.title" 
                                    dialogId="drDialog" actionUrl="${changeGearsUrl}" 
                                    logoKey="yukon.web.modules.dr.programDetail.actions.changeGearsIcon"
                                    labelKey="yukon.web.modules.dr.programDetail.actions.changeGears"/>
                                <br>
                                <cti:url var="sendDisableUrl" value="/dr/program/sendEnableConfirm">
                                    <cti:param name="programId" value="${programId}"/>
                                    <cti:param name="isEnabled" value="false"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendDisableConfirm.title" 
                                    dialogId="drDialog" actionUrl="${sendDisableUrl}" 
                                    logoKey="yukon.web.modules.dr.programDetail.actions.disableIcon"
                                    labelKey="yukon.web.modules.dr.programDetail.actions.disable"/>
                                <br>
                            </tags:dynamicChooseOption>

                            <tags:dynamicChooseOption optionId="scheduledEnabled">
                                <cti:url var="startProgramUrl" value="/dr/program/start/details">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startProgram.title" 
                                    dialogId="drDialog" actionUrl="${startProgramUrl}" 
                                    logoKey="yukon.web.modules.dr.programDetail.actions.startIcon"
                                    labelKey="yukon.web.modules.dr.programDetail.actions.start"/>
                                <br>
                                <cti:url var="stopProgramUrl" value="/dr/program/stop/details">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopProgram.title" 
                                    dialogId="drDialog" actionUrl="${stopProgramUrl}" 
                                    logoKey="yukon.web.modules.dr.programDetail.actions.stopIcon"
                                    labelKey="yukon.web.modules.dr.programDetail.actions.stop"/>
                                <br>
                                
                                <cti:msg var="changeGearsDisabled" key="yukon.web.modules.dr.programDetail.actions.changeGears.disabled"/>
                                <span id="changeGearDisabled" title="${changeGearsDisabled}">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.changeGearsIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.changeGears"/>
                                </span>
                                <br>
                                
                                <cti:url var="sendDisableUrl" value="/dr/program/sendEnableConfirm">
                                    <cti:param name="programId" value="${programId}"/>
                                    <cti:param name="isEnabled" value="false"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendDisableConfirm.title" 
                                    dialogId="drDialog" actionUrl="${sendDisableUrl}" 
                                    logoKey="yukon.web.modules.dr.programDetail.actions.disableIcon"
                                    labelKey="yukon.web.modules.dr.programDetail.actions.disable"/>
                                <br>
                            </tags:dynamicChooseOption>

                            <tags:dynamicChooseOption optionId="enabled">
                                <cti:url var="startProgramUrl" value="/dr/program/start/details">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startProgram.title" 
                                    dialogId="drDialog" actionUrl="${startProgramUrl}" 
                                    logoKey="yukon.web.modules.dr.programDetail.actions.startIcon"
                                    labelKey="yukon.web.modules.dr.programDetail.actions.start"/>
                                <br>
                                <cti:msg var="notRunning" key="yukon.web.modules.dr.programDetail.notRunning"/>
                                <span class="disabledAction" title="${notRunning}">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.stop"/>
                                </span>
                                <br>
                                <cti:msg var="changeGearsDisabled" key="yukon.web.modules.dr.programDetail.actions.changeGears.disabled"/>
                                <span id="changeGearDisabled" title="${changeGearsDisabled}">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.changeGearsIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.changeGears"/>
                                </span>
                                <br>
                                <cti:url var="sendDisableUrl" value="/dr/program/sendEnableConfirm">
                                    <cti:param name="programId" value="${programId}"/>
                                    <cti:param name="isEnabled" value="false"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendDisableConfirm.title" 
                                    dialogId="drDialog" actionUrl="${sendDisableUrl}" 
                                    logoKey="yukon.web.modules.dr.programDetail.actions.disableIcon"
                                    labelKey="yukon.web.modules.dr.programDetail.actions.disable"/>
                                <br>
                            </tags:dynamicChooseOption>

                            <tags:dynamicChooseOption optionId="runningDisabled">
                                <cti:msg var="alreadyRunning" key="yukon.web.modules.dr.programDetail.alreadyRunning"/>
                                <span class="disabledAction" title="${alreadyRunning}">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.start"/>
                                </span>
                                <br>
                                <cti:url var="stopProgramUrl" value="/dr/program/stop/details">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopProgram.title" 
                                    dialogId="drDialog" actionUrl="${stopProgramUrl}" 
                                    logoKey="yukon.web.modules.dr.programDetail.actions.stopIcon"
                                    labelKey="yukon.web.modules.dr.programDetail.actions.stop"/>
                                <br>
                                <cti:url var="changeGearsUrl" value="/dr/program/getChangeGearValue">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.getChangeGearValue.title" 
                                    dialogId="drDialog" actionUrl="${changeGearsUrl}" 
                                    logoKey="yukon.web.modules.dr.programDetail.actions.changeGearsIcon"
                                    labelKey="yukon.web.modules.dr.programDetail.actions.changeGears"/>
                                <br>
                                <cti:url var="sendEnableUrl" value="/dr/program/sendEnableConfirm">
                                    <cti:param name="programId" value="${programId}"/>
                                    <cti:param name="isEnabled" value="true"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendEnableConfirm.title" 
                                    dialogId="drDialog" actionUrl="${sendEnableUrl}" 
                                    logoKey="yukon.web.modules.dr.programDetail.actions.enableIcon"
                                    labelKey="yukon.web.modules.dr.programDetail.actions.enable"/>
                                <br>
                            </tags:dynamicChooseOption>

                            <tags:dynamicChooseOption optionId="scheduledDisabled">
                                <cti:url var="startProgramUrl" value="/dr/program/start/details">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startProgram.title" 
                                    dialogId="drDialog" actionUrl="${startProgramUrl}" 
                                    logoKey="yukon.web.modules.dr.programDetail.actions.startIcon"
                                    labelKey="yukon.web.modules.dr.programDetail.actions.start"/>
                                <br>
                                <cti:url var="stopProgramUrl" value="/dr/program/stop/details">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopProgram.title" 
                                    dialogId="drDialog" actionUrl="${stopProgramUrl}" 
                                    logoKey="yukon.web.modules.dr.programDetail.actions.stopIcon"
                                    labelKey="yukon.web.modules.dr.programDetail.actions.stop"/>
                                <br>
                                <cti:msg var="changeGearsDisabled" key="yukon.web.modules.dr.programDetail.actions.changeGears.disabled"/>
                                <span id="changeGearDisabled" title="${changeGearsDisabled}">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.changeGearsIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.changeGears"/>
                                </span>
                                <br>
                                <cti:url var="sendEnableUrl" value="/dr/program/sendEnableConfirm">
                                    <cti:param name="programId" value="${programId}"/>
                                    <cti:param name="isEnabled" value="true"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendEnableConfirm.title" 
                                    dialogId="drDialog" actionUrl="${sendEnableUrl}" 
                                    logoKey="yukon.web.modules.dr.programDetail.actions.enableIcon"
                                    labelKey="yukon.web.modules.dr.programDetail.actions.enable"/>
                                <br>
                            </tags:dynamicChooseOption>

                            <tags:dynamicChooseOption optionId="disabled">
                                <cti:url var="startProgramUrl" value="/dr/program/start/details">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startProgram.title" 
                                    dialogId="drDialog" actionUrl="${startProgramUrl}" 
                                    logoKey="yukon.web.modules.dr.programDetail.actions.startIcon"
                                    labelKey="yukon.web.modules.dr.programDetail.actions.start"/>
                                <br>
                                <cti:msg var="notRunning" key="yukon.web.modules.dr.programDetail.notRunning"/>
                                <span class="disabledAction" title="${notRunning}">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.stop"/>
                                </span>
                                <br>
                                <cti:msg var="changeGearsDisabled" key="yukon.web.modules.dr.programDetail.actions.changeGears.disabled"/>
                                <span id="changeGearDisabled" title="${changeGearsDisabled}">
                                    <cti:logo key="yukon.web.modules.dr.programDetail.actions.changeGearsIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.changeGears"/>
                                </span>
                                <br>
                                <cti:url var="sendEnableUrl" value="/dr/program/sendEnableConfirm">
                                    <cti:param name="programId" value="${programId}"/>
                                    <cti:param name="isEnabled" value="true"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendEnableConfirm.title" 
                                    dialogId="drDialog" actionUrl="${sendEnableUrl}" 
                                    logoKey="yukon.web.modules.dr.programDetail.actions.enableIcon"
                                    labelKey="yukon.web.modules.dr.programDetail.actions.enable"/>
                                <br>
                            </tags:dynamicChooseOption>
                        </tags:dynamicChoose>
                    </cti:checkPaoAuthorization>                    
                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${program}" invert="true">
                    
                        <%-- Actions are disabled if the user does not have CONTROL_COMMAND for LM objects --%>
                    
                        <cti:msg var="noProgramControl" key="yukon.web.modules.dr.programDetail.noControl"/>
                        <div class="subtle" title="${noProgramControl}">
                            <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.programDetail.actions.start"/>
                        </div>
                        <div class="subtle" title="${noProgramControl}">
                            <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.programDetail.actions.stop"/>
                        </div>
                        <div class="subtle" title="${noProgramControl}">
                            <cti:logo key="yukon.web.modules.dr.programDetail.actions.changeGearsIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.programDetail.actions.changeGears"/>
                        </div>
                        <div class="subtle" title="${noProgramControl}">
                            <cti:logo key="yukon.web.modules.dr.programDetail.actions.disableIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.programDetail.actions.disable"/>
                        </div>
                    </cti:checkPaoAuthorization>
                </tags:boxContainer2>
        </div>
    </div>

    <%-- Child Load Groups for the Program --%>
    <div class="column_24">
        <div class="column one nogutter">
            <c:set var="baseUrl" value="/dr/program/detail"/>
            <%@ include file="../loadGroup/loadGroupList.jspf" %>
        </div>
    </div>
        <c:set var="showControlAreas" value="false"/>
        <c:set var="showScenarios" value="false"/>
        <cti:checkRolesAndProperties value="SHOW_CONTROL_AREAS">
            <c:set var="showControlAreas" value="true"/>
        </cti:checkRolesAndProperties>
        <cti:checkRolesAndProperties value="SHOW_SCENARIOS">
            <c:set var="showScenarios" value="true"/>
        </cti:checkRolesAndProperties>

        <div class="column_12_12">
            <div class="column one">
            <c:if test="${showControlAreas}">
                    <%-- 
                        Parent Control Area for the Program - has a link to Control Area detail if
                        the user has permission to view the Control Area 
                    --%>
                    <tags:boxContainer2 nameKey="parents.controlArea">
                        <c:if test="${empty parentControlArea}">
                            <span class="empty-list"><cti:msg key="yukon.web.modules.dr.programDetail.parents.noControlArea"/></span>
                        </c:if>
                        <c:if test="${!empty parentControlArea}">
                            <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentControlArea}">
                                <c:url var="controlAreaURL" value="/dr/controlArea/detail">
                                    <c:param name="controlAreaId" value="${parentControlArea.paoIdentifier.paoId}"/>
                                </c:url>
                                <a href="${controlAreaURL}"><spring:escapeBody htmlEscape="true">${parentControlArea.name}</spring:escapeBody></a><br>
                            </cti:checkPaoAuthorization>
                            <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentControlArea}" invert="true">
                                <cti:msg var="noParentPermission" key="yukon.web.modules.dr.programDetail.parents.noControlAreaPermission"/>
                                <span title="${noParentPermission}">
                                    <spring:escapeBody htmlEscape="true">${parentControlArea.name}</spring:escapeBody>
                                </span>
                            </cti:checkPaoAuthorization>
                        </c:if>
                    </tags:boxContainer2>
            </c:if>
            </div>
            <div class="column two nogutter">
                <c:if test="${showScenarios}">
                    <tags:boxContainer2 nameKey="parents.scenarios">
                        <%-- 
                            Parent Scenario(s) for the Program - has a link to Scenario detail if
                            the user has permission to view the Scenario 
                        --%>
                        <c:if test="${empty parentScenarios}">
                            <span class="empty-list"><cti:msg key="yukon.web.modules.dr.programDetail.parents.noScenarios"/></span>
                        </c:if>
                        <c:if test="${!empty parentScenarios}">
                            <c:forEach var="parentScenario" items="${parentScenarios}">
                                <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentScenario}">
                                    <c:url var="scenarioURL" value="/dr/scenario/detail">
                                        <c:param name="scenarioId" value="${parentScenario.paoIdentifier.paoId}"/>
                                    </c:url>
                                    <a href="${scenarioURL}"><spring:escapeBody htmlEscape="true">${parentScenario.name}</spring:escapeBody></a><br>
                                </cti:checkPaoAuthorization>
                                <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentScenario}" invert="true">
                                    <cti:msg var="noParentPermission" key="yukon.web.modules.dr.programDetail.parents.noScenarioPermission"/>
                                    <span title="${noParentPermission}">
                                        <spring:escapeBody htmlEscape="true">${parentScenario.name}</spring:escapeBody>
                                    </span>
                                </cti:checkPaoAuthorization>
                            </c:forEach>
                        </c:if>
                    </tags:boxContainer2>
                </c:if>
            </div>
        </div>
    </table>
</cti:standardPage>
