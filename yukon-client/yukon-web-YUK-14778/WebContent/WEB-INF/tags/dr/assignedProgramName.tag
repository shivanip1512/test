<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="assignedProgram" required="true" type="com.cannontech.stars.dr.appliance.model.AssignedProgram" %>

<cti:msgScope paths="modules.adminSetup.applianceCategory.PROGRAMS">
<c:if test="${pageScope.assignedProgram.programId == 0}">
    <i:inline key=".virtualProgramName" arguments="${pageScope.assignedProgram.displayName}"/>
</c:if>
<c:if test="${pageScope.assignedProgram.programId != 0}">
    ${fn:escapeXml(pageScope.assignedProgram.programName)}
    <c:if test="${not empty pageScope.assignedProgram.displayName 
            && pageScope.assignedProgram.displayName != pageScope.assignedProgram.programName}">
        (${fn:escapeXml(pageScope.assignedProgram.displayName)})
    </c:if>
</c:if>
</cti:msgScope>