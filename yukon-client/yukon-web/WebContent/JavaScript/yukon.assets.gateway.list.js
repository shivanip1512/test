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
    /** {String} - The IANA timezone name. */
    _tz = jstz.determine().name(),
    
    _timeFormat = 'MM/DD/YYYY hh:mm A',
    
    _initialized = false,
    
    _update = function () {
        $.ajax({
            url: yukon.url('/stars/gateways/data'),
            contentType: 'application/json'
        }).done(function (gateways) {
            var ids = Object.keys(gateways);
            ids.forEach(function (paoId) {
                
                var 
                clone, timestamp, percent,
                gateway = gateways[paoId],
                data = gateway.data,
                row = $('[data-gateway="' + paoId + '"]');
                
                if (data != null) {
                    timestamp = moment(data.lastCommTimestamp).tz(_tz).format(_timeFormat);
                    percent = data.collectionPercent.toFixed(2);
                    
                    if (!row.data('loaded')) {
                        clone = $('.js-loaded-row').clone();
                        clone.attr('data-gateway', paoId)
                        .removeClass('js-loaded-row')
                        .find('.js-gw-name').text(gateway.name)
                        .next('.js-gw-sn').text(gateway.rfnId.sensorSerialNumber);
                        row.after(clone);
                        row.remove();
                        row = $('[data-gateway="' + paoId + '"]');
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
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
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
                    success: function (gateway, status, xhr, $form) {
                        
                        $('#gateway-create-popup').dialog('close');
                        
                        var row = $('.js-loading-row').clone();
                        row.attr('data-gateway', paoId)
                        .removeClass('.js-loading-row');
                        row.find('.js-gw-name').text(gateway.name);
                        row.find('.js-gw-sn').text(gateway.rfnId.sensorSerialNumber);
                        $('#gateways-table tbody').prepend(row);
                    },
                    error: function (xhr, status, error, $form) {
                        $('#gateway-create-popup').html(xhr.responseText);
                    },
                    complete: function () {
                        yukon.ui.unbusy(primary);
                        secondary.prop('disabled', false);
                    }
                });
            });
            
            _update();
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.assets.gateway.list.init(); });