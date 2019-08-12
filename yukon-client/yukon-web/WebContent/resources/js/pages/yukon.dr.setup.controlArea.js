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
        var start = _timeFormatter.parse24HourTime($('#dailyStartTime').val());
        $('#dailyStartTimeInMinutes').val(start);
    },
    
    _updateStopTime = function () {
        var stop = _timeFormatter.parse24HourTime($('#dailyStopTime').val());
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
                'message': yg.text.confirmRemoveMessage.replace("{0}", triggerName),
                'ok': yg.text.remove,
                'cancel': yg.text.cancel,
            }
        });
    },

    _setTriggerType = function(triggerType){
        $("#trigger-type").val(triggerType);
    },
    
    _validatePickerValues = function(thresholdType) {
        var errorFlag = false,
            nonSelected = "(none selected)",
            triggerPointName = $("#point-trigger-identification-name").val(),
            usePeakTrackingThresholdPoint = $("#js-use-peak-tracking-threshold-point .button.yes").hasClass('on'),
            usePeakTrackingThreshold = $("#js-use-peak-tracking-threshold .button.yes").hasClass('on');

        if (triggerPointName.indexOf(nonSelected) != -1) {
            errorFlag = true;
            $(".js-trigger-identification-error").removeClass("dn");
        }
        if (usePeakTrackingThresholdPoint && thresholdType === "THRESHOLD_POINT") {
            if (thresholdPointPeakTracking.selectionLabel.innerText.indexOf(nonSelected) != -1) {
                errorFlag = true;
                $(".js-peak-tracking-error").removeClass("dn");
            }
        }
        if (usePeakTrackingThreshold && thresholdType === "THRESHOLD") {
            if (thresholdPeakTracking.selectionLabel.innerText.indexOf(nonSelected) != -1) {
                errorFlag = true;
                $(".js-peak-tracking-error").removeClass("dn");
            }
        }
        if (thresholdType === "THRESHOLD_POINT") {
            if (thresholdPointThresholdSettings.selectionLabel.innerText.indexOf(nonSelected) != -1) {
                errorFlag = true;
                $(".js-threshold-setting-error").removeClass("dn");
            }
        }
        return errorFlag;
    },
    _enableDisableTriggerCreate = function (){
        if($(".js-trigger-container > div").length >= 3){
            $(".js-create-trigger").prop('disabled', true);
        }else{
            $(".js-create-trigger").prop('disabled', false);
        }
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
                var dialog = $(event.target);
                if (($("#js-trigger-type option:selected").val() === "THRESHOLD_POINT") || 
                        ($(this).find("#trigger-type").val() === "THRESHOLD_POINT")) {
                    $("#point-trigger-identification-name").val(thresholdPointTriggerIdentification.selectionLabel.innerText);
                    $("#min-restore-offset").val($("#js-threshold-point-min-restore-offset").val());
                    _setTriggerType("THRESHOLD_POINT");
                    if (_validatePickerValues("THRESHOLD_POINT")) {
                        return;
                    }
                }
                if (($("#js-trigger-type option:selected").val() === "THRESHOLD") || 
                        ($(this).find("#trigger-type").val() === "THRESHOLD")) {
                    $("#point-trigger-identification-name").val(thresholdTriggerIdentification.selectionLabel.innerText);
                    $("#min-restore-offset").val($("#js-threshold-min-restore-offset").val());
                    _setTriggerType("THRESHOLD");
                    if(_validatePickerValues("THRESHOLD")){
                        return;
                    }
                    $("#point-trigger-identification-name").val(thresholdTriggerIdentification.selectionLabel.innerText);
                    $("#trigger-type").val("THRESHOLD");
                }
                if (($("#js-trigger-type option:selected").val() === "STATUS") || 
                        ($(this).find("#trigger-type").val() === "STATUS")) {
                    $("#point-trigger-identification-name").val(statusTriggerIdentification.selectionLabel.innerText);
                    _setTriggerType("STATUS");
                    if (_validatePickerValues("STATUS")) {
                        return;
                    }
                    $("#point-trigger-identification-name").val(statusTriggerIdentification.selectionLabel.innerText);
                    $("#trigger-type").val("STATUS");
                }
                var triggerId = $(event.target).data("triggerId");
                if(!triggerId){
                    dialog.dialog('close');
                }
                $("#js-trigger-id").val(triggerId);
                $('#js-controlArea-trigger-form').ajaxSubmit({
                    success: function (data) {
                        var triggerId = data.triggerId, 
                        dataId = $(".js-trigger-container").find("#js-trigger-"+triggerId).data('trigger-id');
                    if(!dataId){
                        var triggerName = data.triggerName,
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
                        
                        _renderConfirmDeletePopup(triggerId, triggerName);

                        var clonedDialog = $(".js-trigger-dialog-template").clone();
                        clonedDialog.attr("id", "js-trigger-dialog-" + triggerId);
                        clonedDialog.attr("data-target","#js-trigger-link-" + triggerId)
                        clonedDialog.attr("data-url", url);
                        clonedDialog.attr("data-title", triggerName);
                        clonedDialog.removeClass("js-trigger-dialog-template");
                        $(".js-trigger-container").append(clonedDialog);

                        anchorTag.attr("data-popup", "#js-trigger-dialog-" + triggerId);
                    }
                    $("#js-trigger-dialog-"+dataId).dialog('close');
                    }
                });
            });
            $(document).on('click', '.js-trigger-link', function(event) {
                event.preventDefault();
            });
            $(document).on('click', '#js-cancel-btn', function (event) {
                window.history.back();
            });
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.dr.setup.controlArea.init(); });