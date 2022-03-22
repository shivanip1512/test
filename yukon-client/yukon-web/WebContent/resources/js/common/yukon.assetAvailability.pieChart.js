yukon.namespace('yukon.assetAvailability.pieChart');
 
/**
 * Module to handle common functionality related to PIE chart in asset availability.
 * 
 * @module yukon.assetAvailability.pieChart
 * @requires yukon
 * @requires JQUERY
 */
yukon.assetAvailability.pieChart = (function () {
    
    'use strict';
 
    var 
    
    mod = {
    
        getData: function (data) {
            return [
                {
                    name: $('.js-asset-ACTIVE').val(),
                    filter: 'ACTIVE',
                    displayPercentage: data.active.percentage < 1 && data.active.percentage != 0 ? '&lt;1%' : yukon.percent(data.active.percentage, 100, 1),
                    y: (data.active.percentage < 1 && data.active.percentage != 0 ? 1 : data.active.percentage),
                    x: data.active.deviceCount,
                    color: yg.colors.GREEN
                },
                {
                    name: $('.js-asset-OPTED_OUT').val(),
                    filter: 'OPTED_OUT',
                    displayPercentage: data.optedOut.percentage < 1 && data.optedOut.percentage != 0 ? '&lt;1%' : yukon.percent(data.optedOut.percentage, 100, 1),
                    y: (data.optedOut.percentage < 1 && data.optedOut.percentage != 0 ? 1 : data.optedOut.percentage),
                    x: data.optedOut.deviceCount,
                    color: yg.colors.BLUE
                },
                {
                    name: $('.js-asset-INACTIVE').val(),
                    filter: 'INACTIVE',
                    displayPercentage: data.inactive.percentage < 1 && data.inactive.percentage != 0 ? '&lt;1%' : yukon.percent(data.inactive.percentage, 100, 1),
                    y: (data.inactive.percentage < 1 && data.inactive.percentage != 0 ? 1 : data.inactive.percentage),
                    x: data.inactive.deviceCount,
                    color: yg.colors.ORANGE
                },
                {
                    name: $('.js-asset-UNAVAILABLE').val(),
                    filter: 'UNAVAILABLE',
                    displayPercentage: data.unavailable.percentage < 1 && data.unavailable.percentage != 0 ? '&lt;1%' : yukon.percent(data.unavailable.percentage, 100, 1),
                    y: (data.unavailable.percentage < 1 && data.unavailable.percentage != 0 ? 1 : data.unavailable.percentage),
                    x: data.unavailable.deviceCount,
                    color: yg.colors.GRAY
                }
            ]
        },
        
        showDetailsPage: function (paoId, chartContainer) {
            var statuses = [];
            //check which legend items are selected
            chartContainer.find('div.highcharts-legend-item').each(function(index, elem) {
                if (!$(elem).hasClass('highcharts-legend-item-hidden')) {
                    var legendValue = $(elem).find('.js-asset-availability-legend-value').text();
                    statuses.push(legendValue);
                }
            });
            window.open(yukon.url('/dr/assetAvailability/detail?paobjectId=' + paoId + '&statuses=' + statuses));
        },
        
        buildChart : function (chart, data, showDetailsPageOnPIEChartClick) {
            debug.log('building chart');
            
            var legendOptionsJSON = {
                    labelFormatter: function (point) {
                        var legendValueText = '<span class="js-asset-availability-legend-value dn">' + this.filter + '</span>';
                        var spanText = '<span class="badge" style="margin:2px;padding:4px;width:60px;color:white;background-color:' + this.color + '">' + this.x + '</span> ';
                        return legendValueText + spanText + this.name + ': ' + this.displayPercentage;
                    }
                },
                plotPieJSON = {
                    className: showDetailsPageOnPIEChartClick ? 'js-asset-availability-data-pie' : '',
                    point: {
                        events: {
                            legendItemClick: function(e) {
                                e.preventDefault();
                            }
                        }
                    }
                },
                chartDimensionJSON = {
                    width: 460,
                    height: 200
                };

            chart.highcharts({
                credits: yg.highcharts_options.disable_credits,
                chart: $.extend({}, yg.highcharts_options.chart_options, chartDimensionJSON),
                legend: $.extend({}, yg.highcharts_options.pie_chart_options.legend, legendOptionsJSON),
                title: yg.highcharts_options.pie_chart_options.title,
                tooltip: yg.highcharts_options.pie_chart_options.tooltip,
                plotOptions: {
                    pie: $.extend({}, yg.highcharts_options.pie_chart_options.plotOptions.pie, plotPieJSON),
                },
                series: [{
                    type: yg.highcharts_options.pie_chart_options.series_type_pie,
                    data: yukon.assetAvailability.pieChart.getData(data)
                }]
            });
            
            chart.removeClass('js-initialize');

        },

    };
 
    return mod;
})(); 
