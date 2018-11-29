yukon.namespace('yukon.dr.assetavailability.detail');

/**
 * Module for the Asset Availability Detail page.
 * @module yukon.dr.assetavailability.detail
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.assetavailability.detail = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    _filterResults = function () {
        yukon.ui.blockPage();
        var statuses = [];
        $("#js-asset-availability-filter-form").find('div.highcharts-legend-item').each(function(index, elem) {
            if (!$(elem).hasClass('highcharts-legend-item-hidden')) {
                var legendValue = $(elem).find('.js-asset-availability-legend-value').text();
                statuses.push(legendValue);
            }
        });
        
        var controlAreaOrProgramOrScenarioId = $("input[name=controlAreaOrProgramOrScenarioId]").val(),
            url = yukon.url('/dr/assetAvailability/filterResults?controlAreaOrProgramOrScenarioId=' + controlAreaOrProgramOrScenarioId + '&statuses=' + statuses);
        
        $.get(url, function (data) {
            $("#js-filtered-results").html(data);
        }).always(function () {
            yukon.ui.unbusy($('.js-filter-results'));
            yukon.ui.unblockPage();
        });
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;

            var chart = $('.js-asset-availability-pie-chart-summary'),
                data = yukon.fromJson('#js-asset-availability-summary');
            yukon.widget.assetAvailability.buildChart(chart, data);
            
            _filterResults();
            
            $(document).on('click', '.js-filter-results', function () {
                _filterResults(); 
            });

            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.dr.assetavailability.detail.init(); });