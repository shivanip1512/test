package com.cannontech.common.typeResolver;

import java.io.IOException;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.AsPropertyTypeDeserializer;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;

public class NestedTypeDeserializer extends AsPropertyTypeDeserializer {

    private static final long serialVersionUID = 1L;
    private final static Logger log = YukonLogManager.getLogger(NestedTypeDeserializer.class);

    public NestedTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible,
            JavaType defaultImpl, JsonTypeInfo.As inclusion) {
        super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl, inclusion);
    }

    public NestedTypeDeserializer(AsPropertyTypeDeserializer src, BeanProperty property) {
        super(src, property);
    }

    @Override
    public TypeDeserializer forProperty(BeanProperty prop) {
        return (prop == _property) ? this : new NestedTypeDeserializer(this, prop);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object deserializeTypedFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode originalNode = p.readValueAsTree();
        JsonNode node = originalNode;
        log.debug("Searching for type discriminator [{}]...", _typePropertyName);
        for (String property : _typePropertyName.split("\\.")) {
            JsonNode nestedProp = node.get(property);
            if (nestedProp == null) {
                ctxt.reportWrongTokenException(p, JsonToken.FIELD_NAME,
                        "missing property '" + _typePropertyName + "' that is to contain type id  (for class "
                                + baseTypeName() + ")");
                return null;
            }
            node = nestedProp;
        }
        log.debug("Found [{}] with value [{}]", _typePropertyName, node.asText());
        JsonDeserializer<Object> deser = _findDeserializer(ctxt, "" + node.asText());
        JsonParser jsonParser = new TreeTraversingParser(originalNode, p.getCodec());
        if (jsonParser.getCurrentToken() == null) {
            jsonParser.nextToken();
        }
        return deser.deserialize(jsonParser, ctxt);
    }
}