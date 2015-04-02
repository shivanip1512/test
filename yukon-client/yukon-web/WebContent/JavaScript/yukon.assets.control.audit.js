yukon.namespace('yukon.assets.controlAudit');

/**
 * Module for the control audit page.
 * @module yukon.assets.controlAudit
 * @requires JQUERY
 * @requires yukon
 */
yukon.assets.controlAudit = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    _auditId,
    
    /** Build the pie chart for the first time. */
    _buildChart = function (chart, data) {
        
        debug.log('building chart');
        chart.highcharts({
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
                height: 220
            },
            credits: {
                enabled: false
            },
            legend: {
                align: 'right',
                borderWidth: 0,
                labelFormatter: function (point) {
                    return this.name + ': ' + yukon.percent(this.y, 100, 3);
                },
                layout: 'vertical',
                x: -50,
                y: -100
            },
            title: { text: null },
            tooltip: {
                pointFormat: '<b>{point.percentage:.3f}%</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    center: [100, 60],
                    cursor: 'pointer',
                    dataLabels: { enabled: false },
                    showInLegend: true
                }
            },
            series: [{
                type: 'pie',
                data: status.data
            }]
        });
        
        chart.removeClass('js-initialize');
    },
    
    /** Update the existing pie chart. */
    _updateChart = function (chart, data) {
        debug.log('updating chart');
        console.log(data);
        Highcharts.charts[0].series[0].setData(data);
    },
    
    /** Update the page until the audit is complete. */
    _update = function () {
        
        $.ajax(yukon.url('/stars/operator/inventory/controlAudit/' + _auditId + '/update'))
        .done(function (status) {
            
            var percent = yukon.percent(status.completed, status.total, 2);
            var progress = $('.js-progress');
            var progressText = $('.js-percent-text');
            
            progress.find('.progress-bar').css({ width: percent })
            .toggleClass('progress-bar-striped', !status.complete);
            progressText.text(percent);
            
            var chart = $('.js-pie-chart');
            
            if (chart.is('.js-initialize')) {
                _buildChart(chart, status.data);
                _updateChart(chart, status.data);
            } else {
                _updateChart(chart, status.data);
            }
            
            if (!status.complete) {
                setTimeout(_update, 2000);
            } else {
                progress.hide();
                progressText.addClass('success').text(yg.text.finished);
                
                $.ajax(yukon.url('/stars/operator/inventory/controlAudit/' + _auditId + '/details'))
                .done(function (details) { $('#audit-result-details').html(details); });
            }
        });
        
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            _auditId = $('[data-audit-id]').data('auditId');
            
            if (_auditId) {
                // An audit task has been started
                _update();
            }
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.assets.controlAudit.init(); });