<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.operator.enrollmentList">

<cti:url var="submitUrl" value="/stars/operator/enrollment/unenroll"/>
<form action="${submitUrl}">
    <input type="hidden" name="accountId" value="${param.accountId}"/>
    <input type="hidden" name="assignedProgramId" value="${param.assignedProgramId}"/>
    <p class="stacked"><i:inline key=".confirmUnenroll" arguments="${assignedProgram.name.displayName}"/></p>
</form>

</cti:msgScope>