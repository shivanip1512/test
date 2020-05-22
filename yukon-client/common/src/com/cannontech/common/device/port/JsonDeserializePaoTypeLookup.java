package com.cannontech.common.device.port;

import java.io.IOException;

import com.cannontech.common.device.model.PaoModelFactory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.yukon.IDatabaseCache;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("serial")
public class JsonDeserializePaoTypeLookup extends StdDeserializer<YukonPao> {

    protected JsonDeserializePaoTypeLookup() {
        super(YukonPao.class);
    }

    private IDatabaseCache serverDatabaseCache = YukonSpringHook.getBean(IDatabaseCache.class);

    @Override
    public YukonPao deserialize(JsonParser parser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        TreeNode node = parser.readValueAsTree();
        // Catch the update case here.
        String id = ServletUtils.getPathVariable("id");
        PaoType paoType = null;
        TreeNode type = node.get("type");
        if (id == null) {
            // Create Case
            if (type != null) {
                try {
                    paoType = PaoType.valueOf(type.toString().replace("\"", ""));
                } catch (IllegalArgumentException e) {
                    throw new NotFoundException("type is not valid.");
                }
            } else {
                throw new NotFoundException("type is not found in the request.");
            }

        } else {
            // Update case.
            LiteYukonPAObject pao = serverDatabaseCache.getAllPaosMap().get(Integer.valueOf(id));
            if (pao != null) {
                paoType = pao.getPaoType();
            } else {
                throw new NotFoundException("id not found.");
            }
        }

        Class<?> clazz = PaoModelFactory.getModel(paoType).getClass();
        return (YukonPao) parser.getCodec().treeToValue(node, clazz);
    }
}