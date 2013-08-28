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
            var index;
            mod.addAction.nextRowIdNum = 0;
            if (processors) {
                for (index = 0; index < processors.length; index++) {
                    mod.addAction(processors[index]);
                }
            }
        },

        deleteAction: function (rowIdNum) {
            var index,
                inputElement;

            jQuery('#actionRow' + rowIdNum).remove();

            inputElement = jQuery('#actionsTable td input.statusPointMonitorProcessorId');
            for (index = 0; index < inputElement.length; index++) {
                inputElement[index].name = 'processors[' + index + '].statusPointMonitorProcessorId';
            }
            
            inputElement = jQuery('#actionsTable td select.prevStateSelect');
            for (index = 0; index < inputElement.length; index++) {
                inputElement[index].name = 'processors[' + index + '].prevState';
            }
            
            inputElement = jQuery('#actionsTable td select.nextStateSelect');
            for (index = 0; index < inputElement.length; index++) {
                inputElement[index].name = 'processors[' + index + '].nextState';
            }
            
            inputElement = jQuery('#actionsTable td select.actionTypeSelect');
            for (index = 0; index < inputElement.length; index++) {
                inputElement[index].name = 'processors[' + index + '].actionType';
            }
        },
        
        addAction: function (processor) {
            var templateHtml = jQuery('#templateHtml > a'),
                deleteActionIcon = templateHtml[0],
                templateHtml = jQuery('#templateHtml > select'),
                states = templateHtml[0],
                eventTypes = templateHtml[1],
                actionsTable = jQuery('#actionsTable > tbody:last'),
                rowNum = actionsTable.prop('rows').length,
                newRow,
                newRowIdNum,
                cellPrevState,
                cellNextState,
                cellActionType,
                cellDelete,
                prevSelectNode,
                nextSelectNode,
                actionTypeSelectNode,
                deleteIconNode,
                processorIdNode;

            actionsTable.append("<tr></tr>");
            newRow = actionsTable.find("tr:last");
            newRowIdNum = mod.addAction.nextRowIdNum++;
            newRow.attr('id', 'actionRow' + newRowIdNum);

            newRow.append('<td></td>');
            cellPrevState = newRow.find('td:last');
            newRow.append('<td></td>');
            cellNextState = newRow.find('td:last');
            newRow.append('<td></td>');
            cellActionType = newRow.find('td:last');
            newRow.append('<td></td>');
            cellDelete = newRow.find('td:last');;

            prevSelectNode = states.clone(true);
            prevSelectNode.name = 'processors[' + (rowNum) + '].prevState';
            prevSelectNode.className = 'prevStateSelect';
            cellPrevState.append(prevSelectNode);

            nextSelectNode = states.clone(true);
            nextSelectNode.name = 'processors[' + (rowNum) + '].nextState';
            nextSelectNode.className = 'nextStateSelect';
            cellNextState.append(nextSelectNode);

            actionTypeSelectNode = eventTypes.clone(true);
            actionTypeSelectNode.name = 'processors[' + (rowNum) + '].actionType';
            actionTypeSelectNode.className = 'actionTypeSelect';
            cellActionType.append(actionTypeSelectNode);

            deleteIconNode = deleteActionIcon.clone(true);
            deleteIconNode.href = "javascript:Yukon.StatusPointMonitor.deleteAction(" + newRowIdNum + ")";
            cellDelete.append(deleteIconNode);

            if(processor) {
                processorIdNode = document.createElement("input");
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