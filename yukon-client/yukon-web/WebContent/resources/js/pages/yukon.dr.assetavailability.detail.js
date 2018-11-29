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
        
        $('input[name=statuses]').each(function (index, element) {
            if($(element).prop('checked')){
                statuses.push($(element).val());
            }
        });
        if ($.isEmptyObject(statuses)) {
            $('input[name=statuses]').each(function (index, element) {
                statuses.push($(element).val());
                $(element).prop( "checked", true );
            });
        }
        
        var assetId = $("input[name=assetId]").val(),
            deviceSubGroups = [];
        
        $("input[name=deviceSubGroups]").each(function (index, element) {
            deviceSubGroups.push($(element).val());
        });
        
        var url = yukon.url('/dr/assetAvailability/filterResults?assetId=' + assetId + '&deviceSubGroups=' + deviceSubGroups + '&statuses=' + statuses);
        
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
            
            $('input[name=statuses]').each(function() {
                var statusButton = $(this);
                if (!statusButton.prop("checked")) {
                    var legendItems = chart.highcharts().series[0].data;
                    for (var i = 0; i < legendItems.length; i++) {
                        if (statusButton.val() == legendItems[i].filter) {
                            legendItems[i].setVisible(false, false);
                        }
                    }
                }
            });
            chart.highcharts().redraw();
            
            $(document).on('click', '.js-filter-results', function () {
                _filterResults(); 
            });
            
            _filterResults();

            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.dr.assetavailability.detail.init(); });