package com.cannontech.web.jws;

import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.mvc.AbstractController;

public abstract class JnlpControllerBase extends AbstractController {
    protected String title;
    protected String description;
    protected String path;
    protected Set<String> jars = Collections.emptySet();

    public String getTitle() {
        return title;
    }

    @Required
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    @Required
    public void setPath(String path) {
        this.path = path;
    }

    public void setJars(Set<String> jars) {
        this.jars = jars;
    }
}
