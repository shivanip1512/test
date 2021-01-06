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
    
    mod = {
    
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
                    plotBorderWidth: 2
                },
                yaxisOptionsJSON = {
                    title: {
                        align: 'middle',
                        rotation: 270
                    },
                    gridLineWidth: gridLineWidth
                },
                yaxesOptions = [];

            $.each(jsonResponse.yaxis, function (index, item) {
                $.extend(true, jsonResponse.yaxis[index], yaxisOptionsJSON)
            });
            
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
                series: jsonResponse.seriesDetails
            });
        },

    };
 
    return mod;
})(); 