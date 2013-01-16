package com.cannontech.web.common.chart.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.ivvc.models.VfGraph;

public interface FlotChartService {
    
    /**
     * This method returns a JSONObject to be consumed by yukon.flot.js (our implementation of FlotCharts.js)
     * The format of this object is as follows:
     * 
     * (if the format of this is changed, please update this comment)
     * 
     * {
     *      "datas": [{
     *          "data": [
     *              [xValue1, yValue1, {"tooltip": "tooltip html"}],
     *              [xValue2, yValue2, {"tooltip": "tooltip html"}]
     *          ]}],
     *      "type": "bar/line",
     *      "options": {
     *          "series": {
     *              "bars": {"barWidth": somebarwidth}
     *          },
     *          "yaxis": {
     *              "position":"left",
     *              "axisLabel":"some axis label"
     *          },
     *          "xaxis": {
     *              "mode": "time",
     *              "min": min_value,
     *              "max": max_value,
     *              "autoscaleMargin": 0.1
     *          }
     *      }
     *  }
     *  
     *  @return JSONObject
     */
    JSONObject getMeterGraphData(List<Integer> pointIds, Date startDate, Date stopDate, Double yMin, Double yMax,
                                ChartInterval interval, ConverterType converterType, GraphType graphType, String yLabel,
                                YukonUserContext userContext);
    /**
     * This method returns a JSONObject for IVVC SubBus and Zone charts
     * to be consumed by yukon.flot.js (our implementation of FlotCharts.js)
     * The format of this object is as follows:
     * 
     * (if the format of this is changed, please update this comment)
     * 
     * {
     *      "datas": [{
     *          "title": "gang", 
     *          "titleXPos": 0.75,
     *          "titleYPos": 125,
     *          "lineName": "Phase A",
     *          "data":[
     *              [0.75, 125, {"tooltip": "tooltip html"}],
     *              [1.75, 124, {"tooltip": "tooltip html"}]
     *          ],
     *          "color": "#993333",
     *          "phase": "A"
     *      },
     *      {
     *          "title": "gang",
     *          "titleXPos": 0.75,
     *          "titleYPos": 125,
     *          "lineName": "Phase B",
     *          "data": [
     *              [0.75, 125, {"tooltip": "tooltip html"}],
     *              [3.75, 127, {"tooltip": "tooltip html"}]
     *          ],
     *          "color": "#2929FF",
     *          "phase": "B"
     *      }],
     *      "options": {
     *          "xaxis": {
     *              "autoscaleMargin": 0.1
     *          },
     *          "yaxis": {
     *              "position": "left",
     *              "axisLabel": "Volts",
     *              "min": 107,
     *              "max": 133,
     *              "autoscaleMargin": 0.1
     *          },
     *          "grid": {
     *              "markings": [{
     *                  "color": "#f1f1f1",
     *                  "yaxis": {"from": 130 (strategyHigh value)}
     *              },
     *              {
     *                  "color": "#f1f1f1",
     *                  "yaxis": {"to": 110 (strategyLow value)}
     *              }]
     *          }
     *      }
     *  }
     *  
     *  @return JSONObject
     */
    JSONObject getIVVCGraphData(VfGraph graph, boolean includeTitles);
    
    /**
     * This method returns a JSONObject to be consumed by yukon.flot.js (our implementation of FlotCharts.js)
     * The format of this object is as follows:
     * 
     * (if the format of this is changed, please update this comment)
     * 
     * {
     *      "datas": [{
     *          "label": "unique label",
     *          "data": some numeric value,
     *          "tooltip": "tooltip html"
     *      },
     *      {
     *          "label": "unique label",
     *          "data": some numeric value,
     *          "tooltip": "tooltip html"
     *      }],
     *      "type": "pie"
     *  }
     * 
     * @return JSONObject
     */
    JSONObject getPieGraphData(Map<String, Integer> labelValueMap);
}
