yukon.namespace('yukon.widget.infrastructureWarnings');

/**
 * Module for the Infrastructure Warnings Widget
 * @module yukon.widget.infrastructureWarnings
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.infrastructureWarnings = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            $(document).on('click', '.js-force-update', function () {
                $.ajax(yukon.url('/dashboards/infrastructureWarnings/forceUpdate'));
            });

            _initialized = true;
        },
        
    };
    
    return mod;
})();

$(function () { yukon.widget.infrastructureWarnings.init(); });