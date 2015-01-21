yukon.namespace('yukon.da.schedules');
/**
 * Handles the schedule and schedule assignment pages.
 * @module yukon.da.schedules
 * @requires JQUERY
 * @requires yukon
 * @requires yukon.date.time.picker
 */
yukon.da.schedules = (function () {

    var mod = {

        init: function () {

            $(document).on('click', '.js-edit-schedule', function () {
                var link = $(this),
                    popup = $('#schedule-popup'),
                    id = link.data('scheduleId');
                popup.load(yukon.url('/capcontrol/schedule/' + id), function () {
                    var mode = popup.find('form').data('mode');
                    var title = popup.find('.js-popup-title')[0].value;
                    var buttons = [];
                    var deleteButton = {
                        text: yg.text['delete'],
                        click: function (ev) {
                            yukon.ui.confirm({
                                dialog: popup,
                                event: 'yukon:da:schedules:delete',
                            });
                        },
                        'class': 'delete action'
                    };

                    if (mode === 'EDIT') {
                        buttons = yukon.ui.buttons({
                            event: 'yukon:da:schedules:edit:submit'
                        });
                        //Puts the delete button between OK and Cancel
                        buttons.splice(1,0, deleteButton);
                    }

                    yukon.ui.initDateTimePickers();

                    popup.dialog({
                        title: title,
                        buttons: buttons,
                        width: 400
                    });
                });
            });

            $(document).on('yukon:da:schedules:create', yukon.ui.initDateTimePickers);

            $(document).on('yukon:da:schedules:edit:submit', function (ev) {
                var dialog = $(ev.target);
                var form = dialog.find('form');
                form.ajaxSubmit({
                    success: function () {
                        window.location.reload();
                    },
                    error: function (xhr) {
                        dialog.html(xhr.responseText);
                        yukon.ui.initDateTimePickers();
                    }
                });
            });
            $(document).on('yukon:da:schedules:delete', function (ev) {
                var dialog = $(ev.target),
                    id = dialog.find('input[name="id"]').val(),
                    deleteMessage = dialog.find('form').data('deleteMessage');
                $.ajax(yukon.url('/capcontrol/schedule/' + id + '/delete'))
                .done(function () {
                    $(document).find('[data-schedule-id="' + id + '"]').remove();
                    $('.main-container').addMessage({
                        message: deleteMessage,
                        messageClass: 'success'
                    });
                });
                dialog.dialog('close');
            });

        },
    };

    return mod;
}());

$(function () { yukon.da.schedules.init(); });