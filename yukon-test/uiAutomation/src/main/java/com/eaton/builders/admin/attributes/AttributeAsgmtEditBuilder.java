package com.eaton.builders.admin.attributes;

import java.util.Optional;

import org.json.JSONObject;

import com.github.javafaker.Faker;

public class AttributeAsgmtEditBuilder {

    public static class Builder {

        private int attributeId;
        private String paoType;
        private String pointType;
        private Integer offSet;
        private Faker faker = new Faker();

        public Builder(Integer attributeId) {
            this.attributeId = attributeId;
        }

        public Builder withPaoType(Optional<AttributeAsgmtTypes.PaoTypes> paoType) {
            AttributeAsgmtTypes.PaoTypes type = paoType.orElse(AttributeAsgmtTypes.PaoTypes.getRandomPaoType());

            this.paoType = type.getPaoType();

            return this;
        }

        public Builder withPointType(Optional<AttributeAsgmtTypes.PointTypes> pointType) {
            AttributeAsgmtTypes.PointTypes type = pointType.orElse(AttributeAsgmtTypes.PointTypes.getRandomPointType());

            this.pointType = type.getPointType();

            return this;
        }

        public Builder withMultiplier(Optional<Integer> offSet) {
            this.offSet = offSet.orElse(faker.number().numberBetween(1, 99999999));

            return this;
        }

        public JSONObject build() {
            JSONObject jo = new JSONObject();
            jo.put("attributeId", this.attributeId);
            jo.put("paoType", this.paoType);
            jo.put("pointType", this.pointType);
            jo.put("offset", this.offSet);
            return jo;
        }
    }
}