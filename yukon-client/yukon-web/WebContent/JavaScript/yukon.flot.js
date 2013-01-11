if(typeof(Yukon) === 'undefined'){
    Yukon = {};
}

if(typeof(Yukon.Flot) === 'undefined'){
    /**
     * Singleton that manages the javascript of our FlotCharts implementation
     * 
     * @class Yukon.Flot javascript
     * @author <a href="mailto:alex.delegard@cooperindustries.com">Alex Delegard</a>
     * @requires jQuery 1.6+
     */
    Yukon.Flot = {
        _initialized: false,
        
        /* selectors */
        _selector_chart_container: ".flotchart_container",
        _selector_chart: ".flotchart",
        _selector_show_all: ".show_all",
        _selector_tooltip: ".flottip",
        
        _previousHoverPoint: [],
        charts: {},
        
        defaults: {
            base_options: {
                xaxes: {
                    position: 'bottom',
                    axisLabel: 'xlabel',
                },
                yaxes: {
                    position: 'left',
                    axisLabel: 'bar',
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

        _init: function() {
            if(this._initialized) return;
            
            /* more options initialization */
            this.defaults.methods = {
                plotGraph: this._plotGraph,
                getFilteredGraphData: this._getFilteredGraphData,
            };
            this.defaults.bar_options = jQuery.extend(true, {}, {
                series : {
                    color: "rgb(63, 105, 149)",
                    bars: {
                        show: true,
                        align: "center",
                        fill: true,
                        fillColor: "rgb(99,143,189)",
                    },
                    lines: { 
                        show: false,
                    },
                    points: { 
                        show: false,
                    }
                }
            }, this.defaults.base_options);
            this.defaults.line_options = jQuery.extend(true, {}, {
                series : {
                    fillColor: "rgb(99,143,189)",
                    color: "rgb(63, 105, 149)",
                    bars: {
                        show: false,
                    },
                    lines: { 
                        show: true,
                    },
                    points: { 
                        show: true,
                    }
                }
            }, this.defaults.base_options);
            this.defaults.pie_options = jQuery.extend(true, {}, {
                series: {
                    pie: { 
                        show: true
                    }
                }
            }, this.defaults.base_options);
            this.defaults.options_type_map = {
                bar: this.defaults.bar_options,
                line: this.defaults.line_options,
                pie: this.defaults.pie_options
            };

            jQuery(document).on("plothover", Yukon.Flot._selector_chart, this._plot_hover);
            jQuery(document).on("plotclick", Yukon.Flot._selector_chart, this._plot_click);
            jQuery(document).on("plotselected", Yukon.Flot._selector_chart, this._plot_selected);
            jQuery(document).on("plotunselected", Yukon.Flot._selector_chart, this._plot_unselected);
            jQuery(document).on("click", Yukon.Flot._selector_show_all, this._show_all_clicked);

            this._initialized = true;
        },

        /* -------------- */
        /* public methods */
        /* -------------- */

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
            Yukon.Flot.charts[chartId] = {};
            Yukon.Flot.charts[chartId].options = Yukon.Flot._getDefaultMergedOptions(type, options);
            Yukon.Flot.charts[chartId].methods = jQuery.extend(true, {}, Yukon.Flot.defaults.methods, methods);
            Yukon.Flot.charts[chartId].data_with_meta = data;
        },

        /**
         * Method that is meant to work with the cti:dataUpdaterCallback tag
         */
        reloadChartIfExpired: function(params) {
            /* validation */
            Yukon.Flot._validateReloadParams(params);
            var chartId = params.chartId;

            //assumes data is of type Hash
            return function(data) {
                var newLargestTime = data.get('largestTime');
                if (typeof Yukon.Flot.charts[chartId].mostRecentPointTime === 'undefined') {
                    Yukon.Flot.charts[chartId].mostRecentPointTime = newLargestTime;
                }
                if (Yukon.Flot.charts[chartId].mostRecentPointTime > 0 &&
                    newLargestTime > Yukon.Flot.charts[chartId].mostRecentPointTime) {
                    Yukon.Flot.charts[chartId].mostRecentPointTime = newLargestTime;
                    Yukon.Flot.reloadFlotChart(params);
                }
            };
        },
        reloadFlotChart: function(params) {
            /* validation */
            Yukon.Flot._validateReloadParams(params);

            var ajaxData = {};
            ajaxData[params.attrName] = params.attrVal;
            jQuery.ajax({
                url: params.dataUrl,
                data: ajaxData,
                success: function(data) {
                    Yukon.Flot.charts[params.chartId].options = Yukon.Flot._getDefaultMergedOptions(data.type, data.options);
                    Yukon.Flot.charts[params.chartId].data_with_meta = data.datas;
                    Yukon.Flot.charts[params.chartId].methods.plotGraph(params.chartId);
                }
            });
        },

        /* --------------- */
        /* private methods */
        /* --------------- */

        _plotGraph: function(chartId, params) {
            /* before plot hook */
            if (typeof Yukon.Flot.charts[chartId].methods.beforePlotGraph !== 'undefined') {
                Yukon.Flot.charts[chartId].methods.beforePlotGraph();
            }
            
            /* setup our data, labels, and tool tips */
            var data = Yukon.Flot.charts[chartId].methods.getFilteredGraphData(chartId);
            
            /* make a copy of data b/c setupGraph messes with it */
            data = jQuery.extend(true, [], data);
            Yukon.Flot._setupGraph(chartId, data);
            
            var graph_options = Yukon.Flot.charts[chartId].options;
            if (typeof params !== 'undefined' && typeof params.type !== 'undefined') {
                graph_options = jQuery.extend(true, {}, graph_options, Yukon.Flot.defaults.options_type_map[params.type]); 
            }
            if (typeof params !== 'undefined' && typeof params.options !== 'undefined') {
                graph_options = jQuery.extend(true, {}, graph_options, params.options);
            }

            /* if the graph is being updated via ajax we want to use the same zoom level */
            if (Yukon.Flot.charts[chartId].ranges_xaxis_from) {
                graph_options = jQuery.extend(true, {}, graph_options, {
                    xaxis: { min: Yukon.Flot.charts[chartId].ranges_xaxis_from, max: Yukon.Flot.charts[chartId].ranges_xaxis_to }
                });
            }

            var chartElement = jQuery(document.getElementById(chartId));
            chartElement.empty();
            var chart = jQuery.plot(chartElement, data, graph_options);
            Yukon.Flot.charts[chartId].chart = chart;
            
            /* after plot hook */
            if (typeof Yukon.Flot.charts[chartId].methods.afterPlotGraph !== 'undefined') {
                Yukon.Flot.charts[chartId].methods.afterPlotGraph();
            }
            
            // add graph labels
            if (chart.getData().length === 0) return;
            for (var key in Yukon.Flot.charts[chartId].labels) {
                var obj = Yukon.Flot.charts[chartId].labels[key];
                o = chart.pointOffset({ x: obj.x, y: obj.y,});
                // we just append it to the chartContainer which Flot already uses for positioning
                chartElement.append('<div style="position:absolute;left:' + (o.left - 10) +
                                'px;top:' + (o.top - 25) + 'px;color:#666;font-size:smaller">' +
                                key + '</div>');
            }
        },
        
        _validateReloadParams: function(params) {
            if (typeof params.chartId === 'undefined') throw "no chartId specified";
            if (typeof params.dataUrl === 'undefined') throw "no dataUrl specified";
            if (typeof params.attrName === 'undefined') throw "no attrName specified";
            if (typeof params.attrVal === 'undefined') throw "no attrVal specified";
        },

        _getDefaultMergedOptions: function(type, options) {
            return jQuery.extend(true, {}, Yukon.Flot.defaults.options_type_map[type], options);
        },

        _setupGraph: function(chartId, data) {
            var labels = {};
            var tooltipMap = {};
            for (var i=0; i < data.length; i++) {
                /* if data isn't an array then we are a pie chart (much simpler) */
                if (typeof data[i].data.length === 'undefined') {
                    if (typeof data[i].tooltip !== 'undefined') {
                        tooltipMap[data[i].label] = data[i].tooltip;
                        delete data[i].tooltip;
                    }
                } else {
                    /* we are graphing a line or bar chart */
                    for (var j=0; j < data[i].data.length; j++) {
                        var x = data[i].data[j][0],
                            y = data[i].data[j][1],
                            meta_data = data[i].data[j][2];
    
                        if (typeof meta_data === 'undefined') continue;
                        
                        /* build up our tool-tip object */
                        if (typeof meta_data.tooltip !== 'undefined') {
                            var key = Yukon.Flot._getTooltipMapKey(x, y);
                            
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
                    var label_data = {};
                    label_data.x = data[i].titleXPos;
                    label_data.y = data[i].titleYPos;
                    labels[data[i].title] = label_data;
                }
            }
            Yukon.Flot.charts[chartId].tooltipMap = tooltipMap;
            Yukon.Flot.charts[chartId].labels = labels;
            Yukon.Flot.charts[chartId].data = data;
        },
        
        _getChartIdFromElement: function(elem) {
            return jQuery(elem).closest(Yukon.Flot._selector_chart_container).find(Yukon.Flot._selector_chart).attr("id");
        },

        _showTooltip: function(x, y, contents) {
            var pos = {top: y + 11, left: x + 11};
            if (jQuery(Yukon.Flot._selector_tooltip).length > 0) {
                jQuery(Yukon.Flot._selector_tooltip).css(pos);
                return;
            }
            // substr here to remove the leading period
            jQuery("<div class='" +
                    Yukon.Flot._selector_tooltip.substr(1, Yukon.Flot._selector_tooltip.length) + "'>" +
                    contents + "</div>").css(pos).appendTo("body").fadeIn(200);
        },
        
        _getTooltipMapKey: function(x, y) {
            return x + "" + y;
        },
        
        _getFilteredGraphData: function(chartId) {
            return Yukon.Flot.charts[chartId].data_with_meta;
        },

        /* --------------- */
        /* event handlers  */
        /* --------------- */

        _plot_selected: function(event, ranges) {
            jQuery(Yukon.Flot._selector_show_all).show();
            var chartId = this.id;
            Yukon.Flot.charts[chartId].ranges_xaxis_from = ranges.xaxis.from; 
            Yukon.Flot.charts[chartId].ranges_xaxis_to = ranges.xaxis.to; 
            Yukon.Flot.charts[chartId].methods.plotGraph(chartId, {
                options: {
                    xaxis: { min: ranges.xaxis.from, max: ranges.xaxis.to }
                }
            });
        },
        
        _plot_unselected: function(event) {
            jQuery(this).closest(Yukon.Flot._selector_chart_container).find(Yukon.Flot._selector_show_all).hide();
            var chartId = this.id;
            Yukon.Flot.charts[chartId].ranges_xaxis_from = null; 
            Yukon.Flot.charts[chartId].ranges_xaxis_to = null;
            Yukon.Flot.charts[chartId].methods.plotGraph(chartId);
        },
        
        _show_all_clicked: function() {
            jQuery(this).hide();
            var chartId = Yukon.Flot._getChartIdFromElement(this);
            Yukon.Flot.charts[chartId].ranges_xaxis_from = null; 
            Yukon.Flot.charts[chartId].ranges_xaxis_to = null;
            Yukon.Flot.charts[chartId].methods.plotGraph(chartId);
        },

        _plot_hover: function(event, pos, item) {
            if (item) {
                var tt_key;
                var remove_prev = false;
                /* graph is a pie chart? */
                if (typeof item.series.pie !== 'undefined' && item.series.pie.show === true && typeof item.series.label !== 'undefined') {
                    if (Yukon.Flot._previousHoverPoint !== item.series.label) {
                        Yukon.Flot._previousHoverPoint = item.series.label;
                        remove_prev = true;
                    }
                    tt_key = item.series.label;
                } else {
                    /* graph is line/bar */
                    if (Yukon.Flot._previousHoverPoint[0] !== item.datapoint[0] ||
                            Yukon.Flot._previousHoverPoint[1] !== item.datapoint[1]) {
                        Yukon.Flot._previousHoverPoint[0] = item.datapoint[0];
                        Yukon.Flot._previousHoverPoint[1] = item.datapoint[1];
                        
                        remove_prev = true;
                    }
                    var x = item.datapoint[0],
                        y = item.datapoint[1];
                    tt_key = Yukon.Flot._getTooltipMapKey(x, y);
                }
                if (remove_prev) {
                    /* remove the tool tip element */
                    jQuery(Yukon.Flot._selector_tooltip).remove();
                }
                var chartId = Yukon.Flot._getChartIdFromElement(this);
                Yukon.Flot._showTooltip(pos.pageX, pos.pageY, Yukon.Flot.charts[chartId].tooltipMap[tt_key]);
            }
            else {
                jQuery(Yukon.Flot._selector_tooltip).remove();
                Yukon.Flot._previousHoverPoint = [];
            }
        }
    };
}
jQuery(function() {
    Yukon.Flot._init();
});
