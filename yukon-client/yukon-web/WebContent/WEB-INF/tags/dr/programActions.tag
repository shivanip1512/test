<%@ attribute name="program" required="true" type="com.cannontech.common.pao.DisplayablePao" rtexprvalue="true" %>
<%@ attribute name="state" required="true" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:dynamicChooseOption optionId="${pageScope.state}">

<c:set var="programId" value="${pageScope.program.paoIdentifier.paoId}"/>

<%-- Start --%>
<c:choose>
    <c:when test="${pageScope.state == 'unknown'}">
        <cti:msg var="programUnknown" key="yukon.web.modules.dr.programDetail.unknown"/>
        <span class="disabledAction" title="${programUnknown}">
            <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon.disabled"/>
            <cti:msg key="yukon.web.modules.dr.programDetail.actions.start"/>
        </span>
    </c:when>
    <c:when test="${pageScope.state == 'runningEnabled' || pageScope.state == 'runningDisabled'}">
        <cti:msg var="alreadyRunning" key="yukon.web.modules.dr.programDetail.alreadyRunning"/>
        <span class="disabledAction" title="${alreadyRunning}">
            <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon.disabled"/>
            <cti:msg key="yukon.web.modules.dr.programDetail.actions.start"/>
        </span>
    </c:when>
    <c:otherwise>
        <cti:url var="startProgramUrl" value="/spring/dr/program/start/details">
            <cti:param name="programId" value="${programId}"/>
        </cti:url>
        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startProgram.title" 
            dialogId="drDialog" actionUrl="${startProgramUrl}" 
            logoKey="yukon.web.modules.dr.programDetail.actions.startIcon"
            labelKey="yukon.web.modules.dr.programDetail.actions.start"/>
    </c:otherwise>
</c:choose>
<br>

<%-- Stop --%>
<c:choose>
    <c:when test="${pageScope.state == 'unknown'}">
        <span class="disabledAction" title="${programUnknown}">
            <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon.disabled"/>
            <cti:msg key="yukon.web.modules.dr.programDetail.actions.stop"/>
        </span>
    </c:when>
    <c:when test="${pageScope.state == 'enabled' || pageScope.state == 'disabled'}">
        <cti:msg var="notRunning" key="yukon.web.modules.dr.programDetail.notRunning"/>
        <span class="disabledAction" title="${notRunning}">
            <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon.disabled"/>
            <cti:msg key="yukon.web.modules.dr.programDetail.actions.stop"/>
        </span>
    </c:when>
    <c:otherwise>
        <cti:url var="stopProgramUrl" value="/spring/dr/program/stop/details">
            <cti:param name="programId" value="${programId}"/>
        </cti:url>
        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopProgram.title" 
            dialogId="drDialog" actionUrl="${stopProgramUrl}" 
            logoKey="yukon.web.modules.dr.programDetail.actions.stopIcon"
            labelKey="yukon.web.modules.dr.programDetail.actions.stop"/>
    </c:otherwise>
</c:choose>
<br>

<%-- Change Gears --%>
<c:choose>
    <c:when test="${pageScope.state == 'unknown'}">
        <span class="disabledAction" title="${programUnknown}">
            <cti:logo key="yukon.web.modules.dr.programDetail.actions.changeGearsIcon.disabled"/>
            <cti:msg key="yukon.web.modules.dr.programDetail.actions.changeGears"/>
        </span>
    </c:when>
    <c:when test="${pageScope.state == 'runningEnabled' || pageScope.state == 'runningDisabled'}">
        <cti:url var="changeGearsUrl" value="/spring/dr/program/getChangeGearValue">
            <cti:param name="programId" value="${programId}"/>
        </cti:url>
        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.getChangeGearValue.title" 
            dialogId="drDialog" actionUrl="${changeGearsUrl}" 
            logoKey="yukon.web.modules.dr.programDetail.actions.changeGearsIcon"
            labelKey="yukon.web.modules.dr.programDetail.actions.changeGears"/>
    </c:when>
    <c:otherwise>
        <cti:msg var="changeGearsDisabled" key="yukon.web.modules.dr.programDetail.actions.changeGears.disabled"/>
        <span id="changeGearDisabled" title="${changeGearsDisabled}">
            <cti:logo key="yukon.web.modules.dr.programDetail.actions.changeGearsIcon.disabled"/>
            <cti:msg key="yukon.web.modules.dr.programDetail.actions.changeGears"/>
        </span>
    </c:otherwise>
</c:choose>
<br>

<%-- Enable/Disable --%>
<c:choose>
    <c:when test="${pageScope.state == 'unknown'}">
        <span class="disabledAction" title="${programUnknown}">
            <cti:logo key="yukon.web.modules.dr.programDetail.actions.disableIcon.disabled"/>
            <cti:msg key="yukon.web.modules.dr.programDetail.actions.disable"/>
        </span>
    </c:when>
    <c:when test="${pageScope.state == 'enabled' || pageScope.state == 'scheduledEnabled' || pageScope.state == 'runningEnabled'}">
        <cti:url var="sendDisableUrl" value="/spring/dr/program/sendEnableConfirm">
            <cti:param name="programId" value="${programId}"/>
            <cti:param name="isEnabled" value="false"/>
        </cti:url>
        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendDisableConfirm.title" 
            dialogId="drDialog" actionUrl="${sendDisableUrl}" 
            logoKey="yukon.web.modules.dr.programDetail.actions.disableIcon"
            labelKey="yukon.web.modules.dr.programDetail.actions.disable"/>
    </c:when>
    <c:when test="${pageScope.state == 'disabled' || pageScope.state == 'scheduledDisabled' || pageScope.state == 'runningDisabled'}">
        <cti:url var="sendEnableUrl" value="/spring/dr/program/sendEnableConfirm">
            <cti:param name="programId" value="${programId}"/>
            <cti:param name="isEnabled" value="true"/>
        </cti:url>
        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.sendEnableConfirm.title" 
            dialogId="drDialog" actionUrl="${sendEnableUrl}" 
            logoKey="yukon.web.modules.dr.programDetail.actions.enableIcon"
            labelKey="yukon.web.modules.dr.programDetail.actions.enable"/>
    </c:when>
</c:choose>
<br>

</tags:dynamicChooseOption>
