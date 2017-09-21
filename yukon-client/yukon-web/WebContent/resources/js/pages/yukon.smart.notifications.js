yukon.namespace('yukon.smart.notifications');

/**
 * Module for the Smart Notification pages.
 * @module yukon.smart.notifications
 * @requires JQUERY
 * @requires yukon
 */
yukon.smart.notifications = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    updateTypeFields = function (popup) {
        var type = popup.find('.js-type').val(),
            deviceDataMonitor = type == 'DEVICE_DATA_MONITOR';
        popup.find('.js-monitor').toggleClass('dn', !deviceDataMonitor);
        popup.find('#device-data-monitor').prop("disabled", !deviceDataMonitor);
   };
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            /** Load the notifications popup. */
            $(document).on('yukon:notifications:load', function (ev) {
                var popup = $(ev.target),
                    sendTimeField = popup.find('#notifications-send-time'),
                    timeLabel = popup.find('.js-time-label'),
                    timeSlider = popup.find('.js-time-slider'),
                    currentValue = sendTimeField.val(),
                    defaultValue = currentValue ? currentValue : 10 * 60;
                //initialize time slider
                timeSlider.slider({
                    max: 24 * 60 - 60,
                    min: 0,
                    value: defaultValue,
                    step: 60,
                    slide: function (ev, ui) {
                        timeLabel.text(yukon.timeFormatter.formatTime(ui.value, 0));
                        timeSlider.val(ui.value);
                        sendTimeField.val(ui.value);
                    },
                    change: function (ev, ui) {
                        timeLabel.text(yukon.timeFormatter.formatTime(ui.value, 0));
                        timeSlider.val(ui.value);
                        sendTimeField.val(ui.value);
                    }
                });
                sendTimeField.val(defaultValue);
                timeLabel.text(yukon.timeFormatter.formatTime(defaultValue, 0));
                timeSlider.val(defaultValue);
                updateTypeFields(popup);
            });
            
            /** 'Save' button clicked on the notifications popup. */
            $(document).on('yukon:notifications:save', function (ev) {
                var popup = $(ev.target),
                    form = popup.find('#notification-details');
                form.ajaxSubmit({
                    success: function (data, status, xhr, $form) {
                        popup.dialog('close');
                        yukon.ui.alertSuccess(data.successMsg);
                        window.location.reload();
                    },
                    error: function (xhr, status, error, $form) {
                        form.html(xhr.responseText);
                    }
                });
            });
            
            $(document).on('yukon:notifications:remove', function (ev) {
                var container = $(ev.target),
                    subscriptionId = container.data('subscriptionId');
                $.ajax({
                    url: yukon.url('/notifications/subscription/' + subscriptionId + '/unsubscribe'),
                    type: 'post'
                }).done(function () {
                    window.location.reload();
                });
            });
            
            $(document).on('change', '.js-frequency', function (ev) {
                var freq = $(this).val(),
                    form = $(this).closest('#notification-details'),
                    dailySelected = freq == 'DAILY_DIGEST';
                form.find('.js-daily').toggleClass('dn', !dailySelected);
                form.find('#notifications-send-time').prop("disabled", !dailySelected);
            });
            
            $(document).on('change', '.js-type', function (ev) {
                updateTypeFields($(this.closest('#notification-details')));
            });
                        
            _initialized = true;
        },
        
    };
    
    return mod;
})();

$(function () { yukon.smart.notifications.init(); });