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
   },
   
   initializeTimeSlider = function (container) {
       var sendTimeField = container.find('#notifications-send-time'),
           timeLabel = container.find('.js-time-label'),
           timeSlider = container.find('.js-time-slider'),
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
    },
    
    initializeSmartNotificationsTable = function () {
        var tableContainer = $('#smart-notifications-container');
        if (tableContainer.is(':visible')) {
            var reloadUrl = tableContainer.attr('data-url');
            tableContainer.load(reloadUrl, function () {
                initializeTimeSlider($('#send-time'));
            });
        }
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            /** Load the notifications popup. */
            $(document).on('yukon:notifications:load', function (ev) {
                var popup = $(ev.target);
                initializeTimeSlider(popup);
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
            
            $(document).on('click', '.js-single-notification', function (ev) {
                var singleNotification = $('#singleNotificationValue').is(':checked');
                $('#send-time').toggleClass('dn', singleNotification);
                var container = $('#send-time');
                initializeTimeSlider(container);
            });
            
            $(document).on('click', '.js-show-all', function (ev) {
                var tableContainer = $('#smart-notifications-container'),
                    url = yukon.url("/notifications/subscriptions");
                $('.js-filter-popup').dialog('destroy');
                tableContainer.load(url, function () {});
                tableContainer.data('url', url);
            });
            
            $(document).on('click', '.js-filter', function (ev) {
                var tableContainer = $('#smart-notifications-container'),
                    form = $('#filter-form');
                form.ajaxSubmit({
                    success: function(data, status, xhr, $form) {
                        $('.js-filter-popup').dialog('destroy');
                        tableContainer.html(data);
                        tableContainer.data('url', yukon.url('/notifications/subscriptions?' + form.serialize()));
                    }
                });
            });
            
            initializeSmartNotificationsTable();
      
            _initialized = true;
        },
        
    };
    
    return mod;
})();

$(function () { yukon.smart.notifications.init(); });