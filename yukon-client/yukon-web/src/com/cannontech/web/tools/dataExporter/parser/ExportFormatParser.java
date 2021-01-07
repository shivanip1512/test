package com.cannontech.web.tools.dataExporter.parser;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.clientutils.YukonLogManager;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExportFormatParser {
    private static final Logger log = YukonLogManager.getLogger(ExportFormatParser.class);

    public ExportFormat parse(InputStream inputStream) throws IOException {
        Yaml y = new Yaml();

        Object yamlObject = y.load(inputStream);

        ObjectMapper jsonFormatter = new ObjectMapper();

        byte[] jsonBytes = jsonFormatter.writeValueAsBytes(yamlObject);

        ExportFormat exportFormat = jsonFormatter.readValue(jsonBytes, ExportFormat.class);
        log.debug("Parsed ExportFormat object: " + exportFormat);

        return exportFormat;
    }

}
