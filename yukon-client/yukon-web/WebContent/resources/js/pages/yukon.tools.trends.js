yukon.namespace('yukon.tools.trends');

/**
 * Module to manage the trends page, uses highstock library to display svg trends
 * 
 * @require JQUERY
 * @require highstock 1.3.9
 */
yukon.tools.trends = (function () {
    
    var mod = {
        
        init: function (trendId) {
            
            var trendList;
            var selectedOption;
            $.ajax({
                url: yukon.url('/tools/trends/getZoom'),
                type: 'get'
            }).done(function (data) {
                selectedOption=data.prefZoom;
            });
            Highcharts.setOptions(yg.highcharts_options);
            yukon.ui.block('[data-trend]');
            
            $.getJSON(yukon.url('/tools/trends/' + trendId + '/data'), function (trend) {
                
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
                        height: 600 + 18 * trend.series.length,
                        
                        events: {
                            load: function (ev) {
                                yukon.ui.unblock('[data-trend]');
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
                            text: labels.day,
                            value: 0
                        }, {
                            type: 'week',
                            count: 1,
                            text: labels.week,
                            value: 1
                        }, {
                            type: 'month',
                            count: 1,
                            text: labels.month,
                            value: 2
                        }, {
                            type: 'month',
                            count: 3,
                            text: labels.threeMonths,
                            value: 3
                        }, {
                            type: 'month',
                            count: 6,
                            text: labels.sixMonths,
                            value: 4
                        }, {
                            type: 'ytd',
                            text: labels.ytd,
                            value: 5
                        }, {
                            type: 'year',
                            count: 1,
                            text: labels.year,
                            value: 6
                        }, {
                            type: 'all',
                            text: labels.all,
                            value: 7
                        }],
                        
                        selected : selectedOption
                    },
                    
                    series : trend.series,
                    
                    tooltip: {
                        dateTimeLabelFormats: dateTimeLabelFormats,
                        valueDecimals: 3
                    },
                    
                    xAxis: {
                        // Disabling stops highcharts from spacing points equally regardless of timestamp,
                        // it means sections with no data will take up space in the chart.
                        // http://api.highcharts.com/highstock#xAxis.ordinal
                        ordinal: false 
                    },
                    
                    yAxis: trend.yAxis
                });
            });
            
            trendList = $('.trend-list li.selected');
            if (trendList.length) {
                $('.trend-list').scrollTo(trendList);
            }
            $(document).on('click', '.js-print', function (ev) {
                var chart = $('[data-trend]').highcharts();
                var w = chart.chartWidth;
                var h = chart.chartHeight;
                chart.print();               
                setTimeout(function() {
                    chart.setSize(w, h, false);
                }, 100);
            });
            $(document).on('click', '.js-dl-png', function (ev) {
                var chart = $('[data-trend]').highcharts();
                chart.exportChart({type: 'image/png'});
            });
            $(document).on('click', '.js-dl-jpg', function (ev) {
                var chart = $('[data-trend]').highcharts();
                chart.exportChart({type: 'image/jpeg'});
            });
            $(document).on('click', '.js-dl-pdf', function (ev) {
                var chart = $('[data-trend]').highcharts();
                chart.exportChart({type: 'application/pdf'});
            });
            $(document).on('click', '.js-dl-svg', function (ev) {
                var chart = $('[data-trend]').highcharts();
                chart.exportChart({type: 'image/svg+xml'});
            });
            $(document).on('click', '.js-dl-csv', function (ev) {
                
                var chart = $('[data-trend]').highcharts(),
                    ex = chart.series[0].xAxis.getExtremes(),
                    trendId = $(this).closest('li').data('trendId');
                
                window.location = yukon.url('/tools/trends/' + trendId + '/csv?' 
                + 'from=' + new Date(ex.min).getTime() + '&to=' + new Date(ex.max).getTime()); 
            });
        }
    };
    
    return mod;
}());

$(function () {
    
    var trendId, trendData = $('[data-trend]');
    
    if (trendData.length) {
        trendId = trendData.data('trend');
        if (trendId) {
            yukon.tools.trends.init(trendId);
        }
    }
});