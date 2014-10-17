yukon.namespace('yukon.assets.gateway.shared');

/**
 * Module to handle shared behavior between gateway list and details page.
 * @module yukon.assets.gateway.shared
 * @requires JQUERY
 * @requires yukon
 */
yukon.assets.gateway.shared = (function () {

    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            /** User input happened in create or edit popup, adjust 'test connection' buttons. */
            $(document).on('input', '.js-gateway-edit-ip, .js-gateway-edit-username, .js-gateway-edit-password', function (ev) {
                var ip = $('.js-gateway-edit-ip').val(),
                    usernames = $('.js-gateway-edit-username');
                if (ip) {
                    usernames.each(function (idx, item) {
                        item = $(item);
                        var disabled = !item.val().trim() || !item.siblings('.js-gateway-edit-password').val().trim();
                        item.siblings('.button').prop('disabled', disabled);
                    });
                } else {
                    $('.js-gateway-edit-super-admin .button, .js-gateway-edit-admin .button, .js-gateway-edit-user .button')
                    .prop('disabled', true);
                }
            });
            
            _initialized = true;
        }

    };
    
    return mod;
})();
 
$(function () { yukon.assets.gateway.shared.init(); });