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
                var endpoint = $(this).data('endpoint');
                $.getJSON(yukon.url('/dev/pxMiddleware/testEndpoint?endpoint=' + endpoint))
                .done(function (json) {
                    var resultJson = $('.js-test-endpoint-results');
                    if (json.testResultJson) {
                        resultJson.html(json.testResultJson)
                    }
                    if (json.errorMessage) {
                        resultJson.html(json.errorMessage);
                    }
                    resultJson.removeClass('dn');
                    
                });
            });

            _initialized = true;
        },

    };
    return mod;
}());

$(function() { yukon.dev.simulators.pxMWSimulator.init(); });