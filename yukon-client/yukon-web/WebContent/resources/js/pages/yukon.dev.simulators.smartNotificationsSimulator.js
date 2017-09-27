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
                generateTestEmail = $('#generateTestEmailAddresses').is(':checked');
                $.ajax({
                    url: yukon.url('/dev/saveSubscription?userGroupId=' + userGroupId + "&generateTestEmailAddresses=" + generateTestEmail),
                    type: 'post',
                    data: form.serialize()
                }).done( function(data) {
                    popup.dialog('close');
                    yukon.ui.alertSuccess("Subscription has been saved successfully.")
                });
            });
            
            $(document).on('click', '.js-create-events', function () {
                var waitTime = $('#waitTime').val(),
                    eventsPerMessage = $('#eventsPerMessage').val(),
                    numberOfMessages = $('#numberOfMessages').val();
                window.location.href = yukon.url('/dev/createEvents?waitTime=' + waitTime + "&eventsPerMessage=" + eventsPerMessage + "&numberOfMessages=" + numberOfMessages);
            });
            
            _initialized = true;
        },

    };
    return mod;
}());

$( function() { yukon.dev.smartNotificationsSimulator.init(); });