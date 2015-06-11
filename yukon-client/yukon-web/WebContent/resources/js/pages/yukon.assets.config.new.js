yukon.namespace('yukon.assets.newConfig');

/**
 * Module for the new config collection action page.
 * @module yukon.assets.newConfig
 * @requires JQUERY
 * @requires yukon
 */
yukon.assets.newConfig = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $('.js-config-type').on('change', function (ev) {
                window.location = 'new?'
                    + $('.js-inventory-params :input').serialize()
                    + '&' + $.param({ type : $(this).val() });
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.assets.newConfig.init(); });