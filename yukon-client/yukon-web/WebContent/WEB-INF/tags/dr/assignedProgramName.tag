<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ tag body-content="empty" %>
<%@ attribute name="assignedProgram" required="true" type="com.cannontech.stars.dr.appliance.model.AssignedProgram" rtexprvalue="true" %>

<c:if test="${pageScope.assignedProgram.programId == 0}">
    <i:inline key=".virtualProgramName" arguments="${pageScope.assignedProgram.displayName}"/>
</c:if>
<c:if test="${pageScope.assignedProgram.programId != 0}">
    <spring:escapeBody htmlEscape="true">${pageScope.assignedProgram.programName}</spring:escapeBody>
    <c:if test="${!empty pageScope.assignedProgram.displayName && pageScope.assignedProgram.displayName != pageScope.assignedProgram.programName}">
        (<spring:escapeBody htmlEscape="true">${pageScope.assignedProgram.displayName}</spring:escapeBody>)
    </c:if>
</c:if>
