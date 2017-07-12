yukon.namespace("yukon.dev.simulators.dataStreamingSimulator");

/**
 * Module handling dataStreamingSimulator checkbox toggle-grouping
 * @module yukon.dev.simulators.dataStreamingSimulator
 * @requires JQUERY
 * @requires yukon
 */

yukon.dev.simulators.dataStreamingSimulator = ( function() {
       
    'use strict';

    var 
    _initialized = false,
    
    mod = {
        init: function() {
            
            if (_initialized) return;
            
            //handle enabling and disabling of Device Error fields for verification
            $("#verification-checkbox").attr("data-toggle", "resp-device-error");
            $("#verification-select").attr("data-toggle-group", "resp-device-error");
            $("#verification-number").attr("data-toggle-group", "resp-device-error");
            try {
                if ($("#verification-checkbox")[0].hasAttribute("checked")) {
                    $("#verification-select").removeAttr("disabled");
                    $("#verification-number").removeAttr("disabled");
                } else {
                    $("#verification-select").attr("disabled", "true");
                    $("#verification-number").attr("disabled", "true");
                }
            } catch (typeError) {
                //don't do anything, this means verification-checkbox is disabled
            }
            
            try {
                //handle enabling and disabling of Device Error fields for config
                $("#config-checkbox").attr("data-toggle", "conf-device-error");
                $("#config-select").attr("data-toggle-group", "conf-device-error");
                $("#config-number").attr("data-toggle-group", "conf-device-error");
                if ($("#config-checkbox")[0].hasAttribute("checked")) {
                    $("#config-select").removeAttr("disabled");
                    $("#config-number").removeAttr("disabled");
                } else {
                    $("#config-select").attr("disabled", "true");
                    $("#config-number").attr("disabled", "true");
                }
            } catch (typeError) {
                //don't do anything, this means config-checkbox is disabled
            }
            
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.dev.simulators.dataStreamingSimulator.init(); });