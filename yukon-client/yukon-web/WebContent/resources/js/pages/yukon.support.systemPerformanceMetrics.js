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
                        anchorTag = $('<a class="js-metrics cp"></a>').text(value.pointName).attr({'data-point-id': value.pointId})
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
                  
                    $(document).on('click', '.js-metrics', function (ev) {
                    	var pointId = $(this).data('pointId');
                    	$('.js-point-data-dialog').load(yukon.url('/meter/historicalReadings/view?pointId=' + pointId), function () {
                    		$('.js-point-data-dialog').dialog({
                    			title : "test",       // value.pointName
                    			width : 500,
                    			height: 600,
                    			autoOpen : true
                    			//chartTableCell.highcharts(options);
                    		});
                    	});
                    });

                });
            });
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.support.systemPerformanceMetrics.init(); });