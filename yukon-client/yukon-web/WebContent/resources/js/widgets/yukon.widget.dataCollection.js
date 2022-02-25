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
    _updateTimeout = null,
    
    _getData = function (data) {
        return [
            {
                name: $('.js-AVAILABLE').val(),
                filter: 'AVAILABLE',
                displayPercentage: data.available.percentage < 1 && data.available.percentage != 0 ? '&lt;1%' : yukon.percent(data.available.percentage, 100, 1),
                y: (data.available.percentage < 1 && data.available.percentage != 0 ? 1 : data.available.percentage),
                x: data.available.deviceCount,
                color: yg.colors.GREEN
            },
            {
                name: $('.js-EXPECTED').val(),
                filter: 'EXPECTED',
                displayPercentage: data.expected.percentage < 1 && data.expected.percentage != 0 ? '&lt;1%' : yukon.percent(data.expected.percentage, 100, 1),
                y: (data.expected.percentage < 1 && data.expected.percentage != 0 ? 1 : data.expected.percentage),
                x: data.expected.deviceCount,
                color: yg.colors.BLUE
                
            },
            {
                name: $('.js-OUTDATED').val(),
                filter: 'OUTDATED',
                displayPercentage: data.outdated.percentage < 1 && data.outdated.percentage != 0 ? '&lt;1%' : yukon.percent(data.outdated.percentage, 100, 1),
                y: (data.outdated.percentage < 1 && data.outdated.percentage != 0 ? 1 : data.outdated.percentage),
                x: data.outdated.deviceCount,
                color: yg.colors.ORANGE
            },
            {
                name: $('.js-UNAVAILABLE').val(),
                filter: 'UNAVAILABLE',
                displayPercentage: data.unavailable.percentage < 1 && data.unavailable.percentage != 0 ? '&lt;1%' : yukon.percent(data.unavailable.percentage, 100, 1),
                y: (data.unavailable.percentage < 1 && data.unavailable.percentage != 0 ? 1 : data.unavailable.percentage),
                x: data.unavailable.deviceCount,
                color: yg.colors.GRAY
            }
        ]
    },
    
    /** Build the pie chart for the first time. */
    _buildChart = function (chart, data) {
        debug.log('building chart');
        
        var legendOptionsJSON = {
                labelFormatter: function (point) {
                    var legendValueText = '<span class="js-legend-value dn">' + this.filter + '</span>',
                        spanText = '<span class="badge" style="margin:3px;width:60px;color:white;background-color:' + this.color + '">' + this.x + '</span> ';
                    return legendValueText + spanText + this.name + ': ' + this.displayPercentage;
                },
            },
            plotPieJSON = {
                className: chart.closest('.widgetWrapper').exists() ? 'js-data-pie' : ''
            },
            chartDimensionJSON = {
                width: 460,
                height: 200
            };

        chart.highcharts({
            credits: yg.highcharts_options.disable_credits,
            chart: $.extend({}, yg.highcharts_options.chart_options, chartDimensionJSON),
            legend: $.extend({}, yg.highcharts_options.pie_chart_options.legend, legendOptionsJSON),
            title: yg.highcharts_options.pie_chart_options.title,
            tooltip: yg.highcharts_options.pie_chart_options.tooltip,
            plotOptions: {
                pie: $.extend({}, yg.highcharts_options.pie_chart_options.plotOptions.pie, plotPieJSON)
            },
            series: [{
                type: yg.highcharts_options.pie_chart_options.series_type_pie,
                data: _getData(data)
            }]
        });
        
        chart.removeClass('js-initialize');
    },

    /** Update the existing pie chart. */
    _updateChart = function (chart, data) {
        chart.find('.highcharts-legend-item').remove();
        chart.highcharts().series[0].setData(data, true, false, false);
    },
    
    /** Update the page every so many seconds */
    _update = function (newSelection) {
        $('.js-data-collection-widget').each(function (idx, item) {
            var deviceGroup = $(item).find('input[name=groupName]').val(),
                includeDisabled = $(item).find('#includeDisabled').is(":checked"),
                chart = $(item).find('.js-pie-chart'),
                errorMessage = $(item).find('.user-message'),
                errorMessageFound = errorMessage.is(":visible");
            if (deviceGroup && (!errorMessageFound || newSelection)) {
                $.ajax({
                    url: yukon.url('/amr/dataCollection/updateChart'),
                    data: {
                        deviceGroup: deviceGroup,
                        includeDisabled: includeDisabled
                    },
                    async: false
                }).done(function (data) {
                    var refreshButton = $(item).find('.js-update-data-collection');
                    refreshButton.attr('disabled', !data.isRefreshPossible);
                    if (!data.isRefreshPossible) {
                        var nextRefreshDateTime = moment(data.nextRefresh).tz(yg.timezone).format(yg.formats.date.both_with_ampm);
                        refreshButton.prop('title', yg.text.nextRefresh + nextRefreshDateTime);
                    } else {
                        refreshButton.prop('title', data.refreshTooltip);
                    }
                    var dateTime = moment(data.lastAttemptedRefresh).tz(yg.timezone).format(yg.formats.date.both_with_ampm);
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
            
        });
        if (_updateTimeout) {
            clearTimeout(_updateTimeout);
        }
        _updateTimeout = setTimeout(_update, yg._updateInterval);
            
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;

            _update();
            
            $(document).on('click', '.js-update-data-collection', function () {
                $(this).attr('disabled', true);
                $.ajax(yukon.url('/amr/dataCollection/forceUpdate'));
            });
            
            $(document).on('click', '.js-data-pie', function () {
                var widget = $(this).closest('.js-data-collection-widget'),
                    deviceGroup = $(widget).find('input[name=groupName]').val(),
                    includeDisabled = $(widget).find('#includeDisabled').is(":checked"),
                    ranges = [];
                
                    //check which legend items are selected
                    $(widget).find('div.highcharts-legend-item').each(function(index, elem) {
                        if (!$(elem).hasClass('highcharts-legend-item-hidden')) {
                            var legendValue = $(elem).find('.js-legend-value').text();
                            ranges.push(legendValue);
                        }
                        
                    });
                var data = {
                        deviceGroup: deviceGroup,
                        includeDisabled: includeDisabled
                }
                window.open(yukon.url('/amr/dataCollection/detail?' + $.param(data) + "&ranges=" + ranges));
            });

            $(document).on('yukon:device:group:picker:selection', '.js-device-group-picker', function (ev, group) {
                _update(true);
            });
            
            $(document).on('click', '.js-include-disabled', function (ev, ui) {
                _update(true);
            });

            _initialized = true;
        },
        setSelectedDeviceGroup : function(uniqueId) {
            var changedGroupName = $('#changedGroupName_' + uniqueId).val();
            $('#changeDeviceGroupLink_' + uniqueId).html(changedGroupName);
            $('#groupName_' + uniqueId).val(changedGroupName);
            _update(true);
        },
        
        buildChart : function (chart, data) {
            _buildChart(chart, data);
        }
        
    };
    
    return mod;
})();

$(function () { yukon.widget.dataCollection.init(); });