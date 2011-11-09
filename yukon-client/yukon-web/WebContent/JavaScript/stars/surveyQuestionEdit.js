// This Javascript file works with pages/stars/survey/editQuestion.jsp (and is only used by it).

function indexOfRow(rowIdNum) {
	for (var index = 0; index < addAnswer.rowIdNums.length; index++) {
		if (addAnswer.rowIdNums[index] == rowIdNum) {
			return index;
		}
	}
}

function disableMoveUp(rowIdNum) {
    var oldMoveIcon = jQuery('#moveUpIcon' + rowIdNum)[0];
    var disabledIcon = moveUpDisabledIcon.cloneNode(true);
    disabledIcon.id = 'moveUpIcon' + rowIdNum;
    oldMoveIcon.parentNode.replaceChild(disabledIcon, oldMoveIcon);
}

function disableMoveDown(rowIdNum) {
	var oldMoveIcon = jQuery('#moveDownIcon' + rowIdNum)[0];
	var disabledIcon = moveDownDisabledIcon.cloneNode(true);
	disabledIcon.id = 'moveDownIcon' + rowIdNum;
	oldMoveIcon.parentNode.replaceChild(disabledIcon, oldMoveIcon);
}

function enableMoveUp(rowIdNum) {
    var oldMoveIcon = jQuery('#moveUpIcon' + rowIdNum)[0];
    var enabledIcon = moveUpIcon.cloneNode(true);
    enabledIcon.id = 'moveUpIcon' + rowIdNum;
    oldMoveIcon.parentNode.replaceChild(enabledIcon, oldMoveIcon);
}

function enableMoveDown(rowIdNum) {
	var oldMoveIcon = jQuery('#moveDownIcon' + rowIdNum)[0];
	var enabledIcon = moveDownIcon.cloneNode(true);
	enabledIcon.id = 'moveDownIcon' + rowIdNum;
	oldMoveIcon.parentNode.replaceChild(enabledIcon, oldMoveIcon);
}

function deleteAnswer(event) {
    var rowIdNum = jQuery(event.target).closest('tr').data('rowIdNum');
	if (addAnswer.rowIdNums.length > 1) {
		if (rowIdNum === addAnswer.rowIdNums[0]) {
			disableMoveUp(addAnswer.rowIdNums[1]);
		} else if (rowIdNum === addAnswer.rowIdNums[addAnswer.rowIdNums.length - 1]) {
			// disable move down on new last row
			disableMoveDown(addAnswer.rowIdNums[addAnswer.rowIdNums.length - 2])
		}
	}

	var row = jQuery('#answerRow' + rowIdNum)[0];
	row.parentNode.removeChild(row);

    addAnswer.rowIdNums.splice(indexOfRow(rowIdNum), 1);
}

function addAnswer(answerKey) {
	var answerTable = $('answerTable');
	var rowNum = answerTable.rows.length;
	var newRow = answerTable.insertRow(rowNum);
	var newRowIdNum = addAnswer.nextRowIdNum++;
	jQuery.data(newRow, 'rowIdNum', newRowIdNum);
	newRow.id = 'answerRow' + newRowIdNum;
	var isFirstRow = addAnswer.rowIdNums.length == 0;

	var keyCell = newRow.insertCell(0);
	var inputNode = document.createElement("input");
	inputNode.name = 'answerKeys';
	inputNode.type = 'text';
	inputNode.maxLength = 64;
	keyCell.appendChild(inputNode);
	var actionsCell = newRow.insertCell(1);

	var icon = (isFirstRow ? moveUpDisabledIcon : moveUpIcon).cloneNode(true);
    icon.id = 'moveUpIcon' + newRowIdNum;
	actionsCell.appendChild(icon);

	actionsCell.appendChild(document.createTextNode(' '));
	icon = moveDownDisabledIcon.cloneNode(true);
	icon.id = 'moveDownIcon' + newRowIdNum;
	actionsCell.appendChild(icon);

	actionsCell.appendChild(document.createTextNode(' '));
	icon = deleteAnswerIcon.cloneNode(true);
	actionsCell.appendChild(icon);

	if (!isFirstRow) {
		enableMoveDown(addAnswer.rowIdNums[addAnswer.rowIdNums.length - 1]);
	}
	addAnswer.rowIdNums.push(newRowIdNum);

	if (answerKey) {
		inputNode.value = answerKey;
	} else {
		var inputElements = answerTable.getElementsBySelector('input');
		var maxKeyNumber = 0;
		for (var index = 0; index < inputElements.length - 1; index++) {
			var result = inputElements[index].value.match(/^answer(\d+)$/);
			if (result) {
				maxKeyNumber = parseInt(result[1]);
			}
		}
		inputNode.value = "answer" + (maxKeyNumber + 1);
		inputNode.focus();
		inputNode.select();
	}
}

function moveAnswerUp(event) {
    var rowIdNum = jQuery(event.target).closest('tr').data('rowIdNum');
	var index = indexOfRow(rowIdNum);
	var previousRowId = addAnswer.rowIdNums[index - 1];
	var rowToMove = jQuery('#answerRow' + rowIdNum)[0];
	var parent = rowToMove.parentNode;
	var previousRow = jQuery('#answerRow' + previousRowId)[0];
	parent.removeChild(rowToMove);
	parent.insertBefore(rowToMove, previousRow);
	var save = addAnswer.rowIdNums[index];
	addAnswer.rowIdNums[index] = addAnswer.rowIdNums[index - 1];
	addAnswer.rowIdNums[index - 1] = save;
	if (index == 1) {
		// swapping with first row; update move up icons
		disableMoveUp(rowIdNum);
		enableMoveUp(previousRowId);
	}
	if (index == addAnswer.rowIdNums.length - 1) {
		// this is the last row; update move down icons
		enableMoveDown(rowIdNum);
		disableMoveDown(previousRowId);
	}
}

function moveAnswerDown(event) {
    var rowIdNum = jQuery(event.target).closest('tr').data('rowIdNum');
	var index = indexOfRow(rowIdNum);
	var nextRowId = addAnswer.rowIdNums[index + 1];
	var rowToMove = jQuery('#answerRow' + rowIdNum)[0];
	var parent = rowToMove.parentNode;
	var nextRow = jQuery('#answerRow' + nextRowId)[0];
	parent.removeChild(nextRow);
	parent.insertBefore(nextRow, rowToMove);
	var save = addAnswer.rowIdNums[index];
	addAnswer.rowIdNums[index] = addAnswer.rowIdNums[index + 1];
	addAnswer.rowIdNums[index + 1] = save;
	if (index == 0) {
		// this is the first row; update move up icons
		enableMoveUp(rowIdNum);
		disableMoveUp(nextRowId);
	}
	if (index == addAnswer.rowIdNums.length - 2) {
		// swapping with last row; update move down icons
		disableMoveDown(rowIdNum);
		enableMoveDown(nextRowId);
	}
}

function initWithAnswerKeys(answerKeys) {
	addAnswer.nextRowIdNum = 0;
	addAnswer.rowIdNums = [];
	if (answerKeys) {
		for (var index = 0; index < answerKeys.length; index++) {
			addAnswer(answerKeys[index]);
		}
	}
}

function questionTypeChanged() {
    var questionType = jQuery('#questionType').val();
    jQuery('.additionalInfo').hide();
    jQuery('.additionalInfo[id$=_' + questionType + ']').show();
}
