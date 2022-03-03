yukon.namespace('yukon.assets.gateway.list');

/**
 * Module that handles the behavior on the gateway list page (localhost:8080/yukon/stars/gateways).
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

    _update = function () {
        $.ajax({
            url: yukon.url('/stars/gateways/data'),
            contentType: 'application/json'
        }).done(function (gateways) {
            Object.keys(gateways).forEach(function (paoId) {
                var clone,
                    timestamp,
                    percent,
                    gateway = gateways[paoId],
                    data = gateway.data,
                    row = $('[data-gateway="' + paoId + '"]');

                if (data != null) {

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
                        row.attr('data-gateway', gateway.paoIdentifier.paoId).removeClass('.js-loading-row');
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
            
            /** 'Update' button clicked on the Update Gateways popup. */
            $(document).on('yukon:assets:gateways:update', function (ev) {
            	var popup = $('#update-gateways-popup'),
                	btns = popup.closest('.ui-dialog').find('.ui-dialog-buttonset'),
                	primary = btns.find('.js-primary-action'),
                	secondary = btns.find('.js-secondary-action');
                yukon.ui.busy(primary);
                secondary.prop('disabled', true);
                $('#update-gateways-form').ajaxSubmit({
                    success: function (result, status, xhr, $form) {
                        popup.dialog('close');
                        if (result.successMessage) {
                        	yukon.ui.alertSuccess(result.successMessage);
                        }
                        if (result.errorMessage) {
                        	yukon.ui.alertError(result.errorMessage);
                        }
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
            
            $(document).on('click', '.js-download-network-map', function () {
                $('.js-download-warning').removeClass('dn');
                $(this).addClass('very-disabled-look');
                //enable download option again after 5 minutes
                setTimeout(mod.showDownload, 300000);
                window.location = yukon.url('/stars/comprehensiveMap/downloadNetworkInfo');
            });

            _update();
            _initialized = true;
        },
        
        showDownload: function () {
            $('.js-download-network-map').removeClass('very-disabled-look');
            $('.js-download-warning').addClass('dn');
        }
        
    };
    
    return mod;
})();

$(function () { yukon.assets.gateway.list.init(); });