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
    _okClicked = false,
    _selectedOption = null,
    
    /** return a list of the port ids that are currently selected for the widget */
    _getPortIds = function(widget) {
        var portIds = [];
        $(widget).find('input[name=portIds]').each( function() {
            portIds.push($(this).val());
        });
        return portIds;
    },
    
    /** Build the given chart with the given data */
    _buildChart = function (chart, data) {
        debug.log('building chart');
        var labels = JSON.parse(data.labels),
            labelFormat = '%Y-%m-%d %l:%M:%S %p',
            container = chart.closest('.widgetWrapper'),
            chartWidth = container.width() - 20,
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
                width: chartWidth,
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
                inputEnabled: true,
                inputBoxWidth: 75,
                inputDateFormat: '%m/%d/%Y',
                inputEditDateFormat: '%m/%d/%Y',
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
                }, {
                	type: 'all',
                	text: 'All',
                	value: 'ALL'
                }],
                buttonTheme: {
                	width: 20
                },
                
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
        chart.removeClass('js-initialize');
    },
    
    _retrieveLatestData = function (widget) {
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
                    _buildChart(chart, data);
                }
                _updateWidgetRefresh($(widget), data);
            });
        } else {
            if (!chart.is('.js-initialize')) {
                _updateChart(chart, null);
            }
        }
    },
    
    _updateWidgetRefresh = function (widget, data) {
        var dateTime = moment(data.lastUpdateTime.millis).tz(yg.timezone).format(yg.formats.date.both_with_ampm),
            refreshButton = widget.find('.js-update-queue-counts'),
            nextRefreshDateTime = moment(data.nextRefreshDate.millis).tz(yg.timezone).format(yg.formats.date.both_with_ampm);
        widget.find('.js-last-updated').text(dateTime);
        refreshButton.prop('title', yg.text.nextRefresh + nextRefreshDateTime);
        refreshButton.attr('disabled', true);
        setTimeout(function() { 
            refreshButton.attr('disabled', false);
            refreshButton.prop('title', data.updateTooltip);
            }, data.refreshMillis);
    },
    
    /** Update the existing chart. */
    _updateChart = function (chart, data) {
        chart.highcharts().series[0].setData(data);
    },
    
    /** Update the chart whenever the page is refreshed or new comm channels are selected */
    _update = function (widget) {
        if (widget != null) {
            _retrieveLatestData(widget);
        } else { 
            $('.js-porter-queue-counts-widget').each(function (idx, widget) {
                _retrieveLatestData(widget);
            });
        }
    },
    

    mod = {
        
        /** Initialize this module. */
        init : function () {
            if (_initialized) return;
            
            var highChartOptionsLangOptions = {
                    lang:{
                        rangeSelectorZoom: '',
                        rangeSelectorFrom: ''
                    }
            };
            Highcharts.setOptions($.extend(highChartOptionsLangOptions, yg.highcharts_options));
            $.ajax({
                url: yukon.url('/amr/porterQueueCounts/getZoom'),
                type: 'get',
                async: false
            }).done(function (data) {
                _selectedOption = data.prefZoom;
            });
            
            $(document).on('click', '.js-update-queue-counts', function () {
                var widget = $(this).closest('.js-porter-queue-counts-widget');
                _retrieveLatestData(widget);
            });

            $(document).on('dialogclose', '.js-picker-dialog', function (ev, ui) {
                if (_okClicked) {
                    var widget = $('#' + ev.target.id.substring(6));
                    _update(widget);
                    _okClicked = false;
                }
            });
            
            $(document).on('okClicked', function (ev, ui) {
                _okClicked = true;
            });
            
            _update();

            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.widget.porterQueueCounts.init(); });