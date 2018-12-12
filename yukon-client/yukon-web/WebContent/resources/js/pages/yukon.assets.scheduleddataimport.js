yukon.namespace('yukon.assets.scheduleddataimport');

/**
 * Module that handles the behavior on the Scheduled Data Import page.
 * @module yukon.assets.scheduleddataimport
 * @requires JQUERY
 * @requires yukon
 */
yukon.assets.scheduleddataimport = (function() {
    
    'use strict';
    
    var
    _initialized = false,

    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $(document).on("yukon:scheduledDataImport:delete", function () {
                yukon.ui.blockPage();
                $('#delete-scheduledDataImport-form').submit();
            });
            
            $(document).on('click', '#cancel-btn', function (event) {
                window.history.back();
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.assets.scheduleddataimport.init(); });