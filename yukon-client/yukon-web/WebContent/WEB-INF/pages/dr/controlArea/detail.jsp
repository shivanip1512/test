<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="controlAreaDetail">

    <cti:includeScript link="/JavaScript/yukon.hide.reveal.js"/>
    <cti:includeScript link="/JavaScript/yukon.dr.estimated.load.js"/>
    <cti:includeScript link="YUKON_FLOTCHARTS"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS_PIE"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS_SELECTION"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS_AXIS_LABEL"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS_RESIZE"/>
    <cti:includeScript link="JQUERY_FLOTCHARTS_TIME"/>
    <!--[if lte IE 8]><cti:includeScript link="JQUERY_EXCANVAS"/><![endif]-->
    <cti:includeScript link="/JavaScript/yukon.ui.progressbar.js"/>
    <cti:includeScript link="/JavaScript/yukon.dr.asset.details.js"/>
    
    <cti:includeCss link="/WebConfig/yukon/styles/flotChart.css"/>
    
    <tags:simpleDialog id="drDialog"/>
    
    <c:set var="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>

    <input id="assetId" type="hidden" value="${controlAreaId}"/>

    <div class="column-12-12">
        <div class="column one">
            <tags:sectionContainer2 nameKey="heading.info">
                <tags:nameValueContainer2 tableClass="stacked">
                    <cti:checkRolesAndProperties value="CONTROL_AREA_STATE">
                        <tags:nameValue2 nameKey=".info.state">
                            <dr:controlAreaState controlAreaId="${controlAreaId}"/>
                        </tags:nameValue2>
                    </cti:checkRolesAndProperties>
                    <cti:checkRolesAndProperties value="CONTROL_AREA_PRIORITY">
                        <tags:nameValue2 nameKey=".info.priority">
                            <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/PRIORITY"/>
                        </tags:nameValue2>
                    </cti:checkRolesAndProperties>
                    <cti:checkRolesAndProperties value="CONTROL_AREA_TIME_WINDOW">
                        <tags:nameValue2 nameKey=".info.startStop">
                            <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/START"/>
                            <i:inline key=".info.separator"/>
                            <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/STOP"/>
                        </tags:nameValue2>
                    </cti:checkRolesAndProperties>
                    <cti:checkRolesAndProperties value="ENABLE_ESTIMATED_LOAD">
                        <cti:dataUpdaterCallback
                            function="yukon.dr.estimatedLoad.displaySummaryValue"
                            value="ESTIMATED_LOAD/${controlAreaId}/CONTROL_AREA"/>
                        <tags:nameValue2 nameKey="yukon.web.modules.dr.estimatedLoad.info.connectedLoad">
                            <div data-pao="${controlAreaId}">
                                <cti:icon icon="icon-error" classes="dn"/>
                                <cti:icon icon="icon-spinner" classes="f-spinner"/>
                                <span class="f-connected-load">
                                    <i:inline key="yukon.web.modules.dr.estimatedLoad.calculating"/>
                                </span>
                            </div>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey="yukon.web.modules.dr.estimatedLoad.info.diversifiedLoad">
                            <div data-pao="${controlAreaId}">
                                <cti:icon icon="icon-error" classes="dn"/>
                                <cti:icon icon="icon-spinner" classes="f-spinner"/>
                                <span class="f-diversified-load">
                                    <i:inline key="yukon.web.modules.dr.estimatedLoad.calculating"/>
                                </span>
                            </div>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey="yukon.web.modules.dr.estimatedLoad.info.kwSavings">
                            <div data-pao="${controlAreaId}">
                                <cti:icon icon="icon-error" classes="dn"/>
                                <cti:icon icon="icon-spinner" classes="f-spinner"/>
                                <span class="f-kw-savings">
                                    <i:inline key="yukon.web.modules.dr.estimatedLoad.calculating"/>
                                </span>
                            </div>
                        </tags:nameValue2>
                    </cti:checkRolesAndProperties>
              </tags:nameValueContainer2>

              <tags:nameValueContainer2>
                    <c:if test="${empty controlArea.triggers}">
                        <span class="empty-list"><i:inline key=".info.noTriggers"/></span>
                    </c:if>
                    <c:if test="${!empty controlArea.triggers}">
                        <c:forEach var="trigger" items="${controlArea.triggers}">
                            <cti:checkRolesAndProperties value="CONTROL_AREA_VALUE_THRESHOLD,CONTROL_AREA_PEAK_PROJECTION,CONTROL_AREA_ATKU">
                                <c:set var="triggerNumber" value="${trigger.triggerNumber}"/>
                                <tags:nameValue2 rowClass="strong-label-small" nameKey=".info.trigger" argument="${triggerNumber}"></tags:nameValue2>
                                <tags:nameValue2 nameKey=".info.loadCapacity">
                                    <cti:checkRolesAndProperties value="CONTROL_AREA_VALUE_THRESHOLD">
                                        <tags:nameValue2 nameKey=".info.valueThreshold">
                                            <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${triggerNumber}/VALUE_THRESHOLD"/>
                                        </tags:nameValue2>
                                    </cti:checkRolesAndProperties>
                                    <cti:checkRolesAndProperties value="CONTROL_AREA_PEAK_PROJECTION">
                                        <tags:nameValue2 nameKey=".info.peakProjection">
                                            <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${triggerNumber}/PEAK_PROJECTION"/>
                                        </tags:nameValue2>
                                    </cti:checkRolesAndProperties>
                                    <cti:checkRolesAndProperties value="CONTROL_AREA_ATKU">
                                        <tags:nameValue2 nameKey=".info.atku">
                                            <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${triggerNumber}/ATKU"/>
                                        </tags:nameValue2>
                                    </cti:checkRolesAndProperties>
                                </tags:nameValue2>
                            </cti:checkRolesAndProperties>
                        </c:forEach>
                    </c:if>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
        
        <div class="column two nogutter">
            <cti:checkRolesAndProperties value="SHOW_ASSET_AVAILABILITY">
                <tags:sectionContainer2 nameKey="assetAvailability">
                    <%-- Display the Asset Availability Info --%>
                    <div class="f-asset-availability f-block-this">
                        <i:inline key="yukon.common.loading"/>
                    </div>
                </tags:sectionContainer2>
            </cti:checkRolesAndProperties>
            
        </div>
    </div>

    <div class="column-24">
        <div class="column one nogutter">
            <c:set var="baseUrl" value="/dr/controlArea/detail"/>
            <%@ include file="../program/programList.jspf" %>
        </div>
    </div>
    
    <%--
        Control Area Actions section each action has a simpleDialogLink that
        pops open a dialog for the action.  The available actions are based
        on the dynamically updated SHOW_ACTION value
    --%>
    <!-- Page Dropdown Actions -->
    <div id="f-page-actions" class="dn">
    
        <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${controlArea}">

            <%-- Actions are enabled only if the user has CONTROL_COMMAND for LM objects --%>
            <tags:dynamicChoose updaterString="DR_CONTROLAREA/${controlAreaId}/SHOW_ACTION" suffix="${controlAreaId}">

                <%-- Actions are disabled if the user does not have CONTROL_COMMAND for LM objects --%>
                <tags:dynamicChooseOption optionId="noAssignedPrograms">
                    <cti:msg2 var="disabledMessage" key=".noAssignedPrograms"/>
                    <cm:dropdownOption disabled="true" icon="icon-control-play-blue" title="${disabledMessage}" key=".actions.start"/>
                    <cm:dropdownOption disabled="true" icon="icon-control-stop-blue" title="${disabledMessage}" key=".actions.stop"/>
                    <li class="divider"></li>
                    <cm:dropdownOption disabled="true" icon="icon-wrench" title="${disabledMessage}" key=".actions.triggersChange"/>
                    <cm:dropdownOption disabled="true" icon="icon-time" title="${disabledMessage}" key=".actions.dailyTimeChange"/>
                    <cm:dropdownOption disabled="true" icon="icon-delete" title="${disabledMessage}" key=".actions.disable"/>
                    <li class="divider"></li>
                    <cm:dropdownOption disabled="true" icon="icon-accept" title="${disabledMessage}" key=".actions.enablePrograms"/>
                    <cm:dropdownOption disabled="true" icon="icon-delete" title="${disabledMessage}" key=".actions.disablePrograms"/>
                    <cm:dropdownOption disabled="true" icon="icon-control-repeat-blue" title="${disabledMessage}" key=".actions.resetPeak"/>
                </tags:dynamicChooseOption>

                <%-- Actions shown when the Control Area is enabled but not fully active --%>
                <tags:dynamicChooseOption optionId="enabled">
                    <cti:url var="startControlAreaUrl" value="/dr/program/start/multipleDetails">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startMultiplePrograms.title" 
                            dialogId="drDialog" actionUrl="${startControlAreaUrl}" icon="icon-control-play-blue"
                            labelKey=".actions.start"/>
                    </li>
                    <cti:url var="stopControlAreaUrl" value="/dr/program/stop/multipleDetails">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopMultiplePrograms.title" 
                            dialogId="drDialog" actionUrl="${stopControlAreaUrl}" icon="icon-control-stop-blue"
                            labelKey=".actions.stop"/>
                    </li>
                    <li class="divider"></li>
                        <c:choose>
                            <%-- Trigger actions are only active for Control Areas with at least one threshold trigger --%>
                            <c:when test="${controlArea.hasThresholdTrigger}">
                                <cti:url var="sendTriggerChangeUrl" value="/dr/controlArea/getTriggerChangeValues">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey=".getChangeTriggerValues.title" 
                                        dialogId="drDialog" actionUrl="${sendTriggerChangeUrl}" icon="icon-wrench"
                                        labelKey=".actions.triggersChange"/>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <cti:msg2 var="triggersChangeDisabled" key=".actions.triggersChange.disabled"/>
                                <cm:dropdownOption disabled="true" icon="icon-wrench" title="${triggersChangeDisabled}" key=".actions.triggersChange"/>
                            </c:otherwise>
                        </c:choose>
                    <cti:url var="sendChangeTimeWindowUrl" value="/dr/controlArea/getChangeTimeWindowValues">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.title" 
                            dialogId="drDialog" actionUrl="${sendChangeTimeWindowUrl}" icon="icon-time"
                            labelKey=".actions.dailyTimeChange"/>
                    </li>
                    <cti:url var="sendDisableUrl" value="/dr/controlArea/sendEnableConfirm">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        <cti:param name="isEnabled" value="false"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendDisableConfirm.title" 
                            dialogId="drDialog" actionUrl="${sendDisableUrl}" icon="icon-delete"
                            labelKey=".actions.disable"/>
                    </li>
                    <cti:url var="changeScenarioGearsUrl" value="/dr/program/changeGearMultiplePopup">
                         <cti:param name="controlAreaId" value="${controlAreaId}"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.getChangeGearValue.title" 
                            dialogId="drDialog" actionUrl="${changeScenarioGearsUrl}" icon="icon-cog-edit"
                            labelKey="yukon.web.modules.dr.programDetail.actions.changeGears"/>
                    </li>
                    <cti:url var="sendEnableProgramsUrl" value="/dr/program/sendEnableDisableProgramsConfirm">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        <cti:param name="enable" value="true"/>
                    </cti:url>
                    <li class="divider"></li>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendEnableProgramsConfirm.title" 
                            dialogId="drDialog" actionUrl="${sendEnableProgramsUrl}" icon="icon-accept"
                            labelKey=".actions.enablePrograms"/>
                    </li>
                    <cti:url var="sendDisableProgramsUrl" value="/dr/program/sendEnableDisableProgramsConfirm">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        <cti:param name="enable" value="false"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendDisableProgramsConfirm.title" 
                            dialogId="drDialog" actionUrl="${sendDisableProgramsUrl}" icon="icon-delete"
                            labelKey=".actions.disablePrograms"/>
                    </li>
                    <c:choose>
                        <%-- Trigger actions are only active for Control Areas with at least one trigger --%>
                        <c:when test="${!empty controlArea.triggers}">
                            <cti:url var="sendResetPeakUrl" value="/dr/controlArea/sendResetPeakConfirm">
                                <cti:param name="controlAreaId" value="${controlAreaId}"/>
                            </cti:url>
                            <li>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendResetPeakConfirm.title" 
                                    dialogId="drDialog" actionUrl="${sendResetPeakUrl}" icon="icon-control-repeat-blue"
                                    labelKey=".actions.resetPeak"/>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <cm:dropdownOption disabled="true" icon="icon-control-repeat-blue" title="${resetDisabled}" key=".actions.resetPeak"/>
                        </c:otherwise>
                    </c:choose>
                </tags:dynamicChooseOption>

                <%-- Actions shown when the Control Area is fully active and enabled --%>
                <tags:dynamicChooseOption optionId="fullyActiveEnabled">
                    <cti:url var="startControlAreaUrl" value="/dr/program/start/multipleDetails">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startMultiplePrograms.title" 
                            dialogId="drDialog" actionUrl="${startControlAreaUrl}" icon="icon-control-play-blue"
                            labelKey=".actions.start"/>
                    </li>
                    <cti:url var="stopControlAreaUrl" value="/dr/program/stop/multipleDetails">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopMultiplePrograms.title" 
                            dialogId="drDialog" actionUrl="${stopControlAreaUrl}" icon="icon-control-stop-blue"
                            labelKey=".actions.stop"/>
                    </li>
                    <li class="divider"></li>
                    <c:choose>
                        <%-- Trigger actions are only active for Control Areas with at least one threshold trigger --%>
                        <c:when test="${controlArea.hasThresholdTrigger}">
                            <cti:url var="sendTriggerChangeUrl" value="/dr/controlArea/getTriggerChangeValues">
                                <cti:param name="controlAreaId" value="${controlAreaId}"/>
                            </cti:url>
                            <li>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startMultiplePrograms.title" 
                                    dialogId="drDialog" actionUrl="${sendTriggerChangeUrl}" icon="icon-wrench"
                                    labelKey=".actions.triggersChange"/>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <cti:msg2 var="triggersChangeDisabled" key=".actions.triggersChange.disabled"/>
                            <cm:dropdownOption disabled="true" icon="icon-wrench" title="${triggersChangeDisabled}" key=".actions.triggersChange"/>
                        </c:otherwise>
                    </c:choose>
                    <cti:url var="sendChangeTimeWindowUrl" value="/dr/controlArea/getChangeTimeWindowValues">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.title" 
                            dialogId="drDialog" actionUrl="${sendChangeTimeWindowUrl}" icon="icon-time"
                            labelKey=".actions.dailyTimeChange"/>
                    </li>
                    <cti:url var="sendDisableUrl" value="/dr/controlArea/sendEnableConfirm">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        <cti:param name="isEnabled" value="false"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendDisableConfirm.title" 
                            dialogId="drDialog" actionUrl="${sendDisableUrl}" icon="icon-delete"
                            labelKey=".actions.disable"/>
                    </li>
                    <cti:url var="changeScenarioGearsUrl" value="/dr/program/changeGearMultiplePopup">
                         <cti:param name="controlAreaId" value="${controlAreaId}"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.getChangeGearValue.title" 
                            dialogId="drDialog" actionUrl="${changeScenarioGearsUrl}" icon="icon-cog-edit"
                            labelKey="yukon.web.modules.dr.programDetail.actions.changeGears"/>
                    </li>
                    <li class="divider"></li>
                    <cti:url var="sendEnableProgramsUrl" value="/dr/program/sendEnableDisableProgramsConfirm">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        <cti:param name="enable" value="true"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendEnableProgramsConfirm.title" 
                            dialogId="drDialog" actionUrl="${sendEnableProgramsUrl}" icon="icon-accept"
                            labelKey=".actions.enablePrograms"/>
                    </li>
                    <cti:url var="sendDisableProgramsUrl" value="/dr/program/sendEnableDisableProgramsConfirm">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        <cti:param name="enable" value="false"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendDisableProgramsConfirm.title" 
                            dialogId="drDialog" actionUrl="${sendDisableProgramsUrl}" icon="icon-delete"
                            labelKey=".actions.disablePrograms"/>
                    </li>
                    <c:choose>
                        <%-- Trigger actions are only active for Control Areas with at least one trigger --%>
                        <c:when test="${!empty controlArea.triggers}">
                            <cti:url var="sendResetPeakUrl" value="/dr/controlArea/sendResetPeakConfirm">
                                <cti:param name="controlAreaId" value="${controlAreaId}"/>
                            </cti:url>
                            <li>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendResetPeakConfirm.title" 
                                    dialogId="drDialog" actionUrl="${sendResetPeakUrl}" icon="icon-control-repeat-blue"
                                    labelKey=".actions.resetPeak"/>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <cti:msg2 var="resetDisabled" key=".actions.resetPeak.disabled"/>
                            <cm:dropdownOption disabled="true" icon="icon-control-repeat-blue" title="${resetDisabled}" key=".actions.resetPeak"/>
                        </c:otherwise>
                    </c:choose>
                </tags:dynamicChooseOption>

                <%-- Actions shown when the Control Area is disabled but not fully active --%>
                <tags:dynamicChooseOption optionId="disabled">
                    <cti:url var="startControlAreaUrl" value="/dr/program/start/multipleDetails">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startMultiplePrograms.title" 
                            dialogId="drDialog" actionUrl="${startControlAreaUrl}" icon="icon-control-play-blue"
                            labelKey=".actions.start"/>
                    </li>
                    <cti:url var="stopControlAreaUrl" value="/dr/program/stop/multipleDetails">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopMultiplePrograms.title" 
                            dialogId="drDialog" actionUrl="${stopControlAreaUrl}" icon="icon-control-stop-blue"
                            labelKey=".actions.stop"/>
                    </li>
                    <li class="divider"></li>
                        <c:choose>
                            <%-- Trigger actions are only active for Control Areas with at least one threshold trigger --%>
                            <c:when test="${controlArea.hasThresholdTrigger}">
                                <cti:url var="sendTriggerChangeUrl" value="/dr/controlArea/getTriggerChangeValues">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey=".getChangeTriggerValues.title" 
                                        dialogId="drDialog" actionUrl="${sendTriggerChangeUrl}" icon="icon-wrench"
                                        labelKey=".actions.triggersChange"/>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <cm:dropdownOption disabled="true" icon="icon-wrench" title="${triggersChangeDisabled}" key=".actions.triggersChange"/>
                            </c:otherwise>
                        </c:choose>
                    <cti:url var="sendChangeTimeWindowUrl" value="/dr/controlArea/getChangeTimeWindowValues">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.title" 
                            dialogId="drDialog" actionUrl="${sendChangeTimeWindowUrl}" icon="icon-time"
                            labelKey=".actions.dailyTimeChange"/>
                    </li>
                    <cti:url var="sendEnableUrl" value="/dr/controlArea/sendEnableConfirm">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        <cti:param name="isEnabled" value="true"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendEnableConfirm.title" 
                            dialogId="drDialog" actionUrl="${sendEnableUrl}" icon="icon-accept"
                            labelKey=".actions.enable"/>
                    </li>
                    <cti:url var="sendEnableProgramsUrl" value="/dr/program/sendEnableDisableProgramsConfirm">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        <cti:param name="enable" value="true"/>
                    </cti:url>
                    <li class="divider"></li>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendEnableProgramsConfirm.title" 
                            dialogId="drDialog" actionUrl="${sendEnableProgramsUrl}" icon="icon-accept"
                            labelKey=".actions.enablePrograms"/>
                    </li>
                    <cti:url var="sendDisableProgramsUrl" value="/dr/program/sendEnableDisableProgramsConfirm">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        <cti:param name="enable" value="false"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendDisableProgramsConfirm.title" 
                            dialogId="drDialog" actionUrl="${sendDisableProgramsUrl}" icon="icon-delete"
                            labelKey=".actions.disablePrograms"/>
                    </li>
                    <c:choose>
                        <%-- Trigger actions are only active for Control Areas with at least one trigger --%>
                        <c:when test="${!empty controlArea.triggers}">
                            <cti:url var="sendResetPeakUrl" value="/dr/controlArea/sendResetPeakConfirm">
                                <cti:param name="controlAreaId" value="${controlAreaId}"/>
                            </cti:url>
                            <li>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendResetPeakConfirm.title" 
                                    dialogId="drDialog" actionUrl="${sendResetPeakUrl}" icon="icon-control-repeat-blue"
                                    labelKey=".actions.resetPeak"/>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <cm:dropdownOption disabled="true" icon="icon-control-repeat-blue" title="${resetDisabled}" key=".actions.resetPeak"/>
                        </c:otherwise>
                    </c:choose>
                </tags:dynamicChooseOption>

                <%-- Actions shown when the Control Area is fully active and disabled --%>
                <tags:dynamicChooseOption optionId="fullyActiveDisabled">
                    <cti:msg2 var="disabledMessage" key=".fullyActive"/>
                    <cm:dropdownOption disabled="true" icon="icon-control-play-blue" title="${disabledMessage}" key=".actions.start"/>
                    <cti:url var="stopControlAreaUrl" value="/dr/program/stop/multipleDetails">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopMultiplePrograms.title" 
                            dialogId="drDialog" actionUrl="${stopControlAreaUrl}" icon="icon-control-stop-blue"
                            labelKey=".actions.stop"/>
                    </li>
                    <li class="divider"></li>
                        <c:choose>
                            <%-- Trigger actions are only active for Control Areas with at least one threshold trigger --%>
                            <c:when test="${controlArea.hasThresholdTrigger}">
                                <cti:url var="sendTriggerChangeUrl" value="/dr/controlArea/getTriggerChangeValues">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey=".getChangeTriggerValues.title" 
                                        dialogId="drDialog" actionUrl="${sendTriggerChangeUrl}" icon="icon-wrench"
                                        labelKey=".actions.triggersChange"/>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <cm:dropdownOption disabled="true" icon="icon-wrench" title="${triggersChangeDisabled}" key=".actions.triggersChange"/>
                            </c:otherwise>
                        </c:choose>
                    <cti:url var="sendChangeTimeWindowUrl" value="/dr/controlArea/getChangeTimeWindowValues">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.title" 
                            dialogId="drDialog" actionUrl="${sendChangeTimeWindowUrl}" icon="icon-time"
                            labelKey=".actions.dailyTimeChange"/>
                    </li>
                    <cti:url var="sendEnableUrl" value="/dr/controlArea/sendEnableConfirm">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        <cti:param name="isEnabled" value="true"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendEnableConfirm.title" 
                            dialogId="drDialog" actionUrl="${sendEnableUrl}" icon="icon-accept"
                            labelKey=".actions.enable"/>
                    </li>
                    <li class="divider"></li>
                    <cti:url var="sendEnableProgramsUrl" value="/dr/program/sendEnableDisableProgramsConfirm">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        <cti:param name="enable" value="true"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendEnableProgramsConfirm.title" 
                            dialogId="drDialog" actionUrl="${sendEnableProgramsUrl}" icon="icon-accept"
                            labelKey=".actions.enablePrograms"/>
                    </li>
                    <cti:url var="sendDisableProgramsUrl" value="/dr/program/sendEnableDisableProgramsConfirm">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        <cti:param name="enable" value="false"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendDisableProgramsConfirm.title" 
                            dialogId="drDialog" actionUrl="${sendDisableProgramsUrl}" icon="icon-delete"
                            labelKey=".actions.disablePrograms"/>
                    </li>
                    <c:choose>
                        <%-- Trigger actions are only active for Control Areas with at least one trigger --%>
                        <c:when test="${!empty controlArea.triggers}">
                            <cti:url var="sendResetPeakUrl" value="/dr/controlArea/sendResetPeakConfirm">
                                <cti:param name="controlAreaId" value="${controlAreaId}"/>
                            </cti:url>
                            <li>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendResetPeakConfirm.title" 
                                    dialogId="drDialog" actionUrl="${sendResetPeakUrl}" icon="icon-control-repeat-blue"
                                    labelKey=".actions.resetPeak"/>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <cm:dropdownOption disabled="true" icon="icon-control-repeat-blue" title="${resetDisabled}" key=".actions.resetPeak"/>
                        </c:otherwise>
                    </c:choose>
                </tags:dynamicChooseOption>

                <%-- Actions shown when the Control Area is inactive and disabled --%>
                <tags:dynamicChooseOption optionId="inactiveDisabled">
                    <cti:url var="startControlAreaUrl" value="/dr/program/start/multipleDetails">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startMultiplePrograms.title" 
                            dialogId="drDialog" actionUrl="${startControlAreaUrl}" icon="icon-control-play-blue"
                            labelKey=".actions.start"/>
                    </li>
                    <cti:msg2 var="disabledMessage" key=".inactive"/>
                    <cm:dropdownOption disabled="true" icon="icon-control-stop-blue" title="${disabledMessage}" key=".actions.stop"/>
                    <li class="divider"></li>
                        <c:choose>
                            <%-- Trigger actions are only active for Control Areas with at least one threshold trigger --%>
                            <c:when test="${controlArea.hasThresholdTrigger}">
                                <cti:url var="sendTriggerChangeUrl" value="/dr/controlArea/getTriggerChangeValues">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey=".getChangeTriggerValues.title" 
                                        dialogId="drDialog" actionUrl="${sendTriggerChangeUrl}" icon="icon-wrench"
                                        labelKey=".actions.triggersChange"/>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <cti:msg2 var="triggersChangeDisabled" key=".actions.triggersChange.disabled"/>
                                <cm:dropdownOption disabled="true" icon="icon-wrench" title="${triggersChangeDisabled}" key=".actions.triggersChange"/>
                            </c:otherwise>
                        </c:choose>
                    <cti:url var="sendChangeTimeWindowUrl" value="/dr/controlArea/getChangeTimeWindowValues">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.title" 
                            dialogId="drDialog" actionUrl="${sendChangeTimeWindowUrl}" icon="icon-time"
                            labelKey=".actions.dailyTimeChange"/>
                    </li>
                    <cti:url var="sendEnableUrl" value="/dr/controlArea/sendEnableConfirm">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        <cti:param name="isEnabled" value="true"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendEnableConfirm.title" 
                            dialogId="drDialog" actionUrl="${sendEnableUrl}" icon="icon-accept"
                            labelKey=".actions.enable"/>
                    </li>
                    <cti:url var="sendEnableProgramsUrl" value="/dr/program/sendEnableDisableProgramsConfirm">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        <cti:param name="enable" value="true"/>
                    </cti:url>
                    <li class="divider"></li>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendEnableProgramsConfirm.title" 
                            dialogId="drDialog" actionUrl="${sendEnableProgramsUrl}" icon="icon-accept"
                            labelKey=".actions.enablePrograms"/>
                    </li>
                    <cti:url var="sendDisableProgramsUrl" value="/dr/program/sendEnableDisableProgramsConfirm">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        <cti:param name="enable" value="false"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendDisableProgramsConfirm.title" 
                            dialogId="drDialog" actionUrl="${sendDisableProgramsUrl}" icon="icon-delete"
                            labelKey=".actions.disablePrograms"/>
                    </li>
                    <c:choose>
                        <%-- Trigger actions are only active for Control Areas with at least one trigger --%>
                        <c:when test="${!empty controlArea.triggers}">
                            <cti:url var="sendResetPeakUrl" value="/dr/controlArea/sendResetPeakConfirm">
                                <cti:param name="controlAreaId" value="${controlAreaId}"/>
                            </cti:url>
                            <li>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendResetPeakConfirm.title" 
                                    dialogId="drDialog" actionUrl="${sendResetPeakUrl}" icon="icon-control-repeat-blue"
                                    labelKey=".actions.resetPeak"/>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <cti:msg2 var="resetDisabled" key=".actions.resetPeak.disabled"/>
                            <cm:dropdownOption disabled="true" icon="icon-control-repeat-blue" title="${resetDisabled}" key=".actions.resetPeak"/>
                        </c:otherwise>
                    </c:choose>
                </tags:dynamicChooseOption>

                <%-- Actions shown when the Control Area is inactive and enabled --%>
                <tags:dynamicChooseOption optionId="inactiveEnabled">
                    <cti:url var="startControlAreaUrl" value="/dr/program/start/multipleDetails">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startMultiplePrograms.title" 
                            dialogId="drDialog" actionUrl="${startControlAreaUrl}" icon="icon-control-play-blue"
                            labelKey=".actions.start"/>
                    </li>
                    <cti:msg2 var="disabledMessage" key=".inactive"/>
                    <cm:dropdownOption disabled="true" icon="icon-control-stop-blue" title="${disabledMessage}" key=".actions.stop"/>
                    <li class="divider"></li>
                        <c:choose>
                            <%-- Trigger actions are only active for Control Areas with at least one threshold trigger --%>
                            <c:when test="${controlArea.hasThresholdTrigger}">
                                <cti:url var="sendTriggerChangeUrl" value="/dr/controlArea/getTriggerChangeValues">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                </cti:url>
                                <li>
                                    <tags:simpleDialogLink titleKey=".getChangeTriggerValues.title" 
                                        dialogId="drDialog" actionUrl="${sendTriggerChangeUrl}" icon="icon-wrench"
                                        labelKey=".actions.triggersChange"/>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <cti:msg2 var="triggersChangeDisabled" key=".actions.triggersChange.disabled"/>
                                <cm:dropdownOption disabled="true" icon="icon-wrench" title="${triggersChangeDisabled}" key=".actions.triggersChange"/>
                            </c:otherwise>
                        </c:choose>
                    <cti:url var="sendChangeTimeWindowUrl" value="/dr/controlArea/getChangeTimeWindowValues">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.title" 
                            dialogId="drDialog" actionUrl="${sendChangeTimeWindowUrl}" icon="icon-time"
                            labelKey=".actions.dailyTimeChange"/>
                    </li>
                    <cti:url var="sendDisableUrl" value="/dr/controlArea/sendEnableConfirm">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        <cti:param name="isEnabled" value="false"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendDisableConfirm.title" 
                            dialogId="drDialog" actionUrl="${sendDisableUrl}" icon="icon-delete"
                            labelKey=".actions.disable"/>
                    </li>
                    <li class="divider"></li>
                    <cti:url var="sendEnableProgramsUrl" value="/dr/program/sendEnableDisableProgramsConfirm">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        <cti:param name="enable" value="true"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendEnableProgramsConfirm.title" 
                            dialogId="drDialog" actionUrl="${sendEnableProgramsUrl}" icon="icon-accept"
                            labelKey=".actions.enablePrograms"/>
                    </li>
                    <cti:url var="sendDisableProgramsUrl" value="/dr/program/sendEnableDisableProgramsConfirm">
                        <cti:param name="controlAreaId" value="${controlAreaId}"/>
                        <cti:param name="enable" value="false"/>
                    </cti:url>
                    <li>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendDisableProgramsConfirm.title" 
                            dialogId="drDialog" actionUrl="${sendDisableProgramsUrl}" icon="icon-delete"
                            labelKey=".actions.disablePrograms"/>
                    </li>
                    <c:choose>
                        <%-- Trigger actions are only active for Control Areas with at least one trigger --%>
                        <c:when test="${!empty controlArea.triggers}">
                            <cti:url var="sendResetPeakUrl" value="/dr/controlArea/sendResetPeakConfirm">
                                <cti:param name="controlAreaId" value="${controlAreaId}"/>
                            </cti:url>
                            <li>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendResetPeakConfirm.title" 
                                    dialogId="drDialog" actionUrl="${sendResetPeakUrl}" icon="icon-control-repeat-blue"
                                    labelKey=".actions.resetPeak"/>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <cti:msg2 var="resetDisabled" key=".actions.resetPeak.disabled"/>
                            <cm:dropdownOption disabled="true" icon="icon-control-repeat-blue" title="${resetDisabled}" key=".actions.resetPeak"/>
                        </c:otherwise>
                    </c:choose>
                </tags:dynamicChooseOption>
            </tags:dynamicChoose>
        </cti:checkPaoAuthorization>

        <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${controlArea}" invert="true">
            <%-- Actions are disabled if the user does not have CONTROL_COMMAND for LM objects --%>
            <cti:msg2 var="noAssignedPrograms" key=".noControl"/>
            <cm:dropdownOption disabled="true" icon="icon-control-play-blue" title="${noAssignedPrograms}" key=".actions.start"/>
            <cm:dropdownOption disabled="true" icon="icon-control-stop-blue" title="${noAssignedPrograms}" key=".actions.stop"/>
            <li class="divider"></li>
            <cm:dropdownOption disabled="true" icon="icon-wrench" title="${noAssignedPrograms}" key=".actions.triggersChange"/>
            <cm:dropdownOption disabled="true" icon="icon-time" title="${noAssignedPrograms}" key=".actions.dailyTimeChange"/>
            <cm:dropdownOption disabled="true" icon="icon-delete" title="${noAssignedPrograms}" key=".actions.disable"/>
            <cm:dropdownOption disabled="true" icon="icon-control-repeat-blue" title="${noAssignedPrograms}" key=".actions.resetPeak"/>
        </cti:checkPaoAuthorization>
    </div>
    
</cti:standardPage>