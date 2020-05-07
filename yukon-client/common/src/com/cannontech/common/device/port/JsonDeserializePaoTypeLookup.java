package com.cannontech.common.device.port;

import java.io.IOException;

import com.cannontech.common.device.port.service.impl.PortServiceImpl;
import com.cannontech.common.pao.PaoType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.yukon.IDatabaseCache;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("serial")
public class JsonDeserializePaoTypeLookup extends StdDeserializer<Object> {

    protected JsonDeserializePaoTypeLookup() {
        super(Object.class);
    }

    private IDatabaseCache serverDatabaseCache = YukonSpringHook.getBean(IDatabaseCache.class);

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        TreeNode node = parser.readValueAsTree();
        // Catch the update case here
        String portId = ServletUtils.getPathVariable("portId");
        PaoType paoType = null;
        if (portId == null) {
            // Create case.
            paoType = PaoType.valueOf(node.get("type").toString().replace("\"", ""));
        } else {
            // Update case.
            paoType = serverDatabaseCache.getAllPaosMap().get(Integer.valueOf(portId)).getPaoType();
        }

        Class<?> clazz = PortServiceImpl.getModel(paoType).getClass();
        return parser.getCodec().treeToValue(node, clazz);
    }
}