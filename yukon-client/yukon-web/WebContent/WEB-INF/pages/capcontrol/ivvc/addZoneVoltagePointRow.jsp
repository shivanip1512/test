<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<tags:standardPageFragment pageName="ivvc" module="capcontrol" fragmentName="zoneWizard">

<table class="compact-results-table">
<tr>
	<td>
        <spring:escapeBody>${row.name}</spring:escapeBody>
        <input type="hidden" value="${row.id}" name="pointAssignments[${itemIndex}].id"/>
        <input type="hidden" value="<spring:escapeBody>${row.name}</spring:escapeBody>" name="pointAssignments[${itemIndex}].name"/>
        <input type="hidden" value="<spring:escapeBody>${row.device}</spring:escapeBody>" name="pointAssignments[${itemIndex}].device"/>
        <input type="hidden" value="false" name="pointAssignments[${itemIndex}].deletion" class="isDeletionField">
    </td>
	<td><spring:escapeBody>${row.device}</spring:escapeBody></td>
    <td>
        <c:choose>
            <c:when test="${phaseUneditable}">
                <input type="hidden" name="pointAssignments[${itemIndex}].phase" value="${phases[0]}"/>
                <i:inline key="${phases[0]}" />
            </c:when>
            <c:otherwise>
                <select name="pointAssignments[${itemIndex}].phase">
                    <c:forEach var="phase" items="${phases}">
                        <option value="${phase}">
                            <i:inline key="${phase}" />
                        </option>
                    </c:forEach>
                </select>
            </c:otherwise>
        </c:choose>
    </td>
	<td><input name="pointAssignments[${itemIndex}].graphPositionOffset" size="1" value="${itemIndex+1}.0"/></td>
	<td><input name="pointAssignments[${itemIndex}].distance" size="3" value="0.0"/></td>
    <tags:dynamicTableActionsCell tableId="pointTable" isFirst="true" isLast="true" skipMoveButtons="true"/>
</tr>
<tags:dynamicTableUndoRow columnSpan="6" nameKey="undoRow"/>
</table>

</tags:standardPageFragment>