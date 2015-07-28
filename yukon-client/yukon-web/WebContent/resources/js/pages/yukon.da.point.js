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
    
    var updateReasonability = function (event) {
        
        var checkbox = $(event.currentTarget);
        
        var toggleGroup = checkbox.data('toggle');
        var input = $('[data-toggle-group="' + toggleGroup + '"]');
        
        if (checkbox.is(':checked')) {
            if (Math.abs(input.val()) > 1e29) {
                input.val(0);
            }
        }
    };
    
    var updateFdrInterface = function (translationNumber) {
        
        var allProperties = $('[data-fdr-translation="' + translationNumber +'"]');
        var fdrInterface = allProperties.find('.js-fdr-interface').val();
        
        var pointType = $('.js-point-type').val();
        $.ajax(yukon.url('/capcontrol/fdr/' + fdrInterface + '?point-type=' + pointType))
        .done(function (data) {
            
            var directionSelect = allProperties.find('.js-fdr-direction');
            directionSelect.find('option').prop('disabled', true);
            
            data.directions.forEach(function (direction) {
                directionSelect.find('option[value="' + direction + '"]').prop('disabled', false);
            });
            
            if (directionSelect.find('option:selected').prop('disabled')) {
                directionSelect.val(data.directions[0]);
            }
            
            var translationFields = allProperties.find('.js-translation-fields');
            translationOptions.empty();
            
            data.translations.forEach(function (field) {

                var optionInput = $('<input>').attr('size', 5);
                if (field.options !== undefined) {
                    optionInput = $('<select>');
                    field.options.forEach(function (option) {
                        var thing = $('<option>').val(option).text(option);
                        optionInput.append(thing);
                    });
                }
                optionInput.attr('name', field.name);
                optionInput.val(field.value);
                
                var li = $('<li>');
                
                $('<span class="name">').text(field.name)
                    .append(':')
                    .appendTo(li);
                
                li.append('&nbsp;');

                $('<span class="value">').append(optionInput)
                    .append(';')
                    .appendTo(li);

                if (field.hidden) li.addClass('dn');
                
                translationField.append(li);
            });
            
            makeTranslation(translationNumber);
            
        });
    };
    
    var makeTranslation = function(translationNumber) {
        
        var allProperties = $('[data-fdr-translation="' + translationNumber +'"]');
        
        var finalInput = allProperties.find('.js-fdr-translation');
        var optionsHolder = allProperties.find('.js-translation-options');
        
        var translationString = '';
        
        optionsHolder.find(':input').each(function (idx, input) {
           input = $(input);
           
           translationString += input.attr('name');
           translationString += ':';
           translationString += input.val();
           translationString += ';';
        });
        
        finalInput.val(translationString);
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
            
            $('.js-reasonability').on('change', updateReasonability);
            
            $('.js-fdr-interface').on('change', function () {
                var elem = $(this);
                var number = elem.closest('[data-fdr-translation]').data('fdrTranslation');
                updateFdrInterface(number);
            });
            
            $(document).on('input', '.js-translation-options :input', function () {
                var elem = $(this);
                var number = elem.closest('[data-fdr-translation]').data('fdrTranslation');
                makeTranslation(number);
            });
            
            var tabContainer = $('.tabbed-container');
            
            tabContainer.tabs('option','activate', function (ev, ui){
                window.sessionStorage.setItem('yukon:da:points:tab', ui.newTab.index());
            });
            
            tabContainer.tabs('option', 'active', window.sessionStorage.getItem('yukon:da:points:tab'));
            
            
            var errorEncountered = false;
            $('.ui-tabs-panel').each(function (idx, elem) {
                elem = $(elem);
                if (elem.find('.error').length) {
                    var id = elem.attr('id');
                    var link = $('[href="#' + id + '"]');
                    link.closest('li').addClass('error');
                    if (! errorEncountered) {
                        tabContainer.tabs('option', 'active', idx);
                        errorEncountered = true;
                    }
                }
            });
            
            
        }
        
    };
    
    return mod;
}());

$(function () { yukon.da.point.init(); });