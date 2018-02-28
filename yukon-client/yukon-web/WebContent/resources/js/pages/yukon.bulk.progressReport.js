yukon.namespace('yukon.bulk.progressReport');

/**
 * Module for the Bulk Operations/Collection Actions Progress Report
 * @module yukon.bulk.progressReport
 * @requires JQUERY
 * @requires yukon
 */
yukon.bulk.progressReport = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    _key,
    /** @type {number} - The setTimeout reference for periodic updating of the pie chart. */
    _updateInterval = 6000,
    _updateTimeout = null,
    
    _getData = function (data) {
        var legendItems = [];
        Object.keys(data.details).forEach(function(key) {
            var item = {},
                value = data.details[key];
            item.name = $('#detail-' + key).val();
            item.x = value.devices.deviceCount;
            item.y = value.devices.deviceCount / data.counts.completed;
            item.displayPercentage = yukon.percent(value.devices.deviceCount, data.counts.completed, 1);
            item.url = yukon.url("/bulk/collectionActions?" + $.param(value.devices.collectionParameters));
            if (key == 'SUCCESS') {
                item.color = '#009933'
            } else if (key == 'FAILURE') {
                item.color = '#d14836';
                item.color = '#ec971f';
            } else if (key == 'UNSUPPORTED') {
                item.color = '#888888';
            }
            legendItems.push(item);
        });
        return legendItems;
    },
    
    /** Build the pie chart for the first time. */
    _buildChart = function (chart, data) {
        debug.log('building chart');
        chart.highcharts({
            chart: {
                renderTo: 'chart',
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false,
                height: 200
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
                    var newOperationText = $('#newOperationText').val();
                    var spanText = '<span class="badge" style="margin:2px;width:60px;color:white;background-color:' + this.color + '">' + this.x + '</span> ';
                    var cogText = '<button role="button" type="button" class="button naked fn" style="padding-left:5px;" title="' + newOperationText + '"data-href="' + this.url + '"><i class="icon icon-cog-go"></i>';
                    return spanText + this.name + ': ' + this.displayPercentage + cogText;
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
                    point: {
                        events: {
                            legendItemClick: function(e) {
                                e.preventDefault();
                            }
                        }
                    }
                }
            },
            series: [{
                type: 'pie',
                data: _getData(data)
            }]
        });
        
        chart.removeClass('js-initialize');
    },
    
    /** Update the existing pie chart. */
    _updateChart = function (chart, data) {
        chart.highcharts().series[0].setData(data);
    },
    
    /** Update the page every so many seconds */
    _update = function () {
        var chart = $('.js-pie-chart');
        $.ajax({
            url: yukon.url('/bulk/progressReport/updateProgressReport'),
            data: {
                key: _key,
            }
        }).done(function (data) {
            if (data.result != null) {
                //update progress bar
                var percent = yukon.percent(data.result.counts.percentCompleted, 100, 2);
                var progress = $('.js-progress');
                var progressText = $('.js-percent-text');
                
                progress.find('.progress-bar').css({ width: percent })
                .toggleClass('progress-bar-striped', data.result.counts.percentCompleted < 100);
                progressText.text(percent);
                $('.js-completed-count').text(data.result.counts.completed + "/" + data.result.inputs.collection.deviceCount);
                
                //update pie chart
                if (chart.is('.js-initialize')) {
                    _buildChart(chart, data.result);
                } else {
                    _updateChart(chart, _getData(data.result));
                }
                
                //update stop time and stop updating page when complete
                if (data.result.counts.percentCompleted == 100) {
                    //clearTimeout(_updateTimeout);
                    if (data.result.stopTime) {
                        var timeText = moment(data.result.stopTime.millis).tz(yg.timezone).format(yg.formats.date.both);
                        $('.js-stop-time').text(timeText);
                    }
                }
                //if execution exception occurred show error and stop updating
                if (data.result.executionExceptionText) {
                    yukon.ui.alertError(data.result.executionExceptionText);
                    clearTimeout(_updateTimeout);
                }
            }
        });
        if (_updateTimeout) {
            clearTimeout(_updateTimeout);
        }
        _updateTimeout = setTimeout(_update, _updateInterval);
        
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            _key = $('#key').val();

            _update();
            
            $(document).on('click', '.js-cancel', function() {
                $.ajax({
                    url: yukon.url('/bulk/progressReport/cancel'),
                    data: {
                        key: _key,
                    }
                }).done(function (data) {
                    if (data.successMsg) {
                        yukon.ui.alertSuccess(data.successMsg);
                        clearTimeout(_updateTimeout);
                    }
                });
            });

            _initialized = true;
        },
        
        buildChart : function (chart, data) {
            _buildChart(chart, data);
        }
        
    };
    
    return mod;
})();

$(function () { yukon.bulk.progressReport.init(); });