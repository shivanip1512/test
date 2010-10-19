<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<script type="text/javascript">

	cleanupExistingRows = function (type) {
		var existingRows = $(type+'TableBody').getElementsBySelector('tr');
		for (var i = 0; i < existingRows.size(); i++) {
			existingRows[i].remove();
		}
		return true;
	}

	addBankHandler = function (selectedPaoInfo) {
		var url = '/spring/capcontrol/ivvc/wizard/addCapBank';

		cleanupExistingRows('bank');
	    
		for(var i = 0; i < selectedPaoInfo.size(); i++) {
			var paoId = selectedPaoInfo[i].paoId;
			addRow(url,paoId,'bank');	
		}
	    
	    return true;
	}

	addPointHandler = function (selectedPointInfo) {
		var url = '/spring/capcontrol/ivvc/wizard/addVoltagePoint';

		cleanupExistingRows('point');
		
		for(var i = 0; i < selectedPointInfo.size(); i++) {
	    	var pointId = selectedPointInfo[i].pointId;		
	    	addRow(url,pointId,'point');
		}
		
	    return true;
	}
	
	addRow = function (url,id,rowType) {
	    var newRow = $('defaultRow').cloneNode(true);
	    $(rowType+'TableBody').appendChild(newRow);

	    new Ajax.Request(url,{
	        parameters: {'id': id},
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

	removeTableRow = function (rowId) {
		var rowType = rowId.split('_')[0];
		var inputValue = rowId.split('_')[1];

		//Remove the hidden input with the bankId
		var span = $('picker_'+rowType+'Picker_inputArea');

		if (span != null) {
			var inputs = span.getElementsBySelector('input');
			for (var i = 0; i < inputs.size();i++) {
				if (inputs[i].value == inputValue) {
					inputs[i].remove()
					break;
				}
			}
		}

		//Now remove the table row
		$(rowId).remove();	
	}
</script>


<cti:msgScope paths="modules.capcontrol.ivvc.zoneWizard">

<tags:setFormEditMode mode="${mode}"/>

<cti:displayForPageEditModes modes="EDIT">
	<cti:url var="action"  value="/spring/capcontrol/ivvc/wizard/updateZone"/>
</cti:displayForPageEditModes>
<cti:displayForPageEditModes modes="CREATE">
	<cti:url var="action" value="/spring/capcontrol/ivvc/wizard/createZone"/>
</cti:displayForPageEditModes>

<form:form commandName="zoneDto" action="${action}" >
	<span id="errorOnPage" style="display:none" class="errorIndicator">Missing fields are marked in red.</span>

	<tags:nameValueContainer2 style="border-collapse:separate;border-spacing:5px;">
		<%-- Zone Name --%>
		<form:hidden path="zoneId" id="zoneId"/>
		<tags:inputNameValue path="name" nameKey=".label.name" size="50"/>

		<%-- Regulator Selection --%>
		<tags:nameValue2 nameKey=".label.regulator">
			<form:hidden path="regulatorId" id="selectedRegulatorId"/>
			<span id="selectedRegulatorName">
				<spring:escapeBody htmlEscape="true">${regulatorName}</spring:escapeBody>
			</span>
			<tags:pickerDialog 	id="voltageRegulatorPicker" 
				type="voltageRegulatorPicker" 
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
		<cti:displayForPageEditModes modes="CREATE">
			<tags:selectNameValue items="${zones}" 
				itemLabel="name" 
				nameKey=".label.parentZone" 
				path="parentZoneId" 
				itemValue="id" 
				defaultItemLabel="Choose Parent Zone"
				defaultItemValue="-1"/>
		</cti:displayForPageEditModes>
		<cti:displayForPageEditModes modes="EDIT,VIEW">
			<tags:nameValue2 nameKey=".label.parentZone">
				<form:hidden path="parentZoneId" id="parentZoneId"/>
				<span id="parentZoneName" class="disabledRow">
					<spring:escapeBody htmlEscape="true">${parentZoneName}</spring:escapeBody>
				</span>
			</tags:nameValue2>
		</cti:displayForPageEditModes>
	</tags:nameValueContainer2>
	
	<cti:dataGrid tableStyle="width:100%;" cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:20px;width:30%">
		<cti:msg2 var="pickerText" key=".label.picker"/>
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
								<th><i:inline key=".table.remove"/></th>
							</tr>
						</thead>
						<tbody id="bankTableBody">
							<c:forEach var="row" items="${assignedBanks}">
								<tr id="${row.type}_${row.id}">
									<td>
										<input type="hidden" value="${row.id}" name="${row.type}Ids"/>
										<spring:escapeBody htmlEscape="true">${row.name}</spring:escapeBody>
									</td>
									<td>
										<spring:escapeBody htmlEscape="true">${row.device}</spring:escapeBody>
									</td>
									<td>
										<cti:img key="delete" href="javascript:removeTableRow('${row.type}_${row.id}')"/>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<br>
				<tags:pickerDialog 	id="bankPicker" 
					type="capBankPicker"
					multiSelectMode="true"
					endAction="addBankHandler"
					linkType="button">${pickerText}</tags:pickerDialog>
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
								<th><i:inline key=".table.remove"/></th>
							</tr>
						</thead>
						<tbody id="pointTableBody">
							<c:forEach var="row" items="${assignedPoints}">
								<tr id="${row.type}_${row.id}">
									<td>
										<input type="hidden" value="${row.id}" name="${row.type}Ids"/>
										<spring:escapeBody htmlEscape="true">${row.name}</spring:escapeBody>
									</td>
									<td>
										<spring:escapeBody htmlEscape="true">${row.device}</spring:escapeBody>
									</td>
									<td>
										<cti:img key="delete" href="javascript:removeTableRow('${row.type}_${row.id}')"/>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<br>
				<tags:pickerDialog 	id="pointPicker" 
					type="voltPointPicker"
					multiSelectMode="true"
					endAction="addPointHandler"
					linkType="button">${pickerText}</tags:pickerDialog>
			</tags:boxContainer2>
		</cti:dataGridCell>
	</cti:dataGrid>
	
	<div class="pageActionArea">
		<cti:displayForPageEditModes modes="EDIT">
			<input type="submit" value="Update" class="formSubmit">
		</cti:displayForPageEditModes>
		<cti:displayForPageEditModes modes="CREATE">
			<input type="submit" value="Create" class="formSubmit">
		</cti:displayForPageEditModes>
	</div>

    
</form:form>

</cti:msgScope>

