yukon.namespace('yukon.support.systemPerformanceMetrics');

/**
 * Module that handles the behavior on System Performance Metrics page
 * @module yukon.support.systemPerformanceMetrics
 * @requires JQUERY
 * @requires yukon
 */
yukon.support.systemPerformanceMetrics = (function() {
    
    'use strict';
    
    var
    _initialized = false,

    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            var startDate = $(".js-dateStart").val(),
                endDate = $(".js-dateEnd").val();
            $.get(yukon.url("/support/systemPerformanceMetrics/getChartJson?startDate=" + startDate + "&endDate=" + endDate)).done(function(data){
                var count = 0;
                $.each(data, function (key, value) {
                    debugger;
                    var row = $('<tr></tr>'),
                        pointNameTableCell = $('<td></td>').attr({'data-point-id': value.pointId})
                                                           .css({'width': '20%'})
                                                           .appendTo(row),
                        anchorTag = $('<a></a>').text(value.pointName)
                                                .attr({'href': '#'})
                                                .appendTo(pointNameTableCell),
                        chartTableCell = $('<td></td>').attr({id: value.pointId, class: 'js-chart-cell', 'data-point-data': JSON.stringify(value.pointData)})
                                                       .appendTo(row);
                    
                    var appendToTableCssClass = (count % 2 == 0) ? '.js-chart-table-left' : '.js-chart-table-right';
                    row.appendTo(appendToTableCssClass);
                    count++;
                });
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.support.systemPerformanceMetrics.init(); });