<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="charts">
    <tags:styleguide page="charts">

<style>     
    .description {
        line-height: 22px;
    } 
</style>

<p class="description">
    The highcharts/highstock charting library is used for any charts that are displayed within Yukon (ex: pie charts, line charts, bar charts).<br/>
    See: <a href="https://www.highcharts.com">https://www.highcharts.com</a> for more information on the highcharts/highstock charting library. There are demos for each chart type along with the charting API for reference.<br/>
    Yukon uses our in house JS: <b>yukon.highChart.js</b> for helper methods in building charts using highcharts/highstock. Yukon also has several common charting options defined in <b>jsGlobals.js</b> under yg.highcharts_options to provide a common look and feel for the charts used in Yukon.
</p>

<h2>Pie Chart with Sample Data</h2>

<p class="description">
    The following example illustrates how a pie chart can be created using the highcharts/highstock JS library. This uses the common charting options found in jsGlobals.js to establish a common look and feel among the pie charts that are used within Yukon.
</p>

<div class="separated-sections clearfix style-guide-example">
    <div class="section"><h4 class="subtle">Example:</h4>
    <div style="max-height: 200px; max-length: 200px;" class="js-pie-chart-summary" />
    <script>
        var chart = $('.js-pie-chart-summary'),
            legendOptionsJSON = {
                labelFormatter : function(point) {
                    var legendValueText = '<span class="js-legend-value dn">' + this.name + '</span>', 
                        spanText = '<span class="badge" style="margin:2px;padding:4px;width:60px;color:white;background-color:' + this.color + '">'
                                    + this.x + '</span> ';
                    return legendValueText + spanText + this.name + ': ' + this.displayPercentage;
                },
            }, 
            chartDimensionJSON = {
                width : 420,
                height : 200
            },
            data = [
                {
                     name: 'Available',
                     displayPercentage : '20%',
                     y : 20,
                     x : 40,
                     color : yg.colors.GREEN
                },
                {
                     name: 'Expected',
                     displayPercentage : '40%',
                     y : 40,
                     x : 80,
                     color : yg.colors.BLUE
        
                 },
                 {
                     name: 'Outdated',
                     displayPercentage : '10%',
                     y : 10,
                     x : 20,
                     color : yg.colors.ORANGE
                 },
                 {
                     name: 'Unavailable',
                     displayPercentage : '30%',
                     y : 30,
                     x : 60,
                     color : yg.colors.GRAY
                 } 
             ];
        
        chart.highcharts({
            credits : yg.highcharts_options.disable_credits,
            chart : $.extend({},yg.highcharts_options.chart_options, chartDimensionJSON),
            legend : $.extend({},yg.highcharts_options.pie_chart_options.legend, legendOptionsJSON),
            title : yg.highcharts_options.pie_chart_options.title,
            tooltip : yg.highcharts_options.pie_chart_options.tooltip,
            plotOptions : {
                pie : yg.highcharts_options.pie_chart_options.plotOptions.pie
            },
            series : [ {
                type : yg.highcharts_options.pie_chart_options.series_type_pie,
                data : data
            } ]
           });
       </script>
    </div>
    <h4 class="subtle">Code:</h4>
    <pre class="code prettyprint">
    
    &lt;div style=&quot;max-height: 200px; max-length: 200px;&quot; class=&quot;js-pie-chart-summary&quot; /&gt;
    &lt;script&gt;
        var chart = $('.js-pie-chart-summary'),
            legendOptionsJSON = {
                labelFormatter : function(point) {
                var legendValueText = '&lt;span class=&quot;js-legend-value dn&quot;&gt;' + this.name + '&lt;/span&gt;', 
                spanText = '&lt;span class=&quot;badge&quot; style=&quot;margin:2px;width:60px;color:white;background-color:' + this.color + '&quot;&gt;'
                                    + this.x + '&lt;/span&gt; ';
                return legendValueText + spanText + this.name + ': ' + this.displayPercentage;
                },
            }, 
            chartDimensionJSON = {
                width : 420,
                height : 200
            },
            data = [
                {
                     name : 'Available',
                     displayPercentage : '20%',
                     y : 20,
                     x : 40,
                     color : yg.colors.GREEN
                },
                {
                     name : 'Expected',
                     displayPercentage : '40%',
                     y : 40,
                     x : 80,
                     color : yg.colors.BLUE
        
                 },
                 {
                     name : 'Outdated',
                     displayPercentage : '10%',
                     y : 10,
                     x : 20,
                     color : yg.colors.ORANGE
                 },
                 {
                     name : 'Unavailable',
                     displayPercentage : '30%',
                     y : 30,
                     x : 60,
                     color : yg.colors.GRAY
                 } 
             ];
    
    chart.highcharts({
        credits : yg.highcharts_options.disable_credits,
        chart : $.extend({},yg.highcharts_options.chart_options, chartDimensionJSON),
        legend : $.extend({},yg.highcharts_options.pie_chart_options.legend, legendOptionsJSON),
        title : yg.highcharts_options.pie_chart_options.title,
        tooltip : yg.highcharts_options.pie_chart_options.tooltip,
        plotOptions : {
            pie : yg.highcharts_options.pie_chart_options.plotOptions.pie
        },
        series : [ {
            type : yg.highcharts_options.pie_chart_options.series_type_pie,
            data : data
        } ]
       });
    &lt;/script&gt;
    </pre>

    <div class="section">
        <h2>Line Graph with Sample Data</h2>
        <p class="description">
            The following example illustrates how a line graph can be created using the highcharts/highstock JS library. This uses the yukon.highChart.js buildChart method to establish a common look and feel among the line graphs that are used within Yukon.
        </p>
        <h4 class="subtle">Example:</h4>
        <div style="max-height: 300px; max-length: 470px;" class="js-chart-container" />
        
        <script>
            var data = {
                xaxis: {
                    max: 1613617600000,
                    min: 1610755200000,
                    type: 'datetime'
                },
                yaxis: [{
                    alignTicks: true,
                    endOnTick: false,
                    gridLineWidth: 1,
                    index: 0,
                    min: 0,
                    startOnTick: false,
                    tickAmount: 6,
                    title: {
                        rotation: 270,
                        text: 'kWH / day',
                        align: 'middle'
                    }
                }],
                seriesDetails: [{
                    color: '#5d9c53',
                    borderColor: 'EMERALD',
                    fillOpacity: '0.45',
                    marker: {enabled: true},
                    showInLegend: false,
                    type: 'area',
                    yAxis: 0,
                    data: [
                        {
                            "x": 1611100800000,
                            "tooltip": "Delivered kWh<div>1.293 kWH / day</div><div>01/20/2021 12:00:00.000 AM</div>",
                            "y": 1.292699999998149
                        },
                        {
                            "x": 1611187200000,
                            "tooltip": "Delivered kWh<div>1.302 kWH / day</div><div>01/21/2021 12:00:00.000 AM</div>",
                            "y": 1.302400000004127
                        },
                        {
                            "x": 1611273600000,
                            "tooltip": "Delivered kWh<div>1.313 kWH / day</div><div>01/22/2021 12:00:00.000 AM</div>",
                            "y": 1.3125999999974738
                        },
                        {
                            "x": 1611331200000,
                            "tooltip": "Delivered kWh<div>1.751 kWH / day</div><div>01/22/2021 04:00:00.000 PM</div>",
                            "y": 1.7511000000031345
                        },
                        {
                            "x": 1611532800000,
                            "tooltip": "Delivered kWh<div>1.215 kWH / day</div><div>01/25/2021 12:00:00.000 AM</div>",
                            "y": 1.215385714284951
                        },
                        {
                            "x": 1611619200000,
                            "tooltip": "Delivered kWh<div>1.358 kWH / day</div><div>01/26/2021 12:00:00.000 AM</div>",
                            "y": 1.357800000001589
                        }
                    ],
                }]
            };
            
            yukon.highChart.buildChart($(".js-chart-container"), data, "Previous Month's Midnight Reading Usage Reading", "300", "470");

        </script>

    </div>
        
        <h4 class="subtle">Code:</h4>
        <pre class="code prettyprint">
        &lt;div style=&quot;max-height: 300px; max-length: 470px;&quot; class=&quot;js-chart-container&quot; /&gt;
        &lt;script&gt;
            var data = {
                xaxis: {
                    max: 1613617600000,
                    min: 1610755200000,
                    type: 'datetime'
                },
                yaxis: [{
                    alignTicks: true,
                    endOnTick: false,
                    gridLineWidth: 1,
                    index: 0,
                    min: 0,
                    startOnTick: false,
                    tickAmount: 6,
                    title: {
                        rotation: 270,
                        text: 'kWH / day',
                        align: 'middle'
                    }
                }],
                seriesDetails: [{
                    color: '#5d9c53',
                    borderColor: 'EMERALD',
                    fillOpacity: '0.45',
                    marker: {enabled: true},
                    showInLegend: false,
                    type: 'area',
                    yAxis: 0,
                    data: [
                        {
                            "x": 1611100800000,
                            "tooltip": "Delivered kWh&lt;div&gt;1.293 kWH / day&lt;/div&gt;&lt;div&gt;01/20/2021 12:00:00.000 AM&lt;/div&gt;",
                            "y": 1.292699999998149
                        },
                        {
                            "x": 1611187200000,
                            "tooltip": "Delivered kWh&lt;div&gt;1.302 kWH / day&lt;/div&gt;&lt;div&gt;01/21/2021 12:00:00.000 AM&lt;/div&gt;",
                            "y": 1.302400000004127
                        },
                        {
                            "x": 1611273600000,
                            "tooltip": "Delivered kWh&lt;div&gt;1.313 kWH / day&lt;/div&gt;&lt;div&gt;01/22/2021 12:00:00.000 AM&lt;/div&gt;",
                            "y": 1.3125999999974738
                        },
                        {
                            "x": 1611331200000,
                            "tooltip": "Delivered kWh&lt;div&gt;1.751 kWH / day&lt;/div&gt;&lt;div&gt;01/22/2021 04:00:00.000 PM&lt;/div&gt;",
                            "y": 1.7511000000031345
                        },
                        {
                            "x": 1611532800000,
                            "tooltip": "Delivered kWh&lt;div&gt;1.215 kWH / day&lt;/div&gt;&lt;div&gt;01/25/2021 12:00:00.000 AM&lt;/div&gt;",
                            "y": 1.215385714284951
                        },
                        {
                            "x": 1611619200000,
                            "tooltip": "Delivered kWh&lt;div&gt;1.358 kWH / day&lt;/div&gt;&lt;div&gt;01/26/2021 12:00:00.000 AM&lt;/div&gt;",
                            "y": 1.357800000001589
                        }
                    ],
                }]
            };
            
            yukon.highChart.buildChart($(".js-chart-container"), data, "Previous Month's Midnight Reading Usage Reading", "300", "470");
    
        &lt;/script&gt;
        </pre>
        </div>
    </tags:styleguide>
</cti:standardPage>