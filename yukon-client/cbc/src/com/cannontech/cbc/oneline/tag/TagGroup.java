package com.cannontech.cbc.oneline.tag;

public class TagGroup {
   
    private String tagGroupName;
    private String[] tagNames;

    public TagGroup(String tName, String[] sNames) {
        tagGroupName = tName;
        tagNames = sNames;
    
    }

    public String[] getTagNames() {
        return tagNames;
    }

    public String getTagGroupName() {
        return tagGroupName;
    }

}
