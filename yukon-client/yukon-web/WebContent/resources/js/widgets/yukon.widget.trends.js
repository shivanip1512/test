yukon.namespace('yukon.widget.trends');

/**
 * Module for the Trends Widget.
 * @module yukon.widget.trends
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.trends = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    _updateInterval = 1800000,
    _updateTimeout = null,
    
    _updateChart = function (widgetContainer, animateSeriesPloting) {
        if (animateSeriesPloting) {
            yukon.ui.block(widgetContainer, 200);
        }
        
        var trendId = widgetContainer.find('.js-trends-chart').data("trend"),
            labels = JSON.parse(decodeURIComponent(widgetContainer.closest('.widgetWrapper').find('.label-json').html())),
            rangeSelectorButtons = [{
                type: 'day',
                count: 1,
                text: labels.day,
                value: 'DAY_1'
            }, {
                type: 'week',
                count: 1,
                text: labels.week,
                value: 'WEEK_1'
            }, {
                type: 'month',
                count: 1,
                text: labels.month,
                value: 'MONTH_1'
            }];
        
        $.getJSON(yukon.url('/tools/trends/widgetDisplay/' + trendId + '/data'), function (trend) {
            var chartContainer = widgetContainer.find('.js-trends-chart'),
                chartOptions = {
                    rangeSelector: {
                        inputEnabled: true,
                        rangeSelectorButtons : rangeSelectorButtons,
                        selected: 1
                    },
                    chartWidth : widgetContainer.closest('.widgetWrapper').width() - 20,
                    // TODO: Explain the formula here.
                    chartHeight : trend.isDataAvaliableForAnySeries ? 470 + 18 * 4 : 235 + 18 * 4,
                    animateSeriesPloting: animateSeriesPloting
            };
            if (widgetContainer.exists()) {
                Highcharts.wrap(Highcharts.RangeSelector.prototype, 'drawInput', function (proceed, name) {
                    proceed.call(this, name);
                    this[name + 'DateBox'].attr("fill", "#f7f7f7");
                    this[name + 'DateBox'].on('click', function () {} );
                });
                yukon.trends.buildChart(chartContainer, chartOptions, trend);
                chartContainer.find(".highcharts-input-group").attr('cursor','default');
            }
            var refreshButton = widgetContainer.find('.js-trends-update'),
                dateTime = moment(trend.lastAttemptedRefresh.millis).tz(yg.timezone).format(yg.formats.date.both_with_ampm),
                nextRunDateTime = moment(trend.nextRun.millis).tz(yg.timezone).format(yg.formats.date.both_with_ampm),
                tooltipTemplate = widgetContainer.find('.js-tooltip-template').val();
            refreshButton.prop('title', tooltipTemplate + nextRunDateTime);
            refreshButton.attr('disabled', true);
            widgetContainer.find('.js-last-updated').text(dateTime);

            setTimeout(function() { 
                refreshButton.attr('disabled', false);
                refreshButton.prop('title', trend.updateTooltip);
                }, trend.refreshMillis);
        }).always(function () {
            yukon.ui.unblock(widgetContainer, 200);
        });
        
        if (_updateTimeout) {
            clearTimeout(_updateTimeout);
        }
        _updateTimeout = setTimeout(function () { _updateChart(widgetContainer)}, _updateInterval);
        
    },

    mod = {
        
        /** Initialize this module. */
        init : function (trendId) {
            
            if (_initialized) return;
            
            $(".js-trends-widget-container").each(function (index, widgetContainer) {
                _updateChart($(widgetContainer), true);
            });
            
            $(document).on('click', '.js-trends-update', function (event) {
                _updateChart($(this).closest('.js-trends-widget-container'), true);
            });
            
            $(document).on("yukon:trends:selection", function (event, items, picker) {
                var trendId = $(picker.inputAreaDiv).find("input[name=js-trend-id]").val(),
                    widgetContainer = $(picker.inputAreaDiv).closest('.widgetWrapper').find('.js-trends-widget-container'),
                    detailsUrl = yukon.url("/tools/trends/" + trendId);
                widgetContainer.find(".js-details-link").attr("href", detailsUrl);
                widgetContainer.find(".js-trends-chart").data("trend", trendId);
                widgetContainer.find(".js-trends-chart").empty();
                _updateChart(widgetContainer, true);
            });
            
            _initialized = true;
            
        }
    };
    
    return mod;
})();

$(function () { yukon.widget.trends.init(); });