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
    _updateInterval = 4000,
    
    _getData = function (data) {
        return [
            {
                name: 'Available',
                y: data.available.percentage,
                x: data.available.deviceCount,
                color: '#5cb85c',
                className: 'js-available'
            },
            {
                name: 'Expected',
                y: data.expected.percentage,
                x: data.expected.deviceCount,
                color: '#FFFF00',
                className: 'js-expected'
                   
            },
            {
                name: 'Outdated',
                y: data.outdated.percentage,
                x: data.outdated.deviceCount,
                color: '#fb8521',
                className: 'js-outdated'
            },
            {
                name: 'Unavailable',
                y: data.unavailable.percentage,
                x: data.unavailable.deviceCount,
                color: '#999999',
                className: 'js-unavailable'
            }
        ]
    },
    
    /** Build the pie chart for the first time. */
    _buildChart = function (chart, data) {
        debug.log('building chart');
        chart.highcharts({
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
                height: 200
            },
            credits: {
                enabled: false
            },
            legend: {
                align: 'right',
                borderWidth: 0,
                labelFormatter: function (point) {
                    return this.name + ': ' + yukon.percent(this.y, 100, 3) + ", " + this.x + " devices";
                },
                layout: 'vertical',
                verticalAlign: 'middle'
            },
            title: { text: null },
            tooltip: {
                pointFormat: '<b>{point.percentage:.3f}%</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: { enabled: false },
                    showInLegend: true,
                    borderWidth: 0
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
    _updateChart = function (data, idx) {
        Highcharts.charts[idx].series[0].setData(_getData(data));
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
                            _buildChart(chart, data.summary, 220);
                        } else {
                            _updateChart(data.summary, idx);
                        }
                        var dateTime = moment(data.summary.collectionTime).tz(data.summary.collectionTime.zone.id).format(yg.formats.date.both);
                        $(item).find('.js-last-updated').text(dateTime);
                    }
                });
            } else {
                if (!chart.is('.js-initialize')) {
                    Highcharts.charts[idx].series[0].setData(null);
                }
            }
            
        });
        //setTimeout(_update, _updateInterval);
        
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;

            _update();
            
            $(document).on('click', '.js-force-update', function () {
                $.ajax(yukon.url('/amr/dataCollection/forceUpdate'));
            });
            
            $(document).on('click', '.js-pie-chart', function () {
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

            _initialized = true;
        },
        
        buildChart : function (chart, data) {
            _buildChart(chart, data);
        }
        
    };
    
    return mod;
})();

$(function () { yukon.widget.dataCollection.init(); });