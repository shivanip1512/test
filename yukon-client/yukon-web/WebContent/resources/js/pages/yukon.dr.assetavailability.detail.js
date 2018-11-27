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
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;

            var chart = $('.js-asset-availability-pie-chart-summary'),
                data = yukon.fromJson('#js-asset-availability-summary');
                        
            yukon.widget.assetAvailability.buildChart(chart, data);

            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.dr.assetavailability.detail.init(); });