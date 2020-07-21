package com.cannontech.amr.archivedValueExporter.model;

import java.io.IOException;

import com.cannontech.common.pao.attribute.dao.AttributeDao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
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
    
    private AttributeDao attributeDao = YukonSpringHook.getBean(AttributeDao.class);

    @Override
    public Attribute deserialize(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
            throws IOException, JsonProcessingException {
        TreeNode node = paramJsonParser.readValueAsTree();
        TreeNode idNode = node.get("id");
        if (idNode != null) {
            return attributeDao.getCustomAttribute(Integer.parseInt(idNode.toString()));
        } else {
            return BuiltInAttribute.valueOf(node.toString().replaceAll("\"", ""));
        }
    }

}
