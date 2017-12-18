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
           sendTimeValue = sendTimeField.val(),
           sendTimeValue = yukon.timeFormatter.parse24HourTime(sendTimeValue),
           userSettingSendTime = container.find('#userSettingSendTime').val(),
           userSettingSendTimeValue = yukon.timeFormatter.parse24HourTime(userSettingSendTime),
           currentValue = userSettingSendTimeValue > 0 ? userSettingSendTimeValue : sendTimeValue,
           defaultValue = currentValue ? currentValue : 0;
       if (timeSlider) {
           //initialize time slider
           timeSlider.slider({
               max: 24 * 60 - 60,
               min: 0,
               value: defaultValue,
               step: 60,
               disabled: userSettingSendTime,
               slide: function (ev, ui) {
                   timeLabel.text(yukon.timeFormatter.formatTime(ui.value, 0));
                   timeSlider.val(ui.value);
                   sendTimeField.val(yukon.timeFormatter.format24HourTime(ui.value, 0));
               },
               change: function (ev, ui) {
                   timeLabel.text(yukon.timeFormatter.formatTime(ui.value, 0));
                   timeSlider.val(ui.value);
                   sendTimeField.val(yukon.timeFormatter.format24HourTime(ui.value, 0));
               }
           });
           sendTimeField.val(yukon.timeFormatter.format24HourTime(defaultValue, 0));
           timeLabel.text(yukon.timeFormatter.formatTime(defaultValue, 0));
           timeSlider.val(defaultValue);
           if (userSettingSendTime) {
               //this made the slider hidden, but we still want it shown just disabled
               timeSlider.removeClass('ui-state-disabled');
               $('.js-single-notification-warning').removeClass('dn');
           }
       }
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
    
    initializeEventsTimeline = function () {
        var timeline = $('.js-events-timeline'),
            eventData = yukon.fromJson('#eventsjson'),
            toAdd = [],
            options = {},
            startDate = $('#startDateFilter').val(),
            endDate = $('#endDateFilter').val();
        options.begin = new Date(startDate).getTime();
        options.end = new Date(endDate).getTime();

        // Reverse order to add oldest first.
        eventData.reverse().forEach(function (event) {
            var statusMessage = $('.js-status-' + event.eventId),
                eventMessageSpan = $('<span />');
            event.id = event.eventId;
            eventMessageSpan.append('<strong></strong>');
            eventMessageSpan.find('strong').text(event.deviceName);
            event.message = eventMessageSpan.html() + " - " + statusMessage.text();
            event.timestamp = event.timestamp.millis;
            toAdd.push(event);
        });
        options.events = toAdd;
        timeline.timeline(options);
        timeline.timeline('draw');
    },
    
    mod = {
            
        initTimeSlider : function () {
            initializeTimeSlider($('#send-time'));
        },
        
        initEventsTimeline : function () {
            initializeEventsTimeline();
        },
        
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
                        tableContainer.html(data);
                        tableContainer.data('url', yukon.url('/notifications/subscriptions?' + form.serialize()));
                    }
                });
            });
            
            $(document).on('click', '.js-download', function () {
                var form = $('#filter-form');
                var data = form.serialize();
                window.location = yukon.url('/notifications/download?' + data);
            });
            
            $(document).on('click', '.js-settings-link', function (ev) {
                var popup = $(this).closest('.js-smart-notifications-popup');
                popup.dialog('close');
                window.location = yukon.url('/user/profile?#notifications-section');
            });
            
            initializeSmartNotificationsTable();

            _initialized = true;
        },
        
    };
    
    return mod;
})();

$(function () { yukon.smart.notifications.init(); });