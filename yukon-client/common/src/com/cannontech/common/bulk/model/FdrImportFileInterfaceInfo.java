package com.cannontech.common.bulk.model;

import java.util.List;

import com.cannontech.common.fdr.FdrInterfaceType;
import com.google.common.collect.Lists;

public class FdrImportFileInterfaceInfo {
    private List<FdrInterfaceType> interfacesInFile = Lists.newArrayList();
    private List<Integer> columnsToIgnore = Lists.newArrayList();
    
    public FdrImportFileInterfaceInfo() {
    }
    
    public List<FdrInterfaceType> getInterfaces() {
        return interfacesInFile;
    }
    
    public void addInterface(FdrInterfaceType fdrInterface) {
        interfacesInFile.add(fdrInterface);
    }
    
    public List<Integer> getColumnsToIgnore() {
        return columnsToIgnore;
    }
    
    public void addColumnToIgnore(int columnNumber) {
        columnsToIgnore.add(columnNumber);
    }
    
}
