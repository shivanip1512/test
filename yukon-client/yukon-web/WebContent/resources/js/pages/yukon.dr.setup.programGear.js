yukon.namespace('yukon.dr.setup.programGear');

/**
 * Module that handles the behavior on the setup program Gear page.
 * 
 * @module yukon.dr.setup.programGear
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.setup.programGear = (function() {

    'use strict';

    var _initialized = false, 
    
    _whenToChange = function() {
        var whenToChange = $("#whenToChange").val();
        if (whenToChange == 'Duration') {
            $('#js-changePriority-row').hide();
            $('#js-changeDurationInMinutes-row').show();
            $('#js-triggerNumber-row').hide();
            $('#js-triggerOffset-row').hide();
        } else if (whenToChange == 'Priority') {
            $('#js-changePriority-row').show();
            $('#js-changeDurationInMinutes-row').hide();
            $('#js-triggerNumber-row').hide();
            $('#js-triggerOffset-row').hide();
        } else if (whenToChange == 'TriggerOffset') {
            $('#js-triggerOffset-row').show();
            $('#js-triggerNumber-row').show();
            $('#js-changePriority-row').hide();
            $('#js-changeDurationInMinutes-row').hide();
        } else {
            $('#js-changePriority-row').hide();
            $('#js-changeDurationInMinutes-row').hide();
            $('#js-triggerNumber-row').hide();
            $('#js-triggerOffset-row').hide();
        }
    },
    
    mod = {
        /** Initialize this module. */
        init : function() {
            if (_initialized)
                return;

            $(document).on('click', '#js-cancel-btn', function(event) {
                window.history.back();
            });

            $(document).on('change', '#whenToChange', function(event) {
                _whenToChange();
            });
            
            $(document).on('click', '.js-help-btn', function () {
                var selectedGearType = $("#controlMethod option:selected").val();
                $(".js-simple-thermostat-ramping-alert-box").toggleClass("dn", selectedGearType !== 'SimpleThermostatRamping');
                $(".js-thermostat-ramping-alert-box").toggleClass("dn", selectedGearType !== 'ThermostatRamping');
            });
            
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function() {
    yukon.dr.setup.programGear.init();
});