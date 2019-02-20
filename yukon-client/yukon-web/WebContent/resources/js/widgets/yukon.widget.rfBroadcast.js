yukon.namespace('yukon.widget.rfBroadcast');

/**
 * Module for the RF Broadcast Performance Widget
 * @module yukon.widget.rfBroadcast.js
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.rfBroadcast = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    _updateInterval = 900000,
    
    _refreshWidget = function (widgetContainer) {
        $.ajax({
            url: yukon.url('/widget/rfBroadcastWidget/render'),
        }).done(function (data) {
            widgetContainer.html(data);
            var refreshButton = widgetContainer.find('.js-update-rf-broadcast'),
                lastRefresh = widgetContainer.find('.js-last-attempted-refersh').val(),
                lastRefershDateTime = moment(new Date(lastRefresh)).tz(yg.timezone).format(yg.formats.date.both_with_ampm),
                nextRefresh = widgetContainer.find('.js-next-refersh-date-time').val(),
                nextRefreshDateTime = moment(new Date(nextRefresh)).tz(yg.timezone).format(yg.formats.date.both_with_ampm);
            refreshButton.attr('disabled', true);
            refreshButton.attr('title', yg.text.nextRefersh + nextRefreshDateTime);
            widgetContainer.find('.js-last-updated').text(lastRefershDateTime);
            setTimeout(function () {
                refreshButton.attr('disabled', false);
                refreshButton.attr('title', $("#js-update-tooltip").val());
                }, $("#js-force-update-interval").val());
        });
    },
    
    _update = function (widgetContainer) {
        _refreshWidget(widgetContainer);
        setTimeout(function () {_update(widgetContainer)}, _updateInterval);
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            if (_initialized) return;
            
            $(".js-rf-broadcast-widget").each(function (index, widgetContainer) {
                _update($(widgetContainer));
            });
            
            $(document).on('click', '.js-update-rf-broadcast', function () {
                _refreshWidget($(this).closest(".js-rf-broadcast-widget"));
            });
            _initialized = true;
        },
        
    };
    
    return mod;
})();

$(function () { yukon.widget.rfBroadcast.init(); });