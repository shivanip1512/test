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
    _deviceGroup,
    /** @type {number} - The setTimeout reference for periodic updating of the pie chart. */
    _updateInterval = 4000,
    
    _getData = function (data) {
        return [
            {
                name: 'Available',
                y: data.summary.available.percentage,
                x: data.summary.available.deviceCount,
                color: '#5cb85c'
            },
            {
                name: 'Expected',
                y: data.summary.expected.percentage,
                x: data.summary.expected.deviceCount,
                color: '#FFFF00'
            },
            {
                name: 'Outdated',
                y: data.summary.outdated.percentage,
                x: data.summary.outdated.deviceCount,
                color: '#fb8521'
            },
            {
                name: 'Unavailable',
                y: data.summary.unavailable.percentage,
                x: data.summary.unavailable.deviceCount,
                color: '#999999'
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
                height: 220
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
                x: 0,
                y: -80
            },
            title: { text: null },
            tooltip: {
                pointFormat: '<b>{point.percentage:.3f}%</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    center: [100, 60],
                    cursor: 'pointer',
                    dataLabels: { enabled: false },
                    showInLegend: true
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
        debug.log('updating chart');
        console.log(data);
        Highcharts.charts[0].series[0].setData(_getData(data));
    },
    
    /** Update the page every so many seconds */
    _update = function () {
        var deviceGroup = $('[name=groupName]').val(),
            includeDisabled = $('#includeDisabled').is(":checked"),
            chart = $('.js-pie-chart');
        if (deviceGroup) {
            $.ajax({
                url: yukon.url('/widget/dataCollectionWidget/updateChart'),
                data: {
                    deviceGroup: deviceGroup,
                    includeDisabled: includeDisabled
                }        
            }).done(function (data) {
                if (data.summary != null) {
                    if (chart.is('.js-initialize')) {
                        _buildChart(chart, data);
                    } else {
                        _updateChart(chart, data);
                    }
                    var dateTime = moment(data.summary.collectionTime).tz(yg.timezone).format(yg.formats.date.both);
                    $('.js-last-updated').text(dateTime);
                }
            });
        } else {
            if (!chart.is('.js-initialize')) {
                Highcharts.charts[0].series[0].setData(null);
            }
        }
        setTimeout(_update, _updateInterval);
        
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;

            _update();
            
            $(document).on('click', '.js-force-update', function () {
                $.ajax(yukon.url('/widget/dataCollectionWidget/forceUpdate'));
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.widget.dataCollection.init(); });