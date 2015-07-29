yukon.namespace('yukon.da.point');

/**
 * Module for the volt/var point editor page
 * @module yukon.da.point
 * @requires JQUERY
 * @requires yukon
 */
yukon.da.point = (function () {
    
    'use strict';

    /**
     * Updates initial state options to those for the selected state group
     */
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

    /**
     * Shows and hides archive interval field based on Archive Type selected
     */
    var updateArchive = function () {
        var archiveType = $('.js-archive-type').val();
        
        /* TODO these enum names should come from the controller */
        var intervalNeeded = archiveType === 'ON_TIMER' || archiveType === 'ON_TIMER_OR_UPDATE';
        
        $('.js-archive-interval').toggle(intervalNeeded);
        
    };

    /**
     * Shows and hides calc interval field based on Calc Type selected
     */
    var updateCalc = function () {
        var updateType = $('.js-calc-update-type').val();
        
        /* TODO these enum names should come from the controller */
        var periodNeeded = updateType === 'ON_TIMER' || updateType === 'ON_TIMER_AND_CHANGE';
        
        $('.js-calc-period').toggle(periodNeeded);
        
    };

    /**
     * Shows and hides analog control fields based on control type selected
     */
    var updateAnalogControl = function () {
        var controlType = $('.js-analog-control-type:checked').val();
        
        /* TODO these enum names should come from the controller */
        var inputsNeeded = controlType !== 'None';
        
        $('.js-analog-control-input').toggle(inputsNeeded);
    };

    /**
     * Shows and hides status control fields based on control type selected
     */
    var updateStatusControl = function () {
        var controlType = $('.js-status-control-type').val();
        
        /* TODO these enum names should come from the controller */
        var inputsNeeded = controlType !== 'None';
        
        $('.js-status-control-input').toggle(inputsNeeded);
    };

    /**
     * When stale data is enabled, show the subfields. otherwise, hide them.
     */
    var updateStaleData = function () {
        var enabled = $('.js-stale-data-enabled').is(':checked');
        
        $('.js-stale-data-input').toggle(enabled);
    };

    /**
     * When enabling reasonability, make the value 0 rather than the previous value (magnitude 1e30)
     */
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

    /**
     * Update the Direction options and Translation fields based on the interface type chosen.
     * 
     * @param {number} translationNumber - the index of the translation to update
     */
    var updateFdrInterface = function (translationNumber) {
        
        var row = $('[data-fdr-translation="' + translationNumber +'"]');
        var fdrInterface = row.find('.js-fdr-interface').val();
        var pointType = $('.js-point-type').val();
        
        $.ajax(yukon.url('/capcontrol/fdr/' + fdrInterface + '?point-type=' + pointType))
        .done(function (data) {

            /* Enable only the valid directions */
            var directionSelect = row.find('.js-fdr-direction');
            directionSelect.find('option').prop('disabled', true);
            
            data.directions.forEach(function (direction) {
                directionSelect.find('option[value="' + direction + '"]').prop('disabled', false);
            });
            
            if (directionSelect.find('option:selected').prop('disabled')) {
                directionSelect.val(data.directions[0]);
            }
            
            var translationFields = row.find('.js-translation-fields');
            //Start with a fresh list of translation fields
            translationFields.empty();

            data.translations.forEach(function (field) {

                var optionInput = $('<input>').attr('size', field.value.length);

                //Some fields have an option list, rather than a text field.
                if (field.options !== undefined) {
                    optionInput = $('<select>');
                    field.options.forEach(function (option) {
                        $('<option>').val(option).text(option).appendTo(optionInput);
                    });
                }

                optionInput.attr('name', field.name);
                optionInput.val(field.value);

                var li = $('<li>');

                li.append(
                    $('<span class="name">').text(field.name)
                    .append(':')
                );

                li.append('&nbsp;');

                li.append(
                    $('<span class="value">').append(optionInput)
                    .append(';')
                );

                if (field.hidden) li.addClass('dn');

                translationFields.append(li);
            });

            //Setup the true translation field 
            makeTranslation(translationNumber);

        });
    };

    /**
     * Combine all the translation fields into the true (hidden) input
     * 
     * @param {number} translationNumber - the index of the translation to update
     */
    var makeTranslation = function(translationNumber) {

        var row = $('[data-fdr-translation="' + translationNumber +'"]');

        var finalInput = row.find('.js-fdr-translation');
        var optionsHolder = row.find('.js-translation-fields');

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

    /**
     * Show the first hidden fdr translation and set up its fields
     */
    var addFdr = function () {
        var hiddenRows = $('[data-fdr-translation]').not(':visible');
        var newRow = hiddenRows.eq(0);
        var number = newRow.data('fdrTranslation');
        updateFdrInterface(number);
        newRow.removeClass('dn');

        $('.js-fdr-empty').addClass('dn');

        if (hiddenRows.length <= 1) {
            $('.js-add-fdr').addClass('dn');
        }
    };

    /**
     * Hides the selected row and clears its tranlsation
     * @param {event} Click event within the row
     */
    var removeFdr = function (event) {
        var buttonClicked = $(event.currentTarget);
        var row = buttonClicked.closest('[data-fdr-translation]');
        row.find('.js-fdr-translation').val('');
        row.addClass('dn');

        $('.js-add-fdr').removeClass('dn');

        var visibleRows = $('[data-fdr-translation]').filter(':visible');

        if (visibleRows.length < 1) {
            $('.js-fdr-empty').removeClass('dn');
        }
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

            $('.js-add-fdr').on('click', addFdr);

            $('.js-remove-fdr').on('click', removeFdr);

            $('.js-fdr-interface').on('change', function () {

                var elem = $(this);
                var number = elem.closest('[data-fdr-translation]').data('fdrTranslation');
                updateFdrInterface(number);
            });

            $(document).on('input', '.js-translation-fields :input', function () {

                var elem = $(this);
                var number = elem.closest('[data-fdr-translation]').data('fdrTranslation');

                //Make the input big enough for typing
                var currentSize = +elem.attr('size');
                if (currentSize < elem.val().length) {
                    elem.attr('size', currentSize + 5);
                }
                makeTranslation(number);
            });

            /* Save tab selection */
            var tabContainer = $('.tabbed-container');

            tabContainer.tabs('option','activate', function (ev, ui){
                window.sessionStorage.setItem('yukon:da:points:tab', ui.newTab.index());
            });

            tabContainer.tabs('option', 'active', window.sessionStorage.getItem('yukon:da:points:tab'));

            /* If there was an error in a a field, go to the first tab with an error */
            var errorEncountered = false;
            $('.ui-tabs-panel').each(function (idx, elem) {
                elem = $(elem);
                if (elem.find('.error').length) {
                    var id = elem.attr('id');
                    var link = $('[href="#' + id + '"]');
                    link.closest('li').addClass('error');
                    if (!errorEncountered) {
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