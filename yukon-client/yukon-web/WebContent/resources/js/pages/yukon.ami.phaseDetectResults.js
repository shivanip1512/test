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
            }
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
                console.log(data);
                debug.log('building chart');

                $("#js-pie-chart-container").highcharts({
                    chart: {
                        renderTo: 'chart',
                        plotBackgroundColor: null,
                        plotBorderWidth: null,
                        plotShadow: false,
                        height: 200,
                        width: 460
                    },
                    credits: {
                        enabled: false
                    },
                    legend: {
                        symbolPadding: -60,
                        symbolWidth: 0.001,
                        symbolHeight: 0.001,
                        symbolRadius: 0,
                        align: 'right',
                        borderWidth: 0,
                        useHTML: true,
                        labelFormatter: function (point) {
                            var legendValueText = '<span class="js-phase-detect-results-legend-value dn">' + this.filter + '</span>';
                            var spanText = '<span class="badge" style="width:60px;color:white;background-color:' + this.color + '">' + this.x + '</span> ';
                            return legendValueText + spanText + this.name + ': ' + this.displayPercentage;
                        },
                        layout: 'vertical',
                        verticalAlign: 'middle'
                    },
                    title: { text: null },
                    tooltip: {
                        pointFormat: '<b>{point.displayPercentage}, {point.x} devices</b>'
                    },
                    plotOptions: {
                        pie: {
                            allowPointSelect: true,
                            cursor: 'pointer',
                            dataLabels: { enabled: false },
                            showInLegend: true,
                            borderWidth: 0.25,
                        }
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