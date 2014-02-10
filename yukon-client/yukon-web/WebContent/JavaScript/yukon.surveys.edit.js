var yukon = (function (yukonMod) {
    return yukonMod;
})(yukon || {});
yukon.namespace('yukon.Surveys.Edit');

yukon.Surveys.Edit = (function () {


    var _moveUpIcon,
        _moveDownIcon,
        _deleteAnswerIcon,
        _moveUpDisabledIcon,
        _moveDownDisabledIcon,
        _indexOfRow = function (rowIdNum) {
            var index;
            for (index = 0; index < mod.addAnswer.rowIdNums.length; index += 1) {
                if (mod.addAnswer.rowIdNums[index] === rowIdNum) {
                    return index;
                }
            }
        },

        _disableMoveUp = function (rowIdNum) {
            var oldMoveIcon = jQuery('#moveUpIcon' + rowIdNum)[0],
                disabledIcon = _moveUpDisabledIcon.cloneNode(true);
            disabledIcon.id = 'moveUpIcon' + rowIdNum;
            oldMoveIcon.parentNode.replaceChild(disabledIcon, oldMoveIcon);
        },

        _disableMoveDown = function (rowIdNum) {
            var oldMoveIcon = jQuery('#moveDownIcon' + rowIdNum)[0],
                disabledIcon = _moveDownDisabledIcon.cloneNode(true);
            disabledIcon.id = 'moveDownIcon' + rowIdNum;
            oldMoveIcon.parentNode.replaceChild(disabledIcon, oldMoveIcon);
        },

        _enableMoveUp = function (rowIdNum) {
            var oldMoveIcon = jQuery('#moveUpIcon' + rowIdNum)[0],
                enabledIcon = _moveUpIcon.cloneNode(true);
            enabledIcon.id = 'moveUpIcon' + rowIdNum;
            oldMoveIcon.parentNode.replaceChild(enabledIcon, oldMoveIcon);
        },

        _enableMoveDown = function (rowIdNum) {
            var oldMoveIcon = jQuery('#moveDownIcon' + rowIdNum)[0],
                enabledIcon = _moveDownIcon.cloneNode(true);
            enabledIcon.id = 'moveDownIcon' + rowIdNum;
            oldMoveIcon.parentNode.replaceChild(enabledIcon, oldMoveIcon);
        },

        _deleteAnswer = function (event) {
            var rowIdNum = jQuery(event.target).closest('tr').data('rowIdNum'),
                row = jQuery('#answerRow' + rowIdNum)[0];
            if (mod.addAnswer.rowIdNums.length > 1) {
                if (rowIdNum === mod.addAnswer.rowIdNums[0]) {
                    _disableMoveUp(mod.addAnswer.rowIdNums[1]);
                } else if (rowIdNum === mod.addAnswer.rowIdNums[mod.addAnswer.rowIdNums.length - 1]) {
                    // disable move down on new last row
                    _disableMoveDown(mod.addAnswer.rowIdNums[mod.addAnswer.rowIdNums.length - 2]);
                }
            }

            row.parentNode.removeChild(row);

            mod.addAnswer.rowIdNums.splice(_indexOfRow(rowIdNum), 1);
        },

        _moveAnswerUp = function (event) {
            var rowIdNum = jQuery(event.target).closest('tr').data('rowIdNum'),
                index = _indexOfRow(rowIdNum),
                previousRowId = mod.addAnswer.rowIdNums[index - 1],
                rowToMove = jQuery('#answerRow' + rowIdNum)[0],
                parent = rowToMove.parentNode,
                previousRow = jQuery('#answerRow' + previousRowId)[0],
                save;

            parent.removeChild(rowToMove);
            parent.insertBefore(rowToMove, previousRow);
            save = mod.addAnswer.rowIdNums[index];
            mod.addAnswer.rowIdNums[index] = mod.addAnswer.rowIdNums[index - 1];
            mod.addAnswer.rowIdNums[index - 1] = save;
            if (index === 1) {
                // swapping with first row; update move up icons
                _disableMoveUp(rowIdNum);
                _enableMoveUp(previousRowId);
            }
            if (index === mod.addAnswer.rowIdNums.length - 1) {
                // this is the last row; update move down icons
                _enableMoveDown(rowIdNum);
                _disableMoveDown(previousRowId);
            }
        },

        _moveAnswerDown = function (event) {
            var rowIdNum = jQuery(event.target).closest('tr').data('rowIdNum'),
                index = _indexOfRow(rowIdNum),
                nextRowId = mod.addAnswer.rowIdNums[index + 1],
                rowToMove = jQuery('#answerRow' + rowIdNum)[0],
                parent = rowToMove.parentNode,
                nextRow = jQuery('#answerRow' + nextRowId)[0],
                save = mod.addAnswer.rowIdNums[index];

            parent.removeChild(nextRow);
            parent.insertBefore(nextRow, rowToMove);
            mod.addAnswer.rowIdNums[index] = mod.addAnswer.rowIdNums[index + 1];
            mod.addAnswer.rowIdNums[index + 1] = save;
            if (index === 0) {
                // this is the first row; update move up icons
                _enableMoveUp(rowIdNum);
                _disableMoveUp(nextRowId);
            }
            if (index === mod.addAnswer.rowIdNums.length - 2) {
                // swapping with last row; update move down icons
                _disableMoveDown(rowIdNum);
                _enableMoveDown(nextRowId);
            }
        },

        _questionTypeChanged = function () {
            var questionType = jQuery('#questionType').val();
            jQuery('.additionalInfo').hide();
            jQuery('.additionalInfo[id$=_' + questionType + ']').show();
        },

        mod = {
            init: function (params) {
                var editDetailsBtn = jQuery('#editDetailsBtn'),
                    addQuestionBtn = jQuery('#addQuestionBtn'),
                    icons;

                if (params.hasBeenTaken === 'false') {
                    icons = jQuery('#templateIcons > button');
                    _moveUpIcon = icons[0];
                    _moveDownIcon = icons[1];
                    _deleteAnswerIcon = icons[2];
                    _moveUpDisabledIcon = icons[3];
                    _moveDownDisabledIcon = icons[4];
                }
                editDetailsBtn.click(function () {
                    var editDetailsUrl = editDetailsBtn.attr('data-detail-url');
                    jQuery('#ajaxDialog').load(editDetailsUrl);
                });

                addQuestionBtn.click(function () {
                    var addQuestionUrl = addQuestionBtn.attr('data-add-question-url');
                    jQuery('#ajaxDialog').load(addQuestionUrl);
                });

                jQuery('.editQuestionBtn').click(function (event) {
                    var editQuestionUrl = jQuery(event.target).closest('[data-edit-question-url]').attr('data-edit-question-url');
                    jQuery('#ajaxDialog').load(editQuestionUrl);
                });

                jQuery(document).on('click', '.moveAnswerUp', _moveAnswerUp);
                jQuery(document).on('click', '.moveAnswerDown', _moveAnswerDown);
                jQuery(document).on('click', '.deleteAnswer', _deleteAnswer);

                jQuery(document).on('yukonDetailsUpdated', closeAjaxDialogAndRefresh);
                jQuery(document).on('yukonQuestionSaved', closeAjaxDialogAndRefresh);
            },

            addAnswer : function (answerKey) {
                var answerTable = jQuery('#answerTable tbody'),
                    newRow = jQuery(document.createElement("tr")),
                    newRowIdNum,
                    isFirstRow,
                    keyCell = jQuery(document.createElement("td")),
                    inputNode = document.createElement("input"),
                    actionsCell = jQuery(document.createElement("td")),
                    icon,
                    index,
                    inputElements,
                    maxKeyNumber = 0,
                    result;
                answerTable.append(newRow);
                newRowIdNum = mod.addAnswer.nextRowIdNum;
                mod.addAnswer.nextRowIdNum += 1;
                isFirstRow = mod.addAnswer.rowIdNums.length === 0;
                newRow.data('rowIdNum', newRowIdNum);
                newRow.attr("id", "answerRow" + newRowIdNum);

                newRow.append(keyCell);
                inputNode.name = 'answerKeys';
                inputNode.type = 'text';
                inputNode.maxLength = 64;
                keyCell.append(inputNode);
                newRow.append(actionsCell);

                icon = (isFirstRow ? _moveUpDisabledIcon : _moveUpIcon).cloneNode(true);
                icon.id = 'moveUpIcon' + newRowIdNum;
                actionsCell.append(icon);

                actionsCell.append(document.createTextNode(' '));
                icon = _moveDownDisabledIcon.cloneNode(true);
                icon.id = 'moveDownIcon' + newRowIdNum;
                actionsCell.append(icon);

                actionsCell.append(document.createTextNode(' '));
                icon = _deleteAnswerIcon.cloneNode(true);
                actionsCell.append(icon);

                if (!isFirstRow) {
                    _enableMoveDown(mod.addAnswer.rowIdNums[mod.addAnswer.rowIdNums.length - 1]);
                }
                mod.addAnswer.rowIdNums.push(newRowIdNum);

                if (answerKey) {
                    inputNode.value = answerKey;
                } else {
                    inputElements = answerTable.find('input');
                    maxKeyNumber = 0;
                    for (index = 0; index < inputElements.length - 1; index += 1) {
                        result = inputElements[index].value.match(/^answer(\d+)$/);
                        if (result) {
                            maxKeyNumber = parseInt(result[1], 10);
                        }
                    }
                    inputNode.value = "answer" + (maxKeyNumber + 1);
                    inputNode.focus();
                    inputNode.select();
                }
            },

            initQuestions : function () {
                _questionTypeChanged();
                jQuery('#questionType').change(yukon.Surveys.questionTypeChanged);
                jQuery('#inputForm').ajaxForm({'target' : '#ajaxDialog'});
            },

            initAnswerKeys : function (answerKeys) {
                var localEdit = yukon.Surveys.Edit,
                    ii;
                localEdit.addAnswer.nextRowIdNum = 0;
                localEdit.addAnswer.rowIdNums = [];
                if (answerKeys) {
                    for (ii = 0; ii < answerKeys.length; ii += 1) {
                        localEdit.addAnswer(answerKeys[ii]);
                    }
                }
                jQuery('#questionKey').focus();
                jQuery('#questionKey').select();
            }
        };
    return mod;
}());