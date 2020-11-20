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
        var filters = _getFilters();
        yukon.ui.blockPage();
        var url = yukon.url('/dr/assetAvailability/filterResults?paobjectId=' + filters.paobjectId + '&deviceSubGroups=' + filters.deviceSubGroups + '&statuses=' + filters.statuses + '&selectedGateways=' + filters.selectedGateways);
        $.get(url, function (data) {
            $("#js-filtered-results").html(data);
        }).always(function () {
            yukon.ui.unbusy($('.js-filter-results'));
            yukon.ui.unblockPage();
        });
    },
    
    _getFilters = function () {
        var statuses = [],
            deviceSubGroups = [],
            selectedGateways = [];
        
        $('input[name=statuses]').each(function (index, element) {
            if($(element).prop('checked')) {
                statuses.push($(element).val());
            }
        });
        if ($.isEmptyObject(statuses)) {
            $('input[name=statuses]').each(function (index, element) {
                statuses.push($(element).val());
                $(element).prop( "checked", true );
            });
        }
        $("input[name=deviceSubGroups]").each(function (index, element) {
            deviceSubGroups.push($(element).val());
        });
       
        selectedGateways =  $("#selectedGatewayIds").chosen().val();
        
        var filters = {
                paobjectId : $("input[name=paobjectId]").val(),
                deviceSubGroups : deviceSubGroups,
                statuses : statuses, 
                selectedGateways : selectedGateways
        };
        
        return filters;
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;

            var chart = $('.js-asset-availability-pie-chart-summary'),
                data = yukon.fromJson('#js-asset-availability-summary');
            yukon.assetAvailability.pieChart.buildChart(chart, data, false);
            
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

            $(document).on('click', '.js-download-filter-result', function () {
                var filters = _getFilters();
                window.location.href = yukon.url('/dr/assetAvailability/downloadFilteredResults?paobjectId=' + filters.paobjectId + '&deviceSubGroups=' + filters.deviceSubGroups +"&selectedGatewayIds=" + filters.selectedGateways
                        + '&statuses=' + filters.statuses);
             });
            
            $(document).on('click', '.js-inventory-actions', function () {
                var form = $('#js-asset-availability-filter-form'),
                data = form.serialize();
                window.open(yukon.url('/dr/assetAvailability/inventoryAction?' + data), '_blank');
            });

            $(document).on('click', '.js-collection-action', function () {
                var actionUrl = $(this).data('url'),
                    data = $('#js-asset-availability-filter-form').serialize();
                window.open(yukon.url('/dr/assetAvailability/collectionAction?actionUrl=' + actionUrl + '&' + data));
            });
            
            $(".js-primary-gateway-select").chosen({width: "300px"});

            _filterResults();

            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.dr.assetavailability.detail.init(); });