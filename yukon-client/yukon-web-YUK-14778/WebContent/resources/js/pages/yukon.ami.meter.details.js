yukon.namespace('yukon.ami.meterDetails');

/**
 * Handles behavior on the meter details page.
 * @module yukon.ami.meterDetails
 * @requires JQUERY
 * @requires yukon
 */
yukon.ami.meterDetails = (function () {

    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $('#commander-menu-option').click(function (ev) {
                var deviceId = $('#device-id').val();
                yukon.cookie.set('commander', 'lastTarget', 'DEVICE');
                yukon.cookie.set('commander', 'lastPaoId', deviceId);
                window.location.href = yukon.url('/tools/commander');
            });
            
            _initialized = true;
        }

    };
    
    return mod;
})();
 
$(function () { yukon.ami.meterDetails.init(); });