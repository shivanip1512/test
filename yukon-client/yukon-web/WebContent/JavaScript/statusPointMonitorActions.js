/**
 * Singleton that manages the javascript in the Status Point Monitor
 * 
 * @class Status Point Monitor javascript
 * @requires jQuery 1.6+
 */
Yukon.namespace('Yukon.StatusPointMonitor');

Yukon.StatusPointMonitor = (function () {
    var mod;
    
    mod = {
            
        initWithProcessors: function (processors) {
            mod.addAction.nextRowIdNum = 0;
            if (processors) {
                for (var index = 0; index < processors.length; index++) {
                    mod.addAction(processors[index]);
                }
            }
        },
        
        deleteAction: function (rowIdNum) {
            jQuery('#actionRow' + rowIdNum).remove();

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
        },
        
        addAction: function (processor) {
            var templateHtml = jQuery('#templateHtml > a');
            var deleteActionIcon = templateHtml[0];
            
            var templateHtml = jQuery('#templateHtml > select');
            var states = templateHtml[0];
            var eventTypes = templateHtml[1];
            
            var actionsTable = jQuery('#actionsTable > tbody:last');
            var rowNum = actionsTable.prop('rows').length;
            
            actionsTable.append("<tr></tr>");
            var newRow = actionsTable.find("tr:last");
            var newRowIdNum = mod.addAction.nextRowIdNum++;
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
            prevSelectNode.name = 'processors[' + (rowNum) + '].prevState';
            prevSelectNode.className = 'prevStateSelect';
            cellPrevState.append(prevSelectNode);
            
            var nextSelectNode = states.clone(true);
            nextSelectNode.name = 'processors[' + (rowNum) + '].nextState';
            nextSelectNode.className = 'nextStateSelect';
            cellNextState.append(nextSelectNode);
            
            var actionTypeSelectNode = eventTypes.clone(true);
            actionTypeSelectNode.name = 'processors[' + (rowNum) + '].actionType';
            actionTypeSelectNode.className = 'actionTypeSelect';
            cellActionType.append(actionTypeSelectNode);
            
            var deleteIconNode = deleteActionIcon.clone(true);
            deleteIconNode.href = "javascript:Yukon.StatusPointMonitor.deleteAction(" + newRowIdNum + ")";
            cellDelete.append(deleteIconNode);
            
            if(processor) {
                var processorIdNode = document.createElement("input");
                processorIdNode.name = 'processors[' + (rowNum) + '].statusPointMonitorProcessorId';
                processorIdNode.type = 'hidden';
                processorIdNode.className = 'statusPointMonitorProcessorId';
                processorIdNode.value = processor.statusPointMonitorProcessorId;
                cellPrevState.append(processorIdNode);
                
                prevSelectNode.value = processor.prevState;
                nextSelectNode.value = processor.nextState;
                actionTypeSelectNode.value = processor.actionType;
            } else {
                flashYellow(newRow.id);
            }
        }
    };
    return mod;
})(Yukon.StatusPointMonitor || {});