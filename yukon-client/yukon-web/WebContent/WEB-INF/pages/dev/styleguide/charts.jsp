<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="highChart" tagdir="/WEB-INF/tags/highChart"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="charts">
    <tags:styleguide page="charts">

        <style>
.style-guide-example .one {
    line-height: 26px;
}

.description {
    line-height: 22px;
}
</style>

<p class="description">
    Charts like Pie chart, Bar graph, line are used for pictorial representation of data in the in the Yukon. All
            these used highstock js library.
</p>

<h2>Pie Chart with Sample Data</h2>

<p class="description">
    Create a pie chart component through jquery pie chart using highstock js library. this consists of data related
    to device status . Available, Expected, Outdated, Unavailable with their device counts and its percentage.
</p>

<div class="separated-sections clearfix style-guide-example">
    <div class="section"><h4 class="subtle">Example:</h4>
    <div style="max-height: 200px; max-length: 200px;" class="js-pie-chart-summary" />
    <script>
        function _getData(data) {
        return [
            {
                 name : $('.js-AVAILABLE').val(),
                 filter : 'Available',
                 displayPercentage : data.available.percentage < 1 && data.available.percentage != 0 ? '&lt;1%'
                     : yukon.percent(data.available.percentage,100, 1),
                 y : (data.available.percentage < 1 && data.available.percentage != 0 ? 1: data.available.percentage),
                 x : data.available.deviceCount,
                 color : yg.colors.GREEN
            },
            {
                 name : $('.js-EXPECTED').val(),
                 filter : 'Expected',
                 displayPercentage : data.expected.percentage < 1 && data.expected.percentage != 0 ? '&lt;1%'
                     : yukon.percent(data.expected.percentage,100, 1),
                 y : (data.expected.percentage < 1 && data.expected.percentage != 0 ? 1 : data.expected.percentage),
                 x : data.expected.deviceCount,
                 color : yg.colors.BLUE

             },
             {
                 name : $('.js-OUTDATED').val(),
                 filter : 'Outdated',
                 displayPercentage : data.outdated.percentage < 1 && data.outdated.percentage != 0 ? '&lt;1%'
                     : yukon.percent(data.outdated.percentage,100, 1),
                 y : (data.outdated.percentage < 1 && data.outdated.percentage != 0 ? 1 : data.outdated.percentage),
                 x : data.outdated.deviceCount,
                 color : yg.colors.ORANGE
             },
             {
                 name : $('.js-UNAVAILABLE').val(),
                 filter : 'Unavailable',
                 displayPercentage : data.unavailable.percentage < 1 && data.unavailable.percentage != 0 ? '&lt;1%'
                     : yukon.percent(data.unavailable.percentage,100, 1),
                 y : (data.unavailable.percentage < 1 && data.unavailable.percentage != 0 ? 1 : data.unavailable.percentage),
                 x : data.unavailable.deviceCount,
                 color : yg.colors.GRAY
             } ]
        };

        var chart = $('.js-pie-chart-summary');
        var data = {
                "available" : {
                    "percentage" : 20,
                    "deviceCount" : 40
                 },
                 "expected" : {
                     "percentage" : 40,
                     "deviceCount" : 80
                 },
                 "outdated" : {
                     "percentage" : 10,
                     "deviceCount" : 20
                 },
                 "unavailable" : {
                     "percentage" : 30,
                     "deviceCount" : 60
                 },
                 "totalDeviceCount" : 200,

        };

        var legendOptionsJSON = {
                labelFormatter : function(point) {
                    var legendValueText = '<span class="js-legend-value dn">' + this.filter + '</span>', 
                        spanText = '<span class="badge" style="margin:2px;width:60px;color:white;background-color:' + this.color + '">'
                                    + this.x + '</span> ';
                    return legendValueText + spanText + this.filter + ': ' + this.displayPercentage;
                },
            }, plotPieJSON = {
                    className : ''
            }, chartDimensionJSON = {
                    width : 420,
                    height : 200
        };
    
    chart.highcharts({
        credits : yg.highcharts_options.disable_credits,
        chart : $.extend({},yg.highcharts_options.chart_options,chartDimensionJSON),
        legend : $.extend({},yg.highcharts_options.pie_chart_options.legend,legendOptionsJSON),
        title : yg.highcharts_options.pie_chart_options.title,
        tooltip : yg.highcharts_options.pie_chart_options.tooltip,
        plotOptions : {
            pie : $.extend({},yg.highcharts_options.pie_chart_options.plotOptions.pie,plotPieJSON)},
            series : [ {
                type : yg.highcharts_options.pie_chart_options.series_type_pie,
                data : _getData(data)
            } ]
       });
       </script>
    </div>
    <h4 class="subtle">Code:</h4>
    <pre class="code prettyprint">
    
    &lt;div style=&quot;max-height: 200px; max-length: 200px;&quot; class=&quot;js-pie-chart-summary&quot; /&gt;
    &lt;script&gt;
        function _getData(data) {
        return [
            {
                 name : $('.js-AVAILABLE').val(),
                 filter : 'Available',
                 displayPercentage : data.available.percentage < 1 && data.available.percentage != 0 ? '&lt;1%'
                     : yukon.percent(data.available.percentage,100, 1),
                 y : (data.available.percentage < 1 && data.available.percentage != 0 ? 1: data.available.percentage),
                 x : data.available.deviceCount,
                 color : yg.colors.GREEN
            },
            {
                 name : $('.js-EXPECTED').val(),
                 filter : 'Expected',
                 displayPercentage : data.expected.percentage < 1 && data.expected.percentage != 0 ? '&lt;1%'
                     : yukon.percent(data.expected.percentage,100, 1),
                 y : (data.expected.percentage < 1 && data.expected.percentage != 0 ? 1 : data.expected.percentage),
                 x : data.expected.deviceCount,
                 color : yg.colors.BLUE

             },
             {
                 name : $('.js-OUTDATED').val(),
                 filter : 'Outdated',
                 displayPercentage : data.outdated.percentage < 1 && data.outdated.percentage != 0 ? '&lt;1%'
                     : yukon.percent(data.outdated.percentage,100, 1),
                 y : (data.outdated.percentage < 1 && data.outdated.percentage != 0 ? 1 : data.outdated.percentage),
                 x : data.outdated.deviceCount,
                 color : yg.colors.ORANGE
             },
             {
                 name : $('.js-UNAVAILABLE').val(),
                 filter : 'Unavailable',
                 displayPercentage : data.unavailable.percentage < 1 && data.unavailable.percentage != 0 ? '&lt;1%'
                     : yukon.percent(data.unavailable.percentage,100, 1),
                 y : (data.unavailable.percentage < 1 && data.unavailable.percentage != 0 ? 1 : data.unavailable.percentage),
                 x : data.unavailable.deviceCount,
                 color : yg.colors.GRAY
             } ]
        };

        var chart = $('.js-pie-chart-summary');
        var data = {
                "available" : {
                    "percentage" : 20,
                    "deviceCount" : 40
                 },
                 "expected" : {
                     "percentage" : 40,
                     "deviceCount" : 80
                 },
                 "outdated" : {
                     "percentage" : 10,
                     "deviceCount" : 20
                 },
                 "unavailable" : {
                     "percentage" : 30,
                     "deviceCount" : 60
                 },
                 "totalDeviceCount" : 200,
        };

        var legendOptionsJSON = {
                labelFormatter : function(point) {
                var legendValueText = '&lt;span class=&quot;js-legend-value dn&quot;&gt;' + this.filter + '&lt;/span&gt;', 
                spanText = '&lt;span class=&quot;badge&quot; style=&quot;margin:2px;width:60px;color:white;background-color:' + this.color + '&quot;&gt;'
                                    + this.x + '&lt;/span&gt; ';
                return legendValueText + spanText + this.filter + ': ' + this.displayPercentage;
                },
            }, plotPieJSON = {
                    className : ''
            }, chartDimensionJSON = {
                    width : 420,
                    height : 200
        };
    
    chart.highcharts({
        credits : yg.highcharts_options.disable_credits,
        chart : $.extend({},yg.highcharts_options.chart_options,chartDimensionJSON),
        legend : $.extend({},yg.highcharts_options.pie_chart_options.legend,legendOptionsJSON),
        title : yg.highcharts_options.pie_chart_options.title,
        tooltip : yg.highcharts_options.pie_chart_options.tooltip,
        plotOptions : {
            pie : $.extend({},yg.highcharts_options.pie_chart_options.plotOptions.pie,plotPieJSON)},
            series : [ {
                type : yg.highcharts_options.pie_chart_options.series_type_pie,
                data : _getData(data)
            } ]
       });
    &lt;/script&gt;
    </pre>

    <div class="section">
        <h2>Line Graph with Sample Data</h2>
        <p class="description">
            Create a chart component through jquery highchart chart using yukon.trends.js and highstock.js library. Insert group of
            data for that. This data is related to trend. The chart will be build using yukon.trends.buildChart components.
        </p>
        <h4 class="subtle">Example:</h4>
        <div style="max-height: 200px; max-length: 200px;" class="js-trend-chart-summary" />

        <cti:includeScript link="/resources/js/common/yukon.trends.js" />
        
        <script>
            var trend = {
                    "yAxis": [
                        {
                            "opposite": false,
                            "plotLines": [
                                {
                                    "color": "#e99012",
                                    "width": 2,
                                    "value": 1
                                },
                                {
                                    "color": "#0088f2",
                                    "width": 2,
                                    "value": 1.8
                                }
                            ],
                            "labels": {
                                "style": {
                                    "color": "#7b8387"
                                }
                            }
                        },
                        {
                            "opposite": true,
                            "labels": {
                                "style": {
                                    "color": "#7b8387"
                                 }
                            }
                        }
                    ],
                    "series": [
                        {
                            "yAxis": 1,
                            "data": [],
                            "color": "#e99012",
                            "threshold-value": 1,
                            "name": "k0 Mark - Axis: Right",
                            "error": "No Data available."
                        },
                        {
                            "yAxis": 0,
                            "data": [],
                            "color": "#0088f2",
                            "threshold-value": 1.8,
                            "name": "0o Mark - Axis: Left",
                            "error": "No Data available."
                        },
                        {
                            "yAxis": 0,
                            "data": [],
                            "color": "#b2c98d",
                            "name": "Blink Count / 101001 Date 07/06/2020 - Axis: Left",
                            "error": "No Data available."
                        },
                        {
                            "yAxis": 0,
                            "data": [
                                [1612901100000,83.42976251065741], [1613264700000, 99.73843874298709],
                                [1613282700000,89.07407972561818], [1613318700000, 41.856915277772806],
                                [1613333100000,83.42976251065741], [1613369100000, 89.07407972561818],
                                [1613387100000,92.92465186515989], [1613592300000, 83.42976251065741],
                                [1613628300000,89.07407972561818], [1614009900000, 41.856915277772806],
                                [1614042300000,99.73843874298709], [1614060300000, 89.07407972561818],
                                [1614251100000,92.92465186515989], [1614269100000, 41.856915277772806],
                                [1614301500000,99.73843874298709], [1614319500000, 89.07407972561818],
                                [1614355500000,41.856915277772806], [1614528300000, 41.856915277772806],
                                [1614560700000,99.73843874298709], [1614578700000, 89.07407972561818],
                                [1614614700000,41.856915277772806], [1614629100000, 83.42976251065741]
                            ],
                            "color": "#2ca618",
                            "name": "Blink Count / 101002 Peak 02/10/2021 - Axis: Left"
                        }
                    ],
                    "name": "101006 2"
                };
                var _trendChartContainer = $('.js-trend-chart-summary');
                var trendChartOptions = {
                    rangeSelector: {
                        inputEnabled: true,
                        inputStyle : {
                            color: yg.colors.BLACK
                        }
                    },
                    chartWidth : null, //When null the width is calculated from the offset width of the containing element.
                    chartHeight : 675,
                    animateSeriesPloting: true
                };
                var highChartOptions = {};
                    
                if (_trendChartContainer.exists()) {
                    yukon.trends.buildChart(_trendChartContainer, trend, trendChartOptions, highChartOptions);
                }
            </script>
        </div>

        </div>
        <h4 class="subtle">Code:</h4>
        <pre class="code prettyprint">
        &lt;div style=&quot;max-height: 200px; max-length: 200px;&quot; class=&quot;js-trend-chart-summary&quot; /&gt;
        &lt;cti:includeScript link=&quot;/resources/js/common/yukon.trends.js&quot; /&gt;
        &lt;script&gt;
        var trend = {
                        "yAxis": [
                        {
                            "opposite": false,
                            "plotLines": [
                                {
                                    "color": "#e99012",
                                    "width": 2,
                                    "value": 1
                                },
                                {
                                    "color": "#0088f2",
                                    "width": 2,
                                    "value": 1.8
                                }
                            ],
                            "labels": {
                                "style": {
                                    "color": "#7b8387"
                                }
                            }
                        },
                        {
                            "opposite": true,
                            "labels": {
                                "style": {
                                   "color": "#7b8387"
                                 }
                            }
                        }
                     ],
                     "series": [
                         {
                             "yAxis": 1,
                             "data": [],
                             "color": "#e99012",
                             "threshold-value": 1,
                             "name": "k0 Mark - Axis: Right",
                             "error": "No Data available."
                         },
                         {
                             "yAxis": 0,
                             "data": [],
                             "color": "#0088f2",
                             "threshold-value": 1.8,
                             "name": "0o Mark - Axis: Left",
                             "error": "No Data available."
                         },
                         {
                             "yAxis": 0,
                             "data": [],
                             "color": "#b2c98d",
                             "name": "Blink Count / 101001 Date 07/06/2020 - Axis: Left",
                             "error": "No Data available."
                         },
                         {
                             "yAxis": 0,
                             "data": [
                                 [1612901100000,83.42976251065741], [1613264700000, 99.73843874298709],
                                 [1613282700000,89.07407972561818], [1613318700000, 41.856915277772806],
                                 [1613333100000,83.42976251065741], [1613369100000, 89.07407972561818],
                                 [1613387100000,92.92465186515989], [1613592300000, 83.42976251065741],
                                 [1613628300000,89.07407972561818], [1614009900000, 41.856915277772806],
                                 [1614042300000,99.73843874298709], [1614060300000, 89.07407972561818],
                                 [1614251100000,92.92465186515989], [1614269100000, 41.856915277772806],
                                 [1614301500000,99.73843874298709], [1614319500000, 89.07407972561818],
                                 [1614355500000,41.856915277772806], [1614528300000, 41.856915277772806],
                                 [1614560700000,99.73843874298709], [1614578700000, 89.07407972561818],
                                 [1614614700000,41.856915277772806], [1614629100000, 83.42976251065741]
                             ],
                             "color": "#2ca618",
                             "name": "Blink Count / 101002 Peak 02/10/2021 - Axis: Left"
                         }
                     ],
                 "name": "101006 2"
             };
            var _trendChartContainer = $('.js-trend-chart-summary');
            var trendChartOptions = {
                rangeSelector: {
                    inputEnabled: true,
                    inputStyle : {
                        color: yg.colors.BLACK
                    }
                },
                chartWidth : null, //When null the width is calculated from the offset width of the containing element.
                chartHeight : 675,
                animateSeriesPloting: true
            };
            var highChartOptions = {};
            if (_trendChartContainer.exists()) {
                yukon.trends.buildChart(_trendChartContainer, trend, trendChartOptions, highChartOptions);
            }
    
            &lt;/script&gt;
        </pre>
        </div>
    </tags:styleguide>
</cti:standardPage>