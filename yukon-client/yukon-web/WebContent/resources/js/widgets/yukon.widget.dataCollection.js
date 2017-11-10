yukon.namespace('yukon.widget.dataCollection');

/**
 * Module for the Data Collection Widget
 * @module yukon.widget.dataCollection
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.dataCollection = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    /** @type {number} - The setTimeout reference for periodic updating of the pie chart. */
    _updateInterval = 6000,
    _updateTimeout = null,
    
    _getData = function (data) {
        return [
            {
                name: $('.js-AVAILABLE').val(),
                displayPercentage: data.available.percentage < 1 && data.available.percentage != 0 ? '&lt;1%' : yukon.percent(data.available.percentage, 100, 1),
                y: (data.available.percentage < 1 && data.available.percentage != 0 ? 1 : data.available.percentage),
                x: data.available.deviceCount,
                color: '#009933'
            },
            {
                name: $('.js-EXPECTED').val(),
                displayPercentage: data.expected.percentage < 1 && data.expected.percentage != 0 ? '&lt;1%' : yukon.percent(data.expected.percentage, 100, 1),
                y: (data.expected.percentage < 1 && data.expected.percentage != 0 ? 1 : data.expected.percentage),
                x: data.expected.deviceCount,
                color: '#4d90fe'
                
            },
            {
                name: $('.js-OUTDATED').val(),
                displayPercentage: data.outdated.percentage < 1 && data.outdated.percentage != 0 ? '&lt;1%' : yukon.percent(data.outdated.percentage, 100, 1),
                y: (data.outdated.percentage < 1 && data.outdated.percentage != 0 ? 1 : data.outdated.percentage),
                x: data.outdated.deviceCount,
                color: '#ec971f'
            },
            {
                name: $('.js-UNAVAILABLE').val(),
                displayPercentage: data.unavailable.percentage < 1 && data.unavailable.percentage != 0 ? '&lt;1%' : yukon.percent(data.unavailable.percentage, 100, 1),
                y: (data.unavailable.percentage < 1 && data.unavailable.percentage != 0 ? 1 : data.unavailable.percentage),
                x: data.unavailable.deviceCount,
                color: '#888'
            }
        ]
    },
    
    /** Build the pie chart for the first time. */
    _buildChart = function (chart, data) {
        debug.log('building chart');
        //use widget wrapper for width if within widget and summary if on detail page
        var container = chart.closest('.widgetWrapper'),
            summaryPage = chart.closest('.js-pie-chart-summary'),
            onWidget = container.length,
            containerWidth = onWidget ? container.width() : summaryPage.width(),
            chartWidth = containerWidth - 20;
        chart.highcharts({
            chart: {
                renderTo: 'chart',
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
                height: 200,
                width: chartWidth
            },
            credits: {
                enabled: false
            },
            legend: {
                symbolPadding: -60,
                symbolWidth: 0.001,
                symbolHeight: 0.001,
                symbolRadius: 0,
                align: 'right',
                borderWidth: 0,
                useHTML: true,
                labelFormatter: function (point) {
                    var spanText = '<span class="badge" style="margin:2px;width:60px;color:white;background-color:' + this.color + '">' + this.x + '</span> ';
                    return spanText + this.name + ': ' + this.displayPercentage;
                },
                layout: 'vertical',
                verticalAlign: 'middle'
            },
            title: { text: null },
            tooltip: {
                pointFormat: '<b>{point.displayPercentage}, {point.x} devices</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: { enabled: false },
                    showInLegend: true,
                    borderWidth: 0.25,
                    className: onWidget ? 'js-data-pie' : ''
                }
            },
            series: [{
                type: 'pie',
                data: _getData(data)
            }]
        });
        
        chart.removeClass('js-initialize');
    },
    
    /** Update the existing pie chart. */
    _updateChart = function (chart, data) {
        chart.highcharts().series[0].setData(data);
    },
    
    /** Update the page every so many seconds */
    _update = function () {
        $('.js-data-collection-widget').each(function (idx, item) {
            var deviceGroup = $(item).find('input[name=groupName]').val(),
            includeDisabled = $(item).find('#includeDisabled').is(":checked"),
            chart = $(item).find('.js-pie-chart');
            if (deviceGroup) {
                $.ajax({
                    url: yukon.url('/amr/dataCollection/updateChart'),
                    data: {
                        deviceGroup: deviceGroup,
                        includeDisabled: includeDisabled
                    },
                    async: false
                }).done(function (data) {
                    if (data.summary != null) {
                        if (chart.is('.js-initialize')) {
                            _buildChart(chart, data.summary);
                        } else {
                            _updateChart(chart, _getData(data.summary));
                        }
                        var dateTime = moment(data.summary.collectionTime.millis).tz(yg.timezone).format(yg.formats.date.both_with_ampm);
                        $(item).find('.js-last-updated').text(dateTime);
                    }
                });
            } else {
                if (!chart.is('.js-initialize')) {
                    _updateChart(chart, null);
                }
            }
            
        });
        if (_updateTimeout) {
            clearTimeout(_updateTimeout);
        }
        _updateTimeout = setTimeout(_update, _updateInterval);
        
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;

            _update();
            
            $(document).on('click', '.js-force-update', function () {
                $.ajax(yukon.url('/amr/dataCollection/forceUpdate'));
            });
            
            $(document).on('click', '.js-data-pie', function () {
                var widget = $(this).closest('.js-data-collection-widget'),
                    deviceGroup = $(widget).find('input[name=groupName]').val(),
                    includeDisabled = $(widget).find('#includeDisabled').is(":checked");
                var data = {
                        deviceGroup: deviceGroup,
                        includeDisabled: includeDisabled
                }
                window.open(yukon.url('/amr/dataCollection/detail?' + $.param(data)));
            });

            $(document).on('dialogclose', '.js-device-group-picker-dialog', function (ev, ui) {
                _update();
            });
            
            $(document).on('click', '.js-include-disabled', function (ev, ui) {
                _update();
            });

            _initialized = true;
        },
        
        buildChart : function (chart, data) {
            _buildChart(chart, data);
        }
        
    };
    
    return mod;
})();

$(function () { yukon.widget.dataCollection.init(); });