yukon.namespace('yukon.dev.simulators.pxMWSimulator');

/**
 * Module handling PxMW Simulator
 * @module yukon.dev.simulators.pwMWSimulator
 * @requires JQUERY
 * @requires yukon
 */

yukon.dev.simulators.pxMWSimulator = ( function() {
    
    'use strict';

    var 
    _initialized = false,

    mod = {
        init : function() {
            
            if (_initialized) return;
            
            $(document).on('change', '.js-selected-status', function () {
                //submit all settings
                $('#pxMWForm').submit();
            });
            
            $(document).on('click', '.js-test-endpoint', function () {
                var endpoint = $(this).data('endpoint'),
                params = $('#' + endpoint + '_parameters').val();
                $.getJSON(yukon.url('/dev/pxMiddleware/testEndpoint?endpoint=' + endpoint + '&params=' + params))
                .done(function (json) {
                    var resultJson = $('.js-test-endpoint-results');
                    if (json.testResultJson) {
                        resultJson.html(json.testResultJson)
                    }
                    if (json.errorMessage) {
                        resultJson.html(json.errorMessage);
                    }
                    if (json.alertError) {
                        yukon.ui.alertError(json.alertError);
                    }
                    resultJson.removeClass('dn');
                    
                });
            });
            
            $(document).on('click', '.js-clear-cache', function () {
                $.post(yukon.url('/dev/pxMiddleware/clearCache'));
                $.ajax({
                    type: 'POST',
                    url: yukon.url('/dev/pxMiddleware/clearCache')
                }).done(function(json) {
                   if (json.userMessage) {
                       yukon.ui.alertSuccess(json.userMessage);
                   } 
                });
            });

            _initialized = true;
        },

    };
    return mod;
}());

$(function() { yukon.dev.simulators.pxMWSimulator.init(); });