yukon.namespace('yukon.assets.gateway.manageFirmware');

/**
 * Module that handles the behavior on the gateway manage firmware page (localhost:8080/yukon/stars/gateways/manageFirmware).
 * @module yukon.assets.gateway.manageFirmware
 * @requires JQUERY
 * @requires MOMENT
 * @requires MOMENT_TZ
 * @requires yukon
 */
yukon.assets.gateway.manageFirmware = (function () {

    'use strict';

    var
    _initialized = false,

    _text,

    _updateFirmwareUpdates = function () {
        $.ajax({
            url: yukon.url('/stars/gateways/firmware-update/data'),
            contentType: 'application/json'
        }).done(function (updates) {
            Object.keys(updates).forEach(function (updateId) {
                var update = updates[updateId],
                    row = $('[data-update-id="' + updateId + '"]');

                // If a row doesn't exist yet for this update, clone one from the template
                if (!row.length) {
                    row = $('.js-new-firmware-update').clone()
                                                      .removeClass('js-new-firmware-update')
                                                      .attr('data-update-id', updateId);

                    // Update the new row like normal
                    _updateFirmwareRow(row, update);

                    // Append the new row and do some show/hide, in case there were no rows before this one.
                    $('#firmware-table tbody').prepend(row);
                    $('#firmware-table').show();
                } else {
                    // A row already existed for this update, so just update the values.
                    _updateFirmwareRow(row, update);
                }
            });
        }).always(function () {
            setTimeout(_updateFirmwareUpdates, 4000);
        });
    },

    _updateFirmwareRow = function (row, update) {
        var timestamp = moment(update.sendDate).tz(yg.timezone).format(yg.formats.date.full_hm);

        row.find('.js-firmware-update-timestamp a').text(timestamp);
        row.find('.js-firmware-gateways').text(update.totalGateways);
        row.find('.js-firmware-update-servers').text(update.totalUpdateServers);

        if (update.complete) {
            row.find('.js-firmware-update-status').html('<span class="success">' + _text['complete'] + '</span>');
        } else {
            row.find('.js-firmware-update-status .progress-bar-success')
               .css('width', yukon.percent(update.gatewayUpdatesSuccessful, update.totalGateways, 2))
               .siblings('.progress-bar-danger')
               .css('width', yukon.percent(update.gatewayUpdatesFailed, update.totalGateways, 2));
            row.find('.js-firmware-update-status .js-percent')
               .text(yukon.percent(update.gatewayUpdatesFailed + update.gatewayUpdatesSuccessful, update.totalGateways, 2));
        }
        row.find('.js-firmware-update-pending')
           .text(update.gatewayUpdatesPending)
           .siblings('.js-firmware-update-failed')
           .text(update.gatewayUpdatesFailed)
           .siblings('.js-firmware-update-successful')
           .text(update.gatewayUpdatesSuccessful);
    },

    mod = {
        
        /** Initialize this module. */
        init: function () {

            if (_initialized) return;

            _text = yukon.fromJson('#gateway-text');

            /** 'Send' button disabled if none of the gateways are selected on the 'Update Firmware Version' popup. */
            $(document).on('change', '.js-send-now, .js-select-all', function (ev) {
                var checkBoxes = $('.js-send-now');
                $('.js-send-btn').prop('disabled', checkBoxes.filter(':checked').length < 1);
                $('.js-send-btn').removeClass('ui-button-disabled ui-state-disabled')
            });

            /** User clicked one of the firmware update timestamp links, show details popup */
            $(document).on('click', '.js-firmware-update-timestamp a', function () {
                var id = $(this).closest('tr').data('updateId'),
                    timestamp = $(this).text(),
                    popup = $('#gateway-firmware-details-popup');

                $.ajax({
                    url: yukon.url('/stars/gateways/firmware-update/' + id + '/details')
                }).done(function (details) {
                    popup.html(details).dialog({
                        title: _text['firmware.update.label'] + ': ' + timestamp,
                        width: 900,
                        modal: true,
                        height: 400,
                        buttons: [{ text: yg.text.close, click: function () { $(this).dialog('close'); } }]
                    });
                });
            });

            /** 'Save' button clicked on the set update servers popup. */
            $(document).on('yukon:assets:gateway:update-server:save', function (ev) {
                var popup = $('#firmware-server-popup'),
                    btns = popup.closest('.ui-dialog').find('.ui-dialog-buttonset'),
                    primary = btns.find('.js-primary-action'),
                    secondary = btns.find('.js-secondary-action');

                yukon.ui.busy(primary);
                secondary.prop('disabled', true);

                popup.find('.user-message').remove();

                $('#update-servers-form').ajaxSubmit({
                    success: function (result, status, xhr, $form) {

                        popup.dialog('close');
                        yukon.ui.alertSuccess(result.message);

                    },
                    error: function (xhr, status, error, $form) {
                        popup.html(xhr.responseText);
                        yukon.ui.initContent(popup);
                    },
                    complete: function () {
                        yukon.ui.unbusy(primary);
                        secondary.prop('disabled', false);
                    }
                });
            });

            /** 'Send' button clicked on the Update Firmware Version popup. */
            $(document).on('yukon:assets:gateway:firmware-upgrade:send', function (ev) {
                var popup = $('#send-firmware-upgrade-popup'),
                    btns = popup.closest('.ui-dialog').find('.ui-dialog-buttonset'),
                    primary = btns.find('.js-primary-action'),
                    secondary = btns.find('.js-secondary-action');

                yukon.ui.busy(primary);
                secondary.prop('disabled', true);
                popup.find('.user-message').remove();
                var selectedRows = $('#firmware-upgrade-form').find('tr')
                                                              .filter(function (idx, row) {
                                                                   return $(row).find('.js-send-now').is(':checked');
                                                              });

                /** Object.<string, number>
                 *  Object mapping update servers to the count of gateways selected to use them
                 */
                var updateServersCount = {};
                selectedRows.each(function (idx, row) {
                    var updateServer = $(row).find('.js-update-server:input').val();
                    if (updateServersCount[updateServer] !== undefined) {
                        updateServersCount[updateServer] += 1;
                    } else {
                        updateServersCount[updateServer] = 1;
                    }
                });

                var updateServers = Object.keys(updateServersCount);

                var confirmMessage = popup.data('confirmMultipleText')
                                          .replace('{0}', selectedRows.length)
                                          .replace('{1}', updateServers.length);

                yukon.ui.confirm({
                    dialog: popup,
                    confirmText: confirmMessage,
                    event: 'yukon:assets:gateway:firmware-upgrade:confirmed',
                    yesText: yg.text.confirm,
                    noText: yg.text.cancel
                });
            });

            $(document).on('yukon:assets:gateway:firmware-upgrade:confirmed', function (ev) {
                var popup = $(ev.target),
                    btns = popup.closest('.ui-dialog').find('.ui-dialog-buttonset'),
                    primary = btns.find('.js-primary-action'),
                    secondary = btns.find('.js-secondary-action');

                $('#firmware-upgrade-form').ajaxSubmit({
                    success: function (result, status, xhr, $form) {

                        popup.dialog('close');
                        yukon.ui.alertSuccess(result.message);

                    },
                    error: function (xhr, status, error, $form) {
                        popup.html(xhr.responseText);
                        yukon.ui.initContent(popup);
                    },
                    complete: function () {
                        yukon.ui.unbusy(primary);
                        secondary.prop('disabled', false);
                    }
                });
            });

            _updateFirmwareUpdates();
            _initialized = true;
        }

    };

    return mod;
})();

$(function () { yukon.assets.gateway.manageFirmware.init(); });