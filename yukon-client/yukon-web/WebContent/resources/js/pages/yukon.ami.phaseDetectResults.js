yukon.namespace('yukon.ami.phaseDetectResults');

/**
 * Module that handles the behavior on the phase detect results page.
 * 
 * @module yukon.ami.phaseDetectResults
 * @requires JQUERY
 * @requires yukon
 */
yukon.ami.phaseDetectResults = (function() {

    'use strict';

    var _initialized = false,
    
    _getData = function (data) {
        var array = [];
        $.each(data.phaseDetectDetails, function (index, value) {
            var json = {
                name: value.phase,
                filter: value.phase,
                displayPercentage: value.percentage < 1 && value.percentage != 0 ? '&lt;1%' : yukon.percent(value.percentage, 100, 1),
                y: (value.percentage < 1 && value.percentage != 0 ? 1 : value.percentage),
                x: value.meterCount,
                color: value.colorHexValue
            };
            array.push(json);
        });
        return array;
    },

    mod = {

        /** Initialize this module. */
        init : function() {

            if (_initialized)
                return;

            $.ajax({
                url: yukon.url("/amr/phaseDetect/chart?key=" + $(".js-cache-key").val()),
                dataType : 'json'
            }).done(function(data) {
                debug.log('building chart');
                
                var legendOptionsJSON = {
                    labelFormatter: function (point) {
                        var legendValueText = '<span class="js-phase-detect-results-legend-value dn">' + this.filter + '</span>',
                            spanText = '<span class="badge" style="margin:1px;width:60px;color:white;background-color:' + this.color + '">' + this.x + '</span> ';
                        return legendValueText + spanText + this.name + ': ' + this.displayPercentage;
                    }
                };

                $("#js-pie-chart-container").highcharts({
                    chart: yg.highcharts_options.pie_chart_options.chart,
                    credits: yg.highcharts_options.pie_chart_options.credits,
                    legend: $.extend({}, yg.highcharts_options.pie_chart_options.legend, legendOptionsJSON),
                    title: yg.highcharts_options.pie_chart_options.title,
                    tooltip: yg.highcharts_options.pie_chart_options.tooltip,
                    plotOptions: {
                        pie: yg.highcharts_options.pie_chart_options.plotOptions.pie
                    },
                    series: [{
                        type: 'pie',
                        data: _getData(data)
                    }]
                });
            });
            
            _initialized = true;
        }
    };

    return mod;
})();

$(function() {
    yukon.ami.phaseDetectResults.init();
});