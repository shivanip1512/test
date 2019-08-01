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
                    clonedRow.attr('data-id', program.paoId);
                    clonedRow.find('.js-program-name').text(program.paoName);
                    clonedRow.find('.js-program-id').val(program.paoId).removeAttr('disabled');
                    clonedRow.find('.js-start-priority').removeAttr('disabled');
                    clonedRow.find('.js-stop-priority').removeAttr('disabled');
                    clonedRow.removeClass('dn js-template-row');
                    clonedRow.appendTo($('#js-assigned-programs'));
                    picker.disableItem(program.paoId);
                });
                $('#js-assigned-programs').closest('.select-box').find('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
                picker.clearEntireSelection();
                var rows = $('.js-assigned-program');
                yukon.ui.reindexRows(rows);
            });
            
            $(document).on('click', '.js-remove', function () {
                var picker = yukon.pickers['js-avaliable-programs-picker'],
                    selectedId = $(this).parent().attr('data-id');
                $(this).parent().remove();
                $('#js-assigned-programs').closest('.select-box').find('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
                picker.enableItem(selectedId);
                var rows = $('.js-assigned-program');
                yukon.ui.reindexRows(rows);
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
                var number = _timeFormatter.parse24HourTime($(this).val());
                $('#dailyStartTimeInMinutes').val(number);
            });
            
            $(document).on('change', '#dailyStopTime', function () {
                var number = _timeFormatter.parse24HourTime($(this).val());
                $('#dailyStopTimeInMinutes').val(number);
            });
            
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.dr.setup.controlArea.init(); });