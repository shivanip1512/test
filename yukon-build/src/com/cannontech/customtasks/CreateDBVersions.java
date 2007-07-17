package com.cannontech.customtasks;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class CreateDBVersions extends Task {
    private static final String REGEX_PATTERN = "^(\\d+)\\.(\\d+)\\.(\\d+)$";
    private static final String YUKON_MAJOR = "yukon.major";
    private static final String YUKON_MINOR = "yukon.minor";
    private static final String YUKON_REVISION = "yukon.revision";
    private String buildlabel;
    
    
    public void execute() {
        
        if (buildlabel == null) {
            throw new BuildException("buildLabel must be set");
        }

        Matcher matcher = Pattern.compile(REGEX_PATTERN).matcher(buildlabel);
        if (!matcher.matches() || matcher.groupCount() != 3) {
            throw new BuildException("could not parse buildlabel: " + buildlabel);
        }
        
        this.getProject().setProperty(YUKON_MAJOR, matcher.group(1));
        this.getProject().setProperty(YUKON_MINOR, matcher.group(2));
        this.getProject().setProperty(YUKON_REVISION, matcher.group(3));
    }
    
    public void setBuildLabel(final String buildlabel) {
        this.buildlabel = buildlabel;
    }
    
}
