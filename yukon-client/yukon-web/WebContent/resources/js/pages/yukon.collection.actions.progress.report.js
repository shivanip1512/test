yukon.namespace('yukon.collection.actions.progress.report');

/**
 * Module for the Collection Actions Progress Report
 * @module yukon.collection.actions.progress.report
 * @requires JQUERY
 * @requires yukon
 */
yukon.collection.actions.progress.report = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    _key,
    /** @type {number} - The setTimeout reference for periodic updating of the pie chart. */
    _updateTimeout = null,
    
    _getData = function (data) {
        var legendItems = [],
            massDeletionSuccessful = false;
        Object.keys(data.details).forEach(function(key) {
            var item = {},
                value = data.details[key],
                deviceCount = value.devices.deviceCount;
            if (deviceCount > 0) {
                var cogMenu = $('.js-cog-menu').clone();
                cogMenu.removeClass('js-cog-menu');
                item.name = $('#detail-' + key).val();
                item.color = $('#color-' + key).val();
                var percentage = data.counts.percentages[key];
                item.x = deviceCount;
                item.y = percentage;
                //change the urls for the menu to be the correct devices
                cogMenu.find('a').each(function() {
                    //disable the cog for successful deletions
                    if (data.action == 'MASS_DELETE' && key == 'SUCCESS') {
                        $(this).addClass('disabled-look');
                        massDeletionSuccessful = true;
                    }
                    var href = $(this).attr('href');
                    if (href) {
                        var url = href.substring(0, href.indexOf('?') + 1);
                        url = url + $.param(value.devices.collectionParameters);
                        $(this).attr('href', url);
                    }
                });
                item.cog = cogMenu;
                legendItems.push(item);
            }
        });
        if (massDeletionSuccessful) {
            //disable the devices cog (no other operations can be performed on deleted devices
            $('.js-cog-menu').find('a').each(function() {
                $(this).addClass('disabled-look');
            });
        }
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
                    var spanText = '<span class="badge" style="margin:2px;width:60px;color:white;background-color:' + this.color + '">' + this.x + '</span> ';
                    return spanText + this.name + ': ' + this.y + "%" + this.cog[0].outerHTML;
                },
                layout: 'vertical',
                verticalAlign: 'middle'
            },
            title: { text: null },
            tooltip: {
                pointFormat: '<b>{point.y}%, {point.x} devices</b>'
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
        $.ajax({
            url: yukon.url('/collectionActions/progressReport/updateProgressReport'),
            data: {
                key: _key,
            }
        }).done(function (data) {
            _updatePage(data.result);
            //show hide Log Icon if available
            $('.js-log-icon').toggleClass('dn', !data.isLogAvailable);
        });
        if (_updateTimeout) {
            clearTimeout(_updateTimeout);
        }
        _updateTimeout = setTimeout(_update, yg._updateInterval);
        
    },
    
    _updatePage = function (data) {
        var chart = $('.js-pie-chart');
        if (data != null) {
            //update progress bar
            var percent = yukon.percent(data.counts.percentCompleted, 100, 2),
                progress = $('.js-progress'),
                progressText = $('.js-percent-text');
            
            progress.find('.progress-bar').css({ width: percent })
            .toggleClass('progress-bar-striped', data.counts.percentCompleted < 100);
            progressText.text(percent);
            $('.js-completed-count').text(data.counts.completed + "/" + data.inputs.collection.deviceCount);
            var statusValue = $('#status-' + data.status).val();
            $('.js-status').text(statusValue).toggleClass('error', data.status == 'FAILED');
            
            //update pie chart
            if (chart.is('.js-initialize')) {
                _buildChart(chart, data);
            } else {
                _updateChart(chart, _getData(data));
            }
            
            $('.js-set-routes').attr('disabled', true);
            //update stop time and stop updating page when complete
            if (data.status == 'COMPLETE' || data.status == 'CANCELED') {
                clearTimeout(_updateTimeout);
                if (data.stopTime) {
                    var timeText = moment(data.stopTime.millis).tz(yg.timezone).format(yg.formats.date.both);
                    $('.js-stop-time').text(timeText);
                }
                $('.js-set-routes').removeAttr('disabled');
            }
            //if execution exception occurred show error
            if (data.executionExceptionText) {
                $('.js-progress-error').html(data.executionExceptionText).removeClass('dn');
            }
            if (data.infoText) {
                $('.js-progress-info').html(data.infoText).removeClass('dn');
            }
            //show/hide cancel
            $('.js-cancel').toggleClass('dn', data.displayCancelButton);
        }
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
                        
            _key = $('#key').val();
            var resultsJson = $('#resultsjson'),
                resultsData;
            if (resultsJson.length > 0) {
                resultsData = yukon.fromJson('#resultsjson');
                _updatePage(resultsData);
                if (resultsData.status != 'COMPLETE') {
                    _update();
                }
            }
            
            if (_initialized) return;
            
            $(document).on('click', '.js-cancel', function() {
                $.ajax({
                    url: yukon.url('/collectionActions/progressReport/cancel'),
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
            
            $(document).on('click', '.js-set-routes', function() {
                window.open(yukon.url('/bulk/routeLocate/routeSettings?resultId=' + _key));
            });
            
            $(document).on('click', '.js-cre', function() {
                var url = $(this).data('url');
                window.open(url);
            });

            _initialized = true;
        },
        
        buildChart : function (chart, data) {
            _buildChart(chart, data);
        },
        
        cancelUpdating : function () {
            clearTimeout(_updateTimeout);
        }
        
    };
    
    return mod;
})();

$(function () { yukon.collection.actions.progress.report.init(); });