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
            
            /** User clicked the connect option, send connect request. */
            $(document).on('click', '.js-gw-connect', function (ev) {
                
                var trigger = $(this).closest('.dropdown-menu').data('trigger'),
                    id = trigger.data('id'),
                    name = trigger.data('name');
                
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
            
            /** User clicked the disconnect option, send disconnect request. */
            $(document).on('click', '.js-gw-disconnect', function (ev) {
                
                var trigger = $(this).closest('.dropdown-menu').data('trigger'),
                    id = trigger.data('id'),
                    name = trigger.data('name');
                
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
            
            /** User clicked the collect data option, show collect data dialog. */
            $(document).on('click', '.js-gw-collect-data', function (ev) {
                
                var popup = $('#gateway-collect-data-popup'),
                    trigger = $(this).closest('.dropdown-menu').data('trigger'),
                    name = trigger.data('name'),
                    target = trigger;
                
                popup.load(yukon.url('/stars/gateways/collect-data/options'), function () {
                    popup.dialog({
                        title: _text['collect.data.title'].replace('{0}', name),
                        width: 550,
                        buttons: yukon.ui.buttons({ event: 'yukon:assets:gateway:collect:data', target: target })
                    });
                });
            });
            
            /** User clicked 'OK' on collect data dialog, send collect data request. */
            $(document).on('yukon:assets:gateway:collect:data', function (ev) {
                
                var popup = $('#gateway-collect-data-popup'),
                    id = $(ev.target).data('id'),
                    name = $(ev.target).data('name'),
                    types = [];
                
                popup.find('.js-select-all-item:checked').each(function (idx, item) { types.push(item.name); });
                var data={
                        types : types
                };
                yukon.ui.alertPending(_text['collect.data.pending'].replace('{0}', name));
                
                $.ajax({
                    url: yukon.url('/stars/gateways/' + id + '/collect-data'), 
                    data: JSON.stringify(data),
                    contentType: 'application/json', 
                    type: 'post'
                }).done(function (result) {
                    if (result.success) {
                        yukon.ui.alertSuccess(_text['collect.data.success'].replace('{0}', name));
                    } else {
                        yukon.ui.alertError(_text['collect.data.failure'].replace('{0}', name));
                    }
                }).always(function () {
                    popup.dialog('close');
                });
            });
            
            /** User input happened in create or edit popup, adjust 'test connection' buttons. */
            $(document).on('input', '.js-gateway-edit-ip, .js-gateway-edit-username', function (ev) {
                mod.adjustTestConnectionButtons();
            });
            
            /** User clicked the streaming capacity pill.  Redirect to Data Streaming Summary Page with gateway selected */
            $(document).on('click', '.js-streaming-capacity', function (ev) {
                var gatewayId = $(this).closest('tr').data('gateway');
                window.location.href = yukon.url('/tools/dataStreaming/summary?selectedGatewayIds=' + gatewayId);
            });
            
            _initialized = true;
        },
        
        adjustTestConnectionButtons: function () {
            var ip = $('.js-gateway-edit-ip').val(),
                usernames = $('.js-gateway-edit-username');
            if (ip) {
                usernames.each(function (idx, item) {
                    item = $(item);
                    var disabled = !item.val().trim();
                    if (item.attr('id') == 'admin.username') {
                        $('.admin').prop('disabled', disabled);
                    } else {
                        $('.superAdmin').prop('disabled', disabled);
                    }
                });
            } else {
                $('.js-gateway-edit-super-admin .button,' 
                        + ' .js-gateway-edit-admin .button,' 
                        + ' .js-gateway-edit-user .button').prop('disabled', true);
            }
        }
    
    };
    
    return mod;
})();
 
$(function () { yukon.assets.gateway.shared.init(); });