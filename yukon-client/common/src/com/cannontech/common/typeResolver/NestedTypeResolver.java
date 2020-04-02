package com.cannontech.common.typeResolver;

import java.util.Collection;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
/**
 * This class will be used to resolve nested type property.
 * Example : 
 * {
    "info": {
        "type": "TCPPORT",
        "name": "Test",
        "enable": true,
        "baudRate": "BAUD_300"
    },
    "timing": {
        "preTxWait": 25,
        "rtsToTxWait": 0,
        "postTxWait": 0,
        "receiveDataWait": 0,
        "extraTimeOut": 0
    }
  }
 * 
 * @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "info.type")
 * 
 * In the above example property is multilevel i.e inner object property of the passes JSON.
 *
 */
public class NestedTypeResolver extends StdTypeResolverBuilder {
    @Override
    public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType,
            Collection<NamedType> subtypes) {
            TypeIdResolver idRes = idResolver(config, baseType, null, subtypes, false, true);
            return new NestedTypeDeserializer(baseType, idRes, _typeProperty, _typeIdVisible ,
                null, _includeAs);
    }
}
