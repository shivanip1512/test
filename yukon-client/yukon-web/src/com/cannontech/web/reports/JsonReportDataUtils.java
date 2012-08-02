package com.cannontech.web.reports;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jsonOLD.JSONObject;

import com.cannontech.simplereport.ColumnInfo;

public class JsonReportDataUtils {

    public static void outputReportData(List<List<String>> data,
            List<ColumnInfo> columnInfo, OutputStream oStream)
            throws IOException {

        
        Map<String, Object> root = new HashMap<String, Object>();
        List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
        
        for (List<String> columnList : data) {
            
            Map<String, String> rowData = new HashMap<String, String>();
            
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
        
        
        JSONObject jsonObj = new JSONObject(root);
        
        PrintWriter writer = new PrintWriter(oStream);
        jsonObj.write(writer);
        writer.flush();
    }
}
