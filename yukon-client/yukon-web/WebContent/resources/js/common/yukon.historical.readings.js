yukon.namespace('yukon.historical.readings');

/**
 * Module that handles the behavior on the Historical Readings popup
 * @module yukon.historical.readings
 * @requires JQUERY
 * @requires yukon
 */
yukon.historical.readings = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $(document).on('click', '.js-hide-dropdown', function () {
                $('.dropdown-menu').hide();
            });
            
            /** User is deleting a RPH value */
            $(document).on('yukon:historical:readings:delete', function (ev) {
                var container = $(ev.target),
                    pointId = container.attr('data-pointId'),
                    value = container.attr('data-value'),
                    timestamp = container.attr('data-timestamp'),
                    valuesTable = $('#valuesTable_' + pointId),
                    valuesDialog = valuesTable.closest('.ui-dialog-content');

                $.ajax({ 
                    url: yukon.url('/meter/historicalReadings/delete'),
                    data: {
                        pointId: pointId,
                        value: value,
                        timestamp: timestamp
                    },
                    method : 'POST'
                }).done(function (details) {
                    valuesTable.html(details);
                    valuesDialog.scrollTop(0);
                });             
            });

            $(document).on('yukon:historical:readings:editValue', function(ev) {
                var dialog = $(ev.target),
                    pointId = dialog.attr('data-point-id'),
                    valuesTable = $('#valuesTable_' + pointId),
                    valuesDialog = valuesTable.closest('.ui-dialog-content'),
                    form = dialog.find('#manual-entry-form');
                
                form.ajaxSubmit({
                    success: function (result, status, xhr, $form) {
                        dialog.dialog('close');
                        valuesTable.html(result);
                        valuesDialog.scrollTop(0);
                    },
                    error: function (xhr, status, error, $form) {
                        dialog.html(xhr.responseText);
                    }
                });
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.historical.readings.init(); });