package com.cannontech.cbc.oneline;

import com.cannontech.esub.util.HTMLGenerator;

public class OnelineHTMLGenerator extends HTMLGenerator {

    private String[] files = {
    //"PopupWindow.js", 
            "prototype.js", "cc.js" };

    public OnelineHTMLGenerator() {
        super();
    }

    @Override
    public String getScript() {
        String script = "";
        for (int i = 0; i < files.length; i++) {
            script += "<script type=\"text/JavaScript\" src=\"" + files[i] + "\"></script>";
        }
        return script;
    }

}
