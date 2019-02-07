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
        if(!inputsNeeded){
        	$('.js-reset-field').val(0);
        }
        $('.js-analog-control-input').toggle(inputsNeeded);
    };

    /**
     * Shows and hides status control fields based on control type selected
     */
    var updateStatusControl = function () {
        var controlType = $('.js-status-control-type').val();

        /* TODO these enum names should come from the controller */
        var inputsNeeded = controlType !== 'None';
        if(!inputsNeeded){
        	$('.js-reset-field').val(0);
        }
        $('.js-status-control-input').toggle(inputsNeeded);
    };
    
    /**
     * Shows and hides status control fields based on control type selected
     */
    var updateLimits1 = function () {
        var visible = $('.js-limit-one-enabled').is(':visible');
        var enabled = $('.js-limit-one-enabled').is(':checked');
        if(visible && !enabled){
        	$('.js-reset-field').val(0);
        }
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
        if(!enabled){
            $('.js-reset-field-time').val(5);
        }
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
            var directionSelect = row.find('.js-fdr-direction');
            
            enableValidDirections(data, row);
            
            var translationFields = row.find('.js-translation-fields');
            //Start with a fresh list of translation fields
            translationFields.empty();

            data.translations.forEach(function (field) {

                var optionInput = $('<input>').attr('size', field.value.length);
                optionInput.attr('type', 'text');

                //Some fields have an option list, rather than a text field.
                if (field.options !== undefined) {
                    optionInput = $('<select>');
                    field.options.forEach(function (option) {
                        $('<option>').val(option).text(option).appendTo(optionInput);
                    });
                }

                optionInput.attr('name', field.name);
                if(field.maxLength !== undefined) {
                   optionInput.attr('maxlength', field.maxLength);
                }
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
            
            if (directionSelect.hasClass('error')) {
                translationFields.find('select:visible').addClass('error');
                translationFields.find('input:visible').addClass('error');
                var errorMessageSpanSelector = "#pointBase\\.pointFDRList" + translationNumber + "\\.interfaceType\\.errors";
                $(errorMessageSpanSelector).insertAfter(translationFields);
            } else {
                translationFields.find('select:visible').removeClass('error');
                translationFields.find('input:visible').removeClass('error');
            }

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
    
    var addCalc = function () {
        var calcTable = $('#calculationTable'),
            rows = calcTable.find('tr').length - 1,
            pointId = $('#pointId').val();

        $.ajax(yukon.url('/tools/calculationRow/add?nextIndex=' + rows + '&pointId=' + pointId))
        .done(function (data) {
            calcTable.find('tbody').append(data);
            var newRow = calcTable.find('.js-add-calc-row');
            newRow.find('.js-component-type').on('change', changeCalcType);
            newRow.find('.js-remove-calc').on('click', removeCalc);
            newRow.find('.js-component-type').val("Operation");
            newRow.find('.js-component-type').trigger("change");
            newRow.find('.js-function-options').on('change', changeFunctionType);
            newRow.find('.js-baseline-options').on('change', changeBaseLine);
            checkIfBaselineExists();
            var pointPickerId = newRow.data("pointPickerId");
            yukon.pickers[pointPickerId].removeEvent();
            newRow.removeClass('js-add-calc-row');
            yukon.ui.reindexInputs(calcTable);
        });
    };
    
    var updateComponentOrders = function () {
        var calcTable = $('#calculationTable'),
            rows = calcTable.find('tbody tr');
        
        rows.each(function (idx, row) {
            row = $(row);
            var order = idx + 1;
            row.find('.js-component-order').val(order);
        });
    };
    
    /**
     * Remove a row from the Calc table
     */
    var removeCalc = function () {
        var row = $(this).closest('tr'),
        calcTable = row.closest('table');
        var pointPickerId = row.data("pointPickerId");
        yukon.pickers[pointPickerId].removeEvent();
        row.remove();
        yukon.ui.reindexInputs(calcTable);
        updateComponentOrders();
        checkIfBaselineExists();
    };
    
    /**
     * Calc Type was changed
     */
    var changeCalcType = function () {
        var newValue = $(this).val();
        var row = $(this).closest('tr');
        if (newValue == 'Constant') {
            row.find('.js-constant').removeClass('dn');
            var pointPickerId = row.data("pointPickerId");
            yukon.pickers[pointPickerId].removeEvent();
            row.find('.js-point-picker').addClass('dn');
        } else {
            row.find('.js-constant').addClass('dn');
            row.find('.js-point-picker').removeClass('dn');
            row.find('.js-constant-value').val(0);
        }
        if (newValue == 'Function') {
            row.find('.js-function-options option[value="(none)"]').remove();
            row.find('.js-function-operations').removeClass('dn');
            row.find('.js-operations').addClass('dn');
            row.find('.js-operation-options').append('<option value="(none)"/>');
            row.find('.js-operation-options').val('(none)');
            var firstValue = row.find('.js-function-options option:first').val();
            row.find('.js-function-options').val(firstValue);
        } else {
            row.find('.js-operation-options option[value="(none)"]').remove();
            row.find('.js-function-operations').addClass('dn');
            row.find('.js-operations').removeClass('dn');
            row.find('.js-function-options').append('<option value="(none)"/>');
            row.find('.js-function-options').val('(none)');
            var firstValue = row.find('.js-operation-options option:first').val();
            row.find('.js-operation-options').val(firstValue);
        }
        
    };

     /**
     * Calc Operation was changed
     */
    var changeFunctionType = function () {
        var newValue = $(this).val(),
            row = $(this).closest('tr'),
            isBaseline = newValue == 'Baseline';
            checkIfBaselineExists();
            row.find('.js-baseline-picker').toggleClass('dn', !isBaseline);
    };

    /**
     * Check if BaseLine is selected
     */
    var checkIfBaselineExists = function () {
        var isBaseLineOptSelected = false;
        $('.js-function-options' ).each(function() {
            if ('Baseline' === $( this ).find(":selected").text()) {
                isBaseLineOptSelected = true;
            }
        });
        $('.js-baseline').toggleClass('dn', !isBaseLineOptSelected);
        $('.js-baseline-assigned').val(isBaseLineOptSelected);
    }
    
    /**
     * Enable valid translations for the interface.
     */
    var enableValidDirections = function(data, row) {
        var directionSelect = row.find('.js-fdr-direction');
        directionSelect.find('option').prop('disabled', true);

        data.directions.forEach(function(direction) {
            directionSelect.find('option[value="' + direction + '"]').prop('disabled', false);
        });

        if (directionSelect.find('option:selected').prop('disabled')) {
            directionSelect.val(data.directions[0]);
        }
    };

    /**
     * Calc Baseline was changed
     */
    var changeBaseLine = function () {
         var value=$(this).val();
         $('.js-baseline-assigned' ).each(function() {
             $('option[value='+value+']').prop('selected', true);
         });
    };

    var mod = {
        
        getNextValidPointOffset : function (selectedPaoInfo) {
            var pointType = $("#copy-point-pointType").val(),
                paoId = selectedPaoInfo[0].paoId,
                url = yukon.url('/tools/points/getNextAvaliablePointOffset');
            
            $.getJSON(url, {paoId : paoId, pointType : pointType}, function (data) {
                $("#copy-point-physicalOffset-txt").val(data.nextValidPointOffset);
            });
            
            if ($("#copy-point-physicalOffset-txt").hasClass('error')) {
                $("#copy-point-physicalOffset-txt").removeClass('error');
                $('#pointOffset\\.errors').text('');
            }
            
        },
        
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
            $(document).on('change', '.js-use-offset', updatePointOffset);

            $('.js-add-fdr').on('click', addFdr);
            
            $('.js-remove-fdr').on('click', removeFdr);
            
            $('.js-add-calc').on('click', addCalc);
            
            $('.js-remove-calc').on('click', removeCalc);
            
            $('.js-component-type').on('change', changeCalcType);
            
            $('.js-function-options').on('change', changeFunctionType);
            $('.js-baseline-options').on('change', changeBaseLine)
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
            
            /** Move row up. */
            $(document).on('click','.js-up', function (ev) {
                var row = $(this).closest('tr'),
                    prevRow = row.prev();
                
                row.insertBefore(prevRow);
                yukon.ui.reindexInputs(row.closest('table'));
                updateComponentOrders();
            });
            
            /** Move row down. */
            $(document).on('click','.js-down', function (ev) {
                var row = $(this).closest('tr'),
                    nextRow = row.next();
                
                row.insertAfter(nextRow);
                yukon.ui.reindexInputs(row.closest('table'));
                updateComponentOrders();
            });
            
            yukon.ui.highlightErrorTabs();

            $(document).on('yukon:da:point:delete', function () {
                $('#delete-point').submit();
            });
            
            $('[data-fdr-translation]').filter(':visible').each(function(index, value) {
                var translationNumber = $(value).data('fdrTranslation'),
                    row = $('[data-fdr-translation="' + translationNumber +'"]'),
                    fdrInterface = row.find('.js-fdr-interface').val(),
                    pointType = $('.js-point-type').val();
                
                $.ajax(yukon.url('/tools/fdr/' + fdrInterface + '?point-type=' + pointType))
                    .done(function (data) {
                        enableValidDirections(data, row);
                });
            });
            
            $(document).on('yukon:tools:point:copy', function() {
                yukon.ui.blockPage();
                $('#copy-point-form').ajaxSubmit({
                    success: function (data, status, xhr, $form) {
                        window.location.href = yukon.url('/tools/points/' + data.pointId);
                    },
                    error: function (xhr, status, error, $form) {
                        $('#copy-point-popup').html(xhr.responseText);
                        yukon.ui.initContent('#copy-point-popup');
                        yukon.ui.unblockPage();
                    }
                });
            });
            
            $(document).on('click', '#copy-point-physicalOffset-toggle', function () {
            	if ($("#copy-point-physicalOffset-txt").hasClass('dn')) {
                    $('#pointOffset\\.errors').addClass('dn');
                } else {
                    $('#pointOffset\\.errors').removeClass('dn');
                }
            });
        }
    };

    return mod;
}());

$(function () { yukon.tools.point.init(); });