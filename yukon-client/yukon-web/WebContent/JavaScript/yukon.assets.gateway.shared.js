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
            
            $(document).on('click', '.js-gw-collect-data', function (ev) {
                
                var popup = $('#gateway-collect-data-popup'),
                    trigger = $(this).closest('.dropdown-menu').data('trigger');
                
                popup.load(yukon.url('/stars/gateways/collect-data/options'), function () {
                    popup.dialog({
                        title: _text['collect.data.title'].replace('{0}', trigger.data('name')),
                        width: 550,
                        buttons: yukon.ui.buttons({ event: 'yukon:assets:gateway:collect:data', target: trigger })
                    });
                });
            });
            
            $(document).on('yukon:assets:gateway:collect:data', function (ev) {
                
                var popup = $('#gateway-collect-data-popup'),
                    id = $(ev.target).data('id'),
                    name = $(ev.target).data('name'),
                    types = [];
                
                popup.find('.js-select-all-item:checked').each(function (idx, item) { types.push(item.name); });
                
                yukon.ui.alertPending(_text['collect.data.pending'].replace('{0}', name));
                
                $.ajax({
                    url: yukon.url('/stars/gateways/' + id + '/collect-data'), 
                    data: JSON.stringify(types),
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
            
            $(document).on('yukon:assets:gateway:cert:update', function (ev) {
                
                var popup = $('#gateway-cert-popup'),
                    file = popup.find('input[type=file]'),
                    gateways = popup.find('.js-select-all-item'),
                    chosen = popup.find('.js-select-all-item:checked'),
                    btns = popup.closest('.ui-dialog').find('.ui-dialog-buttonset'),
                    primary = btns.find('.js-primary-action'),
                    secondary = btns.find('.js-secondary-action'),
                    valid = true;
                
                if (!file.val()) {
                    file.addClass('animated shake-subtle error')
                    .one(yg.events.animationend, function() { $(this).removeClass('animated shake-subtle error'); });
                    valid = false;
                }
                
                if (!chosen.length) {
                    gateways.addClass('animated shake-subtle error')
                    .one(yg.events.animationend, function() { $(this).removeClass('animated shake-subtle error'); });
                    valid = false;
                }
                
                if (!valid) return;
                
                yukon.ui.busy(primary);
                secondary.prop('disabled', true);
            
                popup.find('.user-message').remove();
                
                $('#gateway-cert-form').ajaxSubmit({
                    url: yukon.url('/stars/gateways/cert-update'), 
                    type: 'post',
                    success: function (result, status, xhr, $form) {
                        
                        popup.dialog('close');
                        
                        console.log('yep');
                    },
                    error: function (xhr, status, error, $form) {
                        popup.html(xhr.responseText);
                    },
                    complete: function () {
                        yukon.ui.unbusy(primary);
                        secondary.prop('disabled', false);
                    }
                });
            });
            
            /** User input happened in create or edit popup, adjust 'test connection' buttons. */
            $(document).on('input', '.js-gateway-edit-ip, .js-gateway-edit-username', function (ev) {
                mod.adjustTestConnectionButtons();
            });
            
            /** Test a connection for username and password. */
            $(document).on('click', '.js-conn-test-btn', function (ev) {
                
                var btn = $(this),
                    row = btn.closest('tr'),
                    ip = $('#gateway-settings-form .js-gateway-edit-ip').val(),
                    username = row.find('.js-gateway-edit-username').val(),
                    password = row.find('.js-gateway-edit-password').val();
                
                yukon.ui.busy(btn);
                $('.js-test-results').removeClass('success error').text('');
                
                $.ajax({
                    url: yukon.url('/stars/gateways/test-connection'),
                    data: {
                        ip: ip,
                        username: username,
                        password: password
                    }
                }).done(function (result) {
                    if (result.success) {
                        $('.js-test-results').addClass('success').text(_text['login.successful']);
                    } else {
                        $('.js-test-results').addClass('error').text(_text['login.failed']);
                    }
                }).always(function () {
                    yukon.ui.unbusy(btn);
                });
                
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
                    item.siblings('.button').prop('disabled', disabled);
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