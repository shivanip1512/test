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

    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            var startDate = $(".js-dateStart").val(),
                endDate = $(".js-dateEnd").val();
            $.get(yukon.url("/support/systemPerformanceMetrics/getChartJson?startDate=" + startDate + "&endDate=" + endDate)).done(function(data){
                var count = 0;
                $.each(data, function (key, value) {
                    debugger;
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
                    
                    
                    
                    /*************spark line start**************/ 
                        Highcharts.SparkLine = function (a, b, c) {
                            const hasRenderToArg = typeof a === 'string' || a.nodeName;
                            let options = arguments[hasRenderToArg ? 1 : 0];
                            const defaultOptions = {
                                chart: {
                                    renderTo: (options.chart && options.chart.renderTo) || (hasRenderToArg && a),
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
                                    labels: {
                                        enabled: false
                                    },
                                    title: {
                                        text: null
                                    },
                                    startOnTick: false,
                                    endOnTick: false,
                                    tickPositions: []
                                },
                                yAxis: {
                                    endOnTick: false,
                                    startOnTick: false,
                                    labels: {
                                        enabled: false
                                    },
                                    title: {
                                        text: null
                                    },
                                    tickPositions: [0]
                                },
                                legend: {
                                    enabled: false
                                },
                                tooltip: {
                                    hideDelay: 0,
                                    outside: true,
                                    shared: true
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
                                        fillOpacity: 0.25
                                    },
                                    column: {
                                        negativeColor: '#910000',
                                        borderColor: 'silver'
                                    }
                                }
                            };

                            options = Highcharts.merge(defaultOptions, options);

                            return hasRenderToArg ?
                                new Highcharts.Chart(a, options, c) :
                                new Highcharts.Chart(options, b);
                        };

                        const start = +new Date(),
                            tds = Array.from(document.querySelectorAll('td[data-point-data]')),
                            fullLen = tds.length;

                        let n = 0;

                        
                        function doChunk() {
                            const time = +new Date(),
                                len = tds.length;

                            for (let i = 0; i < len; i += 1) {
                                const td = tds[i];
                                const stringdata = td.dataset.pointData;
                                const arr = stringdata.split('; ');
                                const data = arr[0].split(', ').map(parseFloat);
                                const chart = {};

                                if (arr[1]) {
                                    chart.type = arr[1];
                                }

                                Highcharts.SparkLine(td, {
                                    series: [{
                                        data: data,
                                        pointStart: 1
                                    }],
                                    tooltip: {
                                        headerFormat: '<span style="font-size: 10px">' + td.parentElement.querySelector('td').innerText + ', {point.x}:</span><br/>',
                                        pointFormat: '<b>{point.y}.000</b>'
                                    },
                                    chart: chart
                                });

                                n += 1;

                                // If the process takes too much time, run a timeout to allow interaction with the browser
                                if (new Date() - time > 500) {
                                    tds.splice(0, i + 1);
                                    setTimeout(doChunk, 0);
                                    break;
                                }
                            }
                        }
                        doChunk();

                   /***********spark line end****************/ 
                    
                });
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.support.systemPerformanceMetrics.init(); });