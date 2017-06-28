/**
 * Singleton that manages the yukon's FlotCharts implementation
 * 
 * @class yukon.flot javascript
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.flot');

yukon.flot = (function () {
    
    var _initialized = false,
        /* selectors */
        _selector_chart_container = ".flotchart_container",
        _selector_chart = ".flotchart",
        _selector_show_all = ".show_all",
        _selector_tooltip = ".flottip",
        _previousHoverPoint = [],
        _timeoutArgs = '',
        _timeout = 0,
        _defaults = {
            base_options: {
                xaxis: {
                    position: 'bottom'
                },
                yaxis: {
                    position: 'left'
                },
                grid: {
                    hoverable: true,
                    clickable: true,
                    mouseActiveRadius: 50,
                    borderColor: '#ccc'
                },
                selection: {
                    mode: "x",
                    color: "blue"
                }
            }
        },
        /* --------------- */
        /* private methods */
        /* --------------- */
        _plotGraph = function(chartId, params) {
            var data,
                graph_options,
                chartElement,
                chart,
                key,
                obj,
                o;
            /* before plot hook */
            if (typeof mod.charts[chartId].methods.beforePlotGraph !== 'undefined') {
                mod.charts[chartId].methods.beforePlotGraph();
            }
            
            /* setup our data, labels, and tool tips */
            data = mod.charts[chartId].methods.getFilteredGraphData(chartId);
            
            /* make a copy of data b/c setupGraph messes with it */
            data = $.extend(true, [], data);
            _setupGraph(chartId, data);
            
            graph_options = mod.charts[chartId].options;
            if (typeof params !== 'undefined' && typeof params.type !== 'undefined') {
                graph_options = $.extend(true, {}, graph_options, _defaults.options_type_map[params.type]); 
            }
            if (typeof params !== 'undefined' && typeof params.options !== 'undefined') {
                graph_options = $.extend(true, {}, graph_options, params.options);
            }

            /* if the graph is being updated via ajax we want to use the same zoom level */
            if (mod.charts[chartId].ranges_xaxis_from) {
                graph_options = $.extend(true, {}, graph_options, {
                    xaxis: { min: mod.charts[chartId].ranges_xaxis_from, max: mod.charts[chartId].ranges_xaxis_to }
                });
            }

            chartElement = $(document.getElementById(chartId));
            chartElement.empty();
            chart = $.plot(chartElement, data, graph_options);
            mod.charts[chartId].chart = chart;
            
            /* after plot hook */
            if (typeof mod.charts[chartId].methods.afterPlotGraph !== 'undefined') {
                mod.charts[chartId].methods.afterPlotGraph();
            }
            
            // add graph labels
            var topoffset = 0;
            if (chart.getData().length === 0) return;
            for (key in mod.charts[chartId].labels) {
                obj = mod.charts[chartId].labels[key];
                o = chart.pointOffset({ x: obj.x, y: obj.y,});
                // we just append it to the chartContainer which Flot already uses for positioning
                chartElement.append('<div style="position:absolute;left:' + (o.left - 10) +
                                'px;top:' + (o.top - 25 - topoffset) + 'px;color:#666;font-size:smaller">' +
                                key + '</div>');
                topoffset = topoffset+10;
            }
        },
        _validateReloadParams = function(params) {
            if (typeof params.chartId === 'undefined') throw "no chartId specified";
            if (typeof params.dataUrl === 'undefined') throw "no dataUrl specified";
        },
        _getDefaultMergedOptions = function(type, options) {
            return $.extend(true, {}, _defaults.options_type_map[type], options);
        },
        _setupGraph = function(chartId, data) {
            var labels = {},
                label_data,
                tooltipMap = {},
                i,
                j,
                x,
                y,
                meta_data,
                key;
            for (i=0; i < data.length; i++) {
                /* check if we have an empty data set "[[]]" */
                if (typeof data[i].data === 'undefined') continue;
                /* if data isn't an array then we are a pie chart (much simpler) */
                if (typeof data[i].data.length === 'undefined' &&
                    typeof data[i].tooltip !== 'undefined') {
                    tooltipMap[data[i].label] = data[i].tooltip;
                    delete data[i].tooltip;
                } else {
                    /* we are graphing a line or bar chart */
                    for (j=0; j < data[i].data.length; j++) {
                            x = data[i].data[j][0];
                            y = data[i].data[j][1];
                            meta_data = data[i].data[j][2];
    
                        if (typeof meta_data === 'undefined') continue;
                        
                        /* build up our tool-tip object */
                        if (typeof meta_data.tooltip !== 'undefined') {
                            key = _getTooltipMapKey(x, y);
                            
                            /* if we already have a point at that location */
                            if (typeof tooltipMap[key] !== 'undefined') {
                                tooltipMap[key] += '<div class="divider"></div>';
                                tooltipMap[key] += meta_data.tooltip;
                            } else {
                                tooltipMap[key] = meta_data.tooltip;
                            }
                        }
                        /* remove our extra meta data field so FlotCharts doesn't choke on it */
                        data[i].data[j].splice(2,1);
                    }
                }

                // build up our graph labels
                if (typeof data[i].title !== 'undefined') {
                    label_data = {};
                    label_data.x = data[i].titleXPos;
                    label_data.y = data[i].titleYPos;
                    labels[data[i].title] = label_data;
                }
            }
            mod.charts[chartId].tooltipMap = tooltipMap;
            mod.charts[chartId].labels = labels;
            mod.charts[chartId].data = data;
        },
        _getChartIdFromElement = function(elem) {
            return $(elem).closest(_selector_chart_container).find(_selector_chart).attr("id");
        },
        _showTooltip = function(x, y, contents) {
            var pos = {top: y + 11, left: x + 11};
            if ($(_selector_tooltip).length > 0) {
                $(_selector_tooltip).css(pos);
                return;
            }
            // substr here to remove the leading period
            $("<div class='" +
                    _selector_tooltip.substr(1, _selector_tooltip.length) + "'>" +
                    contents + "</div>").css(pos).appendTo("body").fadeIn(200);
        },
        _getTooltipMapKey = function(x, y) {
            return x;
        },
        _getFilteredGraphData = function(chartId) {
            return mod.charts[chartId].data_with_meta;
        },
        /* --------------- */
        /* event handlers  */
        /* --------------- */
        _plot_selected = function(event, ranges) {
            var chartId;
            $(_selector_show_all).show();
            chartId = this.id;
            mod.charts[chartId].ranges_xaxis_from = ranges.xaxis.from; 
            mod.charts[chartId].ranges_xaxis_to = ranges.xaxis.to; 
            mod.charts[chartId].methods.plotGraph(chartId, {
                options: {
                    xaxis: { min: ranges.xaxis.from, max: ranges.xaxis.to}
                }
            });
        },
        _plot_unselected = function(event) {
            var chartId;
            $(this).closest(_selector_chart_container).find(_selector_show_all).hide();
            chartId = this.id;
            mod.charts[chartId].ranges_xaxis_from = null; 
            mod.charts[chartId].ranges_xaxis_to = null;
            mod.charts[chartId].methods.plotGraph(chartId);
        },
        _show_all_clicked = function() {
            var chartId;
            $(this).hide();
            chartId = _getChartIdFromElement(this);
            mod.charts[chartId].ranges_xaxis_from = null; 
            mod.charts[chartId].ranges_xaxis_to = null;
            mod.charts[chartId].methods.plotGraph(chartId);
        },
        _plot_hover = function(event, pos, item) {
            var tt_key,
                remove_prev = false,
                x,
                y,
                chartId;
            if (item) {
                tt_key;
                remove_prev = false;
                /* graph is a pie chart? */
                if (typeof item.series.pie !== 'undefined' && item.series.pie.show === true && typeof item.series.label !== 'undefined') {
                    if (_previousHoverPoint !== item.series.label) {
                        _previousHoverPoint = item.series.label;
                        remove_prev = true;
                    }
                    tt_key = item.series.label;
                } else {
                    /* graph is line/bar */
                    if (_previousHoverPoint[0] !== item.datapoint[0] ||
                            _previousHoverPoint[1] !== item.datapoint[1]) {
                        _previousHoverPoint[0] = item.datapoint[0];
                        _previousHoverPoint[1] = item.datapoint[1];
                        
                        remove_prev = true;
                    }
                    x = item.datapoint[0],
                    y = item.datapoint[1];
                    tt_key = _getTooltipMapKey(x, y);
                }
                if (remove_prev) {
                    /* remove the tool tip element */
                    $(_selector_tooltip).remove();
                }
                chartId = _getChartIdFromElement(this);
                _showTooltip(pos.pageX, pos.pageY, mod.charts[chartId].tooltipMap[tt_key]);
            }
            else {
                $(_selector_tooltip).remove();
                _previousHoverPoint = [];
            }
        },
        mod;
        
    mod = {

        charts: {},

        _init: function() {
            if (_initialized) return;
            
            /* more options initialization */
            _defaults.methods = {
                plotGraph: _plotGraph,
                getFilteredGraphData: _getFilteredGraphData
            };
            _defaults.bar_options = $.extend(true, {}, {
                series : {
                    color: "rgb(63, 105, 149)",
                    bars: {
                        show: true,
                        align: "center",
                        fill: true,
                        fillColor: "rgb(99,143,189)"
                    },
                    lines: { 
                        show: false
                    },
                    points: { 
                        show: false
                    }
                }
            }, _defaults.base_options);
            _defaults.line_options = $.extend(true, {}, {
                series : {
                    fillColor: "rgb(99,143,189)",
                    color: "rgb(63, 105, 149)",
                    bars: {
                        show: false
                    },
                    lines: { 
                        show: true,
                        fill: true
                    },
                    points: { 
                        show: true
                    }
                }
            }, _defaults.base_options);
            _defaults.pie_options = $.extend(true, {}, {
                series: {
                    pie: { 
                        show: true
                    }
                }
            }, _defaults.base_options);
            _defaults.options_type_map = {
                bar: _defaults.bar_options,
                line: _defaults.line_options,
                pie: _defaults.pie_options
            };

            $(document).on("plothover", _selector_chart, _plot_hover);
            // TODO: the plot thickens: the _plot_click method is nowhere to be found!
            //$(document).on("plotclick", _selector_chart, _plot_click);
            $(document).on("plotselected", _selector_chart, _plot_selected);
            $(document).on("plotunselected", _selector_chart, _plot_unselected);
            $(document).on("click", _selector_show_all, _show_all_clicked);

            _initialized = true;
        },

        /* -------------- */
        /* public methods */
        /* -------------- */

        /**
         * Add a flotChart. This method simply adds the options, methods, and data to the Singleton.
         * Calling plotGraph (yukon.flot.charts[chartId].methods.plotGraph(chartId)) is still required for the chart to show up
         * 
         * Required parameters: chartId, type (bar, line, pie), data
         */
        addChart: function(params) {
            var chartId = params.chartId,
                type = params.type.toLowerCase(),
                options = params.options,
                methods = params.methods,
                data = params.data;
            /* validation */
            if (typeof chartId === 'undefined') throw "no chartId specified";
            if (typeof type === 'undefined') throw "no type specified (bar, line, etc)";
            if (typeof data === 'undefined') throw "no data specified";

            /* initialization */
            mod.charts[chartId] = {};
            mod.charts[chartId].options = _getDefaultMergedOptions(type, options);
            mod.charts[chartId].methods = $.extend(true, {}, _defaults.methods, methods);
            mod.charts[chartId].data_with_meta = data;
        },

        /**
         * Method that is meant to work with the cti:dataUpdaterCallback tag
         * 
         * Required parameters: chartId, dataUrl
         */
        reloadChartIfExpired: function(params) {
            var chartId,
                newLargestTime;
            /* validation */
            _validateReloadParams(params);
            chartId = params.chartId;
            //assumes data is of type Hash
            return function(data) {
                newLargestTime = data.largestTime;
                if (typeof mod.charts[chartId].mostRecentPointTime === 'undefined') {
                    mod.charts[chartId].mostRecentPointTime = newLargestTime;
                }
                if (mod.charts[chartId].mostRecentPointTime > 0 &&
                    newLargestTime > mod.charts[chartId].mostRecentPointTime) {
                    mod.charts[chartId].mostRecentPointTime = newLargestTime;
                    mod.reloadFlotChart(params);
                }
            };
        },
        /**
         * This method currently only supports reloading one chart per page.
         * If you can figure out the setTimeout call to support multiple charts, let me know :)
         * 
         *  Required parameters: chartId, dataUrl, reloadInterval (in milliseconds)
         */
        reloadChartOnInterval: function(params) {
            /* validation */
            _validateReloadParams(params);
            if (typeof params.reloadInterval === 'undefined') throw "no reloadInterval specified";

            _timeoutArgs = params;
            clearTimeout(_timeout);
            _timeout = setTimeout(function () {
                mod.reloadChartOnInterval(_timeoutArgs);
            }, params.reloadInterval);
            mod.reloadFlotChart({chartId: params.chartId, dataUrl: params.dataUrl});
        },
        /**
         * Reload a chart. If the chart hasn't been added yet, then it is added
         * 
         *  Required parameters: chartId, dataUrl
         */
        reloadFlotChart: function(params) {
            /* validation */
            _validateReloadParams(params);
            $.ajax({
                url: params.dataUrl
            }).done(function (data, textStatus, jqXHR) {
                /* if the chart hasn't been added yet, add it */
                if (typeof mod.charts[params.chartId] === 'undefined') {
                    mod.addChart({
                        chartId : params.chartId,
                        type : data.type,
                        options : data.options,
                        methods : data.methods,
                        data : data.datas
                    });
                }
                mod.charts[params.chartId].options = _getDefaultMergedOptions(data.type, data.options);
                mod.charts[params.chartId].data_with_meta = data.datas;
                mod.charts[params.chartId].methods.plotGraph(params.chartId);
            });
        }
    };
    return mod;
}());

$(function() {
    yukon.flot._init();
});
