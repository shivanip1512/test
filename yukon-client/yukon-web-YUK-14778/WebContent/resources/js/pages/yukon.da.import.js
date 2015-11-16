yukon.namespace('yukon.da.import');

/**
 * Module that manages the capcontrol import page.
 * @module yukon.da.import
 * @requires JQUERY
 * @requires yukon
 */
yukon.da.import = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    _update = function () {
        var type = $('#import-type').val();
        $('[data-type]').hide();
        $('[data-type="' + type + '"]').show();
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            $('#import-type').change(function (ev) {
                _update();
                // Get the import type value, remove spaces, lowercase.
                var type = $('#import-type').val().replace(/_/g, '').toLowerCase();
                $('#importForm').attr('action', yukon.url('/capcontrol/import/') + type + 'File');
            });
            
            _update();
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.da.import.init(); });