yukon.namespace('yukon.trending');

/**
 * Module to manage the trends page, uses highstock library to display svg trends
 * 
 * @require JQUERY
 * @require highstock 1.3.9
 */
yukon.trends = (function() {
    
    var mod = {
        
        init: function (trendId) {
            var trendList;
            
            Highcharts.setOptions(yg.highcharts_options);
            yukon.ui.elementGlass.show('[data-trend]');
            $.getJSON(yukon.url('/tools/trends/' + trendId + '/data'), function(trend) {
                
                var labels = JSON.parse(decodeURIComponent($('#label-json').html())),
                    labelFormat = '%Y-%m-%d %l:%M:%S %p',
                    dateTimeLabelFormats = {
                        millisecond: [labelFormat,labelFormat],
                        second:      [labelFormat,labelFormat],
                        minute:      [labelFormat,labelFormat],
                        hour:        [labelFormat,labelFormat],
                        day:         [labelFormat,labelFormat],
                        week:        [labelFormat,labelFormat],
                        month:       [labelFormat,labelFormat],
                        year:        [labelFormat,labelFormat]
                    };
                // Create the chart
                $('[data-trend]').highcharts('StockChart', {
                    
                    chart: {
                        height: 600,
                        
                        events: {
                            load: function(event) {
                                yukon.ui.elementGlass.hide('[data-trend]');
                            }
                        }
                    },
                    
                    credits: {
                        enabled: false
                    },
                    
                    exporting: {
                        enabled: false
                    },
                    
                    legend: {
                        enabled: true,
                        align: 'center',
                        backgroundColor: '#fefefe',
                        borderColor: '#ccc',
                        borderWidth: 1,
                        borderRadius: 1,
                        layout: 'vertical',
                        verticalAlign: 'bottom',
                        shadow: true
                    },
                    
                    plotOptions: {
                        series: {
                            dataGrouping: {
                                dateTimeLabelFormats: dateTimeLabelFormats
                            }
                        }
                    },

                    rangeSelector : {
                        buttons: [{
                            type: 'day',
                            count: 1,
                            text: labels.day
                        }, {
                            type: 'week',
                            count: 1,
                            text: labels.week
                        }, {
                            type: 'month',
                            count: 1,
                            text: labels.month
                        }, {
                            type: 'month',
                            count: 3,
                            text: labels.threeMonths
                        }, {
                            type: 'month',
                            count: 6,
                            text: labels.sixMonths
                        }, {
                            type: 'ytd',
                            text: labels.ytd
                        }, {
                            type: 'year',
                            count: 1,
                            text: labels.year
                        }, {
                            type: 'all',
                            text: labels.all
                        }],
                        selected : 2
                    },
                    
                    series : trend.series,

                    tooltip: {
                        dateTimeLabelFormats: dateTimeLabelFormats,
                        valueDecimals: 3
                    },
                    
                    yAxis: trend.yAxis
                });
            });
            
            trendList = $('.trend-list li.selected');
            if (0 < trendList.length) {
                $('.trend-list').scrollTo(trendList);
            }
            $(document).on('click', '.js-print', function(e) {
                var chart = $('[data-trend]').highcharts();
                chart.print();
            });
            $(document).on('click', '.js-dl-png', function(e) {
                var chart = $('[data-trend]').highcharts();
                chart.exportChart({type: 'image/png'});
            });
            $(document).on('click', '.js-dl-jpg', function(e) {
                var chart = $('[data-trend]').highcharts();
                chart.exportChart({type: 'image/jpeg'});
            });
            $(document).on('click', '.js-dl-pdf', function(e) {
                var chart = $('[data-trend]').highcharts();
                chart.exportChart({type: 'application/pdf'});
            });
            $(document).on('click', '.js-dl-svg', function(e) {
                var chart = $('[data-trend]').highcharts();
                chart.exportChart({type: 'image/svg+xml'});
            });
            $(document).on('click', '.js-dl-csv', function(e) {
                var chart = $('[data-trend]').highcharts(),
                    ex = chart.series[0].xAxis.getExtremes(),
                    trendId = $(this).closest('li').data('trendId');
                
                window.location = yukon.url('/tools/trends/' + trendId + '/csv?' + 'from=' + new Date(ex.min).getTime() + '&to=' + new Date(ex.max).getTime()); 
            });
        }
    };
    
    return mod;
}());

$(function () {
    var trendData = $('[data-trend]'),
        trends;
    if (0 < trendData.length) {
        trends = trendData.data('trend');
        if ('' !== trends) {
            yukon.trends.init(trends);
        }
    }
});