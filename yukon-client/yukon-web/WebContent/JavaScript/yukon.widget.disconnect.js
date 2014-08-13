yukon.namespace('yukon.widget.disconnect');

/**
 * Handles behavior for the disconnect widget.
 * 
 * @module yukon.widget.disconnect
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.disconnect = (function() {
    
    'use strict';
    
    var
    _initialized = false,
    
    _mod = {
        
        /** Initialize this module. Depends on DOM elements so only call after DOM is loaded. */
        init: function() {

            if (_initialized) return;
            
            _initialized = true;
            
        },
        
        toggleButtons: function (data) {
            
        }
    };
    
    return _mod;
})();

$(function() { yukon.widget.disconnect.init(); });