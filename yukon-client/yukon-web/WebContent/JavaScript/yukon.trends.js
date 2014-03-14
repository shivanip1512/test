/**
 * Manages the trends page, uses highstock library to display svg trends
 * 
 * @require jQuery 1.9+
 * @require highstock 1.3.9
 */
yukon.namespace('yukon.trending');

yukon.trends = (function() {
    
    var mod = {
        
        init: function (trendId) {
            yukon.ui.elementGlass.show('[data-trend]');
            jQuery.getJSON(yukon.url('/trends/' + trendId + '/data'), function(trend) {
                
                var labels = JSON.parse(decodeURIComponent(jQuery('#label-json').html()));
                // Create the chart
                jQuery('[data-trend]').highcharts('StockChart', {
                    
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
                        valueDecimals: 3
                    },
                    
                    yAxis: trend.yAxis
                });
            });
            
            jQuery('.trend-list').scrollTo(jQuery('.trend-list li.selected'));
            jQuery(document).on('click', '.f-print', function(e) {
                var chart = jQuery('[data-trend]').highcharts();
                chart.print();
            });
            jQuery(document).on('click', '.f-dl-png', function(e) {
                var chart = jQuery('[data-trend]').highcharts();
                chart.exportChart({type: 'image/png'});
            });
            jQuery(document).on('click', '.f-dl-jpg', function(e) {
                var chart = jQuery('[data-trend]').highcharts();
                chart.exportChart({type: 'image/jpeg'});
            });
            jQuery(document).on('click', '.f-dl-pdf', function(e) {
                var chart = jQuery('[data-trend]').highcharts();
                chart.exportChart({type: 'application/pdf'});
            });
            jQuery(document).on('click', '.f-dl-svg', function(e) {
                var chart = jQuery('[data-trend]').highcharts();
                chart.exportChart({type: 'image/svg+xml'});
            });
            jQuery(document).on('click', '.f-dl-csv', function(e) {
                var chart = jQuery('[data-trend]').highcharts(),
                    ex = chart.series[0].xAxis.getExtremes(),
                    trendId = jQuery(this).closest('li').data('trendId');
                
                window.location = yukon.url('/trends/' + trendId + '/csv?' + 'from=' + new Date(ex.min).getTime() + '&to=' + new Date(ex.max).getTime()); 
            });
        }
    };
    
    return mod;
}());

jQuery(function () {yukon.trends.init(jQuery('[data-trend]').data('trend'));});