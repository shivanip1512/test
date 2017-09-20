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
                userGroupId = $('#userGroupId').val();
                $.ajax({
                    url: yukon.url('/dev/saveSubscription?userGroupId=' + userGroupId),
                    type: 'post',
                    data: form.serialize()
                }).done( function(data) {
                    popup.dialog('close');
                });
            });
            
            $(document).on('click', '.js-create-events', function () {
                var waitTime = $('#waitTime').val(),
                    eventsPerMessage = $('#eventsPerMessage').val();
                window.location.href = yukon.url('/dev/createEvents?waitTime=' + waitTime + "&eventsPerMessage=" + eventsPerMessage);
            });
            
            _initialized = true;
        },

    };
    return mod;
}());

$( function() { yukon.dev.smartNotificationsSimulator.init(); });