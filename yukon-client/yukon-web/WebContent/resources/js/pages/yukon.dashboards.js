yukon.namespace('yukon.dashboards');

/**
 * Module for the dashboard pages.
 * @module yukon.dashboards
 * @requires JQUERY
 * @requires yukon
 */
yukon.dashboards = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
                        
            _initialized = true;
        },
        
    };
    
    return mod;
})();

$(function () { yukon.dashboards.init(); });