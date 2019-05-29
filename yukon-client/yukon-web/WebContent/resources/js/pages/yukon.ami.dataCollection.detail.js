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
            
            $(".js-primary-gateway-select").chosen({width: "300px"});
            
            $(document).on('click', '.js-download', function () {
                var form = $('#collectionDetail');
                form.attr('action', yukon.url('/amr/dataCollection/download'));
                form.submit();
                form.attr('action', yukon.url('/amr/dataCollection/detail'));
            });
            
            $(document).on('click', '.js-collection-action', function () {
                var collectionAction = $(this).data('collection-action'),
                    form = $('#collectionDetail'),
                    data = form.serialize();
                window.open(yukon.url('/amr/dataCollection/collectionAction?actionType=' + collectionAction + '&' + data));
            });

            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.ami.dataCollection.detail.init(); });