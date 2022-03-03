package com.eaton.builders.admin.attributes;

import java.util.Optional;
import java.util.UUID;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.rest.api.admin.attributes.AttributesRequest;

import io.restassured.response.ExtractableResponse;

public class AttributesCreateBuilder {

    public static class Builder {

        private String name;

        public Builder(Optional<String> name) {
            String u = UUID.randomUUID().toString();
            String uuid = u.replace("-", "");

            this.name = name.orElse("AT Attr " + uuid);
        }

        public JSONObject build() {
            JSONObject jo = new JSONObject();
            jo.put("name", this.name);
            return jo;
        }

        public Pair<JSONObject, JSONObject> create() {
            JSONObject request = build();

            ExtractableResponse<?> createResponse = AttributesRequest.createAttribute(request.toString());

            String res = createResponse.asString();
            JSONObject response = new JSONObject(res);

            return new Pair<>(request, response);
        }
    }
}