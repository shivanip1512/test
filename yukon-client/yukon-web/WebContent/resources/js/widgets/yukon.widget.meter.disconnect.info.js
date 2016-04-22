yukon.namespace('yukon.widget.disconnectInfo');

/**
 * Module for the meter disconnnect info widget.
 * 
 * @module yukon.widget.meterInfo
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.disconnectInfo = (function () {

    'use strict';

    var _initialized = false,

    /** @constant {string} - URL for configuring the disconnect address. */
    _saveUrl = yukon.url('/widget/disconnectMeterWidget/saveDisconnectCollar'), 
    _deleteUrl = yukon.url('/widget/disconnectMeterWidget/removeDisconnectCollar'),

    mod = {

        /** Initialize this module. */
        init: function () {
            if (_initialized) return;
            $(document)
                .on('click','#edit-btn',function (ev) {
                        var popup = $('#disconnect-meter-popup'), btns = popup
                            .closest('.ui-dialog').find('.ui-dialog-buttonset'), primary = btns
                            .find('.js-primary-action'), secondary = btns
                            .find('.js-secondary-action');
                        yukon.ui.busy(primary);
                        secondary.prop('disabled', true);
                        popup.find('.user-message').remove();
                        $('#meter-disconnect-addr-form').ajaxSubmit({
                            url: _saveUrl,
                            type: 'post',
                            success: function (result, status, xhr, $form) {
                                $('#disconnect-meter-popup').dialog('close');
                                window.location.href = window.location.href;
                            },
                            error: function (xhr, status, error, $form) {
                                popup.html(xhr.responseText);
                            },
                            complete: function () {
                                yukon.ui.unbusy(primary);
                                secondary.prop('disabled', false);
                            }
                        });
                    });

            /** Delete a Collar Entry */
            $(document).on('yukon:widget:meter:disconnect:delete:addr',
                function (ev) {
                    $.ajax({
                        url: _deleteUrl,
                        type: 'post',
                        data: {
                            shortName: 'disconnectMeterWidget',
                            disconnectAddress: $('#disconnectAddress').val(),
                            deviceId: $('#deviceId').val()
                        },
                        success: function (result, status, xhr, $form) {
                            window.location.href = window.location.href;
                        },
                        error: function (xhr, status, error, $form) {
                            popup.html(xhr.responseText);
                        },
                    });
                });

            $(document).on('click', '#cancel-btn', function (ev) {
                $('#disconnect-meter-popup').dialog('close');
            });
            _initialized = true;
        }
    };
    return mod;
})();

$(function () {
    yukon.widget.disconnectInfo.init();
});