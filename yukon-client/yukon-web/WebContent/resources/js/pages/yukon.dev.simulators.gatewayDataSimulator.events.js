yukon.namespace('yukon.dev.simulators.gatewayDataSimulator.events');

/**
 * Module handling enabling and disabling portions of the gatewayDataSimulator
 * @module yukon.dev.simulators.gatewayDataSimulator.events
 * @requires JQUERY
 * @requires yukon
 */

yukon.dev.simulators.gatewayDataSimulator.events = ( function() {
    
    'use strict';
    
    var
    _initialized = false,
    
    _enableAll = function (event) {
        $.ajax({
            url: yukon.url('enableAllGatewaySimulators'),
            type: 'post',
            data: formData
        }).done(function() {
            window.location.href = yukon.url('/dev/rfn/gatewaySimulator');
        });
    },
    
    mod = {
        init: function() {
            
            if (_initialized) return;
            $('#tabs').tabs();  
            $('#enable-all').click(_enableAll);
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.dev.simulators.gatewayDataSimulator.events.init(); });