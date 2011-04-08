<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<tags:standardPopup pageName="ivvc" module="capcontrol" popupName="zoneWizard">

<script type="text/javascript">

	addBankHandler = function (selectedPaoInfo) {
		var url = '/spring/capcontrol/ivvc/wizard/addCapBank';

		var index = $$('.bankRowCounter').length;
	    
		for(var i = 0; i < selectedPaoInfo.size(); i++) {
			var paoId = selectedPaoInfo[i].paoId;
			addRow(url,paoId,index++,'bank');	
		}
	    
	    return true;
	}

	addPointHandler = function (selectedPointInfo) {
		var url = '/spring/capcontrol/ivvc/wizard/addVoltagePoint';
		var index = $$('.pointRowCounter').length;
		
		for(var i = 0; i < selectedPointInfo.size(); i++) {
	    	var pointId = selectedPointInfo[i].pointId;		
	    	addRow(url,pointId,index++,'point');
		}
		
	    return true;
	}
	
	addRow = function (url,id,index,rowType) {
	    var newRow = $('defaultRow').cloneNode(true);
	    $(rowType+'TableBody').appendChild(newRow);

	    new Ajax.Request(url,{
	        parameters: {'id': id, 'index': index},
	        onSuccess: function(transport) {
	            var dummyHolder = document.createElement('div');
	            dummyHolder.innerHTML = transport.responseText;
	            var replacementRow = $(dummyHolder).getElementsBySelector('tr')[0];
	            $(rowType+'TableBody').replaceChild(replacementRow, newRow);
	        },
	        onFailure: function() { 
		        newRow.remove();
	        }
	    });
	}

	removeTableRow = function (rowType, rowId) {
		//Remove the table row
		var rowToDelete = document.createElement('input');
		rowToDelete.type = 'hidden';

		//rowType will be "bank" or "point"
		rowToDelete.name = rowType + 'sToRemove';
		rowToDelete.value = rowId;
		rowToDelete.id =  'deleteInput_' + rowId;
		$('zoneForm').appendChild(rowToDelete);
		$(rowType +'_'+ rowId).hide();
		$(rowType +'_'+ rowId + '_undo').show();
	}

	undoRemoveTableRow = function (rowType, rowId) {
		$('deleteInput_' + rowId).remove();
		$(rowType +'_'+ rowId).show();
		$(rowType +'_'+ rowId + '_undo').hide();
	}

	zoneSubmit = function() {
		submitFormViaAjax('tierContentPopup', 'zoneForm', null);
	}
</script>




<tags:setFormEditMode mode="${mode}"/>

<cti:displayForPageEditModes modes="EDIT">
	<cti:url var="action"  value="/spring/capcontrol/ivvc/wizard/updateZone"/>
</cti:displayForPageEditModes>
<cti:displayForPageEditModes modes="CREATE">
	<cti:url var="action" value="/spring/capcontrol/ivvc/wizard/createZone"/>
</cti:displayForPageEditModes>

<form:form id="zoneForm" commandName="zoneDto" action="${action}" >
	<form:hidden path="zoneId" id="zoneId"/>
	
	<span id="errorOnPage" style="display:none" class="errorIndicator">Missing fields are marked in red.</span>

	<tags:nameValueContainer2 style="border-collapse:separate;border-spacing:5px;">
		<%-- Zone Name --%>
		<tags:inputNameValue path="name" nameKey=".label.name" size="50"/>

		<%-- Regulator Selection --%>
		<tags:nameValue2 nameKey=".label.regulator">
			<form:hidden path="regulatorId" id="selectedRegulatorId"/>
			<span id="selectedRegulatorName">
				<spring:escapeBody htmlEscape="true">${regulatorName}</spring:escapeBody>
			</span>
			<tags:pickerDialog 	id="voltageRegulatorPicker" 
				type="availableVoltageRegulatorPicker" 
				destinationFieldId="selectedRegulatorId"
				extraDestinationFields="paoName:selectedRegulatorName;" 
				multiSelectMode="false"
				immediateSelectMode="true" 
				anchorStyleClass="simpleLink">
				<cti:img key="picker"/>
			</tags:pickerDialog>
		</tags:nameValue2>
		
		<%-- Substation Bus Selection --%>
		<tags:nameValue2 nameKey=".label.substationBus">
			<form:hidden path="substationBusId" id="selectedBusId"/>
			<span id="selectedBusName" class="disabledRow">
				<spring:escapeBody htmlEscape="true">${subBusName}</spring:escapeBody>
			</span>
		</tags:nameValue2>
		
		<%-- Parent Zone Selection --%>
		<cti:msg2 var="createAsParentLabel" key=".label.createAsParentZone"/>
		<cti:displayForPageEditModes modes="CREATE">
			<tags:selectNameValue items="${zones}" 
				itemLabel="name" 
				nameKey=".label.parentZone" 
				path="parentZoneId" 
				itemValue="id" 
				defaultItemLabel="${createAsParentLabel}"
				defaultItemValue=""/>
		</cti:displayForPageEditModes>
		<cti:displayForPageEditModes modes="EDIT,VIEW">
			<tags:nameValue2 nameKey=".label.parentZone">
				<form:hidden path="parentZoneId" id="parentZoneId"/>
				<span id="parentZoneName" class="disabledRow">
					<spring:escapeBody htmlEscape="true">${parentZoneName}</spring:escapeBody>
				</span>
			</tags:nameValue2>
		</cti:displayForPageEditModes>
		
		<%-- Graph Start Position --%>
		<tags:inputNameValue path="graphStartPosition" nameKey=".label.graphStartPosition" size="2"/>
	</tags:nameValueContainer2>
	
	<cti:dataGrid tableStyle="width:100%;" cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:20px;width:30%">
		<table style="display:none">
			<tr id="defaultRow">
				<td colspan="3" style="text-align: center"><img src="/WebConfig/yukon/Icons/indicator_arrows.gif"></td>
			</tr>
		</table>
		<cti:dataGridCell>
			<tags:boxContainer2 nameKey="assignedVoltageDevice" hideEnabled="false" showInitially="true">
				<div style="overflow: auto;max-height: 150px;">
					<table id="bankTable" class="compactResultsTable">
						<thead>
							<tr>
								<th><i:inline key=".table.bank.name"/></th>
								<th><i:inline key=".table.bank.device"/></th>
								<th><i:inline key=".table.position"/></th>
								<th><i:inline key=".table.distance"/></th>
								<th class="removeColumn"><i:inline key=".table.remove"/></th>
							</tr>
						</thead>
						<tbody id="bankTableBody">
							<c:forEach var="row" varStatus="status" items="${zoneDto.bankAssignments}">
								<tr id="${row.type}_${row.id}" class="bankRowCounter">
									<td>
										<form:hidden path="bankAssignments[${status.index}].id" id="bankAssignments[${status.index}].id"/>
										<spring:escapeBody htmlEscape="true">${row.name}</spring:escapeBody>
									</td>
									<td>
										<spring:escapeBody htmlEscape="true">${row.device}</spring:escapeBody>
									</td>
									<td>
										<tags:input path="bankAssignments[${status.index}].graphPositionOffset" size="1"/>
									</td>
									<td>
										<tags:input path="bankAssignments[${status.index}].distance" size="3"/>
									</td>
									<td class="removeColumn" >
										<cti:img key="delete" href="javascript:removeTableRow('${row.type}','${row.id}')"/>
									</td>
								</tr>
								<tr style="display: none" id="${row.type}_${row.id}_undo" class="undoRow">
									<td colspan="4" align="center">
										${row.name} will be removed
									</td>
									<td colspan="1" align="center">
										<a href="javascript:undoRemoveTableRow('${row.type}','${row.id}')">Undo</a>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="actionArea">
					<tags:pickerDialog 	id="bankPicker" 
						type="availableCapBankPicker"
						multiSelectMode="true"
						endAction="addBankHandler"
						linkType="button" nameKey="add"
                        extraArgs="${zoneDto.substationBusId}"/>
				</div>
			</tags:boxContainer2>
		</cti:dataGridCell>
		<br>
		<cti:dataGridCell>	
			<tags:boxContainer2 nameKey="assignedVoltagePoint" hideEnabled="false" showInitially="true">
				<div style="overflow: auto;max-height: 150px;">
					<table id="pointTable" class="compactResultsTable">
						<thead>
							<tr>
								<th><i:inline key=".table.point.name"/></th>
								<th><i:inline key=".table.point.device"/></th>
								<th><i:inline key=".table.position"/></th>
								<th><i:inline key=".table.distance"/></th>
								<th class="removeColumn"><i:inline key=".table.remove"/></th>
							</tr>
						</thead>
						<tbody id="pointTableBody">
							<c:forEach var="row" varStatus="status" items="${zoneDto.pointAssignments}">
								<tr id="${row.type}_${row.id}" class="pointRowCounter">
									<td>
										<form:hidden path="pointAssignments[${status.index}].id" id="pointAssignments[${status.index}].id"/>
										<spring:escapeBody htmlEscape="true">${row.name}</spring:escapeBody>
									</td>
									<td>
										<spring:escapeBody htmlEscape="true">${row.device}</spring:escapeBody>
									</td>
									<td>
										<tags:input path="pointAssignments[${status.index}].graphPositionOffset" size="1"/>
									</td>
									<td>
										<tags:input path="pointAssignments[${status.index}].distance" size="3"/>
									</td>
									<td class="removeColumn">
										<cti:img key="delete" href="javascript:removeTableRow('${row.type}','${row.id}')"/>
									</td>
								</tr>
								<tr style="display: none" id="${row.type}_${row.id}_undo" class="undoRow">
									<td colspan="4" align="center">
										${row.name} will be removed
									</td>
									<td colspan="1" align="center">
										<a href="javascript:undoRemoveTableRow('${row.type}','${row.id}')">Undo</a>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="actionArea">
					<tags:pickerDialog 	id="pointPicker" 
						type="voltPointPicker"
						multiSelectMode="true"
						endAction="addPointHandler"
						linkType="button" nameKey="add"/>
				</div>
			</tags:boxContainer2>
		</cti:dataGridCell>
	</cti:dataGrid>
	
	<div class="pageActionArea">
		<cti:displayForPageEditModes modes="EDIT">
			<cti:msg2 var="submitButtonText" key=".label.updateButton"/>
		</cti:displayForPageEditModes>
		<cti:displayForPageEditModes modes="CREATE">
			<cti:msg2 var="submitButtonText" key=".label.createButton"/>
		</cti:displayForPageEditModes>
		<input type="button" value="${submitButtonText}" onclick="zoneSubmit();">
	</div>

    
</form:form>

</tags:standardPopup>
