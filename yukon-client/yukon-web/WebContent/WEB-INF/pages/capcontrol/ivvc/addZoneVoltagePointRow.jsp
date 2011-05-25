<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<tags:standardPageFragment pageName="ivvc" module="capcontrol" fragmentName="zoneWizard">

<table class="compactResultsTable">

<tr id="${row.type}_${row.id}" class="${row.type}RowCounter">
	<td><input type="hidden" value="${row.id}" name="${row.type}Assignments[${index}].id"/>${row.name}</td>
	<td>${row.device}</td>
    <td>
        <select name="${row.type}Assignments[${index}].phase">
            <c:forEach var="phase" items="${phases}">
                <option value="${phase}">
                    <i:inline key="${phase.formatKey}" />
                </option>
            </c:forEach>
        </select>
    </td>
	<td><input name="${row.type}Assignments[${index}].graphPositionOffset" size="1" value="${index+1}"/></td>
	<td><input name="${row.type}Assignments[${index}].distance" size="3" value="0"/></td>
	<td class="removeColumn"><cti:img key="delete" href="javascript:removeTableRow('${row.type}', '${row.id}')"/></td>
</tr>
</table>

</tags:standardPageFragment>