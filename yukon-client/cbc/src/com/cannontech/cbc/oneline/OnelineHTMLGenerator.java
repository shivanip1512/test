package com.cannontech.cbc.oneline;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.Writer;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.util.HTMLGenerator;

public class OnelineHTMLGenerator extends HTMLGenerator {
    private static final String NEW_LINE = "\n";
    
    public void generate(Writer w, Drawing d, Dimension dim) throws IOException {
        String svgFile = new File(d.getFileName().replaceAll("jlx", "svg")).getName();
        super.generate(w, svgFile, (int)dim.getWidth(), (int)dim.getHeight(), "#000000");
    }
    
    private String[] files = {
            "AnchorPosition.js",
            "PopupWindow.js", 
            "prototype150.js",
            "scriptaculous/scriptaculous.js",
            "cc.js",
            "cconelinepopup.js"
    };

    public OnelineHTMLGenerator() {
        super();
    }

    @Override
    public String getScript() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < files.length; i++) {
            sb.append("<script type=\"text/JavaScript\" src=\"" + files[i] + "\"></script>" + NEW_LINE);
        }
        
        sb.append("<script type=\"text/javascript\" language=\"JavaScript\">" + NEW_LINE);
        sb.append("var x;" + NEW_LINE);
        sb.append("var y;" + NEW_LINE);
        sb.append("Event.observe(document, 'mousemove', function(event) {" + NEW_LINE);
        sb.append("    x = Event.pointerX(event);" + NEW_LINE);
        sb.append("    y = Event.pointerY(event);" + NEW_LINE);
        sb.append("});" + NEW_LINE);
        sb.append(NEW_LINE);
        sb.append("var printableView = false;" + NEW_LINE);
        sb.append("function togglePrintableView() {" + NEW_LINE);
        sb.append("    if (printableView) {" + NEW_LINE);
        sb.append("        window.location.reload();" + NEW_LINE);
        sb.append("    } else {" + NEW_LINE);
        sb.append("        document.body.style.backgroundColor = 'white';" + NEW_LINE);
        sb.append("        printableView = true;" + NEW_LINE);
        sb.append("    }" + NEW_LINE);
        sb.append("}" + NEW_LINE);
        sb.append("</script>" + NEW_LINE);
        return sb.toString();
    }
    
    @Override
    public void writeAdditionalFields(Writer w) {
        try {
            w.write("<DIV ID=\"cmdMessageDiv\" STYLE=\"position:absolute;visibility:hidden;background-color:#000000;\"></DIV>");
        } catch (IOException e) {
            CTILogger.error(e);
        }
    }    
        
}
