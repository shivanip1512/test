// This Javascript file works with edit.jsp (and is only used by it).

function deleteAction(rowIdNum) {
	var row = $('actionRow' + rowIdNum);
	
	row.parentNode.removeChild(row);

    var inputElement = $$('#actionsTable td input.statusPointMonitorProcessorId');
    for (var index = 0; index < inputElement.length; index++) {
    	inputElement[index].name = 'processors[' + index + '].statusPointMonitorProcessorId';
    }
    
    inputElement = $$('#actionsTable td select.prevStateSelect');
    for (var index = 0; index < inputElement.length; index++) {
    	inputElement[index].name = 'processors[' + index + '].prevState';
    }
    
    inputElement = $$('#actionsTable td select.nextStateSelect');
    for (var index = 0; index < inputElement.length; index++) {
    	inputElement[index].name = 'processors[' + index + '].nextState';
    }
    
    inputElement = $$('#actionsTable td select.actionTypeSelect');
    for (var index = 0; index < inputElement.length; index++) {
    	inputElement[index].name = 'processors[' + index + '].actionType';
    }
}

function addAction(processor) {
	var templateHtml = $$('#templateHtml > a');
    var deleteActionIcon = templateHtml[0];
    
    var templateHtml = $$('#templateHtml > select');
    var states = templateHtml[0];
    var eventTypes = templateHtml[1];
    
	var actionsTable = $('actionsTable');
	var rowNum = actionsTable.rows.length;
	var newRow = actionsTable.insertRow(rowNum);
	var newRowIdNum = addAction.nextRowIdNum++;
	newRow.id = 'actionRow' + newRowIdNum;
	
	var cellPrevState = newRow.insertCell(0);
	var cellNextState = newRow.insertCell(1);
	var cellActionType = newRow.insertCell(2);
	var cellDelete = newRow.insertCell(3);
	
	var prevSelectNode = states.cloneNode(true);
	prevSelectNode.name = 'processors[' + (rowNum-1) + '].prevState';
	prevSelectNode.className = 'prevStateSelect';
	cellPrevState.appendChild(prevSelectNode);
	
	var nextSelectNode = states.cloneNode(true);
	nextSelectNode.name = 'processors[' + (rowNum-1) + '].nextState';
	nextSelectNode.className = 'nextStateSelect';
	cellNextState.appendChild(nextSelectNode);
	
	var actionTypeSelectNode = eventTypes.cloneNode(true);
	actionTypeSelectNode.name = 'processors[' + (rowNum-1) + '].actionType';
	actionTypeSelectNode.className = 'actionTypeSelect';
	cellActionType.appendChild(actionTypeSelectNode);
	
	var deleteIconNode = deleteActionIcon.cloneNode(true);
	deleteIconNode.href = "javascript:deleteAction(" + newRowIdNum + ")";
	cellDelete.appendChild(deleteIconNode);
	
	if(processor) {
		var processorIdNode = document.createElement("input");
		processorIdNode.name = 'processors[' + (rowNum-1) + '].statusPointMonitorProcessorId';
		processorIdNode.type = 'hidden';
		processorIdNode.className = 'statusPointMonitorProcessorId';
		processorIdNode.value = processor.statusPointMonitorProcessorId;
		cellPrevState.appendChild(processorIdNode);
		
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
