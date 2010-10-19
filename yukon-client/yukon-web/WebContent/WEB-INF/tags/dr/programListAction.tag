<%@ attribute name="program" required="true" type="com.cannontech.common.pao.DisplayablePao" rtexprvalue="true" %>
<%@ attribute name="state" required="true" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:dynamicChooseOption optionId="${pageScope.state}">

<c:set var="programId" value="${pageScope.program.paoIdentifier.paoId}"/>

<cti:msg var="programUnknown" key="yukon.web.modules.dr.programDetail.unknown"/>
<cti:msg var="notRunning" key="yukon.web.modules.dr.programDetail.notRunning"/>
<cti:msg var="alreadyRunning" key="yukon.web.modules.dr.programDetail.alreadyRunning"/>

<%-- Start --%>
<c:choose>
    <c:when test="${pageScope.state == 'unknown'}">
        <span class="subtleGray" title="${programUnknown}">
            <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon.disabled"/>
        </span>
    </c:when>
    <c:when test="${pageScope.state == 'runningEnabled' || pageScope.state == 'runningDisabled'}">
        <span class="subtleGray" title="${alreadyRunning}">
            <cti:logo key="yukon.web.modules.dr.programDetail.actions.startIcon.disabled"/>
        </span>
    </c:when>
    <c:otherwise>
        <cti:url var="startProgramUrl" value="/spring/dr/program/start/details">
            <cti:param name="programId" value="${programId}"/>
        </cti:url>
        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.startProgram.title" 
            dialogId="drDialog" actionUrl="${startProgramUrl}" 
            logoKey="yukon.web.modules.dr.programDetail.actions.startIcon"/>
    </c:otherwise>
</c:choose>

<%-- Stop --%>
<c:choose>
    <c:when test="${pageScope.state == 'unknown'}">
        <span class="subtleGray" title="${programUnknown}">
            <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon.disabled"/>
        </span>
    </c:when>
    <c:when test="${pageScope.state == 'enabled' || pageScope.state == 'disabled'}">
        <cti:url var="stopProgramUrl" value="/spring/dr/program/stop/details">
            <cti:param name="programId" value="${programId}"/>
        </cti:url>
        <span title="${notRunning}">
            <cti:logo key="yukon.web.modules.dr.programDetail.actions.stopIcon.disabled"/>
        </span>
    </c:when>
    <c:otherwise>
        <cti:url var="stopProgramUrl" value="/spring/dr/program/stop/details">
            <cti:param name="programId" value="${programId}"/>
        </cti:url>
        <tags:simpleDialogLink titleKey="yukon.web.modules.dr.program.stopProgram.title" 
            dialogId="drDialog" actionUrl="${stopProgramUrl}" 
            logoKey="yukon.web.modules.dr.programDetail.actions.stopIcon"/>
    </c:otherwise>
</c:choose>

</tags:dynamicChooseOption>
