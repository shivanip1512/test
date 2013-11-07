<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>


<cti:msgScope paths="modules.adminSetup.applianceCategory.unassignedProgram">

<cti:url var="submitUrl" value="unassignProgram">
    <cti:param name="ecId" value="${ecId}"/>
</cti:url>
<form id="unassignProgramForm_${assignedProgram.assignedProgramId}" action="${submitUrl}"
    onsubmit="return submitFormViaAjax('acDialog', 'unassignProgramForm_${assignedProgram.assignedProgramId}')">
    <input type="hidden" name="applianceCategoryId" value="${applianceCategory.applianceCategoryId}"/>
    <input type="hidden" name="assignedProgramId" value="${assignedProgram.assignedProgramId}"/>

    <p><cti:msg2 key="${confirmationQuestion}"/></p>

    <div class="action-area">
        <cti:button type="submit" nameKey="ok" classes="primary action"/>
        <cti:button nameKey="cancel" onclick="jQuery('#acDialog').dialog('close');"/>
    </div>
</form>

</cti:msgScope>
