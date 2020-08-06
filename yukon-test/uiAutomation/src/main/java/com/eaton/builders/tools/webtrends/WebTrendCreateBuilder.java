package com.eaton.builders.tools.webtrends;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.eaton.framework.TestConstants;
import com.eaton.rest.api.trend.TrendRequest;

import io.restassured.response.ExtractableResponse;

public class WebTrendCreateBuilder {
    
    public static class TrendSeries {

        private WebTrendEnums.Type type;
        private WebTrendEnums.Color color;
        private WebTrendEnums.Axis axis;
        private WebTrendEnums.Style style;
        private String date;
        private Integer pointId;
        private String label;
        private Double multiplier;
    }
    
    public static class TrendModel {
        private Integer trendId;
        private String name;
        private TrendSeries trendSeries;
    }

    public static class Builder {

        TrendSeries trendSeries = new TrendSeries();
        TrendModel trendModel= new TrendModel();
        
        public Builder(Optional<String> name) {
            String u = UUID.randomUUID().toString();
            String uuid = u.replace("-", "");

            trendModel.name = name.orElse("AT WT " + uuid);
        }
        
        public Builder withLabel(Optional<String> label) {
            trendSeries.label = label.orElse("Analog Point");
            return this;
        }

        public Builder withMultiplier(Optional<Double> multiplier) {
            trendSeries.multiplier = multiplier.orElse(2.0);
            return this;
        }
        
        public Builder withPointId(Optional<Integer> pointId) {
            trendSeries.pointId = pointId.orElse(-110);
            return this;
        }
        
        public Builder withType(Optional<WebTrendEnums.Type> type) {
            WebTrendEnums.Type trendType = type.orElse(WebTrendEnums.Type.BASIC_TYPE);
            // this.baudRate = rate.getBaudRate();
            trendSeries.type = trendType;
            return this;

        }
        
        public Builder withDate(Optional<String> date) {
            String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
            trendSeries.date= date.orElse(timeStamp);
            
            return this;
        }

        public Builder withColor(Optional<WebTrendEnums.Color> color) {
            WebTrendEnums.Color colorAs = color.orElse(WebTrendEnums.Color.RED);
            
            trendSeries.color = colorAs;
            return this;
        }
        
        public Builder withAxis(Optional<WebTrendEnums.Axis> axis) {
            WebTrendEnums.Axis axisAs = axis.orElse(WebTrendEnums.Axis.LEFT);
            
            trendSeries.axis = axisAs;
            return this;
        }
        public JSONObject build() {
            JSONObject j = new JSONObject();
            j.put("name", this.trendModel.name);
            
            JSONObject jo=new JSONObject();
            jo.put("type", trendSeries.type);
            jo.put("pointId", trendSeries.pointId);
            jo.put("label", trendSeries.label);
            jo.put("color", trendSeries.color);
            jo.put("axis", trendSeries.axis);
            jo.put("multiplier", trendSeries.multiplier);
            jo.put("style", trendSeries.style);
            if((trendSeries.date).equals("DATE_TYPE")) {
                jo.put("date", trendSeries.date);
            }

            JSONArray ja=new JSONArray();
            ja.put(0,jo);
            
            j.put("trendSeries", ja);
            return j;
        }

        public Pair<JSONObject, JSONObject> create() {
            
            JSONObject request = build();

            ExtractableResponse<?> createResponse = TrendRequest.createTrend(request.toString());

            String id = createResponse.path("trendId").toString();

            ExtractableResponse<?> er = TrendRequest.getTrend(id);

            String res = er.asString();
            JSONObject response = new JSONObject(res);
            
//            trendModel.trendId=createResponse.path("trendId");
//            JSONArray jsonResponse = response.getJSONArray("trendSeries");
//            trendModel.trendSeries= jsonResponse.get(0).toString();
           

            return new Pair<>(request, response);
        }
    }
}
