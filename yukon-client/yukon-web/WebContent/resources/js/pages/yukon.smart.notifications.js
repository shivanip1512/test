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
    
    initializeSmartNotificationsTable = function () {
        var tableContainer = $('#smart-notifications-container');
        if (tableContainer.is(':visible')) {
            var reloadUrl = tableContainer.attr('data-url');
            tableContainer.load(reloadUrl);
        }
    },
    
    initializeEventsTimeline = function () {
        var timeline = $('.js-events-timeline'),
            eventData = yukon.fromJson('#eventsjson'),
            toAdd = [],
            options = {},
            startDateInstant = $('#startInstant').val(),
            endDateInstant = $('#endInstant').val();
        
        options.begin = new Date(startDateInstant).getTime();
        options.end = new Date (endDateInstant).getTime();
        options.showLabels = true;
        // Reverse order to add oldest first.
        eventData.reverse().forEach(function (event) {
            var statusMessage = $('.js-status-' + event.eventId),
                eventMessageSpan = $('<span />');
            event.id = event.eventId;
            eventMessageSpan.append('<strong></strong>');
            eventMessageSpan.find('strong').text(event.deviceName);
            event.message = eventMessageSpan.html() + " - " + statusMessage.text();
            event.timestamp = event.timestamp.millis;
            //change the timezone if needed
            var row = $('.js-event-' + event.id);
            var timeText = moment(event.timestamp).tz(yg.timezone).format(yg.formats.date.full);
            row.find('.js-timestamp').text(timeText);
            
            toAdd.push(event);
        });
        options.events = toAdd;
        timeline.timeline(options);
        timeline.timeline('draw'); 
    },
    
    updateSubscriptions = function () {
        var tableContainer = $('#smart-notifications-container'),
            form = $('#filter-form');
        form.ajaxSubmit({
            success: function(data, status, xhr, $form) {
                tableContainer.html(data);
                tableContainer.data('url', yukon.url('/notifications/subscriptions?' + form.serialize()));
            }
        });
    },
    
    refreshExistingSubscriptions = function (existingSubscriptionsTable, data) {
        var existingPopup = existingSubscriptionsTable.closest('.js-smart-notifications-popup'),
            type = existingPopup.find('#type').val(),
            monitorId = existingPopup.find('#monitorId').val();
        existingPopup.load(yukon.url('/notifications/subscription/existingPopup/' + type + "?monitorId=" + monitorId), function () {
            existingPopup.find('#successMessage').text(data.successMessage);
            existingPopup.find('#successMessage').removeClass('dn');
        });
    },
    
    saveSubscription = function (form) {
        form.ajaxSubmit({
            success: function (data, status, xhr, $form) {
                form.closest('.js-smart-notifications-popup').dialog('close');
                var existingSubscriptionsTable = $('.js-existing-subscriptions:visible');
                //refresh subscriptions if on profile page
                if ($('#filter-form').is(":visible")) {
                    updateSubscriptions();
                }
                //refresh subscriptions in Existing Subscriptions popup
                if (existingSubscriptionsTable.length > 0) {
                    refreshExistingSubscriptions(existingSubscriptionsTable, data);
                } else {
                    yukon.ui.alertSuccess(data.successMessage);
                }
                
            },
            error: function (xhr, status, error, $form) {
                form.html(xhr.responseText);
                yukon.ui.timeSlider.init();
                updateTypeFields(form);
            }
        });
    },
    
    mod = {
        
        initEventsTimeline : function () {
            initializeEventsTimeline();
        },
        
        /** Initialize this module. */
        init : function () {
                                    
            if (_initialized) return;
            
            /** Load the notifications popup. */
            $(document).on('yukon:notifications:load', function (ev) {
                var popup = $(ev.target);
                updateTypeFields(popup);
                yukon.ui.timeSlider.init();
                //check if single notification was selected
                var singleNotificationSlider = $('#notifications-section').find('.js-time-slider');
                if (singleNotificationSlider.is(":visible")) {
                    //this made the slider hidden, but we still want it shown just disabled
                    var popupSlider = popup.find('.js-time-slider');
                    popupSlider.slider({disabled: true});
                    popupSlider.removeClass('ui-state-disabled');
                    $('.js-single-notification-warning').removeClass('dn');
                }
            });
            
            $(document).on('submit', '#notification-details', function (ev) {
                var form = $(ev.target);
                saveSubscription(form);
                return false;
            });
            
            /** 'Save' button clicked on the notifications popup. */
            $(document).on('yukon:notifications:save', function (ev) {
                var popup = $(ev.target),
                    form = popup.find('#notification-details');
                saveSubscription(form);
            });
            
            $(document).on('yukon:notifications:remove', function (ev) {
                var container = $(ev.target),
                    subscriptionId = container.data('subscriptionId');
                $.ajax({
                    url: yukon.url('/notifications/subscription/' + subscriptionId + '/unsubscribe'),
                    type: 'post'
                }).done(function (data) {
                    if (data.successMessage) {
                        if ($('#filter-form').is(":visible")) {
                            yukon.ui.alertSuccess(data.successMessage)
                            updateSubscriptions();
                        }
                        var existingSubscriptionsTable = $('.js-existing-subscriptions:visible');
                        if (existingSubscriptionsTable.length > 0) {
                            refreshExistingSubscriptions(existingSubscriptionsTable, data);
                        }
                    } else if (data.errorMessage) {
                        yukon.ui.alertError(data.errorMessage)
                    }
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
                updateTypeFields($(this).closest('#notification-details'));
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
                updateSubscriptions();
            });
            
            $(document).on('click', '.js-download', function () {
                var form = $('#filter-form');
                var data = form.serialize();
                window.location = yukon.url('/notifications/download?' + data);
            });
            
            $(document).on('click', '.js-notifications-help', function (ev) {
                $('.js-user-message').removeClass('dn');
            });
            
            initializeSmartNotificationsTable();

            _initialized = true;
        },
        
    };
    
    return mod;
})();

$(function () { yukon.smart.notifications.init(); });