package com.cannontech.importer.progress.capcontol;

import java.util.ArrayList;

public class ProgCCArea {

    private ArrayList<ProgCCSubstation> ccSubstationList = new ArrayList<ProgCCSubstation>();
    private String name = "default";
    private int id;

    public ProgCCArea(){
        super();
    }
    public ProgCCArea(String name){
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
    
    public void addSub( ProgCCSubstation sub ){
        ccSubstationList.add(sub);
    }
    
    public ArrayList<ProgCCSubstation> getSubs(){
        ArrayList<ProgCCSubstation> c = new ArrayList<ProgCCSubstation>();
        c.addAll(ccSubstationList);
        return c;
    }
    
    public int getArraySize(){
        return ccSubstationList.size();
    }

}
