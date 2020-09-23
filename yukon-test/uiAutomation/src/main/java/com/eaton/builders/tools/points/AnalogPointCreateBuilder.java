package com.eaton.builders.tools.points;

import java.util.Optional;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.rest.api.point.PointRequest;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

public class AnalogPointCreateBuilder {
	public static class Builder {
        private Faker faker = new Faker();

        private static final String POINTTYPE = "Analog";
        private String pointName;
        private int pointOffset;
        private int uomId;
        private int paoId;

        public Builder(Optional<String> pointName) {
            String u = UUID.randomUUID().toString();
            String uuid = u.replace("-", "");

            this.pointName = pointName.orElse("Analog Point " + uuid);
        }

        public Builder withPaoId(Integer paoId) {
            this.paoId = paoId;

            return this;
        }
        
        public Builder withPointName(String pointName) {
            this.pointName = pointName;
            return this;
        }

        public Builder withPointOffset(Optional<Integer> pointOffset) {
            this.pointOffset = pointOffset.orElse(faker.number().numberBetween(0, 99999999));
            return this;
        }
        
        public Builder withUomId(Optional<Integer> uomId) {
        	this.uomId = uomId.orElse(faker.number().numberBetween(1, 57));
        	return this;
        }

        public JSONObject build() {
            JSONObject j = new JSONObject();

            JSONObject jo = new JSONObject();
            
            j.put("pointName", this.pointName);
            j.put("pointType", POINTTYPE);
            j.put("paoId", this.paoId);
            j.put("pointOffset", this.pointOffset);
            jo.put("uomId", this.uomId);

            j.put("pointUnit", jo);

            return j;
        }
        
        public Pair<JSONObject, JSONObject> create() {
            JSONObject request = build();
            ExtractableResponse<?> createResponse = PointRequest.createPoint(request.toString());

            String res = createResponse.asString();
            JSONObject response = new JSONObject(res);

            return new Pair<>(request, response);
        }
    }
}
