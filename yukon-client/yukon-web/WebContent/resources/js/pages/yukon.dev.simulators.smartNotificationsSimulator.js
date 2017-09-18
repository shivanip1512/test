yukon.namespace('yukon.dev.smartNotificationsSimulator');

/**
 * Module handling smart notifications simulator settings
 * @module yukon.dev.simulators.smartNotificationsSimulator
 * @requires JQUERY
 * @requires yukon
 */

yukon.dev.smartNotificationsSimulator = (function() {
    var _initialized = false,

    mod = {
        init : function() {
            if (_initialized) return;
            
            /** 'Save' button clicked on the notifications popup. */
            $(document).on('yukon:simulatorNotifications:save', function (ev) {
                var popup = $(ev.target),
                form = popup.find('#notification-details'),
                userGroupPicker = yukon.pickers['userGroupPicker'],
                userGroupId = $('#userGroupId').val(),
                //post combine daily with true/false values
                combineDailyField = popup.find('#combineDailyField'),
                combineDailySwitch = popup.find('.js-combine-daily');
            combineDailyField.attr('value', combineDailySwitch.prop('checked'));
            
                $.ajax({
                    url: yukon.url('/dev/rfn/smartNotificationsSimulator/saveSubscription?userGroupId=' + userGroupId),
                    type: 'post',
                    data: form.serialize()
                }).done( function(data) {
                    popup.dialog('close');
                });
            });
            
            _initialized = true;
        },

    };
    return mod;
}());

$( function() { yukon.dev.smartNotificationsSimulator.init(); });