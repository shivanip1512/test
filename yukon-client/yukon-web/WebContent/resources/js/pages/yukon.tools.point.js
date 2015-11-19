yukon.namespace('yukon.tools.point');

/**
 * Module for the point editor page
 * @module yukon.tools.point
 * @requires JQUERY
 * @requires yukon
 */
yukon.tools.point = (function () {
    
    'use strict';

    /**
     * Updates initial state options to those for the selected state group
     */
    var updateStateGroup = function () {
        var stateGroup = $('.js-state-group').val();
        
        $.ajax(yukon.url('/tools/state-group/' + stateGroup + '/states')).done(function (data) {
            
            var select = $('.js-initial-state');
            select.empty();
            
            data.forEach(function (state) {
                var option = $('<option>').text(state.stateText).val(state.liteID);
                
                select.append(option);
            });
        });
    };
    
    var _setOffsetDisabled = function (disabled) {
        $('.js-point-offset').prop('disabled', disabled);
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
        var periodNeeded = updateType === 'On Timer' || updateType === 'On Timer+Change';
        
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
     * Shows and hides status control fields based on control type selected
     */
    var updateLimits1 = function () {
        var enabled = $('.js-limit-one-enabled').is(':checked');

        $('.js-limit-one-input').toggle(enabled);
    };

    /**
     * Shows and hides status control fields based on control type selected
     */
    var updateLimits2 = function () {
        var enabled = $('.js-limit-two-enabled').is(':checked');

        $('.js-limit-two-input').toggle(enabled);
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
     * When disabling Physical Point Offset, make the value 0
     */
    var updatePointOffset = function (event) {
        
        var checkbox = $(event.currentTarget);
        
        var toggleGroup = checkbox.data('toggle');
        var input = $('[data-toggle-group="' + toggleGroup + '"]');
        
        if (!checkbox.is(':checked')) {
                input.val(0);
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
        
        $.ajax(yukon.url('/tools/fdr/' + fdrInterface + '?point-type=' + pointType))
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
        	var _mode = $('.js-page-mode').val();
        	if (_mode !== 'VIEW') {
                 var useOffset = $('.js-use-offset').prop('checked');
                 
                 if (useOffset) {
                	 _setOffsetDisabled(false);
                 }else{
                	 _setOffsetDisabled(true);
                 }
             }
            $('.js-state-group').on('change', updateStateGroup);

            updateArchive();
            $('.js-archive-type').on('change', updateArchive);
            updateCalc();
            $('.js-calc-update-type').on('change', updateCalc);
            updateAnalogControl();
            $('.js-analog-control-type').on('change', updateAnalogControl);
            updateStatusControl();
            $('.js-status-control-type').on('change', updateStatusControl);
            updateLimits1();
            $('.js-limit-one-enabled').on('change', updateLimits1);
            updateLimits2();
            $('.js-limit-two-enabled').on('change', updateLimits2);
            updateStaleData();
            $('.js-stale-data-enabled').on('change', updateStaleData);

            $('.js-reasonability').on('change', updateReasonability);
            $('.js-use-offset').on('change', updatePointOffset);

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

            $(document).on('yukon:da:point:delete', function () {
                $('#delete-point').submit();
            });
        }
    };

    return mod;
}());

$(function () { yukon.tools.point.init(); });