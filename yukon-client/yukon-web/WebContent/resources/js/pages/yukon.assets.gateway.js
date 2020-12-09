yukon.namespace('yukon.assets.gateway');

/**
 * Module that handles the behavior on the gateway list page and manage firmware.
 * @module yukon.assets.gateway
 * @requires JQUERY
 * @requires MOMENT
 * @requires MOMENT_TZ
 * @requires yukon
 */
yukon.assets.gateway = (function () {

    'use strict';
    var
    _initialized = false,

    _update = function () {
        $.ajax({
            url: yukon.url('/stars/gateways/data'),
            contentType: 'application/json'
        }).done(function (gateways) {
            var dataExists = false;
            Object.keys(gateways).forEach(function (paoId) {
                var clone,
                    timestamp,
                    percent,
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
                    if (data.dataStreamingLoadingPercent > 100 ) {
                        color = "badge-warning";
                    }
                    if (data.dataStreamingLoadingPercent > 120) {
                        color = "badge-error";
                    }
                    capacity.text(data.dataStreamingLoadingPercent.toFixed(2) + "%");
                    capacity.removeClass("badge-success badge-warning badge-error");
                    capacity.addClass(color);
                    row.find('.js-gw-rv-text').text(data.releaseVersion);
                    if (data.hasUpdateVersion) { 
                        row.find('.js-gateway-update-available').removeClass('dn');
                    } else {
                        if (!row.find('.js-gateway-update-available').hasClass('dn')) {
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

    mod = {
        
        /** Initialize this module. */
        init: function () {

            if (_initialized) return;

            _update();
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.assets.gateway.init(); });