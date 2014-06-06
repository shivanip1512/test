/**
 * Handles the schedule and schedule assignment pages.
 * @module yukon.da.schedules
 * @requires JQUERY
 * @requires yukon
 * @requires yukon.da
 */

yukon.namespace('yukon.da.schedules');

yukon.da.schedules = (function () {
    
    var _initialized = false,
    
    mod;

    mod = {
        
        init: function () {
            if (_initialized) {
                return;
            }
            
            /** start/stop schedule */
            $(document).on('click', 'button.runSchedule', function (ev) {
                var row = $(ev.currentTarget).closest('tr');
                var scheduleName = row.children('td[name=schedName]').html();
                var deviceName = row.children('td[name=deviceName]').html();
                var eventId = row[0].id.split('_')[1];
                
                $.getJSON(yukon.url('/capcontrol/schedule/startSchedule'), {
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
            
            $(document).on('click', 'button.stopSchedule', function (ev) {
                var row = $(ev.currentTarget).closest('tr');
                
                var deviceName = row.children('td[name=deviceName]').html();
                var deviceId = $(ev.currentTarget).attr('name');
                
                $.getJSON(yukon.url('/capcontrol/schedule/stopSchedule'), {
                    'deviceId': deviceId, 
                    'deviceName': deviceName
                }).done(function (json) {
                    if(!json.success) {
                        yukon.da.showAlertMessageForAction('<cti:msg2 key=".stopSchedule" javaScriptEscape="true"/>', '', json.resultText, 'red');
                    } else {
                        yukon.da.showAlertMessageForAction('<cti:msg2 key=".stopSchedule" javaScriptEscape="true"/>', '', json.resultText, 'green');
                    }
                });
                
            });
            
            $(document).on('click', '#removeAssignment', function (ev) {
                $('#removeAssignmentForm').submit();
            });
            /** close dialogs */
            $(document).on('click', '#newScheduleAssignmentCancelButton', function (ev) {
                $('#addAssignments').dialog('close');
            });
            $(document).on('click', '#stopScheduleAssignmentCancelButton', function (ev) {
                $('#stopAssignments').dialog('close');
            });
            $(document).on('click', '#startScheduleAssignmentCancelButton', function (ev) {
                $('#startAssignments').dialog('close');
            });
            
            /** enable menu click*/
            $(document).on('click', '.js-enable-ovuv', function (ev) {
                var eventId = $(ev.target).children("input").attr("value");
                $.post(yukon.url('/capcontrol/schedule/setOvUv'), {
                    'eventId': eventId, 
                    'ovuv': 1
                }).done(function (json) {
                    if (!json.success) {
                        yukon.da.showAlertMessageForAction('OvUv', '', json.resultText, 'red');
                    }
                });
            });
            /** disable menu click*/
            $(document).on('click', '.js-disable-ovuv', function (ev) {
                var eventId = $(ev.target).children("input").attr("value");
                $.post(yukon.url('/capcontrol/schedule/setOvUv'), {
                    'eventId': eventId, 
                    'ovuv': 0
                }).done(function (json) {
                    if (!json.success) {
                        yukon.da.showAlertMessageForAction('OvUv', '', json.resultText, 'red');
                    }
                });
            });
            
            _initialized = true;
        },
        
        clearFilter: function () {
            window.location.href = yukon.url('/capcontrol/schedule/scheduleAssignments');
        }
    };
    
    return mod;
}());

$(function () { yukon.da.schedules.init(); });