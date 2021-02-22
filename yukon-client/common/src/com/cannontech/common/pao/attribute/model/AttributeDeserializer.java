package com.cannontech.common.pao.attribute.model;

import java.io.IOException;

import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.spring.YukonSpringHook;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class AttributeDeserializer extends StdDeserializer<Attribute> {
    
    protected AttributeDeserializer() {
        super(Attribute.class);
    }
    
    private AttributeService attributeService = YukonSpringHook.getBean(AttributeService.class);

    @Override
    public Attribute deserialize(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
            throws IOException, JsonProcessingException {
        TreeNode node = paramJsonParser.readValueAsTree();
        TreeNode idNode = node.get("customAttributeId");
        String attributeName = null;
        if (idNode != null) {
            attributeName = idNode.toString();
        } else {
            attributeName = node.toString().replaceAll("\"", "");
        }
        return attributeService.resolveAttributeName(attributeName);
    }

}
