package com.cannontech.web.support.development;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import com.cannontech.clientutils.YukonLogManager;

public class SampleEimFile implements Comparable<SampleEimFile> {
    private final static Logger log = YukonLogManager.getLogger(SampleEimFile.class);

    private final static Pattern filenamePattern =
        Pattern.compile(".*/api/(\\w+)/schemas/xml-templates/([\\w\\.]+)\\.xml");

    private int id;
    private String category;
    private Resource resource;
    private String niceName;

    public SampleEimFile(int id, Resource resource) throws IOException {
        this.id = id;
        this.resource = resource;
        String path = resource.getURL().getPath();

        Matcher matcher = filenamePattern.matcher(path);
        if (!matcher.matches()) {
            log.error(path + " does not match", new IllegalArgumentException());
        }
        category = nicifyCategory(matcher.group(1));
        niceName = nicifyName(matcher.group(2));
    }

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public String getNiceName() {
        return niceName;
    }

    public void setNiceName(String niceName) {
        this.niceName = niceName;
    }

    /**
     * Separate camel case into space-separated words.  Capitalize.
     */
    private String nicify(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).replaceAll("(?<! )([A-Z])", " $1");
    }

    /**
     * Call {@link #nicify(String)} and remove "Example" and "Request".
     */
    private String nicifyName(String category) {
        return nicify(category).replace("Example", "").replaceAll("_?Request", "").replace('_', ' ');
    }

    /**
     * Call {@link #nicify(String)} and rename "amr" to "AMI".
     */
    private String nicifyCategory(String category) {
        if (category.equals("amr")) {
            return "AMI";
        }
        return nicify(category);
    }

    @Override
    public int compareTo(SampleEimFile other) {
        int retVal = category.compareTo(other.category);
        if (retVal == 0) {
            retVal = niceName.compareTo(other.niceName);
        }
        return retVal;
    }
}
