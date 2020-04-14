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
    _updateInterval = 1800000,
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
                name: $('.js-asset-OPTED_OUT').val(),
                filter: 'OPTED_OUT',
                displayPercentage: data.optedOut.percentage < 1 && data.optedOut.percentage != 0 ? '&lt;1%' : yukon.percent(data.optedOut.percentage, 100, 1),
                y: (data.optedOut.percentage < 1 && data.optedOut.percentage != 0 ? 1 : data.optedOut.percentage),
                x: data.optedOut.deviceCount,
                color: '#4d90fe'
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
                name: $('.js-asset-UNAVAILABLE').val(),
                filter: 'UNAVAILABLE',
                displayPercentage: data.unavailable.percentage < 1 && data.unavailable.percentage != 0 ? '&lt;1%' : yukon.percent(data.unavailable.percentage, 100, 1),
                y: (data.unavailable.percentage < 1 && data.unavailable.percentage != 0 ? 1 : data.unavailable.percentage),
                x: data.unavailable.deviceCount,
                color: '#888'
            }
        ]
    },
    
    _update = function (widgetContainer, selectionChanged) {
        var controlAreaOrProgramOrScenarioId = widgetContainer.find('input[name=controlAreaOrProgramOrScenarioId]').val(),
            chart = widgetContainer.find('.js-asset-availability-pie-chart'),
            errorMessage = widgetContainer.find('.user-message'),
            errorMessageFound = errorMessage.is(":visible");
            
        if (controlAreaOrProgramOrScenarioId && (!errorMessageFound || selectionChanged)) {
            $.ajax({
                url: yukon.url('/dr/assetAvailability/updateChart'),
                data: {
                    controlAreaOrProgramOrScenarioId: controlAreaOrProgramOrScenarioId
                }
            }).done(function (data) {
                var refreshButton = widgetContainer.find('.js-update-asset-availability'),
                    nextRefreshDateTime = moment(data.nextRefreshTime).tz(yg.timezone).format(yg.formats.date.both_with_ampm);
                refreshButton.prop('title', yg.text.nextRefresh + nextRefreshDateTime);
                refreshButton.attr('disabled', true);
                var dateTime = moment(data.lastAttemptedRefresh).tz(yg.timezone).format(yg.formats.date.both_with_ampm);
                widgetContainer.find('.js-last-updated').text(dateTime);

                setTimeout(function() { 
                    refreshButton.attr('disabled', false);
                    refreshButton.prop('title', data.updateTooltip);
                    }, data.refreshMillis);

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
                errorMessage.addClass('dn');
                if (data.errorMessage != null) {
                    errorMessage.html(data.errorMessage);
                    errorMessage.removeClass('dn');
                }
            });
        } else {
            if (!chart.is('.js-initialize')) {
                _updateChart(chart, null);
            }
        }
        
        if (_updateTimeout) {
            clearTimeout(_updateTimeout);
        }
        _updateTimeout = setTimeout(function () {_update(widgetContainer, selectionChanged)}, _updateInterval);
    },
    
    /** Update the existing pie chart. */
    _updateChart = function (chart, data) {
        chart.find('.highcharts-legend-item').remove();
        chart.highcharts().series[0].setData(data, true, false, false);
    },
    
    _buildChart = function (chart, data) {
        debug.log('building chart');
        var container = chart.closest('.widgetWrapper'),
            summaryPage = chart.closest('.js-asset-availability-pie-chart-summary'),
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
                    var legendValueText = '<span class="js-asset-availability-legend-value dn">' + this.filter + '</span>';
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
            
            $(".js-asset-availability-widget").each(function (index, widgetContainer) {
                _update($(widgetContainer), false);
            });
            
            $(document).on('yukon:asset:availability:selection', function (event, items, picker) {
                var widgetContainer = $(picker.inputAreaDiv).closest('.widgetWrapper').find('.js-asset-availability-widget');
                _update($(widgetContainer), true);
            });
            
            $(document).on('click', '.js-update-asset-availability', function (event) {
                _update($(this).closest('.widgetWrapper').find('.js-asset-availability-widget'), true);
            });
            
            $(document).on('click', '.js-asset-availability-data-pie', function () {
                var widget = $(this).closest('.js-asset-availability-widget'),
                    controlAreaOrProgramOrScenarioId = $(widget).find('input[name=controlAreaOrProgramOrScenarioId]').val(),
                    statuses = [];
                
                //check which legend items are selected
                $(widget).find('div.highcharts-legend-item').each(function(index, elem) {
                    if (!$(elem).hasClass('highcharts-legend-item-hidden')) {
                        var legendValue = $(elem).find('.js-asset-availability-legend-value').text();
                        statuses.push(legendValue);
                    }
                });
                
                window.open(yukon.url('/dr/assetAvailability/detail?paobjectId=' + controlAreaOrProgramOrScenarioId + '&statuses=' + statuses));
            });
            
            _initialized = true;
        },
        
        buildChart : function (chart, data) {
            _buildChart(chart, data);
        }
    };
    
    return mod;
})();

$(function () { yukon.widget.assetAvailability.init(); });