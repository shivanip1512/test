 yukon.namespace('yukon.widget.program');
 
/**
 * Module for the Program Widget
 * @module yukon.widget.program
 * @requires JQUERY
 * @requires yukon
 */
 yukon.widget.program = (function () {
 
    'use strict';
 
    var
    _initialized = false,
    _updateInterval = 60000,
    _refreshWidget = function (widgetContainer) {
        $.ajax({
            url: yukon.url('/widget/programWidget/render'),
        }).done(function (data) {
            widgetContainer.html(data);
            var json = $.parseJSON($("#js-widget-json-data").text()),
                refreshButton = widgetContainer.find('.js-update-program'),
                lastRefreshDateTime = moment(json.lastUpdateTime.millis).tz(yg.timezone).format(yg.formats.date.both_with_ampm),
                nextRefreshDateTime = moment(json.nextRun.millis).tz(yg.timezone).format(yg.formats.date.both_with_ampm);
            refreshButton.attr('title', yg.text.nextRefresh + nextRefreshDateTime);
            widgetContainer.find('.js-last-updated').text(lastRefreshDateTime);
            setTimeout(function () {
                refreshButton.attr('disabled', false);
                refreshButton.attr('title', json.updateTooltip);
                }, json.forceRefreshInterval);
        });
    },
    
    _update = function (widgetContainer) {
        _refreshWidget(widgetContainer);
        setTimeout(function () {_update(widgetContainer)}, _updateInterval);
    },
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;
 
            $(".js-program-widget").each(function (index, widgetContainer) {
                var json = $.parseJSON($(widgetContainer).find("#js-widget-json-data").text()),
                    lastRefreshDateTime = moment(json.lastUpdateTime.millis).tz(yg.timezone).format(yg.formats.date.both_with_ampm);
                $(widgetContainer).find('.js-last-updated').text(lastRefreshDateTime);
                _update($(widgetContainer));
            });
            
            $(document).on('click', '.js-update-program', function () {
                _refreshWidget($(this).closest(".js-program-widget"));
            });
 
            _initialized = true;
        },
 
        setStartTimeTooltip: function (element) {
            return function (data) {
                element.attr("title", moment(data.value).tz(yg.timezone).format(yg.formats.date.both_with_ampm));
            }
        },

        setStopTimeTooltip: function (element) {
            return function (data) {
                element.attr("title", moment(data.value).tz(yg.timezone).format(yg.formats.date.both_with_ampm));
            }
        }
    };
 
    return mod;
})();
 
$(function () { yukon.widget.program.init(); });