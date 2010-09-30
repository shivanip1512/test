<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:msgScope paths="modules.capcontrol.ivvc.zoneWizard">

<table class="compactResultsTable">

<tr id="${row.type}_${row.id}">
	<td><input type="hidden" value="${row.id}" name="${row.type}Ids"/>${row.name}</td>
	<td>${row.device}</td>
	<td><cti:img key="delete" href="javascript:removeTableRow('${row.type}_${row.id}')"/></td>
</tr>
</table>

</cti:msgScope>