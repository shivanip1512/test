package com.cannontech.amr.archivedValueExporter.model;

import java.util.ArrayList;
import java.util.List;

public class Preview {

    private String header = "";
    private List<String> body = new ArrayList<>();
    private String footer = "";
    
    public String getHeader() {
        return header;
    }
    
    public void setHeader(String header) {
        this.header = header;
    }
    
    public List<String> getBody() {
        return body;
    }
    
    public void setBody(List<String> body) {
        this.body = body;
    }
    
    public String getFooter() {
        return footer;
    }
    
    public void setFooter(String footer) {
        this.footer = footer;
    }
    
}