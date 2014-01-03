package com.cannontech.web.reports;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.simplereport.ColumnInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

public class JsonReportDataUtils {
    private static ObjectMapper jsonObjectMapper = new ObjectMapper();

    public static void outputReportData(List<List<String>> data,
            List<ColumnInfo> columnInfo, OutputStream oStream) throws IOException {

        Map<String, Object> root = Maps.newHashMapWithExpectedSize(3);
        List<Map<String, String>> rows = new ArrayList<>();
        
        for (List<String> columnList : data) {
            Map<String, String> rowData = Maps.newHashMapWithExpectedSize(columnList.size());
            
            Iterator<ColumnInfo> infoIterator = columnInfo.iterator();
            for (String columnData : columnList) {
                ColumnInfo info = infoIterator.next();
                rowData.put(info.getName(), columnData);
            }
            rows.add(rowData);
        }
        
        root.put("rows", rows);
        root.put("page", 1);
        root.put("total", rows.size());

        PrintWriter writer = new PrintWriter(oStream);
        jsonObjectMapper.writeValue(writer, root);
        writer.flush();
    }
}
