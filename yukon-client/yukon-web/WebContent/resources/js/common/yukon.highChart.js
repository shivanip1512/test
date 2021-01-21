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
        });
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
                if (item.data.length > 0) {
                    gridLineWidth = 1;
                }
            });
            var chartOptionsJSON = {
                    width: chartWidth,
                    height: chartHeight,
                    zoomType: 'x',
                    plotBorderWidth: 2,
                },
                yaxesOptions = [];
            
            chartContainer.highcharts({
                plotOptions: {
                    series: {
                        animation: false
                    }
                },
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
                    type: 'datetime',
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
                    tickWidth: 0
                },
                tooltip: {
                    shared: true,
                    useHTML: true,
                    formatter: function () {
                        var tooltipHtml = '',
                            pointsArray = this.points;
                        
                        $.each(pointsArray, function(index, item) {
                            tooltipHtml += item.point.tooltip;
                            if (pointsArray.length > 1 && index !== pointsArray.length-1) {
                                tooltipHtml += "<br>";
                            }
                        });

                        return tooltipHtml;
                    },
                },
                yAxis: jsonResponse.yaxis,
                series: jsonResponse.seriesDetails,
                plotOptions: {
                    series: {
                        states: {
                            inactive: {
                                opacity: 1
                            }
                        }
                    },
                    column: {
                        grouping: false,
                    }
                }
            });
        },

    };
 
    return mod;
})(); 