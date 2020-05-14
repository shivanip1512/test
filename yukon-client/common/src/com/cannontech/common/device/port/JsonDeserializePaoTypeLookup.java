package com.cannontech.common.device.port;

import java.io.IOException;

import com.cannontech.common.device.model.PaoModelFactory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.NotFoundException;
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
        if (id == null) {
            // Create case. We should expect "type" field in the request.
            TreeNode type = node.get("type");
            if (type != null) {
                paoType = PaoType.valueOf(type.toString().replace("\"", ""));
            } else {
                throw new NotFoundException("type is not found in the request.");
            }
        } else {
            // Update case.
            paoType = serverDatabaseCache.getAllPaosMap().get(Integer.valueOf(id)).getPaoType();
        }

        Class<?> clazz = PaoModelFactory.getModel(paoType).getClass();
        return (YukonPao) parser.getCodec().treeToValue(node, clazz);
    }
}