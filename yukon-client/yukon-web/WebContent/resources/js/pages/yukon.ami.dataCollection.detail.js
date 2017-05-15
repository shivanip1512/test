yukon.namespace('yukon.ami.dataCollection.detail');

/**
 * Module for the Data Collection Detail page
 * @module yukon.ami.dataCollection.detail
 * @requires JQUERY
 * @requires yukon
 */
yukon.ami.dataCollection.detail = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;

            var chart = $('.js-pie-chart-summary'),
                data = yukon.fromJson('#summaryData');
                        
            yukon.widget.dataCollection.buildChart(chart, data);

            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.ami.dataCollection.detail.init(); });