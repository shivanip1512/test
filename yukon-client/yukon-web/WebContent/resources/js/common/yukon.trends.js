yukon.namespace('yukon.trends');

/**
 * Module to plot trends.
 * 
 * @require JQUERY
 * @require highstock
 */
yukon.trends = (function () {
    
    var mod = {
        
        buildChart: function (trendChartContainer, trendChartOptions, trend) {
            var trendId = trendChartContainer.data('trend');
            
            Highcharts.setOptions(yg.highcharts_options);
            var labelFormat = '%Y-%m-%d %l:%M:%S %p',
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
            
            var legend = {
                    enabled: true,
                    align: 'center',
                    backgroundColor: '#fefefe',
                    borderColor: '#ccc',
                    borderWidth: 1,
                    borderRadius: 1,
                    layout: 'vertical',
                    verticalAlign: 'bottom',
                    shadow: true,
                    title : {
                        style:{
                            "color" :"#ff0000"
                            },
                        text:trend.truncateMessage
                    },
                    maxHeight: 80
                };
            var width = trendChartContainer.closest('.widgetWrapper').width() - 20;
            // Create the chart
            trendChartContainer.highcharts('StockChart', {
                chart: {
                    height: trendChartOptions.chartHeight,
                    width: trendChartOptions.chartWidth,
                    events: {
                        load: function (ev) {
                            yukon.ui.unblock(trendChartContainer);
                        }
                    },
                },
                credits: {
                    enabled: false
                },
                exporting: {
                    enabled: false
                },
                legend: legend,
                plotOptions: {
                    series: {
                        dataGrouping: {
                            dateTimeLabelFormats: dateTimeLabelFormats
                        },
                        animation: trendChartOptions.animateSeriesPloting
                    }
                },
                rangeSelector : {
                    inputBoxWidth: 80,
                    buttons: trendChartOptions.rangeSelector.rangeSelectorButtons,
                    inputEnabled: trendChartOptions.rangeSelector.inputEnabled,
                    selected: trendChartOptions.rangeSelector.selected,
                },
                series : trend.series,
                tooltip: {
                    dateTimeLabelFormats: dateTimeLabelFormats,
                    xDateFormat: labelFormat,
                    valueDecimals: 3,
                    style: {
                        fontSize: "10px",
                        width: "200px",
                    }
                },
                xAxis: {
                    // Disabling stops highcharts from spacing points equally regardless of timestamp,
                    // it means sections with no data will take up space in the chart.
                    // http://api.highcharts.com/highstock#xAxis.ordinal
                    ordinal: false ,
                    events: {
                        setExtremes: function(e) {
                            if (typeof(e.rangeSelectorButton) !== 'undefined') {
                              var url = yukon.url('/tools/trends/updateZoom'),
                                  params = {
                                      value : e.rangeSelectorButton.value
                                  };
                              $.ajax({ type: 'post', url: url, data: params});
                            }
                        }
                    }
                },
                yAxis: trend.yAxis
            });
        }
    };
    
    return mod;
}());