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
            var chartOptionsJSON = {
                    width: chartWidth,
                    height: chartHeight,
                    zoomType: 'x'
                },
                yaxisOptionsJSON = {
                    title: {
                        align: 'middle',
                        rotation: 270
                    }
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
                        fontSize: '15px'
                    }
                },
                xAxis: {
                    type: 'datetime',
                    labels: {
                        formatter: function() {
                          return Highcharts.dateFormat('%b %e', this.value);
                        }
                    }
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