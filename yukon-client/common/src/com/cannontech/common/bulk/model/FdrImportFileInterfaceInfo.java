package com.cannontech.common.bulk.model;

import java.util.List;
import java.util.Set;

import com.cannontech.common.fdr.FdrInterfaceType;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class FdrImportFileInterfaceInfo {
    private Set<FdrInterfaceType> interfacesInFile = Sets.newHashSet();
    private List<Integer> columnsToIgnore = Lists.newArrayList();
    
    public FdrImportFileInterfaceInfo() {
    }
    
    public Set<FdrInterfaceType> getInterfaces() {
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
