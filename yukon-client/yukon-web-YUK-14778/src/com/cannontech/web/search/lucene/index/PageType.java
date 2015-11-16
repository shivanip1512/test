package com.cannontech.web.search.lucene.index;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;

import com.cannontech.common.userpage.model.SiteModule;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.common.userpage.model.UserPage.Key;
import com.cannontech.web.common.search.result.Page;
import com.cannontech.web.support.SiteMapPage;

public enum PageType {
    
    USER_PAGE {
        @Override
        public Page buildPage(Document document) {
            
            String path = document.get("path");
            String pageName = document.get("pageName");
            
            List<String> pageArgs = getListFromDocument(document, "pageArg");
            List<String> summaryArgs = new ArrayList<>(pageArgs);
            summaryArgs.addAll(getListFromDocument(document, "summaryArg"));
            
            SiteModule module = SiteModule.getByName(document.get("module"));
            UserPage userPage = new UserPage(null, new Key(0, path), module, pageName, pageArgs, false, null);
            Page page = new Page(userPage, summaryArgs);
            
            return page;
        }
    },
    
    SITE_MAP {
        @Override
        public Page buildPage(Document document) {
            
            String[] pageKeyParts = document.get("pageKey").split(":");
            Page page = new Page(SiteMapPage.valueOf(pageKeyParts[1]));
            
            return page;
        }
    },
    
    LEGACY {
        @Override
        public Page buildPage(Document document) {
            
            String path = document.get("path");
            String module = document.get("module");
            String pageName = document.get("pageName");
            
            List<String> pageArgs = getListFromDocument(document, "pageArg");
            List<String> summaryArgs = new ArrayList<>(pageArgs);
            summaryArgs.addAll(getListFromDocument(document, "summaryArg"));
            
            return new Page(path, module, pageName, summaryArgs);
        }
    },
    ;
    
    private static List<String> getListFromDocument(Document document, String prefix) {
        
        List<String> list = new ArrayList<>();
        int index = 0;
        String fieldValue;
        
        while ((fieldValue = document.get(prefix + index)) != null) {
            list.add(fieldValue);
            index++;
        }
        
        return list;
    }
    
    public abstract Page buildPage(Document document);
    
}