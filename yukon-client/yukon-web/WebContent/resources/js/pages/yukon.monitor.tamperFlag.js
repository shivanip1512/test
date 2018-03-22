yukon.namespace('yukon.monitor.tamperFlag');

/**
 * Module that manages Tamper Flag Monitors.
 * 
 * @module yukon.monitor.tamperFlag
 * @requires JQUERY
 * @requires JQUERY UI
 */
yukon.monitor.tamperFlag = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
            
        /** 
         * Enabled/Disables the Read Flags functionality based on violation count
         */
        enableDisableReadFlags : function (violationCount) {
            if (violationCount.value == 0) {
                $('.js-read-flags').attr('disabled', 'disabled');
                $('.js-read-flags-message').removeClass('dn');
            } else {
                $('.js-read-flags').removeAttr('disabled');
                $('.js-read-flags-message').addClass('dn');
            }
        },
            
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;

        }
    };
        
    return mod;
})();
 
$(function () { yukon.monitor.tamperFlag.init(); });