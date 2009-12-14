package com.cannontech.common.databaseMigration.bean;

import org.jdom.Element;
import org.springframework.core.style.ToStringCreator;

public class DatabaseMigrationImport {
    
    private String identifier;
    private Element element;

    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Element getElement() {
        return element;
    }
    public void setElement(Element element) {
        this.element = element;
    }

    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("identifier", getIdentifier());
        tsc.append("element", getElement());
        return tsc.toString();
    }
}
