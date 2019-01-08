yukon.namespace('yukon.da.scheduleAssignments');
/**
 * Handles the schedule and schedule assignment pages.
 * @module yukon.da.scheduleAssignments
 * @requires JQUERY
 * @requires yukon
 * @requires yukon.da
 */

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
                
                $.getJSON(yukon.url('/capcontrol/schedules/start'), {
                    'eventId': eventId, 
                    'deviceName': deviceName
                }).done(function (json) {
                    yukon.da.common.showAlertMessageForAction(scheduleName, '', json.resultText, json.success);
                });
                
            });
            
            $(document).on('change', '#cmd', function (ev) {
                var text = this.options[this.selectedIndex].text,
                value = this.options[this.selectedIndex].value;
                if (value == 'VerifyNotOperatedIn') {
                    $('#cmdInput').show();
                    $('#cmdInput').val(text);
                }
                else {
                    $('#cmdInput').val('');
                    $('#cmdInput').hide();
                }
            });
            
            /** Click detection to start schedule. */
            $(document).on('click', '.stop-schedule', function (ev) {
                var deviceName = $(ev.target).closest('li').attr('data-device-name');
                var deviceId = $(ev.target).closest('li').attr('data-device-id');
                
                $.getJSON(yukon.url('/capcontrol/schedules/stop'), {
                    'deviceId': deviceId, 
                    'deviceName': deviceName
                }).done(function (json) {
                    yukon.da.common.showAlertMessageForAction('', '', json.resultText, json.success);
                });
                
            });

            $(document).on('yukon_da_schedules_remove', '.deleteAssignment', function () {
                var btn = $(this),
                    removeUrl = yukon.url('/capcontrol/schedules/remove-assignment'),
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
                yukon.ui.blockPage();
                $('#add-schedule-assignment-form').ajaxSubmit({
                    success: function (data, status, xhr, $form) {
                        window.location.href = yukon.url('/capcontrol/schedules/assignments');
                    },
                    error: function (xhr, status, error, $form) {
                        $('#add-assignments').html(xhr.responseText);
                        yukon.ui.initContent('#add-assignments');
                        yukon.ui.unblockPage();
                    }
                });
            });

            /** Click detection to post stop schedules form. */
            $('#stop-assignments').on('yukon.vv.schedules.stop.all', function (ev) {
                var reviewTableUrl = yukon.url('/capcontrol/schedules/stop-multiple');
                $.post(reviewTableUrl, $('#stop-multiple-schedules-form').serialize()).done(function(json) {
                    $('#stop-assignments').dialog('close');
                    yukon.da.common.showAlertMessageForAction(json.schedule, '', json.resultText, json.success);
                });
            });
            
            /** Click detection to submit start assignment form. */
            $('#start-assignments').on('yukon.vv.schedules.start.all', function (ev) {
                var reviewTableUrl = yukon.url('/capcontrol/schedules/start-multiple');
                $.post(reviewTableUrl, $('#start-multiple-schedules-form').serialize()).done(function(json) {
                    $('#start-assignments').dialog('close');
                    yukon.da.common.showAlertMessageForAction(json.schedule, '', json.resultText, json.success);
                });
            });
            
            /** Click detection for enable/disable OvUv menu click. */
            $(document).on('click', '.js-enable-ovuv, .js-disable-ovuv', function (ev) {
                var link = $(ev.target).closest('li'),
                    enableOvUv = link.hasClass('js-enable-ovuv'),
                    eventId = link.attr('value');
                $.post(yukon.url('/capcontrol/schedules/set-ovuv'), {
                    'eventId': eventId, 
                    'ovuv': enableOvUv ? 1 : 0
                }).done(function (json) {
                    yukon.da.common.showAlertMessageForAction('OvUv', '', json.resultText, json.success);
                    if (json.success) {
                        var enableLi = $('li[value=' + json.id + '].js-enable-ovuv'),
                            disableLi = $('li[value=' + json.id + '].js-disable-ovuv');
                        enableLi.toggleClass('dn', enableOvUv);
                        disableLi.toggleClass('dn', !enableOvUv);
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