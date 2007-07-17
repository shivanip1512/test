package com.cannontech.customtasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class RegexReplaceProperty extends Task {
    private String name;
    private String match;
    private String replace;
    private String value;
    
    public void execute() {
        if (name == null) throw new BuildException("name not set");
        if (match == null) throw new BuildException("match not set");
        if (replace == null) throw new BuildException("replace not set");
        if (value == null) throw new BuildException("value not set");
        
        String clean = replaceAll(match, value, replace);
        this.getProject().setProperty(name, clean);
    }
    
    private String replaceAll(final String match, final String input, final String replace) {
        return input.replaceAll(match, replace);
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setMatch(final String match) {
        this.match = match;
    }
    
    public void setReplace(final String replace) {
        this.replace = replace;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
}
