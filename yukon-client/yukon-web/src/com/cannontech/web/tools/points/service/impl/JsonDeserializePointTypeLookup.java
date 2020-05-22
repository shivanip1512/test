package com.cannontech.web.tools.points.service.impl;

import java.io.IOException;

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
    public LitePointModel deserialize(JsonParser parser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        TreeNode node = parser.readValueAsTree();
        // Catch the update case here.
        String id = ServletUtils.getPathVariable("id");
        PointType pointType = null;
        if (id == null) {
            // Create case. We should expect "type" field in the request.
            TreeNode type = node.get("pointType");
            if (type != null) {
                pointType = PointType.valueOf(type.toString().replace("\"", ""));
            } else {
                throw new NotFoundException("pointType is not found in the request.");
            }
        } else {
            // Update case.
            pointType = pointDao.getLitePoint(Integer.valueOf(id)).getPointTypeEnum();
        }

        Class<?> clazz = PointBaseModelFactory.createPointBaseModel(pointType).getClass();
        return (LitePointModel) parser.getCodec().treeToValue(node, clazz);
    }
}