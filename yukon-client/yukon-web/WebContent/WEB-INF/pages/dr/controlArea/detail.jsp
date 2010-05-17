<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="dr" page="controlAreaDetail">

    <tags:simpleDialog id="drDialog"/>
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
    <cti:includeCss link="/WebConfig/yukon/styles/operator/demandResponse.css"/>
    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>
    <dr:favoriteIconSetup/>
	
    <c:set var="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
    <tags:layoutHeadingPrefixPart>
    	<dr:favoriteIcon paoId="${controlAreaId}" isFavorite="${isFavorite}"/>
    </tags:layoutHeadingPrefixPart>

    <table class="widgetColumns">
        <tr>
            <td class="widgetColumnCell" valign="top">

                <%-- Control Area Info section --%>

                <div class="widgetContainer">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.controlAreaDetail.heading.info"/>
                <tags:abstractContainer type="box" title="${boxTitle}">
                    <tags:nameValueContainer>
                        <cti:checkRolesAndProperties value="CONTROL_AREA_STATE">
                            <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaDetail.info.state"/>
                            <tags:nameValue name="${fieldName}" nameColumnWidth="150px">
                                <dr:controlAreaState controlAreaId="${controlAreaId}"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="CONTROL_AREA_PRIORITY">
                            <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaDetail.info.priority"/>
                            <tags:nameValue name="${fieldName}">
                                <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/PRIORITY"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                        <cti:checkRolesAndProperties value="CONTROL_AREA_TIME_WINDOW">
                            <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaDetail.info.startStop"/>
                            <tags:nameValue name="${fieldName}">
                                <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/START"/>
                                <cti:msg key="yukon.web.modules.dr.controlAreaDetail.info.separator"/>
                                <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/STOP"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                        <%--
                        <cti:checkRolesAndProperties value="CONTROL_AREA_LOAD_CAPACITY">
                            <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaDetail.info.loadCapacity"/>
                            <tags:nameValue name="${fieldName}">
                                <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/LOAD_CAPACITY"/>
                            </tags:nameValue>
                        </cti:checkRolesAndProperties>
                        --%>

                        <c:if test="${!empty controlArea.triggers}">
                            <c:forEach var="trigger" items="${controlArea.triggers}">
                                <cti:checkRolesAndProperties value="CONTROL_AREA_VALUE_THRESHOLD,CONTROL_AREA_PEAK_PROJECTION,CONTROL_AREA_ATKU">
                                    <c:set var="triggerNumber" value="${trigger.triggerNumber}"/>
                                    <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaDetail.info.trigger" argument="${triggerNumber}"/>
                                    <tags:nameValue name="${fieldName}" isSection="true">
                                        <cti:checkRolesAndProperties value="CONTROL_AREA_VALUE_THRESHOLD">
                                            <cti:msg var="thresholdTitle" key="yukon.web.modules.dr.controlAreaDetail.info.valueThreshold"/>
                                            <tags:nameValue name="${thresholdTitle}">
                                                <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${triggerNumber}/VALUE_THRESHOLD"/>
                                            </tags:nameValue>
                                        </cti:checkRolesAndProperties>
                                        <cti:checkRolesAndProperties value="CONTROL_AREA_PEAK_PROJECTION">
                                            <cti:msg var="peakTitle" key="yukon.web.modules.dr.controlAreaDetail.info.peakProjection"/>
                                            <tags:nameValue name="${peakTitle}">
                                                <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${triggerNumber}/PEAK_PROJECTION"/>
                                            </tags:nameValue>
                                        </cti:checkRolesAndProperties>
                                        <cti:checkRolesAndProperties value="CONTROL_AREA_ATKU">
                                            <cti:msg var="atkuTitle" key="yukon.web.modules.dr.controlAreaDetail.info.atku"/>
                                            <tags:nameValue name="${atkuTitle}">
                                                <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${triggerNumber}/ATKU"/>
                                            </tags:nameValue>
                                        </cti:checkRolesAndProperties>
                                    </tags:nameValue>
                                </cti:checkRolesAndProperties>
                            </c:forEach>
                        </c:if>
                    </tags:nameValueContainer>
                    <c:if test="${empty controlArea.triggers}">
                        <br/>
                        <cti:msg key="yukon.web.modules.dr.controlAreaDetail.info.noTriggers"/>
                    </c:if>
                </tags:abstractContainer>
                </div>
            </td>
            <td class="widgetColumnCell" valign="top">

                <%--
                    Control Area Actions section each action has a simpleDialogLink that
                    pops open a dialog for the action.  The available actions are based
                    on the dynamically updated SHOW_ACTION value
                --%>

                <div class="widgetContainer">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.controlAreaDetail.heading.actions"/>
                <tags:abstractContainer type="box" title="${boxTitle}">
                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${controlArea}">

                        <%-- Actions are enabled only if the user has CONTROL_COMMAND for LM objects --%>
                        <tags:dynamicChoose updaterString="DR_CONTROLAREA/${controlAreaId}/SHOW_ACTION" suffix="${controlAreaId}">
                            <tags:dynamicChooseOption optionId="noAssignedPrograms">
                                <%-- Actions are disabled if the user does not have CONTROL_COMMAND for LM objects --%>

                                <cti:msg var="noAssignedPrograms" key="yukon.web.modules.dr.controlAreaDetail.noAssignedPrograms"/>
                                <div class="subtleGray" title="${noAssignedPrograms}">
                                    <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.startIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.start"/>
                                </div>
                                <div class="subtleGray" title="${noAssignedPrograms}">
                                    <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.stopIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.stop"/>
                                </div>
                                <div class="subtleGray" title="${noAssignedPrograms}">
                                    <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChangeIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange"/>
                                </div>
                                <div class="subtleGray" title="${noAssignedPrograms}">
                                    <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChangeIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChange"/>
                                </div>
                                <div class="subtleGray" title="${noAssignedPrograms}">
                                    <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.disableIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.disable"/>
                                </div>
                                <div class="subtleGray" title="${noAssignedPrograms}">
                                    <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeakIcon.disabled"/>
                                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak"/>
                                </div>
                                
                            </tags:dynamicChooseOption>
                            <tags:dynamicChooseOption optionId="enabled">

                                <%-- Actions shown when the Control Area is enabled but not fully active --%>

                                <cti:url var="startControlAreaUrl" value="/spring/dr/program/start/multipleDetails">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startMultiplePrograms.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${startControlAreaUrl}"
                                                       logoKey="yukon.web.modules.dr.controlAreaDetail.actions.startIcon"
                                                       labelKey="yukon.web.modules.dr.controlAreaDetail.actions.start"/>
                                <br>

                                <cti:url var="stopControlAreaUrl" value="/spring/dr/program/stop/multipleDetails">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopMultiplePrograms.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${stopControlAreaUrl}"
                                                       logoKey="yukon.web.modules.dr.controlAreaDetail.actions.stopIcon"
                                                       labelKey="yukon.web.modules.dr.controlAreaDetail.actions.stop"/>
                                <br>

                                <c:choose>
                                    <%-- Trigger actions are only active for Control Areas with at least one threshold trigger --%>
                                    <c:when test="${controlArea.hasThresholdTrigger}">
                                        <cti:url var="sendTriggerChangeUrl" value="/spring/dr/controlArea/getTriggerChangeValues">
                                            <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                        </cti:url>
                                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.getChangeTriggerValues.title"
                                                               dialogId="drDialog"
                                                               actionUrl="${sendTriggerChangeUrl}"
                                                               logoKey="yukon.web.modules.dr.controlAreaDetail.actions.triggersChangeIcon"
                                                               labelKey="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:msg var="triggersChangeDisabled" key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange.disabled"/>
                                        <span class="subtleGray" title="${triggersChangeDisabled}">
                                            <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChangeIcon.disabled"/>
                                            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange"/>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                                <br>

                                <cti:url var="sendChangeTimeWindowUrl" value="/spring/dr/controlArea/getChangeTimeWindowValues">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${sendChangeTimeWindowUrl}"
                                                       logoKey="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChangeIcon"
                                                       labelKey="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChange"/>
                                <br>

                                <cti:url var="sendDisableUrl" value="/spring/dr/controlArea/sendEnableConfirm">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                    <cti:param name="isEnabled" value="false"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendDisableConfirm.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${sendDisableUrl}"
                                                       logoKey="yukon.web.modules.dr.controlAreaDetail.actions.disableIcon"
                                                       labelKey="yukon.web.modules.dr.controlAreaDetail.actions.disable"/>
                                <br>
                                <c:choose>
                                    <%-- Trigger actions are only active for Control Areas with at least one trigger --%>
                                    <c:when test="${!empty controlArea.triggers}">
                                        <cti:url var="sendResetPeakUrl" value="/spring/dr/controlArea/sendResetPeakConfirm">
                                            <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                        </cti:url>
                                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendResetPeakConfirm.title"
                                                               dialogId="drDialog"
                                                               actionUrl="${sendResetPeakUrl}"
                                                               logoKey="yukon.web.modules.dr.controlAreaDetail.actions.resetPeakIcon"
                                                               labelKey="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:msg var="resetDisabled" key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak.disabled"/>
                                        <span class="subtleGray" title="${resetDisabled}">
                                            <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeakIcon.disabled"/>
                                            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak"/>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                                <br>
                            </tags:dynamicChooseOption>
                            <tags:dynamicChooseOption optionId="fullyActiveEnabled">

                                <%-- Actions shown when the Control Area is fully active and enabled --%>

                                <cti:url var="stopControlAreaUrl" value="/spring/dr/program/stop/multipleDetails">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopMultiplePrograms.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${stopControlAreaUrl}"
                                                       logoKey="yukon.web.modules.dr.controlAreaDetail.actions.stopIcon"
                                                       labelKey="yukon.web.modules.dr.controlAreaDetail.actions.stop"/>
                                <br>

                                <c:choose>
                                    <%-- Trigger actions are only active for Control Areas with at least one trigger --%>
                                    <c:when test="${!empty controlArea.triggers}">
                                        <cti:url var="sendTriggerChangeUrl" value="/spring/dr/controlArea/getTriggerChangeValues">
                                            <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                        </cti:url>
                                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.getChangeTriggerValues.title"
                                                               dialogId="drDialog"
                                                               actionUrl="${sendTriggerChangeUrl}"
                                                               logoKey="yukon.web.modules.dr.controlAreaDetail.actions.triggersChangeIcon"
                                                               labelKey="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:msg var="triggersChangeDisabled" key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange.disabled"/>
                                        <span class="subtleGray" title="${triggersChangeDisabled}">
                                            <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChangeIcon.disabled"/>
                                            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange"/>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                                <br>

                                <cti:url var="sendChangeTimeWindowUrl" value="/spring/dr/controlArea/getChangeTimeWindowValues">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${sendChangeTimeWindowUrl}"
                                                       logoKey="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChangeIcon"
                                                       labelKey="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChange"/>
                                <br>

                                <cti:url var="sendDisableUrl" value="/spring/dr/controlArea/sendEnableConfirm">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                    <cti:param name="isEnabled" value="false"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendDisableConfirm.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${sendDisableUrl}"
                                                       logoKey="yukon.web.modules.dr.controlAreaDetail.actions.disableIcon"
                                                       labelKey="yukon.web.modules.dr.controlAreaDetail.actions.disable"/>
                                <br>
                                <c:choose>
                                    <%-- Trigger actions are only active for Control Areas with at least one trigger --%>
                                    <c:when test="${!empty controlArea.triggers}">
                                        <cti:url var="sendResetPeakUrl" value="/spring/dr/controlArea/sendResetPeakConfirm">
                                            <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                        </cti:url>
                                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendResetPeakConfirm.title"
                                                               dialogId="drDialog"
                                                               actionUrl="${sendResetPeakUrl}"
                                                               logoKey="yukon.web.modules.dr.controlAreaDetail.actions.resetPeakIcon"
                                                               labelKey="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:msg var="resetDisabled" key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak.disabled"/>
                                        <span class="subtleGray" title="${resetDisabled}">
                                            <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeakIcon.disabled"/>
                                            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak"/>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                                <br>
                            </tags:dynamicChooseOption>
                            <tags:dynamicChooseOption optionId="disabled">

                                <%-- Actions shown when the Control Area is disabled but not fully active --%>

                                <cti:url var="startControlAreaUrl" value="/spring/dr/program/start/multipleDetails">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startMultiplePrograms.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${startControlAreaUrl}"
                                                       logoKey="yukon.web.modules.dr.controlAreaDetail.actions.startIcon"
                                                       labelKey="yukon.web.modules.dr.controlAreaDetail.actions.start"/>
                                <br>
                                <cti:url var="stopControlAreaUrl" value="/spring/dr/program/stop/multipleDetails">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopMultiplePrograms.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${stopControlAreaUrl}"
                                                       logoKey="yukon.web.modules.dr.controlAreaDetail.actions.stopIcon"
                                                       labelKey="yukon.web.modules.dr.controlAreaDetail.actions.stop"/>
                                <br>

                                <c:choose>
                                    <%-- Trigger actions are only active for Control Areas with at least one trigger --%>
                                    <c:when test="${!empty controlArea.triggers}">
                                        <cti:url var="sendTriggerChangeUrl" value="/spring/dr/controlArea/getTriggerChangeValues">
                                            <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                        </cti:url>
                                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.getChangeTriggerValues.title"
                                                               dialogId="drDialog"
                                                               actionUrl="${sendTriggerChangeUrl}"
                                                               logoKey="yukon.web.modules.dr.controlAreaDetail.actions.triggersChangeIcon"
                                                               labelKey="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:msg var="triggersChangeDisabled" key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange.disabled"/>
                                        <span class="subtleGray" title="${triggersChangeDisabled}">
                                            <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChangeIcon.disabled"/>
                                            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange"/>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                                <br>

                                <cti:url var="sendChangeTimeWindowUrl" value="/spring/dr/controlArea/getChangeTimeWindowValues">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${sendChangeTimeWindowUrl}"
                                                       logoKey="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChangeIcon"
                                                       labelKey="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChange"/>
                                <br>

                                <cti:url var="sendEnableUrl" value="/spring/dr/controlArea/sendEnableConfirm">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                    <cti:param name="isEnabled" value="true"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendEnableConfirm.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${sendEnableUrl}"
                                                       logoKey="yukon.web.modules.dr.controlAreaDetail.actions.enableIcon"
                                                       labelKey="yukon.web.modules.dr.controlAreaDetail.actions.enable"/>
                                <br>
                                <c:choose>
                                    <%-- Trigger actions are only active for Control Areas with at least one trigger --%>
                                    <c:when test="${!empty controlArea.triggers}">
                                        <cti:url var="sendResetPeakUrl" value="/spring/dr/controlArea/sendResetPeakConfirm">
                                            <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                        </cti:url>
                                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendResetPeakConfirm.title"
                                                               dialogId="drDialog"
                                                               actionUrl="${sendResetPeakUrl}"
                                                               logoKey="yukon.web.modules.dr.controlAreaDetail.actions.resetPeakIcon"
                                                               labelKey="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:msg var="resetDisabled" key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak.disabled"/>
                                        <span class="subtleGray" title="${resetDisabled}">
                                            <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeakIcon.disabled"/>
                                            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak"/>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                                <br>
                            </tags:dynamicChooseOption>
                            <tags:dynamicChooseOption optionId="fullyActiveDisabled">

                                <%-- Actions shown when the Control Area is fully active and disabled --%>

                                <cti:url var="stopControlAreaUrl" value="/spring/dr/program/stop/multipleDetails">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopMultiplePrograms.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${stopControlAreaUrl}"
                                                       logoKey="yukon.web.modules.dr.controlAreaDetail.actions.stopIcon"
                                                       labelKey="yukon.web.modules.dr.controlAreaDetail.actions.stop"/>
                                <br>

                                <c:choose>
                                    <%-- Trigger actions are only active for Control Areas with at least one trigger --%>
                                    <c:when test="${!empty controlArea.triggers}">
                                        <cti:url var="sendTriggerChangeUrl" value="/spring/dr/controlArea/getTriggerChangeValues">
                                            <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                        </cti:url>
                                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.getChangeTriggerValues.title"
                                                               dialogId="drDialog"
                                                               actionUrl="${sendTriggerChangeUrl}"
                                                               logoKey="yukon.web.modules.dr.controlAreaDetail.actions.triggersChangeIcon"
                                                               labelKey="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:msg var="triggersChangeDisabled" key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange.disabled"/>
                                        <span class="subtleGray" title="${triggersChangeDisabled}">
                                            <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChangeIcon.disabled"/>
                                            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange"/>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                                <br>

                                <cti:url var="sendChangeTimeWindowUrl" value="/spring/dr/controlArea/getChangeTimeWindowValues">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${sendChangeTimeWindowUrl}"
                                                       logoKey="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChangeIcon"
                                                       labelKey="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChange"/>
                                <br>

                                <cti:url var="sendEnableUrl" value="/spring/dr/controlArea/sendEnableConfirm">
                                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                    <cti:param name="isEnabled" value="true"/>
                                </cti:url>
                                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendEnableConfirm.title"
                                                       dialogId="drDialog"
                                                       actionUrl="${sendEnableUrl}"
                                                       logoKey="yukon.web.modules.dr.controlAreaDetail.actions.enableIcon"
                                                       labelKey="yukon.web.modules.dr.controlAreaDetail.actions.enable"/>
                                <br>
                                <c:choose>
                                    <%-- Trigger actions are only active for Control Areas with at least one trigger --%>
                                    <c:when test="${!empty controlArea.triggers}">
                                        <cti:url var="sendResetPeakUrl" value="/spring/dr/controlArea/sendResetPeakConfirm">
                                            <cti:param name="controlAreaId" value="${controlAreaId}"/>
                                        </cti:url>
                                        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendResetPeakConfirm.title"
                                                               dialogId="drDialog"
                                                               actionUrl="${sendResetPeakUrl}"
                                                               logoKey="yukon.web.modules.dr.controlAreaDetail.actions.resetPeakIcon"
                                                               labelKey="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:msg var="resetDisabled" key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak.disabled"/>
                                        <span class="subtleGray" title="${resetDisabled}">
                                            <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeakIcon.disabled"/>
                                            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak"/>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                                <br>
                            </tags:dynamicChooseOption>
                        </tags:dynamicChoose>
                    </cti:checkPaoAuthorization>
                    <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${controlArea}" invert="true">

                        <%-- Actions are disabled if the user does not have CONTROL_COMMAND for LM objects --%>

                        <cti:msg var="noAssignedPrograms" key="yukon.web.modules.dr.controlAreaDetail.noControl"/>
                        <div class="subtleGray" title="${noAssignedPrograms}">
                            <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.startIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.start"/>
                        </div>
                        <div class="subtleGray" title="${noAssignedPrograms}">
                            <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.stopIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.stop"/>
                        </div>
                        <div class="subtleGray" title="${noAssignedPrograms}">
                            <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChangeIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange"/>
                        </div>
                        <div class="subtleGray" title="${noAssignedPrograms}">
                            <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChangeIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChange"/>
                        </div>
                        <div class="subtleGray" title="${noAssignedPrograms}">
                            <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.disableIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.disable"/>
                        </div>
                        <div class="subtleGray" title="${noAssignedPrograms}">
                            <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeakIcon.disabled"/>
                            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak"/>
                        </div>
                    </cti:checkPaoAuthorization>
                </tags:abstractContainer>
                </div>
            </td>
        </tr>

        <%-- Child programs for the control area --%>
        <tr>
            <td class="widgetColumnCell" colspan="2">
                <div class="widgetContainer">
				    <c:set var="baseUrl" value="/spring/dr/controlArea/detail"/>
				    <%@ include file="../program/programList.jspf" %>
                </div>
            </td>
        </tr>
    </table>

</cti:standardPage>
