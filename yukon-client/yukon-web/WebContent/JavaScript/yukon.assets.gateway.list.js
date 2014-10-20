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
                    console.log('connected: ' + data.connected);
                    timestamp = moment(data.lastCommTimestamp).tz(_tz).format(_timeFormat);
                    percent = data.collectionPercent.toFixed(2);
                    
                    if (!row.data('loaded')) {
                        clone = $('.js-loaded-row').clone();
                        clone.attr('data-gateway', paoId)
                        .removeClass('.js-loaded-row');
                        clone.find('.js-gw-name').text(gateway.name);
                        clone.find('.js-gw-sn').text(gateway.rfnId.sensorSerialNumber);
                        row.replaceWith(clone);
                        row = clone;
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
                    row.find('.js-data-collection-percent').text(percent + '%');
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
                // TODO
            });
            
            _update();
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.assets.gateway.list.init(); });