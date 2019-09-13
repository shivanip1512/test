yukon.namespace('yukon.dr.setup.controlArea');

/**
 * Module that handles the behavior on the DR setup Control Area page.
 * @module yukon.dr.setup.controlArea
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.setup.controlArea = (function() {
    
    'use strict';
    
    var
    _initialized = false,
    
    _timeFormatter = yukon.timeFormatter,
    
    _updateStartTime = function () {
        var dailyStartTime = $('#dailyStartTime').val(),
            start = "";
        if (dailyStartTime != "") {
            start = _timeFormatter.parse24HourTime($('#dailyStartTime').val());
        }
        $('#dailyStartTimeInMinutes').val(start);
    },
    
    _updateStopTime = function () {
        var dailyStopTime = $('#dailyStopTime').val(),
            stop = "";
        if (dailyStopTime != "") {
            stop = _timeFormatter.parse24HourTime($('#dailyStopTime').val());
        }
        $('#dailyStopTimeInMinutes').val(stop);
    },
    
     _clearErrors = function() {
        $(".js-trigger-identification-error").addClass("dn");
        $(".js-peak-tracking-error").addClass("dn");
        $(".js-threshold-setting-error").addClass("dn");
    },

    _renderConfirmDeletePopup = function(triggerId, triggerName){
        yukon.dialogConfirm.add({
            'on': '#delete-trigger-'+triggerId,
            'strings':{
                'title': yg.text.confirmRemoveTitle,
                'message': yg.text.confirmRemoveMessage.replace("{0}", $('<span />').text(triggerName).html()),
                'ok': yg.text.remove,
                'cancel': yg.text.cancel,
            }
        });
    },

    _setTriggerType = function(triggerType){
        $("div.ui-dialog:visible").find("#trigger-type").val(triggerType);
    },
    _checkForTrigger = function(triggerId) {
        var selector = "#js-trigger-" + triggerId;
        var length = $(".js-trigger-container").children(selector).length;
        return length;
    },
    
    _validatePickerValues = function(triggerType) {
        var errorFlag = false,
            nonSelected = "(none selected)",
            triggerPointName = $("#point-trigger-identification-name").val(),
            usePeakTracking = $("#js-use-peak-tracking").prop('checked');

        if (triggerPointName.indexOf(nonSelected) != -1) {
            errorFlag = true;
            $(".js-trigger-identification-error").removeClass("dn");
        }
        if (usePeakTracking && triggerType !== 'STATUS') {
            if (thresholdPointPeakTracking.selectionLabel.innerText.indexOf(nonSelected) != -1) {
                errorFlag = true;
                $(".js-peak-tracking-error").removeClass("dn");
            }
        }
        if (triggerType === "THRESHOLD_POINT") {
            if (thresholdPointThresholdSettings.selectionLabel.innerText.indexOf(nonSelected) != -1) {
                errorFlag = true;
                $(".js-threshold-setting-error").removeClass("dn");
            }
        }
        return errorFlag;
    },
    _enableDisableTriggerCreate = function (){
        var disableCreateButton = $('.js-trigger-container > div[data-trigger-id]').length < 2 ? false : true;
        $(".js-create-trigger").prop('disabled', disableCreateButton);
    }

    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $(document).ready(_enableDisableTriggerCreate());
            
            if ($("#js-inline-picker-container").is(":visible")) {
                yukon.pickers['js-avaliable-programs-picker'].show();
            } else {
                $("#js-assigned-programs-table").scrollTableBody({rowsToDisplay: 20});
            }
            
            $(document).on('click', '.js-add-program', function () {
                var picker = yukon.pickers['js-avaliable-programs-picker'];
                
                picker.selectedItems.forEach(function (program) {
                    var clonedRow = $('.js-template-row').clone();
                    clonedRow.find('.js-program-name').text(program.paoName);
                    clonedRow.find('.js-program-id').val(program.paoId).removeAttr('disabled');
                    clonedRow.find('.js-remove').attr('data-id', program.paoId);
                    var startPriority = clonedRow.find('.js-start-priority'),
                        minValue = startPriority.data('minValue'),
                        maxValue = startPriority.data('maxValue');
                    startPriority.removeAttr('disabled');
                    startPriority.val(1);
                    startPriority.spinner({
                        minValue: minValue,
                        maxValue: maxValue
                    })
                    var stopPriority = clonedRow.find('.js-stop-priority'),
                        minValue = stopPriority.data('minValue'),
                        maxValue = stopPriority.data('maxValue');
                    stopPriority.removeAttr('disabled');
                    stopPriority.val(1);
                    stopPriority.spinner({
                        minValue: minValue,
                        maxValue: maxValue
                    })
                    clonedRow.removeClass('dn js-template-row');
                    clonedRow.appendTo($('#program-assignments'));
                    picker.disableItem(program.paoId);
                });
                picker.clearEntireSelection();
                yukon.ui.reindexInputs('#program-assignments');
            });
            
            $(document).on('click', '.js-remove', function () {
                var picker = yukon.pickers['js-avaliable-programs-picker'],
                    selectedId = $(this).attr('data-id');
                $(this).closest('tr').remove();
                picker.enableItem(selectedId);
                yukon.ui.reindexInputs('#program-assignments');
            });
            
            $(document).on('yukon:controlArea:delete', function () {
                yukon.ui.blockPage();
                $('#delete-controlArea-form').submit();
            });
            
            $(document).on('change', '#dailyStartTime', function() {
                _updateStartTime();
            });
            
            $(document).on('change', '#dailyStopTime', function () {
                _updateStopTime();
            });
            
            $(document).on('click', '.js-control-window', function() {
                var controlWindowRow = $(this).closest('tr'),
                    useControlWindow = controlWindowRow.find('.switch-btn-checkbox').prop('checked');
                $('#dailyStartTimeInMinutes').prop('disabled', !useControlWindow);
                $('#dailyStopTimeInMinutes').prop('disabled', !useControlWindow);
            });
            
            $(document).on("yukon:trigger:delete", function (ev) {
                var container = $(ev.target),
                triggerId = container.data('id');
                $("#js-trigger-" + triggerId).remove();
                $("#js-trigger-dialog-" + triggerId).remove();
                var flag = $(".js-create-trigger").prop('disabled');
                if (flag) {
                    $(".js-create-trigger").prop('disabled', false);
                }
                if ($(".js-trigger-container > div").length === 1) {
                    $(".js-no-triggers").removeClass("dn");
                }
                $.ajax({
                    url: yukon.url('/dr/setup/controlArea/trigger/remove/' + triggerId),
                    type: 'delete'
                });
            });
            $(document).on('click', '.js-save', function() {
                var triggerIds = [];

                $('.js-trigger-container > div').get().forEach(function(item){
                    if ($(item).data('trigger-id'))
                        triggerIds.push($(item).data('trigger-id'));
                });
                $("#js-trigger-ids").val(triggerIds);
                $("#js-control-area-form").submit();
            });

            $(document).on("yukon:dr:setup:controlArea:saveTrigger", function (event) {
                _clearErrors();
                var dialog = $(event.target),
                    container = $("div.ui-dialog:visible"),
                    triggerType = $("#js-trigger-type option:selected").val();
                
                container.find("#point-trigger-identification-name").val(triggerIdentification.selectionLabel.innerText);

                if(triggerType === undefined){
                    triggerType = container.find("#triggerType").val();
                }

                if (_validatePickerValues(triggerType)) {
                    return;
                }

                var triggerId = $(event.target).data("triggerId"),
                    newTrigger = false;
                if(triggerId === undefined){
                    newTrigger = true;
                }
                container.find("#js-trigger-id").val(triggerId);
                container.find('#js-controlArea-trigger-form').ajaxSubmit({
                    success: function (data) {
                        if(_checkForTrigger(data.triggerId) == 0){
                            var triggerName = data.triggerName,
                                triggerId = data.triggerId,
                                clonedRow = $(".js-trigger-template-row").clone(),
                                anchorTag = $("<a>"),
                                url = yukon.url("/dr/setup/controlArea/trigger/" + triggerId),
                                mode = $("#js-form-edit-mode").val(),
                                url = yukon.url("/dr/setup/controlArea/renderTrigger/" + triggerId + "?mode=" + mode);

                            anchorTag.attr("href", url);
                            anchorTag.attr("class", "js-trigger-link");
                            anchorTag.attr("id", "js-trigger-link-"+triggerId);
                            anchorTag.attr("data-trigger-id", triggerId);
                            anchorTag.text(triggerName);

                            clonedRow.find(".js-trigger-name").append(anchorTag);
                            clonedRow.attr("id", "js-trigger-" + triggerId);
                            clonedRow.attr("data-trigger-id", triggerId);
                            clonedRow.removeClass("js-trigger-template-row dn");
                            $(".js-no-triggers").addClass("dn");
                            $(".js-trigger-container").append(clonedRow);
                            _enableDisableTriggerCreate();

                            clonedRow.find(".select-box-item-remove").attr("id", "delete-trigger-" + triggerId);
                            clonedRow.find(".select-box-item-remove").attr("data-id", triggerId);

                            var clonedDialog = $(".js-trigger-dialog-template").clone();
                            clonedDialog.attr("id", "js-trigger-dialog-" + triggerId);
                            clonedDialog.attr("data-target","#js-trigger-link-" + triggerId)
                            clonedDialog.attr("data-url", url);
                            clonedDialog.attr("data-title", triggerName);
                            clonedDialog.removeClass("js-trigger-dialog-template");
                            $(".js-trigger-container").append(clonedDialog);

                            anchorTag.attr("data-popup", "#js-trigger-dialog-" + triggerId);
                        } else {
                            $("#js-trigger-link-" + data.triggerId).text(data.triggerName);
                        }
                        _renderConfirmDeletePopup(data.triggerId, data.triggerName);
                        if(!newTrigger){
                            $("#js-trigger-dialog-" + data.triggerId).dialog('close');
                            $("#js-trigger-dialog-" + data.triggerId).empty()
                        }
                    }
                });
                if(newTrigger){
                    dialog.dialog('close');
                    dialog.empty();
                }
            });
            $(document).on('click', '.js-trigger-link', function(event) {
            	$('#js-controlArea-trigger-form').html('');
                event.preventDefault();
            });
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.dr.setup.controlArea.init(); });