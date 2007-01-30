package com.cannontech.cbc.oneline;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.util.HTMLGenerator;

public class OnelineHTMLGenerator extends HTMLGenerator {

    public void generate(Writer w, Drawing d) throws IOException {
        String svgFile = new File(d.getFileName().replaceAll("jlx", "svg")).getName();
        super.generate(w, svgFile, OnelineUtil.DEFAULT_WIDTH, OnelineUtil.DEFAULT_HEIGHT);
    }

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
