<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="programDetail">

    <tags:simpleDialog id="drDialog"/>
    <cti:includeScript link="/JavaScript/yukon.dr.asset.details.js"/>
    <cti:includeScript link="/JavaScript/yukon.dr.estimated.load.js"/>
    <cti:includeScript link="/JavaScript/yukon.dr.program.showActionDataUpdator.js"/>
    <cti:includeScript link="YUKON_FLOTCHARTS"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS_PIE"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS_SELECTION"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS_AXIS_LABEL"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS_RESIZE"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS_TIME"/>
    <!--[if lte IE 8]><cti:includeScript link="JQUERY_EXCANVAS"/><![endif]-->
    <cti:includeScript link="/JavaScript/yukon.ui.progressbar.js"/>
    <cti:includeCss link="/WebConfig/yukon/styles/flotChart.css"/>
    
    <c:set var="programId" value="${program.paoIdentifier.paoId}"/>

    <input id="assetId" type="hidden" value="${programId}"/>

    <div class="column-12-12 clear">
        <div class="column one">

                <%-- Program Info section --%>
                <tags:sectionContainer2 nameKey="heading.info">
                    <tags:nameValueContainer2>
                        <cti:checkRolesAndProperties value="PROGRAM_STATE">
                            <tags:nameValue2 nameKey="yukon.web.modules.dr.programDetail.info.state"
                                    nameColumnWidth="175px">
                                <dr:programState programId="${programId}"/>
                            </tags:nameValue2>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="PROGRAM_START">
                            <tags:nameValue2 nameKey="yukon.web.modules.dr.programDetail.info.start">
                                <cti:dataUpdaterValue identifier="${programId}/START" type="DR_PROGRAM"/>
                            </tags:nameValue2>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="PROGRAM_STOP">
                            <tags:nameValue2 nameKey="yukon.web.modules.dr.programDetail.info.stop">
                                <cti:dataUpdaterValue identifier="${programId}/STOP" type="DR_PROGRAM"/>
                            </tags:nameValue2>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="PROGRAM_CURRENT_GEAR">
                            <tags:nameValue2 nameKey="yukon.web.modules.dr.programDetail.info.currentGear">
                                <cti:dataUpdaterValue identifier="${programId}/CURRENT_GEAR" type="DR_PROGRAM"/>
                            </tags:nameValue2>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="PROGRAM_PRIORITY">
                            <tags:nameValue2 nameKey="yukon.web.modules.dr.programDetail.info.priority">
                                <cti:dataUpdaterValue identifier="${programId}/PRIORITY" type="DR_PROGRAM"/>
                            </tags:nameValue2>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="PROGRAM_REDUCTION">
                            <tags:nameValue2 nameKey="yukon.web.modules.dr.programDetail.info.reduction">
                                <cti:dataUpdaterValue identifier="${programId}/REDUCTION" type="DR_PROGRAM"/>
                            </tags:nameValue2>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="ENABLE_ESTIMATED_LOAD">
                            <cti:dataUpdaterCallback
                                function="yukon.dr.estimatedLoad.displayProgramValue"
                                value="ESTIMATED_LOAD/${programId}/PROGRAM"
                                initialize="true"/>
                            <tags:nameValue2 nameKey="yukon.web.modules.dr.estimatedLoad.info.connectedLoad">
                                <div data-pao="${programId}">
                                    <cti:icon icon="icon-error" classes="dn"/>
                                    <cti:icon icon="icon-spinner"/>
                                    <span class="f-connected-load">
                                        <i:inline key="yukon.web.modules.dr.estimatedLoad.calculating"/>
                                    </span>
                                </div>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey="yukon.web.modules.dr.estimatedLoad.info.diversifiedLoad">
                                <div data-pao="${programId}">
                                    <cti:icon icon="icon-error" classes="dn"/>
                                    <cti:icon icon="icon-spinner"/>
                                    <span class="f-diversified-load">
                                        <i:inline key="yukon.web.modules.dr.estimatedLoad.calculating"/>
                                    </span>
                                </div>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey="yukon.web.modules.dr.estimatedLoad.info.kwSavings">
                                <div data-pao="${programId}">
                                    <cti:icon icon="icon-error" classes="dn"/>
                                    <cti:icon icon="icon-spinner"/>
                                    <span class="f-kw-savings">
                                        <i:inline key="yukon.web.modules.dr.estimatedLoad.calculating"/>
                                    </span>
                                </div>
                            </tags:nameValue2>
                        </cti:checkRolesAndProperties>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
        </div>
        <div class="column two nogutter">
            <cti:checkRolesAndProperties value="SHOW_ASSET_AVAILABILITY">
                <%-- Display the Asset Availability Info --%>
                <tags:sectionContainer2 nameKey="assetAvailability">
                    <div class="f-asset-availability f-block-this">
                        <i:inline key="yukon.common.loading"/>
                    </div>
                </tags:sectionContainer2>
            </cti:checkRolesAndProperties>

            <%-- 
                Program Actions section each action has a simpleDialogLink that
                pops open a dialog for the action.  The available actions are based
                on the dynamically updated SHOW_ACTION value
            --%>
            <!-- Page Dropdown Actions -->
            <div id="f-page-actions" class="dn">
                <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${program}">
                    <div data-start-action="enabled" data-pao-id="${programId}">
                        <cti:url var="startProgramUrl" value="/dr/program/start/details">
                                <cti:param name="programId" value="${programId}"/>
                        </cti:url>
                        <li><tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startProgram.title" 
                                dialogId="drDialog" actionUrl="${startProgramUrl}" icon="icon-control-play-blue"
                                labelKey=".actions.start"/>
                        </li>
                    </div>
                    <div class="dn" data-start-action="disabled" data-pao-id="${programId}">
                        <cm:dropdownOption icon="icon-control-play-blue" disabled="true">
                            <cti:msg2 key=".actions.start"/>
                        </cm:dropdownOption>
                    </div>

                    <div data-stop-action="enabled" data-pao-id="${programId}">
                        <cti:url var="stopProgramUrl" value="/dr/program/stop/details">
                                <cti:param name="programId" value="${programId}"/>
                        </cti:url>
                        <li><tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopProgram.title" 
                                dialogId="drDialog" actionUrl="${stopProgramUrl}" icon="icon-control-stop-blue"
                                labelKey=".actions.stop"/>
                        </li>
                    </div>
                    <div class="dn" data-stop-action="disabled" data-pao-id="${programId}">
                        <cm:dropdownOption icon="icon-control-stop-blue" disabled="true">
                            <cti:msg2 key=".actions.stop"/>
                        </cm:dropdownOption>
                    </div>

                    <div data-change-gears-action="enabled" data-pao-id="${programId}">
                        <cti:url var="changeGearsUrl" value="/dr/program/getChangeGearValue">
                            <cti:param name="programId" value="${programId}"/>
                        </cti:url>
                        <li>
                            <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.getChangeGearValue.title" 
                                    dialogId="drDialog" actionUrl="${changeGearsUrl}" icon="icon-cog-edit"
                                    labelKey=".actions.changeGears"/>
                        </li>
                    </div>
                    <div class="dn" data-change-gears-action="disabled" data-pao-id="${programId}">
                        <cm:dropdownOption icon="icon-cog-edit" disabled="true">
                            <cti:msg2 key=".actions.changeGears"/>
                        </cm:dropdownOption>
                    </div>

                    <div data-disable-action="enabled" data-pao-id="${programId}">
                        <cti:url var="sendDisableUrl" value="/dr/program/sendEnableConfirm">
                            <cti:param name="programId" value="${programId}"/>
                            <cti:param name="isEnabled" value="false"/>
                        </cti:url>
                        <li><tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendDisableConfirm.title" 
                                dialogId="drDialog" actionUrl="${sendDisableUrl}" icon="icon-delete"
                                labelKey=".actions.disable"/>
                        </li>
                    </div>
                    <div class="dn" data-disable-action="disabled" data-pao-id="${programId}">
                        <cm:dropdownOption icon="icon-delete" disabled="true">
                            <cti:msg2 key=".actions.disable"/>
                        </cm:dropdownOption>
                    </div>
                    <div data-enable-action data-pao-id="${programId}">
                        <cti:url var="sendEnableUrl" value="/dr/program/sendEnableConfirm">
                            <cti:param name="programId" value="${programId}"/>
                            <cti:param name="isEnabled" value="true"/>
                        </cti:url>
                        <li><tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendEnableConfirm.title" 
                                dialogId="drDialog" actionUrl="${sendEnableUrl}" icon="icon-accept"
                                labelKey=".actions.enable"/>
                        </li>
                    </div>
                </cti:checkPaoAuthorization>
                <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${program}" invert="true">
                    <%-- Actions are disabled if the user does not have CONTROL_COMMAND for LM objects --%>
                
                    <cti:msg var="noProgramControl" key="yukon.web.modules.dr.programDetail.noControl"/>
                    <li>
                        <a class="clearfix" title="${noProgramControl}">
                            <cti:icon icon="icon-control-play-blue" classes="disabled"/>
                            <span class="dib disabled"><cti:msg2 key=".actions.start"/></span>
                        </a>
                    </li>
                    <li>
                        <a class="clearfix" title="${noProgramControl}">
                            <cti:icon icon="icon-control-stop-blue" classes="disabled"/>
                            <span class="dib disabled"><cti:msg2 key=".actions.stop"/></span>
                        </a>
                    </li>
                    <li>
                        <a class="clearfix" title="${noProgramControl}">
                            <cti:icon icon="icon-cog-edit" classes="disabled"/>
                            <span class="dib disabled"><cti:msg2 key=".actions.changeGears"/></span>
                        </a>
                    </li>
                    <li>
                        <a class="clearfix" title="${noProgramControl}">
                            <cti:icon icon="icon-delete" classes="disabled"/>
                            <span class="dib disabled"><cti:msg2 key=".actions.disable"/></span>
                        </a>
                    </li>
                </cti:checkPaoAuthorization>
            </div>
        </div>
    </div>

    <%-- Child Load Groups for the Program --%>
    <div class="column-24">
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

        <div class="column-12-12">
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
    <cti:dataUpdaterCallback function="yukon.dr.program.showActionDataUpdator.updateDetailLinks(${programId})" 
        initialize="true" state="DR_PROGRAM/${programId}/SHOW_ACTION"/>
</cti:standardPage>
