yukon.namespace('yukon.assets.gateway.manageCertificate');

/**
 * Module that handles the behavior on the gateway manage certificate page (localhost:8080/yukon/stars/gateways/manageCertificates).
 * @module yukon.assets.gateway.manageCertificate
 * @requires JQUERY
 * @requires MOMENT
 * @requires MOMENT_TZ
 * @requires yukon
 */
yukon.assets.gateway.manageCertificate = (function () {
    
    'use strict';

    var
    _initialized = false,

    _text,

    _updateCerts = function () {
        $.ajax({
            url: yukon.url('/stars/gateways/cert-update/data'),
            contentType: 'application/json'
        }).done(function (updates) {
            Object.keys(updates).forEach(function (yui) {
                var update = updates[yui],
                    row = $('[data-yui="' + yui + '"]');
                _updateCertRow(row, update);
            });
            
        }).always(function () {
            setTimeout(_updateCerts, 4000);
        });
    },

    _updateCertRow = function (row, update) {
        var gwText,
            timestamp = moment(update.timestamp).tz(yg.timezone).format(yg.formats.date.full_hm);

        row.find('.js-cert-update-timestamp a').text(timestamp);
        row.find('.js-cert-update-file').text(update.fileName);
        gwText = update.gateways[0].name;
        if (update.gateways.length > 1) {
            gwText += ', ' + update.gateways[1].name;
        }
        if (update.gateways.length > 2) {
            gwText += _text['cert.update.more'].replace('{0}', update.gateways.length - 2);
        }
        row.find('.js-cert-update-gateways').text(gwText);
        if (update.complete) {
            row.find('.js-cert-update-status').html('<span class="success">' 
                    + _text['complete'] + '</span>');
        } else {
            row.find('.js-cert-update-status .progress-bar-success')
               .css('width', yukon.percent(update.successful.length, update.gateways.length, 2))
               .siblings('.progress-bar-danger')
               .css('width', yukon.percent(update.failed.length, update.gateways.length, 2));
            row.find('.js-cert-update-status .js-percent')
               .text(yukon.percent(update.failed.length + update.successful.length, update.gateways.length, 2));
        }
        row.find('.js-cert-update-pending').text(update.pending.length)
        .siblings('.js-cert-update-failed').text(update.failed.length)
        .siblings('.js-cert-update-successful').text(update.successful.length);
    },

    mod = {
        
        /** Initialize this module. */
        init: function () {

            if (_initialized) return;

            _text = yukon.fromJson('#gateway-text');

            /** Start clicked on the certificate update popup. */
            $(document).on('yukon:assets:gateway:cert:update', function (ev) {
                var popup = $('#gateway-cert-popup'),
                    file = popup.find('input[type=file]'),
                    gateways = popup.find("input[name='gatewayIdList']").map(function () {return $(this).val();}).get().join(","),
                    btns = popup.closest('.ui-dialog').find('.ui-dialog-buttonset'),
                    primary = btns.find('.js-primary-action'),
                    secondary = btns.find('.js-secondary-action'),
                    valid = true;

                if (!file.val()) {
                    popup.addMessage({message: $('.js-no-file-upload').val(), messageClass: 'error'});
                    valid = false;
                } else if (gateways.length === 0) {
                    popup.addMessage({message: $('.js-no-gateway-selected').val(), messageClass: 'error'});
                    valid = false;
                }

                if (!valid) return;
                yukon.ui.busy(primary);
                secondary.prop('disabled', true);
                popup.find('.user-message').remove();

                $('#gateway-cert-form').ajaxSubmit({
                    url: yukon.url('/stars/gateways/cert-update'), 
                    type: 'post',
                    data: { gateways: gateways},
                    success: function (updateText, status, xhr, $form) {
                        popup.dialog('close');
                        var update = JSON.parse(updateText),
                            row = $('.js-new-cert-update').clone().removeClass('js-new-cert-update').attr('data-yui', update.yukonUpdateId);
                        // Add an entry to the certificates updates information table
                        _updateCertRow(row, update);
                        $('#cert-table tbody').prepend(row);
                        $('#cert-table').show();
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

            /** User clicked one of the cert update timestamp links, show details popup */
            $(document).on('click', '.js-cert-update-timestamp a', function () {
                var id = $(this).closest('tr').data('yui'),
                    timestamp = $(this).text(),
                    popup = $('#gateway-cert-details-popup');

                $.ajax({
                    url: yukon.url('/stars/gateways/cert-update/' + id + '/details')
                }).done(function (details) {
                    popup.html(details).dialog({
                        title: _text['cert.update.label'] + ': ' + timestamp,
                        width: 620,
                        modal: true,
                        height: 400,
                        buttons: [{ text: yg.text.close, click: function () { $(this).dialog('close'); } }]
                    });
                });
            });

            _updateCerts();
            _initialized = true;
        }
        
    };

    return mod;
})();

$(function () { yukon.assets.gateway.manageCertificate.init(); });