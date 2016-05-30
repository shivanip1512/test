package com.cannontech.tools.util;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.cannontech.clientutils.CTILogger;

public class CvsCommentChanger {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        
        if (args.length != 2) {
            System.out.println("Usage: \nCvsCommentChanger <changesetid> \"<new comment>\"");
        }
        
        String changeSet = args[0];
        String newComment = args[1];
        
        String fisheyeBaseUrl = "http://fisheye.cannontech.com/";
        String getChangeSetUrl = fisheyeBaseUrl + "api/rest/getChangeset?csid=" + changeSet + "&rep=yukon";
        
        try {
            URL url = new URL(getChangeSetUrl);
            InputStream stream = url.openStream();
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(stream);
            Element rootElement = document.getRootElement();
            Element changesetEl = rootElement.getChild("changeset");
            Element revisionsEl = changesetEl.getChild("revisions");
            
            List<Element> children = revisionsEl.getChildren("revisionkey");
            
            StringBuilder output = new StringBuilder();
            for (Element revisionkeyEl : children) {
                String path = revisionkeyEl.getAttributeValue("path");
                String rev = revisionkeyEl.getAttributeValue("rev");
                
                output.append("cvs admin -m");
                output.append(rev);
                output.append(":\"");
                
                output.append(newComment);
                output.append("\" ");
                output.append(path);
                output.append("\n");
            }
            
            System.out.println(output);
            
        } catch (Exception e) {
            CTILogger.error(e);
        }
    }

}
