<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

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
                <tags:sectionContainer2 nameKey="heading.info">
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
                    </tags:nameValueContainer>
                </tags:sectionContainer2>
        </div>
        <div class="column two nogutter">

            <%-- Display the Asset Availability Info --%>
            <tags:sectionContainer2 nameKey="assetAvailability">
                <dr:assetAvailabilityStatus assetId="${programId}" 
                    assetAvailabilitySummary="${assetAvailabilitySummary}" pieJSONData="${pieJSONData}"/>
            </tags:sectionContainer2>


                <%-- 
                    Program Actions section each action has a simpleDialogLink that
                    pops open a dialog for the action.  The available actions are based
                    on the dynamically updated SHOW_ACTION value
                --%>
                <!-- Page Dropdown Actions -->
                <div id="f-page-actions" class="dn">
                
                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${program}">
                        <%-- Actions are enabled only if the user has CONTROL_COMMAND for LM objects --%>
                        <tags:dynamicChoose updaterString="DR_PROGRAM/${programId}/SHOW_ACTION" suffix="${programId}">
                        
                            <tags:dynamicChooseOption optionId="unknown">
                                <cti:msg var="programUnknown" key="yukon.web.modules.dr.programDetail.unknown"/>
                                <li>
                                    <a class="clearfix" title="${programUnknown}">
                                        <cti:icon icon="icon-control-start-blue" classes="disabled"/>
                                        <span class="fl dib disabled"><cti:msg2 key=".actions.start"/></span>
                                    </a>
                                </li>
                                <li>
                                    <a class="clearfix" title="${programUnknown}">
                                        <cti:icon icon="icon-control-stop-blue" classes="disabled"/>
                                        <span class="fl dib disabled"><cti:msg2 key=".actions.stop"/></span>
                                    </a>
                                </li>
                                <li>
                                    <a class="clearfix" title="${programUnknown}">
                                        <cti:icon icon="icon-cog-edit" classes="disabled"/>
                                        <span class="fl dib disabled"><cti:msg2 key=".actions.changeGears"/></span>
                                    </a>
                                </li>
                                <li>
                                    <a class="clearfix" title="${programUnknown}">
                                        <cti:icon icon="icon-delete" classes="disabled"/>
                                        <span class="fl dib disabled"><cti:msg2 key=".actions.disable"/></span>
                                    </a>
                                </li>
                            </tags:dynamicChooseOption>

                            <tags:dynamicChooseOption optionId="runningEnabled">
                                <cti:msg var="alreadyRunning" key="yukon.web.modules.dr.programDetail.alreadyRunning"/>
                                <li>
                                    <a class="clearfix" title="${alreadyRunning}">
                                        <cti:icon icon="icon-control-start-blue" classes="disabled"/>
                                        <span class="fl dib disabled"><cti:msg2 key=".actions.start"/></span>
                                    </a>
                                </li>
                                <cti:url var="stopProgramUrl" value="/dr/program/stop/details">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopProgram.title" 
                                        dialogId="drDialog" actionUrl="${stopProgramUrl}" icon="icon-control-stop-blue"
                                        labelKey=".actions.stop"/>
                                </li>
                                <cti:url var="changeGearsUrl" value="/dr/program/getChangeGearValue">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.getChangeGearValue.title" 
                                        dialogId="drDialog" actionUrl="${changeGearsUrl}" icon="icon-cog-edit"
                                        labelKey=".actions.changeGears"/>
                                </li>
                                <cti:url var="sendDisableUrl" value="/dr/program/sendEnableConfirm">
                                    <cti:param name="programId" value="${programId}"/>
                                    <cti:param name="isEnabled" value="false"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendDisableConfirm.title" 
                                        dialogId="drDialog" actionUrl="${sendDisableUrl}" icon="icon-delete"
                                        labelKey=".actions.disable"/>
                                </li>
                            </tags:dynamicChooseOption>

                            <tags:dynamicChooseOption optionId="scheduledEnabled">
                                <cti:url var="startProgramUrl" value="/dr/program/start/details">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startProgram.title" 
                                        dialogId="drDialog" actionUrl="${startProgramUrl}" icon="icon-control-play-blue"
                                        labelKey=".actions.start"/>
                                </li>
                                <cti:url var="stopProgramUrl" value="/dr/program/stop/details">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopProgram.title" 
                                        dialogId="drDialog" actionUrl="${startProgramUrl}" icon="icon-control-stop-blue"
                                        labelKey=".actions.stop"/>
                                </li>
                                <li>
                                    <cti:msg2 var="changeGearsDisabled" key=".actions.changeGears.disabled"/>
                                    <a class="clearfix" title="${changeGearsDisabled}">
                                        <cti:icon icon="icon-cog-edit" classes="disabled"/>
                                        <span class="fl dib disabled"><cti:msg2 key=".actions.changeGears"/></span>
                                    </a>
                                </li>
                                <cti:url var="sendDisableUrl" value="/dr/program/sendEnableConfirm">
                                    <cti:param name="programId" value="${programId}"/>
                                    <cti:param name="isEnabled" value="false"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendDisableConfirm.title" 
                                        dialogId="drDialog" actionUrl="${sendDisableUrl}" icon="icon-delete"
                                        labelKey=".actions.disable"/>
                                </li>
                            </tags:dynamicChooseOption>

                            <tags:dynamicChooseOption optionId="enabled">
                                <cti:url var="startProgramUrl" value="/dr/program/start/details">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startProgram.title" 
                                        dialogId="drDialog" actionUrl="${startProgramUrl}" icon="icon-control-play-blue"
                                        labelKey=".actions.start"/>
                                </li>
                                <li>
                                    <cti:msg var="notRunning" key="yukon.web.modules.dr.programDetail.notRunning"/>
                                    <a class="clearfix" title="${notRunning}">
                                        <cti:icon icon="icon-control-stop-blue" classes="disabled"/>
                                        <span class="fl dib disabled"><cti:msg2 key=".actions.stop"/></span>
                                    </a>
                                </li>
                                <li>
                                    <cti:msg2 var="changeGearsDisabled" key=".actions.changeGears.disabled"/>
                                    <a class="clearfix" title="${changeGearsDisabled}">
                                        <cti:icon icon="icon-cog-edit" classes="disabled"/>
                                        <span class="fl dib disabled"><cti:msg2 key=".actions.changeGears"/></span>
                                    </a>
                                </li>
                                <cti:url var="sendDisableUrl" value="/dr/program/sendEnableConfirm">
                                    <cti:param name="programId" value="${programId}"/>
                                    <cti:param name="isEnabled" value="false"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendDisableConfirm.title" 
                                        dialogId="drDialog" actionUrl="${sendDisableUrl}" icon="icon-delete"
                                        labelKey=".actions.disable"/>
                                </li>
                            </tags:dynamicChooseOption>

                            <tags:dynamicChooseOption optionId="runningDisabled">
                                <cti:msg var="alreadyRunning" key="yukon.web.modules.dr.programDetail.alreadyRunning"/>
                                <li>
                                    <a class="clearfix" title="${alreadyRunning}">
                                        <cti:icon icon="icon-control-start-blue" classes="disabled"/>
                                        <span class="fl dib disabled"><cti:msg2 key=".actions.start"/></span>
                                    </a>
                                </li>

                                <cti:url var="stopProgramUrl" value="/dr/program/stop/details">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopProgram.title" 
                                        dialogId="drDialog" actionUrl="${stopProgramUrl}" icon="icon-control-stop-blue"
                                        labelKey=".actions.stop"/>
                                </li>

                                <cti:url var="changeGearsUrl" value="/dr/program/getChangeGearValue">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.getChangeGearValue.title" 
                                        dialogId="drDialog" actionUrl="${changeGearsUrl}" icon="icon-cog-edit"
                                        labelKey=".actions.changeGears"/>
                                </li>
                                
                                <cti:url var="sendEnableUrl" value="/dr/program/sendEnableConfirm">
                                    <cti:param name="programId" value="${programId}"/>
                                    <cti:param name="isEnabled" value="true"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendEnableConfirm.title" 
                                        dialogId="drDialog" actionUrl="${sendEnableUrl}" icon="icon-accept"
                                        labelKey=".actions.enable"/>
                                </li>
                            </tags:dynamicChooseOption>

                            <tags:dynamicChooseOption optionId="scheduledDisabled">
                                <cti:url var="startProgramUrl" value="/dr/program/start/details">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startProgram.title" 
                                        dialogId="drDialog" actionUrl="${startProgramUrl}" icon="icon-control-start-blue"
                                        labelKey=".actions.start"/>
                                </li>
                                
                                <cti:url var="stopProgramUrl" value="/dr/program/stop/details">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopProgram.title" 
                                        dialogId="drDialog" actionUrl="${stopProgramUrl}" icon="icon-control-stop-blue"
                                        labelKey=".actions.stop"/>
                                </li>
                                
                                <li>
                                    <cti:msg2 var="changeGearsDisabled" key=".actions.changeGears.disabled"/>
                                    <a class="clearfix" title="${changeGearsDisabled}">
                                        <cti:icon icon="icon-cog-edit" classes="disabled"/>
                                        <span class="fl dib disabled"><cti:msg2 key=".actions.changeGears"/></span>
                                    </a>
                                </li>
                                
                                <cti:url var="sendEnableUrl" value="/dr/program/sendEnableConfirm">
                                    <cti:param name="programId" value="${programId}"/>
                                    <cti:param name="isEnabled" value="true"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendEnableConfirm.title" 
                                        dialogId="drDialog" actionUrl="${sendEnableUrl}" icon="icon-accept"
                                        labelKey=".actions.enable"/>
                                </li>
                            </tags:dynamicChooseOption>

                            <tags:dynamicChooseOption optionId="disabled">
                                <cti:url var="startProgramUrl" value="/dr/program/start/details">
                                    <cti:param name="programId" value="${programId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startProgram.title" 
                                        dialogId="drDialog" actionUrl="${startProgramUrl}" icon="icon-control-play-blue"
                                        labelKey=".actions.start"/>
                                </li>

                                <li>
                                    <cti:msg var="notRunning" key="yukon.web.modules.dr.programDetail.notRunning"/>
                                    <a class="clearfix" title="${notRunning}">
                                        <cti:icon icon="icon-control-stop-blue" classes="disabled"/>
                                        <span class="fl dib disabled"><cti:msg2 key=".actions.stop"/></span>
                                    </a>
                                </li>

                                <li>
                                    <cti:msg2 var="changeGearsDisabled" key=".actions.changeGears.disabled"/>
                                    <a class="clearfix" title="${changeGearsDisabled}">
                                        <cti:icon icon="icon-cog-edit" classes="disabled"/>
                                        <span class="fl dib disabled"><cti:msg2 key=".actions.changeGears"/></span>
                                    </a>
                                </li>

                                <cti:url var="sendEnableUrl" value="/dr/program/sendEnableConfirm">
                                    <cti:param name="programId" value="${programId}"/>
                                    <cti:param name="isEnabled" value="true"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendEnableConfirm.title" 
                                        dialogId="drDialog" actionUrl="${sendEnableUrl}" icon="icon-accept"
                                        labelKey=".actions.enable"/>
                                </li>
                            </tags:dynamicChooseOption>
                        </tags:dynamicChoose>
                    </cti:checkPaoAuthorization>                    
                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${program}" invert="true">
                        <%-- Actions are disabled if the user does not have CONTROL_COMMAND for LM objects --%>
                    
                        <cti:msg var="noProgramControl" key="yukon.web.modules.dr.programDetail.noControl"/>
                        <li>
                            <a class="clearfix" title="${noProgramControl}">
                                <cti:icon icon="icon-control-play-blue" classes="disabled"/>
                                <span class="fl dib disabled"><cti:msg2 key=".actions.start"/></span>
                            </a>
                        </li>
                        <li>
                            <a class="clearfix" title="${noProgramControl}">
                                <cti:icon icon="icon-control-stop-blue" classes="disabled"/>
                                <span class="fl dib disabled"><cti:msg2 key=".actions.stop"/></span>
                            </a>
                        </li>
                        <li>
                            <a class="clearfix" title="${noProgramControl}">
                                <cti:icon icon="icon-cog-edit" classes="disabled"/>
                                <span class="fl dib disabled"><cti:msg2 key=".actions.changeGears"/></span>
                            </a>
                        </li>
                        <li>
                            <a class="clearfix" title="${noProgramControl}">
                                <cti:icon icon="icon-delete" classes="disabled"/>
                                <span class="fl dib disabled"><cti:msg2 key=".actions.disable"/></span>
                            </a>
                        </li>

                    </cti:checkPaoAuthorization>
                </div>
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
                                <a href="${controlAreaURL}">${fn:escapeXml(parentControlArea.name)}</a><br>
                            </cti:checkPaoAuthorization>
                            <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentControlArea}" invert="true">
                                <cti:msg var="noParentPermission" key="yukon.web.modules.dr.programDetail.parents.noControlAreaPermission"/>
                                <span title="${noParentPermission}">
                                    ${fn:escapeXml(parentControlArea.name)}
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
                                    <a href="${scenarioURL}">${fn:escapeXml(parentScenario.name)}</a><br>
                                </cti:checkPaoAuthorization>
                                <cti:checkPaoAuthorization permission="LM_VISIBLE" pao="${parentScenario}" invert="true">
                                    <cti:msg var="noParentPermission" key="yukon.web.modules.dr.programDetail.parents.noScenarioPermission"/>
                                    <span title="${noParentPermission}">
                                        ${fn:escapeXml(parentScenario.name)}
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
