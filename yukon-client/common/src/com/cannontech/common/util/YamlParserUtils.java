package com.cannontech.common.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.cannontech.clientutils.YukonLogManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class YamlParserUtils {
    private static final Logger log = YukonLogManager.getLogger(YamlParserUtils.class);

    public static <T> T parseToObject(InputStream inputStream, Class<T> returnClass) throws IOException {
        Yaml y = new Yaml();
        Object yamlObject = y.load(inputStream);
        ObjectMapper jsonFormatter = new ObjectMapper();
        byte[] jsonBytes = jsonFormatter.writeValueAsBytes(yamlObject);
        T parsedObject = jsonFormatter.readValue(jsonBytes, returnClass);

        log.debug("Parsed {} object:{} ", returnClass.getSimpleName(), yamlObject);
        return parsedObject;
    }

    public static String parseToJson(InputStream inputStream) throws JsonProcessingException {
        Yaml y = new Yaml();

        Object yamlObject = y.load(inputStream);

        ObjectMapper jsonFormatter = new ObjectMapper();

        jsonFormatter.enable(SerializationFeature.INDENT_OUTPUT);

        return jsonFormatter.writeValueAsString(yamlObject);
    }
}
