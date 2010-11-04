<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:msgScope paths="modules.capcontrol.ivvc.zoneWizard">

<table class="compactResultsTable">

<tr id="${row.type}_${row.id}">
	<td><input type="hidden" value="${row.id}" name="${row.type}Assignments[${param.index}].id"/>${row.name}</td>
	<td>${row.device}</td>
	<td><input name="${row.type}Assignments[${param.index}].graphPositionOffset" size="1" value="${param.index+1}"/></td>
	<td><input name="${row.type}Assignments[${param.index}].distance" size="3" value="0"/></td>
	<td class="removeColumn"><cti:img key="delete" href="javascript:removeTableRow('${row.type}_${row.id}')"/></td>
</tr>
</table>

</cti:msgScope>