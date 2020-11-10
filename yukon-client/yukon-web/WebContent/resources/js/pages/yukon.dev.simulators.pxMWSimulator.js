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
                $.ajax({
                    url: yukon.url('/dev/pxMW/testEndpoint'),
                    type: 'post',
                    data: { endpoint : endpoint } 
                });
            });

            _initialized = true;
        },

    };
    return mod;
}());

$(function() { yukon.dev.simulators.pxMWSimulator.init(); });