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
                        yukon.assetAvailability.pieChart.buildChart(chart, data.summary, true);
                    } else {
                        _updateChart(chart, yukon.assetAvailability.pieChart.getData(data.summary));
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
                    controlAreaOrProgramOrScenarioId = $(widget).find('input[name=controlAreaOrProgramOrScenarioId]').val();
                
                yukon.assetAvailability.pieChart.showDetailsPage(controlAreaOrProgramOrScenarioId, widget);
            });
            
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.widget.assetAvailability.init(); });