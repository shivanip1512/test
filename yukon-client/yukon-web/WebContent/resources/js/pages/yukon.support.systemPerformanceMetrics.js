yukon.namespace('yukon.support.systemPerformanceMetrics');

/**
 * Module that handles the behavior on System Performance Metrics page
 * @module yukon.support.systemPerformanceMetrics
 * @requires JQUERY
 * @requires yukon
 */
yukon.support.systemPerformanceMetrics = (function() {
    
    'use strict';
    
    var
    _initialized = false,
    
    labelFormat = '%Y-%m-%d %l:%M:%S %p',
    dateTimeLabelFormats = {
            millisecond: [labelFormat,labelFormat],
            second:      [labelFormat,labelFormat],
            minute:      [labelFormat,labelFormat],
            hour:        [labelFormat,labelFormat],
            day:         [labelFormat,labelFormat],
            week:        [labelFormat,labelFormat],
            month:       [labelFormat,labelFormat],
            year:        [labelFormat,labelFormat]
        },
    
    _sparklineDefaultOptions = {
            chart: {
                backgroundColor: null,
                borderWidth: 0,
                type: 'area',
                margin: [20, 0, 40, 60],
                width: 400,
                height: 100,
                style: {
                    overflow: 'visible'
                },
                // small optimalization, saves 1-2 ms each sparkline
                skipClone: true,
            },
            title: {
                text: ''
            },
            credits: {
                enabled: false
            },
            xAxis: {
                type: 'datetime',
                labels: {
                    enabled: true,
                    step: 1,
                    overflow: 'allow',
                    y: 12,
                    style: {
                        fontSize: '8px'
                    }
                },
                tickLength: 5,
                title: {
                    text: 'Series'
                },
                startOnTick: false,
                endOnTick: false,
            },
            yAxis: {
                categories:  ['low', 'medium', 'high']
            },
            legend: {
                enabled: false
            },
            tooltip: {
            	dateTimeLabelFormats: dateTimeLabelFormats,
                xDateFormat: labelFormat,
                valueDecimals: 3,
                style: {
                    fontSize: "10px",
                    width: "200px",
                }
            },

            plotOptions: {
                series: {
                    animation: false,
                    lineWidth: 1,
                    shadow: false,
                    states: {
                        hover: {
                            lineWidth: 1
                        }
                    },
                    marker: {
                        radius: 1,
                        states: {
                            hover: {
                                radius: 2
                            }
                        }
                    },
                    fillOpacity: 0.25,
                },
                column: {
                    negativeColor: '#910000',
                    borderColor: 'silver'
                }
            }
        },

    mod = {

        /** Initialize this module. */
        init: function () {

            if (_initialized) return;

            var startDate = $(".js-dateStart").val(),
                endDate = $(".js-dateEnd").val();
            $.get(yukon.url("/support/systemPerformanceMetrics/getChartJson?startDate=" + startDate + "&endDate=" + endDate)).done(function(data){
                var count = 0;
                $.each(data, function (key, value) {
                    var data = [];
                        if (value.pointData != null) {
                            value.pointData.forEach(function (item, index) {
                                data.push([item.time, item.value]);
                            });
                        }
                    var row = $('<tr></tr>'),
                        pointNameTableCell = $('<td></td>').attr({'data-point-id': value.pointId})
                                                           .css({'width': '20%'})
                                                           .appendTo(row),
                        anchorTag = $('<a></a>').text(value.pointName)
                                                .attr({'href': '#'})
                                                .appendTo(pointNameTableCell),
                        chartTableCell = $('<td></td>').attr({id: value.pointId, class: 'js-chart-cell', 'data-point-data': JSON.stringify(value.pointData)})
                                                       .appendTo(row);
                    var appendToTableCssClass = (count % 2 == 0) ? '.js-chart-table-left' : '.js-chart-table-right';
                    row.appendTo(appendToTableCssClass);
                    count++;

                    var sparklineOptions = {
                        chart: {
                            renderTo: chartTableCell
                        },
                        series: [{
                            data: data
                        }]
                    };

                    var options = Highcharts.merge(_sparklineDefaultOptions, sparklineOptions);

                    chartTableCell.highcharts(options);

                });
            });
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.support.systemPerformanceMetrics.init(); });