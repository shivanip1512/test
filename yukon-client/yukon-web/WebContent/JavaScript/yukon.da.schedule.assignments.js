/**
 * Handles the schedule and schedule assignment pages.
 * @module yukon.da.scheduleAssignments
 * @requires JQUERY
 * @requires yukon
 * @requires yukon.da
 */

yukon.namespace('yukon.da.scheduleAssignments');

yukon.da.scheduleAssignments = (function () {
    
    var _initialized = false,
    
    mod = {};

    mod = {
        
        init: function () {
            if (_initialized) {
                return;
            }
            
            /** Click detection to start schedule. */
            $(document).on('click', '.run-schedule', function (ev) {
                var scheduleName = $(ev.target).closest('li').attr('data-schedule-name');
                var deviceName = $(ev.target).closest('li').attr('data-device-name');
                var eventId = $(ev.target).closest('li').attr('data-event-id');
                
                $.getJSON(yukon.url('/capcontrol/schedules/startSchedule'), {
                    'eventId': eventId, 
                    'deviceName': deviceName
                }).done(function (json) {
                    if (!json.success) {
                        yukon.da.showAlertMessageForAction(scheduleName, '', json.resultText, 'red');
                    } else {
                        yukon.da.showAlertMessageForAction(scheduleName, '', json.resultText, 'green');
                    }
                });
                
            });
            /** Click detection to start schedule. */
            $(document).on('click', '.stop-schedule', function (ev) {
                var deviceName = $(ev.target).closest('li').attr('data-device-name');
                var deviceId = $(ev.target).closest('li').attr('data-device-id');
                
                $.getJSON(yukon.url('/capcontrol/schedules/stopSchedule'), {
                    'deviceId': deviceId, 
                    'deviceName': deviceName
                }).done(function (json) {
                    if(!json.success) {
                        yukon.da.showAlertMessageForAction('', 
                                '', json.resultText, 'red');
                    } else {
                        yukon.da.showAlertMessageForAction('', 
                                '', json.resultText, 'green');
                    }
                });
                
            });

            $(document).on('yukon_da_schedules_remove', '.deleteAssignment', function () {
                var btn = $(this),
                    removeUrl = yukon.url('/capcontrol/schedules/removePao'),
                    data = {'eventId': btn.data('eventId'),
                            'paoId': btn.data('paoId'),
                            'com.cannontech.yukon.request.csrf.token': btn.data('csrfToken')
                    };
                $.post(removeUrl, data).done(function () {
                    var dropdownMenu = btn.closest('.dropdown-menu'),
                        dropdownTrigger = dropdownMenu.data('trigger'),
                        row = dropdownTrigger.closest('tr');
                    row.fadeOut();
                });
            });
            
            /** Click detection to submit add assignment form. */
            $('#add-assignments').on('yukon.vv.schedules.add', function (ev) {
                $('#add-schedule-assignment-form').submit();
            });

            /** Click detection to post stop schedules form. */
            $('#stop-assignments').on('yukon.vv.schedules.stop.all', function (ev) {
                var reviewTableUrl = yukon.url('/capcontrol/schedules/stopMultiple');
                $.post(reviewTableUrl, $('#stop-multiple-schedules-form').serialize()).done(function(json) {
                    $('#stop-assignments').dialog('close');
                    yukon.da.showAlertMessageForAction(json.schedule, '', json.resultText, 'green');
                }).fail(function() {
                    yukon.da.showAlertMessageForAction(json.schedule, '', json.resultText, 'red');
                });
            });
            
            /** Click detection to submit start assignment form. */
            $('#start-assignments').on('yukon.vv.schedules.start.all', function (ev) {
                var reviewTableUrl = yukon.url('/capcontrol/schedules/startMultiple');
                $.post(reviewTableUrl, $('#start-multiple-schedules-form').serialize()).done(function(json) {
                    $('#start-assignments').dialog('close');
                    yukon.da.showAlertMessageForAction(json.schedule, '', json.resultText, 'green');
                }).fail(function() {
                    yukon.da.showAlertMessageForAction(json.schedule, '', json.resultText, 'red');
                });
            });
            
            /** Click detection for enable OvUv menu click. */
            $(document).on('click', '.js-enable-ovuv', function (ev) {
                var eventId = $(ev.target).closest('li').attr('value');
                $.post(yukon.url('/capcontrol/schedules/setOvUv'), {
                    'eventId': eventId, 
                    'ovuv': 1
                }).done(function (json) {
                    if (!json.success) {
                        yukon.da.showAlertMessageForAction('OvUv', '', json.resultText, 'red');
                    } else {
                        var enableLi = $('li[value=' + json.id + ']').find('.js-enable-ovuv').closest('li');
                        enableLi.hide();
                        var disableLi = $('li[value=' + json.id + ']').find('.js-disable-ovuv').closest('li');
                        disableLi.show();
                    }
                });
            });
            /** Click detection for disable OvUv menu click. */
            $(document).on('click', '.js-disable-ovuv', function (ev) {
                var eventId = $(ev.target).closest('li').attr('value');
                $.post(yukon.url('/capcontrol/schedules/setOvUv'), {
                    'eventId': eventId, 
                    'ovuv': 0
                }).done(function (json) {
                    if (!json.success) {
                        yukon.da.showAlertMessageForAction('OvUv', '', json.resultText, 'red');
                    } else {
                        var enableLi = $('li[value=' + json.id + '] .js-enable-ovuv').closest('li');
                        enableLi.show();
                        var disableLi = $('li[value=' + json.id + '] .js-disable-ovuv').closest('li');
                        disableLi.hide();
                    }
                });
            });

            /** Set filter on the table. */
            $(document).on('click', '#set-filter', function (ev) {
                var filterUrl = yukon.url('/capcontrol/schedules/filter');
                $.post(filterUrl, $('#filter-popup').find('form').serialize()).done(function(result) {
                    $('#schedule-assignments-table').html(result);
                    $('#filter-popup').dialog('close');
                });
            });
            
            /** Clear filter on the table. */
            $(document).on('click', '#clear-filter', function (ev) {
                var filterUrl = yukon.url('/capcontrol/schedules/filter');
                $.post(filterUrl).done(function(result) {
                    $('#schedule-assignments-table').html(result);
                    $('#filter-popup').dialog('close');
                });
            });
            
            _initialized = true;
        },
    };
    
    return mod;
}());

$(function () { yukon.da.scheduleAssignments.init(); });