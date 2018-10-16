<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.operator.enrollmentList">

<cti:url var="submitUrl" value="/stars/operator/enrollment/save"/>
<form:form id="save-enroll-form" modelAttribute="programEnrollment" action="${submitUrl}">
    <cti:csrfToken/>
    <input type="hidden" name="isAdd" value="${isAdd}"/>
    <input type="hidden" name="accountId" value="${accountId}"/>
    <input type="hidden" name="assignedProgramId" value="${param.assignedProgramId}"/>
    <form:hidden path="loadGroupId"/>
    <c:forEach var="item" varStatus="status" items="${programEnrollment.inventoryEnrollments}">
        <form:hidden path="inventoryEnrollments[${status.index}].inventoryId"/>
        <form:hidden path="inventoryEnrollments[${status.index}].enrolled"/>
        <form:hidden path="inventoryEnrollments[${status.index}].relay"/>
        <form:hidden path="inventoryEnrollments[${status.index}].loadGroupId"/>
    </c:forEach>

    <div>
        <c:if test="${isAdd}">
            <i:inline key=".confirmEnroll" arguments="${assignedProgram.name.displayName}"/>
        </c:if>
        <c:if test="${!isAdd}">
            <i:inline key=".confirmUpdate" arguments="${assignedProgram.name.displayName}"/>
        </c:if>
    </div>

    <c:if test="${fn:length(conflictingPrograms) > 0}">
        <%-- Normally, this list will never have more than one item. --%>
        <c:if test="${fn:length(conflictingPrograms) == 1}">
            <p><i:inline key=".willUnenroll" arguments="${conflictingPrograms[0].displayName}"/></p>
        </c:if>
        <%-- This can happen if multiple programs per appliance category is allowed,
             the consumer is enrolled in more than one for a given category and
             then multiple programs per category are disallowed. --%>
        <c:if test="${fn:length(conflictingPrograms) > 1}">
            <p><i:inline key=".willUnenrollMultiple"/></p>
            <ul>
                <c:forEach var="conflictingProgram" items="${conflictingPrograms}">
                    <li>${fn:escapeXml(conflictingProgram.displayName)}</li>
                </c:forEach>
            </ul>
        </c:if>
    </c:if>
    
</form:form>

</cti:msgScope>