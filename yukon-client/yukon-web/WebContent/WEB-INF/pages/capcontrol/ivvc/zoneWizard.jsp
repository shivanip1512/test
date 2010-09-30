<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>


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

<cti:msg2 var="pickerLinkText" key=".label.picker"/>
<cti:msg2 var="voltageDeviceTitle" key=".title.assignedVoltageDevice"/>
<cti:msg2 var="voltagePointTitle" key=".title.assignedVoltagePoint"/>
<cti:msg2 var="tableRemove" key=".table.remove"/>
<cti:msg2 var="bankTableName" key=".table.bank.name"/>
<cti:msg2 var="bankTableDevice" key=".table.bank.device"/>
<cti:msg2 var="pointTableName" key=".table.point.name"/>
<cti:msg2 var="pointTableDevice" key=".table.point.device"/>

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
			<span id="selectedRegulatorName">${regulatorName}</span>
			<tags:pickerDialog 	id="ltcPicker" 
				type="ltcPicker" 
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
			<span id="selectedBusName" class="disabledRow">${subBusName}</span>
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
				<span id="parentZoneName" class="disabledRow">${parentZoneName}</span>
			</tags:nameValue2>
		</cti:displayForPageEditModes>
	</tags:nameValueContainer2>
	
	<cti:dataGrid tableStyle="width:100%;" cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:20px;width:30%">

		<table style="display:none">
			<tr id="defaultRow">
				<td colspan="3" style="text-align: center"><img src="/WebConfig/yukon/Icons/indicator_arrows.gif"></td>
			</tr>
		</table>

		<cti:dataGridCell>
			<tags:boxContainer title="${voltageDeviceTitle}" hideEnabled="false" showInitially="true">
				<div style="overflow: auto;max-height: 150px;">
					<table id="bankTable" class="compactResultsTable">
						<thead>
							<tr>
								<th>${bankTableName}</th>
								<th>${bankTableDevice}</th>
								<th>${tableRemove}</th>
							</tr>
						</thead>
						<tbody id="bankTableBody">
							<c:forEach var="row" items="${assignedBanks}">
								<tr id="${row.type}_${row.id}">
									<td><input type="hidden" value="${row.id}" name="${row.type}Ids"/>${row.name}</td>
									<td>${row.device}</td>
									<td><cti:img key="delete" href="javascript:removeTableRow('${row.type}_${row.id}')"/></td>
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
					linkType="button">${pickerLinkText}</tags:pickerDialog>
			</tags:boxContainer>
		</cti:dataGridCell>
		<br>
		<cti:dataGridCell>	
			<tags:boxContainer title="${voltagePointTitle}" hideEnabled="false" showInitially="true">
				<div style="overflow: auto;max-height: 150px;">
					<table id="pointTable" class="compactResultsTable">
						<thead>
							<tr>
								<th>${pointTableName}</th>
								<th>${pointTableDevice}</th>
								<th>${tableRemove}</th>
							</tr>
						</thead>
						<tbody id="pointTableBody">
							<c:forEach var="row" items="${assignedPoints}">
								<tr id="${row.type}_${row.id}">
									<td><input type="hidden" value="${row.id}" name="${row.type}Ids"/>${row.name}</td>
									<td>${row.device}</td>
									<td><cti:img key="delete" href="javascript:removeTableRow('${row.type}_${row.id}')"/></td>
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
					linkType="button" >${pickerLinkText}</tags:pickerDialog>
			</tags:boxContainer>
		</cti:dataGridCell>
	</cti:dataGrid>
	
	<cti:displayForPageEditModes modes="EDIT">
		<input type="submit" value="Update">
	</cti:displayForPageEditModes>
	<cti:displayForPageEditModes modes="CREATE">
		<input type="submit" value="Create">
	</cti:displayForPageEditModes>

    
</form:form>

</cti:msgScope>

