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
    _text,
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            _text = yukon.fromJson('#gateway-text');
            
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
            
            $(document).on('click', '.js-gw-connect', function (ev) {
                var id = $(this).data('id'),
                    name = $(this).closest('.dropdown-menu').data('trigger').data('name');
                yukon.ui.alertPending(_text['connect.pending'].replace('{0}', name));
                $.ajax({ url: yukon.url('/stars/gateways/' + id + '/connect') })
                .done(function (result) { 
                    if (result.success) {
                        yukon.ui.alertSuccess(_text['connect.success'].replace('{0}', name));
                    } else {
                        yukon.ui.alertError(_text['connect.failure'].replace('{0}', name));
                    }
                });
            });
            
            $(document).on('click', '.js-gw-disconnect', function (ev) {
                var id = $(this).data('id'),
                name = $(this).closest('.dropdown-menu').data('trigger').data('name');
                yukon.ui.alertPending(_text['disconnect.pending'].replace('{0}', name));
                $.ajax({ url: yukon.url('/stars/gateways/' + id + '/disconnect') })
                .done(function (result) { 
                    if (result.success) {
                        yukon.ui.alertSuccess(_text['disconnect.success'].replace('{0}', name));
                    } else {
                        yukon.ui.alertError(_text['disconnect.failure'].replace('{0}', name));
                    }
                });
            });
            
            $(document).on('click', '.js-gw-collect-data', function (ev) {
                var id = $(this).data('id'),
                name = $(this).closest('.dropdown-menu').data('trigger').data('name');
                yukon.ui.alertPending(_text['collect.data.pending'].replace('{0}', name));
                $.ajax({ url: yukon.url('/stars/gateways/' + id + '/collect-data') })
                .done(function (result) { 
                    if (result.success) {
                        yukon.ui.alertSuccess(_text['collect.data.success'].replace('{0}', name));
                    } else {
                        yukon.ui.alertError(_text['collect.data.failure'].replace('{0}', name));
                    }
                });
            });
            
            _initialized = true;
        }

    };
    
    return mod;
})();
 
$(function () { yukon.assets.gateway.shared.init(); });