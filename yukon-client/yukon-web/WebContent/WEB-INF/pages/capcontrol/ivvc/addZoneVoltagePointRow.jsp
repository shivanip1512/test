<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<tags:standardPageFragment pageName="ivvc" module="capcontrol" fragmentName="zoneWizard">

<cti:msg2 var="noneSelected" key="yukon.web.components.button.selectionPicker.label"/>
<table class="compact-results-table">
<tr>
    <td>
        ${fn:escapeXml(row.name)}
        <input type="hidden" value="${row.id}" name="pointAssignments[${itemIndex}].id"/>
        <input type="hidden" value="<spring:escapeBody>${row.name}</spring:escapeBody>" name="pointAssignments[${itemIndex}].name"/>
        <input type="hidden" value="<spring:escapeBody>${row.device}</spring:escapeBody>" name="pointAssignments[${itemIndex}].device"/>
        <input type="hidden" value="false" name="pointAssignments[${itemIndex}].deletion" class="isDeletionField">
    </td>
    <td>${fn:escapeXml(row.device)}</td>
    <td>
        <select name="pointAssignments[${itemIndex}].feederId" >
            <option value>${noneSelected}</option>
            <c:forEach var="feeder" items="${feederList}">
                <option value="${feeder.ccId}">
                    ${feeder.ccName}
                </option>
            </c:forEach>
        </select>
    </td>
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
	<td><input type="text" name="pointAssignments[${itemIndex}].graphPositionOffset" size="3" value="${itemIndex+1}.0"/></td>
	<td><input type="text" name="pointAssignments[${itemIndex}].distance" size="4" value="0.0"/></td>
    <td><input type="checkbox" name="pointAssignments[${itemIndex}].ignore"/></td>
    <tags:dynamicTableActionsCell tableId="pointTable" isFirst="true" isLast="true" skipMoveButtons="true"/>
</tr>
<tags:dynamicTableUndoRow columnSpan="6" nameKey="undoRow"/>
</table>

</tags:standardPageFragment>