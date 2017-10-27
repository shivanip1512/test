yukon.namespace('yukon.widget.porterQueueCounts');

/**
 * Module for the Porter Queue Counts Widget
 * @module yukon.widget.porterQueueCounts
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.porterQueueCounts = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    okClicked = false,
    _selectedOption = null,
    
    /** return a list of the port ids that are currently selected for the widget */
    _getPortIds = function(widget) {
        var portIds = [];
        $(widget).find('input[name=p12-items]').each( function() {
            portIds.push($(this).val());
        });
        return portIds;
    },
    
    /** Build the given chart with the given data */
    _buildChart = function (chart, data) {
        debug.log('building chart');
        var labels = JSON.parse(data.labels);
        var labelFormat = '%Y-%m-%d %l:%M:%S %p',
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
        legend = {
            enabled: true,
            align: 'center',
            backgroundColor: '#fefefe',
            borderColor: '#ccc',
            borderWidth: 1,
            borderRadius: 1,
            layout: 'vertical',
            verticalAlign: 'bottom',
            shadow: true,
            title : {
                style: {
                    "color" :"#ff0000"
                },
                text: data.truncateMessage,
            }
        };
        chart.highcharts('StockChart', {
            chart: {
                zoomType: 'x',
                height: 300 + 18 * data.series.length,
                events: {
                    load: function (ev) {
                        yukon.ui.unblock('js-chart');
                    }
                }
            },
            credits: {
                enabled: false
            },
            exporting: {
                enabled: false
            },
            legend: legend,
            plotOptions: {
                series: {
                    dataGrouping: {
                        dateTimeLabelFormats: dateTimeLabelFormats
                    },
                    showInNavigator: true
                }
            },
            rangeSelector : {
                allButtonsEnabled: true,
                inputEnabled: true,
                inputBoxWidth: 71,
                inputDateFormat: '%M/%d/%Y',
                inputEditDateFormat: '%M/%d/%Y',
                scrollbar: {
                    height: 5,
                },
                buttons: [{
                    type: 'day',
                    count: 1,
                    text: labels.day,
                    value: 'DAY_1'
                }, {
                    type: 'week',
                    count: 1,
                    text: labels.week,
                    value: 'WEEK_1'
                }, {
                    type: 'month',
                    count: 1,
                    text: labels.month,
                    value: 'MONTH_1'
                }, {
                    type: 'month',
                    count: 3,
                    text: labels.threeMonths,
                    value: 'MONTH_3'
                }],
                
                selected: _selectedOption
            },
            lang: {
                noData: 'No Data for Selected Port(s) and Date Range',
            },
            navigator: {
                series: data.series
            },
            series : data.series,
            tooltip: {
                dateTimeLabelFormats: dateTimeLabelFormats,
                xDateFormat: labelFormat,
                valueDecimals: 3
            },
            xAxis: {
                /** 
                 * Disabling stops highcharts from spacing points equally regardless of timestamp,
                 * it means sections with no data will take up space in the chart.
                 * http://api.highcharts.com/highstock#xAxis.ordinal
                 */
                ordinal: false,
                events: {
                    setExtremes: function(e) {
                        if (typeof(e.rangeSelectorButton) !== 'undefined') {
                          var data = {
                                  zoom : e.rangeSelectorButton.value 
                              };
                          $.ajax({ 
                              type: 'post',
                              url: yukon.url('/amr/porterQueueCounts/updateZoom'),
                              data: data
                          });
                        }
                    }
                },
                lineColor: 'transparent'
            },
            yAxis: data.yAxis,
        });
        
        $(chart).find('.highcharts-range-label').first().attr('visibility', 'hidden');
        chart.removeClass('js-initialize');
    },
    
    /** Update the existing queue counts chart. */
    _updateChart = function (data, idx) {
        Highcharts.charts[idx].series[0].setData(data);
    },
    
    /** Update the page every so many seconds */
    _update = function (widget) {
        if (widget != null) {
            var chart = $(widget).find('.js-chart'),
                portIds = _getPortIds(widget);
            if (portIds) {
                $.ajax({
                    url: yukon.url('/amr/porterQueueCounts/data'),
                    data: {
                        portIds: portIds
                    },
                    type: 'post',
                    async: false
                }).done(function (data) {
                    if (data != null) {
                        _buildChart(chart, data);
                        var dateTime = moment(data.lastUpdateTime.millis).tz(yg.timezone).format(yg.formats.date.both_with_ampm);
                        $(widget).find('.js-last-updated').text(dateTime);
                    }
                });
            } else {
                if (!chart.is('.js-initialize')) {
                    Highcharts.charts[idx].series[0].setData(null);
                }
            }
        } else { 
            $('.js-porter-queue-counts-widget').each(function (idx, widget) {
                var portIds = _getPortIds(widget),
                chart = $(widget).find('.js-chart');
                if (portIds) {
                    $.ajax({
                        url: yukon.url('/amr/porterQueueCounts/data'),
                        data: {
                            portIds: portIds
                        },
                        type: 'post',
                        async: false
                    }).done(function (data) {
                        if (data != null) {
                            if (chart.is('.js-initialize')) {
                                _buildChart(chart, data);
                            } else {
                                _updateChart(data, idx);
                            }
                            var dateTime = moment(data.lastUpdateTime.millis).tz(yg.timezone).format(yg.formats.date.both_with_ampm);
                            $(widget).find('.js-last-updated').text(dateTime);
                        }
                    });
                } else {
                    if (!chart.is('.js-initialize')) {
                        Highcharts.charts[idx].series[0].setData(null);
                    }
                }
            });
        }
    },

    mod = {
        
        /** Initialize this module. */
        init : function () {
            if (_initialized) return;
            
            $.ajax({
                url: yukon.url('/amr/porterQueueCounts/getZoom'),
                type: 'get',
                async: false
            }).done(function (data) {
                _selectedOption = data.prefZoom;
            });
            
            $(document).on('click', '.js-force-update', function () {
                var widget = $(this).closest('.js-porter-queue-counts-widget');
                $.ajax({
                    url: yukon.url('/amr/porterQueueCounts/forceUpdate'),
                    data: {
                        lastGraphDataLoadTime: $(this).siblings('.js-last-updated').text(),
                        portIds: _getPortIds(widget)
                    },
                    type: 'post'
                }).done(function (data) {
                    if (data.series != null) {
                        _buildChart($(widget).find('js-chart'), data);
                    }
                    else {
                        $(widget).addMessage({
                            message: "Must wait 15 minutes to between data refresh.",
                            messageClass: "error"
                        });
                        
                        setTimeout(function() { 
                            $(widget).removeMessages();
                            }, 3000);
                    }
                });
            });

            $(document).on('dialogclose', '.js-picker-dialog', function (ev, ui) {
                if (okClicked) {
                    var widget = $('#' + ev.target.id.substring(3));
                    _update(widget);
                    okClicked = false;
                }
            });
            
            $(document).on('okClicked', function (ev, ui) {
                okClicked = true;
            });
            
            _update();

            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.widget.porterQueueCounts.init(); });