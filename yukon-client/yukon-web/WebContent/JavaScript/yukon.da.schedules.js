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
                        yukon.da.showAlertMessageForAction('<cti:msg2 key=".stopSchedule" javaScriptEscape="true"/>', 
                                '', json.resultText, 'red');
                    } else {
                        yukon.da.showAlertMessageForAction('<cti:msg2 key=".stopSchedule" javaScriptEscape="true"/>', 
                                '', json.resultText, 'green');
                    }
                });
                
            });
            
            $(document).on('click', '#removeAssignment', function (ev) {
                $('#removeAssignmentForm').submit();
            });
            
            /** new assignments dialog*/
            $('#add-assignments').on('yukon.vv.schedules.add', function (ev) {
                $('#add-schedule-assignment-form').submit();
            });

            /** stop assignments dialog*/
            $('#stop-assignments').on('yukon.vv.schedules.stop.all', function (ev) {
                var reviewTableUrl = yukon.url('/capcontrol/schedule/stopMultiple');
                $.post(reviewTableUrl, $('#stop-multiple-schedules-form').serialize()).done(function(json) {
                    $('#stop-assignments').dialog('close');
                    yukon.da.showAlertMessageForAction(json.schedule, '', json.resultText, 'green');
                }).fail(function() {
                    yukon.da.showAlertMessageForAction(json.schedule, '', json.resultText, 'red');
                });
            });
            
            /** start assignments dialog*/
            $('#start-assignments').on('yukon.vv.schedules.start.all', function (ev) {
                var reviewTableUrl = yukon.url('/capcontrol/schedule/startMultiple');
                $.post(reviewTableUrl, $('#start-multiple-schedules-form').serialize()).done(function(json) {
                    $('#start-assignments').dialog('close');
                    yukon.da.showAlertMessageForAction(json.schedule, '', json.resultText, 'green');
                }).fail(function() {
                    yukon.da.showAlertMessageForAction(json.schedule, '', json.resultText, 'red');
                });
            });
            
            /** enable menu click*/
            $(document).on('click', '.js-enable-ovuv', function (ev) {
                var eventId = $(ev.target).closest('li').attr('value');
                $.post(yukon.url('/capcontrol/schedule/setOvUv'), {
                    'eventId': eventId, 
                    'ovuv': 1
                }).done(function (json) {
                    if (!json.success) {
                        yukon.da.showAlertMessageForAction('OvUv', '', json.resultText, 'red');
                    } else {
                        enableLi = $('li[value=' + json.id + ']').find('.js-enable-ovuv').closest('li');
                        enableLi.hide();
                        disableLi = $('li[value=' + json.id + ']').find('.js-disable-ovuv').closest('li');
                        disableLi.show();
                    }
                });
            });
            /** disable menu click*/
            $(document).on('click', '.js-disable-ovuv', function (ev) {
                var eventId = $(ev.target).closest('li').attr('value');
                $.post(yukon.url('/capcontrol/schedule/setOvUv'), {
                    'eventId': eventId, 
                    'ovuv': 0
                }).done(function (json) {
                    if (!json.success) {
                        yukon.da.showAlertMessageForAction('OvUv', '', json.resultText, 'red');
                    } else {
                        enableLi = $('li[value=' + json.id + '] .js-enable-ovuv').closest('li');
                        enableLi.show();
                        disableLi = $('li[value=' + json.id + '] .js-disable-ovuv').closest('li');
                        disableLi.hide();
                    }
                });
            });

            /** Set filter */
            $(document).on('click', '#set-filter', function (ev) {
                var filterUrl = yukon.url('/capcontrol/schedule/filter');
                $.post(filterUrl, $('#filter-popup').find('form').serialize()).done(function(result) {
                    $('#schedule-assignments-table').html(result);
                    $('#filter-popup').dialog('close');
                });
            });
            
            /** clear the filter */
            $(document).on('click', '#clear-filter', function (ev) {
                var filterUrl = yukon.url('/capcontrol/schedule/filter');
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

$(function () { yukon.da.schedules.init(); });