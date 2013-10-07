<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dr" page="controlAreaDetail">

    <tags:simpleDialog id="drDialog"/>
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>
    <cti:includeScript link="/JavaScript/hideReveal.js"/>

    <c:set var="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>

    <input id="assetId" type="hidden" value="${controlAreaId}"/>
    <cti:includeScript link="/JavaScript/drAssetDetails.js"/>

    <div class="column_12_12">
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
              </tags:nameValueContainer2>

              <tags:nameValueContainer2>
                    <c:if test="${empty controlArea.triggers}">
                        <span class="empty-list"><i:inline key=".info.noTriggers"/></span>
                    </c:if>
                    <c:if test="${!empty controlArea.triggers}">
                        <c:forEach var="trigger" items="${controlArea.triggers}">
                            <cti:checkRolesAndProperties value="CONTROL_AREA_VALUE_THRESHOLD,CONTROL_AREA_PEAK_PROJECTION,CONTROL_AREA_ATKU">
                                <c:set var="triggerNumber" value="${trigger.triggerNumber}"/>
                                <tags:nameValue2 rowClass="smallBoldLabel" nameKey=".info.trigger" argument="${triggerNumber}"></tags:nameValue2>
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
                <%-- Display the Asset Availability Info --%>
                <tags:sectionContainer2 nameKey="assetAvailability">
                    <c:choose>
                        <c:when test="${dispatchDisconnected}">
                            <span class="error">
                                <i:inline key="yukon.web.modules.operator.hardware.assetAvailability.dispatchDisconnected"/>
                            </span>
                        </c:when>
                        <c:otherwise>
                            <c:choose>
                                <c:when test="${assetTotal <= 0}">
                                    <span class="empty-list">
                                        <cti:msg2 key="yukon.web.modules.operator.hardware.assetAvailability.noDevices"/>
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <dr:assetAvailabilityStatus assetId="${controlAreaId}"
                                        assetAvailabilitySummary="${assetAvailabilitySummary}" 
                                        pieJSONData="${pieJSONData}" showDetails="true"/>
                                </c:otherwise>
                            </c:choose>
                        </c:otherwise>
                    </c:choose>
                </tags:sectionContainer2>
            </cti:checkRolesAndProperties>
            
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
                                <li>
                                    <a class="clearfix" title="${disabledMessage}"> 
                                        <cti:icon icon="icon-control-play-blue" classes="disabled" /> 
                                        <span class="fl dib disabled">
                                            <cti:msg2 key=".actions.start" />
                                        </span>
                                    </a>
                                </li>
                                <li>
                                    <a class="clearfix" title="${disabledMessage}"> 
                                        <cti:icon icon="icon-control-stop-blue" classes="disabled" /> 
                                        <span class="fl dib disabled">
                                            <cti:msg2 key=".actions.stop" />
                                        </span>
                                    </a>
                                </li>
                                <li class="divider"></li>
                                <li>
                                    <a class="clearfix" title="${disabledMessage}"> 
                                        <cti:icon icon="icon-wrench" classes="disabled" /> 
                                        <span class="fl dib disabled">
                                            <cti:msg2 key=".actions.triggersChange" />
                                        </span>
                                    </a>
                                </li>
                                <li>
                                    <a class="clearfix" title="${disabledMessage}"> 
                                        <cti:icon icon="icon-time" classes="disabled" /> 
                                        <span class="fl dib disabled">
                                            <cti:msg2 key=".actions.dailyTimeChange" />
                                        </span>
                                    </a>
                                </li>
                                <li>
                                    <a class="clearfix" title="${disabledMessage}"> 
                                        <cti:icon icon="icon-delete" classes="disabled" /> 
                                        <span class="fl dib disabled">
                                            <cti:msg2 key=".actions.disable" />
                                        </span>
                                    </a>
                                </li>
                                <li class="divider"></li>
                                <li>
                                    <a class="clearfix" title="${disabledMessage}"> 
                                        <cti:icon icon="icon-accept" classes="disabled" /> 
                                        <span class="fl dib disabled">
                                            <cti:msg2 key=".actions.enablePrograms" />
                                        </span>
                                    </a>
                                </li>
                                <li>
                                    <a class="clearfix" title="${disabledMessage}"> 
                                        <cti:icon icon="icon-delete" classes="disabled" /> 
                                        <span class="fl dib disabled">
                                            <cti:msg2 key=".actions.disablePrograms" />
                                        </span>
                                    </a>
                                </li>
                                <li>
                                    <a class="clearfix" title="${disabledMessage}"> 
                                        <cti:icon icon="icon-control-repeat-blue" classes="disabled" /> 
                                        <span class="fl dib disabled">
                                            <cti:msg2 key=".actions.resetPeak" />
                                        </span>
                                    </a>
                                </li>
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
                                            <li>
                                                <cti:msg2 var="triggersChangeDisabled" key=".actions.triggersChange.disabled"/>
                                                <a class="clearfix" title="${triggersChangeDisabled}"> 
                                                    <cti:icon icon="icon-wrench" classes="disabled" /> 
                                                    <span class="fl dib disabled">
                                                        <cti:msg2 key=".actions.triggersChange" />
                                                    </span>
                                                </a>
                                            </li>
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
                                        <li>
                                            <cti:msg2 var="resetDisabled" key=".actions.resetPeak.disabled"/>
                                            <a class="clearfix" title="${resetDisabled}"> 
                                                <cti:icon icon="icon-control-repeat-blue" classes="disabled" /> 
                                                <span class="fl dib disabled">
                                                    <cti:msg2 key=".actions.resetPeak" />
                                                </span>
                                            </a>
                                        </li>
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
                                            <li>
                                                <cti:msg2 var="triggersChangeDisabled" key=".actions.triggersChange.disabled"/>
                                                <a class="clearfix" title="${triggersChangeDisabled}"> 
                                                    <cti:icon icon="icon-wrench" classes="disabled" /> 
                                                    <span class="fl dib disabled">
                                                        <cti:msg2 key=".actions.triggersChange" />
                                                    </span>
                                                </a>
                                            </li>
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
                                        <li>
                                            <cti:msg2 var="resetDisabled" key=".actions.resetPeak.disabled"/>
                                            <a class="clearfix" title="${resetDisabled}"> 
                                                <cti:icon icon="icon-control-repeat-blue" classes="disabled" /> 
                                                <span class="fl dib disabled">
                                                    <cti:msg2 key=".actions.resetPeak" />
                                                </span>
                                            </a>
                                        </li>
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
                                            <li>
                                                <cti:msg2 var="triggersChangeDisabled" key=".actions.triggersChange.disabled"/>
                                                <a class="clearfix" title="${triggersChangeDisabled}"> 
                                                    <cti:icon icon="icon-wrench" classes="disabled" /> 
                                                    <span class="fl dib disabled">
                                                        <cti:msg2 key=".actions.triggersChange" />
                                                    </span>
                                                </a>
                                            </li>
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
                                        <li>
                                            <cti:msg2 var="resetDisabled" key=".actions.resetPeak.disabled"/>
                                            <a class="clearfix" title="${resetDisabled}"> 
                                                <cti:icon icon="icon-control-repeat-blue" classes="disabled" /> 
                                                <span class="fl dib disabled">
                                                    <cti:msg2 key=".actions.resetPeak" />
                                                </span>
                                            </a>
                                        </li>
                                    </c:otherwise>
                                </c:choose>
                            </tags:dynamicChooseOption>

                            <%-- Actions shown when the Control Area is fully active and disabled --%>
                            <tags:dynamicChooseOption optionId="fullyActiveDisabled">
                                <li>
                                    <cti:msg var="disabledMessage" key="yukon.web.modules.dr.controlAreaDetail.fullyActive"/>
                                    <a class="clearfix" title="${disabledMessage}"> 
                                        <cti:icon icon="icon-control-play-blue" classes="disabled" /> 
                                        <span class="fl dib disabled">
                                            <cti:msg2 key=".actions.start" />
                                        </span>
                                    </a>
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
                                            <li>
                                                <cti:msg2 var="triggersChangeDisabled" key=".actions.triggersChange.disabled"/>
                                                <a class="clearfix" title="${triggersChangeDisabled}"> 
                                                    <cti:icon icon="icon-wrench" classes="disabled" /> 
                                                    <span class="fl dib disabled">
                                                        <cti:msg2 key=".actions.triggersChange" />
                                                    </span>
                                                </a>
                                            </li>
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
                                        <li>
                                            <cti:msg2 var="resetDisabled" key=".actions.resetPeak.disabled"/>
                                            <a class="clearfix" title="${resetDisabled}"> 
                                                <cti:icon icon="icon-control-repeat-blue" classes="disabled" /> 
                                                <span class="fl dib disabled">
                                                    <cti:msg2 key=".actions.resetPeak" />
                                                </span>
                                            </a>
                                        </li>
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
                                <li>
                                    <cti:msg var="disabledMessage" key="yukon.web.modules.dr.controlAreaDetail.inactive"/>
                                    <a class="clearfix" title="${disabledMessage}"> 
                                        <cti:icon icon="icon-control-stop-blue" classes="disabled" /> 
                                        <span class="fl dib disabled">
                                            <cti:msg2 key=".actions.stop" />
                                        </span>
                                    </a>
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
                                            <li>
                                                <cti:msg2 var="triggersChangeDisabled" key=".actions.triggersChange.disabled"/>
                                                <a class="clearfix" title="${triggersChangeDisabled}"> 
                                                    <cti:icon icon="icon-wrench" classes="disabled" /> 
                                                    <span class="fl dib disabled">
                                                        <cti:msg2 key=".actions.triggersChange" />
                                                    </span>
                                                </a>
                                            </li>
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
                                        <li>
                                            <cti:msg2 var="resetDisabled" key=".actions.resetPeak.disabled"/>
                                            <a class="clearfix" title="${resetDisabled}"> 
                                                <cti:icon icon="icon-control-repeat-blue" classes="disabled" /> 
                                                <span class="fl dib disabled">
                                                    <cti:msg2 key=".actions.resetPeak" />
                                                </span>
                                            </a>
                                        </li>
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
                                <li>
                                    <cti:msg var="disabledMessage" key="yukon.web.modules.dr.controlAreaDetail.inactive"/>
                                    <a class="clearfix" title="${disabledMessage}"> 
                                        <cti:icon icon="icon-control-stop-blue" classes="disabled" /> 
                                        <span class="fl dib disabled">
                                            <cti:msg2 key=".actions.stop" />
                                        </span>
                                    </a>
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
                                            <li>
                                                <cti:msg2 var="triggersChangeDisabled" key=".actions.triggersChange.disabled"/>
                                                <a class="clearfix" title="${triggersChangeDisabled}"> 
                                                    <cti:icon icon="icon-wrench" classes="disabled" /> 
                                                    <span class="fl dib disabled">
                                                        <cti:msg2 key=".actions.triggersChange" />
                                                    </span>
                                                </a>
                                            </li>
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
                                        <li>
                                            <cti:msg2 var="resetDisabled" key=".actions.resetPeak.disabled"/>
                                            <a class="clearfix" title="${resetDisabled}"> 
                                                <cti:icon icon="icon-control-repeat-blue" classes="disabled" /> 
                                                <span class="fl dib disabled">
                                                    <cti:msg2 key=".actions.resetPeak" />
                                                </span>
                                            </a>
                                        </li>
                                    </c:otherwise>
                                </c:choose>
                            </tags:dynamicChooseOption>
                        </tags:dynamicChoose>
                    </cti:checkPaoAuthorization>

                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${controlArea}" invert="true">
                        <%-- Actions are disabled if the user does not have CONTROL_COMMAND for LM objects --%>
                        <cti:msg var="noAssignedPrograms" key="yukon.web.modules.dr.controlAreaDetail.noControl"/>
                        <li>
                            <a class="clearfix" title="${noAssignedPrograms}"> 
                                <cti:icon icon="icon-control-play-blue" classes="disabled" /> 
                                <span class="fl dib disabled">
                                    <cti:msg2 key=".actions.start" />
                                </span>
                            </a>
                        </li>
                        <li>
                            <a class="clearfix" title="${noAssignedPrograms}"> 
                                <cti:icon icon="icon-control-stop-blue" classes="disabled" /> 
                                <span class="fl dib disabled">
                                    <cti:msg2 key=".actions.stop" />
                                </span>
                            </a>
                        </li>
                        <li class="divider"></li>
                        <li>
                            <a class="clearfix" title="${noAssignedPrograms}"> 
                                <cti:icon icon="icon-wrench" classes="disabled" /> 
                                <span class="fl dib disabled">
                                    <cti:msg2 key=".actions.triggersChange" />
                                </span>
                            </a>
                        </li>
                        <li>
                            <a class="clearfix" title="${noAssignedPrograms}"> 
                                <cti:icon icon="icon-time" classes="disabled" /> 
                                <span class="fl dib disabled">
                                    <cti:msg2 key=".actions.dailyTimeChange" />
                                </span>
                            </a>
                        </li>
                        <li>
                            <a class="clearfix" title="${noAssignedPrograms}"> 
                                <cti:icon icon="icon-delete" classes="disabled" /> 
                                <span class="fl dib disabled">
                                    <cti:msg2 key=".actions.disable" />
                                </span>
                            </a>
                        </li>
                        <li>
                            <a class="clearfix" title="${noAssignedPrograms}"> 
                                <cti:icon icon="icon-control-repeat-blue" classes="disabled" /> 
                                <span class="fl dib disabled">
                                    <cti:msg2 key=".actions.resetPeak" />
                                </span>
                            </a>
                        </li>
                    </cti:checkPaoAuthorization>
                </div>
        </div>
    </div>

    <div class="column_24">
        <div class="column one nogutter">
            <c:set var="baseUrl" value="/dr/controlArea/detail"/>
            <%@ include file="../program/programList.jspf" %>
        </div>
    </div>
</cti:standardPage>
