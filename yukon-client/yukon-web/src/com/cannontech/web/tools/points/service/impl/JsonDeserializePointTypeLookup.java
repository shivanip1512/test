package com.cannontech.web.tools.points.service.impl;

import java.io.IOException;

import com.cannontech.common.exception.TypeNotSupportedException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.point.PointType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.tools.points.model.LitePointModel;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("serial")
public class JsonDeserializePointTypeLookup extends StdDeserializer<LitePointModel> {

    protected JsonDeserializePointTypeLookup() {
        super(LitePointModel.class);
    }

    private PointDao pointDao = YukonSpringHook.getBean(PointDao.class);

    @Override
    public LitePointModel deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException, TypeNotSupportedException {
        TreeNode node = parser.readValueAsTree();
        if (node == null) {
            throw new NotFoundException("request is not found in correct format");
        }
        String idStr = ServletUtils.getPathVariable("id");
        Integer id = null;
        PointType pointType;
        TreeNode typeTreeNode = node.get("pointType");
        if (idStr == null) {
            // Create Case
            pointType = getPointTypeFromJson(typeTreeNode);
        } else {
            // Update case
            // if pointType field is present in request, Validate pointType.
            if (typeTreeNode != null) {
                getPointTypeFromJson(typeTreeNode);
            }
            id = Integer.valueOf(idStr);
            pointType = getPointTypeFromDb(idStr);
        }

        LitePointModel litePointModel = (LitePointModel) parser.getCodec().treeToValue(node, getPointFromModelFactory(pointType, id).getClass());
        if (litePointModel != null) {
            litePointModel.setPointType(pointType);
            litePointModel.setPointId(id);
        }
        return litePointModel;
    }

    /**
     * Retrieves PointType from type field provided in JSON.
     * @throws TypeNotSupportedException when invalid PointType is provided in JSON,
     * this exception is handled by ApiExceptionHandler which will convert it into a global error.
     */
    private PointType getPointTypeFromJson(TreeNode pointType) throws TypeNotSupportedException {
        String pointTypeString = null;
        if (pointType != null) {
            try {
                pointTypeString = pointType.toString().replace("\"", "");
                return PointType.valueOf(pointTypeString);
            } catch (IllegalArgumentException e) {
                // throw exception for invalid pointType
                throw new TypeNotSupportedException(pointTypeString + " pointType is not valid.");
            }
        } else {
            throw new NotFoundException("PointType is not found in the request.");
        }

    }

    /**
     * Retrieves PointType from database based on pointId provided.
     */
    private PointType getPointTypeFromDb(String id) {
        PointType pointType = pointDao.getLitePoint(Integer.valueOf(id)).getPointTypeEnum();
        return pointType;

    }

    /**
     * Retrieves LitePointModel from model factory.
     */
    private LitePointModel getPointFromModelFactory(PointType pointType, Integer id) {
        LitePointModel litePointModel = PointModelFactory.getModel(pointType);
        if (litePointModel != null) {
            litePointModel.setPointId(id);
            return litePointModel;
        } else {
            // throw exception for not supported pointType
            throw new TypeNotSupportedException(pointType + " type is not supported.");
        }
    }
}