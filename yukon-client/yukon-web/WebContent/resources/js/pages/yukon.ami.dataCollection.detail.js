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
            
            //check which legend items should be displayed
            $('input[name=ranges]').each(function() {
                var rangeButton = $(this);
                if (!rangeButton.prop("checked")) {
                    var legendItems = chart.highcharts().series[0].data;
                    for (var i = 0; i < legendItems.length; i++) {
                        if (rangeButton.val() == legendItems[i].filter) {
                            legendItems[i].setVisible(false, false);
                        }
                    }
                }
            });
            chart.highcharts().redraw();
            
            $(document).on('click', '.js-download', function () {
                var form = $('#collectionDetail');
                form.attr('action', yukon.url('/amr/dataCollection/download'));
                form.submit();
                form.attr('action', yukon.url('/amr/dataCollection/detail'));
            });
            
            $(document).on('click', '.js-collection-action', function () {   
                var actionUrl = $(this).data('url'),
                    form = $('#collectionDetail'),
                    data = form.serialize();
                window.open(yukon.url('/amr/dataCollection/collectionAction?actionUrl=' + actionUrl + '&' + data), '_blank');
            });

            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.ami.dataCollection.detail.init(); });