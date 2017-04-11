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
            
            /** 'Save' button clicked on the dashboard details popup. */
            $(document).on('yukon:dashboard:details:save', function (ev) {
                $('#dashboard-details').submit();
/*                $('#dashboard-details').ajaxSubmit({
                    success: function (result, status, xhr, $form) {
                    },
                    error: function (xhr, status, error, $form) {
                    },
                    complete: function () {
                    }
                });*/
            });
                        
            _initialized = true;
        },
        
    };
    
    return mod;
})();

$(function () { yukon.dashboards.init(); });