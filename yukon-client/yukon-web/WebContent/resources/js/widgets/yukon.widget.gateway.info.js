yukon.namespace('yukon.widget.gatewayInfo');

/**
 * Module for the gateway info widget.
 * @module yukon.widget.gatewayInfo
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.gatewayInfo = (function () {
    
    'use strict';
    
    function pad_with_zeroes(string, length) {
        while (string.length < length) {
            string = '0' + string;
        }
        return string;
    }
    
    function updateIPv6input() {
        var ipv6 = $('#ipv6prefix').val();
        if (ipv6 == null) {
            ipv6 = 'FD30:0000:0000:0000::/64'
        }
        $('#ipv6-1').val(ipv6.substring(0,4));
        $('#ipv6-2').val(ipv6.substring(5,9));
        $('#ipv6-3').val(ipv6.substring(10,14));
        $('#ipv6-4').val(ipv6.substring(15,19));
    }
    
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
            
            /** Configure popup was opened, adjust Ipv6 prefix. */
            $(document).on('yukon:assets:gateway:configure:load', function (ev) {
                updateIPv6input();
            });
            
            /** Save button clicked on conifguration popup. */
            $(document).on('yukon:assets:gateway:configure', function (ev) {
                
                var popup = $('#gateway-configure-popup'),
                    btns = popup.closest('.ui-dialog').find('.ui-dialog-buttonset'),
                    primary = btns.find('.js-primary-action'),
                    secondary = btns.find('.js-secondary-action');
                
                yukon.ui.busy(primary);
                secondary.prop('disabled', true);
                
                popup.find('.user-message').remove();
                
                $('#gateway-configuration-form').ajaxSubmit({
                    type: 'post',
                    success: function (result, status, xhr, $form) {
                        
                        popup.dialog('close');
                        window.location.href = window.location.href;
                    },
                    error: function (xhr, status, error, $form) {
                        popup.html(xhr.responseText);
                        yukon.ui.initContent(popup);
                        updateIPv6input();
                    },
                    complete: function () {
                        yukon.ui.unbusy(primary);
                        secondary.prop('disabled', false);
                    }
                });
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
                        updateIPv6input();
                    },
                    complete: function () {
                        yukon.ui.unbusy(primary);
                        secondary.prop('disabled', false);
                    }
                });
            });
            
            $(document).on('input', '.js-ipv6-update', function (ev) {
                $('#ipv6-1').val($('#ipv6-1').val().replace(/[^a-fA-F0-9]+/g,''));
                $('#ipv6-2').val($('#ipv6-2').val().replace(/[^a-fA-F0-9]+/g,''));
                $('#ipv6-3').val($('#ipv6-3').val().replace(/[^a-fA-F0-9]+/g,''));
                $('#ipv6-4').val($('#ipv6-4').val().replace(/[^a-fA-F0-9]+/g,''));

                var ipv6one = pad_with_zeroes($('#ipv6-1').val(), 4),
                    ipv6two = pad_with_zeroes($('#ipv6-2').val(), 4),
                    ipv6three = pad_with_zeroes($('#ipv6-3').val(), 4),
                    ipv6four = pad_with_zeroes($('#ipv6-4').val(), 4);
                
                var ipv6 = ipv6one + ':' + ipv6two + ':' + ipv6three + ':' + ipv6four + "::/64";
                $('#ipv6prefix').val(ipv6);
            });
            _initialized = true;
        },
    
};
    
    return mod;
})();

$(function () { yukon.widget.gatewayInfo.init(); });