package com.eaton.builders.admin.attributes;

import java.util.Optional;
import java.util.UUID;

import org.json.JSONObject;

public class AttributeEditBuilder {
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

    }
}