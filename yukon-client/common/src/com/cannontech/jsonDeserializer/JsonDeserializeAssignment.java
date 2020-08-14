package com.cannontech.jsonDeserializer;

import java.io.IOException;

import com.cannontech.common.exception.TypeNotSupportedException;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.point.PointType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JsonDeserializeAssignment extends StdDeserializer<Assignment> {

    protected JsonDeserializeAssignment() {
        super(Assignment.class);
    }

    @Override
    public Assignment deserialize(JsonParser parser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException, TypeNotSupportedException {
        Assignment assignment = new Assignment();
        TreeNode node = parser.readValueAsTree();
        if (node == null) {
            throw new NotFoundException("request is not found in correct format");
        }
        assignment.setAttributeId(Integer.valueOf(node.get("attributeId").toString()));
        assignment.setOffset(Integer.valueOf(node.get("offset").toString()));
        assignment.setPaoType(getPaoTypeFromJson(node.get("paoType")));
        assignment.setPointType(getPointTypeFromJson(node.get("pointType")));

        return assignment;
    }

    /**
     * Retrieves PaoType from type field provided in JSON.
     * 
     * @throws TypeNotSupportedExcpetion when invalid PaoType is provided in JSON,
     * this exception is handled by ApiExceptionHandler which will convert it into a global error.
     */
    private PaoType getPaoTypeFromJson(TreeNode type) throws TypeNotSupportedException {
        String paoTypeString = null;
        if (type != null) {
            try {
                paoTypeString = type.toString().replace("\"", "");
                return PaoType.valueOf(paoTypeString);
            } catch (IllegalArgumentException e) {
                // throw exception for invalid paoType
                throw new TypeNotSupportedException(paoTypeString + " paoType is not valid.");
            }
        } else {
            throw new NotFoundException("paoType is not found in the request.");
        }

    }

    /**
     * Retrieves PointType from type field provided in JSON.
     * 
     * @throws TypeNotSupportedExcpetion when invalid PointType is provided in JSON,
     * this exception is handled by ApiExceptionHandler which will convert it into a global error.
     */
    private PointType getPointTypeFromJson(TreeNode pointType) throws TypeNotSupportedException {
        String pointTypeString = null;
        if (pointType != null) {
            try {
                pointTypeString = pointType.toString().replace("\"", "");
                return PointType.valueOf(pointTypeString);
            } catch (IllegalArgumentException e) {
                // throw exception for invalid paoType
                throw new TypeNotSupportedException(pointTypeString + " pointType is not valid.");
            }
        } else {
            throw new NotFoundException("pointType is not found in the request.");
        }
    }

}
