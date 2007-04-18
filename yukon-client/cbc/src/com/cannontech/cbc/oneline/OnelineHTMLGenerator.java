package com.cannontech.cbc.oneline;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.util.HTMLGenerator;
import com.cannontech.yukon.cbc.CBCUtils;

public class OnelineHTMLGenerator extends HTMLGenerator {

    public void generate(Writer w, Drawing d) throws IOException {
        String svgFile = new File(d.getFileName().replaceAll("jlx", "svg")).getName();
        super.generate(w, svgFile, OnelineUtil.DEFAULT_WIDTH, OnelineUtil.DEFAULT_HEIGHT);
    }

    private String[] files = {
            "AnchorPosition.js",
            "PopupWindow.js", 
            "prototype.js",  "scriptaculous/scriptaculous.js", "cc.js", "cccommands.js", "cconelinepopup.js"  };

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

    @Override
    public void writeAdditionalFields(Writer w) {
        try {
            //manual cap states
            //encoded as a comma separated, colon dilimited
            //"Any:-1,Open:0,Close:1"
            String manualCapStates = CBCUtils.getAllManualCapStates();
            w.write("<INPUT TYPE=\"hidden\" ID=\"capmanualstates\" value=\"" + manualCapStates + "\"></INPUT>");
            w.write("<DIV ID=\"cmdMessageDiv\" STYLE=\"position:absolute;visibility:hidden;background-color:#000000;\"></DIV>");

        } catch (IOException e) {
            CTILogger.error(e);
        }

    }

}
