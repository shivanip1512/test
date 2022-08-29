yukon.namespace('yukon.highChart');
 
/**
 * Module to build charts using highchart charting library.
 * 
 * @module yukon.highChart
 * @requires yukon
 * @requires JQUERY
 * @requires HIGHCHART
 */
yukon.highChart = (function () {
    
    'use strict';
 
    var 
    
    _buildChart = function (parameters) {
        $.ajax({
            url: parameters.chartUrl,
            dataType : 'json'
        }).done(function (response, textStatus, jqXHR) {
            yukon.highChart.buildChart($(parameters.containerIdentifier), response, parameters.title,
                parameters.height, parameters.width);
            if (parameters.callback) {
                parameters.callback();
            }
        });
    },
    
    _validateReloadParams = function(params) {
        if (typeof params.chartId === 'undefined') throw "no chartId specified";
        if (typeof params.dataUrl === 'undefined') throw "no dataUrl specified";
    },
    
    mod = {
    
        reloadChartAtInterval: function (parameters) {
            setInterval(function(){
                _buildChart(parameters);
            }, parameters.reloadInterval * 1000);
            _buildChart(parameters);
        },
    
        buildChart : function (chartContainer, jsonResponse, title, chartHeight, chartWidth) {
            var gridLineWidth = 0;
            $.each(jsonResponse.seriesDetails, function(index, item) {
                if (item && item.data && item.data.length > 0) {
                    gridLineWidth = 1;
                }
            });
            var chartOptionsJSON = {
                    width: chartWidth,
                    height: chartHeight,
                    zoomType: 'x',
                    plotBorderWidth: 2,
                };
            
            chartContainer.highcharts({
                credits: yg.highcharts_options.disable_credits,
                chart: $.extend({}, yg.highcharts_options.chart_options, chartOptionsJSON),
                title: {
                    text: title,
                    align: 'center',
                    style: {
                        color: yg.colors.BLACK,
                        fontSize: '14px'
                    }
                },
                xAxis: {
                    type: jsonResponse.xaxis.type,
                    dateTimeLabelFormats: {
                        millisecond: '%H:%M:%S.%L',
                        second: '%H:%M:%S',
                        minute: '%H:%M',
                        hour: '%H:%M',
                        day: '%b %e',
                        week: '%b %e',
                        month: '%b \'%y',
                        year: '%Y'
                    },
                    min: jsonResponse.xaxis.min,
                    max: jsonResponse.xaxis.max,
                    gridLineWidth: gridLineWidth,
                    tickWidth: 0,
                    minPadding: jsonResponse.xaxis.minPadding,
                    maxPadding: jsonResponse.xaxis.maxPadding
                },
                tooltip: {
                    shared: true,
                    useHTML: true,
                    outside: true,
                    style: {
                        fontSize: "10px"
                    },
                    formatter: function () {
                        var tooltipHtml = '',
                            firstPoint = true,
                            pointsArray = this.points;
                    
                        $.each(pointsArray, function(index, item) {
                            //check for more points in series with same x
                            var seriesPoints = item.series.data;
                            $.each(seriesPoints, function(i, seriesPoint) {
                                if (seriesPoint.x == item.point.x) {
                                    if (!firstPoint) {
                                        tooltipHtml += "<br>";
                                    }
                                    tooltipHtml += seriesPoint.tooltip;
                                    firstPoint = false;
                                }
                            });
                        });
    
                        return tooltipHtml;
                    },
                    positioner: jsonResponse.tooltip ? jsonResponse.tooltip.positioner : '',
                },
                yAxis: jsonResponse.yaxis,
                series: jsonResponse.seriesDetails,
                time: {
                    timezone: yg.timezone
                },
                plotOptions: {
                    series: {
                        animation: false,
                        states: {
                            inactive: {
                                opacity: 1
                            }
                        },
                        marker: {
                            symbol: 'circle',
                            enabled: true
                        }
                    },
                    column: {
                        grouping: false,
                    }
                }
            });
        },
        
        redrawTrendChart: function (dialog, ui) {
            var heightDifference = ui.originalSize.height - ui.size.height,
                widthDifference = ui.originalSize.width - ui.size.width,
                newChartHeight = dialog.find('.highcharts-container').height() - heightDifference,
                newChartWidth = dialog.find('.highcharts-container').width() - widthDifference;
            
            if (ui.originalSize.width > ui.size.width) {
                newChartWidth = newChartWidth - 2;
            } else  {
                newChartWidth = newChartWidth + 2;
            }
            if (ui.originalSize.height > ui.size.height) {
                newChartHeight = newChartHeight - 2;
            } else  {
                newChartHeight = newChartHeight + 2;
            }
            
            dialog.find(".js-highchart-graph-container").each(function (index, chartContainer) {
                var chosenChart,
                    chartId = $(chartContainer).attr('id');
                Highcharts.charts.forEach(function(chart, index) {
                    if (chart.renderTo.id === chartId) {
                        chosenChart = chart;
                        chosenChart.setSize(newChartWidth, newChartHeight);
                    }
                });
                if ($(chartContainer).closest(".js-trend-analysis")) {
                    $(chartContainer).parent(".js-trend-analysis").height(newChartHeight);
                    $(chartContainer).parent(".js-trend-analysis").width(newChartWidth);
                }
            });
            
            dialog.height(dialog.parent().height()-dialog.prev('.ui-dialog-titlebar').height()-34);
            dialog.width(dialog.prev('.ui-dialog-titlebar').width() - dialog.prev('.ui-dialog-titlebar').find('.ui-dialog-titlebar-close').width());
        },
        
        /**
         * Method that is meant to work with the cti:dataUpdaterCallback tag
         * 
         * Required parameters: chartId, dataUrl
         */
        reloadChartIfExpired: function(params) {
            var chartId,
                dataUrl,
                callback,
                newLargestTime;
            /* validation */
            _validateReloadParams(params);
            chartId = params.chartId;
            dataUrl = params.dataUrl;
            callback = params.callback;
            //assumes data is of type Hash
            return function(data) {
                newLargestTime = data.largestTime;
                var chartContainer = $('#js-chart-container-' + chartId),
                    chart = chartContainer.highcharts();
                if (typeof chart.mostRecentPointTime === 'undefined') {
                    chart.mostRecentPointTime = newLargestTime;
                }
                if (chart.mostRecentPointTime > 0 &&
                    newLargestTime > chart.mostRecentPointTime) {
                    chart.mostRecentPointTime = newLargestTime;
                    var parameters = {
                        containerIdentifier: '#js-chart-container-' + chartId,
                        title: chart.title.textStr,
                        height: chart.chartHeight,
                        width: chart.chartWidth,
                        chartUrl: dataUrl,
                        callback: callback
                    };
                    _buildChart(parameters);
                }
            };
        },
        
    };
 
    return mod;
})(); 