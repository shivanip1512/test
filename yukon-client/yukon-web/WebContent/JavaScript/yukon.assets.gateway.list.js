yukon.namespace('yukon.assets.gateway.list');

/**
 * Module that handles the behavior on the gatway list page (localhost:8080/yukon/stars/gateways).
 * @module yukon.assets.gateway.list
 * @requires JQUERY
 * @requires yukon
 */
yukon.assets.gateway.list = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    _text,
    
    /** {String} - The IANA timezone name. */
    _tz = jstz.determine().name(),
    
    _timeFormat = 'MM/DD/YYYY hh:mm A',
    
    _update = function () {
        $.ajax({
            url: yukon.url('/stars/gateways/data'),
            contentType: 'application/json'
        }).done(function (gateways) {
            
            Object.keys(gateways).forEach(function (paoId) {
                
                var 
                clone, timestamp, percent,
                gateway = gateways[paoId],
                data = gateway.data,
                row = $('[data-gateway="' + paoId + '"]');
                
                if (data != null) {
                    
                    timestamp = moment(data.lastCommTimestamp).tz(_tz).format(_timeFormat);
                    percent = data.collectionPercent.toFixed(2);
                    
                    if (!row.data('loaded')) {
                        // This gateway didn't have data at page load.
                        // Replace the 'loading' row with a 'loaded' template row and fill in with data.
                        clone = $('.js-loaded-row').clone();
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
                }
            });
            
        }).always(function () {
            setTimeout(_update, 4000);
        });
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
        timestamp = moment(update.timestamp.millis).tz(_tz).format(_timeFormat);
        
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
            
            _update();
            _updateCerts();
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.assets.gateway.list.init(); });