package com.cannontech.importer.progress.capcontol;

import java.util.ArrayList;

public class ProgCCFeeder {

    private String name = "default";
    private ArrayList<ProgCCCapbank> ccCapbankList = new ArrayList<ProgCCCapbank>();
    private int id;
    
    public ProgCCFeeder(){
        super();
    }
    public ProgCCFeeder( String name ){
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
    public ArrayList<ProgCCCapbank> getCcCapBanks() {
        ArrayList<ProgCCCapbank> c = new ArrayList<ProgCCCapbank>();
        c.addAll( ccCapbankList );
        return c;
    }
    
    public void addCapbank( ProgCCCapbank bank ){
        ccCapbankList.add(bank);
    }
    public int getArraySize(){
        return ccCapbankList.size();
    }

    
}
