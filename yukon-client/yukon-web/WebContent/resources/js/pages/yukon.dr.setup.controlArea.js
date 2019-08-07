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
    
    _enableDisableTriggerCreate = function () {
        $('.js-create-trigger').removeAttr('disabled');
        var nbrTriggers = $('.js-triggers').length;
        if (nbrTriggers > 1) {
            $('.js-create-trigger').attr('disabled', 'disabled');
        }
        $('.js-no-triggers').toggleClass('dn', nbrTriggers > 0);
        
    },
    
    _updateStartTime = function () {
        var start = _timeFormatter.parse24HourTime($('#dailyStartTime').val());
        $('#dailyStartTimeInMinutes').val(start);
    },
    
    _updateStopTime = function () {
        var stop = _timeFormatter.parse24HourTime($('#dailyStopTime').val());
        $('#dailyStopTimeInMinutes').val(stop);
    }

    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            _enableDisableTriggerCreate();
            
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
            
            $(document).on('yukon:trigger:delete', function (ev) {
                var container = $(ev.target),
                    triggerId = container.data('id'),
                    selectedRow = $('.js-trigger-row-' + triggerId);
                selectedRow.remove();
                _enableDisableTriggerCreate();
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
                if (!useControlWindow) {
                    $('#dailyStartTimeInMinutes').val(0);
                    $('#dailyStopTimeInMinutes').val(0);
                } else {
                    _updateStartTime();
                    _updateStopTime();
                }
            });
            
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.dr.setup.controlArea.init(); });