yukon.namespace('yukon.da.ivvcChart');

/**
 * Module for the IVVC Chart
 * @module yukon.da.ivvcChart
 * @requires JQUERY
 * @requires yukon
 */
yukon.da.ivvcChart = (function() {

    'use strict';

    var _initialized = false;
    
    var _filterLines = function() {
        var selectedFeederString = $('.js-feeder-filter').chosen().val(),
            selectedFeeders = [],
            chartContainer = $('.js-highchart-graph-container'),
            chart = chartContainer.highcharts(),
            options = chart.options;
        options.tooltip.formatter = _ivvcTooltip;
        if (selectedFeederString.length > 1) {
            selectedFeeders = selectedFeederString.map(function(feeder) {
                return parseInt(feeder);
            });
        } else if (selectedFeederString.length == 1){
            selectedFeeders.push(parseInt(selectedFeederString));
        }
        if (chart != null) {
            $.each(chart.legend.allItems, function(index, legend) {
                $.each(legend.linkedSeries, function(index, line) {
                    var lineFeeder = line.feederId ? line.feederId : line.userOptions.feederId;
                    //all feeders selected
                    if (selectedFeeders.length == 0) {
                        if (legend.visible) {
                            line.show();
                        } else {
                            line.hide();
                        }
                    } else {
                        if (!selectedFeeders.includes(lineFeeder)) {
                            line.hide();
                        } else {
                            if (legend.visible) {
                                line.show();
                            } else {
                                line.hide();
                            }
                        }
                    }
                });
            });
        }

    };
    
    var _ivvcTooltip = function() {
        var tooltipHtml = '',
            firstPoint = true,
            pointsArray = this.points,
            tooltipArray = [];
    
        $.each(pointsArray, function(index, item) {
            //check for more points in series with same x
            var seriesPoints = item.series.data;
            $.each(seriesPoints, function(i, seriesPoint) {
                if (seriesPoint.x == item.point.x) {
                    if (!tooltipArray.includes(seriesPoint.tooltip)) {
                        if (!firstPoint) {
                            tooltipHtml += "<br>";
                        }
                        tooltipHtml += seriesPoint.tooltip;
                        tooltipArray.push(seriesPoint.tooltip);
                        firstPoint = false;
                    }
                }
            });
        });
    
        return tooltipHtml;
    };

    var mod = {

        /** Initialize this module. */
        init : function() {

            if (_initialized)
                return;
            
            //set custom tooltip formatter
            $(document).ready(function() {
                var chartContainer = $('.js-highchart-graph-container'),
                    chart = chartContainer.highcharts(),
                    options = chart.options;
                options.tooltip.formatter = _ivvcTooltip;
            });
            
            $(".js-feeder-filter").chosen({width: "250px"});

            $(document).on('change', '.js-feeder-filter', function(event) {
                _filterLines();
            });
            
            $(document).on('click', '.highcharts-legend-item', function(event) {
                event.preventDefault();
                _filterLines();
            });

            _initialized = true;
        },
        
        /**
         * Method that is meant to work with the cti:dataUpdaterCallback tag
         * 
         * Required parameters: chartId, dataUrl
         */
        reloadChartIfExpired: function(params) {
            params.callback = _filterLines;
            return yukon.highChart.reloadChartIfExpired(params);
        },

    };

    return mod;
})();

$(function() {
    yukon.da.ivvcChart.init();
});