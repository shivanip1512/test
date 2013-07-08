// This Javascript file works with edit.jsp (and is only used by it).

function deleteAction(rowIdNum) {
	var row = jQuery('#actionRow' + rowIdNum).remove();
	

    var inputElement = jQuery('#actionsTable td input.statusPointMonitorProcessorId');
    for (var index = 0; index < inputElement.length; index++) {
    	inputElement[index].name = 'processors[' + index + '].statusPointMonitorProcessorId';
    }
    
    inputElement = jQuery('#actionsTable td select.prevStateSelect');
    for (var index = 0; index < inputElement.length; index++) {
    	inputElement[index].name = 'processors[' + index + '].prevState';
    }
    
    inputElement = jQuery('#actionsTable td select.nextStateSelect');
    for (var index = 0; index < inputElement.length; index++) {
    	inputElement[index].name = 'processors[' + index + '].nextState';
    }
    
    inputElement = jQuery('#actionsTable td select.actionTypeSelect');
    for (var index = 0; index < inputElement.length; index++) {
    	inputElement[index].name = 'processors[' + index + '].actionType';
    }
}

function addAction(processor) {
	var templateHtml = jQuery('#templateHtml > a');
    var deleteActionIcon = templateHtml[0];
    
    var templateHtml = jQuery('#templateHtml > select');
    var states = templateHtml[0];
    var eventTypes = templateHtml[1];
    
	var actionsTable = jQuery('#actionsTable > tbody:last');
	var rowJoe = actionsTable.prop('rows');
	var rowNum = rowJoe.length;
	
	
	
	actionsTable.append("<tr></tr>");
	var newRow = actionsTable.find("tr:last");
	var newRowIdNum = addAction.nextRowIdNum++;
	newRow.attr('id', 'actionRow' + newRowIdNum);
	
	newRow.append('<td></td>');
	var cellPrevState = newRow.find('td:last');
	newRow.append('<td></td>');
	var cellNextState = newRow.find('td:last');
	newRow.append('<td></td>');
	var cellActionType = newRow.find('td:last');
	newRow.append('<td></td>');
	var cellDelete = newRow.find('td:last');;
	
	
	var prevSelectNode = states.clone(true);
	prevSelectNode.name = 'processors[' + (rowNum-1) + '].prevState';
	prevSelectNode.className = 'prevStateSelect';
	cellPrevState.append(prevSelectNode);
	
	var nextSelectNode = states.clone(true);
	nextSelectNode.name = 'processors[' + (rowNum-1) + '].nextState';
	nextSelectNode.className = 'nextStateSelect';
	cellNextState.append(nextSelectNode);
	
	var actionTypeSelectNode = eventTypes.clone(true);
	actionTypeSelectNode.name = 'processors[' + (rowNum-1) + '].actionType';
	actionTypeSelectNode.className = 'actionTypeSelect';
	cellActionType.append(actionTypeSelectNode);
	
	var deleteIconNode = deleteActionIcon.clone(true);
	deleteIconNode.href = "javascript:deleteAction(" + newRowIdNum + ")";
	cellDelete.append(deleteIconNode);
	
	if(processor) {
		var processorIdNode = document.createElement("input");
		processorIdNode.name = 'processors[' + (rowNum-1) + '].statusPointMonitorProcessorId';
		processorIdNode.type = 'hidden';
		processorIdNode.className = 'statusPointMonitorProcessorId';
		processorIdNode.value = processor.statusPointMonitorProcessorId;
		cellPrevState.append(processorIdNode);
		
		prevSelectNode.value = processor.prevState;
		nextSelectNode.value = processor.nextState;
		actionTypeSelectNode.value = processor.actionType;
	} else {
		//Highlight new row
		flashYellow(newRow.id);
	}
}

function initWithProcessors(processors) {
	addAction.nextRowIdNum = 0;
	if (processors) {
		for (var index = 0; index < processors.length; index++) {
			addAction(processors[index]);
		}
	}
}
