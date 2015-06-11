yukon.namespace('yukon.surveys.edit');

/**
 * Module that manages survey editing
 * @module   yukon.surveys.edit
 * @requires JQUERY
 * @requires JQUERY UI
 */
yukon.surveys.edit = (function () {

    var 
        /** @type {Object} - Instance of move up Icon. */
        _moveUpIcon,
        
        /** @type {Object} - Instance of move down Icon. */
        _moveDownIcon,
        
        /** @type {Object} - Instance of delete answer Icon. */
        _deleteAnswerIcon,
        
        /** @type {Object} - Instance of disabled move up Icon. */
        _moveUpDisabledIcon,
        
        /** @type {Object} - Instance of disabled move down Icon. */
        _moveDownDisabledIcon,
        
        /** 
         * Returns the index of the row.
         * @param {number} rowIdNum - row Id.
         * @returns {number} index - index of row.
         */
        _indexOfRow = function (rowIdNum) {
            var index;
            for (index = 0; index < mod.addAnswer.rowIdNums.length; index += 1) {
                if (mod.addAnswer.rowIdNums[index] === rowIdNum) {
                    return index;
                }
            }
        },

        /** Open the survey question dialog. */
        _initQuestionDialog = function() {
            var infoHolder = $('[data-answer-keys]'),
                answerKeys = infoHolder.data('answerKeys'),
                ii;
            mod.addAnswer.nextRowIdNum = 0;
            mod.addAnswer.rowIdNums = [];

            _questionTypeChanged();
            
            $(document).on('change', '#questionType', _questionTypeChanged)

            if (answerKeys instanceof Array) {
                for (ii = 0; ii < answerKeys.length; ii += 1) {
                    mod.addAnswer(answerKeys[ii]);
                }
                $('#questionKey').focus();
                $('#questionKey').select();
            }
            
            $('#question-popup').dialog({width: 600, buttons: yukon.ui.buttons({ event: 'yukon.survey.question.save' }) });
        },

        /** 
         * Disable the move Up button.
         * @param {number} rowIdNum - row Id.
         */
        _disableMoveUp = function (rowIdNum) {
            var oldMoveIcon = $('#moveUpIcon' + rowIdNum)[0],
                disabledIcon = _moveUpDisabledIcon.cloneNode(true);
            disabledIcon.id = 'moveUpIcon' + rowIdNum;
            oldMoveIcon.parentNode.replaceChild(disabledIcon, oldMoveIcon);
        },

        /** 
         * Disable the move Down button.
         * @param {number} rowIdNum - row Id.
         */
        _disableMoveDown = function (rowIdNum) {
            var oldMoveIcon = $('#moveDownIcon' + rowIdNum)[0],
                disabledIcon = _moveDownDisabledIcon.cloneNode(true);
            disabledIcon.id = 'moveDownIcon' + rowIdNum;
            oldMoveIcon.parentNode.replaceChild(disabledIcon, oldMoveIcon);
        },
        
        /** 
         * Enable the move Up button.
         * @param {number} rowIdNum - row Id.
         */
        _enableMoveUp = function (rowIdNum) {
            var oldMoveIcon = $('#moveUpIcon' + rowIdNum)[0],
                enabledIcon = _moveUpIcon.cloneNode(true);
            enabledIcon.id = 'moveUpIcon' + rowIdNum;
            oldMoveIcon.parentNode.replaceChild(enabledIcon, oldMoveIcon);
        },

        /** 
         * Enable the move down button.
         * @param {number} rowIdNum - row Id.
         */
        _enableMoveDown = function (rowIdNum) {
            var oldMoveIcon = $('#moveDownIcon' + rowIdNum)[0],
                enabledIcon = _moveDownIcon.cloneNode(true);
            enabledIcon.id = 'moveDownIcon' + rowIdNum;
            if (typeof oldMoveIcon !== 'undefined') {
                oldMoveIcon.parentNode.replaceChild(enabledIcon, oldMoveIcon);
            }
        },

        /** 
         * Delete the survey answer.
         * @param {Object} event - jquery event object.
         */
        _deleteAnswer = function (event) {
            var rowIdNum = $(event.target).closest('tr').data('rowIdNum'),
                row = $('#answerRow' + rowIdNum)[0];
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

        /** 
         * Move the answer up in table.
         * @param {Object} event - jquery event object.
         */
        _moveAnswerUp = function (event) {
            var rowIdNum = $(event.target).closest('tr').data('rowIdNum'),
                index = _indexOfRow(rowIdNum),
                previousRowId = mod.addAnswer.rowIdNums[index - 1],
                rowToMove = $('#answerRow' + rowIdNum)[0],
                parent = rowToMove.parentNode,
                previousRow = $('#answerRow' + previousRowId)[0],
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

        /** 
         * Move the answer down in the table.
         * @param {Object} event - jquery event object.
         */
        _moveAnswerDown = function (event) {
            var rowIdNum = $(event.target).closest('tr').data('rowIdNum'),
                index = _indexOfRow(rowIdNum),
                nextRowId = mod.addAnswer.rowIdNums[index + 1],
                rowToMove = $('#answerRow' + rowIdNum)[0],
                parent = rowToMove.parentNode,
                nextRow = $('#answerRow' + nextRowId)[0],
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

        /** Handles question change event. */
        _questionTypeChanged = function () {
            var questionType = $('#questionType').val();
            $('.additionalInfo').hide();
            $('.additionalInfo[id$=_' + questionType + ']').show();
        },

        mod = {
            init: function (params) {

                if (params.hasBeenTaken === 'false') {
                    var icons = $('#templateIcons > button');
                    _moveUpIcon = icons[0];
                    _moveDownIcon = icons[1];
                    _deleteAnswerIcon = icons[2];
                    _moveUpDisabledIcon = icons[3];
                    _moveDownDisabledIcon = icons[4];
                }
                
                $(document).on('yukon.survey.details.edit' , function (ev) {
                    $('#survey-details-form').ajaxSubmit({
                        success: function(data, status, xhr, $form) {
                            window.location.href = data.url;
                        }, 
                        error: function(xhr, status, error, $form) {
                            $('#details-popup').html(xhr.responseText);
                        }
                    });
                });
                
                $(document).on('yukon.survey.question.save' , function (ev) {
                    $('#question-form').ajaxSubmit({
                        success: function(data, status, xhr, $form) {
                            window.location.href = window.location.href;
                        }, 
                        error: function(xhr, status, error, $form) {
                            $('#question-popup').html(xhr.responseText);
                        }
                    });
                });

                $('#addQuestionBtn').click(function (ev) {
                    var addQuestionUrl = $(this).data('addQuestionUrl');
                    $('#question-popup').load(addQuestionUrl, _initQuestionDialog);
                });

                $('.editQuestionBtn').click(function (event) {
                    var editQuestionUrl = $(event.target).closest('[data-edit-question-url]').data('editQuestionUrl');
                    $('#question-popup').load(editQuestionUrl, _initQuestionDialog);
                });

                $(document).on('click', '.moveAnswerUp', _moveAnswerUp);
                $(document).on('click', '.moveAnswerDown', _moveAnswerDown);
                $(document).on('click', '.deleteAnswer', _deleteAnswer);

            },

            addAnswer : function (answerKey) {
                var answerTable = $('#answerTable tbody'),
                    newRow = $(document.createElement("tr")),
                    newRowIdNum,
                    isFirstRow,
                    keyCell = $(document.createElement("td")),
                    inputNode = document.createElement("input"),
                    actionsCell = $(document.createElement("td")),
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
            }
        };
    return mod;
})();