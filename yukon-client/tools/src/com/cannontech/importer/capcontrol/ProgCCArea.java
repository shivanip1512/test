package com.cannontech.importer.capcontrol;

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

    public ProgCCFeeder getFeeder( String subName, String feederName )
    {
        //Find sub
        for( ProgCCSubstation sub : ccSubstationList )
        {
            if( sub.getName().compareTo(subName) == 0)
                return sub.getFeeder(feederName);
        }
        
        //No sub found, so lets make it, and add make a feeder while we are at it.
        ProgCCSubstation sub = new ProgCCSubstation(subName);
        ccSubstationList.add( sub );
        // new sub, so no feeders. make one and return it.
        ProgCCFeeder feeder = new ProgCCFeeder(feederName);
        sub.addFeeder(feeder);
        
        return feeder;
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
