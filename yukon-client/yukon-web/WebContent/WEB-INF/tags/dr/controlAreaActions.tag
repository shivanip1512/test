<%@ attribute name="controlArea" required="true" type="com.cannontech.common.pao.DisplayablePao" %>
<%@ attribute name="state" required="true" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:dynamicChooseOption optionId="${pageScope.state}">

<c:set var="controlAreaId" value="${pageScope.controlArea.paoIdentifier.paoId}"/>


<%-- Start --%>
<c:set var="startEnabled" value="true"/>
<c:if test="${pageScope.state == 'noAssignedPrograms'}">
    <c:set var="startEnabled" value="false"/>
    <cti:msg var="disabledMessage" key="yukon.web.modules.dr.controlAreaDetail.noAssignedPrograms"/>
</c:if>
<c:if test="${pageScope.state == 'fullyActiveEnabled' || pageScope.state == 'fullyActiveDisabled'}">
    <c:set var="startEnabled" value="false"/>
    <cti:msg var="disabledMessage" key="yukon.web.modules.dr.controlAreaDetail.fullyActive"/>
</c:if>

<c:if test="${startEnabled}">
    <cti:url var="startControlAreaUrl" value="/spring/dr/program/start/multipleDetails">
        <cti:param name="controlAreaId" value="${controlAreaId}"/>
    </cti:url>
    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startMultiplePrograms.title"
        dialogId="drDialog" actionUrl="${startControlAreaUrl}"
        logoKey="yukon.web.modules.dr.controlAreaDetail.actions.startIcon"
        labelKey="yukon.web.modules.dr.controlAreaDetail.actions.start"/>
</c:if>
<c:if test="${!startEnabled}">
    <span class="disabledAction" title="${disabledMessage}">
        <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.startIcon.disabled"/>
        <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.start"/>
    </span>
</c:if>
<br>


<%-- Stop --%>
<c:set var="stopEnabled" value="true"/>
<c:if test="${pageScope.state == 'noAssignedPrograms'}">
    <c:set var="stopEnabled" value="false"/>
</c:if>
<c:if test="${pageScope.state == 'inactiveEnabled' || pageScope.state == 'inactiveDisabled'}">
    <c:set var="stopEnabled" value="false"/>
    <cti:msg var="disabledMessage" key="yukon.web.modules.dr.controlAreaDetail.inactive"/>
</c:if>

<c:if test="${stopEnabled}">
    <cti:url var="stopControlAreaUrl" value="/spring/dr/program/stop/multipleDetails">
        <cti:param name="controlAreaId" value="${controlAreaId}"/>
    </cti:url>
    <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopMultiplePrograms.title"
        dialogId="drDialog" actionUrl="${stopControlAreaUrl}"
        logoKey="yukon.web.modules.dr.controlAreaDetail.actions.stopIcon"
        labelKey="yukon.web.modules.dr.controlAreaDetail.actions.stop"/>
</c:if>
<c:if test="${!stopEnabled}">
    <span class="disabledAction" title="${disabledMessage}">
        <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.stopIcon.disabled"/>
        <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.stop"/>
    </span>
</c:if>
<br>


<%-- Triggers Change --%>
<c:choose>
    <c:when test="${pageScope.state == 'noAssignedPrograms'}">
        <span class="disabledAction" title="${disabledMessage}">
            <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChangeIcon.disabled"/>
            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange"/>
        </span>
    </c:when>
    <c:otherwise>
        <c:choose>
            <%-- Trigger actions are only active for Control Areas with at least one threshold trigger --%>
            <c:when test="${pageScope.controlArea.hasThresholdTrigger}">
                <cti:url var="sendTriggerChangeUrl" value="/spring/dr/controlArea/getTriggerChangeValues">
                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                </cti:url>
                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.getChangeTriggerValues.title"
                    dialogId="drDialog" actionUrl="${sendTriggerChangeUrl}"
                    logoKey="yukon.web.modules.dr.controlAreaDetail.actions.triggersChangeIcon"
                    labelKey="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange"/>
            </c:when>
            <c:otherwise>
                <cti:msg var="triggersChangeDisabled" key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange.disabled"/>
                <span class="disabledAction" title="${triggersChangeDisabled}">
                    <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChangeIcon.disabled"/>
                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange"/>
                </span>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
<br>

<%-- Daily Time Change --%>
<c:choose>
    <c:when test="${pageScope.state == 'noAssignedPrograms'}">
        <span class="disabledAction" title="${disabledMessage}">
            <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChangeIcon.disabled"/>
            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChange"/>
        </span>
    </c:when>
    <c:otherwise>
        <cti:url var="sendChangeTimeWindowUrl" value="/spring/dr/controlArea/getChangeTimeWindowValues">
            <cti:param name="controlAreaId" value="${controlAreaId}"/>
        </cti:url>
        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.title"
            dialogId="drDialog" actionUrl="${sendChangeTimeWindowUrl}"
            logoKey="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChangeIcon"
            labelKey="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChange"/>
    </c:otherwise>
</c:choose>
<br>

<%-- Enable/Disable --%>
<c:choose>
    <c:when test="${pageScope.state == 'noAssignedPrograms'}">
        <span class="disabledAction" title="${disabledMessage}">
            <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.disableIcon.disabled"/>
            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.disable"/>
        </span>
    </c:when>
    <c:when test="${pageScope.state == 'enabled' || pageScope.state == 'fullyActiveEnabled' || pageScope.state == 'inactiveEnabled'}">
        <cti:url var="sendDisableUrl" value="/spring/dr/controlArea/sendEnableConfirm">
            <cti:param name="controlAreaId" value="${controlAreaId}"/>
            <cti:param name="isEnabled" value="false"/>
        </cti:url>
        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendDisableConfirm.title"
            dialogId="drDialog" actionUrl="${sendDisableUrl}"
            logoKey="yukon.web.modules.dr.controlAreaDetail.actions.disableIcon"
            labelKey="yukon.web.modules.dr.controlAreaDetail.actions.disable"/>
    </c:when>
    <c:when test="${pageScope.state == 'disabled' || pageScope.state == 'fullyActiveDisabled' || pageScope.state == 'inactiveDisabled'}">
        <cti:url var="sendEnableUrl" value="/spring/dr/controlArea/sendEnableConfirm">
            <cti:param name="controlAreaId" value="${controlAreaId}"/>
            <cti:param name="isEnabled" value="true"/>
        </cti:url>
        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendEnableConfirm.title"
            dialogId="drDialog" actionUrl="${sendEnableUrl}"
            logoKey="yukon.web.modules.dr.controlAreaDetail.actions.enableIcon"
            labelKey="yukon.web.modules.dr.controlAreaDetail.actions.enable"/>
    </c:when>
</c:choose>
<br>

<%-- Reset Daily Peak --%>
<c:choose>
    <c:when test="${pageScope.state == 'noAssignedPrograms'}">
        <span class="disabledAction" title="${disabledMessage}">
            <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeakIcon.disabled"/>
            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak"/>
        </span>
    </c:when>
    <c:otherwise>
        <c:choose>
            <%-- Trigger actions are only active for Control Areas with at least one trigger --%>
            <c:when test="${!empty pageScope.controlArea.triggers}">
                <cti:url var="sendResetPeakUrl" value="/spring/dr/controlArea/sendResetPeakConfirm">
                    <cti:param name="controlAreaId" value="${controlAreaId}"/>
                </cti:url>
                <tags:simpleDialogLink titleKey="yukon.web.modules.dr.controlArea.sendResetPeakConfirm.title"
                    dialogId="drDialog" actionUrl="${sendResetPeakUrl}"
                    logoKey="yukon.web.modules.dr.controlAreaDetail.actions.resetPeakIcon"
                    labelKey="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak"/>
            </c:when>
            <c:otherwise>
                <cti:msg var="resetDisabled" key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak.disabled"/>
                <span class="disabledAction" title="${resetDisabled}">
                    <cti:logo key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeakIcon.disabled"/>
                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.resetPeak"/>
                </span>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
<br>

</tags:dynamicChooseOption>
