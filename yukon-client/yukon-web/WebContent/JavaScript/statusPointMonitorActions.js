// This Javascript file works with edit.jsp (and is only used by it).


function indexOfRow(rowIdNum) {

	for (var index = 0; index < addAction.rowIdNums.length; index++) {
		if (addAction.rowIdNums[index] == rowIdNum) {
			return index;
		}
	}
}

function deleteAction(rowIdNum) {
	var row = $('actionRow' + rowIdNum);
	row.parentNode.removeChild(row);

    addAction.rowIdNums.splice(indexOfRow(rowIdNum), 1);
    
    var inputElement = $$('#actionsTable td input.statusPointMonitorMessageProcessorId');
    for (var index = 0; index < inputElement.length; index++) {
    	inputElement[index].name = 'messageProcessors[' + index + '].statusPointMonitorMessageProcessorId';
    }
    
    inputElement = $$('#actionsTable td select.prevStateSelect');
    for (var index = 0; index < inputElement.length; index++) {
    	inputElement[index].name = 'messageProcessors[' + index + '].prevState';
    }
    
    inputElement = $$('#actionsTable td select.nextStateSelect');
    for (var index = 0; index < inputElement.length; index++) {
    	inputElement[index].name = 'messageProcessors[' + index + '].nextState';
    }
    
    inputElement = $$('#actionsTable td select.actionTypeSelect');
    for (var index = 0; index < inputElement.length; index++) {
    	inputElement[index].name = 'messageProcessors[' + index + '].actionType';
    }
}

function addAction(statusPointMonitorMessageProcessor) {
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
	
	var prevSelectNode = states.cloneNode(true);
	prevSelectNode.name = 'messageProcessors[' + (rowNum-1) + '].prevState';
	prevSelectNode.className = 'prevStateSelect';
	cellPrevState.appendChild(prevSelectNode);
	
	var nextSelectNode = states.cloneNode(true);
	nextSelectNode.name = 'messageProcessors[' + (rowNum-1) + '].nextState';
	nextSelectNode.className = 'nextStateSelect';
	cellNextState.appendChild(nextSelectNode);
	
	var actionTypeSelectNode = eventTypes.cloneNode(true);
	actionTypeSelectNode.name = 'messageProcessors[' + (rowNum-1) + '].actionType';
	actionTypeSelectNode.className = 'actionTypeSelect';
	cellActionType.appendChild(actionTypeSelectNode);
	
	if(statusPointMonitorMessageProcessor) {
		var messageProcessorIdNode = document.createElement("input");
		messageProcessorIdNode.name = 'messageProcessors[' + (rowNum-1) + '].statusPointMonitorMessageProcessorId';
		messageProcessorIdNode.type = 'hidden';
		messageProcessorIdNode.className = 'statusPointMonitorMessageProcessorId';
		messageProcessorIdNode.value = statusPointMonitorMessageProcessor.statusPointMonitorMessageProcessorId;
		cellPrevState.appendChild(messageProcessorIdNode);
		
		prevSelectNode.value = statusPointMonitorMessageProcessor.prevState;
		nextSelectNode.value = statusPointMonitorMessageProcessor.nextState;
		actionTypeSelectNode.value = statusPointMonitorMessageProcessor.actionType;
	}
	
	var cellDelete = newRow.insertCell(3);
	
	icon = deleteActionIcon.cloneNode(true);
	icon.href = "javascript:deleteAction(" + newRowIdNum + ")";
	cellDelete.appendChild(icon);
}

function initWithMessageProcessors(statusPointMonitorMessageProcessors) {
	addAction.nextRowIdNum = 0;
	addAction.rowIdNums = [];
	if (statusPointMonitorMessageProcessors) {
		for (var index = 0; index < statusPointMonitorMessageProcessors.length; index++) {
			addAction(statusPointMonitorMessageProcessors[index]);
		}
	}
}
