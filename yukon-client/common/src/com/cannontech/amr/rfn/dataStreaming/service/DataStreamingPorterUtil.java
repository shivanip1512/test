package com.cannontech.amr.rfn.dataStreaming.service;

import java.io.IOException;

import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
import com.cannontech.common.util.JsonUtils;

public class DataStreamingPorterUtil {

    private static final String porterJsonTag = "json";
    public static final String porterJsonPrefix = porterJsonTag + "{";

    /**
     * Extracts a JSON-encoded ReportedDataStreamingConfig from a Porter return string.
     * Note that it is not raw JSON - it is prefixed with "json" which is followed by the JSON object.
     * @param porterJson the Porter JSON string
     * @return
     * @throws IOException
     */
    public static ReportedDataStreamingConfig extractReportedDataStreamingConfig(String porterJson)
            throws IOException {
        String rawJson = porterJson.substring(DataStreamingPorterUtil.porterJsonTag.length());
        return JsonUtils.fromJson(rawJson, ReportedDataStreamingConfig.class);
    }
}
