package com.cannontech.common.bulk.model;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * This object holds rows of FDR translation data for export to CSV.
 */
public class FdrExportData {
    private List<String> headerRow;
    private List<FdrExportDataRow> dataRows = Lists.newArrayList();
    
    public void setHeaderRow(List<String> headerRow) {
        this.headerRow = headerRow;
    }
    
    public List<String> getInterfaceHeaders() {
        return headerRow.subList(4, headerRow.size());
    }
    
    public void addRow(FdrExportDataRow row) {
        dataRows.add(row);
    }
    
    /**
     * Creates a 2 dimensional array of String values, which can be
     * fed into a CSVWriter. The header row is output as the first row,
     * followed by all data rows in the order they were added.
     */
    public String[][] asArrays() {
        String[][] output = new String[dataRows.size()+1][];
        output[0] = headerRow.toArray(new String[headerRow.size()]);
        for(int i = 0; i < dataRows.size(); i++) {
            output[i+1] = dataRows.get(i).asArray();
        }
        return output;
    }
}
