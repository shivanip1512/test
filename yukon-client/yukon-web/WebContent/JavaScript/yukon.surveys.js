var Yukon = (function (yukonMod) {
    return yukonMod;
})(Yukon || {});
Yukon.namespace('Yukon.Surveys');
Yukon.Surveys = (function () {

    var _indexOfRow = function (rowIdNum) {
        for (var index = 0; index < mod.addAnswer.rowIdNums.length; index++) {
            if (mod.addAnswer.rowIdNums[index] == rowIdNum) {
                return index;
            }
        }
    },

    _disableMoveUp = function (rowIdNum) {
            var oldMoveIcon = jQuery('#moveUpIcon' + rowIdNum)[0],
                disabledIcon = moveUpDisabledIcon.cloneNode(true);
            disabledIcon.id = 'moveUpIcon' + rowIdNum;
            oldMoveIcon.parentNode.replaceChild(disabledIcon, oldMoveIcon);
    },

    _disableMoveDown = function (rowIdNum) {
        var oldMoveIcon = jQuery('#moveDownIcon' + rowIdNum)[0],
            disabledIcon = moveDownDisabledIcon.cloneNode(true);
        disabledIcon.id = 'moveDownIcon' + rowIdNum;
        oldMoveIcon.parentNode.replaceChild(disabledIcon, oldMoveIcon);
    },

    _enableMoveUp = function (rowIdNum) {
        var oldMoveIcon = jQuery('#moveUpIcon' + rowIdNum)[0],
            enabledIcon = moveUpIcon.cloneNode(true);
        enabledIcon.id = 'moveUpIcon' + rowIdNum;
        oldMoveIcon.parentNode.replaceChild(enabledIcon, oldMoveIcon);
    },

    _enableMoveDown = function (rowIdNum) {
        var oldMoveIcon = jQuery('#moveDownIcon' + rowIdNum)[0];
            enabledIcon = moveDownIcon.cloneNode(true);
        enabledIcon.id = 'moveDownIcon' + rowIdNum;
        oldMoveIcon.parentNode.replaceChild(enabledIcon, oldMoveIcon);
    },

    _deleteAnswer = function (event) {
        var rowIdNum = jQuery(event.target).closest('tr').data('rowIdNum');
        if (mod.addAnswer.rowIdNums.length > 1) {
            if (rowIdNum === mod.addAnswer.rowIdNums[0]) {
                _disableMoveUp(mod.addAnswer.rowIdNums[1]);
            } else if (rowIdNum === mod.addAnswer.rowIdNums[mod.addAnswer.rowIdNums.length - 1]) {
                // disable move down on new last row
                _disableMoveDown(mod.addAnswer.rowIdNums[mod.addAnswer.rowIdNums.length - 2]);
            }
        }

        var row = jQuery('#answerRow' + rowIdNum)[0];
        row.parentNode.removeChild(row);

        mod.addAnswer.rowIdNums.splice(_indexOfRow(rowIdNum), 1);
    },

    _moveAnswerUp = function (event) {
        var rowIdNum = jQuery(event.target).closest('tr').data('rowIdNum'),
            index = _indexOfRow(rowIdNum),
            previousRowId = mod.addAnswer.rowIdNums[index - 1],
            rowToMove = jQuery('#answerRow' + rowIdNum)[0],
            parent = rowToMove.parentNode,
            previousRow = jQuery('#answerRow' + previousRowId)[0];

        parent.removeChild(rowToMove);
        parent.insertBefore(rowToMove, previousRow);
        var save = mod.addAnswer.rowIdNums[index];
        mod.addAnswer.rowIdNums[index] = mod.addAnswer.rowIdNums[index - 1];
        mod.addAnswer.rowIdNums[index - 1] = save;
        if (index == 1) {
            // swapping with first row; update move up icons
            _disableMoveUp(rowIdNum);
            _enableMoveUp(previousRowId);
        }
        if (index == mod.addAnswer.rowIdNums.length - 1) {
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
            nextRow = jQuery('#answerRow' + nextRowId)[0];

        parent.removeChild(nextRow);
        parent.insertBefore(nextRow, rowToMove);
        var save = mod.addAnswer.rowIdNums[index];
        mod.addAnswer.rowIdNums[index] = mod.addAnswer.rowIdNums[index + 1];
        mod.addAnswer.rowIdNums[index + 1] = save;
        if (index == 0) {
            // this is the first row; update move up icons
            _enableMoveUp(rowIdNum);
            _disableMoveUp(nextRowId);
        }
        if (index == mod.addAnswer.rowIdNums.length - 2) {
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
        initList: function() {
            jQuery('#addSurveyBtn').click(function() {
                jQuery('#ajaxDialog').load('editDetails', function() {
                    jQuery('#inputForm').ajaxForm({'target' : '#ajaxDialog'});
                });
            });

            jQuery(document).on('yukonDetailsUpdated', function(event, newUrl) {
                jQuery('#ajaxDialog').dialog('close');
                window.location = newUrl;
            });
        },

        initEdit: function() {
            var editDetailsBtn = jQuery('#editDetailsBtn'),
                addQuestionBtn = jQuery('#addQuestionBtn');

             editDetailsBtn.click(function() {
                 var editDetailsUrl = editDetailsBtn.attr('data-detail-url');
                jQuery('#ajaxDialog').load(editDetailsUrl);
            });

             addQuestionBtn.click(function() {
                var addQuestionUrl = addQuestionBtn.attr('data-add-question-url');
                jQuery('#ajaxDialog').load(addQuestionUrl);
            });

            jQuery('.editQuestionBtn').click(function(event) {
                var editQuestionUrl = jQuery(event.target).closest('[data-edit-question-url]').attr('data-edit-question-url');
                jQuery('#ajaxDialog').load(editQuestionUrl);
            });

            jQuery(document).on('click', '.moveAnswerUp', _moveAnswerUp);
            jQuery(document).on('click', '.moveAnswerDown', _moveAnswerDown);
            jQuery(document).on('click', '.deleteAnswer', _deleteAnswer);

            jQuery(document).bind('yukonDetailsUpdated', closeAjaxDialogAndRefresh);
            jQuery(document).bind('yukonQuestionSaved', closeAjaxDialogAndRefresh);
        },

        addAnswer : function (answerKey) {
            var answerTable = jQuery('#answerTable tbody'),
                newRow = jQuery(document.createElement("tr"));
            answerTable.append(newRow);
            var newRowIdNum = mod.addAnswer.nextRowIdNum++;
            newRow.data('rowIdNum', newRowIdNum);
            newRow.attr("id", "answerRow" + newRowIdNum);
            var isFirstRow = mod.addAnswer.rowIdNums.length == 0;

            var keyCell = jQuery(document.createElement("td"));
            newRow.append(keyCell);
            var inputNode = document.createElement("input");
            inputNode.name = 'answerKeys';
            inputNode.type = 'text';
            inputNode.maxLength = 64;
            keyCell.append(inputNode);
            var actionsCell = jQuery(document.createElement("td"));
            newRow.append(actionsCell);

            var icon = (isFirstRow ? moveUpDisabledIcon : moveUpIcon).cloneNode(true);
            icon.id = 'moveUpIcon' + newRowIdNum;
            actionsCell.append(icon);

            actionsCell.append(document.createTextNode(' '));
            icon = moveDownDisabledIcon.cloneNode(true);
            icon.id = 'moveDownIcon' + newRowIdNum;
            actionsCell.append(icon);

            actionsCell.append(document.createTextNode(' '));
            icon = deleteAnswerIcon.cloneNode(true);
            actionsCell.append(icon);

            if (!isFirstRow) {
                _enableMoveDown(mod.addAnswer.rowIdNums[mod.addAnswer.rowIdNums.length - 1]);
            }
            mod.addAnswer.rowIdNums.push(newRowIdNum);

            if (answerKey) {
                inputNode.value = answerKey;
            } else {
                var inputElements = answerTable.find('input');
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
        },

        initOptOutSurveyList : function() {

            jQuery('.add-survey').click( function() {
                var button = jQuery(this),
                url = button.attr('data-add-url'),
                dialogTitle = button.closest('[data-dialog-title]').attr('data-dialog-title');
                openSimpleDialog('ajaxDialog', url, dialogTitle);
            });

            jQuery('.more-programs').click( function() {
                var link = jQuery(this),
                    url = link.attr('data-list-url'),
                    dialogTitle = link.closest('[data-dialog-title]').attr('data-dialog-title');
                    okText = link.closest('[data-ok-text]').attr('data-ok-text');

                openSimpleDialog(
                    'ajaxDialog',
                    url,
                    dialogTitle,
                    undefined,
                    undefined,
                    {
                      buttons: {
                          ok: {
                              text: okText,
                              click: function() {
                                  jQuery(this).dialog('close');
                              }
                          }
                      }
                    }
                );
            });
        },

        initEditQuestion : function() {
            _questionTypeChanged();
            jQuery('#questionType').change(Yukon.Surveys.questionTypeChanged);
            jQuery('#inputForm').ajaxForm({'target' : '#ajaxDialog'});
        },

        initWithAnswerKeys : function (answerKeys) {
            mod.addAnswer.nextRowIdNum = 0;
            mod.addAnswer.rowIdNums = [];
            if (answerKeys) {
                for (var index = 0; index < answerKeys.length; index++) {
                    mod.addAnswer(answerKeys[index]);
                }
            }
            jQuery('#questionKey').focus();
            jQuery('#questionKey').select();
        }
    };
    return mod;
}());