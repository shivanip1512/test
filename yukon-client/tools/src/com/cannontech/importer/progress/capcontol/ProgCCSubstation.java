package com.cannontech.importer.progress.capcontol;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class ProgCCSubstation {

    private String name = "default";
    private ArrayList<ProgCCFeeder> ccFeederList = new ArrayList<ProgCCFeeder>();
    private int id;
    
    public ProgCCSubstation(){
        super();
    }
    public ProgCCSubstation( String name ){
        this();
        this.name = name;
    }

    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCcFeederList(ArrayList<ProgCCFeeder> ccFeederList) {
        this.ccFeederList = ccFeederList;
    }
    public ArrayList<ProgCCFeeder> getCcFeederList() {
        ArrayList<ProgCCFeeder> c = new ArrayList<ProgCCFeeder>();
        c.addAll(ccFeederList);
        return c;
    }
    
    public void addFeeder( ProgCCFeeder feeder ){
        ccFeederList.add(feeder);
    }
    public int getArraySize(){
        return ccFeederList.size();
    }

}
