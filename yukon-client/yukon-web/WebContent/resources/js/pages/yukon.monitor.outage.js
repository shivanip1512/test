yukon.namespace('yukon.monitor.outage');

/**
 * Module that manages Outage Monitors.
 * 
 * @module yukon.monitor.outage
 * @requires JQUERY
 * @requires JQUERY UI
 */
yukon.monitor.outage = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
            
        /** 
         * Enabled/Disables the Read Logs functionality based on violation count
         */
        enableDisableReadLogs : function (violationCount) {
            if (violationCount.value == 0) {
                $('.js-read-outages').attr('disabled', 'disabled');
                $('.js-read-outage-message').removeClass('dn');
            } else {
                $('.js-read-outages').removeAttr('disabled');
                $('.js-read-outage-message').addClass('dn');
            }
        },
            
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;

        }
    };
        
    return mod;
})();
 
$(function () { yukon.monitor.outage.init(); });