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
                    pointId: data.active.pointId,
                    displayPercentage: data.active.percentage < 1 && data.active.percentage != 0 ? '&lt;1%' : yukon.percent(data.active.percentage, 100, 1),
                    y: (data.active.percentage < 1 && data.active.percentage != 0 ? 1 : data.active.percentage),
                    x: data.active.deviceCount,
                    color: yg.colors.GREEN
                },
                {
                    name: $('.js-asset-OPTED_OUT').val(),
                    pointId: data.optedOut.pointId,
                    displayPercentage: data.optedOut.percentage < 1 && data.optedOut.percentage != 0 ? '&lt;1%' : yukon.percent(data.optedOut.percentage, 100, 1),
                    y: (data.optedOut.percentage < 1 && data.optedOut.percentage != 0 ? 1 : data.optedOut.percentage),
                    x: data.optedOut.deviceCount,
                    color: yg.colors.BLUE
                },
                {
                    name: $('.js-asset-INACTIVE').val(),
                    pointId: data.inactive.pointId,
                    displayPercentage: data.inactive.percentage < 1 && data.inactive.percentage != 0 ? '&lt;1%' : yukon.percent(data.inactive.percentage, 100, 1),
                    y: (data.inactive.percentage < 1 && data.inactive.percentage != 0 ? 1 : data.inactive.percentage),
                    x: data.inactive.deviceCount,
                    color: yg.colors.ORANGE
                },
                {
                    name: $('.js-asset-UNAVAILABLE').val(),
                    pointId: data.unavailable.pointId,
                    displayPercentage: data.unavailable.percentage < 1 && data.unavailable.percentage != 0 ? '&lt;1%' : yukon.percent(data.unavailable.percentage, 100, 1),
                    y: (data.unavailable.percentage < 1 && data.unavailable.percentage != 0 ? 1 : data.unavailable.percentage),
                    x: data.unavailable.deviceCount,
                    color: yg.colors.GRAY
                }
            ]
        },
        
        showDetailsPage: function (paoId, chartContainer) {
            window.open(yukon.url('/dr/assetAvailability/detail?paobjectId=' + paoId));
        },
        
        buildChart : function (chart, data, showDetailsPageOnPIEChartClick) {
            debug.log('building chart');
            
            var legendOptionsJSON = {
                    labelFormatter: function (point) {
                        var spanText = '<span class="badge" style="margin:2px;padding:4px;width:60px;color:white;background-color:' + this.color + '">' + this.x + '</span> ';
                        return spanText + this.name + ': ' + this.displayPercentage;
                    }
                },
                plotPieJSON = {
                    className: showDetailsPageOnPIEChartClick ? 'js-asset-availability-data-pie' : '',
                    point: {
                        events: {
                            legendItemClick: function(e) {
                                var pointId = e.target.pointId,
                                    popup = $('.js-point-data-dialog');
                                if (pointId != null) {
                                    popup.load(yukon.url('/meter/historicalReadings/view?pointId=' + pointId), function () {
                                        popup.dialog({
                                            title : popup.find('.js-popup-title').val(),
                                            width : 600,
                                            height : 400,
                                            modal : true,
                                            autoOpen : true
                                        });
                                    });
                                } else {
                                    popup.html(yg.text.pointDataNotFound).dialog({
                                        title : yg.text.pointNotFound,
                                        width : 400,
                                        height : 200,
                                        modal : true,
                                        autoOpen : true
                                    });
                                }
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
