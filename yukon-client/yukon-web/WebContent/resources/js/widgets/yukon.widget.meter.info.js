yukon.namespace('yukon.widget.meterInfo');

/**
 * Module for the meter info widget.
 * @module yukon.widget.meterInfo
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.meterInfo = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $(document).on('yukon:widget:meter:info:save', function (ev) {
                var popup = $('#meter-info-popup'),
                    btns = popup.closest('.ui-dialog').find('.ui-dialog-buttonset'),
                    primary = btns.find('.js-primary-action'),
                    secondary = btns.find('.js-secondary-action');
                
                yukon.ui.busy(primary);
                secondary.prop('disabled', true);
                
                popup.find('.user-message').remove();
                
                $('#meter-info-form').ajaxSubmit({
                    type: 'post',
                    success: function (result, status, xhr, $form) {
                        
                        popup.dialog('close');
                        window.location.href = window.location.href;
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
            
            $(document).on('input', '.js-meter-info-meter-number input[type=text]', function (ev) {
                
                var meterNumber = $(this).val();
                
                if (!meterNumber) {
                    $('#meter-info-popup').removeMessages();
                } else {
                    $.ajax({
                        url: yukon.url('/widget/meterInformationWidget/check-meter-number'),
                        data: {
                            shortName: 'meterInformationWidget',
                            deviceId: $('.js-meter-info-id').val(),
                            meterNumber: meterNumber
                        },
                        dataType : 'json'
                    }).done(function (data) {
                        if (data.inuse) {
                            $('#meter-info-popup').addMessage({ message: data.message, messageClass: 'warning' });
                        } else {
                            $('#meter-info-popup').removeMessages();
                        }
                    });
                }
                
            });
            
            _initialized = true;
        }
    
    };
    
    return mod;
})();

$(function () { yukon.widget.meterInfo.init(); });