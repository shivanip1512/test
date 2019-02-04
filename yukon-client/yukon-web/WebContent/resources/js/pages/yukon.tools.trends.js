yukon.namespace('yukon.tools.trends');

/**
 * Module to manage the trends page, uses highstock library to display trends
 * 
 * @require JQUERY
 * @require highstock
 */
yukon.tools.trends = (function () {
    
    var mod = {
        
        init: function () {
            
            var trendChartContainer = $('[data-trend]'),
                trendId = $(trendChartContainer).data("trend"),
                labels = JSON.parse(decodeURIComponent($('#label-json').html())),
                rangeSelectorButtons = [{
                    type: 'day',
                    count: 1,
                    text: labels.day,
                    value: 'DAY_1'
                }, {
                    type: 'week',
                    count: 1,
                    text: labels.week,
                    value: 'WEEK_1'
                }, {
                    type: 'month',
                    count: 1,
                    text: labels.month,
                    value: 'MONTH_1'
                }, {
                    type: 'month',
                    count: 3,
                    text: labels.threeMonths,
                    value: 'MONTH_3'
                }, {
                    type: 'month',
                    count: 6,
                    text: labels.sixMonths,
                    value: 'MONTH_6'
                }, {
                    type: 'ytd',
                    text: labels.ytd,
                    value: 'YTD'
                }, {
                    type: 'year',
                    count: 1,
                    text: labels.year,
                    value: 'YEAR_1'
                }, {
                    type: 'all',
                    text: labels.all,
                    value: 'ALL'
                }];
            
            yukon.ui.blockPage();
            var selectedZoomOption;
            $.ajax({
                url: yukon.url('/tools/trends/getZoom'),
                type: 'get'
            }).done(function (data) {
                selectedZoomOption=data.prefZoom;
            });
            $.getJSON(yukon.url('/tools/trends/' + trendId + '/data'), function (trend) {
                var trendChartOptions = {
                        rangeSelector: {
                            inputEnabled: true,
                            rangeSelectorButtons : rangeSelectorButtons,
                            selected: selectedZoomOption,
                            inputStyle : {
                                color: '#333333'
                            }
                        },
                        chartWidth : null, //When null the width is calculated from the offset width of the containing element.
                        chartHeight : 675,
                        animateSeriesPloting: true
                    },
                    highChartOptions = {};
                
                if (trendChartContainer.exists()) {
                    yukon.trends.buildChart(trendChartContainer, trend, trendChartOptions, highChartOptions);
                }
            }).always(function () {
                yukon.ui.unblockPage();
            });
            
            var trendList = $('.trend-list li.selected');
            if (trendList.length) {
                $('.trend-list').scrollTo(trendList);
            }
            $(document).on('click', '.js-print', function (ev) {
                var chart = trendChartContainer.highcharts(),
                    width = chart.chartWidth,
                    height = chart.chartHeight;
                chart.print();
                setTimeout(function() {
                    chart.setSize(width, height, false);
                }, 100);
            });
            $(document).on('click', '.js-dl-png', function (ev) {
                var chart = trendChartContainer.highcharts();
                chart.exportChart({type: 'image/png'});
            });
            $(document).on('click', '.js-dl-jpg', function (ev) {
                var chart = trendChartContainer.highcharts();
                chart.exportChart({type: 'image/jpeg'});
            });
            $(document).on('click', '.js-dl-pdf', function (ev) {
                var chart = trendChartContainer.highcharts();
                chart.exportChart({type: 'application/pdf'});
            });
            $(document).on('click', '.js-dl-csv', function (ev) {
                var chart = trendChartContainer.highcharts(),
                    ex = chart.series[0].xAxis.getExtremes(),
                    trendId = $(this).closest('li').data('trendId');
                
                window.location = yukon.url('/tools/trends/' + trendId + '/csv?' 
                + 'from=' + new Date(ex.min).getTime() + '&to=' + new Date(ex.max).getTime()); 
            });
        }
    };
    
    return mod;
}());

$(function () { yukon.tools.trends.init(); });