<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<tags:standardPageFragment pageName="ivvc" module="capcontrol" fragmentName="zoneWizard">

<table class="compactResultsTable">

<tr id="${row.type}_${row.id}" class="${row.type}RowCounter">
	<td>
        <spring:escapeBody>${row.name}</spring:escapeBody>
        <input type="hidden" value="${row.id}" name="${row.type}Assignments[${index}].id"/>
        <input type="hidden" value="<spring:escapeBody>${row.name}</spring:escapeBody>" name="${row.type}Assignments[${index}].name"/>
        <input type="hidden" value="<spring:escapeBody>${row.device}</spring:escapeBody>" name="${row.type}Assignments[${index}].device"/>
    </td>
	<td><spring:escapeBody>${row.device}</spring:escapeBody></td>
	<td><input name="${row.type}Assignments[${index}].graphPositionOffset" size="1" value="${index+1}"/></td>
	<td><input name="${row.type}Assignments[${index}].distance" size="3" value="0"/></td>
	<td class="removeColumn"><cti:img key="delete" href="javascript:removeTableRow('${row.type}', '${row.id}')"/></td>
</tr>
</table>

</tags:standardPageFragment>