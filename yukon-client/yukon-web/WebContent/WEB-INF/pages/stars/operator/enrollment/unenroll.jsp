<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.operator.enrollmentList">

<cti:url var="submitUrl" value="/stars/operator/enrollment/unenroll"/>
<form id="inputForm" action="${submitUrl}" onsubmit="return submitFormViaAjax('peDialog', 'inputForm')">
    <input type="hidden" name="accountId" value="${param.accountId}"/>
    <input type="hidden" name="assignedProgramId" value="${param.assignedProgramId}"/>

    <p class="stacked"><i:inline key=".confirmUnenroll" arguments="${assignedProgram.name.displayName}"/></p>

    <div class="actionArea">
        <cti:button nameKey="ok" classes="primary action" type="submit"/>
        <cti:button nameKey="cancel" onclick="jQuery('#peDialog').dialog('close');"/>
    </div>
</form>

</cti:msgScope>