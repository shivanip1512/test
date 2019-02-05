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
    _updateInterval = 1800000, // 30 minutes
    _updateTimeout = null,
    
    _updateChart = function (widgetContainer, animateSeriesPloting) {
        widgetContainer.removeMessages();
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
            if (trend.hasOwnProperty('errorMessage')) {
                widgetContainer.addMessage({
                    message: trend.errorMessage,
                    messageClass: "error"
                });
                widgetContainer.find(".js-details-section").addClass("dn");
            } else {
                widgetContainer.find(".js-details-section").removeClass("dn");
                var chartContainer = widgetContainer.find('.js-trends-chart'),
                chartOptions = {
                    rangeSelector: {
                        rangeSelectorButtons : rangeSelectorButtons,
                        selected: 1,
                        inputStyle: {
                            color: '#9e9d9d'
                        }
                    },
                    chartWidth : widgetContainer.closest('.widgetWrapper').width() - 20,
                    chartHeight : 425,
                    animateSeriesPloting: animateSeriesPloting
                };
                if (widgetContainer.exists()) {
                    // disable editing the date fields. 
                    Highcharts.wrap(Highcharts.RangeSelector.prototype, 'drawInput', function (proceed, name) {
                        proceed.call(this, name);
                        if ($(this.chart.renderTo).hasClass('js-trends-chart')) {
                            this[name + 'DateBox'].attr("fill", "#f7f7f7");
                            this[name + 'DateBox'].on('click', function () {} );
                        }
                    });
                    var highChartOptions = {
                            lang:{
                                rangeSelectorZoom: '',
                                rangeSelectorFrom: ''
                            }
                    };
                    yukon.trends.buildChart(chartContainer, trend, chartOptions, highChartOptions);
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
            }
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
                    widgetHeight = widgetContainer.height(),
                    chartHeight = widgetContainer.find(".js-trends-chart").height(),
                    detailsUrl = yukon.url("/tools/trends/" + trendId);
                widgetContainer.find(".js-trends-details-link").attr("href", detailsUrl);
                widgetContainer.find(".js-trends-chart").data("trend", trendId);
                widgetContainer.height(widgetHeight);
                widgetContainer.find(".js-trends-chart").height(chartHeight);
                widgetContainer.find(".js-trends-chart").empty();
                _updateChart(widgetContainer, true);
            });
            
            _initialized = true;
            
        }
    };
    
    return mod;
})();

$(function () { yukon.widget.trends.init(); });