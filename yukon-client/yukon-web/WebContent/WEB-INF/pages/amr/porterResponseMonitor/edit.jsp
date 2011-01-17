<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="porterResponseMonitorEdit">

	<cti:standardMenu menuSelection="meters" />

	<cti:includeScript link="/JavaScript/jquery.js" />

	<cti:breadCrumbs>
		<cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
		<cti:crumbLink url="/spring/meter/start" title="Metering" />
		<cti:crumbLink><i:inline key=".title" /></cti:crumbLink>
	</cti:breadCrumbs>

	<h2><i:inline key=".title" /></h2>
	<br>

<script type="text/javascript">

	addTableRow = function () {

		var url = '/spring/amr/porterResponseMonitor/addRule';
		var index = $$('.ruleCounter').length;
	    var newRow = $('defaultRow').cloneNode(true);
	    $('rulesTableBody').appendChild(newRow);

	    new Ajax.Request(url,{
	        parameters: {'index': index++},
	        onSuccess: function(transport) {
	            var dummyHolder = document.createElement('div');
	            dummyHolder.innerHTML = transport.responseText;
	            var replacementRow = $(dummyHolder).getElementsBySelector('tr')[0];
	            $('rulesTableBody').replaceChild(replacementRow, newRow);
	        },
	        onFailure: function() { 
		        newRow.remove();
	        }
	    });
	}

	removeTableRow = function (rowId) {
		//If this is a new row then just remove it
		//Else hide it and show the undo row
		if (rowId.indexOf('new_') == 0) {
			//jquery implementation
			//j$('#'+rowId).fadeOut(400, function () {
			//	$(rowId).remove();
			//});
			$(rowId).remove();
		} else {
			//Remove the table row
			var rowToDelete = document.createElement('input');
			rowToDelete.type = 'hidden';

			//rowType will be "bank" or "point"
			rowToDelete.name = 'rulesToRemove';
			rowToDelete.value = rowId;
			rowToDelete.id =  'deleteInput_' + rowId;
			$('updateForm').appendChild(rowToDelete);

			//jquery implementation
			//j$('#'+rowId).fadeOut(400, function () {
			//	j$('#'+rowId + '_undo').fadeIn(400);
			//});

			$(rowId).hide();
			$(rowId + '_undo').show();
		}
	}

	undoRemoveTableRow = function (rowId) {
		$('deleteInput_' + rowId).remove();

		//jquery implementation
		//j$('#'+rowId + '_undo').fadeOut(400, function () {
		//	j$('#'+rowId).fadeIn(400);
		//});

		$(rowId + '_undo').hide();
		$(rowId).show();
	}
</script>

	<%-- MISC FORMS --%>
	<form id="monitorDeleteForm" action="/spring/amr/porterResponseMonitor/delete" method="post">
		<input type="hidden" name="monitorId" value="${monitor.monitorId}">
	</form>

	<form id="toggleEnabledForm" action="/spring/amr/porterResponseMonitor/toggleEnabled" method="post">
		<input type="hidden" name="monitorId" value="${monitor.monitorId}">
	</form>

	<i:simplePopup styleClass="mediumSimplePopup" titleKey=".delete.title" id="deleteConfirmDialog">
		<h1 class="dialogQuestion"><i:inline key=".deleteConfirm" arguments="${monitor.name}" /></h1>

		<div class="actionArea">
			<tags:slowInput2 formId="monitorDeleteForm" key="ok" />
			<input type="button" value="<i:inline key=".cancel"/>" 
				onclick="$('deleteConfirmDialog').hide()" class="formSubmit">
		</div>
	</i:simplePopup>

	<form:form id="updateForm" commandName="monitor"
		action="/spring/amr/porterResponseMonitor/update" method="post">

		<form:hidden path="monitorId" />
		<form:hidden path="evaluatorStatus" />

		<tags:formElementContainer nameKey="sectionHeader">
			<tags:nameValueContainer2 style="border-collapse:separate;border-spacing:5px;">

				<%-- name --%>
				<tags:inputNameValue nameKey=".name" path="name" size="50" maxlength="50" />

				<%-- enable/disable monitoring --%>
				<tags:nameValue2 nameKey=".monitoring">
					<i:inline key="${monitor.evaluatorStatus}" />
				</tags:nameValue2>

			</tags:nameValueContainer2>
		</tags:formElementContainer>

		<cti:dataGrid tableStyle="width:100%;" cols="2" rowStyle="vertical-align:top;"
			cellStyle="padding-right:20px;width:50%">

			<table style="display: none">
				<tr id="defaultRow">
					<td colspan="6" style="text-align: center">
						<img src="/WebConfig/yukon/Icons/indicator_arrows.gif">
					</td>
				</tr>
			</table>

			<cti:dataGridCell>
				<tags:boxContainer2 nameKey="rulesTable" hideEnabled="false" showInitially="true">
					<div style="overflow: auto; max-height: 150px;" class="dialogScrollArea">
					<table id="rulesTable" class="compactResultsTable">
						<thead>
							<tr>
								<th><i:inline key=".rulesTable.header.ruleOrder" /></th>
								<th><i:inline key=".rulesTable.header.success" /></th>
								<th><i:inline key=".rulesTable.header.errors" /></th>
								<th><i:inline key=".rulesTable.header.matchStyle" /></th>
								<th><i:inline key=".rulesTable.header.action" /></th>
								<th class="removeColumn"><i:inline key=".rulesTable.header.remove" /></th>
							</tr>
						</thead>
						<tbody id="rulesTableBody">
							<c:forEach var="rule" varStatus="status" items="${monitor.rules}">
								<c:set var="rowId" value="${rule.ruleId}" />
								<c:set var="newRow" value="false" />
								<c:if test="${empty rowId}">
									<c:set var="rowId" value="new_${status.index}" />
									<c:set var="newRow" value="true" />
								</c:if>
								<tr id="${rowId}" class="ruleCounter">
									<td>
										<form:hidden path="rules[${status.index}].ruleId" id="rules[${status.index}].ruleId" />
										<form:input path="rules[${status.index}].ruleOrder" size="1" />
									</td>
									<td><form:checkbox path="rules[${status.index}].success" /></td>
									<td nowrap="nowrap">
										<input type="text" size="5" value="${errorCodesMap[rule.ruleId]}" name="monitorCodes" />
									</td>
									<td>
										<form:select id="matchStyleSelect" path="rules[${status.index}].matchStyle">
											<c:forEach var="style" items="${matchStyleChoices}">
												<form:option value="${style}">
													<i:inline key="${style.formatKey}" />
												</form:option>
											</c:forEach>
										</form:select>
									</td>
									<td>
										<form:select id="actionSelect" path="rules[${status.index}].action">
											<c:forEach var="action" items="${actionChoices}">
												<form:option value="${action}">
													<i:inline key="${action.formatKey}" />
												</form:option>
											</c:forEach>
										</form:select>
									</td>
									<td class="removeColumn">
										<cti:img key="rulesTable.delete" href="javascript:removeTableRow('${rowId}')" />
									</td>
								</tr>
								<c:if test="${newRow == false}">
									<tr style="display: none" id="${rule.ruleId}_undo" class="removed">
										<td colspan="5" align="center">This rule will be removed.</td>
										<td colspan="1" align="center">
											<a href="javascript:undoRemoveTableRow('${rule.ruleId}')">Undo</a>
										</td>
									</tr>
								</c:if>
							</c:forEach>
						</tbody>
					</table>
					</div>
					<br>
					<span style="float: right;">
						<cti:button key="rulesTable.add" onclick="addTableRow()" />
					</span>
				</tags:boxContainer2>
			</cti:dataGridCell>
			<br>
		</cti:dataGrid>

		<%-- create / update / delete --%>
		<div class="pageActionArea">
			<tags:slowInput2 formId="updateForm" key="update" />
			<c:choose>
				<c:when test="${monitor.evaluatorStatus eq 'ENABLED'}">
					<tags:slowInput2 formId="toggleEnabledForm" key="monitoringDisable" />
				</c:when>
				<c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
					<tags:slowInput2 formId="toggleEnabledForm" key="monitoringEnable" />
				</c:when>
			</c:choose>
			<input type="button" onclick="$('deleteConfirmDialog').show()"
				value="<i:inline key=".delete"/>" class="formSubmit">
			<input type="submit" name="cancel" class="formSubmit" value="<i:inline key=".cancel"/>">
		</div>
	</form:form>

</cti:standardPage>
