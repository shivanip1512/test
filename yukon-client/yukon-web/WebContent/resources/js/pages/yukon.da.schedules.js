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
                
                yukon.ui.initDateTimePickers();

                popup.load(yukon.url('/capcontrol/schedules/' + id), function () {

                    var mode = popup.find('form').data('mode'),
                        title = popup.find('.js-popup-title')[0].value,
                        confirmText = popup.find('.js-confirm-delete')[0].value,
                        buttons = [],
                        deleteButton = {
                            text: yg.text.deleteButton,
                            click: function (ev) {
                                yukon.ui.confirm({
                                    confirmText: confirmText,
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


                    popup.dialog({
                        title: title,
                        buttons: buttons,
                        width: 400,
                        maxHeight: 400
                    });
                });
            });

            $(document).on('yukon:da:schedules:create', function (ev) {
                yukon.ui.initDateTimePickers();
                var contentDialog = $('#contentPopup');
                //close create popup if it exists
                if (contentDialog.is(':visible')) {
                    $('#contentPopup').dialog('close');
                }
            });

            $(document).on('change', '#cmd', function() {
                var dmvTestCommand = $("input[name=dmvTestCommand]").val();
                var commandName = $('#cmd option:selected').val();
                $('#dmvTestDiv').toggleClass('dn', commandName != dmvTestCommand);
            });

            $(document).on('yukon:da:schedules:edit:submit', function (ev) {

                var dialog = $(ev.target);

                dialog.find('form').ajaxSubmit({
                    success: function () {
                        window.location.href = yukon.url('/capcontrol/schedules');
                    },
                    error: function (xhr) {
                        dialog.html(xhr.responseText);
                        yukon.ui.initDateTimePickers();
                    }
                });
            });
            $(document).on('yukon:da:schedules:delete', function (ev) {

                var dialog = $(ev.target);
                var id = dialog.find('input[name="id"]').val();
                var form = dialog.find('form[data-delete-message]');
                var deleteMessage = form.data('deleteMessage');
                
                form.ajaxSubmit({
                   success: function (data, status, xhr) {
                       $(document).find('[data-schedule-id="' + id + '"]').remove();
                       $('.main-container').addMessage({
                           message: deleteMessage,
                           messageClass: 'success'
                       });
                   },
                   error: function (xhr, status, error) {
                       window.location.reload();
                   }
                });

                dialog.dialog('close');
            });

        },
    };

    return mod;
}());

$(function () { yukon.da.schedules.init(); });