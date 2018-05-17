yukon.namespace('yukon.bulk.device.configs');

/**
 * Module for the Collection Actions Device Configs Action
 * @module yukon.bulk.device.configs
 * @requires JQUERY
 * @requires yukon
 */
yukon.bulk.device.configs = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    enableDisable = function () {
        var value = $('input[name=action]:checked').val();
        $('.js-assign-config').toggleClass('dn', value != 'ASSIGN');
        $('.js-unassign-config').toggleClass('dn', value != 'UNASSIGN');
        $('.js-send-config').toggleClass('dn', value != 'SEND');
        $('.js-read-config').toggleClass('dn', value != 'READ');
        $('.js-verify-config').toggleClass('dn', value != 'VERIFY');
    };
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
                        
            enableDisable();
            
            if (_initialized) return;
            
            $(document).on('change', 'input[name=action]', function() {
                enableDisable();
            });

            _initialized = true;
        },
        
    };
    
    return mod;
})();

$(function () { yukon.bulk.device.configs.init(); });