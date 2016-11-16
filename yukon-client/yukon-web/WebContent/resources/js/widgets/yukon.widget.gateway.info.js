yukon.namespace('yukon.widget.gatewayInfo');

/**
 * Module for the gateway info widget.
 * @module yukon.widget.gatewayInfo
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.gatewayInfo = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    _text,
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            _text = yukon.fromJson('#gateway-text');
            /** Edit popup was opened, adjust test connection buttons. */
            $(document).on('yukon:assets:gateway:edit:load', function (ev) {
                yukon.assets.gateway.shared.adjustTestConnectionButtons();
            });
            
            /** Save button clicked on edit popup. */
            $(document).on('yukon:assets:gateway:save', function (ev) {
                
                var popup = $('#gateway-edit-popup'),
                    btns = popup.closest('.ui-dialog').find('.ui-dialog-buttonset'),
                    primary = btns.find('.js-primary-action'),
                    secondary = btns.find('.js-secondary-action');
                
                yukon.ui.busy(primary);
                secondary.prop('disabled', true);
                
                popup.find('.user-message').remove();
                
                $('#gateway-settings-form').ajaxSubmit({
                    type: 'post',
                    success: function (result, status, xhr, $form) {
                        
                        popup.dialog('close');
                        window.location.href = window.location.href;
                    },
                    error: function (xhr, status, error, $form) {
                        popup.html(xhr.responseText);
                        yukon.ui.initContent(popup);
                        yukon.assets.gateway.shared.adjustTestConnectionButtons();
                    },
                    complete: function () {
                        yukon.ui.unbusy(primary);
                        secondary.prop('disabled', false);
                    }
                });
            });
            
            /** Test a connection for username and password. */
            $(document).on('click', '.js-conn-test-btn', function (ev) {
                
                var btn = $(this),
                    row = btn.closest('tr'),
                    otherRow = row.is('.js-gateway-edit-admin')
                        ? $('.js-gateway-edit-super-admin') : $('.js-gateway-edit-admin'),
                    ip = $('#gateway-settings-form .js-gateway-edit-ip').val(),
                    username = row.find('.js-gateway-edit-username').val(),
                    password = row.find('.js-gateway-edit-password').val();
                        
               // Disable test buttons until test is over
                yukon.ui.busy(btn);
                otherRow.find('.js-conn-test-btn').prop('disabled', true);
                $('.js-test-results').removeClass('success error').text('');
                
                $.ajax({
                    url: yukon.url('/widget/gatewayInformationWidget/test-connection'),
                    data: {
                        ip: ip,
                        username: username,
                        password: password,
                        id: $('#gateway-edit-popup').data('id')
                    }
                }).done(function (result) {
                    if (result.success) {
                        $('.js-test-results').addClass('success').text(_text['login.successful']);
                    } else {
                        if (result.message) {
                            $('.js-test-results').addClass('error').text(result.message);
                        } else {
                            $('.js-test-results').addClass('error').text(_text['login.failed']);
                        }
                    }
                }).always(function () {
                    yukon.ui.unbusy(btn);
                    otherRow.find('.js-conn-test-btn').prop('disabled', false);
                });
                
            });
            
            _initialized = true;
        },
    
};
    
    return mod;
})();

$(function () { yukon.widget.gatewayInfo.init(); });