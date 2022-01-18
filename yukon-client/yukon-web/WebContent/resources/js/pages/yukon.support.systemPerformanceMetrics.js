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

    _sparklineDefaultOptions = {
            chart: {
                backgroundColor: null,
                borderWidth: 0,
                type: 'area',
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
                labels: {
                    enabled: true,
                    style: {
                        fontSize: '9px'
                    }
                },
                title: {
                    text: null
                },
                startOnTick: false,
                endOnTick: false,
                tickAmount: 3
            },
            yAxis: {
                endOnTick: false,
                startOnTick: false,
                labels: {
                    enabled: true,
                    style: {
                        fontSize: '9px'
                    }
                },
                title: {
                    text: null
                },
                tickAmount: 3
            },
            legend: {
                enabled: false
            },
            time: {
                timezone: yg.timezone
            },
            tooltip: {
                hideDelay: 0,
                outside: true,
                style: {
                    fontSize: "10px",
                    width: "200px",
                },
                formatter: function () {
                    var tooltipHtml = '';
                    tooltipHtml += "<span style='color:" + this.color + "'>\u25CF</span>&nbsp;" + this.series.name + "</br>";
                    tooltipHtml += this.point.formattedValue + " " + this.point.units + "</br>";
                    tooltipHtml += moment(this.point.x).tz(yg.timezone).format(yg.formats.date.both_with_ampm);
                    return tooltipHtml;
                },
                positioner: function() {
                    var chartPosition = this.chart.pointer.getChartPosition();
                    return {
                        x: chartPosition.left + this.chart.hoverPoint.plotX,
                        y: chartPosition.top + this.chart.hoverPoint.plotY + 10
                    }
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
                            data.push({x: item.time, y: item.value, formattedValue: item.formattedValue, units: item.units});
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
                        xAxis: {
                            min: Date.parse(startDate),
                            max: Date.parse(endDate)
                        },
                        series: [{
                            name: value.pointName,
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