package com.cannontech.stars.xml.serialize;

import java.util.Enumeration;
import java.util.Vector;

public class StarsApplianceCategory {
    private int applianceCategoryID;
    private boolean hasApplianceCategoryID;
    private int categoryID;
    private boolean hasCategoryID;
    private boolean inherited;
    private boolean hasInherited;
    private String description;
    private StarsWebConfig starsWebConfig;
    private Vector<StarsEnrLMProgram> starsEnrLMProgramList;

    public StarsApplianceCategory() {
        starsEnrLMProgramList = new Vector<StarsEnrLMProgram>();
    }

    public void addStarsEnrLMProgram(StarsEnrLMProgram vStarsEnrLMProgram) {
        starsEnrLMProgramList.addElement(vStarsEnrLMProgram);
    }

    public void addStarsEnrLMProgram(int index, StarsEnrLMProgram vStarsEnrLMProgram) {
        starsEnrLMProgramList.insertElementAt(vStarsEnrLMProgram, index);
    }

    public void deleteApplianceCategoryID() {
        this.hasApplianceCategoryID = false;
    } 

    public void deleteCategoryID() {
        this.hasCategoryID = false;
    } 

    public void deleteInherited() {
        this.hasInherited = false;
    } 

    public Enumeration<StarsEnrLMProgram> enumerateStarsEnrLMProgram() {
        return starsEnrLMProgramList.elements();
    }

    public int getApplianceCategoryID() {
        return this.applianceCategoryID;
    }

    public int getCategoryID() {
        return this.categoryID;
    }

    public String getDescription() {
        return this.description;
    } 

    public boolean getInherited() {
        return this.inherited;
    }

    public StarsEnrLMProgram getStarsEnrLMProgram(int index) {
        //-- check bounds for index
        if ((index < 0) || (index > starsEnrLMProgramList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return starsEnrLMProgramList.elementAt(index);
    } 

    public StarsEnrLMProgram[] getStarsEnrLMProgram() {
        int size = starsEnrLMProgramList.size();
        StarsEnrLMProgram[] mArray = new StarsEnrLMProgram[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = starsEnrLMProgramList.elementAt(index);
        }
        return mArray;
    }

    public int getStarsEnrLMProgramCount() {
        return starsEnrLMProgramList.size();
    }

    public StarsWebConfig getStarsWebConfig() {
        return this.starsWebConfig;
    }

    public boolean hasApplianceCategoryID() {
        return this.hasApplianceCategoryID;
    }

    public boolean hasCategoryID() {
        return this.hasCategoryID;
    }

    public boolean hasInherited() {
        return this.hasInherited;
    }

    public void removeAllStarsEnrLMProgram() {
        starsEnrLMProgramList.removeAllElements();
    }

    public StarsEnrLMProgram removeStarsEnrLMProgram(int index) {
        StarsEnrLMProgram obj = starsEnrLMProgramList.elementAt(index);
        starsEnrLMProgramList.removeElementAt(index);
        return obj;
    } 

    public void setApplianceCategoryID(int applianceCategoryID) {
        this.applianceCategoryID = applianceCategoryID;
        this.hasApplianceCategoryID = true;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
        this.hasCategoryID = true;
    }

    public void setDescription(String description) {
        this.description = description;
    } 

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
        this.hasInherited = true;
    } 

    public void setStarsEnrLMProgram(int index, StarsEnrLMProgram vStarsEnrLMProgram) {
        //-- check bounds for index
        if ((index < 0) || (index > starsEnrLMProgramList.size())) {
            throw new IndexOutOfBoundsException();
        }
        starsEnrLMProgramList.setElementAt(vStarsEnrLMProgram, index);
    }

    public void setStarsEnrLMProgram(StarsEnrLMProgram[] starsEnrLMProgramArray) {
        //-- copy array
        starsEnrLMProgramList.removeAllElements();
        for (int i = 0; i < starsEnrLMProgramArray.length; i++) {
            starsEnrLMProgramList.addElement(starsEnrLMProgramArray[i]);
        }
    }

    public void setStarsWebConfig(StarsWebConfig starsWebConfig) {
        this.starsWebConfig = starsWebConfig;
    }

}
