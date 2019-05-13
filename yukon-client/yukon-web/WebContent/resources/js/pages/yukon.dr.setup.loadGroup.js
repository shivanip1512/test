yukon.namespace('yukon.dr.setup.loadGroup');

/**
 * Module that handles the behavior on the setup Load Group page.
 * @module yukon.dr.setup.loadGroup
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.setup.loadGroup = (function() {
    
    'use strict';
    
    var
    _initialized = false,

    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $(document).on('click', '#js-cancel-btn', function (event) {
                window.history.back();
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.dr.setup.loadGroup.init(); });