yukon.namespace('yukon.da.point');

/**
 * Module for the volt/var point editor page
 * @module yukon.da.point
 * @requires JQUERY
 * @requires yukon
 */
yukon.da.point = (function () {
    
    'use strict';
    
    var updateArchive = function () {
        var archiveType = $('.js-archive-type').val();
        
        /* TODO these enum names should come from the controller */
        var intervalNeeded = archiveType === 'ON_TIMER' || archiveType === 'ON_TIMER_OR_UPDATE';
        
        $('.js-archive-interval').toggle(intervalNeeded);
        
    };
    
    var updateStateGroup = function () {
        var stateGroup = $('.js-state-group').val();
        
        $.ajax(yukon.url('/capcontrol/state-group/' + stateGroup + '/states')).done(function (data) {
            
            var select = $('.js-initial-state');
            select.empty();
            
            data.forEach(function (state) {
                var option = $('<option>').text(state.stateText).val(state.liteID);
                
                select.append(option);
            });
        });
        
    };

    var updateCalc = function () {
        var updateType = $('.js-calc-update-type').val();
        
        /* TODO these enum names should come from the controller */
        var periodNeeded = updateType === 'ON_TIMER' || updateType === 'ON_TIMER_AND_CHANGE';
        
        $('.js-calc-period').toggle(periodNeeded);
        
    };
    
    var updateAnalogControl = function () {
        var controlType = $('.js-analog-control-type:checked').val();
        
        /* TODO these enum names should come from the controller */
        var inputsNeeded = controlType !== 'None';
        
        $('.js-analog-control-input').toggle(inputsNeeded);
    };
    
    var updateStatusControl = function () {
        var controlType = $('.js-status-control-type').val();
        
        /* TODO these enum names should come from the controller */
        var inputsNeeded = controlType !== 'None';
        
        $('.js-status-control-input').toggle(inputsNeeded);
    };
    
    var updateStaleData = function () {
        var enabled = $('.js-stale-data-enabled').is(':checked');
        
        $('.js-stale-data-input').toggle(enabled);
    };
    
    var updateFdrDirections = function (translationNumber) {
        
        var allProperties = $('[data-fdr-translation="' + translationNumber +'"]');
        var fdrInterface = allProperties.find('.js-fdr-interface').val();
        
        $.ajax(yukon.url('/capcontrol/fdr/' + fdrInterface + '/directions')).done(function (data) {
            
            var select = allProperties.find('.js-fdr-direction');
            select.find('option').prop('disabled', true);
            data.forEach(function (direction) {
                select.find('option[value="' + direction + '"]').prop('disabled', false);
            });
            
            if (select.find('option:selected').prop('disabled')) {
                select.val(data[0]);
            }
            
        });
        var pointType = $('.js-point-type').val();
        $.ajax(yukon.url('/capcontrol/fdr/' + fdrInterface + '/translation?point-type=' + pointType))
        .done(function (data) {
            
            var input = allProperties.find('.js-fdr-translation');
            input.val(data).attr('size', data.length);
            
        });
        
    };
    
    var mod = {
        
        /** Initialize this module. */
        init: function () {
            
            $('.js-state-group').on('change', updateStateGroup);

            updateArchive();
            $('.js-archive-type').on('change', updateArchive);
            updateCalc();
            $('.js-calc-update-type').on('change', updateCalc);
            updateAnalogControl();
            $('.js-analog-control-type').on('change', updateAnalogControl);
            updateStatusControl();
            $('.js-status-control-type').on('change', updateStatusControl);
            updateStaleData();
            $('.js-stale-data-enabled').on('change', updateStaleData);
            
            $('.js-fdr-interface').on('change', function (opt) {
                var elem = $(this);
                var number = elem.closest('[data-fdr-translation]').data('fdrTranslation');
                updateFdrDirections(number);
            });
            
        }
        
    };
    
    return mod;
}());

$(function () { yukon.da.point.init(); });