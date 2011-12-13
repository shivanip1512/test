package com.cannontech.web.bulk.model;

import java.util.List;

import com.cannontech.database.data.fdr.FDRInterface;
import com.google.common.collect.Lists;

public class FdrImportFileInterfaceInfo {
    private List<FDRInterface> interfacesInFile = Lists.newArrayList();
    private List<Integer> columnsToIgnore = Lists.newArrayList();
    
    public FdrImportFileInterfaceInfo() {
    }
    
    public FdrImportFileInterfaceInfo(List<FDRInterface> interfacesInFile, List<Integer> columnsToIgnore) {
        this.interfacesInFile = interfacesInFile;
        this.columnsToIgnore = columnsToIgnore;
    }
    
    public List<FDRInterface> getInterfaces() {
        return interfacesInFile;
    }
    
    public void addInterface(FDRInterface fdrInterface) {
        interfacesInFile.add(fdrInterface);
    }
    
    public List<Integer> getColumnsToIgnore() {
        return columnsToIgnore;
    }
    
    public void addColumnToIgnore(int columnNumber) {
        columnsToIgnore.add(columnNumber);
    }
    
}
