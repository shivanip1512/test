yukon.namespace('yukon.assets.gateway.list');

/**
 * Module that handles the behavior on the gatway list page (localhost:8080/yukon/stars/gateways).
 * @module yukon.assets.gateway.list
 * @requires JQUERY
 * @requires MOMENT
 * @requires MOMENT_TZ
 * @requires yukon
 */
yukon.assets.gateway.list = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    _text,

    _update = function () {
        $.ajax({
            url: yukon.url('/stars/gateways/data'),
            contentType: 'application/json'
        }).done(function (gateways) {
            
            var dataExists = false;

            Object.keys(gateways).forEach(function (paoId) {
                
                var 
                clone, timestamp, percent,
                gateway = gateways[paoId],
                data = gateway.data,
                row = $('[data-gateway="' + paoId + '"]');
                
                if (data != null) {
                    if (dataExists === false) {
                        dataExists = true;
                        $('.update-servers').find('.disabled').addBack().removeClass('disabled');
                    }
                    
                    timestamp = moment(data.lastCommTimestamp).tz(yg.timezone).format(yg.formats.date.full_hm);
                    percent = data.collectionPercent.toFixed(2);
                    
                    if (!row.data('loaded')) {
                        // This gateway didn't have data at page load.
                        // Replace the 'loading' row with a 'loaded' template row and fill in with data.
                        clone = $('.js-loaded-row').clone();
                        if (gateway.isDataStreamingSupported === true) {
                            clone.find("span.js-streaming-capacity").removeClass("dn");
                        } else {
                            clone.find("div.js-streaming-unsupported").removeClass("dn");
                        }
                        clone.find('.js-view-all-notes').attr("data-pao-id", paoId);
                        clone.attr('data-gateway', paoId)
                        .removeClass('js-loaded-row')
                        .find('.js-gw-name a').text(gateway.name)
                        .attr('href', yukon.url('/stars/gateways/' + paoId));
                        clone.find('.js-gw-sn').text(gateway.rfnId.sensorSerialNumber);
                        row.after(clone);
                        row.remove();
                        row = $('[data-gateway="' + paoId + '"]');
                        row.find('.dropdown-trigger').attr('data-id', paoId).attr('data-name', gateway.name);
                    }
                    
                    row.find('.js-gw-conn-status').attr('title', data.connectionStatusText)
                    .find('.state-box').toggleClass('green', data.connected).toggleClass('red', !data.connected);
                    row.find('.js-gw-ip').text(data.ip);
                    var capacity = row.find('.js-gw-capacity span');
                    var color = "badge-success";
                    if (data.dataStreamingLoadingPercent > 100 ){
                        color = "badge-warning";
                    }
                    if (data.dataStreamingLoadingPercent > 120) {
                        color = "badge-error";
                    }
                    capacity.text(data.dataStreamingLoadingPercent.toFixed(2) + "%");
                    capacity.removeClass("badge-success badge-warning badge-error");
                    capacity.addClass(color);
                    row.find('.js-gw-rv-text').text(data.releaseVersion);
                    if(data.hasUpdateVersion) { 
                        row.find('.js-gateway-update-available').removeClass('dn');
                    } else {
                        if(!row.find('.js-gateway-update-available').hasClass('dn')) {
                            row.find('.js-gateway-update-available').addClass('dn');
                        }
                    }
                    row.find('.js-gw-last-comm').attr('title', timestamp).text(data.lastCommText)
                    .toggleClass('green', data.lastComm == 'SUCCESSFUL')
                    .toggleClass('red', data.lastComm == 'FAILED')
                    .toggleClass('orange', data.lastComm == 'MISSED')
                    .toggleClass('subtle', data.lastComm == 'UNKNOWN');
                    row.find('.js-gw-data-collection .progress-bar').css({ width: data.collectionPercent + '%' })
                    .toggleClass('progress-bar-success', !data.collectionDanger && !data.collectionWarning)
                    .toggleClass('progress-bar-warning', data.collectionWarning)
                    .toggleClass('progress-bar-danger', data.collectionDanger);
                    if (percent == 100) percent = 100;
                    row.find('.js-gw-data-collection-percent').text(percent + '%');
                    row.find('.js-view-all-notes').toggleClass('dn', !gateway.hasNotes);
                }
            });
            
        }).always(function () {
            setTimeout(_update, 4000);
        });
    },
    
    _updateFirmwareUpdates = function () {
        $.ajax({
            url: yukon.url('/stars/gateways/firmware-update/data'),
            contentType: 'application/json'
        }).done(function (updates) {
            Object.keys(updates).forEach(function (updateId) {
                var 
                update = updates[updateId],
                row = $('[data-update-id="' + updateId + '"]');
                
                // If a row doesn't exist yet for this update, clone one from the template
                if (!row.length) {
                    row = $('.js-new-firmware-update').clone()
                          .removeClass('js-new-firmware-update')
                          .attr('data-update-id', updateId);
                    
                    // Update the new row like normal
                    _updateFirmwareRow(row, update);
                    
                    // Append the new row and do some show/hide, in case there were no rows before this one.
                    $('.js-no-firmware-updates').hide();
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
        row.find('.js-firmware-update-pending').text(update.gatewayUpdatesPending)
        .siblings('.js-firmware-update-failed').text(update.gatewayUpdatesFailed)
        .siblings('.js-firmware-update-successful').text(update.gatewayUpdatesSuccessful);
    },
    
    _updateCerts = function () {
        $.ajax({
            url: yukon.url('/stars/gateways/cert-update/data'),
            contentType: 'application/json'
        }).done(function (updates) {
            
            Object.keys(updates).forEach(function (yui) {
                
                var 
                update = updates[yui],
                row = $('[data-yui="' + yui + '"]');
                
                _updateCertRow(row, update);
                
            });
            
        }).always(function () {
            setTimeout(_updateCerts, 4000);
        });
    },
    
    _updateCertRow = function (row, update) {
        
        var 
        gwText, 
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
            .text(yukon.percent(update.failed.length + update.successful.length, 
                    update.gateways.length, 2));
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
            
            /** Create gateway popup opened. */
            $(document).on('yukon:assets:gateway:load', function (ev) {
                if (!Modernizr.input.placeholder) {
                    $('#gateway-create-popup [placeholder]').placeholder();
                }
            });
            
            /** 'Send' button disabled if none of the gateways are selected on the 'Update Firmware Version' popup. */
            $(document).on('change', '.js-send-now, .js-select-all', function (ev) {
            var checkBoxes = $('.js-send-now');
                $('.js-send-btn').prop('disabled', checkBoxes.filter(':checked').length < 1);
                $('.js-send-btn').removeClass('ui-button-disabled ui-state-disabled')
            });
            
            /** 'Save' button clicked on the create gateway popup. */
            $(document).on('yukon:assets:gateway:save', function (ev) {
                
                var btns = $('#gateway-create-popup').closest('.ui-dialog').find('.ui-dialog-buttonset'),
                    primary = btns.find('.js-primary-action'),
                    secondary = btns.find('.js-secondary-action');
                
                yukon.ui.busy(primary);
                secondary.prop('disabled', true);
                
                $('#gateway-create-popup').find('.user-message').remove();
                
                $('#gateway-settings-form').ajaxSubmit({
                    url: yukon.url('/stars/gateways'), 
                    type: 'post',
                    success: function (result, status, xhr, $form) {
                        
                        $('#gateway-create-popup').dialog('close');
                        
                        var row = $('.js-loading-row').clone(),
                            gateway = result.gateway;
                        
                        row.attr('data-gateway', gateway.paoIdentifier.paoId)
                        .removeClass('.js-loading-row');
                        row.find('.js-gw-name').text(gateway.name);
                        row.find('.js-gw-sn').text(gateway.rfnIdentifier.sensorSerialNumber);
                        $('#gateways-table tbody').prepend(row);
                    },
                    error: function (xhr, status, error, $form) {
                        $('#gateway-create-popup').html(xhr.responseText);
                        yukon.ui.initContent($('#gateway-create-popup'));
                        yukon.assets.gateway.shared.adjustTestConnectionButtons();
                    },
                    complete: function () {
                        yukon.ui.unbusy(primary);
                        secondary.prop('disabled', false);
                    }
                });
            });
            
            /** Start clicked on the certificate update popup. */
            $(document).on('yukon:assets:gateway:cert:update', function (ev) {
                
                var popup = $('#gateway-cert-popup'),
                    file = popup.find('input[type=file]'),
                    gateways = popup.find('.js-select-all-item'),
                    chosen = popup.find('.js-select-all-item:checked'),
                    btns = popup.closest('.ui-dialog').find('.ui-dialog-buttonset'),
                    primary = btns.find('.js-primary-action'),
                    secondary = btns.find('.js-secondary-action'),
                    valid = true;
                
                if (!file.val()) {
                    file.addClass('animated shake-subtle error')
                    .one(yg.events.animationend, function() { $(this).removeClass('animated shake-subtle error'); });
                    valid = false;
                }
                
                if (!chosen.length) {
                    gateways.addClass('animated shake-subtle error')
                    .one(yg.events.animationend, function() { $(this).removeClass('animated shake-subtle error'); });
                    valid = false;
                }
                
                if (!valid) return;
                
                yukon.ui.busy(primary);
                secondary.prop('disabled', true);
                
                popup.find('.user-message').remove();
                
                $('#gateway-cert-form').ajaxSubmit({
                    url: yukon.url('/stars/gateways/cert-update'), 
                    type: 'post',
                    success: function (updateText, status, xhr, $form) {
                        
                        popup.dialog('close');
                        
                        var 
                        update = JSON.parse(updateText),
                        row = $('.js-new-cert-update').clone()
                              .removeClass('js-new-cert-update')
                              .attr('data-yui', update.yukonUpdateId);
                        
                        _updateCertRow(row, update);
                        
                        $('.js-no-cert-updates').hide();
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
                
                $.ajax({ url: yukon.url('/stars/gateways/cert-update/' + id + '/details') })
                .done(function (details) {
                    popup.html(details).dialog({
                        title: _text['cert.update.label'] + ': ' + timestamp,
                        width: 620,
                        height: 400,
                        buttons: [{ text: yg.text.close, click: function () { $(this).dialog('close'); } }]
                    });
                });
            });
            
            /** User clicked one of the firmware update timestamp links, show details popup */
            $(document).on('click', '.js-firmware-update-timestamp a', function () {
                
                var id = $(this).closest('tr').data('updateId'),
                    timestamp = $(this).text(),
                    popup = $('#gateway-firmware-details-popup');
                
                $.ajax({ url: yukon.url('/stars/gateways/firmware-update/' + id + '/details') })
                .done(function (details) {
                    popup.html(details).dialog({
                        title: _text['firmware.update.label'] + ': ' + timestamp,
                        width: 900,
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

            _update();
            _updateCerts();
            _updateFirmwareUpdates();
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.assets.gateway.list.init(); });