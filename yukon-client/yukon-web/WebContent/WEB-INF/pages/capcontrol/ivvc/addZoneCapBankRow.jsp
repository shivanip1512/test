<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<tags:standardPageFragment pageName="ivvc" module="capcontrol" fragmentName="zoneWizard">

<table class="compact-results-table">
<tr>
	<td>
        <spring:escapeBody htmlEscape="true">${row.name}</spring:escapeBody>
        <input type="hidden" value="${row.id}" name="bankAssignments[${itemIndex}].id"/>
        <input type="hidden" value="<spring:escapeBody>${row.name}</spring:escapeBody>" name="bankAssignments[${itemIndex}].name"/>
        <input type="hidden" value="<spring:escapeBody>${row.device}</spring:escapeBody>" name="bankAssignments[${itemIndex}].device"/>
        <input type="hidden" value="false" name="bankAssignments[${itemIndex}].deletion" class="isDeletionField">
    </td>
	<td><spring:escapeBody>${row.device}</spring:escapeBody></td>
	<td><input name="bankAssignments[${itemIndex}].graphPositionOffset" size="1" value="${itemIndex+1}.0"/></td>
	<td><input name="bankAssignments[${itemIndex}].distance" size="3" value="0.0"/></td>
    <tags:dynamicTableActionsCell tableId="bankTable" isFirst="true" isLast="true" skipMoveButtons="true"/>
</tr>
<tags:dynamicTableUndoRow columnSpan="5" nameKey="undoRow"/>
</table>

</tags:standardPageFragment>