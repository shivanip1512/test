yukon.namespace('yukon.widget.assetAvailability');

/**
 * Module for the Asset Availability Widget.
 * @module yukon.widget.assetAvailability
 * @requires JQUERY
 * @requires yukon
 */
yukon.widget.assetAvailability = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    _updateInterval = yg.rp.updater_delay,
    _updateTimeout = null,
    
    _getData = function (data) {
        return [
            {
                name: $('.js-asset-ACTIVE').val(),
                filter: 'ACTIVE',
                displayPercentage: data.active.percentage < 1 && data.active.percentage != 0 ? '&lt;1%' : yukon.percent(data.active.percentage, 100, 1),
                y: (data.active.percentage < 1 && data.active.percentage != 0 ? 1 : data.active.percentage),
                x: data.active.deviceCount,
                color: '#009933'
            },
            {
                name: $('.js-asset-UNAVAILABLE').val(),
                filter: 'UNAVAILABLE',
                displayPercentage: data.unavailabile.percentage < 1 && data.unavailabile.percentage != 0 ? '&lt;1%' : yukon.percent(data.unavailabile.percentage, 100, 1),
                y: (data.unavailabile.percentage < 1 && data.unavailabile.percentage != 0 ? 1 : data.unavailabile.percentage),
                x: data.unavailabile.deviceCount,
                color: '#E50000'
            },
            {
                name: $('.js-asset-INACTIVE').val(),
                filter: 'INACTIVE',
                displayPercentage: data.inactive.percentage < 1 && data.inactive.percentage != 0 ? '&lt;1%' : yukon.percent(data.inactive.percentage, 100, 1),
                y: (data.inactive.percentage < 1 && data.inactive.percentage != 0 ? 1 : data.inactive.percentage),
                x: data.inactive.deviceCount,
                color: '#ec971f'
                
            },
            {
                name: $('.js-asset-OPTED_OUT').val(),
                filter: 'OPTED_OUT',
                displayPercentage: data.optedOut.percentage < 1 && data.optedOut.percentage != 0 ? '&lt;1%' : yukon.percent(data.optedOut.percentage, 100, 1),
                y: (data.optedOut.percentage < 1 && data.optedOut.percentage != 0 ? 1 : data.optedOut.percentage),
                x: data.optedOut.deviceCount,
                color: '#888'
            }
        ]
    },
    
    _update = function (selectionChanged) {
        $('.js-asset-availability-widget').each (function (index, item) {
            var areaOrLMProgramOrScenarioId = $(item).find('input[name=areaOrLMProgramOrScenarioId]').val(),
                chart = $(item).find('.js-pie-chart');
            
            if (areaOrLMProgramOrScenarioId && selectionChanged) {
                $.ajax({
                    url: yukon.url('/dr/assetAvailability/updateChart'),
                    data: {
                        areaOrLMProgramOrScenarioId: areaOrLMProgramOrScenarioId
                    }
                }).done(function (data) {
                    var refreshButton = $(item).find('.js-update-data-collection');
                    refreshButton.prop('title', data.refreshTooltip);
                    refreshButton.attr('disabled', !data.isRefreshPossible);
                    var dateTime = moment(data.lastAttemptedRefresh.millis).tz(yg.timezone).format(yg.formats.date.both_with_ampm);
                    $(item).find('.js-last-updated').text(dateTime);
                    
                    if (data.summary != null) {
                        if (chart.is('.js-initialize')) {
                            _buildChart(chart, data.summary);
                        } else {
                            _updateChart(chart, _getData(data.summary));
                        }
                        chart.removeClass('dn');
                    } else {
                        chart.addClass('dn');
                    }
                });
            }
        });
        
        if (_updateTimeout) {
            clearTimeout(_updateTimeout);
        }
        _updateTimeout = setTimeout(_update, _updateInterval);
    },
    
    /** Update the existing pie chart. */
    _updateChart = function (chart, data) {
        chart.highcharts().series[0].setData(data);
    },
    
    _buildChart = function (chart, data) {
        debug.log('building chart');
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
                    var legendValueText = '<span class="js-legend-value dn">' + this.filter + '</span>';
                    var spanText = '<span class="badge" style="margin:2px;width:60px;color:white;background-color:' + this.color + '">' + this.x + '</span> ';
                    return legendValueText + spanText + this.name + ': ' + this.displayPercentage;
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
                    className: onWidget ? 'js-asset-availability-data-pie' : ''
                }
            },
            series: [{
                type: 'pie',
                data: _getData(data)
            }]
        });
        
        chart.removeClass('js-initialize');
    },

    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            $(document).on('yukon:asset:availability:selection', function (event) {
                _update(true);
            });
            
            $(document).on('click', '.js-update-asset-availability', function (event) {
                $(this).attr('disabled', true);
                $.ajax(yukon.url('/dr/assetAvailability/forceUpdate'));
            });
            
            _update(true);
            
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.widget.assetAvailability.init(); });