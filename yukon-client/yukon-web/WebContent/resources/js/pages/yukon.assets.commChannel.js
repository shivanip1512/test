yukon.namespace('yukon.assets.commChannel.js');
 
/** 
 * Module that handles the behavior on the comm channel
 * @module yukon.assets.commChannel.js
 * @requires JQUERY 
 * @requires yukon 
 */
yukon.assets.commChannel = (function () {
 
    'use strict';

    var
    _initialized = false,
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;

            $(document).on("yukon:commChannel:delete", function () {
                yukon.ui.blockPage();
                $('#delete-commChannel-form').submit();
            });

            $(document).on('change', '#js-comm-channel-type', function (event) {
                var type = $(this).val(); 
                yukon.ui.block($('js-commChannel-container'));
                var name = $('#js-comm-channel-name').val(),
                    popup = $('#js-create-comm-channel-popup'),
                    globalError = popup.find('.js-global-error'),
                    globalErrorFound = globalError.is(":visible");

                if (globalErrorFound) {
                    globalError.remove();
                }

                $.ajax({
                    url: yukon.url('/stars/device/commChannel/create/' + type),
                    type: 'get',
                    data: {name: name}
                }).done(function(data) {
                     $('.commChannel-create-form').html(data);
                     yukon.ui.unblock($('js-commChannel-container'));
               });
            });

            $(document).on('change', '.js-physical-port', function (event) {
                yukon.comm.channel.togglePhysicalPort($(this).closest('.ui-dialog'));
            });

            $(document).on("yukon:assets:commChannel:create", function(event) {
                var dialog = $(event.target),
                    form = dialog.find('.commChannel-create-form'),
                    popup = $('#js-create-comm-channel-popup'),
                    errorMessage = popup.find('.user-message'),
                    errorMessageFound = errorMessage.is(":visible"),
                    globalError = popup.find('.js-global-error'),
                    globalErrorFound = globalError.is(":visible"),
                    userPortField = popup.find('.js-user-physical-port-value'),
                    userPortEntered = userPortField.exists() && !userPortField.hasClass('dn');

                popup.find('.js-physical-port').prop('disabled', userPortEntered);
                popup.find('.js-user-physical-port-value').prop('disabled', !userPortEntered);

                if (!errorMessageFound || globalErrorFound) {
                    $.ajax({
                        type: "POST",
                        url: yukon.url("/stars/device/commChannel/save"),
                        data: form.serialize()
                    }).done(function (data) {
                        if (typeof data.id !== 'undefined') {
                            window.location.href = yukon.url('/stars/device/commChannel/' + data.id);
                        } else {
                            window.location.href = window.location.href;
                        }
                        dialog.dialog('close');
                        dialog.empty();
                    }).fail(function (xhr, status, error){
                        popup.html(xhr.responseText);
                        yukon.ui.initContent(popup);
                        yukon.comm.channel.loadPhysicalPort(popup);
                        yukon.comm.channel.formatPhysicalPortErrors(popup);
                        yukon.ui.unblockPage();
                    });
                } else {
                    yukon.ui.unblockPage();
                    window.location.href = window.location.href;
                }
            });

            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.assets.commChannel.init(); });