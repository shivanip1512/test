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
                    labels: {
                        formatter: function() {
                            return Highcharts.dateFormat(jsonResponse.xaxis.datetimeFormat, this.value);
                        }
                    },
                    min: jsonResponse.xaxis.min,
                    max: jsonResponse.xaxis.max,
                    gridLineWidth: gridLineWidth,
                    tickWidth: 0
                },
                tooltip: {
                    useHTML: true,
                    formatter: function () {
                        return this.point.tooltip;
                    }
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