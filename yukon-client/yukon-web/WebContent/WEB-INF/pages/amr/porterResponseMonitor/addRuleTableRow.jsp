<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<tags:standardPageFragment module="amr"	pageName="porterResponseMonitor" fragmentName="addRule">

	<table class="compactResultsTable">

		<tr id="new_${newRowId}" class="ruleCounter">
			<td><input name="rules[${index}].ruleOrder" size="1" value="${order}" /></td>
			<td><input type="checkbox" name="rules[${index}].success" /></td>
			<td><input type="text" size="5" value="${defaultError}" name="monitorCodes" /></td>
			<td><select name="rules[${index}].matchStyle">
				<c:forEach items="${matchStyleChoices}" var="style">
					<option value="${style}"><i:inline key="${style.formatKey}" /></option>
				</c:forEach>
			</select></td>
			<td><select name="rules[${index}].state">
				<c:forEach items="${statesList}" var="state">
					<option value="${state.liteID}">${state.stateText}</option>
				</c:forEach>
			</select></td>
			<td class="removeColumn"><cti:img key="delete" href="javascript:removeTableRow('new_${newRowId}')" /></td>
		</tr>
	</table>

</tags:standardPageFragment>